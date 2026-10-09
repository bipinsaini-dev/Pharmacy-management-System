package com.pharmacare.service;

import com.pharmacare.dao.CustomerDAO;
import com.pharmacare.dao.MedicineDAO;
import com.pharmacare.dao.SaleDAO;
import com.pharmacare.model.Medicine;
import com.pharmacare.model.Sale;
import com.pharmacare.model.SaleItem;
import com.pharmacare.model.User;
import com.pharmacare.util.BusinessException;
import com.pharmacare.util.DBConnection;
import com.pharmacare.util.InvoiceUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

public class BillingService {
    /** GST charged on (subtotal - discount). 12% is the common slab for medicines. */
    public static final BigDecimal GST_RATE = new BigDecimal("12");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final MedicineDAO medicineDao = new MedicineDAO();
    private final SaleDAO saleDao = new SaleDAO();
    private final CustomerDAO customerDao = new CustomerDAO();

    /** Result of a bill calculation. */
    public static class Totals {
        private final BigDecimal subtotal;
        private final BigDecimal discount;
        private final BigDecimal gst;
        private final BigDecimal grandTotal;

        Totals(BigDecimal subtotal, BigDecimal discount, BigDecimal gst, BigDecimal grandTotal) {
            this.subtotal = subtotal;
            this.discount = discount;
            this.gst = gst;
            this.grandTotal = grandTotal;
        }
        public BigDecimal getSubtotal() { return subtotal; }
        public BigDecimal getDiscount() { return discount; }
        public BigDecimal getGst() { return gst; }
        public BigDecimal getGrandTotal() { return grandTotal; }
    }

    public Totals calculate(BigDecimal subtotal, BigDecimal discountPercent) {
        BigDecimal sub = subtotal.setScale(2, RoundingMode.HALF_UP);
        BigDecimal pct = discountPercent == null ? BigDecimal.ZERO : discountPercent;
        BigDecimal discount = sub.multiply(pct).divide(HUNDRED, 2, RoundingMode.HALF_UP);
        BigDecimal taxable = sub.subtract(discount);
        BigDecimal gst = taxable.multiply(GST_RATE).divide(HUNDRED, 2, RoundingMode.HALF_UP);
        return new Totals(sub, discount, gst, taxable.add(gst));
    }

    /**
     * Completes a sale in ONE database transaction:
     * lock stock rows -> check stock -> insert sale -> insert items -> reduce stock -> commit.
     * Any failure rolls everything back.
     *
     * @param cart medicineId -> quantity
     */
    public Sale checkout(Map<Integer, Integer> cart, int customerId, BigDecimal discountPercent,
                         String paymentMethod, User cashier) throws SQLException, BusinessException {
        if (cart == null || cart.isEmpty()) throw new BusinessException("The cart is empty.");
        if (discountPercent == null || discountPercent.signum() < 0 || discountPercent.compareTo(HUNDRED) > 0) {
            throw new BusinessException("Discount must be between 0 and 100 percent.");
        }
        if (!"Cash".equals(paymentMethod) && !"Card".equals(paymentMethod) && !"UPI".equals(paymentMethod)) {
            throw new BusinessException("Please select a valid payment method.");
        }
        if (customerDao.findById(customerId) == null) throw new BusinessException("Please select a valid customer.");

        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            Sale sale = new Sale();
            BigDecimal subtotal = BigDecimal.ZERO;

            // Sorted order => consistent lock order => no deadlocks between concurrent bills.
            for (Map.Entry<Integer, Integer> e : new TreeMap<>(cart).entrySet()) {
                int qty = e.getValue();
                if (qty <= 0) throw new BusinessException("Quantity must be at least 1.");
                Medicine m = medicineDao.lockById(conn, e.getKey());
                if (m == null || !"ACTIVE".equals(m.getStatus())) {
                    throw new BusinessException("A medicine in the cart is no longer available.");
                }
                if (m.isExpired()) {
                    throw new BusinessException(m.getMedicineName() + " is expired and cannot be sold.");
                }
                if (qty > m.getStockQuantity()) {
                    throw new BusinessException("Insufficient stock for " + m.getMedicineName()
                            + ": requested " + qty + ", available " + m.getStockQuantity() + ".");
                }
                SaleItem it = new SaleItem();
                it.setMedicineId(m.getId());
                it.setQuantity(qty);
                it.setPrice(m.getSellingPrice());                       // price always comes from the database
                it.setTotal(m.getSellingPrice().multiply(BigDecimal.valueOf(qty)).setScale(2, RoundingMode.HALF_UP));
                sale.getItems().add(it);
                subtotal = subtotal.add(it.getTotal());
            }

            Totals t = calculate(subtotal, discountPercent);
            sale.setInvoiceNumber(InvoiceUtil.nextInvoiceNumber(conn));
            sale.setCustomerId(customerId);
            sale.setUserId(cashier.getId());
            sale.setSubtotal(t.getSubtotal());
            sale.setDiscount(t.getDiscount());
            sale.setGst(t.getGst());
            sale.setGrandTotal(t.getGrandTotal());
            sale.setPaymentMethod(paymentMethod);

            saleDao.insert(conn, sale);
            for (SaleItem it : sale.getItems()) {
                if (!medicineDao.decreaseStock(conn, it.getMedicineId(), it.getQuantity())) {
                    throw new BusinessException("Stock changed while billing. Please review the cart and try again.");
                }
            }
            conn.commit();
            return sale;
        } catch (BusinessException | SQLException | RuntimeException ex) {
            rollbackQuietly(conn);
            throw ex;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
                // connection is closed next anyway
            }
            try {
                conn.close();
            } catch (SQLException ignored) {
                // nothing more to do
            }
        }
    }

    private void rollbackQuietly(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException ignored) {
            // original exception is rethrown by the caller
        }
    }
}

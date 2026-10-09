package com.pharmacare.service;

import com.pharmacare.dao.MedicineDAO;
import com.pharmacare.dao.PurchaseDAO;
import com.pharmacare.dao.SupplierDAO;
import com.pharmacare.model.Medicine;
import com.pharmacare.model.Purchase;
import com.pharmacare.model.PurchaseItem;
import com.pharmacare.util.BusinessException;
import com.pharmacare.util.DBConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;

public class PurchaseService {
    private final PurchaseDAO purchaseDao = new PurchaseDAO();
    private final MedicineDAO medicineDao = new MedicineDAO();
    private final SupplierDAO supplierDao = new SupplierDAO();

    /**
     * Saves a purchase and increases stock for every line in ONE transaction.
     * Items must already contain medicineId, quantity and purchasePrice.
     */
    public void record(Purchase p) throws SQLException, BusinessException {
        if (supplierDao.findById(p.getSupplierId()) == null) throw new BusinessException("Please select a valid supplier.");
        if (p.getPurchaseDate() == null) throw new BusinessException("Purchase date is required.");
        if (p.getItems().isEmpty()) throw new BusinessException("Add at least one medicine to the purchase.");

        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            BigDecimal total = BigDecimal.ZERO;
            for (PurchaseItem it : p.getItems()) {
                if (it.getQuantity() <= 0) throw new BusinessException("Quantity must be at least 1.");
                if (it.getPurchasePrice() == null || it.getPurchasePrice().signum() < 0) {
                    throw new BusinessException("Purchase price cannot be negative.");
                }
                Medicine m = medicineDao.lockById(conn, it.getMedicineId());
                if (m == null) throw new BusinessException("A selected medicine does not exist.");
                it.setTotal(it.getPurchasePrice().multiply(BigDecimal.valueOf(it.getQuantity())).setScale(2, RoundingMode.HALF_UP));
                total = total.add(it.getTotal());
            }
            p.setTotalAmount(total);
            purchaseDao.insert(conn, p);
            for (PurchaseItem it : p.getItems()) {
                medicineDao.increaseStock(conn, it.getMedicineId(), it.getQuantity(), it.getPurchasePrice());
            }
            conn.commit();
        } catch (BusinessException | SQLException | RuntimeException ex) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
                // original exception is rethrown below
            }
            throw ex;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
                // closed next
            }
            try {
                conn.close();
            } catch (SQLException ignored) {
                // nothing more to do
            }
        }
    }
}

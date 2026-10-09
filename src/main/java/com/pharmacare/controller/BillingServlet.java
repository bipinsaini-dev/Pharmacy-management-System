package com.pharmacare.controller;

import com.pharmacare.dao.CustomerDAO;
import com.pharmacare.dao.MedicineDAO;
import com.pharmacare.model.Medicine;
import com.pharmacare.model.Sale;
import com.pharmacare.model.SaleItem;
import com.pharmacare.service.BillingService;
import com.pharmacare.util.BusinessException;
import com.pharmacare.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** POS-style billing. The cart lives in the HttpSession as medicineId -> quantity. */
@WebServlet({"/admin/billing", "/pharmacist/billing"})
public class BillingServlet extends BaseServlet {
    private static final int WALK_IN_ID = 1;
    private final MedicineDAO medicineDao = new MedicineDAO();
    private final CustomerDAO customerDao = new CustomerDAO();
    private final BillingService billing = new BillingService();

    @SuppressWarnings("unchecked")
    private Map<Integer, Integer> cart(HttpSession s) {
        Map<Integer, Integer> c = (Map<Integer, Integer>) s.getAttribute("cart");
        if (c == null) {
            c = new LinkedHashMap<>();
            s.setAttribute("cart", c);
        }
        return c;
    }

    private void resetBill(HttpSession s) {
        cart(s).clear();
        s.removeAttribute("billingDiscount");
        s.removeAttribute("billingPayment");
        s.removeAttribute("billingCustomerId");
    }

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpSession s = req.getSession();
        Map<Integer, Integer> cart = cart(s);
        String q = param(req, "q");

        List<SaleItem> lines = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        Iterator<Map.Entry<Integer, Integer>> it = cart.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> e = it.next();
            Medicine m = medicineDao.findById(e.getKey());
            if (m == null || !"ACTIVE".equals(m.getStatus())) {
                it.remove();                       // medicine disappeared since it was added
                continue;
            }
            SaleItem line = new SaleItem();
            line.setMedicineId(m.getId());
            line.setMedicineName(m.getMedicineName());
            line.setBatchNumber(m.getBatchNumber());
            line.setPrice(m.getSellingPrice());
            line.setQuantity(e.getValue());
            line.setAvailableStock(m.getStockQuantity());
            line.setTotal(m.getSellingPrice().multiply(BigDecimal.valueOf(e.getValue())));
            lines.add(line);
            subtotal = subtotal.add(line.getTotal());
        }

        BigDecimal discountPct = (BigDecimal) s.getAttribute("billingDiscount");
        if (discountPct == null) discountPct = BigDecimal.ZERO;
        String payment = (String) s.getAttribute("billingPayment");
        Integer customerId = (Integer) s.getAttribute("billingCustomerId");

        req.setAttribute("q", q);
        req.setAttribute("results", medicineDao.searchAvailable(q, 15));
        req.setAttribute("cartLines", lines);
        req.setAttribute("totals", billing.calculate(subtotal, discountPct));
        req.setAttribute("discountPct", discountPct);
        req.setAttribute("payment", payment == null ? "Cash" : payment);
        req.setAttribute("selectedCustomerId", customerId == null ? WALK_IN_ID : customerId);
        req.setAttribute("customers", customerDao.search(""));
        req.setAttribute("gstRate", BillingService.GST_RATE);
        render(req, resp, "common/billing");
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpSession s = req.getSession();
        Map<Integer, Integer> cart = cart(s);
        String action = param(req, "action");
        String target = "billing";
        try {
            if ("add".equals(action)) {
                addToCart(req, cart);
                String q = param(req, "q");
                if (!q.isEmpty()) target = "billing?q=" + URLEncoder.encode(q, StandardCharsets.UTF_8);
            } else if ("clear".equals(action)) {
                resetBill(s);
                flashSuccess(req, "Cart cleared.");
            } else {
                syncFromForm(req, s, cart);
                if (action.startsWith("remove-")) {
                    Integer id = ValidationUtil.toInt(action.substring("remove-".length()));
                    if (id != null) cart.remove(id);
                    flashSuccess(req, "Item removed from cart.");
                } else if ("checkout".equals(action)) {
                    Integer customerId = (Integer) s.getAttribute("billingCustomerId");
                    BigDecimal discount = (BigDecimal) s.getAttribute("billingDiscount");
                    String payment = (String) s.getAttribute("billingPayment");
                    Sale sale = billing.checkout(new LinkedHashMap<>(cart), customerId == null ? WALK_IN_ID : customerId,
                            discount == null ? BigDecimal.ZERO : discount, payment, currentUser(req));
                    resetBill(s);
                    flashSuccess(req, "Bill " + sale.getInvoiceNumber() + " created successfully. Stock updated.");
                    redirect(req, resp, "sales?action=invoice&id=" + sale.getId());
                    return;
                } else {
                    flashSuccess(req, "Cart updated.");
                }
            }
        } catch (BusinessException e) {
            flashError(req, e.getMessage());
        }
        redirect(req, resp, target);
    }

    private void addToCart(HttpServletRequest req, Map<Integer, Integer> cart) throws Exception {
        Integer id = ValidationUtil.toInt(req.getParameter("medicineId"));
        Integer qty = ValidationUtil.toInt(req.getParameter("quantity"));
        if (id == null || qty == null || qty < 1) throw new BusinessException("Enter a valid quantity (1 or more).");
        Medicine m = medicineDao.findById(id);
        if (m == null || !"ACTIVE".equals(m.getStatus())) throw new BusinessException("Medicine not found.");
        if (m.isExpired()) throw new BusinessException(m.getMedicineName() + " is expired and cannot be sold.");
        int inCart = cart.getOrDefault(id, 0);
        if (inCart + qty > m.getStockQuantity()) {
            throw new BusinessException("Only " + m.getStockQuantity() + " unit(s) of " + m.getMedicineName()
                    + " in stock (" + inCart + " already in cart).");
        }
        cart.put(id, inCart + qty);
        flashSuccess(req, m.getMedicineName() + " added to cart.");
    }

    /** Reads quantities, customer, discount and payment method from the cart form. */
    private void syncFromForm(HttpServletRequest req, HttpSession s, Map<Integer, Integer> cart) throws Exception {
        Integer customerId = ValidationUtil.toInt(req.getParameter("customerId"));
        if (customerId != null) s.setAttribute("billingCustomerId", customerId);
        String payment = param(req, "paymentMethod");
        if (!payment.isEmpty()) s.setAttribute("billingPayment", payment);

        BigDecimal discount = ValidationUtil.isBlank(req.getParameter("discountPercent"))
                ? BigDecimal.ZERO : ValidationUtil.toDecimal(req.getParameter("discountPercent"));
        if (discount == null || discount.signum() < 0 || discount.compareTo(new BigDecimal("100")) > 0) {
            throw new BusinessException("Discount must be a number between 0 and 100.");
        }
        s.setAttribute("billingDiscount", discount);

        String[] ids = req.getParameterValues("medicineId");
        String[] qtys = req.getParameterValues("quantity");
        if (ids == null || qtys == null || ids.length != qtys.length) return;   // empty cart
        Map<Integer, Integer> updated = new LinkedHashMap<>();
        for (int i = 0; i < ids.length; i++) {
            Integer id = ValidationUtil.toInt(ids[i]);
            Integer qty = ValidationUtil.toInt(qtys[i]);
            if (id == null || qty == null) throw new BusinessException("Quantities must be whole numbers.");
            if (qty <= 0) continue;                                              // 0 removes the line
            Medicine m = medicineDao.findById(id);
            if (m == null) continue;
            if (qty > m.getStockQuantity()) {
                throw new BusinessException("Only " + m.getStockQuantity() + " unit(s) of " + m.getMedicineName() + " available.");
            }
            updated.put(id, qty);
        }
        cart.clear();
        cart.putAll(updated);
    }
}

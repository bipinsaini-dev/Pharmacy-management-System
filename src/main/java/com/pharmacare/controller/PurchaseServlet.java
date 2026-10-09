package com.pharmacare.controller;

import com.pharmacare.dao.MedicineDAO;
import com.pharmacare.dao.PurchaseDAO;
import com.pharmacare.dao.SupplierDAO;
import com.pharmacare.model.Purchase;
import com.pharmacare.model.PurchaseItem;
import com.pharmacare.service.PurchaseService;
import com.pharmacare.util.BusinessException;
import com.pharmacare.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/** Admin: purchase records. Completing a purchase increases stock (one transaction). */
@WebServlet("/admin/purchases")
public class PurchaseServlet extends BaseServlet {
    private final PurchaseDAO dao = new PurchaseDAO();
    private final PurchaseService service = new PurchaseService();
    private final SupplierDAO supplierDao = new SupplierDAO();
    private final MedicineDAO medicineDao = new MedicineDAO();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String action = param(req, "action");
        if ("new".equals(action)) {
            req.setAttribute("suppliers", supplierDao.findAll());
            req.setAttribute("medicines", medicineDao.search("", 0, 0, "", "name", "asc", 0, 1000));
            req.setAttribute("today", LocalDate.now().toString());
            render(req, resp, "admin/purchase-form");
            return;
        }
        if ("view".equals(action)) {
            Purchase p = dao.findById(intParam(req, "id", 0));
            if (p == null) {
                flashError(req, "Purchase not found.");
                redirect(req, resp, "purchases");
                return;
            }
            req.setAttribute("purchase", p);
            render(req, resp, "admin/purchase-view");
            return;
        }
        String from = param(req, "from");
        String to = param(req, "to");
        req.setAttribute("purchases", dao.list(ValidationUtil.toDate(from), ValidationUtil.toDate(to)));
        req.setAttribute("from", from);
        req.setAttribute("to", to);
        render(req, resp, "admin/purchases");
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        try {
            Purchase p = new Purchase();
            p.setSupplierId(intParam(req, "supplierId", 0));
            p.setUserId(currentUser(req).getId());
            p.setPurchaseDate(ValidationUtil.toDate(req.getParameter("purchaseDate")));

            String[] ids = req.getParameterValues("medicineId");
            String[] qtys = req.getParameterValues("quantity");
            String[] prices = req.getParameterValues("purchasePrice");
            if (ids != null && qtys != null && prices != null && ids.length == qtys.length && ids.length == prices.length) {
                for (int i = 0; i < ids.length; i++) {
                    if (ValidationUtil.isBlank(ids[i])) continue;           // empty row
                    Integer id = ValidationUtil.toInt(ids[i]);
                    Integer qty = ValidationUtil.toInt(qtys[i]);
                    BigDecimal price = ValidationUtil.toDecimal(prices[i]);
                    if (id == null || qty == null || qty < 1 || price == null || price.signum() < 0) {
                        throw new BusinessException("Each row needs a medicine, a quantity of 1+ and a valid price.");
                    }
                    PurchaseItem it = new PurchaseItem();
                    it.setMedicineId(id);
                    it.setQuantity(qty);
                    it.setPurchasePrice(price.setScale(2, RoundingMode.HALF_UP));
                    p.getItems().add(it);
                }
            }
            service.record(p);
            flashSuccess(req, "Purchase recorded and stock increased.");
            redirect(req, resp, "purchases");
        } catch (BusinessException e) {
            flashError(req, e.getMessage());
            redirect(req, resp, "purchases?action=new");
        }
    }
}

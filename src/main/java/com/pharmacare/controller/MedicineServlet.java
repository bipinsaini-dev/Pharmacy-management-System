package com.pharmacare.controller;

import com.pharmacare.dao.CategoryDAO;
import com.pharmacare.dao.SupplierDAO;
import com.pharmacare.model.Medicine;
import com.pharmacare.service.MedicineService;
import com.pharmacare.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@WebServlet({"/admin/medicines", "/pharmacist/medicines"})
public class MedicineServlet extends BaseServlet {
    private final MedicineService service = new MedicineService();
    private final CategoryDAO categoryDao = new CategoryDAO();
    private final SupplierDAO supplierDao = new SupplierDAO();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String action = param(req, "action");
        switch (action) {
            case "add":
                if (!isAdmin(req) && !isPharmacist(req)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                showForm(req, resp, defaults());
                return;
            case "edit": {
                if (!requireAdmin(req, resp)) return;
                Medicine m = service.find(intParam(req, "id", 0));
                if (m == null) {
                    flashError(req, "Medicine not found.");
                    redirect(req, resp, "medicines");
                    return;
                }
                showForm(req, resp, m);
                return;
            }
            case "view": {
                Medicine m = service.find(intParam(req, "id", 0));
                if (m == null) {
                    flashError(req, "Medicine not found.");
                    redirect(req, resp, "medicines");
                    return;
                }
                req.setAttribute("medicine", m);
                render(req, resp, "common/medicine-view");
                return;
            }
            default:
                list(req, resp);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String q = param(req, "q");
        int cat = intParam(req, "category", 0);
        int sup = intParam(req, "supplier", 0);
        String status = param(req, "status");
        String sort = param(req, "sort");
        String dir = param(req, "dir");
        int page = intParam(req, "page", 1);

        req.setAttribute("result", service.search(q, cat, sup, status, sort, dir, page));
        req.setAttribute("categories", categoryDao.findAll());
        req.setAttribute("suppliers", supplierDao.findAll());
        req.setAttribute("q", q);
        req.setAttribute("category", cat);
        req.setAttribute("supplier", sup);
        req.setAttribute("status", status);
        req.setAttribute("sort", sort);
        req.setAttribute("dir", dir);
        // URL-encoded query string used by the pagination links to keep the current filters
        req.setAttribute("qs", "q=" + enc(q) + "&category=" + cat + "&supplier=" + sup
                + "&status=" + enc(status) + "&sort=" + enc(sort) + "&dir=" + enc(dir));
        render(req, resp, "common/medicines");
    }

    private String enc(String v) {
        return URLEncoder.encode(v == null ? "" : v, StandardCharsets.UTF_8);
    }

    private Medicine defaults() {
        Medicine m = new Medicine();
        m.setReorderLevel(10);
        return m;
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Medicine m) throws Exception {
        req.setAttribute("medicine", m);
        req.setAttribute("categories", categoryDao.findAll());
        req.setAttribute("suppliers", supplierDao.findAll());
        String base = isAdmin(req) ? "admin" : "pharmacist";
        req.setAttribute("medicineBase", base);
        render(req, resp, "admin/medicine-form");
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        boolean admin = isAdmin(req);
        boolean pharmacist = isPharmacist(req);
        if (!admin && !pharmacist) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String action = param(req, "action");
        if ("delete".equals(action)) {
            if (!admin) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            int id = intParam(req, "id", 0);
            boolean deleted = service.delete(id);
            if (deleted) flashSuccess(req, "Medicine deleted.");
            else flashSuccess(req, "Medicine is used in past bills/purchases, so it was deactivated and hidden instead of deleted.");
            redirect(req, resp, "medicines");
            return;
        }
        if (!"save".equals(action)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        List<String> errors = new ArrayList<>();
        Medicine m = readForm(req, errors);
        if (pharmacist && m.getId() > 0) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        errors.addAll(service.validate(m));
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            showForm(req, resp, m);
            return;
        }
        service.save(m);
        flashSuccess(req, m.getId() > 0 ? "Medicine updated successfully." : "Medicine added successfully.");
        redirect(req, resp, "medicines");
    }

    private Medicine readForm(HttpServletRequest req, List<String> errors) {
        Medicine m = new Medicine();
        m.setId(intParam(req, "id", 0));
        m.setMedicineName(param(req, "medicineName"));
        m.setGenericName(ValidationUtil.nullIfBlank(req.getParameter("genericName")));
        m.setCategoryId(intParam(req, "categoryId", 0));
        m.setSupplierId(intParam(req, "supplierId", 0));
        m.setBatchNumber(param(req, "batchNumber"));
        m.setManufactureDate(ValidationUtil.toDate(req.getParameter("manufactureDate")));
        m.setExpiryDate(ValidationUtil.toDate(req.getParameter("expiryDate")));
        m.setPurchasePrice(money(req.getParameter("purchasePrice")));
        m.setSellingPrice(money(req.getParameter("sellingPrice")));
        m.setDescription(ValidationUtil.nullIfBlank(req.getParameter("description")));

        Integer stock = ValidationUtil.toInt(req.getParameter("stockQuantity"));
        if (stock == null) {
            errors.add("Stock quantity must be a whole number.");
            m.setStockQuantity(0);
        } else {
            m.setStockQuantity(stock);
        }
        Integer reorder = ValidationUtil.toInt(req.getParameter("reorderLevel"));
        if (reorder == null) {
            errors.add("Reorder level must be a whole number.");
            m.setReorderLevel(10);
        } else {
            m.setReorderLevel(reorder);
        }
        return m;
    }

    private BigDecimal money(String s) {
        BigDecimal v = ValidationUtil.toDecimal(s);
        return v == null ? null : v.setScale(2, RoundingMode.HALF_UP);
    }
}

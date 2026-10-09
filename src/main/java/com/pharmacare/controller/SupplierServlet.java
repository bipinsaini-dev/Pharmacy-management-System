package com.pharmacare.controller;

import com.pharmacare.dao.SupplierDAO;
import com.pharmacare.model.Supplier;
import com.pharmacare.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/suppliers")
public class SupplierServlet extends BaseServlet {
    private final SupplierDAO dao = new SupplierDAO();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String q = param(req, "q");
        req.setAttribute("q", q);
        req.setAttribute("suppliers", dao.search(q));
        render(req, resp, "admin/suppliers");
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String action = param(req, "action");
        if ("delete".equals(action)) {
            try {
                dao.delete(intParam(req, "id", 0));
                flashSuccess(req, "Supplier deleted.");
            } catch (SQLIntegrityConstraintViolationException e) {
                flashError(req, "This supplier is used by medicines or purchases and cannot be deleted.");
            }
            redirect(req, resp, "suppliers");
            return;
        }
        if (!"save".equals(action)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Supplier s = new Supplier();
        s.setId(intParam(req, "id", 0));
        s.setSupplierName(param(req, "supplierName"));
        s.setContactPerson(ValidationUtil.nullIfBlank(req.getParameter("contactPerson")));
        s.setPhone(ValidationUtil.nullIfBlank(req.getParameter("phone")));
        s.setEmail(ValidationUtil.nullIfBlank(req.getParameter("email")));
        s.setAddress(ValidationUtil.nullIfBlank(req.getParameter("address")));

        List<String> errors = new ArrayList<>();
        if (s.getSupplierName().length() < 2) errors.add("Supplier name is required.");
        if (s.getPhone() != null && !ValidationUtil.isPhone(s.getPhone())) errors.add("Phone must contain 10 to 13 digits.");
        if (s.getEmail() != null && !ValidationUtil.isEmail(s.getEmail())) errors.add("Email address is not valid.");
        if (!errors.isEmpty()) {
            flashError(req, String.join(" ", errors));
        } else if (s.getId() > 0) {
            dao.update(s);
            flashSuccess(req, "Supplier updated.");
        } else {
            dao.create(s);
            flashSuccess(req, "Supplier added.");
        }
        redirect(req, resp, "suppliers");
    }
}

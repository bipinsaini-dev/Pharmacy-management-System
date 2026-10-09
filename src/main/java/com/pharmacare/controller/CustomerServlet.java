package com.pharmacare.controller;

import com.pharmacare.dao.CustomerDAO;
import com.pharmacare.dao.SaleDAO;
import com.pharmacare.model.Customer;
import com.pharmacare.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@WebServlet({"/admin/customers", "/pharmacist/customers"})
public class CustomerServlet extends BaseServlet {
    /** Seeded "Walk-in Customer" row used for anonymous sales; it is protected from edit/delete. */
    private static final int WALK_IN_ID = 1;
    private final CustomerDAO dao = new CustomerDAO();
    private final SaleDAO saleDao = new SaleDAO();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if ("view".equals(param(req, "action"))) {
            Customer c = dao.findById(intParam(req, "id", 0));
            if (c == null) {
                flashError(req, "Customer not found.");
                redirect(req, resp, "customers");
                return;
            }
            req.setAttribute("customer", c);
            req.setAttribute("bills", saleDao.search("", null, null, 0, c.getId(), 500));
            render(req, resp, "common/customer-view");
            return;
        }
        String q = param(req, "q");
        req.setAttribute("q", q);
        req.setAttribute("customers", dao.search(q));
        req.setAttribute("walkInId", WALK_IN_ID);
        render(req, resp, "common/customers");
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String action = param(req, "action");
        boolean fromBilling = "billing".equals(param(req, "returnTo"));
        String back = fromBilling ? "billing" : "customers";

        if ("delete".equals(action)) {
            int id = intParam(req, "id", 0);
            if (id == WALK_IN_ID) {
                flashError(req, "The walk-in customer cannot be deleted.");
            } else {
                try {
                    dao.delete(id);
                    flashSuccess(req, "Customer deleted.");
                } catch (SQLIntegrityConstraintViolationException e) {
                    flashError(req, "This customer has billing history and cannot be deleted.");
                }
            }
            redirect(req, resp, "customers");
            return;
        }
        if (!"save".equals(action)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Customer c = new Customer();
        c.setId(intParam(req, "id", 0));
        c.setCustomerName(param(req, "customerName"));
        c.setPhone(ValidationUtil.nullIfBlank(req.getParameter("phone")));
        c.setEmail(ValidationUtil.nullIfBlank(req.getParameter("email")));
        c.setAddress(ValidationUtil.nullIfBlank(req.getParameter("address")));

        List<String> errors = new ArrayList<>();
        if (c.getCustomerName().length() < 2) errors.add("Customer name is required.");
        if (c.getPhone() != null && !ValidationUtil.isPhone(c.getPhone())) errors.add("Phone must contain 10 to 13 digits.");
        if (c.getEmail() != null && !ValidationUtil.isEmail(c.getEmail())) errors.add("Email address is not valid.");
        if (c.getId() == WALK_IN_ID) errors.add("The walk-in customer cannot be edited.");
        if (!errors.isEmpty()) {
            flashError(req, String.join(" ", errors));
            redirect(req, resp, back);
            return;
        }
        if (c.getId() > 0) {
            dao.update(c);
            flashSuccess(req, "Customer updated.");
        } else {
            int newId = dao.create(c);
            flashSuccess(req, "Customer added.");
            if (fromBilling) {
                req.getSession().setAttribute("billingCustomerId", newId);
            }
        }
        redirect(req, resp, back);
    }
}

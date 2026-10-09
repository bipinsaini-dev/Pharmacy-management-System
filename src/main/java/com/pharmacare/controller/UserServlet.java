package com.pharmacare.controller;

import com.pharmacare.dao.UserDAO;
import com.pharmacare.model.User;
import com.pharmacare.service.AuthService;
import com.pharmacare.util.BusinessException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/** Admin: view, add, edit, enable/disable and delete users. */
@WebServlet("/admin/users")
public class UserServlet extends BaseServlet {
    private final UserDAO dao = new UserDAO();
    private final AuthService authService = new AuthService();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String q = param(req, "q");
        req.setAttribute("q", q);
        req.setAttribute("users", dao.search(q));
        render(req, resp, "admin/users");
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String action = param(req, "action");
        User me = currentUser(req);

        if ("delete".equals(action)) {
            int id = intParam(req, "id", 0);
            if (id == me.getId()) {
                flashError(req, "You cannot delete your own account.");
            } else {
                try {
                    dao.delete(id);
                    flashSuccess(req, "User deleted.");
                } catch (SQLIntegrityConstraintViolationException e) {
                    dao.setStatus(id, "INACTIVE");
                    flashSuccess(req, "User has billing/purchase history, so the account was disabled instead of deleted.");
                }
            }
            redirect(req, resp, "users");
            return;
        }
        if (!"save".equals(action)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User u = new User();
        u.setId(intParam(req, "id", 0));
        u.setFullName(param(req, "fullName"));
        u.setEmail(param(req, "email"));
        u.setPhone(param(req, "phone"));
        u.setUsername(param(req, "username"));
        u.setRole("ADMIN".equals(param(req, "role")) ? "ADMIN" : "PHARMACIST");
        u.setStatus("INACTIVE".equals(param(req, "status")) ? "INACTIVE" : "ACTIVE");
        if (u.getId() == me.getId()) {          // an admin cannot lock themselves out
            u.setRole("ADMIN");
            u.setStatus("ACTIVE");
        }
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirmPassword");
        boolean isNew = u.getId() == 0;

        List<String> errors = authService.validate(u, password, confirm, isNew);
        if (!errors.isEmpty()) {
            flashError(req, String.join(" ", errors));
            redirect(req, resp, "users");
            return;
        }
        try {
            if (isNew) {
                authService.create(u, password);
                flashSuccess(req, "User created.");
            } else {
                authService.updateUser(u, password);
                flashSuccess(req, "User updated.");
            }
        } catch (BusinessException e) {
            flashError(req, e.getMessage());
        }
        redirect(req, resp, "users");
    }
}

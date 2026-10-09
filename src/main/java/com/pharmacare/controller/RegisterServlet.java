package com.pharmacare.controller;

import com.pharmacare.model.User;
import com.pharmacare.service.AuthService;
import com.pharmacare.util.BusinessException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        render(req, resp, "auth/register");
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User u = new User();
        u.setFullName(param(req, "fullName"));
        u.setEmail(param(req, "email"));
        u.setPhone(param(req, "phone"));
        u.setUsername(param(req, "username"));
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirmPassword");

        // Public registration can only ever create PHARMACIST accounts (admins are created by an admin).
        List<String> errors = authService.validate(u, password, confirm, true);
        if (errors.isEmpty()) {
            try {
                authService.register(u, password);
                flashSuccess(req, "Registration successful. You can now log in.");
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            } catch (BusinessException e) {
                errors.add(e.getMessage());
            }
        }
        req.setAttribute("errors", errors);
        req.setAttribute("form", u);
        render(req, resp, "auth/register");
    }
}

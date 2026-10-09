package com.pharmacare.controller;

import com.pharmacare.service.AuthService;
import com.pharmacare.util.BusinessException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends BaseServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        render(req, resp, "auth/forgot-password");
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        try {
            authService.resetPassword(param(req, "username"), param(req, "email"),
                    req.getParameter("newPassword"), req.getParameter("confirmPassword"));
            flashSuccess(req, "Password reset successfully. Please log in with your new password.");
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (BusinessException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("username", param(req, "username"));
            req.setAttribute("email", param(req, "email"));
            render(req, resp, "auth/forgot-password");
        }
    }
}

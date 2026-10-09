package com.pharmacare.controller;

import com.pharmacare.service.AuthService;
import com.pharmacare.util.BusinessException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** "My profile" page with the change-password form (both roles). */
@WebServlet({"/admin/profile", "/pharmacist/profile"})
public class ProfileServlet extends BaseServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        render(req, resp, "common/profile");
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        try {
            authService.changePassword(currentUser(req).getId(), req.getParameter("currentPassword"),
                    req.getParameter("newPassword"), req.getParameter("confirmPassword"));
            flashSuccess(req, "Password changed successfully.");
            redirect(req, resp, "profile");
        } catch (BusinessException e) {
            req.setAttribute("error", e.getMessage());
            render(req, resp, "common/profile");
        }
    }
}

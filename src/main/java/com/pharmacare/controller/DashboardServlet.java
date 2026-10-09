package com.pharmacare.controller;

import com.pharmacare.service.ReportService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

@WebServlet({"/admin/dashboard", "/pharmacist/dashboard"})
public class DashboardServlet extends BaseServlet {
    private final ReportService reports = new ReportService();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Map<String, Object> stats = isAdmin(req)
                ? reports.adminDashboard()
                : reports.pharmacistDashboard(currentUser(req).getId());
        req.setAttribute("stats", stats);
        render(req, resp, base(req) + "/dashboard");
    }
}

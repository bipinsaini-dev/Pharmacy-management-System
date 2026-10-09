package com.pharmacare.controller;

import com.pharmacare.dao.SaleDAO;
import com.pharmacare.model.Sale;
import com.pharmacare.model.User;
import com.pharmacare.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.List;

/** Sales list (admin: all bills, pharmacist: own bills) and the printable invoice. */
@WebServlet({"/admin/sales", "/pharmacist/sales"})
public class SalesServlet extends BaseServlet {
    private final SaleDAO dao = new SaleDAO();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User me = currentUser(req);
        if ("invoice".equals(param(req, "action"))) {
            Sale sale = dao.findById(intParam(req, "id", 0));
            if (sale == null) {
                flashError(req, "Invoice not found.");
                redirect(req, resp, "sales");
                return;
            }
            if (!me.isAdmin() && sale.getUserId() != me.getId()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);   // pharmacists only see their own bills
                return;
            }
            req.setAttribute("sale", sale);
            render(req, resp, "common/invoice");
            return;
        }

        String q = param(req, "q");
        String from = param(req, "from");
        String to = param(req, "to");
        List<Sale> sales = dao.search(q, ValidationUtil.toDate(from), ValidationUtil.toDate(to),
                me.isAdmin() ? 0 : me.getId(), 0, 300);
        BigDecimal total = BigDecimal.ZERO;
        for (Sale s : sales) total = total.add(s.getGrandTotal());

        req.setAttribute("sales", sales);
        req.setAttribute("listTotal", total);
        req.setAttribute("q", q);
        req.setAttribute("from", from);
        req.setAttribute("to", to);
        render(req, resp, "common/sales");
    }
}

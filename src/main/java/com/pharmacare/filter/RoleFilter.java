package com.pharmacare.filter;

import com.pharmacare.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/** Role-based authorization: /admin/* needs ADMIN, /pharmacist/* needs PHARMACIST. */
@WebFilter(urlPatterns = {"/admin/*", "/pharmacist/*"})
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String path = request.getServletPath();
        boolean adminArea = path.startsWith("/admin");
        boolean allowed = adminArea ? "ADMIN".equals(user.getRole()) : "PHARMACIST".equals(user.getRole());
        if (!allowed) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);   // shows 403.jsp (unauthorized page)
            return;
        }
        chain.doFilter(req, res);
    }
}

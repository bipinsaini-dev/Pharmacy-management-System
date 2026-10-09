package com.pharmacare.controller;

import com.pharmacare.model.User;
import com.pharmacare.service.AuthService;
import com.pharmacare.util.BusinessException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    private static final String REMEMBER_COOKIE = "pc_user";
    private final AuthService authService = new AuthService();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User u = currentUser(req);
        if (u != null) {
            resp.sendRedirect(req.getContextPath() + "/" + (u.isAdmin() ? "admin" : "pharmacist") + "/dashboard");
            return;
        }
        if (req.getParameter("logout") != null) {
            req.setAttribute("flashSuccess", "You have been logged out.");
        }
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (REMEMBER_COOKIE.equals(c.getName())) {
                    req.setAttribute("identifier", c.getValue());
                    req.setAttribute("remember", true);
                }
            }
        }
        render(req, resp, "auth/login");
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String identifier = param(req, "identifier");
        String password = req.getParameter("password");
        boolean remember = req.getParameter("remember") != null;
        try {
            User user = authService.login(identifier, password);

            // new session on login prevents session fixation
            HttpSession old = req.getSession(false);
            if (old != null) old.invalidate();
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(30 * 60);

            // "Remember me" only remembers the username to pre-fill the login form (never the password)
            Cookie cookie = new Cookie(REMEMBER_COOKIE, remember ? user.getUsername() : "");
            cookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
            cookie.setHttpOnly(true);
            cookie.setMaxAge(remember ? 30 * 24 * 3600 : 0);
            resp.addCookie(cookie);

            log.info("User '{}' logged in as {}", user.getUsername(), user.getRole());
            resp.sendRedirect(req.getContextPath() + "/" + (user.isAdmin() ? "admin" : "pharmacist") + "/dashboard");
        } catch (BusinessException e) {
            log.warn("Failed login for '{}'", identifier);
            req.setAttribute("error", e.getMessage());
            req.setAttribute("identifier", identifier);
            req.setAttribute("remember", remember);
            render(req, resp, "auth/login");
        }
    }
}

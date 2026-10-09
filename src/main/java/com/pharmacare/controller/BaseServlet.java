package com.pharmacare.controller;

import com.pharmacare.model.User;
import com.pharmacare.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Shared plumbing for all controllers: logging of unexpected errors, flash messages,
 * view rendering and role helpers. Subclasses implement get()/post().
 */
public abstract class BaseServlet extends HttpServlet {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            get(req, resp);
        } catch (ServletException | IOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error on GET {}", req.getRequestURI(), e);
            throw new ServletException("Request failed", e);
        }
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            post(req, resp);
        } catch (ServletException | IOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error on POST {}", req.getRequestURI(), e);
            throw new ServletException("Request failed", e);
        }
    }

    // ---- session / role helpers ---------------------------------------------------------------

    protected User currentUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (User) s.getAttribute("user");
    }

    /** "admin" or "pharmacist", taken from the URL prefix the request came in on. */
    protected String base(HttpServletRequest req) {
        return req.getServletPath().startsWith("/admin") ? "admin" : "pharmacist";
    }

    protected boolean isAdmin(HttpServletRequest req) {
        User u = currentUser(req);
        return u != null && u.isAdmin();
    }

    protected boolean isPharmacist(HttpServletRequest req) {
        User u = currentUser(req);
        return u != null && "PHARMACIST".equals(u.getRole());
    }

    /** Sends 403 and returns false when the caller is not an administrator. */
    protected boolean requireAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (isAdmin(req)) return true;
        resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }

    // ---- flash messages & navigation ----------------------------------------------------------

    protected void flashSuccess(HttpServletRequest req, String msg) {
        req.getSession().setAttribute("flashSuccess", msg);
    }

    protected void flashError(HttpServletRequest req, String msg) {
        req.getSession().setAttribute("flashError", msg);
    }

    /** Redirects to a page inside the caller's area, e.g. redirect(req, resp, "medicines"). */
    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/" + base(req) + "/" + path);
    }

    /** Forwards to /WEB-INF/views/{view}.jsp after moving flash messages into the request. */
    protected void render(HttpServletRequest req, HttpServletResponse resp, String view) throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        if (s != null) {
            moveFlash(s, req, "flashSuccess");
            moveFlash(s, req, "flashError");
        }
        String path = req.getServletPath();
        if (path.startsWith("/admin") || path.startsWith("/pharmacist")) {
            req.setAttribute("base", base(req));
        }
        req.getRequestDispatcher("/WEB-INF/views/" + view + ".jsp").forward(req, resp);
    }

    private void moveFlash(HttpSession s, HttpServletRequest req, String key) {
        Object v = s.getAttribute(key);
        if (v != null) {
            req.setAttribute(key, v);
            s.removeAttribute(key);
        }
    }

    // ---- parameter helpers -------------------------------------------------------------------

    protected int intParam(HttpServletRequest req, String name, int def) {
        Integer v = ValidationUtil.toInt(req.getParameter(name));
        return v == null ? def : v;
    }

    protected String param(HttpServletRequest req, String name) {
        return ValidationUtil.clean(req.getParameter(name));
    }
}

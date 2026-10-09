package com.pharmacare.controller;

import com.pharmacare.dao.CategoryDAO;
import com.pharmacare.model.Category;
import com.pharmacare.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.SQLIntegrityConstraintViolationException;

@WebServlet("/admin/categories")
public class CategoryServlet extends BaseServlet {
    private final CategoryDAO dao = new CategoryDAO();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.setAttribute("categories", dao.findAll());
        render(req, resp, "admin/categories");
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String action = param(req, "action");
        if ("delete".equals(action)) {
            try {
                dao.delete(intParam(req, "id", 0));
                flashSuccess(req, "Category deleted.");
            } catch (SQLIntegrityConstraintViolationException e) {
                flashError(req, "This category contains medicines and cannot be deleted.");
            }
            redirect(req, resp, "categories");
            return;
        }
        if (!"save".equals(action)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Category c = new Category();
        c.setId(intParam(req, "id", 0));
        c.setCategoryName(param(req, "categoryName"));
        c.setDescription(ValidationUtil.nullIfBlank(req.getParameter("description")));

        if (c.getCategoryName().length() < 2) {
            flashError(req, "Category name is required.");
        } else if (dao.nameExists(c.getCategoryName(), c.getId())) {
            flashError(req, "A category with that name already exists.");
        } else if (c.getId() > 0) {
            dao.update(c);
            flashSuccess(req, "Category updated.");
        } else {
            dao.create(c);
            flashSuccess(req, "Category added.");
        }
        redirect(req, resp, "categories");
    }
}

package com.pharmacare.service;

import com.pharmacare.dao.CategoryDAO;
import com.pharmacare.dao.MedicineDAO;
import com.pharmacare.dao.SupplierDAO;
import com.pharmacare.model.Medicine;
import com.pharmacare.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicineService {
    public static final int PAGE_SIZE = 10;

    private final MedicineDAO dao = new MedicineDAO();
    private final CategoryDAO categoryDao = new CategoryDAO();
    private final SupplierDAO supplierDao = new SupplierDAO();

    /** One page of search results. */
    public static class Page {
        private final List<Medicine> items;
        private final int total;
        private final int page;
        private final int pages;

        Page(List<Medicine> items, int total, int page, int pages) {
            this.items = items;
            this.total = total;
            this.page = page;
            this.pages = pages;
        }
        public List<Medicine> getItems() { return items; }
        public int getTotal() { return total; }
        public int getPage() { return page; }
        public int getPages() { return pages; }
    }

    public Page search(String q, int catId, int supId, String status, String sort, String dir, int page) throws SQLException {
        int total = dao.count(q, catId, supId, status);
        int pages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
        int current = Math.min(Math.max(page, 1), pages);
        List<Medicine> items = dao.search(q, catId, supId, status, sort, dir, (current - 1) * PAGE_SIZE, PAGE_SIZE);
        return new Page(items, total, current, pages);
    }

    public Medicine find(int id) throws SQLException {
        return dao.findById(id);
    }

    public List<String> validate(Medicine m) throws SQLException {
        List<String> errors = new ArrayList<>();
        if (ValidationUtil.isBlank(m.getMedicineName())) errors.add("Medicine name is required.");
        if (ValidationUtil.isBlank(m.getBatchNumber())) errors.add("Batch number is required.");
        if (m.getCategoryId() <= 0 || categoryDao.findById(m.getCategoryId()) == null) errors.add("Please select a valid category.");
        if (m.getSupplierId() <= 0 || supplierDao.findById(m.getSupplierId()) == null) errors.add("Please select a valid supplier.");

        BigDecimal pp = m.getPurchasePrice();
        BigDecimal sp = m.getSellingPrice();
        if (pp == null) errors.add("Purchase price is required and must be a number.");
        else if (pp.signum() < 0) errors.add("Purchase price cannot be negative.");
        if (sp == null) errors.add("Selling price is required and must be a number.");
        else if (sp.signum() < 0) errors.add("Selling price cannot be negative.");

        BigDecimal max = new BigDecimal("99999999.99");
        if ((pp != null && pp.compareTo(max) > 0) || (sp != null && sp.compareTo(max) > 0)) {
            errors.add("Prices cannot exceed 99,999,999.99.");
        }
        if (m.getStockQuantity() < 0) errors.add("Stock quantity cannot be negative.");
        if (m.getReorderLevel() < 0) errors.add("Reorder level cannot be negative.");

        if (m.getManufactureDate() == null) errors.add("Manufacture date is required (yyyy-mm-dd).");
        if (m.getExpiryDate() == null) errors.add("Expiry date is required (yyyy-mm-dd).");
        if (m.getManufactureDate() != null && m.getExpiryDate() != null) {
            LocalDate mfg = m.getManufactureDate().toLocalDate();
            LocalDate exp = m.getExpiryDate().toLocalDate();
            if (!exp.isAfter(mfg)) errors.add("Expiry date must be after the manufacture date.");
            if (mfg.isAfter(LocalDate.now())) errors.add("Manufacture date cannot be in the future.");
        }
        if (errors.isEmpty() && dao.batchExists(m.getMedicineName(), m.getBatchNumber(), m.getId())) {
            errors.add("This medicine already exists with the same batch number.");
        }
        return errors;
    }

    public void save(Medicine m) throws SQLException {
        try {
            if (m.getId() > 0) dao.update(m); else dao.create(m);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new SQLException("Duplicate or invalid medicine data: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a medicine. If it is referenced by past bills or purchases it cannot be removed
     * (history must stay intact), so it is deactivated instead.
     * @return true if physically deleted, false if only deactivated
     */
    public boolean delete(int id) throws SQLException {
        try {
            dao.delete(id);
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            dao.deactivate(id);
            return false;
        }
    }
}

package com.pharmacare.service;

import com.pharmacare.dao.*;
import com.pharmacare.model.Category;
import com.pharmacare.model.Medicine;
import com.pharmacare.model.Purchase;
import com.pharmacare.util.JsonUtil;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/** Dashboard statistics and tabular reports. */
public class ReportService {
    private final MedicineDAO medicineDao = new MedicineDAO();
    private final SaleDAO saleDao = new SaleDAO();
    private final PurchaseDAO purchaseDao = new PurchaseDAO();
    private final CustomerDAO customerDao = new CustomerDAO();
    private final UserDAO userDao = new UserDAO();
    private final CategoryDAO categoryDao = new CategoryDAO();

    /** A generic report: title, column headers and string rows (used by the page and CSV export). */
    public static class ReportTable {
        private final String title;
        private final List<String> headers = new ArrayList<>();
        private final List<List<String>> rows = new ArrayList<>();
        private String summary = "";

        ReportTable(String title, String... headers) {
            this.title = title;
            this.headers.addAll(Arrays.asList(headers));
        }
        void add(Object... cells) {
            List<String> row = new ArrayList<>();
            for (Object o : cells) row.add(o == null ? "" : String.valueOf(o));
            rows.add(row);
        }
        public String getTitle() { return title; }
        public List<String> getHeaders() { return headers; }
        public List<List<String>> getRows() { return rows; }
        public String getSummary() { return summary; }
    }

    // ------------------------------------------------------------------------------------------
    // Dashboards
    // ------------------------------------------------------------------------------------------

    public Map<String, Object> adminDashboard() throws SQLException {
        Map<String, Object> m = new LinkedHashMap<>();
        Date today = Date.valueOf(LocalDate.now());
        m.put("totalMedicines", medicineDao.countAll());
        m.put("totalUsers", userDao.count());
        m.put("totalCustomers", customerDao.count());
        m.put("todaySales", saleDao.sumTotal(today, today, 0));
        m.put("totalSales", saleDao.sumTotal(null, null, 0));
        m.put("lowStock", medicineDao.countLowStock());
        m.put("expired", medicineDao.countExpired());
        m.put("nearExpiry", medicineDao.countNearExpiry());

        Map<String, BigDecimal> last7 = dailySeries(7);
        Map<String, BigDecimal> last30 = dailySeries(30);
        m.put("last7Labels", JsonUtil.array(last7.keySet()));
        m.put("last7Values", JsonUtil.array(last7.values()));
        m.put("last30Labels", JsonUtil.array(last30.keySet()));
        m.put("last30Values", JsonUtil.array(last30.values()));

        List<String> topNames = new ArrayList<>();
        List<Long> topQty = new ArrayList<>();
        Date from = Date.valueOf(LocalDate.now().minusDays(365));
        for (Map<String, Object> r : saleDao.topSelling(from, today, 5)) {
            topNames.add((String) r.get("name"));
            topQty.add((Long) r.get("qty"));
        }
        m.put("topLabels", JsonUtil.array(topNames));
        m.put("topValues", JsonUtil.array(topQty));

        List<String> catNames = new ArrayList<>();
        List<Integer> catCounts = new ArrayList<>();
        for (Category c : categoryDao.findAll()) {
            if (c.getMedicineCount() > 0) {
                catNames.add(c.getCategoryName());
                catCounts.add(c.getMedicineCount());
            }
        }
        m.put("catLabels", JsonUtil.array(catNames));
        m.put("catValues", JsonUtil.array(catCounts));
        return m;
    }

    public Map<String, Object> pharmacistDashboard(int userId) throws SQLException {
        Map<String, Object> m = new LinkedHashMap<>();
        Date today = Date.valueOf(LocalDate.now());
        m.put("totalMedicines", medicineDao.countAll());
        m.put("totalCustomers", customerDao.count());
        m.put("todaySales", saleDao.sumTotal(today, today, userId));
        m.put("todayBills", saleDao.countBills(today, today, userId));
        m.put("totalSales", saleDao.sumTotal(null, null, userId));
        m.put("totalBills", saleDao.countBills(null, null, userId));
        m.put("lowStock", medicineDao.countLowStock());
        m.put("expired", medicineDao.countExpired());
        m.put("nearExpiry", medicineDao.countNearExpiry());
        m.put("recentBills", saleDao.search("", null, null, userId, 0, 5));
        return m;
    }

    /** Date label -> total for the last N days, zero-filled. */
    private Map<String, BigDecimal> dailySeries(int days) throws SQLException {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(days - 1L);
        Map<String, BigDecimal> byDay = new HashMap<>();
        for (Map<String, Object> r : saleDao.groupedReport(SaleDAO.BY_DAY, Date.valueOf(start), Date.valueOf(end))) {
            byDay.put((String) r.get("period"), (BigDecimal) r.get("total"));
        }
        Map<String, BigDecimal> series = new LinkedHashMap<>();
        DateTimeFormatter label = DateTimeFormatter.ofPattern("dd MMM");
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            series.put(d.format(label), byDay.getOrDefault(d.toString(), BigDecimal.ZERO));
        }
        return series;
    }

    // ------------------------------------------------------------------------------------------
    // Reports
    // ------------------------------------------------------------------------------------------

    public static final List<String> TYPES =
        Arrays.asList("daily", "monthly", "yearly", "top", "low", "expired", "near", "purchase");

    public LocalDate defaultFrom(String type) {
        LocalDate now = LocalDate.now();
        switch (type) {
            case "monthly": return now.withDayOfYear(1);
            case "yearly":  return now.minusYears(5).withDayOfYear(1);
            default:        return now.withDayOfMonth(1);
        }
    }

    public ReportTable build(String type, Date from, Date to) throws SQLException {
        switch (type) {
            case "monthly":  return salesReport("Monthly Sales Report", "Month", SaleDAO.BY_MONTH, from, to);
            case "yearly":   return salesReport("Yearly Sales Report", "Year", SaleDAO.BY_YEAR, from, to);
            case "top":      return topSelling(from, to);
            case "low":      return lowStock();
            case "expired":  return expired();
            case "near":     return nearExpiry();
            case "purchase": return purchases(from, to);
            default:         return salesReport("Daily Sales Report", "Date", SaleDAO.BY_DAY, from, to);
        }
    }

    private String money(BigDecimal v) {
        return String.format("%.2f", v == null ? BigDecimal.ZERO : v);
    }

    private ReportTable salesReport(String title, String periodHeader, String expr, Date from, Date to) throws SQLException {
        ReportTable t = new ReportTable(title + " (INR)", periodHeader, "Bills", "Subtotal", "Discount", "GST", "Total");
        BigDecimal sum = BigDecimal.ZERO;
        long bills = 0;
        for (Map<String, Object> r : saleDao.groupedReport(expr, from, to)) {
            t.add(r.get("period"), r.get("bills"), money((BigDecimal) r.get("subtotal")), money((BigDecimal) r.get("discount")),
                  money((BigDecimal) r.get("gst")), money((BigDecimal) r.get("total")));
            sum = sum.add((BigDecimal) r.get("total"));
            bills += (Long) r.get("bills");
        }
        t.summary = bills + " bill(s), total sales: " + money(sum);
        return t;
    }

    private ReportTable topSelling(Date from, Date to) throws SQLException {
        ReportTable t = new ReportTable("Top Selling Medicines", "Rank", "Medicine", "Units Sold", "Revenue (INR)");
        int rank = 1;
        for (Map<String, Object> r : saleDao.topSelling(from, to, 20)) {
            t.add(rank++, r.get("name"), r.get("qty"), money((BigDecimal) r.get("revenue")));
        }
        return t;
    }

    private ReportTable lowStock() throws SQLException {
        ReportTable t = new ReportTable("Low Stock Report", "ID", "Medicine", "Batch", "Category", "Supplier", "Stock", "Reorder Level", "Status");
        for (Medicine m : medicineDao.findLowStock()) {
            t.add(m.getId(), m.getMedicineName(), m.getBatchNumber(), m.getCategoryName(), m.getSupplierName(),
                  m.getStockQuantity(), m.getReorderLevel(), m.getStockStatus());
        }
        t.summary = t.rows.size() + " medicine(s) at or below reorder level";
        return t;
    }

    private ReportTable expired() throws SQLException {
        ReportTable t = new ReportTable("Expired Medicines Report", "ID", "Medicine", "Batch", "Expiry Date", "Days Overdue", "Stock", "Stock Value at Cost (INR)");
        LocalDate today = LocalDate.now();
        for (Medicine m : medicineDao.findExpired()) {
            long overdue = ChronoUnit.DAYS.between(m.getExpiryDate().toLocalDate(), today);
            t.add(m.getId(), m.getMedicineName(), m.getBatchNumber(), m.getExpiryDate(), overdue, m.getStockQuantity(),
                  money(m.getPurchasePrice().multiply(BigDecimal.valueOf(m.getStockQuantity()))));
        }
        t.summary = t.rows.size() + " expired medicine(s)";
        return t;
    }

    private ReportTable nearExpiry() throws SQLException {
        ReportTable t = new ReportTable("Near Expiry Medicines (next " + Medicine.NEAR_EXPIRY_DAYS + " days)",
                                        "ID", "Medicine", "Batch", "Expiry Date", "Days Left", "Stock");
        LocalDate today = LocalDate.now();
        for (Medicine m : medicineDao.findNearExpiry()) {
            long left = ChronoUnit.DAYS.between(today, m.getExpiryDate().toLocalDate());
            t.add(m.getId(), m.getMedicineName(), m.getBatchNumber(), m.getExpiryDate(), left, m.getStockQuantity());
        }
        t.summary = t.rows.size() + " medicine(s) expiring soon";
        return t;
    }

    private ReportTable purchases(Date from, Date to) throws SQLException {
        ReportTable t = new ReportTable("Purchase Report (INR)", "ID", "Date", "Supplier", "Recorded By", "Total");
        BigDecimal sum = BigDecimal.ZERO;
        for (Purchase p : purchaseDao.list(from, to)) {
            t.add(p.getId(), p.getPurchaseDate(), p.getSupplierName(), p.getUserName(), money(p.getTotalAmount()));
            sum = sum.add(p.getTotalAmount());
        }
        t.summary = t.rows.size() + " purchase(s), total spent: " + money(sum);
        return t;
    }
}

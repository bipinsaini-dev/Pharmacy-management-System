package com.pharmacare.dao;

import com.pharmacare.model.Sale;
import com.pharmacare.model.SaleItem;
import com.pharmacare.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SaleDAO {
    private static final String SELECT =
        "SELECT s.id, s.invoice_number, s.customer_id, s.user_id, s.subtotal, s.discount, s.gst, s.grand_total, " +
        "s.payment_method, s.sale_date, c.customer_name, c.phone AS customer_phone, c.address AS customer_address, " +
        "u.full_name AS pharmacist_name FROM sales s JOIN customers c ON c.id = s.customer_id JOIN users u ON u.id = s.user_id ";

    private Sale map(ResultSet rs) throws SQLException {
        Sale s = new Sale();
        s.setId(rs.getInt("id"));
        s.setInvoiceNumber(rs.getString("invoice_number"));
        s.setCustomerId(rs.getInt("customer_id"));
        s.setUserId(rs.getInt("user_id"));
        s.setSubtotal(rs.getBigDecimal("subtotal"));
        s.setDiscount(rs.getBigDecimal("discount"));
        s.setGst(rs.getBigDecimal("gst"));
        s.setGrandTotal(rs.getBigDecimal("grand_total"));
        s.setPaymentMethod(rs.getString("payment_method"));
        s.setSaleDate(rs.getTimestamp("sale_date"));
        s.setCustomerName(rs.getString("customer_name"));
        s.setCustomerPhone(rs.getString("customer_phone"));
        s.setCustomerAddress(rs.getString("customer_address"));
        s.setPharmacistName(rs.getString("pharmacist_name"));
        return s;
    }

    /** Inserts the sale and its items using the caller's transaction. Sets the generated id on the sale. */
    public void insert(Connection c, Sale s) throws SQLException {
        String sql = "INSERT INTO sales (invoice_number, customer_id, user_id, subtotal, discount, gst, grand_total, payment_method) " +
                     "VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getInvoiceNumber());
            ps.setInt(2, s.getCustomerId());
            ps.setInt(3, s.getUserId());
            ps.setBigDecimal(4, s.getSubtotal());
            ps.setBigDecimal(5, s.getDiscount());
            ps.setBigDecimal(6, s.getGst());
            ps.setBigDecimal(7, s.getGrandTotal());
            ps.setString(8, s.getPaymentMethod());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("No sale id generated");
                s.setId(keys.getInt(1));
            }
        }
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO sale_items (sale_id, medicine_id, quantity, price, total) VALUES (?,?,?,?,?)")) {
            for (SaleItem it : s.getItems()) {
                ps.setInt(1, s.getId());
                ps.setInt(2, it.getMedicineId());
                ps.setInt(3, it.getQuantity());
                ps.setBigDecimal(4, it.getPrice());
                ps.setBigDecimal(5, it.getTotal());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public Sale findById(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            Sale sale;
            try (PreparedStatement ps = c.prepareStatement(SELECT + " WHERE s.id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return null;
                    sale = map(rs);
                }
            }
            String sql = "SELECT si.id, si.sale_id, si.medicine_id, si.quantity, si.price, si.total, m.medicine_name, m.batch_number " +
                         "FROM sale_items si JOIN medicines m ON m.id = si.medicine_id WHERE si.sale_id = ? ORDER BY si.id";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        SaleItem it = new SaleItem();
                        it.setId(rs.getInt("id"));
                        it.setSaleId(rs.getInt("sale_id"));
                        it.setMedicineId(rs.getInt("medicine_id"));
                        it.setQuantity(rs.getInt("quantity"));
                        it.setPrice(rs.getBigDecimal("price"));
                        it.setTotal(rs.getBigDecimal("total"));
                        it.setMedicineName(rs.getString("medicine_name"));
                        it.setBatchNumber(rs.getString("batch_number"));
                        sale.getItems().add(it);
                    }
                }
            }
            return sale;
        }
    }

    /** Searches invoices. userId / customerId are optional filters (pass 0 for none). */
    public List<Sale> search(String q, Date from, Date to, int userId, int customerId, int limit) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT).append(" WHERE 1=1");
        List<Object> p = new ArrayList<>();
        if (q != null && !q.isBlank()) {
            sql.append(" AND (s.invoice_number LIKE ? OR c.customer_name LIKE ? OR u.full_name LIKE ? OR s.payment_method LIKE ?)");
            String like = "%" + q.trim() + "%";
            p.add(like); p.add(like); p.add(like); p.add(like);
        }
        if (from != null) { sql.append(" AND DATE(s.sale_date) >= ?"); p.add(from); }
        if (to != null)   { sql.append(" AND DATE(s.sale_date) <= ?"); p.add(to); }
        if (userId > 0)     { sql.append(" AND s.user_id = ?"); p.add(userId); }
        if (customerId > 0) { sql.append(" AND s.customer_id = ?"); p.add(customerId); }
        sql.append(" ORDER BY s.sale_date DESC, s.id DESC LIMIT ?");
        p.add(limit);
        List<Sale> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < p.size(); i++) ps.setObject(i + 1, p.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    // ---- aggregates for dashboard and reports -----------------------------------------------------

    public static final String BY_DAY = "DATE(sale_date)";
    public static final String BY_MONTH = "DATE_FORMAT(sale_date, '%Y-%m')";
    public static final String BY_YEAR = "YEAR(sale_date)";

    /** Grouped totals. periodExpr must be one of the BY_* constants. */
    public List<Map<String, Object>> groupedReport(String periodExpr, Date from, Date to) throws SQLException {
        String sql = "SELECT " + periodExpr + " AS period, COUNT(*) AS bills, SUM(subtotal) AS subtotal, SUM(discount) AS discount, " +
                     "SUM(gst) AS gst, SUM(grand_total) AS total FROM sales WHERE DATE(sale_date) BETWEEN ? AND ? " +
                     "GROUP BY period ORDER BY period";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, from);
            ps.setDate(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("period", rs.getString("period"));
                    row.put("bills", rs.getLong("bills"));
                    row.put("subtotal", rs.getBigDecimal("subtotal"));
                    row.put("discount", rs.getBigDecimal("discount"));
                    row.put("gst", rs.getBigDecimal("gst"));
                    row.put("total", rs.getBigDecimal("total"));
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    public List<Map<String, Object>> topSelling(Date from, Date to, int limit) throws SQLException {
        String sql = "SELECT m.medicine_name, SUM(si.quantity) AS qty, SUM(si.total) AS revenue " +
                     "FROM sale_items si JOIN sales s ON s.id = si.sale_id JOIN medicines m ON m.id = si.medicine_id " +
                     "WHERE DATE(s.sale_date) BETWEEN ? AND ? GROUP BY m.id, m.medicine_name ORDER BY qty DESC, revenue DESC LIMIT ?";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, from);
            ps.setDate(2, to);
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("name", rs.getString("medicine_name"));
                    row.put("qty", rs.getLong("qty"));
                    row.put("revenue", rs.getBigDecimal("revenue"));
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    private BigDecimal aggregate(String expr, Date from, Date to, int userId) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT " + expr + " FROM sales WHERE 1=1");
        List<Object> p = new ArrayList<>();
        if (from != null) { sql.append(" AND DATE(sale_date) >= ?"); p.add(from); }
        if (to != null)   { sql.append(" AND DATE(sale_date) <= ?"); p.add(to); }
        if (userId > 0)   { sql.append(" AND user_id = ?"); p.add(userId); }
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < p.size(); i++) ps.setObject(i + 1, p.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
        }
    }

    /** Sum of grand totals; null dates mean unbounded; userId 0 means all users. */
    public BigDecimal sumTotal(Date from, Date to, int userId) throws SQLException {
        return aggregate("COALESCE(SUM(grand_total), 0)", from, to, userId);
    }

    public int countBills(Date from, Date to, int userId) throws SQLException {
        return aggregate("COUNT(*)", from, to, userId).intValue();
    }
}

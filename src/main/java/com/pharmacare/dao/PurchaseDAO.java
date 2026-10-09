package com.pharmacare.dao;

import com.pharmacare.model.Purchase;
import com.pharmacare.model.PurchaseItem;
import com.pharmacare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAO {
    private static final String SELECT =
        "SELECT p.id, p.supplier_id, p.user_id, p.purchase_date, p.total_amount, s.supplier_name, u.full_name AS user_name " +
        "FROM purchases p JOIN suppliers s ON s.id = p.supplier_id JOIN users u ON u.id = p.user_id ";

    private Purchase map(ResultSet rs) throws SQLException {
        Purchase p = new Purchase();
        p.setId(rs.getInt("id"));
        p.setSupplierId(rs.getInt("supplier_id"));
        p.setUserId(rs.getInt("user_id"));
        p.setPurchaseDate(rs.getDate("purchase_date"));
        p.setTotalAmount(rs.getBigDecimal("total_amount"));
        p.setSupplierName(rs.getString("supplier_name"));
        p.setUserName(rs.getString("user_name"));
        return p;
    }

    /** Inserts the purchase and its items inside the caller's transaction. */
    public void insert(Connection c, Purchase p) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO purchases (supplier_id, user_id, purchase_date, total_amount) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getSupplierId());
            ps.setInt(2, p.getUserId());
            ps.setDate(3, p.getPurchaseDate());
            ps.setBigDecimal(4, p.getTotalAmount());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("No purchase id generated");
                p.setId(keys.getInt(1));
            }
        }
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO purchase_items (purchase_id, medicine_id, quantity, purchase_price, total) VALUES (?,?,?,?,?)")) {
            for (PurchaseItem it : p.getItems()) {
                ps.setInt(1, p.getId());
                ps.setInt(2, it.getMedicineId());
                ps.setInt(3, it.getQuantity());
                ps.setBigDecimal(4, it.getPurchasePrice());
                ps.setBigDecimal(5, it.getTotal());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public List<Purchase> list(Date from, Date to) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT).append(" WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (from != null) { sql.append(" AND p.purchase_date >= ?"); params.add(from); }
        if (to != null)   { sql.append(" AND p.purchase_date <= ?"); params.add(to); }
        sql.append(" ORDER BY p.purchase_date DESC, p.id DESC");
        List<Purchase> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public Purchase findById(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            Purchase p;
            try (PreparedStatement ps = c.prepareStatement(SELECT + " WHERE p.id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return null;
                    p = map(rs);
                }
            }
            String sql = "SELECT pi.id, pi.purchase_id, pi.medicine_id, pi.quantity, pi.purchase_price, pi.total, m.medicine_name " +
                         "FROM purchase_items pi JOIN medicines m ON m.id = pi.medicine_id WHERE pi.purchase_id = ? ORDER BY pi.id";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        PurchaseItem it = new PurchaseItem();
                        it.setId(rs.getInt("id"));
                        it.setPurchaseId(rs.getInt("purchase_id"));
                        it.setMedicineId(rs.getInt("medicine_id"));
                        it.setQuantity(rs.getInt("quantity"));
                        it.setPurchasePrice(rs.getBigDecimal("purchase_price"));
                        it.setTotal(rs.getBigDecimal("total"));
                        it.setMedicineName(rs.getString("medicine_name"));
                        p.getItems().add(it);
                    }
                }
            }
            return p;
        }
    }
}

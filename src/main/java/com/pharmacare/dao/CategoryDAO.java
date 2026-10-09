package com.pharmacare.dao;

import com.pharmacare.model.Category;
import com.pharmacare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private static final String SELECT =
        "SELECT c.id, c.category_name, c.description, c.created_at, " +
        "(SELECT COUNT(*) FROM medicines m WHERE m.category_id = c.id AND m.status='ACTIVE') AS med_count FROM categories c ";

    private Category map(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getInt("id"));
        c.setCategoryName(rs.getString("category_name"));
        c.setDescription(rs.getString("description"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setMedicineCount(rs.getInt("med_count"));
        return c;
    }

    public List<Category> findAll() throws SQLException {
        List<Category> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + "ORDER BY c.category_name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Category findById(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + "WHERE c.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public boolean nameExists(String name, int excludeId) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM categories WHERE category_name = ? AND id <> ?")) {
            ps.setString(1, name);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void create(Category cat) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO categories (category_name, description) VALUES (?,?)")) {
            ps.setString(1, cat.getCategoryName());
            ps.setString(2, cat.getDescription());
            ps.executeUpdate();
        }
    }

    public void update(Category cat) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE categories SET category_name=?, description=? WHERE id=?")) {
            ps.setString(1, cat.getCategoryName());
            ps.setString(2, cat.getDescription());
            ps.setInt(3, cat.getId());
            ps.executeUpdate();
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM categories WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}

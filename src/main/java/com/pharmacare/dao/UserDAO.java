package com.pharmacare.dao;

import com.pharmacare.model.User;
import com.pharmacare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final String COLS = "id, full_name, email, phone, username, password, role, status, created_at";

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        u.setStatus(rs.getString("status"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }

    public User findByLogin(String usernameOrEmail) throws SQLException {
        String sql = "SELECT " + COLS + " FROM users WHERE username = ? OR email = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usernameOrEmail);
            ps.setString(2, usernameOrEmail);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public User findByUsernameAndEmail(String username, String email) throws SQLException {
        String sql = "SELECT " + COLS + " FROM users WHERE username = ? AND email = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT " + COLS + " FROM users WHERE id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public boolean usernameExists(String username, int excludeId) throws SQLException {
        return exists("SELECT 1 FROM users WHERE username = ? AND id <> ?", username, excludeId);
    }

    public boolean emailExists(String email, int excludeId) throws SQLException {
        return exists("SELECT 1 FROM users WHERE email = ? AND id <> ?", email, excludeId);
    }

    private boolean exists(String sql, String value, int excludeId) throws SQLException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int create(User u) throws SQLException {
        String sql = "INSERT INTO users (full_name, email, phone, username, password, role, status) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPhone());
            ps.setString(4, u.getUsername());
            ps.setString(5, u.getPassword());
            ps.setString(6, u.getRole());
            ps.setString(7, u.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public boolean update(User u) throws SQLException {
        String sql = "UPDATE users SET full_name=?, email=?, phone=?, username=?, role=?, status=? WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPhone());
            ps.setString(4, u.getUsername());
            ps.setString(5, u.getRole());
            ps.setString(6, u.getStatus());
            ps.setInt(7, u.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePassword(int id, String hash) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE users SET password=? WHERE id=?")) {
            ps.setString(1, hash);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean setStatus(int id, String status) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE users SET status=? WHERE id=?")) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<User> search(String q) throws SQLException {
        String sql = "SELECT " + COLS + " FROM users WHERE full_name LIKE ? OR username LIKE ? OR email LIKE ? OR phone LIKE ? OR role LIKE ? ORDER BY id";
        List<User> list = new ArrayList<>();
        String like = "%" + q + "%";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) ps.setString(i, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public int count() throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM users");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}

package com.pharmacare.dao;

import com.pharmacare.model.Customer;
import com.pharmacare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private static final String COLS = "id, customer_name, phone, email, address, created_at";

    private Customer map(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt("id"));
        c.setCustomerName(rs.getString("customer_name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setAddress(rs.getString("address"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }

    public List<Customer> search(String q) throws SQLException {
        String sql = "SELECT " + COLS + " FROM customers WHERE customer_name LIKE ? OR phone LIKE ? OR email LIKE ? OR address LIKE ? ORDER BY customer_name";
        List<Customer> list = new ArrayList<>();
        String like = "%" + q + "%";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) ps.setString(i, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public Customer findById(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLS + " FROM customers WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public int create(Customer cu) throws SQLException {
        String sql = "INSERT INTO customers (customer_name, phone, email, address) VALUES (?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cu.getCustomerName());
            ps.setString(2, cu.getPhone());
            ps.setString(3, cu.getEmail());
            ps.setString(4, cu.getAddress());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public void update(Customer cu) throws SQLException {
        String sql = "UPDATE customers SET customer_name=?, phone=?, email=?, address=? WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cu.getCustomerName());
            ps.setString(2, cu.getPhone());
            ps.setString(3, cu.getEmail());
            ps.setString(4, cu.getAddress());
            ps.setInt(5, cu.getId());
            ps.executeUpdate();
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM customers WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int count() throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM customers");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}

package com.pharmacare.dao;

import com.pharmacare.model.Supplier;
import com.pharmacare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    private static final String COLS = "id, supplier_name, contact_person, phone, email, address, created_at";

    private Supplier map(ResultSet rs) throws SQLException {
        Supplier s = new Supplier();
        s.setId(rs.getInt("id"));
        s.setSupplierName(rs.getString("supplier_name"));
        s.setContactPerson(rs.getString("contact_person"));
        s.setPhone(rs.getString("phone"));
        s.setEmail(rs.getString("email"));
        s.setAddress(rs.getString("address"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }

    public List<Supplier> search(String q) throws SQLException {
        String sql = "SELECT " + COLS + " FROM suppliers WHERE supplier_name LIKE ? OR contact_person LIKE ? OR phone LIKE ? OR email LIKE ? OR address LIKE ? ORDER BY supplier_name";
        List<Supplier> list = new ArrayList<>();
        String like = "%" + q + "%";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) ps.setString(i, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<Supplier> findAll() throws SQLException {
        return search("");
    }

    public Supplier findById(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLS + " FROM suppliers WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public void create(Supplier s) throws SQLException {
        String sql = "INSERT INTO suppliers (supplier_name, contact_person, phone, email, address) VALUES (?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, s);
            ps.executeUpdate();
        }
    }

    public void update(Supplier s) throws SQLException {
        String sql = "UPDATE suppliers SET supplier_name=?, contact_person=?, phone=?, email=?, address=? WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, s);
            ps.setInt(6, s.getId());
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, Supplier s) throws SQLException {
        ps.setString(1, s.getSupplierName());
        ps.setString(2, s.getContactPerson());
        ps.setString(3, s.getPhone());
        ps.setString(4, s.getEmail());
        ps.setString(5, s.getAddress());
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM suppliers WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}

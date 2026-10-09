package com.pharmacare.dao;

import com.pharmacare.model.Medicine;
import com.pharmacare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MedicineDAO {
    private static final String FROM =
        " FROM medicines m JOIN categories c ON c.id = m.category_id JOIN suppliers s ON s.id = m.supplier_id ";
    private static final String SELECT = "SELECT m.*, c.category_name, s.supplier_name" + FROM;

    /** Whitelisted sort columns (user input is mapped to these, never concatenated). */
    private static final Map<String, String> SORTS = new LinkedHashMap<>();
    static {
        SORTS.put("id", "m.id");
        SORTS.put("name", "m.medicine_name");
        SORTS.put("category", "c.category_name");
        SORTS.put("expiry", "m.expiry_date");
        SORTS.put("price", "m.selling_price");
        SORTS.put("stock", "m.stock_quantity");
    }

    private Medicine map(ResultSet rs) throws SQLException {
        Medicine m = new Medicine();
        m.setId(rs.getInt("id"));
        m.setMedicineName(rs.getString("medicine_name"));
        m.setGenericName(rs.getString("generic_name"));
        m.setCategoryId(rs.getInt("category_id"));
        m.setSupplierId(rs.getInt("supplier_id"));
        m.setBatchNumber(rs.getString("batch_number"));
        m.setManufactureDate(rs.getDate("manufacture_date"));
        m.setExpiryDate(rs.getDate("expiry_date"));
        m.setPurchasePrice(rs.getBigDecimal("purchase_price"));
        m.setSellingPrice(rs.getBigDecimal("selling_price"));
        m.setStockQuantity(rs.getInt("stock_quantity"));
        m.setReorderLevel(rs.getInt("reorder_level"));
        m.setDescription(rs.getString("description"));
        m.setStatus(rs.getString("status"));
        m.setCreatedAt(rs.getTimestamp("created_at"));
        m.setCategoryName(rs.getString("category_name"));
        m.setSupplierName(rs.getString("supplier_name"));
        return m;
    }

    private void bind(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
    }

    private String where(List<Object> p, String q, int catId, int supId, String status) {
        StringBuilder w = new StringBuilder(" WHERE m.status = 'ACTIVE'");
        if (q != null && !q.isBlank()) {
            w.append(" AND (m.medicine_name LIKE ? OR m.generic_name LIKE ? OR m.batch_number LIKE ?)");
            String like = "%" + q.trim() + "%";
            p.add(like); p.add(like); p.add(like);
        }
        if (catId > 0) { w.append(" AND m.category_id = ?"); p.add(catId); }
        if (supId > 0) { w.append(" AND m.supplier_id = ?"); p.add(supId); }
        if (status != null) {
            switch (status) {
                case "AVAILABLE": w.append(" AND m.expiry_date >= CURDATE() AND m.stock_quantity > m.reorder_level"); break;
                case "LOW":       w.append(" AND m.expiry_date >= CURDATE() AND m.stock_quantity > 0 AND m.stock_quantity <= m.reorder_level"); break;
                case "OUT":       w.append(" AND m.expiry_date >= CURDATE() AND m.stock_quantity = 0"); break;
                case "EXPIRED":   w.append(" AND m.expiry_date < CURDATE()"); break;
                case "NEAR":      w.append(" AND m.expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 90 DAY)"); break;
                default: break;
            }
        }
        return w.toString();
    }

    public List<Medicine> search(String q, int catId, int supId, String status, String sort, String dir,
                                 int offset, int limit) throws SQLException {
        List<Object> params = new ArrayList<>();
        String w = where(params, q, catId, supId, status);
        String col = SORTS.getOrDefault(sort, "m.id");
        String direction = "desc".equalsIgnoreCase(dir) ? "DESC" : "ASC";
        String sql = SELECT + w + " ORDER BY " + col + " " + direction + " LIMIT ? OFFSET ?";
        params.add(limit);
        params.add(offset);
        List<Medicine> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public int count(String q, int catId, int supId, String status) throws SQLException {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT COUNT(*)" + FROM + where(params, q, catId, supId, status);
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public Medicine findById(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + " WHERE m.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** Row-locks the medicine inside the caller's transaction. */
    public Medicine lockById(Connection c, int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SELECT + " WHERE m.id = ? FOR UPDATE OF m")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** Medicines that can be sold right now (active, in stock, not expired). */
    public List<Medicine> searchAvailable(String q, int limit) throws SQLException {
        String sql = SELECT + " WHERE m.status='ACTIVE' AND m.stock_quantity > 0 AND m.expiry_date >= CURDATE() " +
                     "AND (m.medicine_name LIKE ? OR m.generic_name LIKE ? OR m.batch_number LIKE ?) " +
                     "ORDER BY m.medicine_name LIMIT ?";
        String like = "%" + (q == null ? "" : q.trim()) + "%";
        List<Medicine> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setInt(4, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public boolean batchExists(String name, String batch, int excludeId) throws SQLException {
        String sql = "SELECT 1 FROM medicines WHERE medicine_name = ? AND batch_number = ? AND id <> ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, batch);
            ps.setInt(3, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void create(Medicine m) throws SQLException {
        String sql = "INSERT INTO medicines (medicine_name, generic_name, category_id, supplier_id, batch_number, " +
                     "manufacture_date, expiry_date, purchase_price, selling_price, stock_quantity, reorder_level, description) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bindMedicine(ps, m);
            ps.executeUpdate();
        }
    }

    public void update(Medicine m) throws SQLException {
        String sql = "UPDATE medicines SET medicine_name=?, generic_name=?, category_id=?, supplier_id=?, batch_number=?, " +
                     "manufacture_date=?, expiry_date=?, purchase_price=?, selling_price=?, stock_quantity=?, reorder_level=?, " +
                     "description=? WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bindMedicine(ps, m);
            ps.setInt(13, m.getId());
            ps.executeUpdate();
        }
    }

    private void bindMedicine(PreparedStatement ps, Medicine m) throws SQLException {
        ps.setString(1, m.getMedicineName());
        ps.setString(2, m.getGenericName());
        ps.setInt(3, m.getCategoryId());
        ps.setInt(4, m.getSupplierId());
        ps.setString(5, m.getBatchNumber());
        ps.setDate(6, m.getManufactureDate());
        ps.setDate(7, m.getExpiryDate());
        ps.setBigDecimal(8, m.getPurchasePrice());
        ps.setBigDecimal(9, m.getSellingPrice());
        ps.setInt(10, m.getStockQuantity());
        ps.setInt(11, m.getReorderLevel());
        ps.setString(12, m.getDescription());
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM medicines WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Soft delete used when a medicine already appears on bills/purchases. */
    public void deactivate(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE medicines SET status='INACTIVE' WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Atomically reduces stock; returns false when there is not enough stock (never goes below 0). */
    public boolean decreaseStock(Connection c, int id, int qty) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE medicines SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?")) {
            ps.setInt(1, qty);
            ps.setInt(2, id);
            ps.setInt(3, qty);
            return ps.executeUpdate() > 0;
        }
    }

    public void increaseStock(Connection c, int id, int qty, java.math.BigDecimal newPurchasePrice) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE medicines SET stock_quantity = stock_quantity + ?, purchase_price = ? WHERE id = ?")) {
            ps.setInt(1, qty);
            ps.setBigDecimal(2, newPurchasePrice);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    // ---- report / dashboard helpers (conditions are internal constants) -------------------------

    private List<Medicine> list(String cond, String order) throws SQLException {
        List<Medicine> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + " WHERE m.status='ACTIVE' AND " + cond + " ORDER BY " + order);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    private int countWhere(String cond) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM medicines m WHERE m.status='ACTIVE' AND " + cond);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private static final String LOW = "m.stock_quantity <= m.reorder_level AND m.expiry_date >= CURDATE()";
    private static final String EXPIRED = "m.expiry_date < CURDATE()";
    private static final String NEAR = "m.expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 90 DAY)";

    public List<Medicine> findLowStock()   throws SQLException { return list(LOW, "m.stock_quantity ASC, m.medicine_name"); }
    public List<Medicine> findExpired()    throws SQLException { return list(EXPIRED, "m.expiry_date ASC"); }
    public List<Medicine> findNearExpiry() throws SQLException { return list(NEAR, "m.expiry_date ASC"); }
    public int countAll()        throws SQLException { return countWhere("1=1"); }
    public int countLowStock()   throws SQLException { return countWhere(LOW); }
    public int countExpired()    throws SQLException { return countWhere(EXPIRED); }
    public int countNearExpiry() throws SQLException { return countWhere(NEAR); }
}

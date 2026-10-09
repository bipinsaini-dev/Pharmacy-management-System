package com.pharmacare.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

public class Medicine {
    private int id;
    private String medicineName;
    private String genericName;
    private int categoryId;
    private int supplierId;
    private String batchNumber;
    private Date manufactureDate;
    private Date expiryDate;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private int stockQuantity;
    private int reorderLevel;
    private String description;
    private String status;
    private Timestamp createdAt;
    private String categoryName;
    private String supplierName;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public Date getManufactureDate() { return manufactureDate; }
    public void setManufactureDate(Date manufactureDate) { this.manufactureDate = manufactureDate; }
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public static final int NEAR_EXPIRY_DAYS = 90;

    public boolean isExpired() {
        return expiryDate != null && expiryDate.toLocalDate().isBefore(LocalDate.now());
    }

    public boolean isNearExpiry() {
        if (expiryDate == null || isExpired()) return false;
        return !expiryDate.toLocalDate().isAfter(LocalDate.now().plusDays(NEAR_EXPIRY_DAYS));
    }

    /** Computed status shown in the medicine list: AVAILABLE / LOW STOCK / OUT OF STOCK / EXPIRED. */
    public String getStockStatus() {
        if (isExpired()) return "EXPIRED";
        if (stockQuantity <= 0) return "OUT OF STOCK";
        if (stockQuantity <= reorderLevel) return "LOW STOCK";
        return "AVAILABLE";
    }

    public String getBadgeClass() {
        switch (getStockStatus()) {
            case "EXPIRED":      return "bg-dark";
            case "OUT OF STOCK": return "bg-danger";
            case "LOW STOCK":    return "bg-warning text-dark";
            default:             return "bg-success";
        }
    }
}

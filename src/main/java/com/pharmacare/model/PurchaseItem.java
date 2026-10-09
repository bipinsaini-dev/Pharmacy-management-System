package com.pharmacare.model;

import java.math.BigDecimal;

public class PurchaseItem {
    private int id;
    private int purchaseId;
    private int medicineId;
    private int quantity;
    private BigDecimal purchasePrice;
    private BigDecimal total;
    private String medicineName;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPurchaseId() { return purchaseId; }
    public void setPurchaseId(int purchaseId) { this.purchaseId = purchaseId; }
    public int getMedicineId() { return medicineId; }
    public void setMedicineId(int medicineId) { this.medicineId = medicineId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
}

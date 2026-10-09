package com.pharmacare.model;

import java.sql.Timestamp;
 

public class Category {
    private int id;
    private String categoryName;
    private String description;
    private Timestamp createdAt;
    private int medicineCount;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public int getMedicineCount() { return medicineCount; }
    public void setMedicineCount(int medicineCount) { this.medicineCount = medicineCount; }
}

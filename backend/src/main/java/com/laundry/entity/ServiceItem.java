package com.laundry.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service Item Entity
 */
public class ServiceItem {
    private Long id;
    private String name;
    private String category; // wash, dry_clean, iron, repair
    private BigDecimal price;
    private String unit; // piece, kg, set
    private String description;
    private Integer status; // 0: Disabled, 1: Active
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public String getCategoryName() {
        if (category == null) return "";
        switch (category) {
            case "wash": return "Washing";
            case "dry_clean": return "Dry Cleaning";
            case "iron": return "Ironing";
            case "repair": return "Repair";
            default: return category;
        }
    }

    public String getStatusName() {
        return status == 1 ? "Active" : "Disabled";
    }
}

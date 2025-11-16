package sothcheat.models;

import java.time.LocalDateTime;

public class Category {
    private int categoryId;
    private int userId;
    private String categoryName;
    private String type; // "income" or "expense"
    private LocalDateTime createdAt;

    // Default constructor
    public Category() {
    }

    public Category(int userId, String categoryName, String type) {
        this.userId = userId;
        this.categoryName = categoryName;
        this.type = type;
    }

    public Category(int categoryId, int userId, String categoryName, String type) {
        this.categoryId = categoryId;
        this.userId = userId;
        this.categoryName = categoryName;
        this.type = type;
    }

    // Getters and Setters
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public boolean isExpense() {
        return "expense".equalsIgnoreCase(type);
    }

    public boolean isIncome() {
        return "income".equalsIgnoreCase(type);
    }

    @Override
    public String toString() {
        return categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return categoryId == category.categoryId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(categoryId);
    }
}
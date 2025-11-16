package sothcheat.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transaction {
    private int transactionId;
    private int userId;
    private int categoryId;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private String type;
    private LocalDateTime createdAt;
    private String categoryName;

    public Transaction() {
    }

    public Transaction(int userId, int categoryId, String description,
                       BigDecimal amount, LocalDate date, String type) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.type = type;
    }

    public Transaction(int transactionId, int userId, int categoryId, String description,
                       BigDecimal amount, LocalDate date, String type) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.type = type;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isExpense() {
        return "expense".equalsIgnoreCase(type);
    }

    public boolean isIncome() {
        return "income".equalsIgnoreCase(type);
    }

    public String getFormattedAmount() {
        return "$" + amount.toString();
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionId == that.transactionId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(transactionId);
    }
}
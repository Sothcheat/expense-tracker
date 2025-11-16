package sothcheat.services;

import sothcheat.database.DBConnection;
import sothcheat.models.Transaction;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {

    public List<Transaction> getRecentTransactionsByUser(int userId, int limit) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, c.category_name FROM transactions t " +
                "LEFT JOIN categories c ON t.category_id = c.category_id " +
                "WHERE t.user_id = ? ORDER BY t.date DESC, t.created_at DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = mapResultSetToTransaction(rs);
                transactions.add(transaction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return transactions;
    }

    // Get all transactions by user and type (expense or income)
    public List<Transaction> getTransactionsByUserAndType(int userId, String type) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, c.category_name FROM transactions t " +
                "LEFT JOIN categories c ON t.category_id = c.category_id " +
                "WHERE t.user_id = ? AND t.type = ? ORDER BY t.date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, type);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = mapResultSetToTransaction(rs);
                transactions.add(transaction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return transactions;
    }

    // Add new transaction
    public boolean addTransaction(int userId, int categoryId, String description,
                                  BigDecimal amount, LocalDate date, String type) {
        String sql = "INSERT INTO transactions(user_id, category_id, description, amount, date, type) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);
            stmt.setString(3, description);
            stmt.setBigDecimal(4, amount);
            stmt.setDate(5, java.sql.Date.valueOf(date));
            stmt.setString(6, type);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Update existing transaction
    public boolean updateTransaction(int transactionId, int categoryId, String description,
                                     BigDecimal amount, LocalDate date) {
        String sql = "UPDATE transactions SET category_id = ?, description = ?, " +
                "amount = ?, date = ? WHERE transaction_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            stmt.setString(2, description);
            stmt.setBigDecimal(3, amount);
            stmt.setDate(4, java.sql.Date.valueOf(date));
            stmt.setInt(5, transactionId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Delete transaction
    public boolean deleteTransaction(int transactionId) {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transactionId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public BigDecimal getTotalExpense(int userId, YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM transactions " +
                "WHERE user_id = ? AND type = 'expense' AND date BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setDate(2, java.sql.Date.valueOf(startDate));
            stmt.setDate(3, java.sql.Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalIncome(int userId, YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM transactions " +
                "WHERE user_id = ? AND type = 'income' AND date BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setDate(2, java.sql.Date.valueOf(startDate));
            stmt.setDate(3, java.sql.Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getBalance(int userId, YearMonth month) {
        BigDecimal income = getTotalIncome(userId, month);
        BigDecimal expense = getTotalExpense(userId, month);
        return income.subtract(expense);
    }

    // Helper method to map ResultSet to Transaction object
    private Transaction mapResultSetToTransaction(ResultSet rs) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setUserId(rs.getInt("user_id"));
        transaction.setCategoryId(rs.getInt("category_id"));
        transaction.setDescription(rs.getString("description"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setDate(rs.getDate("date").toLocalDate());
        transaction.setType(rs.getString("type"));
        transaction.setCategoryName(rs.getString("category_name"));
        return transaction;
    }
}
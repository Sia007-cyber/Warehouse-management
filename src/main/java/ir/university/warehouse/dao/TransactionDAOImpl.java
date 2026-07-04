package ir.university.warehouse.dao;

import ir.university.warehouse.db.DatabaseConnection;
import ir.university.warehouse.model.Transaction;
import ir.university.warehouse.model.TransactionType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDAOImpl implements TransactionDAO {

    @Override
    public Transaction insert(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO Transactions (permission_id, type, amount, transaction_date) VALUES (?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, transaction.getPermissionId());
            stmt.setString(2, transaction.getType().name());
            stmt.setDouble(3, transaction.getAmount());
            stmt.setString(4, transaction.getTransactionDate());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    transaction.setTransactionId(keys.getInt(1));
                }
            }
        }
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(int transactionId) throws SQLException {
        String sql = "SELECT * FROM Transactions WHERE transaction_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transactionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> findAll() throws SQLException {
        String sql = "SELECT * FROM Transactions";
        Connection conn = DatabaseConnection.getConnection();
        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                transactions.add(mapRow(rs));
            }
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByDateRange(String startDate, String endDate) throws SQLException {
        String sql = "SELECT * FROM Transactions WHERE transaction_date BETWEEN ? AND ?";
        Connection conn = DatabaseConnection.getConnection();
        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRow(rs));
                }
            }
        }
        return transactions;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setPermissionId(rs.getInt("permission_id"));
        transaction.setType(TransactionType.valueOf(rs.getString("type")));
        transaction.setAmount(rs.getDouble("amount"));
        transaction.setTransactionDate(rs.getString("transaction_date"));
        return transaction;
    }
}
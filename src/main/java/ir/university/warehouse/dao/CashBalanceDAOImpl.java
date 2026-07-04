package ir.university.warehouse.dao;

import ir.university.warehouse.db.DatabaseConnection;
import ir.university.warehouse.model.CashBalance;
import java.sql.*;

public class CashBalanceDAOImpl implements CashBalanceDAO {

    @Override
    public CashBalance get() throws SQLException {
        String sql = "SELECT * FROM CashBalance WHERE id = 1";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                CashBalance cb = new CashBalance();
                cb.setId(rs.getInt("id"));
                cb.setBalance(rs.getDouble("balance"));
                return cb;
            }
        }
        String insertSql = "INSERT INTO CashBalance (id, balance) VALUES (1, 0)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.executeUpdate();
        }
        CashBalance cb = new CashBalance();
        cb.setId(1);
        cb.setBalance(0);
        return cb;
    }

    @Override
    public void update(double newBalance) throws SQLException {
        String sql = "UPDATE CashBalance SET balance = ? WHERE id = 1";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);
            stmt.executeUpdate();
        }
    }
}
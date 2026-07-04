package ir.university.warehouse.dao;

import ir.university.warehouse.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseAllowedCategoriesDAOImpl implements WarehouseAllowedCategoriesDAO {

    @Override
    public void allow(int warehouseId, int categoryId) throws SQLException {
        String sql = "INSERT OR IGNORE INTO WarehouseAllowedCategories (warehouse_id, category_id) VALUES (?, ?)";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, warehouseId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        }
    }

    @Override
    public void disallow(int warehouseId, int categoryId) throws SQLException {
        String sql = "DELETE FROM WarehouseAllowedCategories WHERE warehouse_id = ? AND category_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, warehouseId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        }
    }

    @Override
    public boolean isAllowed(int warehouseId, int categoryId) throws SQLException {
        String sql = "SELECT 1 FROM WarehouseAllowedCategories WHERE warehouse_id = ? AND category_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, warehouseId);
            stmt.setInt(2, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public List<Integer> findAllowedCategoryIds(int warehouseId) throws SQLException {
        String sql = "SELECT category_id FROM WarehouseAllowedCategories WHERE warehouse_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        List<Integer> ids = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, warehouseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("category_id"));
                }
            }
        }
        return ids;
    }
}
package ir.university.warehouse.dao;

import ir.university.warehouse.db.DatabaseConnection;
import ir.university.warehouse.model.InventoryRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InventoryDAOImpl implements InventoryDAO {

    @Override
    public void upsert(InventoryRecord record) throws SQLException {
        String sql = "INSERT INTO Inventory (warehouse_id, item_id, real_stock) VALUES (?, ?, ?) " +
                     "ON CONFLICT(warehouse_id, item_id) DO UPDATE SET real_stock = excluded.real_stock";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, record.getWarehouseId());
            stmt.setInt(2, record.getItemId());
            stmt.setInt(3, record.getRealStock());
            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<InventoryRecord> find(int warehouseId, int itemId) throws SQLException {
        String sql = "SELECT * FROM Inventory WHERE warehouse_id = ? AND item_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, warehouseId);
            stmt.setInt(2, itemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<InventoryRecord> findByWarehouse(int warehouseId) throws SQLException {
        String sql = "SELECT * FROM Inventory WHERE warehouse_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        List<InventoryRecord> records = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, warehouseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapRow(rs));
                }
            }
        }
        return records;
    }

    @Override
    public List<InventoryRecord> findAll() throws SQLException {
        String sql = "SELECT * FROM Inventory";
        Connection conn = DatabaseConnection.getConnection();
        List<InventoryRecord> records = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                records.add(mapRow(rs));
            }
        }
        return records;
    }

    private InventoryRecord mapRow(ResultSet rs) throws SQLException {
        InventoryRecord record = new InventoryRecord();
        record.setWarehouseId(rs.getInt("warehouse_id"));
        record.setItemId(rs.getInt("item_id"));
        record.setRealStock(rs.getInt("real_stock"));
        return record;
    }
}
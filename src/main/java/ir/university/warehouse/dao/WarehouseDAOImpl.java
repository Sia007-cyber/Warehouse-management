package ir.university.warehouse.dao;

import ir.university.warehouse.db.DatabaseConnection;
import ir.university.warehouse.model.Warehouse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WarehouseDAOImpl implements WarehouseDAO {

    @Override
    public Warehouse insert(Warehouse warehouse) throws SQLException {
        String sql = "INSERT INTO Warehouses (name, address, capacity) VALUES (?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, warehouse.getName());
            stmt.setString(2, warehouse.getAddress());
            stmt.setInt(3, warehouse.getCapacity());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    warehouse.setWarehouseId(generatedKeys.getInt(1));
                }
            }
        }
        return warehouse;
    }

    @Override
    public Optional<Warehouse> findById(int warehouseId) throws SQLException {
        String sql = "SELECT * FROM Warehouses WHERE warehouse_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, warehouseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Warehouse> findAll() throws SQLException {
        String sql = "SELECT * FROM Warehouses";
        Connection conn = DatabaseConnection.getConnection();
        List<Warehouse> warehouses = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                warehouses.add(mapRow(rs));
            }
        }
        return warehouses;
    }

    @Override
    public void update(Warehouse warehouse) throws SQLException {
        String sql = "UPDATE Warehouses SET name = ?, address = ?, capacity = ? WHERE warehouse_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, warehouse.getName());
            stmt.setString(2, warehouse.getAddress());
            stmt.setInt(3, warehouse.getCapacity());
            stmt.setInt(4, warehouse.getWarehouseId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int warehouseId) throws SQLException {
        String sql = "DELETE FROM Warehouses WHERE warehouse_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, warehouseId);
            stmt.executeUpdate();
        }
    }

    private Warehouse mapRow(ResultSet rs) throws SQLException {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseId(rs.getInt("warehouse_id"));
        warehouse.setName(rs.getString("name"));
        warehouse.setAddress(rs.getString("address"));
        warehouse.setCapacity(rs.getInt("capacity"));
        return warehouse;
    }
}
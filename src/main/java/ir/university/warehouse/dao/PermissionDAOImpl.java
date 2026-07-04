package ir.university.warehouse.dao;

import ir.university.warehouse.db.DatabaseConnection;
import ir.university.warehouse.model.Permission;
import ir.university.warehouse.model.PermissionStatus;
import ir.university.warehouse.model.PermissionType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PermissionDAOImpl implements PermissionDAO {

    @Override
    public Permission insert(Permission permission) throws SQLException {
        String sql = "INSERT INTO Permissions " +
                "(type, warehouse_id, item_id, quantity, unit_price, title, description, status, permission_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, permission.getType().name());
            stmt.setInt(2, permission.getWarehouseId());
            stmt.setInt(3, permission.getItemId());
            stmt.setInt(4, permission.getQuantity());
            stmt.setDouble(5, permission.getUnitPrice());
            stmt.setString(6, permission.getTitle());
            stmt.setString(7, permission.getDescription());
            stmt.setString(8, permission.getStatus().name());
            stmt.setString(9, permission.getPermissionDate());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    permission.setPermissionId(keys.getInt(1));
                }
            }
        }
        return permission;
    }

    @Override
    public Optional<Permission> findById(int permissionId) throws SQLException {
        String sql = "SELECT * FROM Permissions WHERE permission_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, permissionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Permission> findAll() throws SQLException {
        String sql = "SELECT * FROM Permissions";
        Connection conn = DatabaseConnection.getConnection();
        List<Permission> permissions = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                permissions.add(mapRow(rs));
            }
        }
        return permissions;
    }
    @Override
    public List<Permission> findByStatus(PermissionStatus status) throws SQLException {
        String sql = "SELECT * FROM Permissions WHERE status = ?";
        Connection conn = DatabaseConnection.getConnection();
        List<Permission> permissions = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissions.add(mapRow(rs));
                }
            }
        }
        return permissions;
    }
    @Override
    public List<Permission> findByWarehouseAndItem(int warehouseId, int itemId, PermissionType type, PermissionStatus status) throws SQLException {
        String sql = "SELECT * FROM Permissions WHERE warehouse_id = ? AND item_id = ? AND type = ? AND status = ?";
        Connection conn = DatabaseConnection.getConnection();
        List<Permission> permissions = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, warehouseId);
            stmt.setInt(2, itemId);
            stmt.setString(3, type.name());
            stmt.setString(4, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissions.add(mapRow(rs));
                }
            }
        }
        return permissions;
    }
    @Override
    public void updateStatus(int permissionId, PermissionStatus newStatus) throws SQLException {
        String sql = "UPDATE Permissions SET status = ? WHERE permission_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus.name());
            stmt.setInt(2, permissionId);
            stmt.executeUpdate();
        }
    }
    @Override
    public void delete(int permissionId) throws SQLException {
        String sql = "DELETE FROM Permissions WHERE permission_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, permissionId);
            stmt.executeUpdate();
        }
    }
    private Permission mapRow(ResultSet rs) throws SQLException {
        Permission permission = new Permission();
        permission.setPermissionId(rs.getInt("permission_id"));
        permission.setType(PermissionType.valueOf(rs.getString("type")));
        permission.setWarehouseId(rs.getInt("warehouse_id"));
        permission.setItemId(rs.getInt("item_id"));
        permission.setQuantity(rs.getInt("quantity"));
        permission.setUnitPrice(rs.getDouble("unit_price"));
        permission.setTitle(rs.getString("title"));
        permission.setDescription(rs.getString("description"));
        permission.setStatus(PermissionStatus.valueOf(rs.getString("status")));
        permission.setPermissionDate(rs.getString("permission_date"));
        return permission;
    }

    @Override
    public List<Permission> findByWarehouse(int warehouseId, PermissionType type, PermissionStatus status) throws SQLException {
    String sql = "SELECT * FROM Permissions WHERE warehouse_id = ? AND type = ? AND status = ?";
    Connection conn = DatabaseConnection.getConnection();
    List<Permission> permissions = new ArrayList<>();

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, warehouseId);
        stmt.setString(2, type.name());
        stmt.setString(3, status.name());
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                permissions.add(mapRow(rs));
            }
        }
    }
    return permissions;
}
}
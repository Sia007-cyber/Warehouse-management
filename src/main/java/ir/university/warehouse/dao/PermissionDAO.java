package ir.university.warehouse.dao;

import ir.university.warehouse.model.Permission;
import ir.university.warehouse.model.PermissionStatus;
import ir.university.warehouse.model.PermissionType;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PermissionDAO {

    Permission insert(Permission permission) throws SQLException;

    Optional<Permission> findById(int permissionId) throws SQLException;

    List<Permission> findAll() throws SQLException;

    List<Permission> findByStatus(PermissionStatus status) throws SQLException;

    List<Permission> findByWarehouseAndItem(int warehouseId, int itemId, PermissionType type, PermissionStatus status) throws SQLException;

    List<Permission> findByWarehouse(int warehouseId, PermissionType type, PermissionStatus status) throws SQLException;

    void updateStatus(int permissionId, PermissionStatus newStatus) throws SQLException;

    void delete(int permissionId) throws SQLException;
}
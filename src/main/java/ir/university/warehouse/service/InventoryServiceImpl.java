package ir.university.warehouse.service;

import ir.university.warehouse.dao.InventoryDAO;
import ir.university.warehouse.dao.PermissionDAO;
import ir.university.warehouse.dao.WarehouseDAO;
import ir.university.warehouse.model.InventoryRecord;
import ir.university.warehouse.model.Permission;
import ir.university.warehouse.model.PermissionStatus;
import ir.university.warehouse.model.PermissionType;
import ir.university.warehouse.model.Warehouse;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class InventoryServiceImpl implements InventoryService {

    private final InventoryDAO inventoryDAO;
    private final PermissionDAO permissionDAO;
    private final WarehouseDAO warehouseDAO;

    public InventoryServiceImpl(InventoryDAO inventoryDAO, PermissionDAO permissionDAO, WarehouseDAO warehouseDAO) {
        this.inventoryDAO = inventoryDAO;
        this.permissionDAO = permissionDAO;
        this.warehouseDAO = warehouseDAO;
    }

    @Override
    public int getRealStock(int warehouseId, int itemId) throws SQLException {
        Optional<InventoryRecord> record = inventoryDAO.find(warehouseId, itemId);
        return record.map(InventoryRecord::getRealStock).orElse(0);
    }

    @Override
    public int getIncomingStock(int warehouseId, int itemId) throws SQLException {
        return sumQuantity(warehouseId, itemId, PermissionType.IN, PermissionStatus.ISSUED);
    }

    @Override
    public int getReservedStock(int warehouseId, int itemId) throws SQLException {
        return sumQuantity(warehouseId, itemId, PermissionType.OUT, PermissionStatus.ISSUED);
    }

    @Override
    public int getAvailableForExit(int warehouseId, int itemId) throws SQLException {
        int realStock = getRealStock(warehouseId, itemId);
        int reserved = getReservedStock(warehouseId, itemId);
        return realStock - reserved;
    }

    @Override
    public boolean hasCapacityFor(int warehouseId, int itemId, int additionalQuantity) throws SQLException {
        Optional<Warehouse> warehouseOpt = warehouseDAO.findById(warehouseId);
        if (warehouseOpt.isEmpty()) {
            return false;
        }
        int capacity = warehouseOpt.get().getCapacity();

        int totalRealStock = 0;
        for (InventoryRecord record : inventoryDAO.findByWarehouse(warehouseId)) {
            totalRealStock += record.getRealStock();
        }

        int totalIncoming = 0;
        for (Permission p : permissionDAO.findByWarehouse(warehouseId, PermissionType.IN, PermissionStatus.ISSUED)) {
            totalIncoming += p.getQuantity();
        }

        return (totalRealStock + totalIncoming + additionalQuantity) <= capacity;
    }

    private int sumQuantity(int warehouseId, int itemId, PermissionType type, PermissionStatus status) throws SQLException {
        List<Permission> permissions = permissionDAO.findByWarehouseAndItem(warehouseId, itemId, type, status);
        int sum = 0;
        for (Permission p : permissions) {
            sum += p.getQuantity();
        }
        return sum;
    }
}
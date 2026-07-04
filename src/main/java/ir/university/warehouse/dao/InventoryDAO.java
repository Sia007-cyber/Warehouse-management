package ir.university.warehouse.dao;

import ir.university.warehouse.model.InventoryRecord;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface InventoryDAO {
    void upsert(InventoryRecord record) throws SQLException;
    Optional<InventoryRecord> find(int warehouseId, int itemId) throws SQLException;
    List<InventoryRecord> findByWarehouse(int warehouseId) throws SQLException;
    List<InventoryRecord> findAll() throws SQLException;
}
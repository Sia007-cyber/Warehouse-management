package ir.university.warehouse.dao;

import ir.university.warehouse.model.Warehouse;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface WarehouseDAO {

    Warehouse insert(Warehouse warehouse) throws SQLException;

    Optional<Warehouse> findById(int warehouseId) throws SQLException;

    List<Warehouse> findAll() throws SQLException;

    void update(Warehouse warehouse) throws SQLException;

    void delete(int warehouseId) throws SQLException;
}
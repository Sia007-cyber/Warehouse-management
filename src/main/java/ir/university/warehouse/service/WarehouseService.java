package ir.university.warehouse.service;

import ir.university.warehouse.exception.EntityNotFoundException;
import ir.university.warehouse.exception.ValidationException;
import ir.university.warehouse.model.Warehouse;

import java.sql.SQLException;
import java.util.List;

public interface WarehouseService {

    Warehouse createWarehouse(String name, String address, int capacity)
            throws SQLException, ValidationException;

    Warehouse getWarehouseById(int warehouseId)
            throws SQLException, EntityNotFoundException;

    List<Warehouse> getAllWarehouses() throws SQLException;

    Warehouse updateWarehouse(int warehouseId, String name, String address, int capacity)
            throws SQLException, ValidationException, EntityNotFoundException;

    void deleteWarehouse(int warehouseId) throws SQLException, EntityNotFoundException;
}
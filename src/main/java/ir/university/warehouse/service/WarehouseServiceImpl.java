package ir.university.warehouse.service;

import ir.university.warehouse.dao.CategoryDAO;
import ir.university.warehouse.dao.WarehouseAllowedCategoriesDAO;
import ir.university.warehouse.dao.WarehouseDAO;
import ir.university.warehouse.exception.EntityNotFoundException;
import ir.university.warehouse.exception.ValidationException;
import ir.university.warehouse.model.Warehouse;

import java.sql.SQLException;
import java.util.List;

public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseDAO warehouseDAO;
    private final WarehouseAllowedCategoriesDAO allowedCategoriesDAO;
    private final CategoryDAO categoryDAO;

    public WarehouseServiceImpl(WarehouseDAO warehouseDAO,
                                 WarehouseAllowedCategoriesDAO allowedCategoriesDAO,
                                 CategoryDAO categoryDAO) {
        this.warehouseDAO = warehouseDAO;
        this.allowedCategoriesDAO = allowedCategoriesDAO;
        this.categoryDAO = categoryDAO;
    }

    @Override
    public Warehouse createWarehouse(String name, String address, int capacity)
            throws SQLException, ValidationException {
        validate(name, capacity);

        Warehouse warehouse = new Warehouse();
        warehouse.setName(name.trim());
        warehouse.setAddress(address);
        warehouse.setCapacity(capacity);
        return warehouseDAO.insert(warehouse);
    }

    @Override
    public Warehouse getWarehouseById(int warehouseId) throws SQLException, EntityNotFoundException {
        return warehouseDAO.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("انباری با شناسه " + warehouseId + " یافت نشد."));
    }

    @Override
    public List<Warehouse> getAllWarehouses() throws SQLException {
        return warehouseDAO.findAll();
    }

    @Override
    public Warehouse updateWarehouse(int warehouseId, String name, String address, int capacity)
            throws SQLException, ValidationException, EntityNotFoundException {
        validate(name, capacity);

        Warehouse existing = getWarehouseById(warehouseId);
        existing.setName(name.trim());
        existing.setAddress(address);
        existing.setCapacity(capacity);
        warehouseDAO.update(existing);
        return existing;
    }

    @Override
    public void deleteWarehouse(int warehouseId) throws SQLException, EntityNotFoundException {
        getWarehouseById(warehouseId); // اطمینان از وجود قبل از حذف
        warehouseDAO.delete(warehouseId);
    }

    @Override
    public void allowCategory(int warehouseId, int categoryId) throws SQLException, EntityNotFoundException {
        getWarehouseById(warehouseId); // اطمینان از وجود انبار
        if (categoryDAO.findById(categoryId).isEmpty()) {
            throw new EntityNotFoundException("دسته‌بندی با شناسه " + categoryId + " یافت نشد.");
        }
        allowedCategoriesDAO.allow(warehouseId, categoryId);
    }

    @Override
    public void disallowCategory(int warehouseId, int categoryId) throws SQLException {
        allowedCategoriesDAO.disallow(warehouseId, categoryId);
    }

    @Override
    public List<Integer> getAllowedCategoryIds(int warehouseId) throws SQLException {
        return allowedCategoriesDAO.findAllowedCategoryIds(warehouseId);
    }

    private void validate(String name, int capacity) throws ValidationException {
        if (name == null || name.isBlank()) {
            throw new ValidationException("نام انبار نمی‌تواند خالی باشد.");
        }
        if (capacity <= 0) {
            throw new ValidationException("ظرفیت انبار باید بزرگ‌تر از صفر باشد.");
        }
    }
}
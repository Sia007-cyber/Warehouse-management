package ir.university.warehouse.dao;

import java.sql.SQLException;
import java.util.List;

public interface WarehouseAllowedCategoriesDAO {
    void allow(int warehouseId, int categoryId) throws SQLException;
    void disallow(int warehouseId, int categoryId) throws SQLException;
    boolean isAllowed(int warehouseId, int categoryId) throws SQLException;
    List<Integer> findAllowedCategoryIds(int warehouseId) throws SQLException;
}
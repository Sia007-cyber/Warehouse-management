package ir.university.warehouse.gui;

import ir.university.warehouse.dao.*;
import ir.university.warehouse.service.*;

/**
 * محل ساخت و نگهداری تمام DAOها و سرویس‌ها برای استفاده مشترک بین تب‌های GUI.
 * (همون کاری که Main.java برای سناریوی تستی خط‌فرمانی انجام می‌داد.)
 */
public class AppContext {

    public final WarehouseDAO warehouseDAO = new WarehouseDAOImpl();
    public final CategoryDAO categoryDAO = new CategoryDAOImpl();
    public final ItemDAO itemDAO = new ItemDAOImpl();
    public final InventoryDAO inventoryDAO = new InventoryDAOImpl();
    public final PermissionDAO permissionDAO = new PermissionDAOImpl();
    public final CashBalanceDAO cashBalanceDAO = new CashBalanceDAOImpl();
    public final TransactionDAO transactionDAO = new TransactionDAOImpl();
    public final WarehouseAllowedCategoriesDAO allowedCategoriesDAO = new WarehouseAllowedCategoriesDAOImpl();

    public final WarehouseService warehouseService =
            new WarehouseServiceImpl(warehouseDAO, allowedCategoriesDAO, categoryDAO);
    public final CategoryService categoryService = new CategoryServiceImpl(categoryDAO);
    public final ItemService itemService = new ItemServiceImpl(itemDAO, categoryDAO);
    public final InventoryService inventoryService =
            new InventoryServiceImpl(inventoryDAO, permissionDAO, warehouseDAO);
    public final AccountingService accountingService =
            new AccountingServiceImpl(cashBalanceDAO, transactionDAO);
    public final PermissionService permissionService = new PermissionServiceImpl(
            permissionDAO, inventoryDAO, warehouseDAO, itemDAO,
            allowedCategoriesDAO, inventoryService, accountingService);
    public final ReportService reportService = new ReportServiceImpl(
            warehouseDAO, itemDAO, permissionDAO, transactionDAO, inventoryService);
}

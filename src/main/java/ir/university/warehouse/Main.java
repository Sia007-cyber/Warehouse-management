package ir.university.warehouse;

import ir.university.warehouse.dao.*;
import ir.university.warehouse.db.DatabaseConnection;
import ir.university.warehouse.exception.WarehouseException;
import ir.university.warehouse.model.*;
import ir.university.warehouse.service.*;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Warehouse Management System - Test Scenario\n");

        // ساخت DAOها
        WarehouseDAO warehouseDAO = new WarehouseDAOImpl();
        CategoryDAO categoryDAO = new CategoryDAOImpl();
        ItemDAO itemDAO = new ItemDAOImpl();
        InventoryDAO inventoryDAO = new InventoryDAOImpl();
        PermissionDAO permissionDAO = new PermissionDAOImpl();
        CashBalanceDAO cashBalanceDAO = new CashBalanceDAOImpl();
        TransactionDAO transactionDAO = new TransactionDAOImpl();
        WarehouseAllowedCategoriesDAO allowedCategoriesDAO = new WarehouseAllowedCategoriesDAOImpl();

        // ساخت سرویس‌ها
        InventoryService inventoryService = new InventoryServiceImpl(inventoryDAO, permissionDAO, warehouseDAO);
        AccountingService accountingService = new AccountingServiceImpl(cashBalanceDAO, transactionDAO);
        PermissionService permissionService = new PermissionServiceImpl(
                permissionDAO, inventoryDAO, warehouseDAO, itemDAO,
                allowedCategoriesDAO, inventoryService, accountingService);

        try {
            // قدم ۱: ساخت انبار
            Warehouse warehouse = new Warehouse();
            warehouse.setName("Central Warehouse - Test");
            warehouse.setAddress("Tehran");
            warehouse.setCapacity(500);
            warehouse = warehouseDAO.insert(warehouse);
            System.out.println("1) انبار ساخته شد: " + warehouse);

            // قدم ۲: ساخت دسته‌بندی و مجاز کردنش برای انبار
            Category category = new Category();
            category.setName("لبنیات - تست");
            category = categoryDAO.insert(category);
            allowedCategoriesDAO.allow(warehouse.getWarehouseId(), category.getCategoryId());
            System.out.println("2) دسته‌بندی ساخته و مجاز شد: " + category);

            // قدم ۳: ساخت کالا
            Item item = new Item();
            item.setItemCode("MILK-001");
            item.setName("شیر پرچرب");
            item.setDescription("شیر پرچرب ۱ لیتری");
            item.setCategoryId(category.getCategoryId());
            item = itemDAO.insert(item);
            System.out.println("3) کالا ساخته شد: " + item);

            // قدم ۴: شارژ حساب نقدی
            accountingService.deposit(10_000_000);
            System.out.println("4) موجودی نقدی شارژ شد. موجودی فعلی: " + accountingService.getBalance());

            // قدم ۵: صدور و تأیید مجوز ورود (خرید ۱۰۰ عدد شیر با قیمت واحد ۵۰۰۰۰)
            Permission inPermission = permissionService.issuePermission(
                    PermissionType.IN, warehouse.getWarehouseId(), item.getItemId(),
                    100, 50_000, "خرید شیر از تأمین‌کننده", "خرید اولیه", "2026-07-04");
            System.out.println("5a) مجوز ورود صادر شد: " + inPermission);
            System.out.println("    موجودی نقدی بعد از صدور: " + accountingService.getBalance());

            permissionService.confirmPermission(inPermission.getPermissionId());
            System.out.println("5b) مجوز ورود تأیید شد.");
            System.out.println("    موجودی واقعی کالا: " +
                    inventoryService.getRealStock(warehouse.getWarehouseId(), item.getItemId()));

            // قدم ۶: صدور و تأیید مجوز خروج (فروش ۳۰ عدد با قیمت واحد ۷۰۰۰۰)
            Permission outPermission = permissionService.issuePermission(
                    PermissionType.OUT, warehouse.getWarehouseId(), item.getItemId(),
                    30, 70_000, "فروش به سوپرمارکت", "فروش تستی", "2026-07-04");
            System.out.println("6a) مجوز خروج صادر شد: " + outPermission);
            System.out.println("    موجودی قابل خروج بعد از صدور (باید کم شده باشه): " +
                    inventoryService.getAvailableForExit(warehouse.getWarehouseId(), item.getItemId()));

            permissionService.confirmPermission(outPermission.getPermissionId());
            System.out.println("6b) مجوز خروج تأیید شد.");
            System.out.println("    موجودی واقعی کالا: " +
                    inventoryService.getRealStock(warehouse.getWarehouseId(), item.getItemId()));
            System.out.println("    موجودی نقدی نهایی: " + accountingService.getBalance());

            // قدم ۷: تست حالت خطا - خروج بیشتر از موجودی
            System.out.println("\n7) تست خطای عمدی (درخواست خروج بیش از موجودی):");
            try {
                permissionService.issuePermission(
                        PermissionType.OUT, warehouse.getWarehouseId(), item.getItemId(),
                        1000, 70_000, "فروش نامعتبر", "باید خطا بده", "2026-07-04");
                System.out.println("    خطا: این خط نباید اجرا بشه!");
            } catch (WarehouseException e) {
                System.out.println("    خطای مورد انتظار دریافت شد: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (WarehouseException e) {
            System.err.println("Business error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}
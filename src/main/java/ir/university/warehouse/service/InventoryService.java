package ir.university.warehouse.service;

import java.sql.SQLException;

public interface InventoryService {

    /**
     * موجودی واقعی کالا در انبار (مستقیم از جدول Inventory)
     */
    int getRealStock(int warehouseId, int itemId) throws SQLException;

    /**
     * مجموع مجوزهای ورود Issued برای این کالا و انبار (کالای در راه)
     */
    int getIncomingStock(int warehouseId, int itemId) throws SQLException;

    /**
     * مجموع مجوزهای خروج Issued برای این کالا و انبار (رزرو خروج)
     */
    int getReservedStock(int warehouseId, int itemId) throws SQLException;

    /**
     * کالای قابل خروج = موجودی واقعی - رزرو خروج
     */
    int getAvailableForExit(int warehouseId, int itemId) throws SQLException;

    /**
     * بررسی ظرفیت: آیا افزودن مقدار جدید (تعداد کالای ورودی) از ظرفیت کل انبار عبور می‌کند؟
     * ظرفیت انبار به‌صورت مجموع همه کالاهای موجود و در راه در نظر گرفته می‌شود.
     */
    boolean hasCapacityFor(int warehouseId, int itemId, int additionalQuantity) throws SQLException;
}
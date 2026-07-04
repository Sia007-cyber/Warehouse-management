package ir.university.warehouse.service;

import ir.university.warehouse.exception.WarehouseException;
import ir.university.warehouse.model.Permission;
import ir.university.warehouse.model.PermissionType;
import java.sql.SQLException;

public interface PermissionService {

    /**
     * صدور مجوز جدید ورود یا خروج. تمام اعتبارسنجی‌ها (ظرفیت، موجودی نقدی،
     * موجودی قابل خروج، دسته‌بندی مجاز) قبل از ثبت انجام می‌شود.
     */
    Permission issuePermission(PermissionType type, int warehouseId, int itemId, int quantity,
                                double unitPrice, String title, String description, String permissionDate)
            throws SQLException, WarehouseException;

    /**
     * تأیید مجوز توسط انباردار (ISSUED -> DONE).
     * برای ورود: موجودی واقعی افزایش می‌یابد.
     * برای خروج: موجودی واقعی کاهش می‌یابد و مبلغ فروش به حساب واریز می‌شود.
     */
    Permission confirmPermission(int permissionId) throws SQLException, WarehouseException;

    /**
     * بررسی امکان صدور مجوز بدون ایجاد تغییر در دیتابیس (برای پاسخ به
     * سوال «آیا امکان صدور مجوز جدید وجود دارد؟» بدون side effect).
     */
    boolean canIssuePermission(PermissionType type, int warehouseId, int itemId, int quantity) throws SQLException;
}
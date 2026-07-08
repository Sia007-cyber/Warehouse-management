package ir.university.warehouse.gui;

import java.util.HashMap;
import java.util.Map;

/**
 * کلاس ساده برای مدیریت متن‌های دوزبانه (فارسی/انگلیسی) رابط کاربری.
 * توجه: پیام‌های خطای اعتبارسنجی که از لایه‌ی سرویس/DAO می‌آیند (getMessage())
 * جزو این سیستم نیستند و همیشه به فارسی نمایش داده می‌شوند؛ فقط متن‌های ثابت
 * رابط کاربری (عنوان‌ها، دکمه‌ها، ستون‌ها) ترجمه می‌شوند.
 */
public final class I18n {

    public enum Lang { FA, EN }

    private static Lang current = Lang.EN;

    private static final Map<String, String[]> MAP = new HashMap<>();
    // ترتیب مقادیر آرایه: [0]=فارسی, [1]=انگلیسی

    private static void put(String key, String fa, String en) {
        MAP.put(key, new String[]{fa, en});
    }

    static {
        // عمومی
        put("app.title", "Warehouse-management", "Warehouse-management");
        put("lang.fa", "فارسی", "Persian");
        put("lang.en", "English", "English");
        put("button.add", "افزودن", "Add");
        put("button.update", "ویرایش", "Update");
        put("button.delete", "حذف", "Delete");
        put("button.clear", "پاک کردن فرم", "Clear form");
        put("button.refresh", "بازخوانی", "Refresh");
        put("button.save", "ذخیره", "Save");
        put("column.id", "شناسه", "ID");
        put("column.name", "نام", "Name");
        put("error.title", "خطا", "Error");
        put("confirm.title", "تأیید عملیات", "Confirm action");

        // تب‌ها
        put("tab.warehouses", "انبارها", "Warehouses");
        put("tab.categories", "دسته‌بندی‌ها", "Categories");
        put("tab.items", "کالاها", "Items");
        put("tab.permissions", "مجوزها", "Permissions");
        put("tab.inventory", "موجودی", "Inventory");
        put("tab.accounting", "حسابداری", "Accounting");
        put("tab.reports", "گزارش‌ها", "Reports");

        // انبارها
        put("warehouse.column.address", "آدرس", "Address");
        put("warehouse.column.capacity", "ظرفیت", "Capacity");
        put("warehouse.form.title", "مشخصات انبار", "Warehouse details");
        put("warehouse.field.name", "نام:", "Name:");
        put("warehouse.field.address", "آدرس:", "Address:");
        put("warehouse.field.capacity", "ظرفیت:", "Capacity:");
        put("warehouse.categories.label", "دسته‌بندی‌های مجاز این انبار:", "Allowed categories for this warehouse:");
        put("warehouse.categories.save", "ذخیره دسته‌های مجاز", "Save allowed categories");
        put("warehouse.select.first", "ابتدا یک انبار را از جدول انتخاب کنید.", "Please select a warehouse from the table first.");
        put("warehouse.confirm.delete", "حذف انبار انتخاب‌شده انجام شود؟", "Delete the selected warehouse?");
        put("warehouse.delete.failed", "حذف ناموفق بود (احتمالاً این انبار در مجوزها استفاده شده): ", "Delete failed (this warehouse is likely referenced by permissions): ");
        put("warehouse.categories.updated", "دسته‌های مجاز به‌روزرسانی شد.", "Allowed categories updated.");
        put("warehouse.capacity.notint", "ظرفیت باید یک عدد صحیح باشد.", "Capacity must be an integer.");
        put("warehouse.load.error", "خطا در بارگذاری انبارها: ", "Error loading warehouses: ");
        put("warehouse.categories.load.error", "خطا در بارگذاری دسته‌بندی‌ها: ", "Error loading categories: ");
        put("warehouse.allowed.load.error", "خطا در بارگذاری دسته‌های مجاز: ", "Error loading allowed categories: ");

        // دسته‌بندی‌ها
        put("category.column.parent", "والد", "Parent");
        put("category.form.title", "مشخصات دسته‌بندی", "Category details");
        put("category.field.name", "نام دسته‌بندی:", "Category name:");
        put("category.field.parent", "دسته‌ی والد (اختیاری):", "Parent category (optional):");
        put("category.parent.none", "(بدون والد)", "(no parent)");
        put("category.select.first", "ابتدا یک دسته‌بندی را از جدول انتخاب کنید.", "Please select a category from the table first.");
        put("category.confirm.delete", "حذف دسته‌بندی انتخاب‌شده انجام شود؟", "Delete the selected category?");
        put("category.delete.failed", "حذف ناموفق بود (احتمالاً کالایی به این دسته وابسته است): ", "Delete failed (an item is likely linked to this category): ");
        put("category.load.error", "خطا در بارگذاری دسته‌بندی‌ها: ", "Error loading categories: ");

        // کالاها
        put("item.column.code", "کد کالا", "Item code");
        put("item.column.category", "دسته‌بندی", "Category");
        put("item.field.code", "کد کالا:", "Item code:");
        put("item.field.name", "نام:", "Name:");
        put("item.field.desc", "توضیحات:", "Description:");
        put("item.field.category", "دسته‌بندی:", "Category:");
        put("item.form.title", "مشخصات کالا", "Item details");
        put("item.select.first", "ابتدا یک کالا را از جدول انتخاب کنید.", "Please select an item from the table first.");
        put("item.confirm.delete", "حذف کالای انتخاب‌شده انجام شود؟", "Delete the selected item?");
        put("item.delete.failed", "حذف ناموفق بود (احتمالاً این کالا در مجوزها یا موجودی استفاده شده): ", "Delete failed (this item is likely referenced in permissions or inventory): ");
        put("item.category.required", "انتخاب دسته‌بندی الزامی است.", "Selecting a category is required.");
        put("item.load.error", "خطا در بارگذاری کالاها: ", "Error loading items: ");

        // مجوزها
        put("permission.column.type", "نوع", "Type");
        put("permission.column.warehouse", "انبار", "Warehouse");
        put("permission.column.item", "کالا", "Item");
        put("permission.column.quantity", "تعداد", "Quantity");
        put("permission.column.unitprice", "قیمت واحد", "Unit price");
        put("permission.column.title", "عنوان", "Title");
        put("permission.column.status", "وضعیت", "Status");
        put("permission.column.date", "تاریخ", "Date");
        put("permission.form.title", "صدور مجوز جدید", "Issue new permission");
        put("permission.field.type", "نوع مجوز:", "Permission type:");
        put("permission.field.warehouse", "انبار:", "Warehouse:");
        put("permission.field.item", "کالا:", "Item:");
        put("permission.field.quantity", "تعداد:", "Quantity:");
        put("permission.field.unitprice", "قیمت واحد:", "Unit price:");
        put("permission.field.title", "عنوان:", "Title:");
        put("permission.field.desc", "توضیحات:", "Description:");
        put("permission.field.date", "تاریخ:", "Date:");
        put("permission.button.issue", "صدور مجوز", "Issue permission");
        put("permission.button.confirm", "تأیید مجوز انتخاب‌شده", "Confirm selected permission");
        put("permission.balance.label", "موجودی نقدی فعلی: ", "Current cash balance: ");
        put("permission.select.first", "ابتدا یک مجوز را از جدول انتخاب کنید.", "Please select a permission from the table first.");
        put("permission.already.confirmed", "این مجوز قبلاً تأیید شده است.", "This permission has already been confirmed.");
        put("permission.fields.required", "نوع، انبار و کالا باید انتخاب شوند.", "Type, warehouse, and item must all be selected.");
        put("permission.number.error", "تعداد و قیمت واحد باید عدد باشند.", "Quantity and unit price must be numbers.");
        put("permission.load.error", "خطا در بارگذاری مجوزها: ", "Error loading permissions: ");
        put("permission.balance.error", "خطا در خواندن موجودی نقدی: ", "Error reading cash balance: ");

        // موجودی
        put("inventory.filter.warehouse", "فیلتر انبار:", "Filter warehouse:");
        put("inventory.filter.all", "همه‌ی انبارها", "All warehouses");
        put("inventory.button.show", "نمایش موجودی", "Show inventory");
        put("inventory.column.real", "موجودی واقعی", "Real stock");
        put("inventory.column.incoming", "در راه", "Incoming");
        put("inventory.column.reserved", "رزرو خروج", "Reserved (out)");
        put("inventory.column.available", "قابل خروج", "Available to ship");
        put("inventory.load.error", "خطا در بارگذاری موجودی: ", "Error loading inventory: ");
        put("inventory.warehouses.error", "خطا در بارگذاری انبارها: ", "Error loading warehouses: ");

        // حسابداری
        put("accounting.balance.label", "موجودی نقدی فعلی: ", "Current cash balance: ");
        put("accounting.field.amount", "مبلغ:", "Amount:");
        put("accounting.amount.prompt", "مبلغ", "amount");
        put("accounting.button.deposit", "افزودن موجودی نقدی (شارژ حساب)", "Add cash (top up balance)");
        put("accounting.history.label", "تاریخچه تراکنش‌های مالی:", "Financial transaction history:");
        put("accounting.column.permission", "شناسه مجوز", "Permission ID");
        put("accounting.column.type", "نوع", "Type");
        put("accounting.column.amount", "مبلغ", "Amount");
        put("accounting.column.date", "تاریخ", "Date");
        put("accounting.amount.positive", "مبلغ باید مثبت باشد.", "Amount must be positive.");
        put("accounting.amount.notnumber", "مبلغ باید عدد باشد.", "Amount must be a number.");
        put("accounting.load.error", "خطا در بارگذاری اطلاعات حسابداری: ", "Error loading accounting data: ");

        // گزارش‌ها
        put("report.inventory.tab", "وضعیت موجودی", "Inventory status");
        put("report.sales.tab", "فروش ماهانه", "Monthly sales");
        put("report.permissions.tab", "گزارش مجوزها", "Permissions report");
        put("report.column.warehouse", "انبار", "Warehouse");
        put("report.column.item", "کالا", "Item");
        put("report.field.warehouse", "انبار:", "Warehouse:");
        put("report.field.year", "سال:", "Year:");
        put("report.field.month", "ماه:", "Month:");
        put("report.button.generate", "ساخت گزارش", "Generate report");
        put("report.column.qtysold", "تعداد فروخته‌شده", "Quantity sold");
        put("report.column.total", "مبلغ کل", "Total amount");
        put("report.grandtotal.label", "جمع کل فروش: ", "Grand total sales: ");
        put("report.select.warehouse.first", "ابتدا یک انبار را انتخاب کنید.", "Please select a warehouse first.");
        put("report.pending.label", "مجوزهای در انتظار (ISSUED):", "Pending permissions (ISSUED):");
        put("report.completed.label", "مجوزهای انجام‌شده (DONE):", "Completed permissions (DONE):");
        put("report.error", "خطا: ", "Error: ");
        put("report.warehouses.error", "خطا در بارگذاری انبارها: ", "Error loading warehouses: ");
    }

    private I18n() {}

    public static void setLanguage(Lang lang) {
        current = lang;
    }

    public static Lang getLanguage() {
        return current;
    }

    public static boolean isRtl() {
        return current == Lang.FA;
    }

    public static String t(String key) {
        String[] values = MAP.get(key);
        if (values == null) return key;
        return current == Lang.FA ? values[0] : values[1];
    }
}

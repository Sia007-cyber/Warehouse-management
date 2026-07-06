-- Warehouse Management System - Database Schema

PRAGMA foreign_keys = ON;

-- جدول انبارها
CREATE TABLE Warehouses (
    warehouse_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    address TEXT,
    capacity INTEGER NOT NULL CHECK (capacity > 0)
);

-- جدول دسته‌بندی‌ها
-- توجه: parent_id برای پشتیبانی احتمالی از دسته‌بندی سلسله‌مراتبی در نظر گرفته شده،
-- اما در داده‌های نمونه‌ی این پروژه دسته‌ها تخت هستند و parent_id همیشه NULL است.
CREATE TABLE Categories (
    category_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    parent_id INTEGER,
    FOREIGN KEY (parent_id) REFERENCES Categories(category_id)
);

-- دسته‌بندی‌های مجاز برای هر انبار (رابطه چند‌به‌چند)
CREATE TABLE WarehouseAllowedCategories (
    warehouse_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    PRIMARY KEY (warehouse_id, category_id),
    FOREIGN KEY (warehouse_id) REFERENCES Warehouses(warehouse_id),
    FOREIGN KEY (category_id) REFERENCES Categories(category_id)
);

-- جدول کالاها
CREATE TABLE Items (
    item_id INTEGER PRIMARY KEY AUTOINCREMENT,
    item_code TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    description TEXT,
    category_id INTEGER NOT NULL,
    FOREIGN KEY (category_id) REFERENCES Categories(category_id)
);

-- جدول مجوزهای ورود و خروج (هسته اصلی سیستم)
CREATE TABLE Permissions (
    permission_id INTEGER PRIMARY KEY AUTOINCREMENT,
    type TEXT NOT NULL CHECK (type IN ('IN', 'OUT')),
    warehouse_id INTEGER NOT NULL,
    item_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price REAL NOT NULL CHECK (unit_price >= 0),
    title TEXT NOT NULL,
    description TEXT,
    status TEXT NOT NULL DEFAULT 'ISSUED' CHECK (status IN ('ISSUED', 'DONE')),
    permission_date TEXT NOT NULL,
    FOREIGN KEY (warehouse_id) REFERENCES Warehouses(warehouse_id),
    FOREIGN KEY (item_id) REFERENCES Items(item_id)
);

-- جدول موجودی واقعی هر کالا در هر انبار
-- توجه: reserved_stock و incoming_stock ذخیره نمی‌شوند، از روی Permissions محاسبه می‌شوند
CREATE TABLE Inventory (
    warehouse_id INTEGER NOT NULL,
    item_id INTEGER NOT NULL,
    real_stock INTEGER NOT NULL DEFAULT 0 CHECK (real_stock >= 0),
    PRIMARY KEY (warehouse_id, item_id),
    FOREIGN KEY (warehouse_id) REFERENCES Warehouses(warehouse_id),
    FOREIGN KEY (item_id) REFERENCES Items(item_id)
);

-- موجودی نقدی سیستم (یک رکورد ساده به‌عنوان singleton)
CREATE TABLE CashBalance (
    id INTEGER PRIMARY KEY CHECK (id = 1),
    balance REAL NOT NULL DEFAULT 0 CHECK (balance >= 0)
);

-- جدول تراکنش‌های مالی (برای گزارش فروش و پیگیری نقدینگی)
CREATE TABLE Transactions (
    transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
    permission_id INTEGER NOT NULL,
    type TEXT NOT NULL CHECK (type IN ('PURCHASE', 'SALE')),
    amount REAL NOT NULL CHECK (amount >= 0),
    transaction_date TEXT NOT NULL,
    FOREIGN KEY (permission_id) REFERENCES Permissions(permission_id)
);
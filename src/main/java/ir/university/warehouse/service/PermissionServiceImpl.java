package ir.university.warehouse.service;

import ir.university.warehouse.dao.*;
import ir.university.warehouse.db.DatabaseConnection;
import ir.university.warehouse.exception.*;
import ir.university.warehouse.model.*;
import java.sql.Connection;
import java.sql.SQLException;

public class PermissionServiceImpl implements PermissionService {

    private final PermissionDAO permissionDAO;
    private final InventoryDAO inventoryDAO;
    private final WarehouseDAO warehouseDAO;
    private final ItemDAO itemDAO;
    private final WarehouseAllowedCategoriesDAO allowedCategoriesDAO;
    private final InventoryService inventoryService;
    private final AccountingService accountingService;

    public PermissionServiceImpl(PermissionDAO permissionDAO,
                                  InventoryDAO inventoryDAO,
                                  WarehouseDAO warehouseDAO,
                                  ItemDAO itemDAO,
                                  WarehouseAllowedCategoriesDAO allowedCategoriesDAO,
                                  InventoryService inventoryService,
                                  AccountingService accountingService) {
        this.permissionDAO = permissionDAO;
        this.inventoryDAO = inventoryDAO;
        this.warehouseDAO = warehouseDAO;
        this.itemDAO = itemDAO;
        this.allowedCategoriesDAO = allowedCategoriesDAO;
        this.inventoryService = inventoryService;
        this.accountingService = accountingService;
    }

    @Override
    public Permission issuePermission(PermissionType type, int warehouseId, int itemId, int quantity,
                                       double unitPrice, String title, String description, String permissionDate)
            throws SQLException, WarehouseException {

        validateBasicInput(quantity, unitPrice, title);

        Warehouse warehouse = warehouseDAO.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("انباری با شناسه " + warehouseId + " یافت نشد."));
        Item item = itemDAO.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("کالایی با شناسه " + itemId + " یافت نشد."));

        if (!allowedCategoriesDAO.isAllowed(warehouseId, item.getCategoryId())) {
            throw new CategoryNotAllowedException(
                    "دسته‌بندی کالای «" + item.getName() + "» برای انبار «" + warehouse.getName() + "» مجاز نیست.");
        }

        if (type == PermissionType.IN) {
            if (!inventoryService.hasCapacityFor(warehouseId, itemId, quantity)) {
                throw new CapacityExceededException(
                        "ظرفیت انبار «" + warehouse.getName() + "» برای این مقدار کافی نیست.");
            }
        } else {
            int availableForExit = inventoryService.getAvailableForExit(warehouseId, itemId);
            if (availableForExit < quantity) {
                throw new InsufficientStockException(
                        "موجودی قابل خروج کالا «" + item.getName() + "» کافی نیست. موجود: "
                                + availableForExit + "، درخواستی: " + quantity);
            }
        }

        Connection conn = DatabaseConnection.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            Permission permission = new Permission();
            permission.setType(type);
            permission.setWarehouseId(warehouseId);
            permission.setItemId(itemId);
            permission.setQuantity(quantity);
            permission.setUnitPrice(unitPrice);
            permission.setTitle(title);
            permission.setDescription(description);
            permission.setStatus(PermissionStatus.ISSUED);
            permission.setPermissionDate(permissionDate);
            permissionDAO.insert(permission);

            if (type == PermissionType.IN) {
                double totalCost = quantity * unitPrice;
                accountingService.withdraw(totalCost);
                accountingService.recordTransaction(permission.getPermissionId(), TransactionType.PURCHASE,
                        totalCost, permissionDate);
            }

            conn.commit();
            return permission;

        } catch (SQLException | WarehouseException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    @Override
    public Permission confirmPermission(int permissionId) throws SQLException, WarehouseException {
        Permission permission = permissionDAO.findById(permissionId)
                .orElseThrow(() -> new EntityNotFoundException("مجوزی با شناسه " + permissionId + " یافت نشد."));

        if (permission.getStatus() != PermissionStatus.ISSUED) {
            throw new InvalidPermissionStatusException(
                    "این مجوز در وضعیت " + permission.getStatus() + " است و قابل تأیید مجدد نیست.");
        }

        Connection conn = DatabaseConnection.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            int currentRealStock = inventoryService.getRealStock(permission.getWarehouseId(), permission.getItemId());
            InventoryRecord record = new InventoryRecord();
            record.setWarehouseId(permission.getWarehouseId());
            record.setItemId(permission.getItemId());

            if (permission.getType() == PermissionType.IN) {
                record.setRealStock(currentRealStock + permission.getQuantity());
                inventoryDAO.upsert(record);

            } else {
                int newStock = currentRealStock - permission.getQuantity();
                if (newStock < 0) {
                    throw new InsufficientStockException(
                            "موجودی واقعی برای انجام این خروج کافی نیست.");
                }
                record.setRealStock(newStock);
                inventoryDAO.upsert(record);

                double totalAmount = permission.getQuantity() * permission.getUnitPrice();
                accountingService.deposit(totalAmount);
                accountingService.recordTransaction(permission.getPermissionId(), TransactionType.SALE,
                        totalAmount, permission.getPermissionDate());
            }

            permissionDAO.updateStatus(permissionId, PermissionStatus.DONE);
            permission.setStatus(PermissionStatus.DONE);

            conn.commit();
            return permission;

        } catch (SQLException | WarehouseException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    @Override
    public boolean canIssuePermission(PermissionType type, int warehouseId, int itemId, int quantity) throws SQLException {
        if (quantity <= 0) return false;
        if (warehouseDAO.findById(warehouseId).isEmpty()) return false;
        var itemOpt = itemDAO.findById(itemId);
        if (itemOpt.isEmpty()) return false;

        if (!allowedCategoriesDAO.isAllowed(warehouseId, itemOpt.get().getCategoryId())) {
            return false;
        }

        if (type == PermissionType.IN) {
            return inventoryService.hasCapacityFor(warehouseId, itemId, quantity);
        } else {
            return inventoryService.getAvailableForExit(warehouseId, itemId) >= quantity;
        }
    }

    private void validateBasicInput(int quantity, double unitPrice, String title) throws ValidationException {
        if (quantity <= 0) {
            throw new ValidationException("تعداد باید بزرگ‌تر از صفر باشد.");
        }
        if (unitPrice < 0) {
            throw new ValidationException("قیمت واحد نمی‌تواند منفی باشد.");
        }
        if (title == null || title.isBlank()) {
            throw new ValidationException("عنوان مجوز نمی‌تواند خالی باشد.");
        }
    }
}
package ir.university.warehouse.service;

import ir.university.warehouse.dao.ItemDAO;
import ir.university.warehouse.dao.PermissionDAO;
import ir.university.warehouse.dao.TransactionDAO;
import ir.university.warehouse.dao.WarehouseDAO;
import ir.university.warehouse.dto.InventoryStatusDTO;
import ir.university.warehouse.dto.MonthlySalesReportDTO;
import ir.university.warehouse.dto.PermissionsReportDTO;
import ir.university.warehouse.dto.SalesLineItemDTO;
import ir.university.warehouse.model.*;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.*;

public class ReportServiceImpl implements ReportService {

    private final WarehouseDAO warehouseDAO;
    private final ItemDAO itemDAO;
    private final PermissionDAO permissionDAO;
    private final TransactionDAO transactionDAO;
    private final InventoryService inventoryService;

    public ReportServiceImpl(WarehouseDAO warehouseDAO, ItemDAO itemDAO, PermissionDAO permissionDAO,
                              TransactionDAO transactionDAO, InventoryService inventoryService) {
        this.warehouseDAO = warehouseDAO;
        this.itemDAO = itemDAO;
        this.permissionDAO = permissionDAO;
        this.transactionDAO = transactionDAO;
        this.inventoryService = inventoryService;
    }

    @Override
    public List<InventoryStatusDTO> getInventoryStatus() throws SQLException {
        List<InventoryStatusDTO> result = new ArrayList<>();
        for (Warehouse warehouse : warehouseDAO.findAll()) {
            result.addAll(buildInventoryStatusForWarehouse(warehouse));
        }
        return result;
    }

    @Override
    public List<InventoryStatusDTO> getInventoryStatusByWarehouse(int warehouseId) throws SQLException {
        Optional<Warehouse> warehouseOpt = warehouseDAO.findById(warehouseId);
        if (warehouseOpt.isEmpty()) {
            return Collections.emptyList();
        }
        return buildInventoryStatusForWarehouse(warehouseOpt.get());
    }

    private List<InventoryStatusDTO> buildInventoryStatusForWarehouse(Warehouse warehouse) throws SQLException {
        List<InventoryStatusDTO> result = new ArrayList<>();
        for (Item item : itemDAO.findAll()) {
            int real = inventoryService.getRealStock(warehouse.getWarehouseId(), item.getItemId());
            int incoming = inventoryService.getIncomingStock(warehouse.getWarehouseId(), item.getItemId());
            int reserved = inventoryService.getReservedStock(warehouse.getWarehouseId(), item.getItemId());

            if (real == 0 && incoming == 0 && reserved == 0) {
                continue; // هیچ فعالیتی برای این ترکیب انبار/کالا نبوده، از گزارش حذف می‌شود
            }

            int available = real - reserved;
            result.add(new InventoryStatusDTO(
                    warehouse.getWarehouseId(), warehouse.getName(),
                    item.getItemId(), item.getItemCode(), item.getName(),
                    real, incoming, reserved, available));
        }
        return result;
    }

    @Override
    public MonthlySalesReportDTO getMonthlySalesReport(int warehouseId, int year, int month) throws SQLException {
        Warehouse warehouse = warehouseDAO.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("انباری با شناسه " + warehouseId + " یافت نشد."));

        YearMonth yearMonth = YearMonth.of(year, month);
        String startDate = yearMonth.atDay(1).toString();
        String endDate = yearMonth.atEndOfMonth().toString();

        List<Transaction> transactions = transactionDAO.findByDateRange(startDate, endDate);

        // itemId -> [quantity, amount]
        Map<Integer, int[]> quantityByItem = new LinkedHashMap<>();
        Map<Integer, Double> amountByItem = new LinkedHashMap<>();
        double grandTotal = 0;

        for (Transaction tx : transactions) {
            if (tx.getType() != TransactionType.SALE) {
                continue;
            }
            Optional<Permission> permissionOpt = permissionDAO.findById(tx.getPermissionId());
            if (permissionOpt.isEmpty()) {
                continue;
            }
            Permission permission = permissionOpt.get();
            if (permission.getWarehouseId() != warehouseId) {
                continue;
            }

            int itemId = permission.getItemId();
            quantityByItem.merge(itemId, new int[]{permission.getQuantity()},
                    (a, b) -> new int[]{a[0] + b[0]});
            amountByItem.merge(itemId, tx.getAmount(), Double::sum);
            grandTotal += tx.getAmount();
        }

        List<SalesLineItemDTO> lineItems = new ArrayList<>();
        for (Map.Entry<Integer, int[]> entry : quantityByItem.entrySet()) {
            int itemId = entry.getKey();
            String itemName = itemDAO.findById(itemId).map(Item::getName).orElse("کالای حذف‌شده #" + itemId);
            lineItems.add(new SalesLineItemDTO(itemId, itemName, entry.getValue()[0], amountByItem.get(itemId)));
        }

        return new MonthlySalesReportDTO(warehouseId, warehouse.getName(), year, month, lineItems, grandTotal);
    }

    @Override
    public PermissionsReportDTO getPermissionsReport() throws SQLException {
        List<Permission> pending = permissionDAO.findByStatus(PermissionStatus.ISSUED);
        List<Permission> completed = permissionDAO.findByStatus(PermissionStatus.DONE);
        return new PermissionsReportDTO(pending, completed);
    }
}
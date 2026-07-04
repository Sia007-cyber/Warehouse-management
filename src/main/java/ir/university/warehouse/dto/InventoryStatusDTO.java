package ir.university.warehouse.dto;

public class InventoryStatusDTO {

    private final int warehouseId;
    private final String warehouseName;
    private final int itemId;
    private final String itemCode;
    private final String itemName;
    private final int realStock;
    private final int incomingStock;
    private final int reservedStock;
    private final int availableForExit;

    public InventoryStatusDTO(int warehouseId, String warehouseName, int itemId, String itemCode,
                               String itemName, int realStock, int incomingStock,
                               int reservedStock, int availableForExit) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.itemId = itemId;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.realStock = realStock;
        this.incomingStock = incomingStock;
        this.reservedStock = reservedStock;
        this.availableForExit = availableForExit;
    }

    public int getWarehouseId() { return warehouseId; }
    public String getWarehouseName() { return warehouseName; }
    public int getItemId() { return itemId; }
    public String getItemCode() { return itemCode; }
    public String getItemName() { return itemName; }
    public int getRealStock() { return realStock; }
    public int getIncomingStock() { return incomingStock; }
    public int getReservedStock() { return reservedStock; }
    public int getAvailableForExit() { return availableForExit; }

    @Override
    public String toString() {
        return "InventoryStatus{" +
                "warehouse='" + warehouseName + '\'' +
                ", item='" + itemName + "' (" + itemCode + ')' +
                ", real=" + realStock +
                ", incoming=" + incomingStock +
                ", reserved=" + reservedStock +
                ", availableForExit=" + availableForExit +
                '}';
    }
}
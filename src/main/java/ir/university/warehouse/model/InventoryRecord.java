package ir.university.warehouse.model;

public class InventoryRecord {

    private int warehouseId;
    private int itemId;
    private int realStock;

    public InventoryRecord() {}

    public InventoryRecord(int warehouseId, int itemId, int realStock) {
        this.warehouseId = warehouseId;
        this.itemId = itemId;
        this.realStock = realStock;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getRealStock() {
        return realStock;
    }

    public void setRealStock(int realStock) {
        this.realStock = realStock;
    }

    @Override
    public String toString() {
        return "InventoryRecord{" +
                "warehouseId=" + warehouseId +
                ", itemId=" + itemId +
                ", realStock=" + realStock +
                '}';
    }
}
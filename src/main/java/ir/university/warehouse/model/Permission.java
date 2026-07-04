package ir.university.warehouse.model;

public class Permission {

    private int permissionId;
    private PermissionType type;
    private int warehouseId;
    private int itemId;
    private int quantity;
    private double unitPrice;
    private String title;
    private String description;
    private PermissionStatus status;
    private String permissionDate;

    public Permission() {}

    public Permission(int permissionId, PermissionType type, int warehouseId, int itemId,
                       int quantity, double unitPrice, String title, String description,
                       PermissionStatus status, String permissionDate) {
        this.permissionId = permissionId;
        this.type = type;
        this.warehouseId = warehouseId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.title = title;
        this.description = description;
        this.status = status;
        this.permissionDate = permissionDate;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public PermissionType getType() {
        return type;
    }

    public void setType(PermissionType type) {
        this.type = type;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PermissionStatus getStatus() {
        return status;
    }

    public void setStatus(PermissionStatus status) {
        this.status = status;
    }

    public String getPermissionDate() {
        return permissionDate;
    }

    public void setPermissionDate(String permissionDate) {
        this.permissionDate = permissionDate;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "permissionId=" + permissionId +
                ", type=" + type +
                ", warehouseId=" + warehouseId +
                ", itemId=" + itemId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", permissionDate='" + permissionDate + '\'' +
                '}';
    }
}
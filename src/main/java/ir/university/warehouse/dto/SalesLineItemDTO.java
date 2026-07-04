package ir.university.warehouse.dto;

public class SalesLineItemDTO {

    private final int itemId;
    private final String itemName;
    private final int quantitySold;
    private final double totalAmount;

    public SalesLineItemDTO(int itemId, String itemName, int quantitySold, double totalAmount) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantitySold = quantitySold;
        this.totalAmount = totalAmount;
    }

    public int getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public int getQuantitySold() { return quantitySold; }
    public double getTotalAmount() { return totalAmount; }

    @Override
    public String toString() {
        return "  - " + itemName + ": " + quantitySold + " واحد، مبلغ " + totalAmount;
    }
}
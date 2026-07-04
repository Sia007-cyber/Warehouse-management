package ir.university.warehouse.dto;

import java.util.List;

public class MonthlySalesReportDTO {

    private final int warehouseId;
    private final String warehouseName;
    private final int year;
    private final int month;
    private final List<SalesLineItemDTO> lineItems;
    private final double grandTotal;

    public MonthlySalesReportDTO(int warehouseId, String warehouseName, int year, int month,
                                  List<SalesLineItemDTO> lineItems, double grandTotal) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.year = year;
        this.month = month;
        this.lineItems = lineItems;
        this.grandTotal = grandTotal;
    }

    public int getWarehouseId() { return warehouseId; }
    public String getWarehouseName() { return warehouseName; }
    public int getYear() { return year; }
    public int getMonth() { return month; }
    public List<SalesLineItemDTO> getLineItems() { return lineItems; }
    public double getGrandTotal() { return grandTotal; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("گزارش فروش ماهانه - انبار: ").append(warehouseName)
          .append(" - ").append(year).append('/').append(month).append('\n');
        for (SalesLineItemDTO item : lineItems) {
            sb.append(item).append('\n');
        }
        sb.append("جمع کل: ").append(grandTotal);
        return sb.toString();
    }
}
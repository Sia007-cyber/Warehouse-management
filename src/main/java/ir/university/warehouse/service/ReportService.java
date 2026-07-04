package ir.university.warehouse.service;

import ir.university.warehouse.dto.InventoryStatusDTO;
import ir.university.warehouse.dto.MonthlySalesReportDTO;
import ir.university.warehouse.dto.PermissionsReportDTO;
import java.sql.SQLException;
import java.util.List;

public interface ReportService {

    /**
     * وضعیت موجودی لحظه‌ای برای همه‌ی ترکیب‌های انبار/کالا که فعالیتی داشته‌اند.
     */
    List<InventoryStatusDTO> getInventoryStatus() throws SQLException;

    /**
     * وضعیت موجودی لحظه‌ای فقط برای یک انبار خاص.
     */
    List<InventoryStatusDTO> getInventoryStatusByWarehouse(int warehouseId) throws SQLException;

    /**
     * گزارش فروش یک انبار خاص در یک ماه/سال مشخص.
     */
    MonthlySalesReportDTO getMonthlySalesReport(int warehouseId, int year, int month) throws SQLException;

    /**
     * گزارش مجوزهای در انتظار (ISSUED) و انجام‌شده (DONE).
     */
    PermissionsReportDTO getPermissionsReport() throws SQLException;
}
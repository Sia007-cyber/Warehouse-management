package ir.university.warehouse.gui.tabs;

import ir.university.warehouse.dto.InventoryStatusDTO;
import ir.university.warehouse.dto.SalesLineItemDTO;
import ir.university.warehouse.gui.AppContext;
import ir.university.warehouse.gui.Dialogs;
import ir.university.warehouse.model.Permission;
import ir.university.warehouse.model.Warehouse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalDate;

public class ReportTab extends Tab {

    private final AppContext ctx;

    public ReportTab(AppContext ctx) {
        super("گزارش‌ها");
        this.ctx = ctx;
        setClosable(false);

        TabPane inner = new TabPane();
        inner.getTabs().addAll(buildInventoryStatusTab(), buildMonthlySalesTab(), buildPermissionsReportTab());
        setContent(inner);
    }

    // --- گزارش وضعیت موجودی ---
    private Tab buildInventoryStatusTab() {
        Tab tab = new Tab("وضعیت موجودی");
        tab.setClosable(false);

        TableView<InventoryStatusDTO> table = new TableView<>();
        ObservableList<InventoryStatusDTO> data = FXCollections.observableArrayList();

        TableColumn<InventoryStatusDTO, String> warehouseCol = new TableColumn<>("انبار");
        warehouseCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getWarehouseName()));
        TableColumn<InventoryStatusDTO, String> itemCol = new TableColumn<>("کالا");
        itemCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getItemName()));
        TableColumn<InventoryStatusDTO, Number> realCol = new TableColumn<>("موجودی واقعی");
        realCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getRealStock()));
        TableColumn<InventoryStatusDTO, Number> availableCol = new TableColumn<>("قابل خروج");
        availableCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getAvailableForExit()));
        table.getColumns().setAll(warehouseCol, itemCol, realCol, availableCol);
        table.setItems(data);

        Button refreshBtn = new Button("بازخوانی گزارش");
        refreshBtn.setOnAction(e -> {
            try {
                data.setAll(ctx.reportService.getInventoryStatus());
            } catch (SQLException ex) {
                Dialogs.error("خطا: " + ex.getMessage());
            }
        });

        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));
        pane.setTop(refreshBtn);
        pane.setCenter(table);
        BorderPane.setMargin(refreshBtn, new Insets(0, 0, 10, 0));
        tab.setContent(pane);
        return tab;
    }

    // --- گزارش فروش ماهانه ---
    private Tab buildMonthlySalesTab() {
        Tab tab = new Tab("فروش ماهانه");
        tab.setClosable(false);

        ComboBox<Warehouse> warehouseCombo = new ComboBox<>();
        warehouseCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Warehouse w) { return w == null ? "" : w.getName(); }
            @Override public Warehouse fromString(String s) { return null; }
        });
        Spinner<Integer> yearSpinner = new Spinner<>(2000, 2100, LocalDate.now().getYear());
        Spinner<Integer> monthSpinner = new Spinner<>(1, 12, LocalDate.now().getMonthValue());
        yearSpinner.setEditable(true);
        monthSpinner.setEditable(true);

        TableView<SalesLineItemDTO> table = new TableView<>();
        ObservableList<SalesLineItemDTO> data = FXCollections.observableArrayList();
        TableColumn<SalesLineItemDTO, String> itemCol = new TableColumn<>("کالا");
        itemCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        TableColumn<SalesLineItemDTO, Number> qtyCol = new TableColumn<>("تعداد فروخته‌شده");
        qtyCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantitySold()));
        TableColumn<SalesLineItemDTO, Number> totalCol = new TableColumn<>("مبلغ کل");
        totalCol.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getTotalAmount()));
        table.getColumns().setAll(itemCol, qtyCol, totalCol);
        table.setItems(data);

        Label grandTotalLabel = new Label();
        grandTotalLabel.setStyle("-fx-font-weight: bold;");

        Button generateBtn = new Button("ساخت گزارش");
        generateBtn.setOnAction(e -> {
            Warehouse w = warehouseCombo.getSelectionModel().getSelectedItem();
            if (w == null) {
                Dialogs.error("ابتدا یک انبار را انتخاب کنید.");
                return;
            }
            try {
                var report = ctx.reportService.getMonthlySalesReport(w.getWarehouseId(), yearSpinner.getValue(), monthSpinner.getValue());
                data.setAll(report.getLineItems());
                grandTotalLabel.setText("جمع کل فروش: " + report.getGrandTotal());
            } catch (SQLException ex) {
                Dialogs.error("خطا: " + ex.getMessage());
            }
        });

        HBox controls = new HBox(8, new Label("انبار:"), warehouseCombo,
                new Label("سال:"), yearSpinner, new Label("ماه:"), monthSpinner, generateBtn);
        controls.setPadding(new Insets(0, 0, 10, 0));

        VBox root = new VBox(10, controls, table, grandTotalLabel);
        root.setPadding(new Insets(10));

        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) {
                try {
                    warehouseCombo.setItems(FXCollections.observableArrayList(ctx.warehouseService.getAllWarehouses()));
                } catch (SQLException ex) {
                    Dialogs.error("خطا در بارگذاری انبارها: " + ex.getMessage());
                }
            }
        });

        tab.setContent(root);
        return tab;
    }

    // --- گزارش مجوزها ---
    private Tab buildPermissionsReportTab() {
        Tab tab = new Tab("گزارش مجوزها");
        tab.setClosable(false);

        TableView<Permission> pendingTable = new TableView<>();
        TableView<Permission> completedTable = new TableView<>();
        ObservableList<Permission> pendingData = FXCollections.observableArrayList();
        ObservableList<Permission> completedData = FXCollections.observableArrayList();

        setupPermissionColumns(pendingTable);
        setupPermissionColumns(completedTable);
        pendingTable.setItems(pendingData);
        completedTable.setItems(completedData);

        Button refreshBtn = new Button("بازخوانی گزارش");
        refreshBtn.setOnAction(e -> {
            try {
                var report = ctx.reportService.getPermissionsReport();
                pendingData.setAll(report.getPending());
                completedData.setAll(report.getCompleted());
            } catch (SQLException ex) {
                Dialogs.error("خطا: " + ex.getMessage());
            }
        });

        VBox root = new VBox(10, refreshBtn,
                new Label("مجوزهای در انتظار (ISSUED):"), pendingTable,
                new Label("مجوزهای انجام‌شده (DONE):"), completedTable);
        root.setPadding(new Insets(10));
        tab.setContent(root);
        return tab;
    }

    private void setupPermissionColumns(TableView<Permission> table) {
        TableColumn<Permission, Number> idCol = new TableColumn<>("شناسه");
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getPermissionId()));
        TableColumn<Permission, String> typeCol = new TableColumn<>("نوع");
        typeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getType().toString()));
        TableColumn<Permission, String> titleCol = new TableColumn<>("عنوان");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Permission, Number> qtyCol = new TableColumn<>("تعداد");
        qtyCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantity()));
        TableColumn<Permission, String> dateCol = new TableColumn<>("تاریخ");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("permissionDate"));
        table.getColumns().setAll(idCol, typeCol, titleCol, qtyCol, dateCol);
        table.setPrefHeight(180);
    }
}

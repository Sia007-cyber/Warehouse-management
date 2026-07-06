package ir.university.warehouse.gui.tabs;

import ir.university.warehouse.dto.InventoryStatusDTO;
import ir.university.warehouse.gui.AppContext;
import ir.university.warehouse.gui.Dialogs;
import ir.university.warehouse.model.Warehouse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.sql.SQLException;

public class InventoryTab extends Tab {

    private final AppContext ctx;
    private final TableView<InventoryStatusDTO> table = new TableView<>();
    private final ObservableList<InventoryStatusDTO> data = FXCollections.observableArrayList();
    private final ComboBox<Warehouse> warehouseCombo = new ComboBox<>();

    public InventoryTab(AppContext ctx) {
        super("موجودی");
        this.ctx = ctx;
        setClosable(false);
        setContent(buildContent());
        refresh();
    }

    private BorderPane buildContent() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        TableColumn<InventoryStatusDTO, String> warehouseCol = new TableColumn<>("انبار");
        warehouseCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getWarehouseName()));
        TableColumn<InventoryStatusDTO, String> codeCol = new TableColumn<>("کد کالا");
        codeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getItemCode()));
        TableColumn<InventoryStatusDTO, String> itemCol = new TableColumn<>("کالا");
        itemCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getItemName()));
        TableColumn<InventoryStatusDTO, Number> realCol = new TableColumn<>("موجودی واقعی");
        realCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getRealStock()));
        TableColumn<InventoryStatusDTO, Number> incomingCol = new TableColumn<>("در راه");
        incomingCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIncomingStock()));
        TableColumn<InventoryStatusDTO, Number> reservedCol = new TableColumn<>("رزرو خروج");
        reservedCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getReservedStock()));
        TableColumn<InventoryStatusDTO, Number> availableCol = new TableColumn<>("قابل خروج");
        availableCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getAvailableForExit()));

        table.getColumns().setAll(warehouseCol, codeCol, itemCol, realCol, incomingCol, reservedCol, availableCol);
        table.setItems(data);

        warehouseCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Warehouse w) { return w == null ? "همه‌ی انبارها" : w.getName(); }
            @Override public Warehouse fromString(String s) { return null; }
        });
        warehouseCombo.getItems().add(null); // گزینه "همه"
        Button loadBtn = new Button("نمایش موجودی");
        loadBtn.setOnAction(e -> loadData());

        HBox top = new HBox(8, new Label("فیلتر انبار:"), warehouseCombo, loadBtn);
        top.setPadding(new Insets(0, 0, 10, 0));

        root.setTop(top);
        root.setCenter(table);
        return root;
    }

    private void loadData() {
        try {
            Warehouse selected = warehouseCombo.getSelectionModel().getSelectedItem();
            if (selected == null) {
                data.setAll(ctx.reportService.getInventoryStatus());
            } else {
                data.setAll(ctx.reportService.getInventoryStatusByWarehouse(selected.getWarehouseId()));
            }
        } catch (SQLException e) {
            Dialogs.error("خطا در بارگذاری موجودی: " + e.getMessage());
        }
    }

    private void refresh() {
        try {
            var warehouses = ctx.warehouseService.getAllWarehouses();
            warehouseCombo.getItems().setAll(warehouses);
            warehouseCombo.getItems().add(0, null);
            loadData();
        } catch (SQLException e) {
            Dialogs.error("خطا در بارگذاری انبارها: " + e.getMessage());
        }
    }
}

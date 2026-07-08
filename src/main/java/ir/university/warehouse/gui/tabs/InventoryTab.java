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

import static ir.university.warehouse.gui.I18n.t;

public class InventoryTab extends Tab {

    private final AppContext ctx;
    private final TableView<InventoryStatusDTO> table = new TableView<>();
    private final ObservableList<InventoryStatusDTO> data = FXCollections.observableArrayList();
    private final ComboBox<Warehouse> warehouseCombo = new ComboBox<>();

    public InventoryTab(AppContext ctx) {
        super(t("tab.inventory"));
        this.ctx = ctx;
        setClosable(false);
        setContent(buildContent());
        refresh();
    }

    private BorderPane buildContent() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        TableColumn<InventoryStatusDTO, String> warehouseCol = new TableColumn<>(t("report.column.warehouse"));
        warehouseCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getWarehouseName()));
        TableColumn<InventoryStatusDTO, String> codeCol = new TableColumn<>(t("item.column.code"));
        codeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getItemCode()));
        TableColumn<InventoryStatusDTO, String> itemCol = new TableColumn<>(t("report.column.item"));
        itemCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getItemName()));
        TableColumn<InventoryStatusDTO, Number> realCol = new TableColumn<>(t("inventory.column.real"));
        realCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getRealStock()));
        TableColumn<InventoryStatusDTO, Number> incomingCol = new TableColumn<>(t("inventory.column.incoming"));
        incomingCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIncomingStock()));
        TableColumn<InventoryStatusDTO, Number> reservedCol = new TableColumn<>(t("inventory.column.reserved"));
        reservedCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getReservedStock()));
        TableColumn<InventoryStatusDTO, Number> availableCol = new TableColumn<>(t("inventory.column.available"));
        availableCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getAvailableForExit()));

        table.getColumns().setAll(warehouseCol, codeCol, itemCol, realCol, incomingCol, reservedCol, availableCol);
        table.setItems(data);

        warehouseCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Warehouse w) { return w == null ? t("inventory.filter.all") : w.getName(); }
            @Override public Warehouse fromString(String s) { return null; }
        });
        warehouseCombo.getItems().add(null); // گزینه "همه"
        Button loadBtn = new Button(t("inventory.button.show"));
        loadBtn.setOnAction(e -> loadData());

        HBox top = new HBox(8, new Label(t("inventory.filter.warehouse")), warehouseCombo, loadBtn);
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
            Dialogs.error(t("inventory.load.error") + e.getMessage());
        }
    }

    private void refresh() {
        try {
            var warehouses = ctx.warehouseService.getAllWarehouses();
            warehouseCombo.getItems().setAll(warehouses);
            warehouseCombo.getItems().add(0, null);
            loadData();
        } catch (SQLException e) {
            Dialogs.error(t("inventory.warehouses.error") + e.getMessage());
        }
    }
}

package ir.university.warehouse.gui.tabs;

import ir.university.warehouse.gui.AppContext;
import ir.university.warehouse.gui.Dialogs;
import ir.university.warehouse.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static ir.university.warehouse.gui.I18n.t;

public class PermissionTab extends Tab {

    private final AppContext ctx;
    private final TableView<Permission> table = new TableView<>();
    private final ObservableList<Permission> data = FXCollections.observableArrayList();
    private final ObservableList<Warehouse> warehouses = FXCollections.observableArrayList();
    private final ObservableList<Item> items = FXCollections.observableArrayList();

    private final ComboBox<PermissionType> typeCombo = new ComboBox<>(FXCollections.observableArrayList(PermissionType.values()));
    private final ComboBox<Warehouse> warehouseCombo = new ComboBox<>();
    private final ComboBox<Item> itemCombo = new ComboBox<>();
    private final TextField quantityField = new TextField();
    private final TextField unitPriceField = new TextField();
    private final TextField titleField = new TextField();
    private final TextField descField = new TextField();
    private final DatePicker datePicker = new DatePicker(LocalDate.now());
    private final Label balanceLabel = new Label();

    public PermissionTab(AppContext ctx) {
        super(t("tab.permissions"));
        this.ctx = ctx;
        setClosable(false);
        setContent(buildContent());
        refresh();
    }

    private BorderPane buildContent() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        TableColumn<Permission, Number> idCol = new TableColumn<>(t("column.id"));
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getPermissionId()));
        TableColumn<Permission, String> typeCol = new TableColumn<>(t("permission.column.type"));
        typeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getType().toString()));
        TableColumn<Permission, String> warehouseCol = new TableColumn<>(t("permission.column.warehouse"));
        warehouseCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(warehouseName(c.getValue().getWarehouseId())));
        TableColumn<Permission, String> itemCol = new TableColumn<>(t("permission.column.item"));
        itemCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(itemName(c.getValue().getItemId())));
        TableColumn<Permission, Number> qtyCol = new TableColumn<>(t("permission.column.quantity"));
        qtyCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantity()));
        TableColumn<Permission, Number> priceCol = new TableColumn<>(t("permission.column.unitprice"));
        priceCol.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getUnitPrice()));
        TableColumn<Permission, String> titleCol = new TableColumn<>(t("permission.column.title"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Permission, String> statusCol = new TableColumn<>(t("permission.column.status"));
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus().toString()));
        TableColumn<Permission, String> dateCol = new TableColumn<>(t("permission.column.date"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("permissionDate"));

        table.getColumns().setAll(idCol, typeCol, warehouseCol, itemCol, qtyCol, priceCol, titleCol, statusCol, dateCol);
        table.setItems(data);

        GridPane form = new GridPane();
        form.setHgap(6);
        form.setVgap(6);
        form.setPadding(new Insets(0, 0, 0, 12));
        int r = 0;
        form.add(new Label(t("permission.field.type")), 0, r);
        form.add(typeCombo, 1, r++);
        form.add(new Label(t("permission.field.warehouse")), 0, r);
        form.add(warehouseCombo, 1, r++);
        form.add(new Label(t("permission.field.item")), 0, r);
        form.add(itemCombo, 1, r++);
        form.add(new Label(t("permission.field.quantity")), 0, r);
        form.add(quantityField, 1, r++);
        form.add(new Label(t("permission.field.unitprice")), 0, r);
        form.add(unitPriceField, 1, r++);
        form.add(new Label(t("permission.field.title")), 0, r);
        form.add(titleField, 1, r++);
        form.add(new Label(t("permission.field.desc")), 0, r);
        form.add(descField, 1, r++);
        form.add(new Label(t("permission.field.date")), 0, r);
        form.add(datePicker, 1, r++);

        warehouseCombo.setConverter(nameConverter(Warehouse::getName));
        itemCombo.setConverter(nameConverter(Item::getName));
        warehouseCombo.setItems(warehouses);
        itemCombo.setItems(items);

        Button issueBtn = new Button(t("permission.button.issue"));
        issueBtn.setOnAction(e -> onIssue());
        Button confirmBtn = new Button(t("permission.button.confirm"));
        confirmBtn.setOnAction(e -> onConfirm());
        Button refreshBtn = new Button(t("button.refresh"));
        refreshBtn.setOnAction(e -> refresh());
        HBox buttons = new HBox(6, issueBtn, confirmBtn, refreshBtn);

        balanceLabel.setStyle("-fx-font-weight: bold;");

        VBox side = new VBox(10, new Label(t("permission.form.title")), form, buttons,
                new Separator(), balanceLabel);
        side.setPrefWidth(340);

        root.setCenter(table);
        root.setRight(side);
        return root;
    }

    private <T> javafx.util.StringConverter<T> nameConverter(java.util.function.Function<T, String> nameFn) {
        return new javafx.util.StringConverter<>() {
            @Override public String toString(T t) { return t == null ? "" : nameFn.apply(t); }
            @Override public T fromString(String s) { return null; }
        };
    }

    private void onIssue() {
        try {
            PermissionType type = typeCombo.getSelectionModel().getSelectedItem();
            Warehouse warehouse = warehouseCombo.getSelectionModel().getSelectedItem();
            Item item = itemCombo.getSelectionModel().getSelectedItem();
            if (type == null || warehouse == null || item == null) {
                throw new IllegalArgumentException(t("permission.fields.required"));
            }
            int quantity = Integer.parseInt(quantityField.getText().trim());
            double unitPrice = Double.parseDouble(unitPriceField.getText().trim());
            String date = datePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);

            Permission p = ctx.permissionService.issuePermission(type, warehouse.getWarehouseId(), item.getItemId(),
                    quantity, unitPrice, titleField.getText(), descField.getText(), date);
            data.add(p);
            clearForm();
            refreshBalance();
        } catch (NumberFormatException e) {
            Dialogs.error(t("permission.number.error"));
        } catch (Exception e) {
            Dialogs.error(e.getMessage());
        }
    }

    private void onConfirm() {
        Permission selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Dialogs.error(t("permission.select.first"));
            return;
        }
        if (selected.getStatus() != PermissionStatus.ISSUED) {
            Dialogs.error(t("permission.already.confirmed"));
            return;
        }
        try {
            Permission confirmed = ctx.permissionService.confirmPermission(selected.getPermissionId());
            int idx = data.indexOf(selected);
            data.set(idx, confirmed);
            table.refresh();
            refreshBalance();
        } catch (Exception e) {
            Dialogs.error(e.getMessage());
        }
    }

    private void clearForm() {
        quantityField.clear();
        unitPriceField.clear();
        titleField.clear();
        descField.clear();
        datePicker.setValue(LocalDate.now());
    }

    private String warehouseName(int id) {
        return warehouses.stream().filter(w -> w.getWarehouseId() == id)
                .findFirst().map(Warehouse::getName).orElse("#" + id);
    }

    private String itemName(int id) {
        return items.stream().filter(i -> i.getItemId() == id)
                .findFirst().map(Item::getName).orElse("#" + id);
    }

    private void refreshBalance() {
        try {
            balanceLabel.setText(t("permission.balance.label") + ctx.accountingService.getBalance());
        } catch (SQLException e) {
            Dialogs.error(t("permission.balance.error") + e.getMessage());
        }
    }

    private void refresh() {
        try {
            warehouses.setAll(ctx.warehouseService.getAllWarehouses());
            items.setAll(ctx.itemService.getAllItems());
            data.setAll(ctx.permissionDAO.findAll());
            table.refresh();
            refreshBalance();
        } catch (SQLException e) {
            Dialogs.error(t("permission.load.error") + e.getMessage());
        }
    }
}

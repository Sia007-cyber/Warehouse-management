package ir.university.warehouse.gui.tabs;

import ir.university.warehouse.gui.AppContext;
import ir.university.warehouse.gui.Dialogs;
import ir.university.warehouse.model.Category;
import ir.university.warehouse.model.Warehouse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class WarehouseTab extends Tab {

    private final AppContext ctx;
    private final TableView<Warehouse> table = new TableView<>();
    private final ObservableList<Warehouse> data = FXCollections.observableArrayList();

    private final TextField nameField = new TextField();
    private final TextField addressField = new TextField();
    private final TextField capacityField = new TextField();

    private final ListView<Category> categoryList = new ListView<>();
    private final Set<Integer> checkedCategoryIds = new HashSet<>();
    private Warehouse selectedWarehouse;

    public WarehouseTab(AppContext ctx) {
        super("انبارها");
        this.ctx = ctx;
        setClosable(false);
        setContent(buildContent());
        refreshTable();
        refreshCategoryList();
    }

    private BorderPane buildContent() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        buildTable();
        root.setCenter(table);
        root.setRight(buildFormAndCategoryPanel());
        return root;
    }

    private void buildTable() {
        TableColumn<Warehouse, Number> idCol = new TableColumn<>("شناسه");
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getWarehouseId()));

        TableColumn<Warehouse, String> nameCol = new TableColumn<>("نام");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Warehouse, String> addressCol = new TableColumn<>("آدرس");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Warehouse, Number> capacityCol = new TableColumn<>("ظرفیت");
        capacityCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getCapacity()));

        table.getColumns().setAll(idCol, nameCol, addressCol, capacityCol);
        table.setItems(data);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> onRowSelected(newV));
        table.setPrefWidth(500);
    }

    private VBox buildFormAndCategoryPanel() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(0, 0, 0, 12));
        box.setPrefWidth(320);

        GridPane form = new GridPane();
        form.setHgap(6);
        form.setVgap(6);
        form.add(new Label("نام:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("آدرس:"), 0, 1);
        form.add(addressField, 1, 1);
        form.add(new Label("ظرفیت:"), 0, 2);
        form.add(capacityField, 1, 2);

        Button addBtn = new Button("افزودن");
        Button updateBtn = new Button("ویرایش");
        Button deleteBtn = new Button("حذف");
        Button clearBtn = new Button("پاک کردن فرم");
        addBtn.setOnAction(e -> onAdd());
        updateBtn.setOnAction(e -> onUpdate());
        deleteBtn.setOnAction(e -> onDelete());
        clearBtn.setOnAction(e -> clearForm());

        HBox buttons = new HBox(6, addBtn, updateBtn, deleteBtn, clearBtn);

        Label categoryLabel = new Label("دسته‌بندی‌های مجاز این انبار:");
        categoryList.setCellFactory(view -> new CheckBoxListCell());
        categoryList.setPrefHeight(220);

        Button saveCategoriesBtn = new Button("ذخیره دسته‌های مجاز");
        saveCategoriesBtn.setOnAction(e -> onSaveAllowedCategories());

        box.getChildren().addAll(new Label("مشخصات انبار"), form, buttons,
                new Separator(), categoryLabel, categoryList, saveCategoriesBtn);
        return box;
    }

    // --- سلول چک‌باکس‌دار برای نمایش/انتخاب دسته‌بندی‌های مجاز ---
    private class CheckBoxListCell extends ListCell<Category> {
        private final CheckBox checkBox = new CheckBox();

        CheckBoxListCell() {
            checkBox.selectedProperty().addListener((obs, oldV, newV) -> {
                if (getItem() == null) return;
                if (newV) checkedCategoryIds.add(getItem().getCategoryId());
                else checkedCategoryIds.remove(getItem().getCategoryId());
            });
        }

        @Override
        protected void updateItem(Category item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                checkBox.setSelected(checkedCategoryIds.contains(item.getCategoryId()));
                checkBox.setText(item.getName());
                setGraphic(checkBox);
                setText(null);
            }
        }
    }

    private void refreshCategoryList() {
        try {
            categoryList.setItems(FXCollections.observableArrayList(ctx.categoryService.getAllCategories()));
            loadAllowedCategoriesForSelected();
        } catch (SQLException e) {
            Dialogs.error("خطا در بارگذاری دسته‌بندی‌ها: " + e.getMessage());
        }
    }

    private void loadAllowedCategoriesForSelected() {
        checkedCategoryIds.clear();
        if (selectedWarehouse != null) {
            try {
                checkedCategoryIds.addAll(ctx.warehouseService.getAllowedCategoryIds(selectedWarehouse.getWarehouseId()));
            } catch (SQLException e) {
                Dialogs.error("خطا در بارگذاری دسته‌های مجاز: " + e.getMessage());
            }
        }
        categoryList.refresh();
    }

    private void onRowSelected(Warehouse warehouse) {
        selectedWarehouse = warehouse;
        if (warehouse == null) {
            clearForm();
            return;
        }
        nameField.setText(warehouse.getName());
        addressField.setText(warehouse.getAddress());
        capacityField.setText(String.valueOf(warehouse.getCapacity()));
        loadAllowedCategoriesForSelected();
    }

    private void onAdd() {
        try {
            int capacity = parseCapacity();
            Warehouse w = ctx.warehouseService.createWarehouse(nameField.getText(), addressField.getText(), capacity);
            data.add(w);
            clearForm();
        } catch (Exception e) {
            Dialogs.error(e.getMessage());
        }
    }

    private void onUpdate() {
        if (selectedWarehouse == null) {
            Dialogs.error("ابتدا یک انبار را از جدول انتخاب کنید.");
            return;
        }
        try {
            int capacity = parseCapacity();
            Warehouse updated = ctx.warehouseService.updateWarehouse(
                    selectedWarehouse.getWarehouseId(), nameField.getText(), addressField.getText(), capacity);
            int idx = data.indexOf(selectedWarehouse);
            data.set(idx, updated);
            table.getSelectionModel().select(idx);
        } catch (Exception e) {
            Dialogs.error(e.getMessage());
        }
    }

    private void onDelete() {
        if (selectedWarehouse == null) {
            Dialogs.error("ابتدا یک انبار را از جدول انتخاب کنید.");
            return;
        }
        if (!Dialogs.confirm("حذف انبار «" + selectedWarehouse.getName() + "» انجام شود؟")) return;
        try {
            ctx.warehouseService.deleteWarehouse(selectedWarehouse.getWarehouseId());
            data.remove(selectedWarehouse);
            clearForm();
        } catch (Exception e) {
            Dialogs.error("حذف ناموفق بود (احتمالاً این انبار در مجوزها استفاده شده): " + e.getMessage());
        }
    }

    private void onSaveAllowedCategories() {
        if (selectedWarehouse == null) {
            Dialogs.error("ابتدا یک انبار را از جدول انتخاب کنید.");
            return;
        }
        try {
            Set<Integer> desired = new HashSet<>(checkedCategoryIds);
            Set<Integer> current = new HashSet<>(ctx.warehouseService.getAllowedCategoryIds(selectedWarehouse.getWarehouseId()));

            for (Integer id : desired) {
                if (!current.contains(id)) {
                    ctx.warehouseService.allowCategory(selectedWarehouse.getWarehouseId(), id);
                }
            }
            for (Integer id : current) {
                if (!desired.contains(id)) {
                    ctx.warehouseService.disallowCategory(selectedWarehouse.getWarehouseId(), id);
                }
            }
            Dialogs.info("دسته‌های مجاز به‌روزرسانی شد.");
        } catch (Exception e) {
            Dialogs.error(e.getMessage());
        }
    }

    private int parseCapacity() {
        try {
            return Integer.parseInt(capacityField.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ظرفیت باید یک عدد صحیح باشد.");
        }
    }

    private void clearForm() {
        selectedWarehouse = null;
        nameField.clear();
        addressField.clear();
        capacityField.clear();
        table.getSelectionModel().clearSelection();
        loadAllowedCategoriesForSelected();
    }

    private void refreshTable() {
        try {
            data.setAll(ctx.warehouseService.getAllWarehouses());
        } catch (SQLException e) {
            Dialogs.error("خطا در بارگذاری انبارها: " + e.getMessage());
        }
    }
}

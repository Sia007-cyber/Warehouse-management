package ir.university.warehouse.gui.tabs;

import ir.university.warehouse.gui.AppContext;
import ir.university.warehouse.gui.Dialogs;
import ir.university.warehouse.model.Category;
import ir.university.warehouse.model.Item;
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

import static ir.university.warehouse.gui.I18n.t;

public class ItemTab extends Tab {

    private final AppContext ctx;
    private final TableView<Item> table = new TableView<>();
    private final ObservableList<Item> data = FXCollections.observableArrayList();
    private final ObservableList<Category> categories = FXCollections.observableArrayList();

    private final TextField codeField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField descField = new TextField();
    private final ComboBox<Category> categoryCombo = new ComboBox<>();
    private Item selected;

    public ItemTab(AppContext ctx) {
        super(t("tab.items"));
        this.ctx = ctx;
        setClosable(false);
        setContent(buildContent());
        refresh();
    }

    private BorderPane buildContent() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        TableColumn<Item, Number> idCol = new TableColumn<>(t("column.id"));
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getItemId()));
        TableColumn<Item, String> codeCol = new TableColumn<>(t("item.column.code"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        TableColumn<Item, String> nameCol = new TableColumn<>(t("column.name"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Item, String> categoryCol = new TableColumn<>(t("item.column.category"));
        categoryCol.setCellValueFactory(c -> {
            String label = categories.stream()
                    .filter(cat -> cat.getCategoryId() == c.getValue().getCategoryId())
                    .findFirst().map(Category::getName).orElse("#" + c.getValue().getCategoryId());
            return new javafx.beans.property.SimpleStringProperty(label);
        });
        table.getColumns().setAll(idCol, codeCol, nameCol, categoryCol);
        table.setItems(data);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> onRowSelected(newV));

        GridPane form = new GridPane();
        form.setHgap(6);
        form.setVgap(6);
        form.setPadding(new Insets(0, 0, 0, 12));
        form.add(new Label(t("item.field.code")), 0, 0);
        form.add(codeField, 1, 0);
        form.add(new Label(t("item.field.name")), 0, 1);
        form.add(nameField, 1, 1);
        form.add(new Label(t("item.field.desc")), 0, 2);
        form.add(descField, 1, 2);
        form.add(new Label(t("item.field.category")), 0, 3);
        categoryCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Category c) { return c == null ? "" : c.getName(); }
            @Override public Category fromString(String s) { return null; }
        });
        form.add(categoryCombo, 1, 3);

        Button addBtn = new Button(t("button.add"));
        Button updateBtn = new Button(t("button.update"));
        Button deleteBtn = new Button(t("button.delete"));
        Button clearBtn = new Button(t("button.clear"));
        addBtn.setOnAction(e -> onAdd());
        updateBtn.setOnAction(e -> onUpdate());
        deleteBtn.setOnAction(e -> onDelete());
        clearBtn.setOnAction(e -> clearForm());
        HBox buttons = new HBox(6, addBtn, updateBtn, deleteBtn, clearBtn);

        VBox side = new VBox(10, new Label(t("item.form.title")), form, buttons);
        side.setPrefWidth(340);

        root.setCenter(table);
        root.setRight(side);
        return root;
    }

    private void onRowSelected(Item item) {
        selected = item;
        if (item == null) {
            clearForm();
            return;
        }
        codeField.setText(item.getItemCode());
        nameField.setText(item.getName());
        descField.setText(item.getDescription());
        categories.stream().filter(c -> c.getCategoryId() == item.getCategoryId())
                .findFirst().ifPresent(categoryCombo.getSelectionModel()::select);
    }

    private void onAdd() {
        try {
            int categoryId = requireCategory();
            Item item = ctx.itemService.createItem(codeField.getText(), nameField.getText(), descField.getText(), categoryId);
            data.add(item);
            clearForm();
        } catch (Exception e) {
            Dialogs.error(e.getMessage());
        }
    }

    private void onUpdate() {
        if (selected == null) {
            Dialogs.error(t("item.select.first"));
            return;
        }
        try {
            int categoryId = requireCategory();
            Item updated = ctx.itemService.updateItem(selected.getItemId(), codeField.getText(),
                    nameField.getText(), descField.getText(), categoryId);
            int idx = data.indexOf(selected);
            data.set(idx, updated);
            table.getSelectionModel().select(idx);
        } catch (Exception e) {
            Dialogs.error(e.getMessage());
        }
    }

    private void onDelete() {
        if (selected == null) {
            Dialogs.error(t("item.select.first"));
            return;
        }
        if (!Dialogs.confirm(t("item.confirm.delete"))) return;
        try {
            ctx.itemService.deleteItem(selected.getItemId());
            data.remove(selected);
            clearForm();
        } catch (Exception e) {
            Dialogs.error(t("item.delete.failed") + e.getMessage());
        }
    }

    private int requireCategory() {
        Category c = categoryCombo.getSelectionModel().getSelectedItem();
        if (c == null) throw new IllegalArgumentException(t("item.category.required"));
        return c.getCategoryId();
    }

    private void clearForm() {
        selected = null;
        codeField.clear();
        nameField.clear();
        descField.clear();
        categoryCombo.getSelectionModel().select(null);
        table.getSelectionModel().clearSelection();
    }

    private void refresh() {
        try {
            categories.setAll(ctx.categoryService.getAllCategories());
            categoryCombo.setItems(categories);
            data.setAll(ctx.itemService.getAllItems());
            table.refresh();
        } catch (SQLException e) {
            Dialogs.error(t("item.load.error") + e.getMessage());
        }
    }
}

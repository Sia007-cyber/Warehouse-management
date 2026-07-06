package ir.university.warehouse.gui.tabs;

import ir.university.warehouse.gui.AppContext;
import ir.university.warehouse.gui.Dialogs;
import ir.university.warehouse.model.Category;
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

public class CategoryTab extends Tab {

    private final AppContext ctx;
    private final TableView<Category> table = new TableView<>();
    private final ObservableList<Category> data = FXCollections.observableArrayList();

    private final TextField nameField = new TextField();
    private final ComboBox<Category> parentCombo = new ComboBox<>();
    private Category selected;

    public CategoryTab(AppContext ctx) {
        super("دسته‌بندی‌ها");
        this.ctx = ctx;
        setClosable(false);
        setContent(buildContent());
        refresh();
    }

    private BorderPane buildContent() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        TableColumn<Category, Number> idCol = new TableColumn<>("شناسه");
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getCategoryId()));
        TableColumn<Category, String> nameCol = new TableColumn<>("نام");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Category, String> parentCol = new TableColumn<>("والد");
        parentCol.setCellValueFactory(c -> {
            Integer pid = c.getValue().getParentId();
            String label = "-";
            if (pid != null) {
                label = data.stream().filter(cat -> cat.getCategoryId() == pid)
                        .findFirst().map(Category::getName).orElse("#" + pid);
            }
            return new javafx.beans.property.SimpleStringProperty(label);
        });
        table.getColumns().setAll(idCol, nameCol, parentCol);
        table.setItems(data);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> onRowSelected(newV));

        GridPane form = new GridPane();
        form.setHgap(6);
        form.setVgap(6);
        form.setPadding(new Insets(0, 0, 0, 12));
        form.add(new Label("نام دسته‌بندی:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("دسته‌ی والد (اختیاری):"), 0, 1);
        parentCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Category c) { return c == null ? "(بدون والد)" : c.getName(); }
            @Override public Category fromString(String s) { return null; }
        });
        form.add(parentCombo, 1, 1);

        Button addBtn = new Button("افزودن");
        Button updateBtn = new Button("ویرایش");
        Button deleteBtn = new Button("حذف");
        Button clearBtn = new Button("پاک کردن فرم");
        addBtn.setOnAction(e -> onAdd());
        updateBtn.setOnAction(e -> onUpdate());
        deleteBtn.setOnAction(e -> onDelete());
        clearBtn.setOnAction(e -> clearForm());
        HBox buttons = new HBox(6, addBtn, updateBtn, deleteBtn, clearBtn);

        VBox side = new VBox(10, new Label("مشخصات دسته‌بندی"), form, buttons);
        side.setPrefWidth(320);

        root.setCenter(table);
        root.setRight(side);
        return root;
    }

    private void onRowSelected(Category category) {
        selected = category;
        if (category == null) {
            clearForm();
            return;
        }
        nameField.setText(category.getName());
        if (category.getParentId() == null) {
            parentCombo.getSelectionModel().select(null);
        } else {
            data.stream().filter(c -> c.getCategoryId() == category.getParentId())
                    .findFirst().ifPresent(parentCombo.getSelectionModel()::select);
        }
    }

    private void onAdd() {
        try {
            Integer parentId = selectedParentId();
            Category c = ctx.categoryService.createCategory(nameField.getText(), parentId);
            data.add(c);
            refreshParentCombo();
            clearForm();
        } catch (Exception e) {
            Dialogs.error(e.getMessage());
        }
    }

    private void onUpdate() {
        if (selected == null) {
            Dialogs.error("ابتدا یک دسته‌بندی را از جدول انتخاب کنید.");
            return;
        }
        try {
            Integer parentId = selectedParentId();
            Category updated = ctx.categoryService.updateCategory(selected.getCategoryId(), nameField.getText(), parentId);
            int idx = data.indexOf(selected);
            data.set(idx, updated);
            table.refresh();
            refreshParentCombo();
            table.getSelectionModel().select(idx);
        } catch (Exception e) {
            Dialogs.error(e.getMessage());
        }
    }

    private void onDelete() {
        if (selected == null) {
            Dialogs.error("ابتدا یک دسته‌بندی را از جدول انتخاب کنید.");
            return;
        }
        if (!Dialogs.confirm("حذف دسته‌بندی «" + selected.getName() + "» انجام شود؟")) return;
        try {
            ctx.categoryService.deleteCategory(selected.getCategoryId());
            data.remove(selected);
            refreshParentCombo();
            clearForm();
        } catch (Exception e) {
            Dialogs.error("حذف ناموفق بود (احتمالاً کالایی به این دسته وابسته است): " + e.getMessage());
        }
    }

    private Integer selectedParentId() {
        Category p = parentCombo.getSelectionModel().getSelectedItem();
        return p == null ? null : p.getCategoryId();
    }

    private void clearForm() {
        selected = null;
        nameField.clear();
        parentCombo.getSelectionModel().select(null);
        table.getSelectionModel().clearSelection();
    }

    private void refreshParentCombo() {
        parentCombo.setItems(FXCollections.observableArrayList(data));
    }

    private void refresh() {
        try {
            data.setAll(ctx.categoryService.getAllCategories());
            refreshParentCombo();
        } catch (SQLException e) {
            Dialogs.error("خطا در بارگذاری دسته‌بندی‌ها: " + e.getMessage());
        }
    }
}

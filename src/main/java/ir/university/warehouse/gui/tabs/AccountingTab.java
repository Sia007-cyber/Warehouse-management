package ir.university.warehouse.gui.tabs;

import ir.university.warehouse.gui.AppContext;
import ir.university.warehouse.gui.Dialogs;
import ir.university.warehouse.model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

import static ir.university.warehouse.gui.I18n.t;

public class AccountingTab extends Tab {

    private final AppContext ctx;
    private final Label balanceLabel = new Label();
    private final TextField amountField = new TextField();
    private final TableView<Transaction> table = new TableView<>();
    private final ObservableList<Transaction> data = FXCollections.observableArrayList();

    public AccountingTab(AppContext ctx) {
        super(t("tab.accounting"));
        this.ctx = ctx;
        setClosable(false);
        setContent(buildContent());
        refresh();
    }

    private BorderPane buildContent() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        balanceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        amountField.setPromptText(t("accounting.amount.prompt"));
        Button depositBtn = new Button(t("accounting.button.deposit"));
        depositBtn.setOnAction(e -> onDeposit());

        HBox depositBox = new HBox(8, new Label(t("accounting.field.amount")), amountField, depositBtn);
        depositBox.setPadding(new Insets(10, 0, 10, 0));

        TableColumn<Transaction, Number> idCol = new TableColumn<>(t("column.id"));
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getTransactionId()));
        TableColumn<Transaction, Number> permCol = new TableColumn<>(t("accounting.column.permission"));
        permCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getPermissionId()));
        TableColumn<Transaction, String> typeCol = new TableColumn<>(t("accounting.column.type"));
        typeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getType().toString()));
        TableColumn<Transaction, Number> amountCol = new TableColumn<>(t("accounting.column.amount"));
        amountCol.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getAmount()));
        TableColumn<Transaction, String> dateCol = new TableColumn<>(t("accounting.column.date"));
        dateCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTransactionDate()));
        table.getColumns().setAll(idCol, permCol, typeCol, amountCol, dateCol);
        table.setItems(data);

        VBox top = new VBox(6, balanceLabel, depositBox, new Label(t("accounting.history.label")));
        root.setTop(top);
        root.setCenter(table);
        return root;
    }

    private void onDeposit() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) throw new IllegalArgumentException(t("accounting.amount.positive"));
            ctx.accountingService.deposit(amount);
            amountField.clear();
            refresh();
        } catch (NumberFormatException e) {
            Dialogs.error(t("accounting.amount.notnumber"));
        } catch (Exception e) {
            Dialogs.error(e.getMessage());
        }
    }

    private void refresh() {
        try {
            balanceLabel.setText(t("accounting.balance.label") + ctx.accountingService.getBalance());
            data.setAll(ctx.transactionDAO.findAll());
        } catch (SQLException e) {
            Dialogs.error(t("accounting.load.error") + e.getMessage());
        }
    }
}

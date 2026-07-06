package ir.university.warehouse.gui;

import ir.university.warehouse.db.DatabaseConnection;
import ir.university.warehouse.gui.tabs.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class WarehouseApp extends Application {

    @Override
    public void start(Stage stage) {
        AppContext ctx = new AppContext();

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                new WarehouseTab(ctx),
                new CategoryTab(ctx),
                new ItemTab(ctx),
                new PermissionTab(ctx),
                new InventoryTab(ctx),
                new AccountingTab(ctx),
                new ReportTab(ctx)
        );

        Scene scene = new Scene(tabPane, 1100, 650);
        stage.setTitle("سامانه مدیریت انبار");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> DatabaseConnection.closeConnection());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

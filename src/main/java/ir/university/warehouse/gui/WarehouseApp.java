package ir.university.warehouse.gui;

import ir.university.warehouse.db.DatabaseConnection;
import ir.university.warehouse.gui.tabs.*;
import javafx.application.Application;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class WarehouseApp extends Application {

    private Stage stage;
    private BorderPane root;
    private TabPane tabPane;
    private AppContext ctx;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.ctx = new AppContext();

        root = new BorderPane();
        root.setTop(buildToolbar());
        rebuildTabs();

        Scene scene = new Scene(root, 1150, 680);
        scene.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());

        stage.setTitle(I18n.t("app.title"));
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> DatabaseConnection.closeConnection());
        stage.show();
    }

    private HBox buildToolbar() {
        Button faBtn = new Button("فارسی");
        Button enBtn = new Button("English");
        faBtn.setOnAction(e -> switchLanguage(I18n.Lang.FA));
        enBtn.setOnAction(e -> switchLanguage(I18n.Lang.EN));

        HBox toolbar = new HBox(8, faBtn, enBtn);
        toolbar.setPadding(new Insets(8));
        return toolbar;
    }

    private void switchLanguage(I18n.Lang lang) {
        I18n.setLanguage(lang);
        stage.setTitle(I18n.t("app.title"));
        root.setTop(buildToolbar());
        rebuildTabs();
    }

    private void rebuildTabs() {
        tabPane = new TabPane();
        tabPane.getTabs().addAll(
                new WarehouseTab(ctx),
                new CategoryTab(ctx),
                new ItemTab(ctx),
                new PermissionTab(ctx),
                new InventoryTab(ctx),
                new AccountingTab(ctx),
                new ReportTab(ctx)
        );
        tabPane.setNodeOrientation(I18n.isRtl() ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
        root.setCenter(tabPane);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

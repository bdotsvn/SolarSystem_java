package com.planetsim;

import com.planetsim.controller.PlanetController;
import com.planetsim.view.ControlPanel;
import com.planetsim.view.PlanetView3D;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Entry point of the Solar System Simulation application.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Khởi tạo Giao diện 3D
        PlanetView3D view3D = new PlanetView3D(800, 600);
        
        // 2. Khởi tạo Bảng điều khiển
        ControlPanel controlPanel = new ControlPanel();
        
        // 3. Khởi tạo Controller (Nhạc trưởng)
        new PlanetController(view3D, controlPanel);

        // 4. Sắp xếp Layout co dãn linh hoạt
        BorderPane mainLayout = new BorderPane();
        
        // Dùng StackPane để bọc SubScene giúp nó có thể co dãn
        javafx.scene.layout.StackPane centerPane = new javafx.scene.layout.StackPane();
        centerPane.getChildren().add(view3D.getSubScene());
        
        // Ràng buộc kích thước SubScene theo Center Pane
        view3D.getSubScene().widthProperty().bind(centerPane.widthProperty());
        view3D.getSubScene().heightProperty().bind(centerPane.heightProperty());

        mainLayout.setCenter(centerPane);
        mainLayout.setRight(controlPanel);

        // 5. Thiết lập Stage
        Scene scene = new Scene(mainLayout, 1100, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setTitle("Solar System Simulator 3.0 - Cyber Edition");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

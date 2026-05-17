package com.planetsim.view;

import com.planetsim.model.Planet;
import com.planetsim.model.Satellite;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * UI Panel for controlling simulations and routing.
 */
public class ControlPanel extends VBox {
    private ComboBox<Planet> planetCombo;
    private ListView<Satellite> satelliteList;
    private Button btnAddSat, btnDeleteSat, btnRoute, btnResetView;
    private Button[] speedButtons;
    private ComboBox<Satellite> sourceCombo, destCombo;
    private TextField txtName, txtAlt, txtLon, txtLat, txtSignalRange;
    private TextArea logArea;

    public ControlPanel() {
        this.setPadding(new Insets(15));
        this.setSpacing(8);
        this.setPrefWidth(300);
        this.getStyleClass().add("cyber-panel");

        // 0. Tốc độ mô phỏng
        Label lblSpeed = new Label("TỐC ĐỘ MÔ PHỎNG");
        double[] speeds = {0.1, 0.2, 0.5, 1.0, 2.0};
        String[] labels = {"x0.1", "x0.2", "x0.5", "x1", "x2"};
        speedButtons = new Button[speeds.length];
        HBox speedBox = new HBox(4);
        speedBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < speeds.length; i++) {
            Button btn = new Button(labels[i]);
            btn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(btn, javafx.scene.layout.Priority.ALWAYS);
            speedButtons[i] = btn;
        }
        speedButtons[3].getStyleClass().add("speed-button-active"); // x1 là mặc định
        speedBox.getChildren().addAll(speedButtons);

        // 1. Planet Selection
        Label lblPlanet = new Label("CHỌN HÀNH TINH");
        planetCombo = new ComboBox<>();
        planetCombo.setMaxWidth(Double.MAX_VALUE);

        btnResetView = new Button("🌞 Toàn cảnh (Mặt Trời)");
        btnResetView.setMaxWidth(Double.MAX_VALUE);

        // 2. Satellite Management
        Label lblSats = new Label("DANH SÁCH VỆ TINH");
        satelliteList = new ListView<>();
        satelliteList.setPrefHeight(150);

        btnAddSat = new Button("Thêm vệ tinh");
        btnDeleteSat = new Button("Xóa vệ tinh");
        btnAddSat.setMaxWidth(Double.MAX_VALUE);
        btnDeleteSat.setMaxWidth(Double.MAX_VALUE);

        txtName = new TextField(); txtName.setPromptText("Tên vệ tinh");
        txtAlt = new TextField(); txtAlt.setPromptText("Độ cao (km)");
        txtLon = new TextField(); txtLon.setPromptText("Kinh độ (0-360)");
        txtLat = new TextField(); txtLat.setPromptText("Vĩ độ (-90..90)");
        txtSignalRange = new TextField(); txtSignalRange.setPromptText("Tầm sóng (km)");

        // 3. Routing Section
        Label lblRoute = new Label("ĐỊNH TUYẾN TIN NHẮN");
        sourceCombo = new ComboBox<>();
        destCombo = new ComboBox<>();
        sourceCombo.setPromptText("Từ vệ tinh...");
        destCombo.setPromptText("Đến vệ tinh...");
        sourceCombo.setMaxWidth(Double.MAX_VALUE);
        destCombo.setMaxWidth(Double.MAX_VALUE);

        btnRoute = new Button("TÌM ĐƯỜNG NGẮN NHẤT");
        btnRoute.getStyleClass().add("blue-button");
        btnRoute.setMaxWidth(Double.MAX_VALUE);

        // 4. Log Output
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(120);

        this.getChildren().addAll(
            lblSpeed, speedBox, new Separator(),
            lblPlanet, planetCombo, btnResetView, new Separator(),
            lblSats, satelliteList,
            new Label("Thông số vệ tinh mới:") {{ setStyle("-fx-text-fill: #888888;"); }},
            txtName, txtAlt, txtLon, txtLat, txtSignalRange,
            btnAddSat, btnDeleteSat, new Separator(),
            lblRoute, sourceCombo, destCombo, btnRoute,
            new Label("Kết quả:") {{ setStyle("-fx-text-fill: #888888;"); }}, logArea
        );
    }

    public ComboBox<Planet> getPlanetCombo() { return planetCombo; }
    public ListView<Satellite> getSatelliteList() { return satelliteList; }
    public Button getBtnAddSat() { return btnAddSat; }
    public Button getBtnDeleteSat() { return btnDeleteSat; }
    public Button getBtnRoute() { return btnRoute; }
    public Button getBtnResetView() { return btnResetView; }
    public Button[] getSpeedButtons() { return speedButtons; }
    public ComboBox<Satellite> getSourceCombo() { return sourceCombo; }
    public ComboBox<Satellite> getDestCombo() { return destCombo; }
    public TextField getTxtName() { return txtName; }
    public TextField getTxtAlt() { return txtAlt; }
    public TextField getTxtLon() { return txtLon; }
    public TextField getTxtLat() { return txtLat; }
    public TextField getTxtSignalRange() { return txtSignalRange; }
    public void appendLog(String text) { logArea.appendText(text + "\n"); }
    public void clearLog() { logArea.clear(); }
}

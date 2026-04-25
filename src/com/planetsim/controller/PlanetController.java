package com.planetsim.controller;

import com.planetsim.model.*;
import com.planetsim.utils.*;
import com.planetsim.view.*;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import java.util.List;

/**
 * Main Controller to handle logic and UI updates.
 */
public class PlanetController {
    private PlanetView3D view3D;
    private ControlPanel controlPanel;
    
    private PlanetDAO planetDAO = new PlanetDAO();
    private SatelliteDAO satelliteDAO = new SatelliteDAO();
    private LogDAO logDAO = new LogDAO();

    public PlanetController(PlanetView3D view3D, ControlPanel controlPanel) {
        this.view3D = view3D;
        this.controlPanel = controlPanel;
        
        initialize();
    }

    private void initialize() {
        // 1. Load danh sách hành tinh
        List<Planet> planets = planetDAO.getAllPlanets();
        controlPanel.getPlanetCombo().setItems(FXCollections.observableArrayList(planets));
        view3D.initSolarSystem(planets);

        // 2. Sự kiện chọn hành tinh
        controlPanel.getPlanetCombo().setOnAction(e -> {
            Planet selected = controlPanel.getPlanetCombo().getValue();
            if (selected != null) {
                view3D.focusOnPlanet(selected.getPlanetId());
                loadSatellites(selected.getPlanetId());
                controlPanel.clearLog();
                controlPanel.appendLog("Đã chọn hành tinh: " + selected.getName());
            }
        });

        // 3. Nút Toàn cảnh
        controlPanel.getBtnResetView().setOnAction(e -> {
            view3D.resetView();
            controlPanel.getPlanetCombo().setValue(null);
            controlPanel.appendLog("Quay về toàn cảnh Mặt Trời.");
        });

        // 4. Nút tốc độ
        double[] speeds = {0.1, 0.2, 0.5, 1.0, 2.0};
        javafx.scene.control.Button[] btns = controlPanel.getSpeedButtons();
        for (int i = 0; i < btns.length; i++) {
            final double speed = speeds[i];
            final int idx = i;
            btns[i].setOnAction(e -> {
                view3D.setTimeScale(speed);
                for (javafx.scene.control.Button b : btns) b.getStyleClass().remove("speed-button-active");
                btns[idx].getStyleClass().add("speed-button-active");
            });
        }

        // 5. Sự kiện định tuyến
        controlPanel.getBtnRoute().setOnAction(e -> handleRouting());

        // 6. Sự kiện thêm vệ tinh
        controlPanel.getBtnAddSat().setOnAction(e -> {
            Planet current = controlPanel.getPlanetCombo().getValue();
            if (current != null) {
                addNewSatellite(current);
            }
        });
    }

    private void loadSatellites(int planetId) {
        List<Satellite> satellites = satelliteDAO.getSatellitesByPlanet(planetId);
        controlPanel.getSatelliteList().setItems(FXCollections.observableArrayList(satellites));
        controlPanel.getSourceCombo().setItems(FXCollections.observableArrayList(satellites));
        controlPanel.getDestCombo().setItems(FXCollections.observableArrayList(satellites));
        view3D.setSatellitesForPlanet(planetId, satellites);
    }

    private void handleRouting() {
        Satellite source = controlPanel.getSourceCombo().getValue();
        Satellite dest = controlPanel.getDestCombo().getValue();

        if (source == null || dest == null) {
            showAlert("Lỗi", "Vui lòng chọn cả vệ tinh nguồn và đích!");
            return;
        }

        List<Satellite> allSats = controlPanel.getSatelliteList().getItems();
        RoutingResult result = DijkstraRouter.findShortestPath(allSats, source.getSatelliteId(), dest.getSatelliteId());

        controlPanel.appendLog("--- Kết quả định tuyến ---");
        controlPanel.appendLog(result.toString());

        if (result.isSuccess()) {
            // Lưu log vào database
            RoutingLog log = new RoutingLog();
            log.setSourceSatId(source.getSatelliteId());
            log.setDestSatId(dest.getSatelliteId());
            log.setPathDescription(result.toString());
            log.setTotalDistanceKm(result.getTotalDistance());
            logDAO.saveLog(log);
        }
    }

    private void addNewSatellite(Planet planet) {
        try {
            Satellite s = new Satellite();
            s.setPlanetId(planet.getPlanetId());
            s.setName(controlPanel.getTxtName().getText().isEmpty() ? "SAT-" + (int)(Math.random()*100) : controlPanel.getTxtName().getText());
            s.setAltitudeKm(Double.parseDouble(controlPanel.getTxtAlt().getText()));
            s.setLongitude(Double.parseDouble(controlPanel.getTxtLon().getText()));
            s.setLatitude(Double.parseDouble(controlPanel.getTxtLat().getText()));
            s.setSignalRangeKm(15000);
            s.setNatural(false);
            
            double v = PhysicsEngine.calculateOrbitalVelocity(planet.getMassKg(), planet.getRadiusKm(), s.getAltitudeKm());
            s.setOrbitalVelocity(v);

            if (satelliteDAO.addSatellite(s)) {
                loadSatellites(planet.getPlanetId());
                controlPanel.appendLog("Thêm thành công: " + s.getName() + " (v = " + String.format("%.2f", v) + " km/s)");
            }
        } catch (Exception e) {
            showAlert("Lỗi nhập liệu", "Vui lòng nhập đúng định dạng số cho tọa độ!");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

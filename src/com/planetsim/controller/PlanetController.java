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
        
        // Load vệ tinh cho tất cả hành tinh ngay từ đầu để hiển thị
        for (Planet p : planets) {
            List<Satellite> satellites = satelliteDAO.getSatellitesByPlanet(p.getPlanetId());
            view3D.setSatellitesForPlanet(p.getPlanetId(), satellites);
        }

        // 2. Sự kiện chọn hành tinh
        controlPanel.getPlanetCombo().setOnAction(e -> {
            Planet selected = controlPanel.getPlanetCombo().getValue();
            if (selected != null) {
                view3D.focusOnPlanet(selected.getPlanetId());
                loadSatellites(selected.getPlanetId());
                controlPanel.clearLog();
                controlPanel.appendLog("Đã chọn hành tinh: " + selected.getName());
                
                // Cập nhật Placeholder (gợi ý độ cao an toàn) cho txtAlt
                double minSafe = Math.max(100, selected.getRadiusKm() * 0.05);
                double maxSafe = selected.getDistanceSunKm() > 0 ? selected.getDistanceSunKm() / 100 : selected.getRadiusKm() * 1000;
                controlPanel.getTxtAlt().setPromptText(String.format("Độ cao (%.0f - %.0f km)", minSafe, maxSafe));
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
            } else {
                showAlert("Chưa chọn hành tinh", "Vui lòng chọn một hành tinh trước khi thêm vệ tinh!");
            }
        });

        // 7. Sự kiện xóa vệ tinh
        controlPanel.getBtnDeleteSat().setOnAction(e -> {
            Satellite selected = controlPanel.getSatelliteList().getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Chưa chọn vệ tinh", "Vui lòng chọn một vệ tinh trong danh sách để xóa!");
                return;
            }
            if (selected.isNatural()) {
                showAlert("Không thể xóa", "Không thể xóa vệ tinh tự nhiên (Moon)!");
                return;
            }
            if (satelliteDAO.deleteSatellite(selected.getSatelliteId())) {
                Planet current = controlPanel.getPlanetCombo().getValue();
                loadSatellites(current.getPlanetId());
                controlPanel.appendLog("Đã xóa vệ tinh: " + selected.getName());
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

        if (source.getSatelliteId() == dest.getSatelliteId()) {
            showAlert("Lỗi định tuyến", "Vệ tinh nguồn và đích phải khác nhau!");
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
            String name = controlPanel.getTxtName().getText().trim();
            String altStr = controlPanel.getTxtAlt().getText().trim();
            String lonStr = controlPanel.getTxtLon().getText().trim();
            String latStr = controlPanel.getTxtLat().getText().trim();
            String sigStr = controlPanel.getTxtSignalRange().getText().trim();

            if (name.isEmpty()) {
                showAlert("Lỗi nhập liệu", "Tên vệ tinh không được để trống!");
                return;
            }

            double altitude = Double.parseDouble(altStr);
            double longitude = Double.parseDouble(lonStr);
            double latitude = Double.parseDouble(latStr);
            double signalRange = sigStr.isEmpty() ? 15000 : Double.parseDouble(sigStr);

            // Xử lý Giới hạn Vật lý (Vận tốc vũ trụ cấp 1) - Phương án 3: Auto-Correction
            double minSafe = Math.max(100, planet.getRadiusKm() * 0.05);
            double maxSafe = planet.getDistanceSunKm() > 0 ? planet.getDistanceSunKm() / 100 : planet.getRadiusKm() * 1000;

            if (altitude < minSafe) {
                altitude = minSafe;
                showAlert("Tự động điều chỉnh", "Độ cao quá thấp (nguy cơ bốc cháy).\nHệ thống đã tự nâng lên mức tối thiểu: " + String.format("%.0f", altitude) + " km.");
            } else if (altitude > maxSafe) {
                altitude = maxSafe;
                showAlert("Tự động điều chỉnh", "Độ cao quá lớn (nguy cơ bị văng khỏi quỹ đạo).\nHệ thống đã tự hạ xuống mức tối đa: " + String.format("%.0f", altitude) + " km.");
            }

            if (longitude < 0 || longitude > 360) {
                showAlert("Lỗi nhập liệu", "Kinh độ phải nằm trong khoảng [0, 360]!");
                return;
            }
            if (latitude < -90 || latitude > 90) {
                showAlert("Lỗi nhập liệu", "Vĩ độ phải nằm trong khoảng [-90, 90]!");
                return;
            }
            if (signalRange <= 0) {
                showAlert("Lỗi nhập liệu", "Tầm phát sóng phải lớn hơn 0 km!");
                return;
            }

            Satellite s = new Satellite();
            s.setPlanetId(planet.getPlanetId());
            s.setName(name);
            s.setAltitudeKm(altitude);
            s.setLongitude(longitude);
            s.setLatitude(latitude);
            s.setSignalRangeKm(signalRange);
            s.setNatural(false);
            
            double v = PhysicsEngine.calculateOrbitalVelocity(planet.getMassKg(), planet.getRadiusKm(), s.getAltitudeKm());
            s.setOrbitalVelocity(v);

            if (satelliteDAO.addSatellite(s)) {
                loadSatellites(planet.getPlanetId());
                controlPanel.appendLog("Thêm thành công: " + s.getName() + " (v = " + String.format("%.2f", v) + " km/s)");
                
                // Xóa form sau khi thêm thành công (Lỗi 7)
                controlPanel.getTxtName().clear();
                controlPanel.getTxtAlt().clear();
                controlPanel.getTxtLon().clear();
                controlPanel.getTxtLat().clear();
                controlPanel.getTxtSignalRange().clear();
            }
        } catch (NumberFormatException e) {
            showAlert("Lỗi nhập liệu", "Vui lòng nhập đúng định dạng số cho tọa độ, độ cao và tầm sóng!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi hệ thống", "Đã xảy ra lỗi khi thêm vệ tinh.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

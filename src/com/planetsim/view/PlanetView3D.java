package com.planetsim.view;

import com.planetsim.model.Planet;
import com.planetsim.model.Satellite;
import com.planetsim.utils.PhysicsEngine;
import javafx.animation.AnimationTimer;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 3D View for the Solar System and Satellite Orbits.
 */
public class PlanetView3D {
    private Group root;
    private SubScene subScene;
    private PerspectiveCamera camera;
    private Group world; 
    private Group cameraPivot; // Trục Camera độc lập
    private final javafx.scene.transform.Translate cameraTranslate = new javafx.scene.transform.Translate(0, 0, -5000);
    private Sphere sun;
    private Map<Integer, Group> planetGroups = new HashMap<>(); // Group contain planet + satellites
    private Map<Integer, Sphere> planetSpheres = new HashMap<>();
    private Map<Integer, List<Node>> satelliteNodes = new HashMap<>(); 
    private Map<Integer, List<Node>> orbitLines = new HashMap<>();

    private List<Planet> planetsList = new ArrayList<>();
    
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    private long lastTime = 0;
    private double timeScale = 0.5; // Tốc độ mô phỏng
    private final javafx.scene.transform.Translate pivotTranslate = new javafx.scene.transform.Translate();

    public PlanetView3D(double width, double height) {
        root = new Group();
        world = new Group();
        root.getChildren().add(world);

        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(100000.0);
        camera.getTransforms().add(cameraTranslate); // Dùng Translate để điều khiển zoom
        
        cameraPivot = new Group();
        cameraPivot.getChildren().add(camera);
        root.getChildren().add(cameraPivot); // CameraPivot nằm ngoài world
        
        subScene = new SubScene(root, width, height, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK);
        subScene.setCamera(camera);

        // Sun logic
        sun = new Sphere(1200); // Tăng kích thước mặt trời hợp lý hơn
        PhongMaterial sunMat = new PhongMaterial();
        try {
            Image sunImg = new Image(getClass().getResourceAsStream("/textures/sun.jpg"));
            sunMat.setDiffuseMap(sunImg);
            sunMat.setSelfIlluminationMap(sunImg); 
        } catch (Exception e) {
            sunMat.setDiffuseColor(Color.YELLOW);
        }
        sun.setMaterial(sunMat);
        world.getChildren().add(sun);

        PointLight sunLight = new PointLight(Color.WHITE);
        sunLight.setTranslateX(0);
        sunLight.setTranslateY(0);
        sunLight.setTranslateZ(0);
        world.getChildren().add(sunLight);
        
        world.getChildren().add(new AmbientLight(Color.web("#222222")));

        initMouseControls();
        
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime > 0) {
                    double deltaTime = (now - lastTime) / 1e9; // seconds
                    updateVisuals(deltaTime);
                }
                lastTime = now;
            }
        };
        timer.start();
    }

    private void initMouseControls() {
        // Áp dụng xoay lên CameraPivot thay vì world
        cameraPivot.getTransforms().addAll(rotateX, rotateY);
        subScene.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
        });
        subScene.setOnMouseDragged(event -> {
            // Phương án 1A: Độ nhạy động theo khoảng cách Camera (Z-depth)
            double sensitivityFactor = Math.abs(cameraTranslate.getZ()) / 5000.0;
            if (sensitivityFactor < 0.1) sensitivityFactor = 0.1;
            
            rotateX.setAngle(anchorAngleX - (anchorY - event.getSceneY()) * sensitivityFactor);
            rotateY.setAngle(anchorAngleY + (anchorX - event.getSceneX()) * sensitivityFactor);
        });
        subScene.setOnScroll(event -> {
            double delta = event.getDeltaY();
            cameraTranslate.setZ(cameraTranslate.getZ() + delta * 20);
        });
    }

    public void initSolarSystem(List<Planet> planets) {
        this.planetsList = planets;
        world.getChildren().removeIf(node -> node instanceof Group); // Clear old planet systems
        planetGroups.clear();
        planetSpheres.clear();

        for (Planet p : planetsList) {
            Group pGroup = new Group();
            
            // Scaled scaling for visibility
            double dist = calculateOrbitRadius(p);
            
            // Tỷ lệ thực tế: Jupiter (radius ~70k km) vs Sun (radius ~700k km) ~ 1/10
            // Sử dụng Earth = 40 làm chuẩn
            double scaledRadius = (p.getRadiusKm() / 6371.0) * 40.0;
            
            // Giới hạn các hành tinh lớn (Jupiter/Saturn) để không chiếm hết màn hình
            if (scaledRadius > 350) scaledRadius = 350; 
            
            Sphere pSphere = new Sphere(scaledRadius);

            PhongMaterial mat = new PhongMaterial();
            try {
                String path = "/textures/" + p.getTextureFile();
                java.io.InputStream is = getClass().getResourceAsStream(path);
                if (is != null) {
                    mat.setDiffuseMap(new Image(is));
                } else {
                    mat.setDiffuseColor(getFallbackColor(p.getName()));
                }
            } catch (Exception e) {
                mat.setDiffuseColor(getFallbackColor(p.getName()));
            }
            pSphere.setMaterial(mat);
            
            // Vẽ đường quỹ đạo cho hành tinh quanh Mặt Trời
            world.getChildren().add(createOrbitLine(dist));

            pGroup.getChildren().add(pSphere);
            
            // Đặc biệt: Thêm vòng nhẫn cho Saturn
            if (p.getName().equalsIgnoreCase("Saturn")) {
                addSaturnRing(pGroup, pSphere.getRadius());
            }

            planetGroups.put(p.getPlanetId(), pGroup);
            planetSpheres.put(p.getPlanetId(), pSphere);
            world.getChildren().add(pGroup);

            // Initial position around Sun
            double startAngle = Math.random() * 360;
            pGroup.setUserData(startAngle); // Current orbital angle
        }
    }

    private Color getFallbackColor(String name) {
        switch (name) {
            case "Mercury": return Color.GRAY;
            case "Venus":   return Color.ORANGE;
            case "Earth":   return Color.BLUE;
            case "Mars":    return Color.RED;
            case "Jupiter": return Color.BROWN;
            case "Saturn":  return Color.KHAKI;
            case "Uranus":  return Color.LIGHTBLUE;
            case "Neptune": return Color.DARKBLUE;
            default:        return Color.WHITE;
        }
    }

    public void setSatellitesForPlanet(int planetId, List<Satellite> satellites) {
        Group pGroup = planetGroups.get(planetId);
        if (pGroup == null) return;

        // Clear previous sats of THIS planet
        pGroup.getChildren().removeIf(node -> !(node instanceof Sphere && node == planetSpheres.get(planetId)));
        
        for (Satellite s : satellites) {
            // Đồng nhất tỷ lệ: 1 unit 3D = 6371 / 40 = 159.275 km
            double scaleFactor = 6371.0 / 40.0;
            double r = planetSpheres.get(planetId).getRadius() + (s.getAltitudeKm() / scaleFactor);
            
            // Create Orbit Line
            Group orbitGroup = createOrbitLine(r);
            pGroup.getChildren().add(orbitGroup);

            // Create Satellite Sphere
            double size = s.isNatural() ? 10 : 3;
            Sphere satSphere = new Sphere(size);
            PhongMaterial mat = new PhongMaterial();
            mat.setDiffuseColor(s.isNatural() ? Color.WHITE : Color.CYAN);
            satSphere.setMaterial(mat);
            
            // Set initial position based on Longitude and Latitude
            double[] pos = PhysicsEngine.getCartesian(r, s.getLatitude(), s.getLongitude());
            satSphere.setTranslateX(pos[0]);
            satSphere.setTranslateY(pos[1]);
            satSphere.setTranslateZ(pos[2]);

            // Add dynamic data
            // Đồng nhất tỷ lệ: planetRadius (3D units) * 159.275 = radius (km)
            double planetRadiusKm = planetSpheres.get(planetId).getRadius() * 159.275;
            double period = PhysicsEngine.calculateOrbitalPeriod(planetRadiusKm, s.getAltitudeKm(), s.getOrbitalVelocity());
            satSphere.setUserData(new SatelliteOrbitData(r, s.getLongitude(), s.getLatitude(), period));

            pGroup.getChildren().add(satSphere);
        }
    }

    private double calculateOrbitRadius(Planet p) {
        return p.getDistanceSunKm() * 0.00002 + 600;
    }

    private Group createOrbitLine(double orbitRadius) {
        Group g = new Group();
        // Phương án 2A: Giảm số đoạn segments (64 -> 32)
        int segments = 32;
        
        // Phương án 2B: Làm mờ và mỏng đường quỹ đạo (radius 1.5 -> 0.4, opacity 0.4 -> 0.15)
        double opacity = 0.15;
        if (orbitRadius > 2000) opacity = 0.08; // Càng xa mặt trời càng mờ để tránh dày đặc
        
        PhongMaterial mat = new PhongMaterial(Color.web("#ffffff", opacity));
        for (int i = 0; i < segments; i++) {
            double a1 = Math.toRadians(i * 360.0 / segments);
            double a2 = Math.toRadians((i + 1) * 360.0 / segments);
            double x1 = orbitRadius * Math.sin(a1), z1 = orbitRadius * Math.cos(a1);
            double x2 = orbitRadius * Math.sin(a2), z2 = orbitRadius * Math.cos(a2);
            double len = Math.sqrt((x2-x1)*(x2-x1) + (z2-z1)*(z2-z1));
            double rotAngle = Math.toDegrees(Math.atan2(x2 - x1, z2 - z1));
            Cylinder seg = new Cylinder(0.4, len);
            seg.setMaterial(mat);
            seg.setTranslateX((x1 + x2) / 2);
            seg.setTranslateZ((z1 + z2) / 2);
            seg.getTransforms().addAll(new Rotate(rotAngle, Rotate.Y_AXIS), new Rotate(90, Rotate.X_AXIS));
            g.getChildren().add(seg);
        }
        return g;
    }

    private void updateVisuals(double dt) {
        double simDt = dt * timeScale * 10; // Speed up

        for (Planet p : planetsList) {
            Group pGroup = planetGroups.get(p.getPlanetId());
            Sphere pSphere = planetSpheres.get(p.getPlanetId());
            if (pGroup == null) continue;

            // 1. Rotate Planet
            pSphere.setRotate(pSphere.getRotate() + p.getRotationSpeed() * simDt * 100);

            // 2. Move Planet around Sun
            double dist = calculateOrbitRadius(p);
            double currentAngle = (double) pGroup.getUserData();
            double orbitalSpeed = 360.0 / (p.getOrbitalPeriodDays() > 0 ? p.getOrbitalPeriodDays() : 365);
            currentAngle += orbitalSpeed * simDt;
            pGroup.setUserData(currentAngle);

            double x = dist * Math.cos(Math.toRadians(currentAngle));
            double z = dist * Math.sin(Math.toRadians(currentAngle));
            pGroup.setTranslateX(x);
            pGroup.setTranslateZ(z);

            // 3. Move Satellites around Planet
            for (Node node : pGroup.getChildren()) {
                if (node instanceof Sphere && node != pSphere) {
                    SatelliteOrbitData data = (SatelliteOrbitData) node.getUserData();
                    if (data != null) {
                        // Vận tốc góc thực tế dựa trên chu kỳ (độ/giây)
                        // Tăng thêm hệ số 1000 để vệ tinh bay nhanh rõ rệt hơn so với hành tinh
                        data.currentAngle += (360.0 / data.period) * simDt * 1000; 
                        double[] pos = PhysicsEngine.getCartesian(data.radius, data.latitude, data.currentAngle);
                        node.setTranslateX(pos[0]);
                        node.setTranslateY(pos[1]);
                        node.setTranslateZ(pos[2]);
                    }
                }
            }

            // 4. Camera Tracking: Nhấc nguyên cái cụm CameraPivot đặt vào tọa độ hành tinh focus
            if (focusedPlanetId == p.getPlanetId()) {
                cameraPivot.setTranslateX(x);
                cameraPivot.setTranslateZ(z);
            }
        }
        // Nếu không focus hành tinh nào → CameraPivot quay về trung tâm (Mặt Trời)
        if (focusedPlanetId == -1) {
            cameraPivot.setTranslateX(0);
            cameraPivot.setTranslateY(0);
            cameraPivot.setTranslateZ(0);
        }
    }

    private int focusedPlanetId = -1;

    public void focusOnPlanet(int planetId) {
        this.focusedPlanetId = planetId;
        if (planetId != -1) {
            cameraTranslate.setZ(-1500); // Zoom gần để quan sát hành tinh
        }
    }
    
    public void resetView() {
        this.focusedPlanetId = -1;
        cameraTranslate.setZ(-5000); // Zoom ra toàn cảnh
        cameraTranslate.setX(0);
        cameraTranslate.setY(0);
    }

    public void setTimeScale(double scale) {
        this.timeScale = scale;
    }


    private void addSaturnRing(Group pGroup, double planetRadius) {
        double innerR = planetRadius * 1.3;
        double outerR = planetRadius * 2.5;
        int seg = 64;

        TriangleMesh mesh = new TriangleMesh();
        float[] pts = new float[seg * 2 * 3];
        float[] tex = new float[seg * 2 * 2];
        int[] faces = new int[seg * 2 * 6];

        for (int i = 0; i < seg; i++) {
            double angle = Math.toRadians(i * 360.0 / seg);
            float cosA = (float) Math.cos(angle);
            float sinA = (float) Math.sin(angle);
            pts[i * 3]           = (float)(innerR * cosA); pts[i * 3 + 1]           = 0f; pts[i * 3 + 2]           = (float)(innerR * sinA);
            pts[(seg + i) * 3]   = (float)(outerR * cosA); pts[(seg + i) * 3 + 1]   = 0f; pts[(seg + i) * 3 + 2]   = (float)(outerR * sinA);
            float u = (float) i / seg;
            tex[i * 2] = u; tex[i * 2 + 1] = 1f;
            tex[(seg + i) * 2] = u; tex[(seg + i) * 2 + 1] = 0f;
        }
        for (int i = 0; i < seg; i++) {
            int next = (i + 1) % seg;
            int i0 = i, i1 = next, o0 = seg + i, o1 = seg + next;
            faces[i * 12]      = i0; faces[i * 12 + 1]  = i0;
            faces[i * 12 + 2]  = o0; faces[i * 12 + 3]  = o0;
            faces[i * 12 + 4]  = i1; faces[i * 12 + 5]  = i1;
            faces[i * 12 + 6]  = i1; faces[i * 12 + 7]  = i1;
            faces[i * 12 + 8]  = o0; faces[i * 12 + 9]  = o0;
            faces[i * 12 + 10] = o1; faces[i * 12 + 11] = o1;
        }
        mesh.getPoints().addAll(pts);
        mesh.getTexCoords().addAll(tex);
        mesh.getFaces().addAll(faces);

        MeshView ring = new MeshView(mesh);
        ring.setCullFace(CullFace.NONE);
        PhongMaterial ringMat = new PhongMaterial();
        try {
            Image ringImg = new Image(getClass().getResourceAsStream("/textures/saturn_ring.png"));
            ringMat.setDiffuseMap(ringImg);
        } catch (Exception e) {
            ringMat.setDiffuseColor(Color.web("#C5AB6E", 0.7));
        }
        ring.setMaterial(ringMat);
        ring.getTransforms().add(new Rotate(26.7, Rotate.X_AXIS));
        pGroup.getChildren().add(ring);
    }

    public SubScene getSubScene() { return subScene; }

    // Helper class to store runtime orbit data
    private static class SatelliteOrbitData {
        double radius, currentAngle, latitude, period;
        SatelliteOrbitData(double r, double angle, double lat, double p) {
            this.radius = r; this.currentAngle = angle; this.latitude = lat; this.period = p;
        }
    }
}

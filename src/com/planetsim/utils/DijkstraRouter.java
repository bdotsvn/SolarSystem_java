package com.planetsim.utils;

import com.planetsim.model.Satellite;
import com.planetsim.model.RoutingResult;
import java.util.*;

/**
 * Implementation of Dijkstra's Algorithm for Satellite Routing.
 */
public class DijkstraRouter {

    /**
     * Tìm đường ngắn nhất giữa 2 vệ tinh.
     */
    public static RoutingResult findShortestPath(List<Satellite> satellites, int sourceId, int destId) {
        if (satellites == null || satellites.isEmpty()) return new RoutingResult(null, 0, false);

        Satellite source = findById(satellites, sourceId);
        Satellite dest = findById(satellites, destId);

        if (source == null || dest == null) return new RoutingResult(null, 0, false);

        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Satellite> previous = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>();

        for (Satellite s : satellites) {
            distances.put(s.getSatelliteId(), Double.MAX_VALUE);
        }

        distances.put(sourceId, 0.0);
        pq.add(new Node(sourceId, 0.0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            if (current.dist > distances.get(current.id)) continue;
            if (current.id == destId) break;

            Satellite currentSat = findById(satellites, current.id);
            if (currentSat == null) continue;

            for (Satellite neighbor : satellites) {
                if (neighbor.getSatelliteId() == current.id) continue;

                double distToNeighbor = calculateDistance(currentSat, neighbor);
                
                // Kết nối hợp lệ khi một trong 2 bên có tầm sóng đủ xa (đồ thị vô hướng)
                boolean inRange = distToNeighbor <= currentSat.getSignalRangeKm()
                               || distToNeighbor <= neighbor.getSignalRangeKm();
                if (inRange) {
                    double newDist = distances.get(current.id) + distToNeighbor;
                    if (newDist < distances.get(neighbor.getSatelliteId())) {
                        distances.put(neighbor.getSatelliteId(), newDist);
                        previous.put(neighbor.getSatelliteId(), currentSat);
                        pq.add(new Node(neighbor.getSatelliteId(), newDist));
                    }
                }
            }
        }

        if (distances.get(destId) == Double.MAX_VALUE) {
            return new RoutingResult(null, 0, false);
        }

        // Truy vết đường đi
        List<Satellite> path = new ArrayList<>();
        Satellite curr = dest;
        while (curr != null) {
            path.add(0, curr);
            curr = previous.get(curr.getSatelliteId());
        }

        return new RoutingResult(path, distances.get(destId), true);
    }

    private static double calculateDistance(Satellite s1, Satellite s2) {
        // R = R_planet + Altitude
        // Vì đơn giản hóa, ta lấy R_earth = 6371 làm chuẩn hoặc lấy từ DB nếu cần
        double r1 = 6371 + s1.getAltitudeKm();
        double r2 = 6371 + s2.getAltitudeKm();
        
        double[] p1 = PhysicsEngine.getCartesian(r1, s1.getLatitude(), s1.getLongitude());
        double[] p2 = PhysicsEngine.getCartesian(r2, s2.getLatitude(), s2.getLongitude());
        
        return Math.sqrt(Math.pow(p2[0] - p1[0], 2) + Math.pow(p2[1] - p1[1], 2) + Math.pow(p2[2] - p1[2], 2));
    }

    private static Satellite findById(List<Satellite> list, int id) {
        for (Satellite s : list) {
            if (s.getSatelliteId() == id) return s;
        }
        return null;
    }

    private static class Node implements Comparable<Node> {
        int id;
        double dist;
        Node(int id, double d) { this.id = id; this.dist = d; }
        public int compareTo(Node o) { return Double.compare(this.dist, o.dist); }
    }
}

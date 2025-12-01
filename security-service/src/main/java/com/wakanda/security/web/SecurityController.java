package com.wakanda.security.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/security")
public class SecurityController {

    private final Map<Long, Map<String, Object>> cameras = new HashMap<>();
    private final Map<Long, Map<String, Object>> drones = new HashMap<>();

    public SecurityController() {
        initCameras();
        initDrones();
    }

    private void initCameras() {
        for (long i = 1; i <= 8; i++) {
            Map<String, Object> camera = new HashMap<>();
            camera.put("id", i);
            camera.put("location", "Cámara " + i);
            camera.put("status", "ACTIVE");
            camera.put("type", "AI_ENABLED");
            cameras.put(i, camera);
        }
    }

    private void initDrones() {
        for (long i = 1; i <= 3; i++) {
            Map<String, Object> drone = new HashMap<>();
            drone.put("id", i);
            drone.put("location", "Zona " + i);
            drone.put("status", "PATROLLING");
            drone.put("batteryLevel", 80 + new Random().nextInt(20));
            drones.put(i, drone);
        }
    }

    @GetMapping("/cameras")
    public Collection<Map<String, Object>> getAllCameras() {
        return cameras.values();
    }

    @GetMapping("/drones")
    public Collection<Map<String, Object>> getAllDrones() {
        return drones.values();
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("totalCameras", cameras.size());
        status.put("totalDrones", drones.size());
        status.put("activeCameras", cameras.values().stream()
                .filter(c -> "ACTIVE".equals(c.get("status")))
                .count());
        status.put("patrollingDrones", drones.values().stream()
                .filter(d -> "PATROLLING".equals(d.get("status")))
                .count());
        status.put("service", "security-service");
        status.put("status", "UP");
        return status;
    }

    @GetMapping("/cameras/status")
    public ResponseEntity<Map<String, Object>> getAllCamerasStatus() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> camerasList = new ArrayList<>();
        
        for (Map.Entry<Long, Map<String, Object>> entry : cameras.entrySet()) {
            Map<String, Object> camera = entry.getValue();
            Map<String, Object> cameraInfo = new HashMap<>();
            cameraInfo.put("id", camera.get("id"));
            cameraInfo.put("location", camera.get("location"));
            cameraInfo.put("status", camera.get("status"));
            cameraInfo.put("type", camera.get("type"));
            camerasList.add(cameraInfo);
        }
        
        response.put("success", true);
        response.put("cameras", camerasList);
        response.put("total", camerasList.size());
        response.put("activeCount", camerasList.stream().filter(c -> "ACTIVE".equals(c.get("status"))).count());
        response.put("aiEnabledCount", camerasList.stream().filter(c -> "AI_ENABLED".equals(c.get("type"))).count());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/drones/status")
    public ResponseEntity<Map<String, Object>> getAllDronesStatus() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> dronesList = new ArrayList<>();
        
        for (Map.Entry<Long, Map<String, Object>> entry : drones.entrySet()) {
            Map<String, Object> drone = entry.getValue();
            Map<String, Object> droneInfo = new HashMap<>();
            droneInfo.put("id", drone.get("id"));
            droneInfo.put("location", drone.get("location"));
            droneInfo.put("status", drone.get("status"));
            droneInfo.put("batteryLevel", drone.get("batteryLevel"));
            dronesList.add(droneInfo);
        }
        
        int totalBattery = dronesList.stream()
                .mapToInt(d -> ((Number) d.get("batteryLevel")).intValue())
                .sum();
        int averageBattery = dronesList.isEmpty() ? 0 : totalBattery / dronesList.size();
        
        response.put("success", true);
        response.put("drones", dronesList);
        response.put("total", dronesList.size());
        response.put("patrollingCount", dronesList.stream().filter(d -> "PATROLLING".equals(d.get("status"))).count());
        response.put("averageBattery", averageBattery);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/drones/{id}/patrol")
    public ResponseEntity<Map<String, Object>> startDronePatrol(@PathVariable("id") Long id) {
        try {
            if (!drones.containsKey(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Drone no encontrado: " + id);
                return ResponseEntity.status(404).body(error);
            }
            
            Map<String, Object> drone = drones.get(id);
            String currentStatus = (String) drone.get("status");
            
            if ("PATROLLING".equals(currentStatus)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", String.format("El drone %d ya está en patrullaje", id));
                response.put("drone", drone);
                return ResponseEntity.ok(response);
            }
            
            drone.put("status", "PATROLLING");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("Drone %d inició patrullaje en %s", id, drone.get("location")));
            response.put("drone", drone);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("ERROR en startDronePatrol: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al procesar la solicitud: " + e.getMessage());
            error.put("errorType", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/cameras/{id}/alert")
    public ResponseEntity<Map<String, Object>> cameraAlert(
            @PathVariable("id") Long id,
            @RequestParam(value = "alertType", required = false, defaultValue = "INTRUSION") String alertType) {
        try {
            // Validar que la cámara existe
            if (!cameras.containsKey(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Cámara no encontrada: " + id);
                return ResponseEntity.status(404).body(error);
            }
            
            if (alertType == null || alertType.trim().isEmpty()) {
                alertType = "INTRUSION";
            }
            
            Map<String, Object> camera = cameras.get(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("Alerta de cámara %d: %s en %s", id, alertType, camera.get("location")));
            response.put("camera", camera);
            response.put("alertType", alertType);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("ERROR en cameraAlert: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al procesar la solicitud: " + e.getMessage());
            error.put("errorType", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(error);
        }
    }
}


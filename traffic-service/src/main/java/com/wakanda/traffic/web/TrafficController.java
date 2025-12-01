package com.wakanda.traffic.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/traffic")
public class TrafficController {

    private final Map<Long, Map<String, Object>> trafficLights = new HashMap<>();
    private final Map<Long, Map<String, Object>> sensors = new HashMap<>();

    public TrafficController() {
        initTrafficLights();
        initSensors();
    }

    private void initTrafficLights() {
        for (long i = 1; i <= 10; i++) {
            Map<String, Object> light = new HashMap<>();
            light.put("id", i);
            light.put("location", "Intersección " + i);
            light.put("status", i % 2 == 0 ? "GREEN" : "RED");
            light.put("duration", 30);
            trafficLights.put(i, light);
        }
    }

    private void initSensors() {
        for (long i = 1; i <= 5; i++) {
            Map<String, Object> sensor = new HashMap<>();
            sensor.put("id", i);
            sensor.put("location", "Sensor " + i);
            sensor.put("vehicleCount", new Random().nextInt(50));
            sensor.put("status", "ACTIVE");
            sensors.put(i, sensor);
        }
    }

    @GetMapping("/lights")
    public Collection<Map<String, Object>> getAllTrafficLights() {
        return trafficLights.values();
    }

    @GetMapping("/lights/{id}")
    public ResponseEntity<Map<String, Object>> getTrafficLight(@PathVariable Long id) {
        Map<String, Object> light = trafficLights.get(id);
        if (light == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(light);
    }

    @GetMapping("/sensors")
    public Collection<Map<String, Object>> getAllSensors() {
        return sensors.values();
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("totalLights", trafficLights.size());
        status.put("totalSensors", sensors.size());
        status.put("greenLights", trafficLights.values().stream()
                .filter(l -> "GREEN".equals(l.get("status")))
                .count());
        status.put("redLights", trafficLights.values().stream()
                .filter(l -> "RED".equals(l.get("status")))
                .count());
        status.put("service", "traffic-service");
        status.put("status", "UP");
        return status;
    }

    @GetMapping("/lights/status")
    public ResponseEntity<Map<String, Object>> getAllLightsStatus() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> lightsList = new ArrayList<>();
        
        for (Map.Entry<Long, Map<String, Object>> entry : trafficLights.entrySet()) {
            Map<String, Object> light = entry.getValue();
            Map<String, Object> lightInfo = new HashMap<>();
            lightInfo.put("id", light.get("id"));
            lightInfo.put("location", light.get("location"));
            lightInfo.put("status", light.get("status"));
            lightInfo.put("duration", light.get("duration"));
            lightsList.add(lightInfo);
        }
        
        response.put("success", true);
        response.put("lights", lightsList);
        response.put("total", lightsList.size());
        response.put("greenCount", lightsList.stream().filter(l -> "GREEN".equals(l.get("status"))).count());
        response.put("redCount", lightsList.stream().filter(l -> "RED".equals(l.get("status"))).count());
        response.put("yellowCount", lightsList.stream().filter(l -> "YELLOW".equals(l.get("status"))).count());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sensors/status")
    public ResponseEntity<Map<String, Object>> getAllSensorsStatus() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> sensorsList = new ArrayList<>();
        
        for (Map.Entry<Long, Map<String, Object>> entry : sensors.entrySet()) {
            Map<String, Object> sensor = entry.getValue();
            Map<String, Object> sensorInfo = new HashMap<>();
            sensorInfo.put("id", sensor.get("id"));
            sensorInfo.put("location", sensor.get("location"));
            sensorInfo.put("vehicleCount", sensor.get("vehicleCount"));
            sensorInfo.put("status", sensor.get("status"));
            sensorsList.add(sensorInfo);
        }
        
        int totalVehicles = sensorsList.stream()
                .mapToInt(s -> ((Number) s.get("vehicleCount")).intValue())
                .sum();
        
        response.put("success", true);
        response.put("sensors", sensorsList);
        response.put("total", sensorsList.size());
        response.put("totalVehicles", totalVehicles);
        response.put("averageVehicles", sensorsList.isEmpty() ? 0 : totalVehicles / sensorsList.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/lights/{id}/change")
    public ResponseEntity<Map<String, Object>> changeTrafficLight(
            @PathVariable("id") Long id,
            @RequestParam(value = "state", required = false, defaultValue = "GREEN") String state) {
        try {
            // Validar que el semáforo existe
            if (!trafficLights.containsKey(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Semáforo no encontrado: " + id);
                return ResponseEntity.status(404).body(error);
            }
            
            // Validar estado
            if (state == null || state.trim().isEmpty()) {
                state = "GREEN";
            }
            state = state.toUpperCase().trim();
            
            if (!state.equals("GREEN") && !state.equals("RED") && !state.equals("YELLOW")) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Estado inválido. Use: GREEN, RED o YELLOW");
                return ResponseEntity.status(400).body(error);
            }
            
            Map<String, Object> light = trafficLights.get(id);
            light.put("status", state);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("Semáforo %d cambiado a %s", id, state));
            response.put("light", light);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("ERROR en changeTrafficLight: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al procesar la solicitud: " + e.getMessage());
            error.put("errorType", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(error);
        }
    }
}


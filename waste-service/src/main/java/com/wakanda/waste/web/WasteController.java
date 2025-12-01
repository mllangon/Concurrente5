package com.wakanda.waste.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/containers")
public class WasteController {

    private final Map<Long, Map<String, Object>> containers = new HashMap<>();

    public WasteController() {
        // Inicializar algunos contenedores de ejemplo
        initContainers();
    }

    private void initContainers() {
        for (long i = 1; i <= 5; i++) {
            Map<String, Object> container = new HashMap<>();
            container.put("id", i);
            container.put("name", "Contenedor " + i);
            container.put("status", "ACTIVE");
            container.put("fillLevel", new Random().nextInt(50));
            container.put("location", "Ubicación " + i);
            containers.put(i, container);
        }
    }

    @GetMapping
    public Collection<Map<String, Object>> getAllContainers() {
        return containers.values();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getContainer(@PathVariable Long id) {
        Map<String, Object> container = containers.get(id);
        if (container == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(container);
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("totalContainers", containers.size());
        status.put("activeContainers", containers.values().stream()
                .filter(c -> "ACTIVE".equals(c.get("status")))
                .count());
        status.put("fullContainers", containers.values().stream()
                .filter(c -> "FULL".equals(c.get("status")))
                .count());
        status.put("service", "waste-service");
        status.put("status", "UP");
        return status;
    }

    @GetMapping("/levels")
    public ResponseEntity<Map<String, Object>> getContainerLevels() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> levels = new ArrayList<>();
        
        for (Map.Entry<Long, Map<String, Object>> entry : containers.entrySet()) {
            Map<String, Object> container = entry.getValue();
            Map<String, Object> levelInfo = new HashMap<>();
            levelInfo.put("id", container.get("id"));
            levelInfo.put("name", container.get("name"));
            levelInfo.put("fillLevel", container.get("fillLevel"));
            levelInfo.put("status", container.get("status"));
            levelInfo.put("location", container.get("location"));
            levels.add(levelInfo);
        }
        
        response.put("success", true);
        response.put("containers", levels);
        response.put("total", levels.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/collect")
    public ResponseEntity<Map<String, Object>> collectWaste(@PathVariable("id") Long id) {
        try {
            if (!containers.containsKey(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Contenedor no encontrado: " + id);
                return ResponseEntity.status(404).body(error);
            }
            
            Map<String, Object> container = containers.get(id);
            container.put("fillLevel", 0);
            container.put("status", "ACTIVE");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("Servicio de recogida ha vaciado el contenedor %d", id));
            response.put("container", container);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("ERROR en collectWaste: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al procesar la solicitud: " + e.getMessage());
            error.put("errorType", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/collect-all")
    public ResponseEntity<Map<String, Object>> collectAllWaste() {
        try {
            int collected = 0;
            for (Map<String, Object> container : containers.values()) {
                Object fillLevel = container.get("fillLevel");
                if (fillLevel != null) {
                    int level = fillLevel instanceof Number ? ((Number) fillLevel).intValue() : 0;
                    if (level > 0) {
                        container.put("fillLevel", 0);
                        container.put("status", "ACTIVE");
                        collected++;
                    }
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("Servicio de recogida ha vaciado %d contenedores", collected));
            response.put("collected", collected);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("ERROR en collectAllWaste: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al procesar la solicitud: " + e.getMessage());
            error.put("errorType", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/{id}/add-waste")
    public ResponseEntity<Map<String, Object>> addWaste(
            @PathVariable("id") Long id,
            @RequestParam(value = "amount", required = false, defaultValue = "10") String amountStr) {
        try {
            // Parsear amount de forma segura
            int amount = 10;
            if (amountStr != null && !amountStr.trim().isEmpty()) {
                try {
                    amount = Integer.parseInt(amountStr.trim());
                } catch (NumberFormatException e) {
                    amount = 10;
                }
            }
            
            // Validar que el contenedor existe
            if (!containers.containsKey(id)) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Contenedor no encontrado: " + id);
                return ResponseEntity.status(404).body(error);
            }
            
            Map<String, Object> container = containers.get(id);
            
            // Obtener el nivel actual de forma segura
            int currentLevel = 0;
            Object fillLevelObj = container.get("fillLevel");
            if (fillLevelObj != null) {
                if (fillLevelObj instanceof Integer) {
                    currentLevel = (Integer) fillLevelObj;
                } else if (fillLevelObj instanceof Number) {
                    currentLevel = ((Number) fillLevelObj).intValue();
                }
            }
            
            // Calcular nuevo nivel
            int newLevel = Math.min(100, currentLevel + amount);
            container.put("fillLevel", newLevel);
            
            // Actualizar estado
            if (newLevel >= 100) {
                container.put("status", "FULL");
            } else {
                container.put("status", "ACTIVE");
            }
            
            // Preparar respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("Añadidas %d unidades de basura al contenedor %d. Nuevo nivel: %d%%", amount, id, newLevel));
            response.put("container", container);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Log del error completo
            System.err.println("ERROR en addWaste: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al procesar la solicitud: " + e.getMessage());
            error.put("errorType", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(error);
        }
    }
}


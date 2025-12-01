package com.wakanda.emergency.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/emergency")
public class EmergencyController {

    private final Map<Long, Map<String, Object>> emergencyUnits = new HashMap<>();
    private final Map<Long, Map<String, Object>> incidents = new HashMap<>();

    public EmergencyController() {
        initEmergencyUnits();
        initIncidents();
    }

    private void initEmergencyUnits() {
        String[] types = {"AMBULANCE", "FIRE_TRUCK", "POLICE"};
        for (long i = 1; i <= 6; i++) {
            Map<String, Object> unit = new HashMap<>();
            unit.put("id", i);
            unit.put("type", types[(int)(i % 3)]);
            unit.put("location", "Base " + i);
            unit.put("status", i % 2 == 0 ? "AVAILABLE" : "ON_DUTY");
            emergencyUnits.put(i, unit);
        }
    }

    private void initIncidents() {
        for (long i = 1; i <= 5; i++) {
            Map<String, Object> incident = new HashMap<>();
            incident.put("id", i);
            incident.put("type", "INCIDENT_" + i);
            incident.put("location", "Ubicación " + i);
            incident.put("status", "ACTIVE");
            incident.put("priority", "HIGH");
            incidents.put(i, incident);
        }
    }

    @GetMapping("/units")
    public Collection<Map<String, Object>> getAllEmergencyUnits() {
        return emergencyUnits.values();
    }

    @GetMapping("/incidents")
    public Collection<Map<String, Object>> getAllIncidents() {
        return incidents.values();
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("totalUnits", emergencyUnits.size());
        status.put("activeIncidents", incidents.size());
        status.put("availableUnits", emergencyUnits.values().stream()
                .filter(u -> "AVAILABLE".equals(u.get("status")))
                .count());
        status.put("highPriorityIncidents", incidents.values().stream()
                .filter(i -> "HIGH".equals(i.get("priority")))
                .count());
        status.put("service", "emergency-service");
        status.put("status", "UP");
        return status;
    }

    @GetMapping("/units/status")
    public ResponseEntity<Map<String, Object>> getAllUnitsStatus() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> unitsList = new ArrayList<>();
        
        for (Map.Entry<Long, Map<String, Object>> entry : emergencyUnits.entrySet()) {
            Map<String, Object> unit = entry.getValue();
            Map<String, Object> unitInfo = new HashMap<>();
            unitInfo.put("id", unit.get("id"));
            unitInfo.put("type", unit.get("type"));
            unitInfo.put("location", unit.get("location"));
            unitInfo.put("status", unit.get("status"));
            unitsList.add(unitInfo);
        }
        
        response.put("success", true);
        response.put("units", unitsList);
        response.put("total", unitsList.size());
        response.put("availableCount", unitsList.stream().filter(u -> "AVAILABLE".equals(u.get("status"))).count());
        response.put("onDutyCount", unitsList.stream().filter(u -> "ON_DUTY".equals(u.get("status"))).count());
        response.put("ambulanceCount", unitsList.stream().filter(u -> "AMBULANCE".equals(u.get("type"))).count());
        response.put("fireTruckCount", unitsList.stream().filter(u -> "FIRE_TRUCK".equals(u.get("type"))).count());
        response.put("policeCount", unitsList.stream().filter(u -> "POLICE".equals(u.get("type"))).count());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/incidents/status")
    public ResponseEntity<Map<String, Object>> getAllIncidentsStatus() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> incidentsList = new ArrayList<>();
        
        for (Map.Entry<Long, Map<String, Object>> entry : incidents.entrySet()) {
            Map<String, Object> incident = entry.getValue();
            Map<String, Object> incidentInfo = new HashMap<>();
            incidentInfo.put("id", incident.get("id"));
            incidentInfo.put("type", incident.get("type"));
            incidentInfo.put("location", incident.get("location"));
            incidentInfo.put("status", incident.get("status"));
            incidentInfo.put("priority", incident.get("priority"));
            incidentsList.add(incidentInfo);
        }
        
        response.put("success", true);
        response.put("incidents", incidentsList);
        response.put("total", incidentsList.size());
        response.put("activeCount", incidentsList.stream().filter(i -> "ACTIVE".equals(i.get("status"))).count());
        response.put("highPriorityCount", incidentsList.stream().filter(i -> "HIGH".equals(i.get("priority"))).count());
        response.put("mediumPriorityCount", incidentsList.stream().filter(i -> "MEDIUM".equals(i.get("priority"))).count());
        response.put("lowPriorityCount", incidentsList.stream().filter(i -> "LOW".equals(i.get("priority"))).count());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/units/{id}/assign")
    public ResponseEntity<Map<String, Object>> assignUnitToIncident(
            @PathVariable("id") Long unitId,
            @RequestParam(value = "incidentId", required = false) String incidentIdStr) {
        try {
            if (!emergencyUnits.containsKey(unitId)) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Unidad de emergencia no encontrada: " + unitId);
                return ResponseEntity.status(404).body(error);
            }
            
            Map<String, Object> unit = emergencyUnits.get(unitId);
            
            if (incidentIdStr == null || incidentIdStr.trim().isEmpty()) {
                // Solo cambiar estado a ON_DUTY
                unit.put("status", "ON_DUTY");
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", String.format("Unidad %d (%s) asignada a servicio", unitId, unit.get("type")));
                response.put("unit", unit);
                return ResponseEntity.ok(response);
            }
            
            Long incidentId;
            try {
                incidentId = Long.parseLong(incidentIdStr.trim());
            } catch (NumberFormatException e) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "ID de incidente inválido: " + incidentIdStr);
                return ResponseEntity.status(400).body(error);
            }
            
            if (!incidents.containsKey(incidentId)) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Incidente no encontrado: " + incidentId);
                return ResponseEntity.status(404).body(error);
            }
            
            unit.put("status", "ON_DUTY");
            Map<String, Object> incident = incidents.get(incidentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("Unidad %d (%s) asignada al incidente %d en %s", 
                    unitId, unit.get("type"), incidentId, incident.get("location")));
            response.put("unit", unit);
            response.put("incident", incident);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("ERROR en assignUnitToIncident: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al procesar la solicitud: " + e.getMessage());
            error.put("errorType", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/incidents/create")
    public ResponseEntity<Map<String, Object>> createIncident(
            @RequestParam(value = "type", required = false, defaultValue = "INCIDENT") String type,
            @RequestParam(value = "location", required = false, defaultValue = "Ubicación desconocida") String location,
            @RequestParam(value = "priority", required = false, defaultValue = "HIGH") String priority) {
        try {
            // Validar parámetros
            if (type == null || type.trim().isEmpty()) {
                type = "INCIDENT";
            }
            
            if (location == null || location.trim().isEmpty()) {
                location = "Ubicación desconocida";
            }
            
            if (priority == null || priority.trim().isEmpty()) {
                priority = "HIGH";
            }
            priority = priority.toUpperCase().trim();
            
            // Crear incidente
            long newId = incidents.size() + 1;
            Map<String, Object> incident = new HashMap<>();
            incident.put("id", newId);
            incident.put("type", type.trim());
            incident.put("location", location.trim());
            incident.put("status", "ACTIVE");
            incident.put("priority", priority);
            incidents.put(newId, incident);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("Emergencia creada: %s en %s (Prioridad: %s)", type, location, priority));
            response.put("incident", incident);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("ERROR en createIncident: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al procesar la solicitud: " + e.getMessage());
            error.put("errorType", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(error);
        }
    }
}


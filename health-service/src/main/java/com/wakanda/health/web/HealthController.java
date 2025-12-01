package com.wakanda.health.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/health")
public class HealthController {

    private final Map<Long, Map<String, Object>> healthCenters = new HashMap<>();
    private final Map<Long, Map<String, Object>> appointments = new HashMap<>();

    public HealthController() {
        initHealthCenters();
        initAppointments();
    }

    private void initHealthCenters() {
        for (long i = 1; i <= 5; i++) {
            Map<String, Object> center = new HashMap<>();
            center.put("id", i);
            center.put("name", "Centro de Salud " + i);
            center.put("location", "Ubicación " + i);
            center.put("availableSlots", new Random().nextInt(20));
            center.put("status", "OPEN");
            healthCenters.put(i, center);
        }
    }

    private void initAppointments() {
        for (long i = 1; i <= 10; i++) {
            Map<String, Object> appointment = new HashMap<>();
            appointment.put("id", i);
            appointment.put("patientId", "P" + i);
            appointment.put("centerId", (i % 5) + 1);
            appointment.put("date", "2025-12-" + (i % 28 + 1));
            appointment.put("status", "SCHEDULED");
            appointments.put(i, appointment);
        }
    }

    @GetMapping("/centers")
    public Collection<Map<String, Object>> getAllHealthCenters() {
        return healthCenters.values();
    }

    @GetMapping("/appointments")
    public Collection<Map<String, Object>> getAllAppointments() {
        return appointments.values();
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("totalCenters", healthCenters.size());
        status.put("totalAppointments", appointments.size());
        status.put("openCenters", healthCenters.values().stream()
                .filter(c -> "OPEN".equals(c.get("status")))
                .count());
        status.put("scheduledAppointments", appointments.values().stream()
                .filter(a -> "SCHEDULED".equals(a.get("status")))
                .count());
        status.put("service", "health-service");
        status.put("status", "UP");
        return status;
    }

    @GetMapping("/centers/status")
    public ResponseEntity<Map<String, Object>> getAllCentersStatus() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> centersList = new ArrayList<>();
        
        for (Map.Entry<Long, Map<String, Object>> entry : healthCenters.entrySet()) {
            Map<String, Object> center = entry.getValue();
            Map<String, Object> centerInfo = new HashMap<>();
            centerInfo.put("id", center.get("id"));
            centerInfo.put("name", center.get("name"));
            centerInfo.put("location", center.get("location"));
            centerInfo.put("status", center.get("status"));
            centerInfo.put("availableSlots", center.get("availableSlots"));
            centersList.add(centerInfo);
        }
        
        int totalSlots = centersList.stream()
                .mapToInt(c -> ((Number) c.get("availableSlots")).intValue())
                .sum();
        
        response.put("success", true);
        response.put("centers", centersList);
        response.put("total", centersList.size());
        response.put("openCount", centersList.stream().filter(c -> "OPEN".equals(c.get("status"))).count());
        response.put("totalAvailableSlots", totalSlots);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/appointments/status")
    public ResponseEntity<Map<String, Object>> getAllAppointmentsStatus() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> appointmentsList = new ArrayList<>();
        
        for (Map.Entry<Long, Map<String, Object>> entry : appointments.entrySet()) {
            Map<String, Object> appointment = entry.getValue();
            Map<String, Object> appointmentInfo = new HashMap<>();
            appointmentInfo.put("id", appointment.get("id"));
            appointmentInfo.put("patientId", appointment.get("patientId"));
            appointmentInfo.put("centerId", appointment.get("centerId"));
            appointmentInfo.put("date", appointment.get("date"));
            appointmentInfo.put("status", appointment.get("status"));
            appointmentsList.add(appointmentInfo);
        }
        
        response.put("success", true);
        response.put("appointments", appointmentsList);
        response.put("total", appointmentsList.size());
        response.put("scheduledCount", appointmentsList.stream().filter(a -> "SCHEDULED".equals(a.get("status"))).count());
        response.put("completedCount", appointmentsList.stream().filter(a -> "COMPLETED".equals(a.get("status"))).count());
        response.put("cancelledCount", appointmentsList.stream().filter(a -> "CANCELLED".equals(a.get("status"))).count());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/appointments/create")
    public ResponseEntity<Map<String, Object>> createAppointment(
            @RequestParam(value = "patientId", required = false) String patientId,
            @RequestParam(value = "centerId", required = false) String centerId,
            @RequestParam(value = "date", required = false) String date) {
        try {
            // Validar parámetros requeridos
            if (patientId == null || patientId.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "El parámetro 'patientId' es requerido");
                return ResponseEntity.status(400).body(error);
            }
            
            if (centerId == null || centerId.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "El parámetro 'centerId' es requerido");
                return ResponseEntity.status(400).body(error);
            }
            
            if (date == null || date.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "El parámetro 'date' es requerido");
                return ResponseEntity.status(400).body(error);
            }
            
            // Parsear centerId
            Long centerIdLong;
            try {
                centerIdLong = Long.parseLong(centerId.trim());
            } catch (NumberFormatException e) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "centerId debe ser un número válido: " + centerId);
                return ResponseEntity.status(400).body(error);
            }
            
            // Crear cita
            long newId = appointments.size() + 1;
            Map<String, Object> appointment = new HashMap<>();
            appointment.put("id", newId);
            appointment.put("patientId", patientId.trim());
            appointment.put("centerId", centerIdLong);
            appointment.put("date", date.trim());
            appointment.put("status", "SCHEDULED");
            appointments.put(newId, appointment);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("Cita creada para paciente %s en centro %d", patientId, centerIdLong));
            response.put("appointment", appointment);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("ERROR en createAppointment: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al procesar la solicitud: " + e.getMessage());
            error.put("errorType", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(error);
        }
    }
}


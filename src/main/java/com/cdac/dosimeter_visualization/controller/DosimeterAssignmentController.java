package com.cdac.dosimeter_visualization.controller;

import com.cdac.dosimeter_visualization.model.DosimeterAssignment;
import com.cdac.dosimeter_visualization.service.DosimeterAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/assignments")
public class DosimeterAssignmentController {

    @Autowired
    private DosimeterAssignmentService service;

    @PostMapping("/assign")
    public ResponseEntity<DosimeterAssignment> assignDosimeter(
            @RequestParam Long userId,
            @RequestParam String deviceId) {
        DosimeterAssignment assignment = service.assignDeviceToUser(userId, deviceId);
        return ResponseEntity.ok(assignment);
    }

    @GetMapping("/get/json")
    public ResponseEntity<?> getJsonDate(
            @RequestParam Long userId,
            @RequestParam String deviceId,
            @RequestParam Double radiationValue) {
        Map<String, Object> response = new HashMap<>();
        DosimeterAssignment assignment = null;
        String status;
        try {
            assignment = service.assignDeviceToUser(userId, deviceId);
            if (assignment.getDosimeter().isActive()) {
                service.addReading(deviceId, radiationValue);
                status = "active";
            } else {
                status = "inactive";
            }
        } catch (RuntimeException e) {
            status = "inactive";
        }
        response.put("assignment", assignment);
        response.put("deviceStatus", status);
        return ResponseEntity.ok(response);
    }
}

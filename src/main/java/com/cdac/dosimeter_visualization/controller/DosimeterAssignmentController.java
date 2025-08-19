package com.cdac.dosimeter_visualization.controller;

import com.cdac.dosimeter_visualization.model.DosimeterAssignment;
import com.cdac.dosimeter_visualization.service.DosimeterAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}

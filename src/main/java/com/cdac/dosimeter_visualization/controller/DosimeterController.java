package com.cdac.dosimeter_visualization.controller;

import com.cdac.dosimeter_visualization.dto.DosimeterReadingRequestDTO;
import com.cdac.dosimeter_visualization.model.Dosimeter;
import com.cdac.dosimeter_visualization.model.DosimeterAssignment;
import com.cdac.dosimeter_visualization.model.User;
import com.cdac.dosimeter_visualization.repository.DosimeterAssignmentRepository;
import com.cdac.dosimeter_visualization.repository.DosimeterRepository;
import com.cdac.dosimeter_visualization.repository.UserRepository;
import com.cdac.dosimeter_visualization.service.DosimeterReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/dosimeter")
public class DosimeterController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DosimeterReadingService readingService;
    @Autowired
    private DosimeterRepository dosimeterRepo;
    @Autowired
    private DosimeterAssignmentRepository assignmentRepo;

    @PostMapping("/reading")
    public ResponseEntity<String> receiveReading(@RequestBody DosimeterReadingRequestDTO dto) {
        readingService.saveReading(dto);
        return ResponseEntity.ok("Reading saved successfully");
    }


    @PostMapping("/assign")
    public ResponseEntity<String> assignDosimeterToUser(@RequestParam Long userId, @RequestParam String deviceId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Dosimeter dosimeter = dosimeterRepo.findById(deviceId).orElseGet(() -> {
            Dosimeter d = new Dosimeter();
            d.setDeviceId(deviceId);
            return dosimeterRepo.save(d);
        });

        // Close any previous assignment
        assignmentRepo.findTopByDosimeter_DeviceIdAndReleasedAtIsNullOrderByAssignedAtDesc(deviceId)
                .ifPresent(prev -> {
                    prev.setReleasedAt(LocalDateTime.now());
                    assignmentRepo.save(prev);
                });

        DosimeterAssignment assignment = new DosimeterAssignment();
        assignment.setUser(userOpt.get());
        assignment.setDosimeter(dosimeter);
        assignment.setAssignedAt(LocalDateTime.now());

        assignmentRepo.save(assignment);

        return ResponseEntity.ok("Dosimeter assigned to user.");
    }

}

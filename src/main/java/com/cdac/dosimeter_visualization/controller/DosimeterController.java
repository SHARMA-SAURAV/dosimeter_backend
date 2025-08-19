package com.cdac.dosimeter_visualization.controller;

import com.cdac.dosimeter_visualization.dto.DosimeterReadingRequestDTO;
import com.cdac.dosimeter_visualization.model.Dosimeter;
import com.cdac.dosimeter_visualization.model.DosimeterAssignment;
import com.cdac.dosimeter_visualization.model.DosimeterReading;
import com.cdac.dosimeter_visualization.model.User;
import com.cdac.dosimeter_visualization.repository.DosimeterAssignmentRepository;
import com.cdac.dosimeter_visualization.repository.DosimeterRepository;
import com.cdac.dosimeter_visualization.repository.UserRepository;
import com.cdac.dosimeter_visualization.service.CsvExporter;
import com.cdac.dosimeter_visualization.service.DosimeterReadingService;
import com.cdac.dosimeter_visualization.service.DosimeterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dosimeter")
public class DosimeterController {

    @Autowired
    private DosimeterReadingService readingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DosimeterAssignmentRepository assignmentRepo;

    @Autowired
    private DosimeterRepository dosimeterRepo;

    @Autowired
    private DosimeterService dosimeterService;

    @Autowired
    private CsvExporter csvExporter;
    // Endpoint to assign user to dosimeter
    @PostMapping("/assign")
    public ResponseEntity<?> assignUserToDosimeter(@RequestParam Long userId, @RequestParam String deviceId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");

        // Find or create dosimeter
        Dosimeter dosimeter = dosimeterRepo.findById(deviceId).orElseGet(() -> {
            Dosimeter d = new Dosimeter();
            d.setDeviceId(deviceId);
            d.setHash("someHash"); // Optional or default
            return dosimeterRepo.save(d);
        });

        // Close previous assignment for this dosimeter
        assignmentRepo.findTopByDosimeter_DeviceIdAndReleasedAtIsNullOrderByAssignedAtDesc(deviceId)
                .ifPresent(prev -> {
                    prev.setReleasedAt(LocalDateTime.now());
                    assignmentRepo.save(prev);
                });

        // Create new assignment
        DosimeterAssignment newAssignment = new DosimeterAssignment();
        newAssignment.setUser(userOpt.get());
        newAssignment.setDosimeter(dosimeter);
        newAssignment.setAssignedAt(LocalDateTime.now());

        assignmentRepo.save(newAssignment);
        return ResponseEntity.ok("User assigned to dosimeter");
    }


    // Endpoint for real-time JSON readings
    @PostMapping("/reading")
    public ResponseEntity<?> receiveReading(@RequestBody DosimeterReadingRequestDTO dto) {
        boolean alert = readingService.saveReadingWithAlert(dto);
        return ResponseEntity.ok(alert ? "Alert: High radiation!" : "Reading stored");
    }


    @GetMapping("/{deviceId}/export/csv")
    public void exportToCSV(@PathVariable String deviceId, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        String filename = "dosimeter_readings_" + deviceId + ".csv";
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        List<DosimeterReading> readings = dosimeterService.getReadingsByDeviceId(deviceId);
        csvExporter.export(response.getWriter(), readings);  // ✅ Correct order
    }


    @PostMapping("/{assignmentId}/export-json")
    public ResponseEntity<String> exportAssignmentToJson(@PathVariable Long assignmentId) throws IOException {
        DosimeterAssignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        List<DosimeterReading> readings = assignment.getReadings();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(readings);

        String fileName = "dosimeter_" + assignment.getDosimeter().getDeviceId() + "_" + assignment.getAssignedAt().toLocalDate() + ".json";
        String desktopPath = System.getProperty("user.home") + "/Desktop/" + fileName;
        Files.write(Paths.get(desktopPath), json.getBytes());

        assignment.setReleasedAt(LocalDateTime.now());
        assignmentRepo.save(assignment);

        return ResponseEntity.ok("Exported to Desktop and marked as inactive.");
    }


}


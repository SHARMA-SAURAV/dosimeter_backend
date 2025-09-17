package com.cdac.dosimeter_visualization.controller;

import com.cdac.dosimeter_visualization.dto.DosimeterReadingRequestDTO;
import com.cdac.dosimeter_visualization.model.DemoJson;
import com.cdac.dosimeter_visualization.model.DosimeterAssignment;
import com.cdac.dosimeter_visualization.model.DosimeterReading;
import com.cdac.dosimeter_visualization.model.User;
import com.cdac.dosimeter_visualization.repository.DosimeterReadingRepository;
import com.cdac.dosimeter_visualization.repository.UserRepository;
import com.cdac.dosimeter_visualization.service.CsvExporter;
import com.cdac.dosimeter_visualization.service.DosimeterAssignmentService;
import com.cdac.dosimeter_visualization.service.DosimeterReadingService;
import com.cdac.dosimeter_visualization.service.DosimeterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/dosimeter")
public class DosimeterController {

    @Autowired
    private DosimeterAssignmentService assignmentService;

    @Autowired
    private DosimeterReadingService readingService;

    @Autowired
    private DosimeterService dosimeterService; // ✅ Add DosimeterService

    @Autowired
    private DosimeterReadingRepository readingRepository;

    @Autowired
    private UserRepository userRepository; // ✅ Add UserRepository

    @Autowired
    private CsvExporter csvExporter;

    // Get demo JSON data (simulates dosimeter data)
    @GetMapping("/demo/data")
    public ResponseEntity<DemoJson> getDemoData() {
        DemoJson demoData = new DemoJson();

        // Process the demo data through dosimeter service
        DosimeterReadingRequestDTO dto = new DosimeterReadingRequestDTO();
        dto.setDeviceId(demoData.getDeviceId());
        dto.setCpm(demoData.getCpm() != null && !demoData.getCpm().trim().isEmpty()
                ? Double.valueOf(demoData.getCpm().trim()) : null);
        dto.setTimestamp(LocalDateTime.now());

        // Save reading and check for alerts
        boolean alert = readingService.saveReadingWithAlert(dto);

        return ResponseEntity.ok(demoData);
    }


    // ✅ Get devices that need assignment
    @GetMapping("/devices/needing-assignment")
    public ResponseEntity<Set<String>> getDevicesNeedingAssignment() {
        Set<String> devices = dosimeterService.getDevicesNeedingAssignment();
        return ResponseEntity.ok(devices);
    }

    // ✅ Get all available users for assignment
    @GetMapping("/users/available")
    public ResponseEntity<List<User>> getAvailableUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // ✅ Get active devices status
    @GetMapping("/devices/active")
    public ResponseEntity<?> getActiveDevices() {
        Set<String> activeDevices = dosimeterService.getActiveDevices();
        return ResponseEntity.ok(activeDevices);
    }

    // Get device status and latest reading
    @GetMapping("/device/{deviceId}/status")
    public ResponseEntity<DeviceStatusResponse> getDeviceStatus(@PathVariable String deviceId) {
        String status = dosimeterService.getDeviceStatus(deviceId);
        DosimeterReading latestReading = dosimeterService.getLatestReading(deviceId);
        boolean hasActiveAssignment = assignmentService.getActiveAssignment(deviceId).isPresent();

        return ResponseEntity.ok(new DeviceStatusResponse(deviceId, status, latestReading, hasActiveAssignment));
    }

    // Assign device to user (creates device if not present, releases previous assignment)
    @PostMapping("/assign")
    public ResponseEntity<?> assignDevice(@RequestParam Long userId, @RequestParam String deviceId) {
        try {
            // Check if device is active
            String deviceStatus = dosimeterService.getDeviceStatus(deviceId);
            if (!"Active".equalsIgnoreCase(deviceStatus)) {
                return ResponseEntity.badRequest().body("Cannot assign inactive device");
            }

            DosimeterAssignment assignment = assignmentService.assignDeviceToUser(userId, deviceId);

            // Assign any unassigned readings to this assignment
            readingService.assignReadingsToAssignment(deviceId, assignment);

            // Mark device as assignment processed
            dosimeterService.markDeviceAssignmentProcessed(deviceId);

            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Assignment failed: " + e.getMessage());
        }
    }
    // Release current assignment for device
    @PostMapping("/release")
    public ResponseEntity<?> releaseDevice(@RequestParam String deviceId) {
        try {
            Optional<DosimeterAssignment> assignment = assignmentService.getActiveAssignment(deviceId);
            if (assignment.isEmpty()) {
                return ResponseEntity.badRequest().body("No active assignment for device");
            }

            assignmentService.releaseAssignment(assignment.get().getId());
            return ResponseEntity.ok("Device released successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Release failed: " + e.getMessage());
        }
    }




    // Save reading (links to assignment if present)
    @PostMapping("/reading")
    public ResponseEntity<?> reading(@RequestBody DosimeterReadingRequestDTO dto) {
        try {
            boolean alert = readingService.saveReadingWithAlert(dto);
            return ResponseEntity.ok(alert ? "Alert saved" : "Reading processed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing reading: " + e.getMessage());
        }
    }

    // Accept DemoJson style object for device data
    @PostMapping("/device/data")
    public ResponseEntity<?> deviceData(@RequestBody Object payload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            DemoJson demo = mapper.convertValue(payload, DemoJson.class);
            Double cpm = null;
            try {
                if (demo.getCpm() != null && !demo.getCpm().trim().isEmpty()) {
                    cpm = Double.valueOf(demo.getCpm().trim());
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid CPM value: " + demo.getCpm());
            }
            DosimeterReadingRequestDTO dto = new DosimeterReadingRequestDTO();
            dto.setDeviceId(demo.getDeviceId());
            dto.setCpm(cpm);
            dto.setTimestamp(LocalDateTime.now());
            boolean alert = readingService.saveReadingWithAlert(dto);
            return ResponseEntity.ok(alert ? "Alert saved" : "Reading processed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid payload: " + e.getMessage());
        }
    }

    // Exports
//    @GetMapping("/{deviceId}/export/device-csv")
//    public void exportDeviceCsv(@PathVariable String deviceId, HttpServletResponse response) throws IOException {
//        List<DosimeterReading> readings = readingRepository.findByDeviceId(deviceId);
//        response.setContentType("text/csv");
//        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"device_" + deviceId + ".csv\"");
//        csvExporter.exportReadings(readings, response.getWriter());
//    }
//
//    @GetMapping("/user/{userId}/export/user-csv")
//    public void exportUserCsv(@PathVariable Long userId, HttpServletResponse response) throws IOException {
//        List<DosimeterReading> readings = readingRepository.findByAssignment_User_Id(userId);
//        response.setContentType("text/csv");
//        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"user_" + userId + ".csv\"");
//        csvExporter.exportReadings(readings, response.getWriter());
//    }
// Get readings for a user
    @GetMapping("/user/{userId}/readings")
    public ResponseEntity<List<DosimeterReading>> getReadingsByUserId(@PathVariable Long userId) {
        List<DosimeterReading> readings = readingService.getReadingsByUserId(userId);
        return ResponseEntity.ok(readings);
    }

    // Get alert readings for user (CPM > 100)
    @GetMapping("/user/{userId}/alerts")
    public ResponseEntity<List<DosimeterReading>> getAlertsByUserId(@PathVariable Long userId) {
        List<DosimeterReading> alerts = readingService.getAlertReadingsByUserId(userId);
        return ResponseEntity.ok(alerts);
    }
    // Get readings endpoints
    @GetMapping("/device/{deviceId}/readings")
    public ResponseEntity<List<DosimeterReading>> getReadingsByDeviceId(@PathVariable String deviceId) {
        List<DosimeterReading> readings = readingService.getReadingsByDeviceId(deviceId);
        return ResponseEntity.ok(readings);
    }
    // Get current assignment for device
    @GetMapping("/device/{deviceId}/assignment")
    public ResponseEntity<?> getCurrentAssignment(@PathVariable String deviceId) {
        Optional<DosimeterAssignment> assignment = assignmentService.getActiveAssignment(deviceId);
        if (assignment.isPresent()) {
            return ResponseEntity.ok(assignment.get());
        }
        return ResponseEntity.ok("No active assignment");
    }

    // Export device readings to CSV
    @GetMapping("/{deviceId}/export/device-csv")
    public void exportDeviceCsv(@PathVariable String deviceId, HttpServletResponse response) throws IOException {
        List<DosimeterReading> readings = readingRepository.findByDeviceId(deviceId);
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"device_" + deviceId + ".csv\"");
        csvExporter.exportReadings(readings, response.getWriter());
    }

    // Export user readings to CSV
    @GetMapping("/user/{userId}/export/user-csv")
    public void exportUserCsv(@PathVariable Long userId, HttpServletResponse response) throws IOException {
        List<DosimeterReading> readings = readingRepository.findByAssignment_User_Id(userId);
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"user_" + userId + ".csv\"");
        csvExporter.exportReadings(readings, response.getWriter());
    }



    public static class DeviceStatusResponse {
        public String deviceId;
        public String status;
        public DosimeterReading latestReading;
        public boolean hasActiveAssignment;

        public DeviceStatusResponse(String deviceId, String status, DosimeterReading latestReading, boolean hasActiveAssignment) {
            this.deviceId = deviceId;
            this.status = status;
            this.latestReading = latestReading;
            this.hasActiveAssignment = hasActiveAssignment;
        }
    }
}
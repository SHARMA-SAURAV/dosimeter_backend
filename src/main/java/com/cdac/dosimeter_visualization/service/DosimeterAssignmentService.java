//package com.cdac.dosimeter_visualization.service;
//
//import com.cdac.dosimeter_visualization.model.Dosimeter;
//import com.cdac.dosimeter_visualization.model.DosimeterAssignment;
//import com.cdac.dosimeter_visualization.model.DosimeterReading;
//import com.cdac.dosimeter_visualization.model.User;
//import com.cdac.dosimeter_visualization.repository.DosimeterAssignmentRepository;
//import com.cdac.dosimeter_visualization.repository.DosimeterReadingRepository;
//import com.cdac.dosimeter_visualization.repository.DosimeterRepository;
//import com.cdac.dosimeter_visualization.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Objects;
//import java.util.Optional;
//@Service
//public class DosimeterAssignmentService {
//
//    @Autowired
//    private DosimeterRepository dosimeterRepo;
//
//    @Autowired
//    private UserRepository userRepo;
//
//    @Autowired
//    private DosimeterAssignmentRepository assignmentRepo;
//
//    @Autowired
//    private DosimeterReadingRepository dosimeterReadingRepo;
//
//    public DosimeterAssignment assignDeviceToUser(Long userId, String deviceId) {
//        Dosimeter dosimeter = dosimeterRepo.findById(deviceId).orElseGet(() -> {
//            Dosimeter newDosimeter = new Dosimeter();
//            newDosimeter.setDeviceId(deviceId);
//            newDosimeter.setActive(true);
//            newDosimeter.setHash(""); // Set hash if needed
//            return dosimeterRepo.save(newDosimeter);
//        });
//
//        // 1. Check if device is active
//        if (!dosimeter.isActive()) {
//            throw new RuntimeException("Device is inactive. Cannot assign.");
//        }
//
//        User user = userRepo.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // 2. Check if this device is already assigned and still active
//        Optional<DosimeterAssignment> currentAssignment =
//                assignmentRepo.findByDosimeterAndReleasedAtIsNull(dosimeter);
//
//        if (currentAssignment.isPresent()) {
//            DosimeterAssignment assignment = currentAssignment.get();
//
////           import java.util.Objects;
//
//            if (Objects.equals(assignment.getUser().getId(), userId)) {
//                // Same user is still assigned → continue readings
//                return assignment;
//            } else {
//                // Device was assigned to another user → release it first
//                assignment.setReleasedAt(LocalDateTime.now());
//                assignmentRepo.save(assignment);
//            }
//        }
//
//        // 3. Create new assignment
//        DosimeterAssignment newAssignment = new DosimeterAssignment();
//        newAssignment.setUser(user);
//        newAssignment.setDosimeter(dosimeter);
//        newAssignment.setAssignedDate(LocalDate.now());
//        newAssignment.setAssignedAt(LocalDateTime.now());
//
//        return assignmentRepo.save(newAssignment);
//    }
//
//    public void addReading(String deviceId, Double radiationValue) {
//        Dosimeter dosimeter = dosimeterRepo.findById(deviceId).orElseGet(() -> {
//            Dosimeter newDosimeter = new Dosimeter();
//            newDosimeter.setDeviceId(deviceId);
//            newDosimeter.setActive(true);
//            newDosimeter.setHash(""); // Set hash if needed
//            return dosimeterRepo.save(newDosimeter);
//        });
//
//        Optional<DosimeterAssignment> assignmentOpt =
//                assignmentRepo.findByDosimeterAndReleasedAtIsNull(dosimeter);
//
//        if (assignmentOpt.isPresent()) {
//            DosimeterAssignment assignment = assignmentOpt.get();
//
//            DosimeterReading reading = new DosimeterReading();
//            reading.setAssignment(assignment);
//            reading.setCpm(radiationValue);
//            reading.setDeviceId(deviceId);
//            reading.setTimestamp(LocalDateTime.now());
//            reading.setStatus("ACTIVE");
//            reading.setBattery("N/A");
//            reading.updateRadiationLevel();
//
//            // Save reading to repository so it persists in dosimeter_reading table
//            dosimeterReadingRepo.save(reading);
//
//            assignment.getReadings().add(reading);
//
//            // 4. Radiation threshold check
//            double totalRadiation = assignment.getReadings().stream()
//                    .mapToDouble(DosimeterReading::getCpm)
//                    .sum();
//
//            if (totalRadiation > 1000) { // Example threshold
//                System.out.println("⚠ ALERT: Radiation limit exceeded for user " +
//                        assignment.getUser().getName());
//            }
//
//            assignmentRepo.save(assignment);
//        } else {
//            throw new RuntimeException("Device not assigned. Cannot log readings.");
//        }
//    }
//
//    public Optional<DosimeterAssignment> getActiveAssignment(String deviceId) {
//        Dosimeter dosimeter = dosimeterRepo.findById(deviceId).orElse(null);
//        if (dosimeter == null) {
//            return Optional.empty();
//        }
//
//        return assignmentRepo.findByDosimeterAndReleasedAtIsNull(dosimeter);
//
//    }
//
//    public void releaseAssignment(Long id) {
//    }
//}






































package com.cdac.dosimeter_visualization.service;

import com.cdac.dosimeter_visualization.model.Dosimeter;
import com.cdac.dosimeter_visualization.model.DosimeterAssignment;
import com.cdac.dosimeter_visualization.model.User;
import com.cdac.dosimeter_visualization.repository.DosimeterAssignmentRepository;
import com.cdac.dosimeter_visualization.repository.DosimeterRepository;
import com.cdac.dosimeter_visualization.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class DosimeterAssignmentService {

    @Autowired
    private DosimeterRepository dosimeterRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private DosimeterAssignmentRepository assignmentRepo;

    public DosimeterAssignment assignDeviceToUser(Long userId, String deviceId) {
        // Find or create dosimeter
        Dosimeter dosimeter = dosimeterRepo.findById(deviceId).orElseGet(() -> {
            Dosimeter newDosimeter = new Dosimeter();
            newDosimeter.setDeviceId(deviceId);
            newDosimeter.setActive(true);
            newDosimeter.setHash("");
            return dosimeterRepo.save(newDosimeter);
        });

        // Check if device is active
        if (!dosimeter.isActive()) {
            throw new RuntimeException("Device is inactive. Cannot assign.");
        }

        // Find user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check current assignment
        Optional<DosimeterAssignment> currentAssignment = getActiveAssignment(deviceId);

        if (currentAssignment.isPresent()) {
            DosimeterAssignment assignment = currentAssignment.get();
            if (Objects.equals(assignment.getUser().getId(), userId)) {
                // Same user - return existing assignment
                return assignment;
            } else {
                // Different user - release current assignment
                releaseAssignment(assignment.getId());
            }
        }

        // Create new assignment
        DosimeterAssignment newAssignment = new DosimeterAssignment();
        newAssignment.setUser(user);
        newAssignment.setDosimeter(dosimeter);
        newAssignment.setAssignedDate(LocalDate.now());
        newAssignment.setAssignedAt(LocalDateTime.now());

        return assignmentRepo.save(newAssignment);
    }

    public Optional<DosimeterAssignment> getActiveAssignment(String deviceId) {
        return assignmentRepo.findTopByDosimeter_DeviceIdAndReleasedAtIsNullOrderByAssignedAtDesc(deviceId);
    }

    public void releaseAssignment(Long assignmentId) {
        Optional<DosimeterAssignment> assignment = assignmentRepo.findById(assignmentId);
        if (assignment.isPresent()) {
            DosimeterAssignment a = assignment.get();
            a.setReleasedAt(LocalDateTime.now());
            assignmentRepo.save(a);
        }
    }
}
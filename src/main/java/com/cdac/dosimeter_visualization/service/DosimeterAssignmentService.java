package com.cdac.dosimeter_visualization.service;

import com.cdac.dosimeter_visualization.model.Dosimeter;
import com.cdac.dosimeter_visualization.model.DosimeterAssignment;
import com.cdac.dosimeter_visualization.model.DosimeterReading;
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
        Dosimeter dosimeter = dosimeterRepo.findById(deviceId).orElseGet(() -> {
                    Dosimeter newDosimeter = new Dosimeter();
                    newDosimeter.setDeviceId(deviceId);
                    newDosimeter.setActive(true);
                    newDosimeter.setHash(""); // Set hash if needed
                    return dosimeterRepo.save(newDosimeter);
                });

        // 1. Check if device is active
        if (!dosimeter.isActive()) {
            throw new RuntimeException("Device is inactive. Cannot assign.");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Check if this device is already assigned and still active
        Optional<DosimeterAssignment> currentAssignment =
                assignmentRepo.findByDosimeterAndReleasedAtIsNull(dosimeter);

        if (currentAssignment.isPresent()) {
            DosimeterAssignment assignment = currentAssignment.get();

//           import java.util.Objects;

            if (Objects.equals(assignment.getUser().getId(), userId)) {
                // Same user is still assigned → continue readings
                return assignment;
            }
            else {
                // Device was assigned to another user → release it first
                assignment.setReleasedAt(LocalDateTime.now());
                assignmentRepo.save(assignment);
            }
        }

        // 3. Create new assignment
        DosimeterAssignment newAssignment = new DosimeterAssignment();
        newAssignment.setUser(user);
        newAssignment.setDosimeter(dosimeter);
        newAssignment.setAssignedDate(LocalDate.now());
        newAssignment.setAssignedAt(LocalDateTime.now());

        return assignmentRepo.save(newAssignment);
    }

    public void addReading(String deviceId, Double radiationValue) {
        Dosimeter dosimeter = dosimeterRepo.findById(deviceId).orElseGet(() -> {
            Dosimeter newDosimeter = new Dosimeter();
            newDosimeter.setDeviceId(deviceId);
            newDosimeter.setActive(true);
            newDosimeter.setHash(""); // Set hash if needed
            return dosimeterRepo.save(newDosimeter);
        });

        // Only add readings if assigned
        Optional<DosimeterAssignment> assignmentOpt =
                assignmentRepo.findByDosimeterAndReleasedAtIsNull(dosimeter);

        if (assignmentOpt.isPresent()) {
            DosimeterAssignment assignment = assignmentOpt.get();

            DosimeterReading reading = new DosimeterReading();
            reading.setAssignment(assignment);
            reading.setAssignment(assignment);  // assignment already contains dosimeter
            reading.setCpm(radiationValue);     // store radiation value in cpm

            reading.setTimestamp(LocalDateTime.now());

            assignment.getReadings().add(reading);

            // 4. Radiation threshold check
            double totalRadiation = assignment.getReadings().stream()
                    .mapToDouble(DosimeterReading::getCpm)
                    .sum();

            if (totalRadiation > 1000) { // Example threshold
                System.out.println("⚠ ALERT: Radiation limit exceeded for user " +
                        assignment.getUser().getName());
            }

            assignmentRepo.save(assignment);
        } else {
            throw new RuntimeException("Device not assigned. Cannot log readings.");
        }
    }
}

//package com.cdac.dosimeter_visualization.service;
//
//import com.cdac.dosimeter_visualization.dto.DosimeterReadingRequestDTO;
//import com.cdac.dosimeter_visualization.model.DosimeterAssignment;
//import com.cdac.dosimeter_visualization.model.DosimeterReading;
//import com.cdac.dosimeter_visualization.repository.DosimeterAssignmentRepository;
//import com.cdac.dosimeter_visualization.repository.DosimeterReadingRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class DosimeterReadingService {
//
//    @Autowired
//    private DosimeterReadingRepository readingRepo;
//
//    @Autowired
//    private DosimeterAssignmentRepository assignmentRepo;
//
//    public boolean saveReadingWithAlert(DosimeterReadingRequestDTO dto) {
//        // find active assignment for this device
//        Optional<DosimeterAssignment> assignmentOpt =
//                assignmentRepo.findTopByDosimeter_DeviceIdAndReleasedAtIsNullOrderByAssignedAtDesc(dto.getDeviceId());
//
//        if (assignmentOpt.isEmpty()) {
//            // Device not assigned to any user → ignore or just log
//            return false;
//        }
//
//        DosimeterAssignment assignment = assignmentOpt.get();
//
//        // save reading linked to assignment
//        DosimeterReading reading = new DosimeterReading();
//        reading.setDeviceId(dto.getDeviceId());
//        reading.setCpm(dto.getCpm() != null ? dto.getCpm().toString() : null);
//        reading.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now());
//        reading.setStatus("ACTIVE");
//        reading.setBattery("N/A"); // or dto.getBattery if you have it
//        reading.setAssignment(assignment);
//
//        readingRepo.save(reading);
//
//        // simple alert logic
//        boolean alert = dto.getCpm() != null && dto.getCpm() > 100.0;
//        return alert;
//    }
//
//    public List<DosimeterReading> getReadingsByDeviceId(String deviceId) {
//        return readingRepo.findByDeviceId(deviceId);
//    }
//
//    public List<DosimeterReading> getReadingsByUserId(Long userId) {
//        return readingRepo.findByAssignment_User_Id(userId);
//    }
//}




























package com.cdac.dosimeter_visualization.service;

import com.cdac.dosimeter_visualization.dto.DosimeterReadingRequestDTO;
import com.cdac.dosimeter_visualization.model.DosimeterAssignment;
import com.cdac.dosimeter_visualization.model.DosimeterReading;
import com.cdac.dosimeter_visualization.repository.DosimeterAssignmentRepository;
import com.cdac.dosimeter_visualization.repository.DosimeterReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DosimeterReadingService {

    @Autowired
    private DosimeterReadingRepository readingRepo;

    @Autowired
    private DosimeterAssignmentRepository assignmentRepo;

    @Autowired
    private DosimeterService dosimeterService; // ✅ Add reference to DosimeterService
    private static final double ALERT_THRESHOLD=100.0;
    public boolean saveReadingWithAlert(DosimeterReadingRequestDTO dto) {
        // ✅ Create reading object first
        DosimeterReading reading = new DosimeterReading();
        reading.setDeviceId(dto.getDeviceId());
        reading.setCpm(dto.getCpm()); // ✅ Now using Double directly
        reading.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now());
        reading.setStatus("Active");
        reading.setBattery("N/A"); // or dto.getBattery if available

        // ✅ Update radiation level
        reading.updateRadiationLevel();

        // ✅ Add to DosimeterService for tracking
        dosimeterService.addReading(reading);

        // Find active assignment for this device
        Optional<DosimeterAssignment> assignmentOpt =
                assignmentRepo.findTopByDosimeter_DeviceIdAndReleasedAtIsNullOrderByAssignedAtDesc(dto.getDeviceId());

        boolean alert = false;
        if (assignmentOpt.isPresent()) {
            // ✅ Device is assigned - save reading with assignment
            DosimeterAssignment assignment = assignmentOpt.get();
            reading.setAssignment(assignment);
//            readingRepo.save(reading);

            // ✅ Check for alert condition
//            boolean alert = dto.getCpm() != null && dto.getCpm() > 100.0;
            if (dto.getCpm() != null && dto.getCpm() > ALERT_THRESHOLD) {
                reading.setAlertTriggered(true);
                alert = true;
                System.out.println("⚠️ ALERT: High radiation detected! CPM: " + dto.getCpm() +
                        " for device: " + dto.getDeviceId() +
                        " assigned to user: " + assignmentOpt.get().getUser().getName());
            }
        }

        readingRepo.save(reading);
        return alert;
    }

    // ✅ Get unassigned readings for a device
    public List<DosimeterReading> getUnassignedReadings(String deviceId) {
        return readingRepo.findByDeviceIdAndAssignmentIsNull(deviceId);
    }

    // ✅ Assign unassigned readings to a new assignment
    public void assignReadingsToAssignment(String deviceId, DosimeterAssignment assignment) {
        List<DosimeterReading> unassignedReadings = getUnassignedReadings(deviceId);
        for (DosimeterReading reading : unassignedReadings) {
            reading.setAssignment(assignment);
            readingRepo.save(reading);
        }
    }

//    public List<DosimeterReading> getReadingsByDeviceId(String deviceId) {
//        return readingRepo.findByDeviceId(deviceId);
//    }

//    public List<DosimeterReading> getReadingsByUserId(Long userId) {
//        return readingRepo.findByAssignment_User_Id(userId);
//    }

    // ✅ Get readings for user within date range
//    public List<DosimeterReading> getReadingsByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
//        return readingRepo.findByAssignment_User_IdAndTimestampBetween(userId, startDate, endDate);
//    }

    // ✅ Get alert readings for user
//    public List<DosimeterReading> getAlertReadingsByUserId(Long userId) {
//        return readingRepo.findByAssignment_User_IdAndAlertTriggeredTrue(userId);
//    }

    public List<DosimeterReading> getReadingsByDeviceId(String deviceId) {
        return readingRepo.findByDeviceIdOrderByTimestampDesc(deviceId);
    }

    public List<DosimeterReading> getReadingsByUserId(Long userId) {
        return readingRepo.findByAssignment_User_IdOrderByTimestampDesc(userId);
    }

    public List<DosimeterReading> getAlertReadingsByUserId(Long userId) {
        return readingRepo.findByAssignment_User_IdAndAlertTriggeredTrue(userId);
    }

    public List<DosimeterReading> getReadingsByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return readingRepo.findByAssignment_User_IdAndTimestampBetween(userId, startDate, endDate);
    }
}
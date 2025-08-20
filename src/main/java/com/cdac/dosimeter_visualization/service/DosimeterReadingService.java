package com.cdac.dosimeter_visualization.service;

import com.cdac.dosimeter_visualization.dto.DosimeterReadingRequestDTO;
import com.cdac.dosimeter_visualization.model.DosimeterAssignment;
import com.cdac.dosimeter_visualization.model.DosimeterReading;
import com.cdac.dosimeter_visualization.repository.DosimeterAssignmentRepository;
import com.cdac.dosimeter_visualization.repository.DosimeterReadingRepository;
import com.cdac.dosimeter_visualization.repository.DosimeterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class DosimeterReadingService {

    private static final double RADIATION_THRESHOLD = 10.0; // Example threshold

    @Autowired
    private DosimeterAssignmentRepository assignmentRepo;

    @Autowired
    private DosimeterReadingRepository readingRepo;

    public boolean saveReadingWithAlert(DosimeterReadingRequestDTO dto) {
        Optional<DosimeterAssignment> optionalAssignment =
                assignmentRepo.findTopByDosimeter_DeviceIdAndReleasedAtIsNullOrderByAssignedAtDesc(dto.getDeviceId());

        if (optionalAssignment.isEmpty()) {
            throw new RuntimeException("No active user assigned to device: " + dto.getDeviceId());
        }

        DosimeterAssignment assignment = optionalAssignment.get();

        DosimeterReading reading = new DosimeterReading();
        reading.setCpm(Double.parseDouble(dto.getCpm()));
//        reading.setDate(LocalDate.parse(dto.getDate()));
//        reading.setTime(LocalTime.parse(dto.getTime()));
        reading.setBattery(Integer.parseInt(dto.getBattery()));
        reading.setStatus(DosimeterReading.Status.valueOf(dto.getStatus()));
        reading.setAssignment(assignment);
        readingRepo.save(reading);

        // Check if radiation threshold is breached
        double cpmValue = Double.parseDouble(dto.getCpm());
        return cpmValue > RADIATION_THRESHOLD;
    }
}

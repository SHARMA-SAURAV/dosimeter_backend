package com.cdac.dosimeter_visualization.service;

import com.cdac.dosimeter_visualization.dto.DosimeterReadingRequestDTO;
import com.cdac.dosimeter_visualization.model.DosimeterAssignment;
import com.cdac.dosimeter_visualization.model.DosimeterReading;
import com.cdac.dosimeter_visualization.repository.DosimeterAssignmentRepository;
import com.cdac.dosimeter_visualization.repository.DosimeterReadingRepository;
import com.cdac.dosimeter_visualization.repository.DosimeterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DosimeterReadingService {

    @Autowired
    private DosimeterAssignmentRepository assignmentRepo;

    @Autowired
    private DosimeterReadingRepository readingRepo;

    @Autowired
    private DosimeterRepository dosimeterRepo;

    public void saveReading(DosimeterReadingRequestDTO dto) {
        // Find the active assignment (user currently using the dosimeter)
        Optional<DosimeterAssignment> optionalAssignment =
                assignmentRepo.findTopByDosimeter_DeviceIdAndReleasedAtIsNullOrderByAssignedAtDesc(dto.getDeviceId());

        if (optionalAssignment.isEmpty()) {
            throw new RuntimeException("No active user for dosimeter " + dto.getDeviceId());
        }

        DosimeterAssignment assignment = optionalAssignment.get();

        DosimeterReading reading = new DosimeterReading();
        reading.setCpm(dto.getCpm());
        reading.setDate(dto.getDate());
        reading.setTime(dto.getTime());
        reading.setBattery(dto.getBattery());
        reading.setStatus(dto.getStatus());
        reading.setAssignment(assignment);

        readingRepo.save(reading);
    }
}

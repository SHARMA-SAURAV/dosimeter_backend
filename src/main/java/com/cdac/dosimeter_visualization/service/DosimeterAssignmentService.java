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

@Service
public class DosimeterAssignmentService {

    @Autowired
    private UserRepository userRepo;
    @Autowired private DosimeterRepository dosimeterRepo;
    @Autowired private DosimeterAssignmentRepository assignmentRepo;

    public DosimeterAssignment assignDeviceToUser(Long userId, String deviceId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Dosimeter dosimeter = dosimeterRepo.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Dosimeter not found"));

        DosimeterAssignment assignment = new DosimeterAssignment();
        assignment.setUser(user);
        assignment.setDosimeter(dosimeter);
        assignment.setAssignedDate(LocalDate.now());

        return assignmentRepo.save(assignment);
    }
}

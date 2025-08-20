package com.cdac.dosimeter_visualization.repository;

import com.cdac.dosimeter_visualization.model.Dosimeter;
import com.cdac.dosimeter_visualization.model.DosimeterAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DosimeterAssignmentRepository extends JpaRepository<DosimeterAssignment, Long> {
    Optional<DosimeterAssignment> findTopByDosimeter_DeviceIdAndReleasedAtIsNullOrderByAssignedAtDesc(String deviceId);
    Optional<DosimeterAssignment> findByDosimeterAndReleasedAtIsNull(Dosimeter dosimeter);
}


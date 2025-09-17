package com.cdac.dosimeter_visualization.repository;

import com.cdac.dosimeter_visualization.model.DosimeterReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DosimeterReadingRepository extends JpaRepository<DosimeterReading, Long> {

//    // Works because deviceId column exists directly in dosimeter_reading table
//    List<DosimeterReading> findByDeviceId(String deviceId);
//
//    // Works because assignment links to user
//    List<DosimeterReading> findByAssignment_User_Id(Long userId);



    // Existing methods
    List<DosimeterReading> findByDeviceId(String deviceId);
    List<DosimeterReading> findByAssignment_User_Id(Long userId);

    // ✅ Additional methods needed
    List<DosimeterReading> findByDeviceIdAndAssignmentIsNull(String deviceId);
    List<DosimeterReading> findByAssignment_User_IdAndAlertTriggeredTrue(Long userId);
    List<DosimeterReading> findByAssignment_User_IdAndTimestampBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    List<DosimeterReading> findByDeviceIdOrderByTimestampDesc(String deviceId);
    List<DosimeterReading> findByAssignment_User_IdOrderByTimestampDesc(Long userId);
}

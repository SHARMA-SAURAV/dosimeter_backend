package com.cdac.dosimeter_visualization.service;

import com.cdac.dosimeter_visualization.model.DosimeterReading;
import com.cdac.dosimeter_visualization.model.Dosimeter;
import com.cdac.dosimeter_visualization.repository.DosimeterRepository;
import com.cdac.dosimeter_visualization.repository.DosimeterAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DosimeterService {

    @Autowired
    private DosimeterRepository dosimeterRepository;

    @Autowired
    private DosimeterAssignmentRepository assignmentRepository;

    // Track device data and status
    private final Map<String, List<DosimeterReading>> dataMap = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastUpdate = new ConcurrentHashMap<>();
    private final Set<String> stoppedDevices = Collections.synchronizedSet(new HashSet<>());
    private final Set<String> newlyActiveDevices = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, String> deviceStatusMap = new ConcurrentHashMap<>();

    public void addReading(DosimeterReading reading) {
        String deviceId = reading.getDeviceId();
        String currentStatus = reading.getStatus();

        // Check if device just became active
        boolean wasInactive = stoppedDevices.contains(deviceId) ||
                !"Active".equalsIgnoreCase(deviceStatusMap.get(deviceId));

        // Update device status
        deviceStatusMap.put(deviceId, currentStatus);
        lastUpdate.put(deviceId, LocalDateTime.now());

        // If device became active, mark for potential assignment
        if ("Active".equalsIgnoreCase(currentStatus) && wasInactive) {
            newlyActiveDevices.add(deviceId);
            stoppedDevices.remove(deviceId);
            ensureDosimeterExists(deviceId);
        }

        // Add reading to memory
        dataMap.computeIfAbsent(deviceId, k -> new ArrayList<>()).add(reading);

        // Remove from stopped devices if active
        if ("Active".equalsIgnoreCase(currentStatus)) {
            stoppedDevices.remove(deviceId);
        }
    }

    private void ensureDosimeterExists(String deviceId) {
        dosimeterRepository.findById(deviceId).orElseGet(() -> {
            Dosimeter dosimeter = new Dosimeter();
            dosimeter.setDeviceId(deviceId);
            dosimeter.setActive(true);
            dosimeter.setHash("");
            return dosimeterRepository.save(dosimeter);
        });
    }

    public Set<String> getDevicesNeedingAssignment() {
        Set<String> needAssignment = new HashSet<>();

        for (String deviceId : newlyActiveDevices) {
            // Only show devices that are currently active and have no assignment
            boolean isActive = "Active".equalsIgnoreCase(deviceStatusMap.get(deviceId));
            boolean hasActiveAssignment = assignmentRepository
                    .findTopByDosimeter_DeviceIdAndReleasedAtIsNullOrderByAssignedAtDesc(deviceId)
                    .isPresent();

            if (isActive && !hasActiveAssignment) {
                needAssignment.add(deviceId);
            }
        }

        return needAssignment;
    }

    public void markDeviceAssignmentProcessed(String deviceId) {
        newlyActiveDevices.remove(deviceId);
    }

    public List<DosimeterReading> getDeviceData(String deviceId) {
        return dataMap.getOrDefault(deviceId, new ArrayList<>());
    }

    public Set<String> getActiveDevices() {
        Set<String> active = new HashSet<>();
        deviceStatusMap.entrySet().stream()
                .filter(entry -> "Active".equalsIgnoreCase(entry.getValue()))
                .filter(entry -> !stoppedDevices.contains(entry.getKey()))
                .forEach(entry -> active.add(entry.getKey()));
        return active;
    }

    public Set<String> getStoppedDevices() {
        return new HashSet<>(stoppedDevices);
    }

    public String getDeviceStatus(String deviceId) {
        if (stoppedDevices.contains(deviceId)) {
            return "Inactive";
        }
        return deviceStatusMap.getOrDefault(deviceId, "UNKNOWN");
    }

    public DosimeterReading getLatestReading(String deviceId) {
        List<DosimeterReading> readings = dataMap.get(deviceId);
        if (readings != null && !readings.isEmpty()) {
            return readings.get(readings.size() - 1);
        }
        return null;
    }

    // Check for stopped devices every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void checkStoppedDevices() {
        LocalDateTime now = LocalDateTime.now();
        lastUpdate.forEach((deviceId, lastTime) -> {
            // Mark as stopped if no data for 60 seconds (6 * 10sec intervals)
            if (lastTime.plusSeconds(60).isBefore(now)) {
                stoppedDevices.add(deviceId);
                deviceStatusMap.put(deviceId, "Inactive");
            }
        });
    }
}
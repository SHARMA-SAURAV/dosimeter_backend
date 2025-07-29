package com.cdac.dosimeter_visualization.service;

//package com.example.dosimeter.service;

//import com.example.dosimeter.model.DosimeterReading;
import com.cdac.dosimeter_visualization.model.DosimeterReading;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DosimeterService {

    private final Map<String, List<DosimeterReading>> dataMap = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastUpdate = new ConcurrentHashMap<>();
    private final Set<String> stoppedDevices = new HashSet<>();

    public void addReading(DosimeterReading reading) {
        dataMap.computeIfAbsent(reading.getDeviceId(), k -> new ArrayList<>()).add(reading);
        lastUpdate.put(reading.getDeviceId(), LocalDateTime.now());
        stoppedDevices.remove(reading.getDeviceId());
    }

    public List<DosimeterReading> getDeviceData(String id) {
        return dataMap.getOrDefault(id, List.of());
    }

    public Set<String> getActiveDevices() {
        Set<String> active = new HashSet<>(dataMap.keySet());
        active.removeAll(stoppedDevices);
        return active;
    }

    public Set<String> getStoppedDevices() {
        return new HashSet<>(stoppedDevices);
    }

    @Scheduled(fixedRate = 5000)
    public void checkStoppedDevices() {
        LocalDateTime now = LocalDateTime.now();
        lastUpdate.forEach((deviceId, time) -> {
            if (time.plusSeconds(10).isBefore(now)) {
                stoppedDevices.add(deviceId);
            }
        });
    }
}

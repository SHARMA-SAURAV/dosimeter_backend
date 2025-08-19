package com.cdac.dosimeter_visualization.service;

//package com.example.dosimeter.service;

//import com.example.dosimeter.model.DosimeterReading;
import com.cdac.dosimeter_visualization.model.DosimeterReading;
import com.cdac.dosimeter_visualization.repository.DosimeterRepository;
import jakarta.annotation.PostConstruct;
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

    private final Map<String, List<DosimeterReading>> dataMap = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastUpdate = new ConcurrentHashMap<>();
    private final Set<String> stoppedDevices = new HashSet<>();

    public void addReading(DosimeterReading reading) {
        dataMap.computeIfAbsent(String.valueOf(reading.getId()), k -> new ArrayList<>()).add(reading);
        lastUpdate.put(String.valueOf(reading.getId()), LocalDateTime.now());
        stoppedDevices.remove(reading.getId());
    }

    public List<DosimeterReading> getDeviceData(String id) {
        return dataMap.getOrDefault(id, List.of());
    }

    public Set<String> getActiveDevices() {
        Set<String> active = new HashSet<>(dataMap.keySet());
        active.removeAll(stoppedDevices);
        return active;
    }
    public List<DosimeterReading> getReadingsByDeviceId(String deviceId) {
        return dosimeterRepository.findByDeviceId(deviceId);
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

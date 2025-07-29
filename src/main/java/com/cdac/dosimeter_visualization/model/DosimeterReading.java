package com.cdac.dosimeter_visualization.model;

//package com.example.dosimeter.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DosimeterReading {
    private String deviceId;
    private double cpm;
    private int battery;
    private LocalDateTime timestamp;
}

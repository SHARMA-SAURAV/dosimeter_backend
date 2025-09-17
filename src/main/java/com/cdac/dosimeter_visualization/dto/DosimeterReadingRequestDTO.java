package com.cdac.dosimeter_visualization.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DosimeterReadingRequestDTO {
    private String deviceId;
    private Double cpm;
    private LocalDateTime timestamp;
}

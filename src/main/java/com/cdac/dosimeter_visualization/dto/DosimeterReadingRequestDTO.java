package com.cdac.dosimeter_visualization.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DosimeterReadingRequestDTO {
    private String deviceId;
    private String cpm;
    private String date;
    private String time;
    private String battery;
    private String status;

    // Getters and Setters
}


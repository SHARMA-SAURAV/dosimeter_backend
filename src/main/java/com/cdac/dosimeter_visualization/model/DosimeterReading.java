package com.cdac.dosimeter_visualization.model;

//package com.example.dosimeter.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DosimeterReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cpm;
    private String date;
    private String time;
    private String battery;
    private String status;

    @ManyToOne
    private DosimeterAssignment assignment;
}

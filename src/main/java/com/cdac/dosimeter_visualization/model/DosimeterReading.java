package com.cdac.dosimeter_visualization.model;

//package com.example.dosimeter.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DosimeterReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cpm;
    private LocalDate date;
    private LocalTime time;
    private String battery;
    private String status;


    @ManyToOne
    private DosimeterAssignment assignment;
}

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

    private double cpm;                 // numeric for analytics
    private LocalDateTime timestamp;    // instead of separate date + time
    private int battery;                // battery percentage (0-100)

    @Enumerated(EnumType.STRING)
    private Status status;              // better than free-text

    @ManyToOne
    private DosimeterAssignment assignment;

    public enum Status {
        ACTIVE, INACTIVE, ERROR
    }
}

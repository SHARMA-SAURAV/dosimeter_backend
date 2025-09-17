package com.cdac.dosimeter_visualization.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DosimeterReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;
    private Double cpm;
    private LocalDateTime timestamp;

    private String time;
    private String battery;
    private String status;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private DosimeterAssignment assignment; // Link to user-device assignment

    // ✅ Track if this reading triggered an alert
    private boolean alertTriggered = false;

    // ✅ Add radiation level categorization
    private String radiationLevel; // LOW, NORMAL, HIGH, CRITICAL

    // Helper method to determine radiation level
    public void updateRadiationLevel() {
        if (cpm == null) {
            this.radiationLevel = "UNKNOWN";
        } else if (cpm < 50) {
            this.radiationLevel = "LOW";
        } else if (cpm <= 100) {
            this.radiationLevel = "NORMAL";
        } else if (cpm <= 200) {
            this.radiationLevel = "HIGH";
        } else {
            this.radiationLevel = "CRITICAL";
        }
    }
}

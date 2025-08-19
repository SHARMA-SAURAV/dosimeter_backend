package com.cdac.dosimeter_visualization.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DosimeterAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Dosimeter dosimeter;

    private LocalDate assignedDate;
    private LocalDateTime assignedAt;
    private LocalDateTime releasedAt;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL)
    private List<DosimeterReading> readings;
}

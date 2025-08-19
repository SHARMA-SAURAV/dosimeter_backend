package com.cdac.dosimeter_visualization.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Dosimeter {
    @Id
    private String deviceId; // unique ID like "DOCI_001"

    private String hash;

    @OneToMany(mappedBy = "dosimeter")
    private List<DosimeterAssignment> assignments;

}


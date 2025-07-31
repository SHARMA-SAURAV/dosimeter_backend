package com.cdac.dosimeter_visualization.repository;
import com.cdac.dosimeter_visualization.model.Dosimeter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface DosimeterRepository extends JpaRepository<Dosimeter, String> {

}
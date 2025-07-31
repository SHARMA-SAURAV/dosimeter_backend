package com.cdac.dosimeter_visualization.repository;
import com.cdac.dosimeter_visualization.model.DosimeterReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface DosimeterReadingRepository extends JpaRepository<DosimeterReading, Long> {


}

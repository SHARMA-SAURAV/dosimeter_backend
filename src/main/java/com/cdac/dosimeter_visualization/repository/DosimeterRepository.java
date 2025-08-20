package com.cdac.dosimeter_visualization.repository;
import com.cdac.dosimeter_visualization.model.Dosimeter;
import com.cdac.dosimeter_visualization.model.DosimeterReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface DosimeterRepository extends JpaRepository<Dosimeter, String> {

//    List<DosimeterReading> findByDeviceId(String deviceId);
    Optional<Dosimeter> findByDeviceId(String deviceId);


}
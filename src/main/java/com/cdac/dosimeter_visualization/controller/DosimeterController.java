package com.cdac.dosimeter_visualization.controller;

//package com.example.dosimeter.controller;
//
//import com.example.dosimeter.model.DosimeterReading;
//import com.example.dosimeter.service.DosimeterService;
//import com.example.dosimeter.export.CsvExporter;
import com.cdac.dosimeter_visualization.export.CsvExporter;
import com.cdac.dosimeter_visualization.model.DosimeterReading;
import com.cdac.dosimeter_visualization.service.DosimeterService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/dosimeters")
@CrossOrigin(origins = "*") // Allow all origins for simplicity; adjust as needed
public class DosimeterController {

    @Autowired
    private DosimeterService service;

    @PostMapping("/reading")
    public void receiveReading(@RequestBody DosimeterReading reading) {
        service.addReading(reading);
    }

    @GetMapping("/{id}/data")
    public List<DosimeterReading> getData(@PathVariable String id) {
        return service.getDeviceData(id);
    }

    @GetMapping("/active")
    public Set<String> getActiveDevices() {
        return service.getActiveDevices();
    }

    @GetMapping("/stopped")
    public Set<String> getStoppedDevices() {
        return service.getStoppedDevices();
    }

    @GetMapping("/{id}/export/csv")
    public void exportCsv(@PathVariable String id, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + id + "_data.csv");
        CsvExporter.writeCsv(service.getDeviceData(id), response.getWriter());
    }
}

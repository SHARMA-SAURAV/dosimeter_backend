package com.cdac.dosimeter_visualization.service;

//package com.cdac.dosimeter_visualization.export;

import com.cdac.dosimeter_visualization.model.DosimeterReading;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

//import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
@Service
//@Component
public class CsvExporter {
    public void export(Writer writer, List<DosimeterReading> readings) throws IOException {
        CSVWriter csvWriter = new CSVWriter(writer);
        // Add headers
        csvWriter.writeNext(new String[]{"Device ID", "CPM", "Date", "Time", "Battery"});

        for (DosimeterReading reading : readings) {
            csvWriter.writeNext(new String[]{
                    String.valueOf(reading.getAssignment().getId()),
                    String.valueOf(reading.getCpm()),
                    reading.getDate().toString(),
                    reading.getTime().toString(),
                    String.valueOf(reading.getBattery())
            });
        }

        csvWriter.flush();
        csvWriter.close();
    }
}


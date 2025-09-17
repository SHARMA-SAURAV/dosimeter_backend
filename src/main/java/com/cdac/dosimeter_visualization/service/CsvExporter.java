package com.cdac.dosimeter_visualization.service;

import com.cdac.dosimeter_visualization.model.DosimeterReading;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

@Service
public class CsvExporter {

    public void exportReadings(List<DosimeterReading> readings, Writer writer) throws IOException {
        writer.write("Timestamp,DeviceId,CPM,UserId,UserName\n");
        for (DosimeterReading r : readings) {
            String userId = (r.getAssignment() != null && r.getAssignment().getUser() != null)
                    ? String.valueOf(r.getAssignment().getUser().getId())
                    : "N/A";
            String userName = (r.getAssignment() != null && r.getAssignment().getUser() != null)
                    ? r.getAssignment().getUser().getName()
                    : "N/A";

            writer.write(String.format("%s,%s,%s,%s,%s\n",
                    r.getTimestamp(),
                    r.getDeviceId(),
                    r.getCpm() == null ? "" : r.getCpm(),
                    userId,
                    userName));
        }
    }
}

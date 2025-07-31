//package com.cdac.dosimeter_visualization.export;
//
////package com.example.dosimeter.export;
//
////import com.example.dosimeter.model.DosimeterReading;
//
//import com.cdac.dosimeter_visualization.model.DosimeterReading;
//
//import java.io.PrintWriter;
//import java.util.List;
//
//public class CsvExporter {
//
//    public static void writeCsv(List<DosimeterReading> data, PrintWriter writer) {
//        writer.println("Time,CPM,Battery");
//        for (DosimeterReading r : data) {
//            writer.printf("%s,%.2f,%d%n", r.getTimestamp(), r.getCpm(), r.getBattery());
//        }
//    }
//}

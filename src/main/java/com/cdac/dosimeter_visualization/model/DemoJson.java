//package com.cdac.dosimeter_visualization.model;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Random;
//import java.util.UUID;
//
//@Data
////@NoArgsConstructor
////@AllArgsConstructor
//public class DemoJson {
//    private String deviceId;
//    private String cpm; // Count per minute, representing radioactivity
//    private String date;
//    private String time;
//    private String battery; // Battery percentage
//    private String status;
//
//    public DemoJson() {
//        this.deviceId = generateRandomDeviceId();
//        this.cpm = generateRandomCpm();
//        this.date = generateRandomDate();
//        this.time = generateRandomTime();
//        this.battery = generateRandomBattery();
//        this.status = generateRandomStatus();
//    }
//
//    private String generateRandomDeviceId() {
//        String[] Ids = {"DOC123", "DOC234", "DOC345"};
//        Random random = new Random();
//        return Ids[random.nextInt(Ids.length)];
////        return UUID.randomUUID().toString(); // Generate a unique device ID
//    }
//
//
//    private String generateRandomCpm() {
//        Random random = new Random();
//        // Generate random CPM values (e.g., between 0 and 100)
//        return String.valueOf(random.nextInt(101));
//    }
//
//    private String generateRandomDate() {
//        Random random = new Random();
////        int year = 2020 + random.nextInt(6); // Random year between 2020 and 2025
////        int month = 1 + random.nextInt(12); // Random month between 1 and 12
////        int day = 1 + random.nextInt(28); // Random day between 1 and 28 (to avoid issues with month lengths)
//        // return today date with dateformatter
//        int year = LocalDate.now().getYear();
//        int month = LocalDate.now().getMonthValue();
//        int day = LocalDate.now().getDayOfMonth();
////        return new Data().formatDate();
//        return LocalDate.of(year, month, day).format(DateTimeFormatter.ISO_LOCAL_DATE); // Format date as ISO 8601
//    }
//
//    private String generateRandomTime() {
//        Random random = new Random();
//        int hour = LocalTime.now().getHour();
//        int minute = LocalTime.now().getMinute();
//        int second = LocalTime.now().getSecond();
//        return LocalTime.of(hour, minute, second).format(DateTimeFormatter.ISO_LOCAL_TIME); // Format time as ISO 8601
//    }
//
//    private String generateRandomBattery() {
//        Random random = new Random();
//        return String.valueOf(random.nextInt(101)); // Random battery percentage between 0 and 100
//    }
//
//    private String generateRandomStatus() {
//        String[] statuses = {"Active"}; // Possible statuses
//        Random random = new Random();
//        return statuses[random.nextInt(statuses.length)];
//    }
//}
//




















package com.cdac.dosimeter_visualization.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Data
public class DemoJson {
    private String deviceId;
    private String cpm; // still kept as string because earlier demo used string
    private String date;
    private String time;
    private String battery;
    private String status;

    public DemoJson() {
        this.deviceId = pickRandomDevice();
        this.cpm = String.valueOf(new Random().nextInt(201)); // 0..200 CPM
        this.date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.time = LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
        this.battery = String.valueOf(new Random().nextInt(101));
//        this.status = "Active";
        this.status = pickRandomStatus();
    }

    private String pickRandomDevice() {
        String[] ids = {"DOC123", "DOC234", "DOC345"};
        return ids[new Random().nextInt(ids.length)];
    }
    private String pickRandomStatus() {
        String[] statuses = {"Active","Inactive"};
        return statuses[new Random().nextInt(statuses.length)];
    }
}

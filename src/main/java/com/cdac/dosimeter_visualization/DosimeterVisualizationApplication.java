package com.cdac.dosimeter_visualization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DosimeterVisualizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(DosimeterVisualizationApplication.class, args);
	}

}

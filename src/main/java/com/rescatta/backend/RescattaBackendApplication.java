package com.rescatta.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RescattaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RescattaBackendApplication.class, args);
    }
}

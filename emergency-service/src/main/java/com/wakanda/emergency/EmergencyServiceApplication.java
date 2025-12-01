package com.wakanda.emergency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EmergencyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmergencyServiceApplication.class, args);
    }
}


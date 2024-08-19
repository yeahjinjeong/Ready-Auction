package com.readyauction.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReadyAuctionApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReadyAuctionApplication.class, args);
    }
}

package com.readyauction.app;

import org.redisson.spring.session.config.EnableRedissonWebSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class ReadyAuctionApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReadyAuctionApplication.class, args);
    }
}

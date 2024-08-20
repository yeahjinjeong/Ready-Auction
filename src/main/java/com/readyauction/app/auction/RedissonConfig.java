package com.readyauction.app.auction;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    // 환경 변수에서 Redis 호스트 주소를 읽어옵니다
    @Value("${REDIS_HOST}")
    private String redisHost;

    // 환경 변수에서 Redis 포트 번호를 읽어옵니다
    @Value("${REDIS_PORT}")
    private int redisPort;

    // 환경 변수에서 Redis 비밀번호를 읽어옵니다
    @Value("${REDIS_PASSWORD}")
    private String redisPassword;
    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort)
                .setPassword(redisPassword);  // 비밀번호 설정
        return Redisson.create(config);
    }
}

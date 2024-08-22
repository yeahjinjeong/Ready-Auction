package com.readyauction.app.auction;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${REDIS_HOST}")
    private String redisHost;

    @Value("${REDIS_PORT}")
    private int redisPort;

    @Value("${REDIS_PASSWORD}")
    private String redisPassword;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort)
                .setPassword(redisPassword)
                .setConnectionPoolSize(5)  // 최대 연결 수를 6으로 설정 (클라이언트당)
                .setConnectionMinimumIdleSize(2)  // 최소 유휴 연결 수를 2로 설정
                .setSubscriptionConnectionPoolSize(3)  // 구독 연결 풀 크기 설정
                .setSubscriptionConnectionMinimumIdleSize(1)  // 구독 연결 최소 유휴 연결 수 설정
                .setIdleConnectionTimeout(10000)  // 유휴 연결 타임아웃 설정 (10초)
                .setConnectTimeout(10000)  // 연결 타임아웃 설정 (10초)
                .setTimeout(3000)  // 명령어 타임아웃 설정 (3초)
                .setRetryAttempts(2)  // 재시도 횟수를 2회로 설정
                .setRetryInterval(1500)  // 재시도 간격 설정 (1.5초)
                .setPingConnectionInterval(2000)  // 연결 상태 확인을 위한 Ping 간격 설정 (2초)
                .setClientName("ready-auction-client");  // 클라이언트 이름 설정 (옵션)

        return Redisson.create(config);
    }
}

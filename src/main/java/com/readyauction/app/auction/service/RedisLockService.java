package com.readyauction.app.auction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisLockService {

    private final RedissonClient redissonClient;

    /**
     * Executes the given callback within a Redis lock.
     *
     * @param lockKey      the key for the lock
     * @param waitTime     the maximum time to wait for the lock
     * @param leaseTime    the time to hold the lock after granting it
     * @param timeUnit     the time unit of the waitTime and leaseTime parameters
     * @param callback     the callback function to execute within the lock
     * @param <T>          the return type of the callback function
     * @return the result of the callback function
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit, LockCallback<T> callback) {
        final RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(waitTime, leaseTime, timeUnit)) {
                throw new RuntimeException("Redisson lock timeout for key: " + lockKey);
            }
            return callback.execute();
        } catch (Exception e) {
            throw new RuntimeException("Error during operation with lock: " + e.getMessage(), e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @FunctionalInterface
    public interface LockCallback<T> {
        T execute() throws Exception;
    }
}

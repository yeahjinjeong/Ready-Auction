package com.readyauction.app.auction.service;

import com.readyauction.app.auction.dto.*;
import com.readyauction.app.auction.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisLockService {

    private final RedissonClient redissonClient;
    private final BidService bidService;
    private final ProductService productService;
    public BidResDto bidLock(String email, BidDto bidDto){
        final RLock lock = redissonClient.getLock(String.format("Product:productId:%d", bidDto.getProductId()));
        try {
            if (!lock.tryLock(10, 1, TimeUnit.SECONDS)) {
                throw new RuntimeException("Redisson lock timeout");
            }
            // 비즈니스 로직을 콜백 함수로 처리
            return bidService.startBid(email, bidDto);

        } catch (Exception e) {
            throw new RuntimeException("Error during operation: " + e.getMessage(), e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }
    public ProductDto winnerLock(String email, WinnerReqDto winnerReqDto){
        final RLock lock = redissonClient.getLock(String.format("Product:productId:%d", winnerReqDto.getProductId()));
        try {
            if (!lock.tryLock(10, 1, TimeUnit.SECONDS)) {
                throw new RuntimeException("Redisson lock timeout");
            }
            // 비즈니스 로직을 콜백 함수로 처리
            return productService.startWinnerProcess(email,winnerReqDto);

        } catch (Exception e) {
            throw new RuntimeException("Error during operation: " + e.getMessage(), e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

}

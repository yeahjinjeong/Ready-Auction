package com.readyauction.app.auction;

import com.readyauction.app.auction.dto.BidDto;
import com.readyauction.app.auction.dto.BidResDto;
import com.readyauction.app.auction.dto.WinnerReqDto;
import com.readyauction.app.auction.entity.PurchaseCategory;
import com.readyauction.app.auction.service.BidService;
import com.readyauction.app.auction.service.RedisLockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;


//동시성 테스트 코드
@SpringBootTest
public class RedisLockServiceConcurrencyTest {

    @Autowired
    private RedisLockService redisLockService;
    @Autowired
    private BidService bidService;
    private BidDto bidDto;
    private String[] emails;


    private WinnerReqDto winnerReqDto;
    @BeforeEach
    void setUp() {
        // BidDto 초기화

        bidDto = BidDto.builder()
                .productId(20L)  // 테스트에 사용할 Product ID
                .bidPrice(15000)  // 테스트에 사용할 입찰 가격
                .bidTime(new Timestamp(System.currentTimeMillis()))
                .build();

        winnerReqDto = WinnerReqDto.builder()
                .productId(20L)
                .buyPrice(15000)
                .buyTime(new Timestamp(System.currentTimeMillis()))
                .category(PurchaseCategory.IMMEDIATE)
                .build();
        // 이메일 목록 초기화
        emails = new String[]{
                "pp@naver.com", "qq@naver.com", "birdpoo@ssg.com", "feelddong@ssg.com",
                "kimcastle@ssg.com", "ducksun@ssg.com", "alex@ssg.com", "rr@naver.com",
                "rkawkaos77@naver.com", "gim670079@gmail.com"
        };
    }

    @Test
    void testBidLockConcurrency() throws InterruptedException {
        int threadCount = emails.length;  // 이메일 개수만큼 스레드 실행
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final String email = emails[i];  // 각 스레드에 할당된 이메일
            executorService.execute(() -> {
                try {
                    // redisLockService.bidLock 메서드 호출
                    BidResDto bidResDto = redisLockService.bidLock(email, bidDto);
//                    `  BidResDto bidResDto = bidService.startBid(email,bidDto);
                    // 응답이 성공적으로 처리되었는지 확인
                    assertThat(bidResDto).isNotNull();
                    System.out.println("Response for " + email + ": " + bidResDto);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();  // CountDownLatch 감소
                }
            });
        }

        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await();
    }
    @Test
    void testBidLockAndWinnerLockConcurrency() throws InterruptedException {
        int threadCount = emails.length;  // 이메일 개수만큼 스레드 실행
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final String email = emails[i];  // 각 스레드에 할당된 이메일
            executorService.execute(() -> {
                try {
                    // redisLockService.bidLock 메서드 호출
                    BidResDto bidResDto = redisLockService.bidLock(email, bidDto);
//                    `  BidResDto bidResDto = bidService.startBid(email,bidDto);
                    // 응답이 성공적으로 처리되었는지 확인
                    assertThat(bidResDto).isNotNull();
                    System.out.println("Response for " + email + ": " + bidResDto);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();  // CountDownLatch 감소
                }
            });
        }

        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await();
    }
}

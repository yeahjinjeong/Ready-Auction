package com.readyauction.app.auction;

import com.readyauction.app.auction.dto.*;
import com.readyauction.app.auction.entity.Category;
import com.readyauction.app.auction.entity.PurchaseCategory;
import com.readyauction.app.auction.service.BidService;
import com.readyauction.app.auction.service.ProductService;
import com.readyauction.app.cash.dto.PaymentReqDto;

import com.readyauction.app.cash.entity.PaymentCategory;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;


@Slf4j
//동시성 테스트 코드
@SpringBootTest
public class RedisLockServiceConcurrencyTest {
    @Autowired
    private BidService bidService;
    private BidDto bidDto;
    private String[] emails;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ProductService productService;
    private PaymentReqDto paymentReqDto;
    private WinnerReqDto winnerReqDto;
    private ProductReqDto productReqDto;
    @BeforeEach
    void setUp() {
        // BidDto 초기화

        bidDto = BidDto.builder()
                .productId(1L)  // 테스트에 사용할 Product ID
                .bidPrice(20000)  // 테스트에 사용할 입찰 가격
                .bidTime(new Timestamp(System.currentTimeMillis()))
                .build();

        winnerReqDto = WinnerReqDto.builder()
                .productId(1L)
                .buyPrice(54000)
                .buyTime(new Timestamp(System.currentTimeMillis()))
                .category(PurchaseCategory.IMMEDIATE)
                .build();
        // 이메일 목록 초기화

        paymentReqDto = PaymentReqDto.builder()
                .productId(8L)
                .payTime(new Timestamp(System.currentTimeMillis()))
                .category(PaymentCategory.BID)
                .amount(50000)
                .build();
        emails = new String[]{
                "stc1015@naver.com", "jyna120@naver.com", "leejunho@naver.com", "5tjddus@gmail.com",
                "jmh907@naver.com", "gim670079@gmail.com"
        };

        Timestamp endTime = new Timestamp(System.currentTimeMillis() + 100 * 1000); // 1 hour later
        List<String> imgUrls = Arrays.asList(
                "https://kr.object.ncloudstorage.com/ready-auction-bucket/productIMG/stc1015@naver.com/60ebe7c2-4dbe-4cfc-8014-96294c5aa35c.jpg",
                "https://kr.object.ncloudstorage.com/ready-auction-bucket/productIMG/stc1015@naver.com/86c5788d-eb0f-4554-8cec-c7f307f6a8e1.jpg"
        );
        productReqDto = ProductReqDto.builder()
                .name("Test Product")
                .category(Category.SOCCER)
                .description("This is a test product.")
                .bidUnit(100)
                .endTime(endTime)
                .currentPrice(1000)
                .immediatePrice(5000)
                .imgUrls(imgUrls)
                .build();

    }
//
//    @Test
//    void testWinnerLockConcurrency() throws InterruptedException {
//        int threadCount = emails.length;  // 이메일 개수만큼 스레드 실행
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        for (int i = 0; i < threadCount; i++) {
//            final String email = emails[i];  // 각 스레드에 할당된 이메일
//            executorService.execute(() -> {
//                try {
//                    // redisLockService.bidLock 메서드 호출
//                     ProductDto productDto = redisLockService.winnerLock(email, winnerReqDto);
////                    `  BidResDto bidResDto = bidService.startBid(email,bidDto);
//                    // 응답이 성공적으로 처리되었는지 확인
//                    assertThat(productDto).isNotNull();
//                    System.out.println("Response for " + email + ": " + productDto);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    latch.countDown();  // CountDownLatch 감소
//                }
//            });
//        }
//
//        // 모든 스레드가 작업을 완료할 때까지 대기
//        latch.await();
//    }

//    @Test
//    void testRedisV(){
//        String pid = ManagementFactory.getRuntimeMXBean().getName();
//        String key =  "kkk"+"_"+"www";
//
//        redisTemplate.opsForValue().set(key,pid);
//        redisTemplate.expire(key,5, TimeUnit.SECONDS);
//    }

    @Test
    void testCreateProduct() throws InterruptedException {

        ProductRepDto actualProductRepDto = productService.createAuction(emails[0], productReqDto);

        bidDto = BidDto.builder()
                .productId(actualProductRepDto.getId())  // 테스트에 사용할 Product ID
                .bidPrice(actualProductRepDto.getCurrentPrice())  // 테스트에 사용할 입찰 가격
                .bidTime(new Timestamp(System.currentTimeMillis()))
                .build();
        bidService.startBid(emails[5], bidDto);

    }
//    @Test
//    void testBidLockConcurrency() throws InterruptedException {
//        int threadCount = emails.length;  // 이메일 개수만큼 스레드 실행
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        for (int j = 0; j < threadCount; j++) {
//            for (int i = 0; i < threadCount; i++) {
//                final String email = emails[i];  // 각 스레드에 할당된 이메일
//                executorService.execute(() -> {
//
//                    long startTime = System.nanoTime();  // 시작 시간 측정
//                    try {
//                        // redisLockService.bidLock 메서드 호출
//                        BidResDto bidResDto = bidService.bidLock(email, bidDto);
////                    `  BidResDto bidResDto = bidService.startBid(email,bidDto);
//                        // 응답이 성공적으로 처리되었는지 확인
//                        assertThat(bidResDto).isNotNull();
//                        long endTime = System.nanoTime();  // 종료 시간 측정
//                        long duration = endTime - startTime;  // 처리 시간 계산
//                        log.info("createPayment method executed in " + duration / 1_000_000.0 + " milliseconds.");
//
//                        log.info("Response for " + email + ": " + bidResDto);
//                    } catch (Exception e) {
//                        long endTime = System.nanoTime();  // 종료 시간 측정
//                        long duration = endTime - startTime;  // 처리 시간 계산
//                        log.info("createPayment method executed in " + duration / 1_000_000.0 + " milliseconds.");
//
//                        e.printStackTrace();
//                    } finally {
//                        latch.countDown();  // CountDownLatch 감소
//                    }
//                });
//            }
//        }
//
//        // 모든 스레드가 작업을 완료할 때까지 대기
//        latch.await();
//    }
//    @Test
//    void testBidLockAndWinnerLockConcurrency() throws InterruptedException {
//        int threadCount = emails.length;  // 이메일 개수만큼 스레드 실행
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        for (int i = 0; i < threadCount; i++) {
//            final String email = emails[i];  // 각 스레드에 할당된 이메일
//            executorService.execute(() -> {
//                try {
//                    // redisLockService.bidLock 메서드 호출
//long startTime = System.nanoTime();  // 시작 시간 측정
//                    BidResDto bidResDto = redisLockService.bidLock(email, bidDto);
////                    `  BidResDto bidResDto = bidService.startBid(email,bidDto);
//                    // 응답이 성공적으로 처리되었는지 확인
//                    assertThat(bidResDto).isNotNull();
//long endTime = System.nanoTime();  // 종료 시간 측정
//    long duration = endTime - startTime;  // 처리 시간 계산
//                    System.out.println("createPayment method executed in " + duration / 1_000_000.0 + " milliseconds.");

//                    System.out.println("Response for " + email + ": " + bidResDto);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    latch.countDown();  // CountDownLatch 감소
//                }
//            });
//        }
//
//        // 모든 스레드가 작업을 완료할 때까지 대기
//        latch.await();
//    }

//    @Test
//    void testPaymentLockConcurrency() throws InterruptedException {
//        int threadCount = emails.length * 5;  // 이메일 개수만큼 스레드 실행
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        for (int i = 0; i < threadCount; i++) {
//            final String email = emails[5];  // 각 스레드에 할당된 이메일
//            executorService.execute(() -> {
//                long startTime = System.nanoTime();  // 시작 시간 측정
//                try {
//                    // redisLockService.bidLock 메서드 호출
//
//
//                    PaymentResDto paymentResDto = redisLockService.paymentLock(email, paymentReqDto);
////                    `  BidResDto bidResDto = bidService.startBid(email,bidDto);
//                    // 응답이 성공적으로 처리되었는지 확인
//                    assertThat(paymentResDto).isNotNull();
//                    log.info("Response for " + email + ": " + paymentResDto);
//                    long endTime = System.nanoTime();  // 종료 시간 측정
//                    long duration = endTime - startTime;  // 처리 시간 계산
//                    log.info("createPayment method executed in " + duration / 1_000_000.0 + " milliseconds.");
//
//                } catch (Exception e) {
//                    long endTime = System.nanoTime();  // 종료 시간 측정
//                    long duration = endTime - startTime;  // 처리 시간 계산
//                    log.info("catch createPayment method executed in " + duration / 1_000_000.0 + " milliseconds.");
//
//                    e.printStackTrace();
//                } finally {
//                    latch.countDown();  // CountDownLatch 감소
//                }
//            });
//        }
//
//        // 모든 스레드가 작업을 완료할 때까지 대기
//        latch.await();
//
//    }

//    @Test
//    void testSuccessLockConcurrency() throws InterruptedException {
//        int threadCount = emails.length;  // 이메일 개수만큼 스레드 실행
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        for (int i = 0; i < threadCount; i++) {
//            final String email = emails[5];  // 각 스레드에 할당된 이메일
//            executorService.execute(() -> {
//                try {
//                    // redisLockService.bidLock 메서드 호출
//                    PaymentResDto paymentResDto = redisLockService.successLock(email, paymentReqDto);
//                    assertThat(paymentResDto).isNotNull();
//                    System.out.println("Response for " + email + ": " + paymentResDto);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    latch.countDown();  // CountDownLatch 감소
//                }
//            });
//        }
//
//        // 모든 스레드가 작업을 완료할 때까지 대기
//        latch.await();
//    }
}

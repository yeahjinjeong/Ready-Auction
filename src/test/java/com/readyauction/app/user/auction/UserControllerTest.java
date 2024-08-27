package com.readyauction.app.auction;

import com.readyauction.app.auction.dto.*;
import com.readyauction.app.auction.entity.Category;
import com.readyauction.app.auction.entity.PurchaseCategory;
import com.readyauction.app.auction.service.BidService;
import com.readyauction.app.auction.service.ProductService;
import com.readyauction.app.cash.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
public class AuctionRestControllerTest {

    @Autowired
    private BidService bidService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ProductService productService;

    private BidDto[] bidDtos;
    private WinnerReqDto winnerReqDto;
    private String[] emails;
    private Long productId;

    @BeforeEach
    void setUp() {
        // 이메일 목록 초기화
        emails = new String[]{
                "jyna120@naver.com","5tjddus@gmail.com", "jmh907@naver.com", "jjinnyzzang@gmail.com", "gim670079@gmail.com"
        };

        Timestamp endTime = new Timestamp(System.currentTimeMillis() +  7 * 24 * 60 * 60 * 1000); // 7일 후 종료
        List<String> imgUrls = Arrays.asList(
                "https://kr.object.ncloudstorage.com/ready-auction-bucket/productIMG/stc1015@naver.com/60ebe7c2-4dbe-4cfc-8014-96294c5aa35c.jpg",
                "https://kr.object.ncloudstorage.com/ready-auction-bucket/productIMG/stc1015@naver.com/86c5788d-eb0f-4554-8cec-c7f307f6a8e1.jpg"
        );


        ProductReqDto productReqDto = ProductReqDto.builder()
                .name("1996년 아디다스 프랑스 국가대표 유니폼")
                .category(Category.SOCCER)
                .description("프랑스 국가대표 유니폼입니다.\n1996년도 잇템")
                .bidUnit(1000)
                .endTime(endTime)
                .currentPrice(2000)
                .immediatePrice(70000)
                .imgUrls(imgUrls)
                .build();

        // 상품 추가
        ProductRepDto productRepDto = productService.createAuction(emails[0], productReqDto);
        productId = productRepDto.getId();  // 생성된 상품 ID 사용

        // 입찰자 BidDto 초기화
        bidDtos = new BidDto[3];
        bidDtos[0] = BidDto.builder()
                .productId(productId)
                .bidPrice(2000)
                .bidTime(new Timestamp(System.currentTimeMillis()))
                .build();

        bidDtos[1] = BidDto.builder()
                .productId(productId)
                .bidPrice(3000)
                .bidTime(new Timestamp(System.currentTimeMillis()))
                .build();

        bidDtos[2] = BidDto.builder()
                .productId(productId)
                .bidPrice(4000)
                .bidTime(new Timestamp(System.currentTimeMillis()))
                .build();

        // 즉시 구매자 설정
        winnerReqDto = WinnerReqDto.builder()
                .productId(productId)
                .buyPrice(70000)
                .buyTime(new Timestamp(System.currentTimeMillis()))
                .category(PurchaseCategory.IMMEDIATE)
                .build();
    }

//    @Test
//    void testCreateProduct() throws Exception {
//        // 상품 추가를 위한 데이터 설정
//        Timestamp endTime = new Timestamp(System.currentTimeMillis() + 60 * 1000); // 7일 후 종료
//        List<String> imgUrls = Arrays.asList(
//                "https://kr.object.ncloudstorage.com/ready-auction-bucket/productIMG/stc1015@naver.com/60ebe7c2-4dbe-4cfc-8014-96294c5aa35c.jpg",
//                "https://kr.object.ncloudstorage.com/ready-auction-bucket/productIMG/stc1015@naver.com/86c5788d-eb0f-4554-8cec-c7f307f6a8e1.jpg"
//        );
//
//
//        ProductReqDto productReqDto = ProductReqDto.builder()
//                .name("1996년 아디다스 프랑스 국가대표 유니폼")
//                .category(Category.SOCCER)
//                .description("프랑스 국가대표 유니폼입니다.\n1996년도 잇템")
//                .bidUnit(1000)
//                .endTime(endTime)
//                .currentPrice(2000)
//                .immediatePrice(70000)
//                .imgUrls(imgUrls)
//                .build();
//
//        // 상품 추가
//        ProductRepDto productRepDto = productService.createAuction(emails[0], productReqDto);
//        productId = productRepDto.getId();  // 생성된 상품 ID 사용
//
//    }
//     패널티 함수 잘 작동되나 테스트
    @Test
    void testAuctionProcessWithProductCreation() throws InterruptedException {
        // 3명의 구매자가 입찰 진행
        for (int i = 1; i < 4; i++) {
            BidResDto bidResDto = bidService.bidLock(emails[i], bidDtos[i-1]);
            assertNotNull(bidResDto);
            log.info("Bid successful for: " + emails[i] + " with bidPrice: " + bidDtos[i-1].getBidPrice());
        }

        // 1명의 구매자가 즉시 구매로 낙찰자로 선정
        ProductDto productDto = bidService.winnerLock(emails[4], winnerReqDto);
        assertNotNull(productDto);
        log.info("Winner selected: " + emails[4] + " with immediate purchase.");
//
//        // 낙찰자가 결제를 안 했다고 가정하고 패널티 함수 실행
        paymentService.paymentPanalty(productId);


        log.info("Auction process with penalty applied completed successfully.");
    }
//
//    @Test
//    void testAuctionProcessWithWinnerCreation() throws InterruptedException {
//        // 3명의 구매자가 입찰 진행
//        for (int i = 1; i < 4; i++) {
//            BidResDto bidResDto = bidService.bidLock(emails[i], bidDtos[i-1]);
//            assertNotNull(bidResDto);
//            log.info("Bid successful for: " + emails[i] + " with bidPrice: " + bidDtos[i-1].getBidPrice());
//        }
//
//        // 1명의 구매자가 즉시 구매로 낙찰자로 선정
//        ProductDto productDto = bidService.winnerLock(emails[4], winnerReqDto);
//        assertNotNull(productDto);
//        log.info("Winner selected: " + emails[4] + " with immediate purchase.");
////
////        // 낙찰자가 결제를 안 했다고 가정하고 패널티 함수 실행
////        paymentService.paymentPanalty(productId);
//
//
//        log.info("Auction process with penalty applied completed successfully.");
//    }



}

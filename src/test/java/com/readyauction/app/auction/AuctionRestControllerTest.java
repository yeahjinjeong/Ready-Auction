package com.readyauction.app.auction;

import com.readyauction.app.auction.dto.*;
import com.readyauction.app.auction.entity.Category;
import com.readyauction.app.auction.entity.PurchaseCategory;
import com.readyauction.app.auction.service.BidService;
import com.readyauction.app.auction.service.ProductService;
import com.readyauction.app.cash.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
    private String email;
    @BeforeEach
    void setUp() {
//        // 이메일 목록 초기화
//        emails = new String[]{
//                "jyna120@naver.com","5tjddus@gmail.com", "jmh907@naver.com", "jjinnyzzang@gmail.com", "gim670079@gmail.com"
//        };
//
//        email = "rkawkaos77@naver.com";
//
//        Timestamp endTime = new Timestamp(System.currentTimeMillis() +  60 * 1000); // 7일 후 종료
//        List<String> imgUrls = Arrays.asList(
//                "https://kr.object.ncloudstorage.com/ready-auction-bucket/productIMG/stc1015@naver.com/7f039388-dd68-4ed2-9bd5-0a44da7fbdc8.jpg"
//                ,"https://kr.object.ncloudstorage.com/ready-auction-bucket/productIMG/stc1015@naver.com/8233ef2c-b321-4fd5-8721-23f01c9fd9bd.jpg"
//        );
//
//        ProductReqDto productReqDto = ProductReqDto.builder()
//                .name("24-25 레알마드리드 축구 유니폼 벨링엄")
//                .category(Category.SOCCER)
//                .description("새상품 (택만 없는 제품) 택 없는 거 감안하여 싸게 판매합니다.\n" +
//                        "\n" +
//                        "국내 4XL (작게 나와 사이즈감 105 정도 됩니다.)\n" +
//                        "\n" +
//                        "홍대 아디다스에서 구매 및 hp 스폰서 제외 풀패치\n" +
//                        "\n" +
//                        "사이즈가 무조건 맞을 줄 알고 택 제거 했는데 원하는 핏이 아니라 판매합니다.")
//                .bidUnit(5000)
//                .endTime(endTime)
//                .currentPrice(20000)
//                .immediatePrice(70000)
//                .imgUrls(imgUrls)
//                .build();

        // 상품 추가
//        ProductRepDto productRepDto = productService.createAuction(emails[0], productReqDto);
//        productId = productRepDto.getId();  // 생성된 상품 ID 사용
//
//        // 입찰자 BidDto 초기화
//        bidDtos = new BidDto[3];
//        bidDtos[0] = BidDto.builder()
//                .productId(productId)
//                .bidPrice(2000)
//                .bidTime(new Timestamp(System.currentTimeMillis()))
//                .build();
//
//        bidDtos[1] = BidDto.builder()
//                .productId(productId)
//                .bidPrice(3000)
//                .bidTime(new Timestamp(System.currentTimeMillis()))
//                .build();
//
//        bidDtos[2] = BidDto.builder()
//                .productId(productId)
//                .bidPrice(4000)
//                .bidTime(new Timestamp(System.currentTimeMillis()))
//                .build();
//
//
//        // 즉시 구매자 설정
//        winnerReqDto = WinnerReqDto.builder()
//                .productId(productId)
//                .buyPrice(70000)
//                .buyTime(new Timestamp(System.currentTimeMillis()))
//                .category(PurchaseCategory.IMMEDIATE)
//                .build();
    }
//
//    @Disabled
    @Test
    void testCreateProduct() throws Exception {
        // 상품 추가를 위한 데이터 설정

        email = "rkawkaos77@naver.com";

        Timestamp endTime = new Timestamp(System.currentTimeMillis() +  60 * 1000); // 7일 후 종료
        List<String> imgUrls = Arrays.asList(
                "https://kr.object.ncloudstorage.com/ready-auction-bucket/productIMG/stc1015@naver.com/ea90fc92-d800-460c-a450-c058c84e66d4.jpg","https://kr.object.ncloudstorage.com/ready-auction-bucket/productIMG/stc1015@naver.com/e3aa9d12-58cf-4e19-8855-d0004739ced9.jpg"
                );

        ProductReqDto productReqDto = ProductReqDto.builder()
                .name("삼성 라이온즈 구자욱 야구 유니폼 반팔")
                .category(Category.SOCCER)
                .description("\uD83D\uDD3A미세 오염있어요\n" +
                        "중고상품이지만 컨디션 좋으며, 세탁 완료되었어요.\n" +
                        "\n" +
                        "•권장 사이즈 : 100\n" +
                        "\n" +
                        "•단면 실측 (오차범위 1-3cm)\n" +
                        "가슴 52 / 총장 69")
                .bidUnit(5000)
                .endTime(endTime)
                .currentPrice(20000)
                .immediatePrice(70000)
                .imgUrls(imgUrls)
                .build();

        // 상품 추가
        ProductRepDto productRepDto = productService.createAuction(email, productReqDto);
        log.info(productRepDto.toString() + "상품등록");
    }

//     패널티 함수 잘 작동되나 테스트
//    @Test
//    void testAuctionProcessWithProductCreation() throws InterruptedException {
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
//
//        // 낙찰자가 결제를 안 했다고 가정하고 패널티 함수 실행
//        paymentService.paymentPanalty(productId);
//
//
//        log.info("Auction process with penalty applied completed successfully.");
//    }
//
//    @Disabled
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
//
////        // 낙찰자가 결제를 안 했다고 가정하고 패널티 함수 실행
////        paymentService.paymentPanalty(productId);
//
//        log.info("Auction process with penalty applied completed successfully.");
//    }
}

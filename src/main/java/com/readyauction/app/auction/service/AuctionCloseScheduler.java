package com.readyauction.app.auction.service;

import com.readyauction.app.auction.dto.EmailMessage;
import com.readyauction.app.auction.dto.WinnerDto;
import com.readyauction.app.auction.dto.WinnerReqDto;
import com.readyauction.app.auction.entity.AuctionStatus;
import com.readyauction.app.auction.entity.Bid;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.repository.ProductRepository;
import com.readyauction.app.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuctionCloseScheduler {
//일분 마다 업데이트
    final ProductService productService;
    final BidService bidService;
    final MemberService memberService;
    final EmailService emailService;
    final RedisLockService redisLockService;
    @Scheduled(cron = "0 * * * * *")	// 1분마다
    public void closeAuction() throws Exception {
        System.out.println("옥션마감!");
        endAuction();

    }
    public void endAuction() throws Exception {
        List<Product> products = productService.getProductsWithEndTimeAtCurrentMinute();
        for (Product product : products) {

            // 제일 비싼 사람들얻어오기
            System.out.println("마감 경매 조회");
            Bid bid = bidService.findTopByProductIdOrderByMyPriceDesc(product.getId());
            if(bid == null){
                product.setAuctionStatus(AuctionStatus.END);
                productService.save(product);
            }
            else {
                System.out.println("제일 비싼 입찰자 얻어오기");

                WinnerReqDto winnerReqDto = (WinnerReqDto.builder().
                        productId(bid.getProduct().getId())).
                        buyPrice(bid.getMyPrice()).
                        buyTime(bid.getBidTime()).
                        build();
                System.out.println("입찰자 낙찰자로 변환" + bid.getMemberId());
                String email = memberService.findEmailById(bid.getMemberId());


                bidService.winnerLock(email, winnerReqDto);
            }

            System.out.println("경매 마감");
        }
    }
}

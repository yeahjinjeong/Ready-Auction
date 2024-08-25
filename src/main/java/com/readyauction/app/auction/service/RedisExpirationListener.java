package com.readyauction.app.auction.service;

import com.readyauction.app.auction.dto.WinnerReqDto;
import com.readyauction.app.auction.entity.AuctionStatus;
import com.readyauction.app.auction.entity.Bid;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.entity.PurchaseCategory;
import com.readyauction.app.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisExpirationListener implements MessageListener {

    private final BidService bidService;
    private final ProductService productService;
    private final MemberService memberService;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = message.toString();

        // Extract product ID from the key
        if (key.startsWith("Auction:ProductId:")) {
            String productId = key.substring("Auction:ProductId:".length());
            endAuction(Long.valueOf(productId));

        } else {
            log.warn("Received expiration event for unknown key: " + key);
        }
    }
    public void endAuction(Long productId){
        log.warn("마감 경매 조회");
        Bid bid = bidService.findTopByProductIdOrderByMyPriceDesc(productId);
        Product product = productService.findById(productId).orElseThrow();
        if(bid == null){
            product.setAuctionStatus(AuctionStatus.END);
            productService.save(product);
            log.warn("입찰이 없음");
        }
        else {
            log.warn("제일 비싼 입찰자 얻어오기");

            WinnerReqDto winnerReqDto = (WinnerReqDto.builder().
                    productId(bid.getProduct().getId())).
                    buyPrice(bid.getMyPrice()).
                    buyTime(bid.getBidTime()).
                    category(PurchaseCategory.BID).
                    build();
            log.warn("입찰자 낙찰자로 변환" + bid.getMemberId());
            String email = memberService.findEmailById(bid.getMemberId());


            bidService.winnerLock(email, winnerReqDto);
        }

        System.out.println("경매 마감");
    }
}
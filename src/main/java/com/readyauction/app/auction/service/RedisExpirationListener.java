package com.readyauction.app.auction.service;

import com.readyauction.app.auction.dto.WinnerReqDto;
import com.readyauction.app.auction.entity.AuctionStatus;
import com.readyauction.app.auction.entity.Bid;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.entity.PurchaseCategory;
import com.readyauction.app.cash.service.PaymentService;
import com.readyauction.app.ncp.service.NcpObjectStorageService;
import com.readyauction.app.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final PaymentService paymentService;
    private final NcpObjectStorageService ncpObjectStorageService;
    @Value("${spring.s3.bucket}")
    private String bucketName;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = message.toString();

        // Extract product ID from the key
        if (key.startsWith("Auction:ProductId:")) {
            String productId = key.substring("Auction:ProductId:".length());
            endAuction(Long.valueOf(productId));

        } else if (key.startsWith("EndPayment:ProductId:")) {
            String productId = key.substring("EndPayment:ProductId:".length());
            paymentPenalty(Long.valueOf(productId));
        } else if(key.startsWith("Auction:ImageId:")){
            String imageKey = key.substring("Auction:ImageId:".length());
            deleteImage(imageKey);
        } else{
            log.warn("Received expiration event for unknown key: " + key);
        }

    }
    public void deleteImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String key = imageUrl.substring(imageUrl.indexOf(bucketName) + bucketName.length() + 1);
            ncpObjectStorageService.deleteFile(key);
        }
    }
    public void paymentPenalty(Long productId){
        paymentService.paymentPanalty(productId);
        //paymentStatus OUTSTANDING 로 바꾸기. 페이먼트 저장...
        //판매자에게 해당 페이먼트의 70퍼 입금
        //비낙찰자들 선입금액 전부 롤백. 단 낙찰자는 제외.

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
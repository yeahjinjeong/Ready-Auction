package com.readyauction.app.auction.service;

import com.readyauction.app.auction.dto.*;
import com.readyauction.app.auction.entity.AuctionStatus;
import com.readyauction.app.auction.entity.Bid;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.entity.PurchaseCategory;
import com.readyauction.app.auction.repository.BidRepository;
import com.readyauction.app.cash.entity.PaymentCategory;
import com.readyauction.app.cash.dto.PaymentReqDto;
import com.readyauction.app.cash.service.PaymentService;
import com.readyauction.app.user.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.readyauction.app.auction.entity.BidStatus;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service

public class BidService {

    private final BidRepository bidRepository;
    private final MemberService memberService;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final EmailService emailService;
    private final RedisLockService redisLockService;

    @Transactional
    public void createBid(Long userId, Product product, Integer price, Timestamp timestamp) throws Exception {

        System.out.println("상품 입찰 크릿 진행!");
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        PaymentReqDto paymentReqDto = PaymentReqDto.builder()
                .payTime(new Timestamp(System.currentTimeMillis()))
                .amount(product.getImmediatePrice()/10)
                .productId(product.getId())
                .category(PaymentCategory.BID)
                .build();

        paymentService.createBidPayment(userId, paymentReqDto);
        System.out.println("상품 입찰 크릿에서 선불 지불 완료!");
        try {
            Bid bid = Bid.builder()
                    .memberId(userId)
                    .product(product)
                    .myPrice(price)
                    .bidTime(timestamp)
                    .bidStatus(BidStatus.CONFIRMED)
                    .build();
            bidRepository.save(bid);


            System.out.println("상품 입찰 저장 완료!");

           } catch (Exception e) {
            log.error("Failed to create bid for userId {} on productId {}: {}", userId, product.getId(), e.getMessage());
            throw new RuntimeException("Failed to create bid", e);  // Triggers rollback
            }

    }

    public BidResDto bidLock(String email, BidDto bidDto) {
        String lockKey = String.format("Product:productId:%d", bidDto.getProductId());
        return redisLockService.executeWithLock(lockKey, 3, 2, TimeUnit.SECONDS, () -> {
            // Execute the business logic within the lock
            return startBid(email, bidDto);
        });
    }


    public ProductDto winnerLock(String email, WinnerReqDto winnerReqDto) {
        String lockKey = String.format("Product:productId:%d", winnerReqDto.getProductId());
        return redisLockService.executeWithLock(lockKey, 30, 30, TimeUnit.SECONDS, () -> {
            Long userId = memberService.findByEmail(email).getId();
            if (!productService.findById(winnerReqDto.getProductId()).get().hasWinner()) {
                if(bidRepository.findByMemberIdAndProductId(userId, winnerReqDto.getProductId()).isEmpty()) {
                    PaymentReqDto paymentReqDto = PaymentReqDto.builder()
                            .productId(winnerReqDto.getProductId())
                            .payTime(winnerReqDto.getBuyTime())
                            .amount(winnerReqDto.getBuyPrice() / 10)
                            .category(PaymentCategory.BID)
                            .build();
                    paymentService.createBidPayment(userId, paymentReqDto);
                }
                ProductDto productDto = productService.startWinnerProcess(email, winnerReqDto);
                EmailMessage emailMessage = EmailMessage.builder()
                        .to(email)
                        .subject("Congratulations! You have won the auction.")
                        .message("<html><head></head><body><div>" + productDto.getName() + " auction was won by you. Please visit our website to complete the payment.</div></body></html>")
                        .build();
                emailService.sendMail(emailMessage);
                return productDto;
            } else {
                throw new RuntimeException("The product has already been won");
            }
        });
    }

    @Transactional
    public void updateBid(Bid bid, Integer price, Timestamp timestamp) throws Exception {

        System.out.println("상품 입찰 업뎃 진행!");
        try {
            bid.setMyPrice(price);
            bid.setBidTime(timestamp);
            bidRepository.save(bid);

        } catch (Exception e) {
            log.error("Failed to update bid with id {}: {}", bid.getId(), e.getMessage());
            throw new Exception("Failed to update bid", e);  // Triggers rollback
        }
    }
    @Transactional
    public BidResDto startBid(String email, BidDto bidDto) {
        try {
            //유저 조회
            Long userId = memberService.findMemberByEmail(email).getId();

            //상품 조회
            Product product = productService.findById(bidDto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + bidDto.getProductId()));
            //해당 상품이 입찰 할 수 있는 조건들을 잘 지키고 있는지 유효성 검사
            validateBid(userId, product, bidDto);

            //입찰 로직 시작
            return processBid(userId, product, bidDto);

        } catch (Exception e) {
            throw new RuntimeException("Error during bidding: " + e.getMessage(), e);
        }
    }

    private void validateBid(Long userId, Product product, BidDto bidDto) {
        if (userId.equals(product.getMemberId())) {
            throw new IllegalStateException("Seller can't start bid for product with ID: " + product.getId());
        }

        if (product.hasWinner()) {
            throw new IllegalStateException("The product has already been won");
        }

        if (bidDto.getBidPrice() < product.getCurrentPrice()) {
            throw new IllegalArgumentException("Bid price must be higher than the current product price");
        }
    }

    private BidResDto processBid(Long userId, Product product, BidDto bidDto) {
        // 상품이 거래되고 있는중이다. (입찰이 존재한다)
        product.setAuctionStatus(AuctionStatus.PROGRESS);

        // 입찰가가 즉시구매가면 즉시 낙찰로 처리
        if (product.getImmediatePrice().equals(bidDto.getBidPrice())) {
            return handleImmediatePurchase(userId, product, bidDto);
        } else {
            // 정상적인 입찰
            return handleStandardBid(userId, product, bidDto);
        }
    }

    private BidResDto handleImmediatePurchase(Long userId, Product product, BidDto bidDto) {
        WinnerReqDto winnerReqDto = WinnerReqDto.builder()
                .buyPrice(bidDto.getBidPrice())
                .buyTime(bidDto.getBidTime())
                .productId(product.getId())
                .category(PurchaseCategory.BID)
                .build();
        WinnerDto winnerDto = WinnerDto.builder()
                .userId(userId)
                .product(product)
                .winnerReqDto(winnerReqDto)
                .build();
        productService.createWinner(winnerDto);

        return createBidResDto(product.getImmediatePrice(), BidStatus.ACCEPTED, Timestamp.from(Instant.now()));
    }

    // 입찰이 존재하면 update, 없으면 create
    private BidResDto handleStandardBid(Long userId, Product product, BidDto bidDto) {
        Integer currentPrice = updateBidPrice(product, bidDto.getBidPrice());

        try {
            bidRepository.findByMemberIdAndProduct(userId, product)
                    .ifPresentOrElse(
                            bid -> {
                                try {
                                    updateBid(bid, bidDto.getBidPrice(), bidDto.getBidTime());
                                } catch (Exception e) {
                                    throw new RuntimeException("Error updating bid: " + e.getMessage(), e);
                                }
                            },
                            () -> {
                                try {
                                    createBid(userId, product, bidDto.getBidPrice(), bidDto.getBidTime());
                                } catch (Exception e) {
                                    throw new RuntimeException("Error creating bid: " + e.getMessage(), e);
                                }
                            }
                    );
        } catch (RuntimeException e) {
            // 여기에 추가적인 예외 처리 로직을 추가할 수 있습니다.
            throw e;
        }

        return createBidResDto(currentPrice, BidStatus.CONFIRMED, Timestamp.from(Instant.now()));
    }

    private BidResDto createBidResDto(Integer bidCurrentPrice, BidStatus bidStatus, Timestamp timestamp) {
        return BidResDto.builder()
                .bidSuccessTime(timestamp)
                .bidCurrentPrice(bidCurrentPrice)
                .bidStatus(bidStatus)
                .build();
    }

    private Integer updateBidPrice(Product product, Integer bidPrice) {
        try {
            return productService.updateBidPrice(product, bidPrice); // Handle concurrency appropriately
        } catch (Exception e) {
            log.error("Failed to update bid price for productId {}: {}", product.getId(), e.getMessage());
            throw new RuntimeException("Failed to update bid price", e);
        }
    }


    /** 지영 - 마이페이지 경매 참여 내역 조회 시 필요 **/

    // 입찰 중 내역
    @Transactional
    public List<Bid> getBiddingBids(Long memberId) {
        return bidRepository.findBiddingBids(memberId);
    }

    // 낙찰 내역
    @Transactional
    public List<Bid> getWinningBids(Long memberId) {
        return bidRepository.findWinningBids(memberId);
    }

    // 패찰 내역
    @Transactional
    public List<Bid> getLosingBids(Long memberId) {
        return bidRepository.findLosingBids(memberId);
    }

    /* 지영 작업 끝 */


    @Transactional
    public Bid findTopByProductIdOrderByMyPriceDesc(Long id) {
        return bidRepository.findTopByProductIdOrderByMyPriceDesc(id).orElse(null);
    }
    @Transactional
    public Bid findByProductIdAndMemberId(Long productId, Long memberId) {
        return bidRepository.findByMemberIdAndProductId(memberId,productId).orElse(null);
    }
}

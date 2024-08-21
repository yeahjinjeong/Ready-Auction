package com.readyauction.app.auction.service;

import com.readyauction.app.auction.dto.*;
import com.readyauction.app.auction.entity.AuctionStatus;
import com.readyauction.app.auction.entity.Bid;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.entity.PurchaseCategory;
import com.readyauction.app.auction.repository.BidRepository;
import com.readyauction.app.auction.repository.ProductRepository;
import com.readyauction.app.cash.entity.PaymentCategory;
import com.readyauction.app.cash.dto.PaymentReqDto;
import com.readyauction.app.cash.service.PaymentService;
import com.readyauction.app.user.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.readyauction.app.auction.entity.BidStatus;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BidService {

    private final BidRepository bidRepository;
    private final MemberService memberService;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;
    private final EmailService emailService;

    final RedissonClient redissonClient;
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
    public Boolean endAuction(){
        // 마감될 경매 조회
        List<Product> products = productService.getProductsWithEndTimeAtCurrentMinute();

//        List<Product> products = productService.findAll(); 테스트용코드
        // 제일 비싼 사람들얻어오기
        System.out.println("마감 경매 조회");
        List<TopBidDto> TopBids = findTopBidsByProducts(products);

        System.out.println("제일 비싼 입찰자 얻어오기");
        //위 두개 조회 쿼리 하나의 쿼리로 합쳐도 ㄱㅊ을듯?


        // 해당 입찰기록으로 프로덕츠에 위너로 넣기 (위너 리스트 반환)
        List<WinnerDto> winnerDtos = new ArrayList<>();
        for(TopBidDto topBid : TopBids) {
            WinnerReqDto winnerReqDto = (WinnerReqDto.builder().
                    productId(topBid.getProduct().getId())).
                    buyPrice(topBid.getMyPrice()).
                    buyTime(topBid.getBidTime()).
                    build();
            WinnerDto winnerDto = WinnerDto.builder().
                    userId(topBid.getMemberId()).
                    product(topBid.getProduct()).
                    winnerReqDto(winnerReqDto).
                    build();
            winnerDtos.add(winnerDto);
            //위너 디티오 만들기 리스트로 만들기.

            System.out.println("입찰자 낙찰자로 변환" + topBid.getMemberId());
            EmailMessage emailMessage = EmailMessage.builder()
                    .to(memberService.findEmailById(topBid.getMemberId()))
                    .subject("중고 스포츠 유니폼 판매 플랫폼 레디옥션입니다.")
                    .message("<html><head></head><body><div style=\"background-color: gray;\">"+winnerDto.getProduct().getName()  +" 경매에서 낙찰자로 선정되신 것을 축하드립니다. 결제를 위해 레디옥션 사이트를 방문 해주세요"+"<div></body></html>")
                    .build();
            emailService.sendMail(emailMessage);
            System.out.println("성공 이메일 보내기");
        }
        productService.createWinners(winnerDtos);

        //  비낙찰자 롤백시키기 (위너 리스트의 멤버아이디를 통해 비낙찰자 찾아내고 롤백 시키기.)


        return true;
    }


    public BidResDto startBid(String email, BidDto bidDto) {
        final RLock lock = redissonClient.getLock(String.format("orderProduct:productId:%d", bidDto.getProductId()));
        try {
            if (!lock.tryLock(10, 1, TimeUnit.SECONDS)) {
                throw new RuntimeException("Redisson lock timeout");
            }

            Long userId = memberService.findMemberByEmail(email).getId();
            Product product = productService.findById(bidDto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + bidDto.getProductId()));

            validateBid(userId, product, bidDto);

            return processBid(userId, product, bidDto);

        } catch (Exception e) {
            throw new RuntimeException("Error during bidding: " + e.getMessage(), e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
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
        product.setAuctionStatus(AuctionStatus.PROGRESS);

        if (product.getImmediatePrice().equals(bidDto.getBidPrice())) {
            return handleImmediatePurchase(userId, product, bidDto);
        } else {
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

        return createBidResDto(currentPrice, BidStatus.ACCEPTED, Timestamp.from(Instant.now()));
    }

    public List<TopBidDto> findTopBidsByProducts(List<Product> products){
        return bidRepository.findTopBidsByProducts(products).orElseThrow(() -> new RuntimeException("No bids found for product ID: "));
    }

    public Boolean rollbackBids(Product product) {
        try {
            // Product 조회
            // 위너가 있는지 확인하고 예외 처리
            if (product.getWinner() == null) {
                throw new RuntimeException("No winner found for product ID: " + product.getId());
            }


            // Payment 롤백 처리
            paymentService.rollbackPayment(product.getId());

            return true;
        } catch (RuntimeException e) {
            // 모든 RuntimeException을 잡아 적절한 메시지를 포함하여 다시 던짐
            throw new RuntimeException("Error during rollback of bids for product ID: " + product.getId() + ". " + e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예상치 못한 예외 처리
            throw new RuntimeException("Unexpected error during rollback of bids for product ID: " + product.getId() + ". " + e.getMessage(), e);
        }
    }

    public Boolean rollbackBids(Long productId) {
        Product product = productService.findById(productId).get();
        try {
            // Product 조회
            // 위너가 있는지 확인하고 예외 처리
            if (product.getWinner() == null) {
                throw new RuntimeException("No winner found for product ID: " + product.getId());
            }

            // 롤백할 Bid 리스트 조회
            // Payment 롤백 처리
            paymentService.rollbackPayment(product.getId());

            return true;
        } catch (RuntimeException e) {
            // 모든 RuntimeException을 잡아 적절한 메시지를 포함하여 다시 던짐
            throw new RuntimeException("Error during rollback of bids for product ID: " + product.getId() + ". " + e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예상치 못한 예외 처리
            throw new RuntimeException("Unexpected error during rollback of bids for product ID: " + product.getId() + ". " + e.getMessage(), e);
        }
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
    public List<Bid> getBidsByStatus(Long memberId, BidStatus status) {
        List<Bid> bids = bidRepository.findByMemberIdAndBidStatus(memberId, status);
        System.out.println("Bids for status " + status + ": " + bids);
        return bids;
    }
 }

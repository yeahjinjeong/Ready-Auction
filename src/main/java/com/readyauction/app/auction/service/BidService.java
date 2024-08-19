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
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.readyauction.app.auction.entity.BidStatus;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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


    public boolean createBid(Long userId, Product product, Integer price, Timestamp timestamp) {

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
            product.setCurrentPrice(price);
            productRepository.save(product);
            System.out.println("상품 입찰 저장 완료!");
            return true;  // Successfully saved
        } catch (Exception e) {
            log.error("Failed to create bid for userId {} on productId {}: {}", userId, product.getId(), e.getMessage());
            throw new RuntimeException("Failed to create bid", e);  // Triggers rollback
        }
    }

    public boolean updateBid(Bid bid, Integer price, Timestamp timestamp) {

        System.out.println("상품 입찰 업뎃 진행!");
        try {
            bid.setMyPrice(price);
            bid.setBidTime(timestamp);
            bidRepository.save(bid);
            bid.getProduct().setCurrentPrice(price);
            productRepository.save(bid.getProduct());
            return true;
        } catch (Exception e) {
            log.error("Failed to update bid with id {}: {}", bid.getId(), e.getMessage());
            throw new RuntimeException("Failed to update bid", e);  // Triggers rollback
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
        }
        productService.createWinners(winnerDtos);

        //  비낙찰자 롤백시키기 (위너 리스트의 멤버아이디를 통해 비낙찰자 찾아내고 롤백 시키기.)


        return true;
    }
    public BidResDto startBid(String email, BidDto bidDto) {
        try {
            // 사용자 조회
            Long userId = memberService.findMemberByEmail(email).getId();

            // 제품 조회
            Product product = productService.findById(bidDto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + bidDto.getProductId()));
            if(userId.equals(product.getMemberId())) {
                throw new IllegalStateException("Seller can't start bid for product with ID: " + bidDto.getProductId());
            }
            System.out.println("상품 조회 성공");
            // 제품이 이미 낙찰되었는지 확인
            if (product.hasWinner()) {
                throw new IllegalStateException("The product has already been won");
            }

            System.out.println("낙찰 여부 체크 성공");
            // 입찰 가격 유효성 검사
            if (bidDto.getBidPrice() < product.getCurrentPrice()) {
                throw new IllegalArgumentException("Bid price must be higher than the current product price");
            }

            System.out.println("가격 유효성 체크 성공");
            // 입찰 가격 갱신

            product.setAuctionStatus(AuctionStatus.PROGRESS);

            BidResDto bidResDto;
            if (product.getImmediatePrice().equals(bidDto.getBidPrice())) {
                // 즉시 구매 처리
                WinnerReqDto winnerReqDto = WinnerReqDto.builder()
                        .buyPrice(bidDto.getBidPrice())
                        .buyTime(bidDto.getBidTime())
                        .productId(product.getId())
                        .category(PurchaseCategory.BID)
                        .build();
                WinnerDto winnerDto = WinnerDto.builder().
                        userId(userId)
                        .product(product)
                        .winnerReqDto(winnerReqDto)
                        .build();
                productService.createWinner(winnerDto);

                bidResDto = createBidResDto(product.getImmediatePrice(), BidStatus.ACCEPTED,Timestamp.from(Instant.now()));

            } else {

                System.out.println("상품 입찰 진행!");
                Integer currentPrice = updateBidPrice(product, bidDto.getBidPrice());
                // 기존 입찰이 있는 경우 갱신, 없는 경우 새로 생성
                bidRepository.findByMemberIdAndProduct(userId, product)
                        .ifPresentOrElse(
                                bid -> updateBid(bid, bidDto.getBidPrice(), bidDto.getBidTime()),
                                () -> createBid(userId, product, bidDto.getBidPrice(), bidDto.getBidTime())
                        );
                bidResDto = createBidResDto(currentPrice,BidStatus.CONFIRMED,Timestamp.from(Instant.now()));
            }

            // 제품 정보 저장
            productRepository.save(product);
        return bidResDto;

        } catch (EntityNotFoundException e) {
            // 특정 엔티티를 찾지 못했을 때의 예외 처리
            throw new RuntimeException("Error during bidding: " + e.getMessage(), e);
        } catch (IllegalStateException e) {
            // 제품이 이미 낙찰되었을 때의 예외 처리
            throw new RuntimeException("Error during bidding: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            // 입찰 가격이 유효하지 않을 때의 예외 처리
            throw new RuntimeException("Invalid bid price: " + e.getMessage(), e);
        } catch (DataAccessException e) {
            // 데이터베이스 관련 예외 처리
            throw new RuntimeException("Database error during bidding: " + e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RuntimeException("Unexpected error occurred during bidding: " + e.getMessage(), e);
        }
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

    /** 지영 - 마이페이지 경매 내역 조회 시 필요 **/
    public List<Bid> getBidsByStatus(Long memberId, BidStatus status) {
        return bidRepository.findByMemberIdAndBidStatus(memberId, status);
    }
 }

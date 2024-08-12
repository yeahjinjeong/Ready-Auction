package com.readyauction.app.auction.service;

import com.readyauction.app.auction.dto.BidDto;
import com.readyauction.app.auction.dto.BidResDto;
import com.readyauction.app.auction.dto.BidStatus;
import com.readyauction.app.auction.dto.WinnerReqDto;
import com.readyauction.app.auction.entity.AuctionStatus;
import com.readyauction.app.auction.entity.Bid;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.repository.BidRepository;
import com.readyauction.app.auction.repository.ProductRepository;
import com.readyauction.app.user.repository.MemberRepository;
import com.readyauction.app.user.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BidService {

    private final BidRepository bidRepository;
    private final MemberService memberService;
    private final ProductService productService;
    private final ProductRepository productRepository;

    public boolean createBid(Long userId, Product product, Integer price, Timestamp timestamp) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        try {
            Bid bid = Bid.builder()
                    .memberId(userId)
                    .product(product)
                    .myPrice(price)
                    .bidTime(timestamp)
                    .build();
            bidRepository.save(bid);
            return true;  // Successfully saved
        } catch (Exception e) {
            log.error("Failed to create bid for userId {} on productId {}: {}", userId, product.getId(), e.getMessage());
            throw new RuntimeException("Failed to create bid", e);  // Triggers rollback
        }
    }

    public boolean updateBid(Bid bid, Integer price, Timestamp timestamp) {

        try {
            bid.setMyPrice(price);
            bid.setBidTime(timestamp);
            bidRepository.save(bid);
            return true;
        } catch (Exception e) {
            log.error("Failed to update bid with id {}: {}", bid.getId(), e.getMessage());
            throw new RuntimeException("Failed to update bid", e);  // Triggers rollback
        }
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
            // 제품이 이미 낙찰되었는지 확인
            if (product.hasWinner()) {
                throw new IllegalStateException("The product has already been won");
            }

            // 입찰 가격 유효성 검사
            if (bidDto.getBidPrice() < product.getCurrentPrice()) {
                throw new IllegalArgumentException("Bid price must be higher than the current product price");
            }

            // 입찰 가격 갱신

            product.setAuctionStatus(AuctionStatus.PROGRESS);

            BidResDto bidResDto;
            if (product.getImmediatePrice().equals(bidDto.getBidPrice())) {
                // 즉시 구매 처리
                WinnerReqDto winnerReqDto = WinnerReqDto.builder()
                        .buyPrice(bidDto.getBidPrice())
                        .buyTime(bidDto.getBidTime())
                        .productId(product.getId())
                        .build();
                productService.createWinner(userId, product, winnerReqDto);

                bidResDto = createBidResDto(product.getImmediatePrice(),BidStatus.SUCCESS,Timestamp.from(Instant.now()));

            } else {
                Integer currentPrice = updateBidPrice(product, bidDto.getBidPrice());
                // 기존 입찰이 있는 경우 갱신, 없는 경우 새로 생성
                bidRepository.findByMemberIdAndProduct(userId, product)
                        .ifPresentOrElse(
                                bid -> updateBid(bid, bidDto.getBidPrice(), bidDto.getBidTime()),
                                () -> createBid(userId, product, bidDto.getBidPrice(), bidDto.getBidTime())
                        );
                bidResDto = createBidResDto(currentPrice,BidStatus.BID,Timestamp.from(Instant.now()));
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
}

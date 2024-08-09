package com.readyauction.app.auction.service;

import com.readyauction.app.auction.dto.BidDto;
import com.readyauction.app.auction.entity.AuctionStatus;
import com.readyauction.app.auction.entity.Bid;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.repository.BidRepository;
import com.readyauction.app.auction.repository.ProductRepository;
import com.readyauction.app.user.repository.MemberRepository;
import com.readyauction.app.user.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BidService {

    private final BidRepository bidRepository;
    private final MemberService memberService;
    private final ProductService productService;
    private final ProductRepository productRepository;

    @Transactional
    public boolean createBid(Long userId, Product product, Integer price, Timestamp timestamp) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
//
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

    @Transactional
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

    @Transactional
    public Integer startBid(HttpServletRequest request, BidDto bidDto) {
        Long userId = memberService.findMemberIdByEmail(request.getHeader("email"));
        Product product = productService.findById(bidDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.hasWinner()) {
            throw new RuntimeException("The product has already been won");
        }

        // Validate the bid price
        if (bidDto.getBidPrice() <product.getCurrentPrice()) {
            throw new RuntimeException("Bid price must be higher than the current product price");
        }
        updateBidPrice(product, bidDto.getBidPrice());
        product.setAuctionStatus(AuctionStatus.PROGRESS);
        productRepository.save(product);
        bidRepository.findByMemberIdAndProduct(userId, product)
                .ifPresentOrElse(
                        bid -> updateBid(bid, bidDto.getBidPrice(), bidDto.getBidTime()),
                        () -> createBid(userId, product, bidDto.getBidPrice(), bidDto.getBidTime())
                );

        return productService.findCurrentPriceById(bidDto.getProductId());
    }


    private void updateBidPrice(Product product, Integer bidPrice) {
        try {
            productService.updateBidPrice(product, bidPrice); // Handle concurrency appropriately
        } catch (Exception e) {
            log.error("Failed to update bid price for productId {}: {}", product.getId(), e.getMessage());
            throw new RuntimeException("Failed to update bid price", e);
        }
    }
}

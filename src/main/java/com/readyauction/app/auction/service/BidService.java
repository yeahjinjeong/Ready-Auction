package com.readyauction.app.auction.service;


import com.readyauction.app.auction.dto.BidDto;
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
    private final ProductRepository productRepository;
    private final MemberRepository userRepository;

    @Transactional
    public boolean createBid(Long userId, Product product, Integer price, Timestamp timestamp) {
        assert product != null;
        try {
            Bid bid = Bid.builder()
                    .memberId(userId)
                    .product(product)
                    .myPrice(price)
                    .bidTime(timestamp)
                    .build();
            bidRepository.save(bid);
            return true;  // 성공적으로 저장
        } catch (Exception e) {
            log.error("Failed to create bid: " + e.getMessage());
            throw new RuntimeException("Failed to create bid", e);  // 예외를 던짐으로써 롤백 유발
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
            log.error("Failed to update bid: " + e.getMessage());
            throw new RuntimeException("Failed to update bid", e);  // 예외를 던짐으로써 롤백 유발
        }
    }

    @Transactional
    public void startBid(HttpServletRequest request, BidDto bidDto) {
        Long userId = memberService.findMemberByEmail(request.getHeader("email")).getId();
        Optional<Product> optionalProduct = productRepository.findById(bidDto.getProductId());
        if (!optionalProduct.isPresent()) {
            throw new RuntimeException("Product not found");
        }
        Product product = optionalProduct.get();
        Optional<Bid> optionalBid = bidRepository.findByMemberIdAndProduct(userId, product);
        if (optionalBid.isPresent()) {
            Bid bid = optionalBid.get();
            updateBid(bid, bidDto.getBidPrice(), bidDto.getBidTime());  // 여기서 실패하면 RuntimeException이 던져짐
        } else {
            createBid(userId, product, bidDto.getBidPrice(), bidDto.getBidTime());  // 여기서 실패하면 RuntimeException이 던져짐
        }
    }
}

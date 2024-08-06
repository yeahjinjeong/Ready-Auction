package com.readyauction.app.auction.service;


import com.readyauction.app.auction.dto.ProductDto;
import com.readyauction.app.auction.dto.ProductRepDto;
import com.readyauction.app.auction.dto.ProductReqDto;
import com.readyauction.app.auction.dto.WinnerReqDto;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.entity.PurchaseStatus;
import com.readyauction.app.auction.entity.Winner;
import com.readyauction.app.auction.repository.ProductRepository;
import com.readyauction.app.auction.repository.WinnerRepository;
import com.readyauction.app.file.model.dto.FileDto;
import com.readyauction.app.file.model.service.NcpObjectStorageService;
import com.readyauction.app.user.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class WinnerService {

    private final WinnerRepository winnerRepository;
    private final NcpObjectStorageService ncpObjectStorageService;
    private final MemberService memberService;
    private final ProductRepository productRepository;

    @Transactional
    public boolean createWinner(Long userId, Long productId, Integer price) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            log.error("Product not found with ID: " + productId);
            return false;
        }
        Product product = productOptional.get();

        Winner winner = Winner.builder()
                .memberId(userId)
                .productId(productId)
                .price(price)
                .winnerTime(new Timestamp(System.currentTimeMillis()))
                .status(PurchaseStatus.CONFIRMED)
                .build();
        winnerRepository.save(winner);
        log.info("Winner created successfully for product ID: " + productId);
        return true;
    }

    // 스테이터스 변경 할 때
    @Transactional
    public boolean statusUpdate(HttpServletRequest request, Long productId,PurchaseStatus purchaseStatus) {
        Long userId = memberService.findMemberByEmail(request.getHeader("email")).getId();

        Optional<Winner> optionalWinner = winnerRepository.findByProductId(productId);
        if (!optionalWinner.isPresent()) {
            log.error("Winner not found with ID: " + productId);
            return false;
        }
        Winner winner = optionalWinner.get();
        winner.setStatus(purchaseStatus);
        winnerRepository.save(winner);
        return true;
    }

    @Transactional
    public void startWinnerProcess(HttpServletRequest request, WinnerReqDto winnerReqDto) {
        Long userId = memberService.findMemberByEmail(request.getHeader("email")).getId();
        if (!productRepository.existsById(winnerReqDto.getProductId())) {
            throw new RuntimeException("Product not found");
        }

        Optional<Winner> optionalWinner = winnerRepository.findByProductId(winnerReqDto.getProductId());
        if (optionalWinner.isPresent()) {
            System.out.println("구매 된 상품입니다.");
            throw new RuntimeException("구매 된 상품입니다.");
        } else {
            createWinner(userId, winnerReqDto.getProductId(), winnerReqDto.getBuyPrice());
        }
    }

}

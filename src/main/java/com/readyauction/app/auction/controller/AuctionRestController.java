package com.readyauction.app.auction.controller;

import com.readyauction.app.auction.dto.BidDto;
import com.readyauction.app.auction.dto.ProductReqDto;
import com.readyauction.app.auction.service.BidService;
import com.readyauction.app.auction.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("auction-api")
@RequiredArgsConstructor
public class AuctionRestController {
    final ProductService productService;
    final BidService bidService;
    @PostMapping("/create")
        public ResponseEntity<ProductReqDto> createAuction(HttpServletRequest request,@RequestBody ProductReqDto productReqDto) {
        log.info(request.getHeader("email")+"이메일");
        log.info(productReqDto.toString());

        ProductReqDto createdProduct = productService.createProduct(request,productReqDto);
        if (createdProduct == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(createdProduct);
    }
    @PostMapping("/Update")
    public String updateAuction() {
        return "업데이트 된 상품정보 json 형식으로 반환";
    }

    @PostMapping("/img-upload")
    public String uploadImg(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        System.out.println("사진 올라감");
        return productService.uploadFile(request,file);
    }
    @PostMapping("/bid")
    public ResponseEntity<?> bid(HttpServletRequest request, @RequestBody BidDto bidDto) {
        try {
            bidService.startBid(request, bidDto);
            return ResponseEntity.ok(bidDto); // 성공적으로 처리되면, 200 OK와 함께 bidDto를 반환
        } catch (RuntimeException e) {
            // startBid에서 던져진 RuntimeException을 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Bid failed: " + e.getMessage());
        }
    }
}

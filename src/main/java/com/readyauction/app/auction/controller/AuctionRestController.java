package com.readyauction.app.auction.controller;

import com.readyauction.app.auction.dto.*;
import com.readyauction.app.auction.entity.PurchaseCategory;
import com.readyauction.app.auction.service.BidService;
import com.readyauction.app.auction.service.ProductService;
import com.readyauction.app.chat.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("auction-api")
@RequiredArgsConstructor
public class AuctionRestController {
    final ProductService productService;
    final BidService bidService;
    final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/create")
        public ResponseEntity<ProductRepDto> createAuction(HttpServletRequest request,@RequestBody ProductReqDto productReqDto) {
        log.info(request.getHeader("email")+"이메일");
        log.info(productReqDto.toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        ProductRepDto createdProduct = productService.createProduct(email,productReqDto);
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
    public ResponseEntity<Map<String, Object>> uploadImg(@RequestParam("files[]") List<MultipartFile> files) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        List<String> results = new ArrayList<>();

        // 파일 리스트를 순회하며 각 파일 처리
        for (MultipartFile file : files) {
            System.out.println("사진 올라감");
            String result = productService.uploadFile(email, file);
            results.add(result);  // 결과를 리스트에 추가
        }

        Map<String, Object> response = new HashMap<>();
        response.put("images", results);  // "images" 키에 파일 URL 리스트 저장
            return ResponseEntity.ok(response);  // JSON 형태로 결과 반환
    }



    @PostMapping("/bid")
    public ResponseEntity<BidResDto> bid(@RequestBody BidDto bidDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        BidResDto bidResDto = new BidResDto();
        try {
            System.out.println("입찰중 "+ email);
            bidResDto = bidService.startBid(email, bidDto);
            System.out.println(bidDto);
            simpMessagingTemplate.convertAndSend("/sub/"+bidDto.getProductId(),bidResDto);
            return ResponseEntity.ok(bidResDto); // 성공적으로 처리되면, 200 OK와 함께 bidDto를 반환
        } catch (RuntimeException e) {
            // startBid에서 던져진 RuntimeException을 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(bidResDto);
        }
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buy(@RequestBody WinnerReqDto winnerReqDto) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            System.out.println("즉시구매중" + winnerReqDto);
            winnerReqDto.setCategory(PurchaseCategory.IMMEDIATE);
            return ResponseEntity.ok(productService.startWinnerProcess(email,winnerReqDto));
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Bid failed: " + e.getMessage());
        }
    }
}

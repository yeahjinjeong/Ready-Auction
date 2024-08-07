package com.readyauction.app.auction.controller;

import com.readyauction.app.auction.dto.ProductDto;
import com.readyauction.app.auction.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("auction-searching-api")
public class AuctionSearchRestController {

    private final ProductService productService;

    @GetMapping("/searching") // 상품 조회
    public List<ProductDto> searchAuction(@RequestParam("query") String query) {
        List<ProductDto> searchResults = productService.searchProducts(query);
        return searchResults;
    }
    @GetMapping("/suggestions") // 자동 완성
    public List<String> getSuggestions(@RequestParam("query") String query) {
        return productService.getSuggestions(query);
    }
}

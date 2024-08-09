package com.readyauction.app.auction.service;

import com.readyauction.app.auction.dto.ProductDto;
import com.readyauction.app.auction.dto.ProductRepDto;
import com.readyauction.app.auction.dto.ProductReqDto;
import com.readyauction.app.auction.dto.WinnerReqDto;
import com.readyauction.app.auction.entity.AuctionStatus;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.entity.PurchaseStatus;
import com.readyauction.app.auction.entity.Winner;
import com.readyauction.app.auction.repository.ProductRepository;
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
public class ProductService {

    private final ProductRepository productRepository;
    private final NcpObjectStorageService ncpObjectStorageService;
    private final MemberService memberService;
    @Transactional
    public ProductRepDto createProduct(HttpServletRequest request, ProductReqDto productReqDto) {
        Long userId = getUserIdFromRequest(request);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Product product = Product.builder()
                .memberId(userId)
                .name(productReqDto.getName())
                .category(productReqDto.getCategory())
                .description(productReqDto.getDescription())
                .bidUnit(productReqDto.getBidUnit())
                .endTime(productReqDto.getEndTime())
                .startTime(timestamp)
                .currentPrice(productReqDto.getCurrentPrice())
                .immediatePrice(productReqDto.getImmediatePrice())
                .image(productReqDto.getImgUrl())
                .auctionStatus(AuctionStatus.START)
                .build();
        product = productRepository.save(product);

        return convertToProductRepDto(product);
    }

    @Transactional
    public String uploadFile(HttpServletRequest request, MultipartFile multipartFile) {
        validateMultipartFile(multipartFile);

        String pathPrefix = request.getHeader("email");
        List<FileDto> s3Files = ncpObjectStorageService.uploadFiles(Collections.singletonList(multipartFile), pathPrefix);

        return s3Files.stream()
                .findFirst()
                .map(FileDto::getUploadFileUrl)
                .orElseThrow(() -> new IllegalStateException("File upload failed"));
    }

    @Transactional
    public ProductRepDto productDetail(Long productId) {
        Product product = findProductById(productId);
        return convertToProductRepDto(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToProductDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Boolean updateBidPrice(Product product, Integer bidPrice) {
        try {
            product.setCurrentPrice(bidPrice + product.getBidUnit());
            productRepository.save(product);
            return true;
        } catch (Exception e) {
            log.error("Failed to update bid price for productId {}: {}", product.getId(), e.getMessage());
            return false;
        }
    }
    @Transactional
    public Integer findCurrentPriceById(Long productId)
    {
        Product productResult = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        return productResult.getCurrentPrice();
    }

    @Transactional(readOnly = true)
    public Optional<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }

    @Transactional
    public boolean statusUpdate(HttpServletRequest request, Long productId, PurchaseStatus purchaseStatus) {
        Product product = findProductById(productId);
        product.getWinner().setStatus(purchaseStatus);
        productRepository.save(product);
        return true;
    }

    @Transactional
    public ProductDto startWinnerProcess(HttpServletRequest request, WinnerReqDto winnerReqDto) {
        Long userId = getUserIdFromRequest(request);
        Product product = findProductById(winnerReqDto.getProductId());

        if (product.hasWinner()) {
            throw new RuntimeException("The product has already been won");
        }

        return convertToProductDto(createWinner(userId, product, winnerReqDto));
    }

    @Transactional
    public Product createWinner(Long userId, Product product, WinnerReqDto winnerReqDto) {
        Winner winner = Winner.builder()
                .memberId(userId)
                .status(PurchaseStatus.CONFIRMED)
                .price(winnerReqDto.getBuyPrice())
                .winnerTime(winnerReqDto.getBuyTime())
                .build();
        product.setWinner(winner);
        product.setAuctionStatus(AuctionStatus.END);
        log.info("Winner created successfully for product ID: {}", product.getId());

        return productRepository.save(product);

    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        try {
            return memberService.findMemberByEmail(request.getHeader("email")).getId();
        } catch (Exception e) {
            log.error("Failed to retrieve user ID: {}", e.getMessage());
            throw new RuntimeException("User not found", e);
        }
    }

    private void validateMultipartFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalStateException("No image file provided");
        }
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found with ID: " + productId));
    }

    private ProductRepDto convertToProductRepDto(Product product) {
        return ProductRepDto.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .description(product.getDescription())
                .bidUnit(product.getBidUnit())
                .endTime(product.getEndTime())
                .startTime(product.getStartTime())
                .currentPrice(product.getCurrentPrice())
                .immediatePrice(product.getImmediatePrice())
                .imgUrl(product.getImage())
                .build();
    }

    private ProductDto convertToProductDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getBidUnit(),
                product.getEndTime(),
                product.getCurrentPrice(),
                product.getImmediatePrice(),
                product.getImage()
        );
    }

    @Transactional(readOnly = true)
    public List<ProductDto> searchProductsByName(String query) {
        return productRepository.findByNameContainingIgnoreCase(query).stream()
                .map(this::convertToProductDto)
                .collect(Collectors.toList());
    }
}

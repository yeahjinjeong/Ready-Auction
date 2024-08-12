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
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
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
    public ProductRepDto createProduct(String email, ProductReqDto productReqDto) {
        Long userId = getUserIdFromRequest(email);
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
    public String uploadFile(String email, MultipartFile multipartFile) {
        validateMultipartFile(multipartFile);
        List<FileDto> s3Files = ncpObjectStorageService.uploadFiles(Collections.singletonList(multipartFile), "productIMG/"+ email);

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
    public Integer updateBidPrice(Product product, Integer bidPrice) {
        try {
            product.setCurrentPrice(bidPrice + product.getBidUnit());
            productRepository.save(product);
            return bidPrice + product.getBidUnit();
        } catch (Exception e) {
            throw new RuntimeException("Update bid price failed", e);
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
    public ProductDto startWinnerProcess(String email, WinnerReqDto winnerReqDto) {
        Long userId = getUserIdFromRequest(email);
        Product product = findProductById(winnerReqDto.getProductId());
        if(userId.equals(product.getMemberId())) {
            throw new IllegalStateException("Seller can't start bid for product with ID: " + winnerReqDto.getProductId());
        }
        if (product.hasWinner()) {
            throw new RuntimeException("The product has already been won");
        }

        return convertToProductDto(createWinner(userId, product, winnerReqDto));
    }

    @Transactional
    public Product progressWinnerProcess(Long productId) {
        try {

            Product product = findProductById(productId);

            if (product == null) {
                throw new EntityNotFoundException("Product not found with ID: " + productId);
            }

            if (product.hasWinner()) {
                product.getWinner().setStatus(PurchaseStatus.IN_PROGRESS);
            }

            Product savedProduct = productRepository.save(product);
            if (savedProduct == null) {
                throw new RuntimeException("Failed to save the product");
            }

            return savedProduct;
        } catch (EntityNotFoundException e) {
            // 제품을 찾지 못했을 때의 예외 처리
            throw new RuntimeException("Error during product search: " + e.getMessage(), e);
        } catch (DataAccessException e) {
            // 데이터베이스 관련 예외 처리
            throw new RuntimeException("Database error during saving product: " + e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RuntimeException("Unexpected error occurred during winner process: " + e.getMessage(), e);
        }
    }


    @Transactional
    public Product createWinner(Long userId, Product product, WinnerReqDto winnerReqDto) {
        try {
            // Winner 객체를 생성하고 제품에 설정
            Winner winner = Winner.builder()
                    .memberId(userId)
                    .status(PurchaseStatus.CONFIRMED)
                    .price(winnerReqDto.getBuyPrice())
                    .winnerTime(winnerReqDto.getBuyTime())
                    .build();
            product.setWinner(winner);
            product.setAuctionStatus(AuctionStatus.END);

            log.info("Winner created successfully for product ID: {}", product.getId());

            // 제품을 저장
            Product savedProduct = productRepository.save(product);

            if (savedProduct == null) {
                throw new RuntimeException("Failed to save the product with the winner");
            }

            return savedProduct;
        } catch (DataAccessException e) {
            // 데이터베이스 관련 예외 처리
            throw new RuntimeException("Database error during saving the product with the winner: " + e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RuntimeException("Unexpected error occurred during creating the winner: " + e.getMessage(), e);
        }
    }

    private Long getUserIdFromRequest(String email) {
        try {
            return memberService.findMemberByEmail(email).getId();
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

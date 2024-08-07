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
//import com.readyauction.app.user.service.MemberService;
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
   // private final MemberService memberService;
    @Transactional
    public ProductRepDto createProduct(HttpServletRequest request, ProductReqDto productReqDto) {
        // Create and save the Product entity
        Long userId = 0L;
        try {
     //    userId = memberService.findMemberByEmail(request.getHeader("email")).getId();
        log.info("유저아이디 " + userId);

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println("Current Timestamp: " + timestamp);
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

            ProductRepDto productRepDto = ProductRepDto.builder()
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
            return productRepDto;

        }
    }

    @Transactional
    public String uploadFile(HttpServletRequest request, MultipartFile multipartFile) {
        MultipartFile file = multipartFile;
        if (file == null || file.isEmpty()) {
            throw new IllegalStateException("No image file provided");
        }

        // Building the path prefix for file storage
        String pathPrefix = request.getHeader("email");

        // Upload the file to S3 and retrieve the file URL
        List<FileDto> s3files = ncpObjectStorageService.uploadFiles(Collections.singletonList(file), pathPrefix);
        if (s3files.isEmpty()) {
            throw new IllegalStateException("File upload failed");
        }

        String filePath = s3files.get(0).getUploadFileUrl();

        return filePath;
    }

    @Transactional
    public ProductRepDto productDetail(Long productId) {
        System.out.println("상품 검색중");
        //상품검색
        Optional<Product> productOptional = productRepository.findById(productId);
        // 2. 상품 검증
        if (productOptional.isEmpty()) {
            throw new IllegalStateException("상품을 찾을 수 없습니다.");
        }

        Product product = productOptional.get();

        // 3. DTO 변환
        ProductRepDto productRepDto = ProductRepDto.builder()
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

        // 4. 결과 반환
        return productRepDto;
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(product -> new ProductDto(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getBidUnit(),
                product.getEndTime(),
                product.getCurrentPrice(),
                product.getImmediatePrice(),
                product.getImage()
        )).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDto> searchProducts(String query) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(query);
        return products.stream().map(product -> new ProductDto(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getBidUnit(),
                product.getEndTime(),
                product.getCurrentPrice(),
                product.getImmediatePrice(),
                product.getImage()
        )).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getSuggestions(String query) {
        return productRepository.findByNameContainingIgnoreCase(query)
                .stream()
                .map(Product::getName)
                .collect(Collectors.toList());
    }

    @Transactional
    public Boolean updateBidPrice(Product product, Integer bidPrice) { // 여기서 동시성 처리 필요함
        try {
            product.setCurrentPrice(bidPrice + product.getBidUnit());
            productRepository.save(product);
            return true; // 성공적으로 업데이트 및 저장이 완료되면 true 반환
        } catch (Exception e) {
            // 예외가 발생하면 로그를 기록하고 false 반환
            System.err.println("Failed to update bid price: " + e.getMessage());
            return false;
        }
    }

    @Transactional
    public Optional<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }

    // 스테이터스 변경 할 때
    @Transactional
    public boolean statusUpdate(HttpServletRequest request, Long productId, PurchaseStatus purchaseStatus) {
        Long userId = memberService.findMemberByEmail(request.getHeader("email")).getId();
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (!optionalProduct.isPresent()) {
            log.error("Product not found with ID: " + productId);
            return false;
        }
        Product product = optionalProduct.get();
        product.getWinner().setStatus(purchaseStatus);
        productRepository.save(product);
        return true;
    }


    @Transactional
    public void startWinnerProcess(HttpServletRequest request, WinnerReqDto winnerReqDto) {
        Long userId = memberService.findMemberByEmail(request.getHeader("email")).getId();
        Optional<Product> productOptional = productRepository.findById(winnerReqDto.getProductId());
        Product product = productOptional.get();

        if (product.getWinner() != null) {
            // The winner exists, so do something with the winner
            System.out.println("The winner is present: " + product.getWinner().getMemberId());
        } else {
            // The winner does not exist
            createWinner(userId, product, winnerReqDto);

        }

    }

    @Transactional
    public boolean createWinner(Long userId, Product product, WinnerReqDto winnerReqDto) {
        product.getWinner().setMemberId(userId);
        product.getWinner().setStatus(PurchaseStatus.CONFIRMED);
        product.getWinner().setPrice(winnerReqDto.getBuyPrice());
        product.getWinner().setWinnerTime(winnerReqDto.getBuyTime());
        product.setAuctionStatus(AuctionStatus.END);
        productRepository.save(product);
        log.info("Winner created successfully for product ID: " + product.getId());
        return true;
    }
}







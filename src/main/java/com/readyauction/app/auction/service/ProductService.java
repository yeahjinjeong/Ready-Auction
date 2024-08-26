package com.readyauction.app.auction.service;

import com.readyauction.app.auction.dto.*;
import com.readyauction.app.auction.entity.*;
import com.readyauction.app.auction.repository.ProductRepository;

import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

import com.readyauction.app.cash.dto.PaymentReqDto;
import com.readyauction.app.cash.entity.PaymentCategory;
import com.readyauction.app.ncp.dto.FileDto;
import com.readyauction.app.ncp.service.NcpObjectStorageService;
import com.readyauction.app.user.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {
    private final RedissonClient redissonClient;
    private final ProductRepository productRepository;

    private final NcpObjectStorageService ncpObjectStorageService;
    private final MemberService memberService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    public List<Product> findAll(){
        return productRepository.findAll();
    }
    @Transactional
    public ProductRepDto createProduct(String email, ProductReqDto productReqDto) {
        Long userId = getUserIdFromRequest(email);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        System.out.println(productReqDto.getImgUrls().get(0));
        Product product = Product.builder()
                .memberId(userId)
                .name(productReqDto.getName())
                .category(productReqDto.getCategory())
                .description(productReqDto.getDescription())
                .bidUnit(productReqDto.getBidUnit())
                .endTime(productReqDto.getEndTime())
                .startTime(timestamp)
                .startPrice(productReqDto.getCurrentPrice())
                .currentPrice(productReqDto.getCurrentPrice())
                .immediatePrice(productReqDto.getImmediatePrice())
                .images(productReqDto.getImgUrls())
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
    @Transactional
    public Integer updateBidPrice(Product product, Integer bidPrice) {
        try {
            product.setCurrentPrice(bidPrice + product.getBidUnit());
            return productRepository.save(product).getCurrentPrice();

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
    @Transactional
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
    public List<Product> getProductsWithEndTimeAtCurrentMinute() {
        try {
            // 현재 시간의 시분으로 설정, 초와 나노초를 0으로 만듦
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

            // 현재 시분 00초 (startTime)
            Timestamp startTime = Timestamp.valueOf(now);

            // 현재 시분 59초 (endTime)
            Timestamp endTime = Timestamp.valueOf(now.plusMinutes(1).minusSeconds(1));

            // 해당 범위에 속하는 Product 목록 조회
            return productRepository.findByEndTimeBetween(startTime, endTime);
        } catch (DataAccessException e) {
            // 데이터베이스 관련 예외 처리
            throw new RuntimeException("Database error occurred while fetching products with end time at current minute: " + e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RuntimeException("Unexpected error occurred while fetching products: " + e.getMessage(), e);
        }
    }

    public ProductRepDto createAuction(String email, ProductReqDto productReqDto) {
        // Redis에 경매 ID와 TTL 설정

        ProductRepDto productRepDto = createProduct(email,productReqDto);
        Timestamp currentTime = Timestamp.from(Instant.now());
        long durationInSeconds = calculateTimeDifference(currentTime, productRepDto.getEndTime()).getSeconds();
        // 경매 시작 시 TTL 설정 (예: 1시간)

        String pid = ManagementFactory.getRuntimeMXBean().getName();
        String key =  "Auction:ProductId:" + productRepDto.getId();
        redisTemplate.opsForValue().set(key,pid);
        redisTemplate.expire(key,durationInSeconds, TimeUnit.SECONDS);

        log.info("Auction started for product ID: " + productRepDto.getId());
        return productRepDto;
    }
    public static Duration calculateTimeDifference(Timestamp startTime, Timestamp endTime) {
        // startTime과 endTime의 차이를 Duration 객체로 변환
        return Duration.between(startTime.toInstant(), endTime.toInstant());
    }
    @Transactional
    public List<Product> setProductsStatus(List<Product> products) {
        try {
            // Product 목록 저장
            products.forEach(product -> product.setAuctionStatus(AuctionStatus.END));
            return productRepository.saveAll(products);
        } catch (DataAccessException e) {
            // 데이터베이스 관련 예외 처리
            throw new RuntimeException("Database error occurred while saving products status: " + e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RuntimeException("Unexpected error occurred while saving products status: " + e.getMessage(), e);
        }
    }
    @Transactional
    public ProductDto startWinnerProcess(String email, WinnerReqDto winnerReqDto) {
        //유저 조회
        Long userId = getUserIdFromRequest(email);
        // 상품 검색
        Product product = findProductById(winnerReqDto.getProductId());
        //10퍼 계산하는 코드
        // 맴버아이디가 판매자와 다른지, 이미 상품에 낙찰자가 존재하는지 유효성 검사
        if(userId.equals(product.getMemberId())) {
            throw new IllegalStateException("Seller can't start bid for product with ID: " + winnerReqDto.getProductId());
        }
        if (product.hasWinner()) {
            throw new RuntimeException("The product has already been won");
        }
        WinnerDto winnerDto = WinnerDto.builder().
                userId(userId).
                product(product).
                winnerReqDto(winnerReqDto).
                build();
        return convertToProductDto(createWinner(winnerDto));
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
    public Product progressWinnerAccepted(Long productId) {
        try {

            Product product = findProductById(productId);

            if (product == null) {
                throw new EntityNotFoundException("Product not found with ID: " + productId);
            }

            if (product.hasWinner()) {
                product.getWinner().setStatus(PurchaseStatus.ACCEPTED);
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
    public Product createWinner(WinnerDto winnerDto) {
        try {
            // Winner 객체를 생성하고 제품에 설정
            Winner winner = Winner.builder()
                    .memberId(winnerDto.getUserId())
                    .status(PurchaseStatus.CONFIRMED)
                    .price(winnerDto.getWinnerReqDto().getBuyPrice())
                    .winnerTime(winnerDto.getWinnerReqDto().getBuyTime())
                    .category(winnerDto.getWinnerReqDto().getCategory())
                    .build();
            winnerDto.getProduct().setWinner(winner);
            winnerDto.getProduct().setAuctionStatus(AuctionStatus.END);
            winnerDto.getProduct().setCurrentPrice(winnerDto.getWinnerReqDto().getBuyPrice());
            log.info("Winner created successfully for product ID: {}", winnerDto.getProduct().getId());

            // 제품을 저장
            Product savedProduct = productRepository.save(winnerDto.getProduct());


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
                .startPrice(product.getStartPrice())
                .currentPrice(product.getCurrentPrice())
                .immediatePrice(product.getImmediatePrice())
                .imgUrl(product.getImages())
                .memberId(product.getMemberId())
                .nickName(memberService.findMemberById(product.getMemberId()).getNickname())
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
                (product.getImages() != null && !product.getImages().isEmpty()) ? product.getImages().get(0) : null
        );
    }

    @Transactional
    public Page<ProductDto> searchProductsByName(String name, Pageable pageable) {
        return productRepository.searchByNameAndStatus(name, AuctionStatus.END, pageable)
                .map(this::convertToProductDto);
    }
    @Transactional
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findActiveProducts(AuctionStatus.END, pageable)
                .map(this::convertToProductDto);
    }

    /** 지영 - 계좌 내역 조회 시 필요**/
    @Transactional
    public String findProductNameById(Long productId) {
        return productRepository.findById(productId)
                .map(Product::getName)
                .orElse("N/A");
    }

    /** 지영 - 마이페이지 경매 등록 내역 조회 시 필요 **/

    // 판매 중 내역
    @Transactional
    public List<Product> getActiveProducts(Long memberId) {
        return productRepository.findActiveProductsByMemberId(memberId);
    }

    // 거래 완료 내역
    @Transactional
    public List<Product> getCompletedProducts(Long memberId) {
        // 결제 완료
        List<Product> confirmedProducts = productRepository.findConfirmedProductsByMemberId(memberId);
        // 구매 확정
        List<Product> acceptedProducts = productRepository.findAcceptedProductsByMemberId(memberId);

        // 두 리스트를 합치기
        List<Product> completedProducts = new ArrayList<>();
        completedProducts.addAll(confirmedProducts);
        completedProducts.addAll(acceptedProducts);

        return completedProducts;
    }

    // 유찰 내역
    @Transactional
    public List<Product> getFailedProducts(Long memberId) {
        return productRepository.findFailedProductsByMemberId(memberId);
    }


    /** 지영 - 마이페이지 경매 참여 내역 조회 시 필요 **/

    // 낙찰 내역
    @Transactional
    public List<Product> getWinningBids(Long memberId) {
        return productRepository.findWinningBids(memberId);
    }

    // 낙찰 시 결제 버튼
    @Transactional
    public boolean isWinnerConfirmed(Long productId, Long memberId) {
        // Product에서 Winner의 상태를 확인하는 로직
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // Product에 내장된 Winner의 memberId와 status를 확인
        return product.getWinner() != null &&
                product.getWinner().getMemberId().equals(memberId) &&
                product.getWinner().getStatus() == PurchaseStatus.CONFIRMED;
    }

    // 낙찰 시 결제 완료 버튼
    @Transactional
    public boolean isPaymentComplete(Long productId, Long memberId) {
        // Product에서 Winner의 상태를 확인하는 로직
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // Product에 내장된 Winner의 memberId와 status를 확인
        return product.getWinner() != null &&
                product.getWinner().getMemberId().equals(memberId) &&
                product.getWinner().getStatus() == PurchaseStatus.ACCEPTED;
    }

    @Transactional
    public void save(Product product) {
        productRepository.save(product);
    }
}
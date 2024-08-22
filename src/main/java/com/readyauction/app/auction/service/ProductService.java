package com.readyauction.app.auction.service;

import com.readyauction.app.auction.dto.*;
import com.readyauction.app.auction.entity.*;
import com.readyauction.app.auction.repository.BidRepository;
import com.readyauction.app.auction.repository.ProductRepository;

import com.readyauction.app.cash.entity.PaymentStatus;
import com.readyauction.app.cash.repository.PaymentRepository;
import com.readyauction.app.cash.service.PaymentService;
import com.readyauction.app.ncp.dto.FileDto;
import com.readyauction.app.ncp.service.NcpObjectStorageService;
import com.readyauction.app.user.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final BidRepository bidRepository;
    private final NcpObjectStorageService ncpObjectStorageService;
    private final MemberService memberService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final EmailService emailService;
    public List<Product> findAll(){
        return productRepository.findAll();
    }
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

    public String uploadFile(String email, MultipartFile multipartFile) {
        validateMultipartFile(multipartFile);
        List<FileDto> s3Files = ncpObjectStorageService.uploadFiles(Collections.singletonList(multipartFile), "productIMG/"+ email);

        return s3Files.stream()
                .findFirst()
                .map(FileDto::getUploadFileUrl)
                .orElseThrow(() -> new IllegalStateException("File upload failed"));
    }

    public ProductRepDto productDetail(Long productId) {
        Product product = findProductById(productId);
        return convertToProductRepDto(product);
    }

    public Integer updateBidPrice(Product product, Integer bidPrice) {
        try {
            product.setCurrentPrice(bidPrice + product.getBidUnit());
            return productRepository.save(product).getCurrentPrice();

        } catch (Exception e) {
            throw new RuntimeException("Update bid price failed", e);
        }
    }

    public Integer findCurrentPriceById(Long productId)
    {
        Product productResult = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        return productResult.getCurrentPrice();
    }

    public Optional<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }

    public boolean statusUpdate(HttpServletRequest request, Long productId, PurchaseStatus purchaseStatus) {
        Product product = findProductById(productId);
        product.getWinner().setStatus(purchaseStatus);
        productRepository.save(product);
        return true;
    }
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

    public ProductDto startWinnerProcess(String email, WinnerReqDto winnerReqDto) {
        Long userId = getUserIdFromRequest(email);
        Product product = findProductById(winnerReqDto.getProductId());

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

            EmailMessage emailMessage = EmailMessage.builder()
                    .to(memberService.findEmailById(winnerDto.getUserId()))
                    .subject("중고 스포츠 유니폼 판매 플랫폼 레디옥션입니다.")
                    .message("<html><head></head><body><div style=\"background-color: gray;\">"+winnerDto.getProduct().getName()  +" 경매에서 낙찰자로 선정되신 것을 축하드립니다. 결제를 위해 레디옥션 사이트를 방문 해주세요"+"<div></body></html>")
                    .build();
            emailService.sendMail(emailMessage);
            return savedProduct;
        } catch (DataAccessException e) {
            // 데이터베이스 관련 예외 처리
            throw new RuntimeException("Database error during saving the product with the winner: " + e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RuntimeException("Unexpected error occurred during creating the winner: " + e.getMessage(), e);
        }
    }


    public List<Product> createWinners(List<WinnerDto> winnerDtos) {
        try {
            System.out.println("입찰자 낙찰자로 진짜 변환!");
            // Winner 객체를 생성하고 제품에 설정
            List<Product> products = new ArrayList<>();
            for(WinnerDto winnerDto : winnerDtos){
                Winner winner = Winner.builder()
                        .memberId(winnerDto.getUserId())
                        .status(PurchaseStatus.CONFIRMED)
                        .price(winnerDto.getWinnerReqDto().getBuyPrice())
                        .winnerTime(winnerDto.getWinnerReqDto().getBuyTime())
                        .category(PurchaseCategory.BID)
                        .build();
                winnerDto.getProduct().setWinner(winner);
                winnerDto.getProduct().setAuctionStatus(AuctionStatus.END);

                log.info("Winner created successfully for product ID: {}", winnerDto.getProduct().getId());
                // 제품을 저장
                products.add(winnerDto.getProduct());
                AlarmDto alarmDto = AlarmDto.builder()
                        .productId(winnerDto.getProduct().getId())
                        .productName(winnerDto.getProduct().getName())
                        .payPrice(winnerDto.getProduct().getCurrentPrice())
                        .payTime(winnerDto.getWinnerReqDto().getBuyTime())
                        .build();
                simpMessagingTemplate.convertAndSendToUser(memberService.findById(winnerDto.getUserId()).getEmail(),"/alarm/sub", alarmDto);
                EmailMessage emailMessage = EmailMessage.builder()
                        .to(memberService.findEmailById(winnerDto.getUserId()))
                        .subject(winnerDto.getProduct().getName() + " 경매에서 낙찰자로 선정되었습니다. 웹사이트에 접속해서 결제를 진행해주세요!")
                        .message("안녕하세요,\n\n" + winnerDto.getProduct().getName() + " 경매에서 낙찰자로 선정되었습니다.\n\n 웹사이트에 접속해서 결제를 진행해주세요!\n\n감사합니다.") // 이메일 본문 추가
                        .build();
                emailService.sendMail(emailMessage);
                System.out.println("이메일 보내기 성공");
            }
            return productRepository.saveAll(products);
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


    public Page<ProductDto> searchProductsByName(String name, Pageable pageable) {
        return productRepository.searchByNameAndStatus(name, AuctionStatus.END, pageable)
                .map(this::convertToProductDto);
    }

    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findActiveProducts(AuctionStatus.END, pageable)
                .map(this::convertToProductDto);
    }

    /** 지영 - 계좌 내역 조회 시 필요**/
    public String findProductNameById(Long productId) {
        return productRepository.findById(productId)
                .map(Product::getName)
                .orElse("N/A");
    }

    /** 지영 - 마이페이지 경매 등록 내역 조회 시 필요 **/
    // 판매 중 (auctionStatus가 START 또는 PROGRESS)
    public List<Product> getActiveProducts(Long memberId) {
        return productRepository.findByMemberIdAndAuctionStatusIn(memberId, List.of(AuctionStatus.START, AuctionStatus.PROGRESS));
    }




    // 유찰 (auctionStatus가 END이고, 해당 상품에 대해 입찰 내역이 없는 경우)
    public List<Product> getFailedProducts(Long memberId) {
        List<Long> productIdsWithBids = bidRepository.findProductIdsWithBidsByMemberId(memberId);
        return productRepository.findByMemberIdAndAuctionStatusAndIdNotIn(memberId, AuctionStatus.END, productIdsWithBids);
    }

    public List<Product> findByIdIn(List<Long> productIds) {
        return productRepository.findByIdIn(productIds);
    }

    public void save(Product product) {
        productRepository.save(product);
    }
}
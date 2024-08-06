package com.readyauction.app.auction.service;


import com.readyauction.app.auction.dto.ProductDto;
import com.readyauction.app.auction.dto.ProductReqDto;
import com.readyauction.app.auction.entity.Product;
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
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final NcpObjectStorageService ncpObjectStorageService;
    private final MemberService memberService;
    @Transactional
    public ProductReqDto createProduct(HttpServletRequest request,ProductReqDto productReqDto) {
        // Create and save the Product entity
        Long userId = 0L;
        try {
         userId=memberService.findByEmail(request.getHeader("email")).getId();
        log.info("유저아이디 " + userId);

        }catch (Exception e) {
            log.error(e.getMessage());
        }
        finally {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println("Current Timestamp: " + timestamp);
            Product product = Product.builder()
                    .memberId(userId)
//                .memberId(1L)
                    .name(productReqDto.getName())
                    .category(productReqDto.getCategory())
                    .description(productReqDto.getDescription())
                    .bidUnit(productReqDto.getBidUnit())
                    .endTime(productReqDto.getEndTime())
                    .startTime(timestamp)
                    .currentPrice(productReqDto.getCurrentPrice())
                    .immediatePrice(productReqDto.getImmediatePrice())
                    .image(productReqDto.getImgUrl())
                    .build();
            productRepository.save(product);
            return productReqDto;

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
}

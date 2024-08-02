package com.readyauction.app.auction.service;


import com.readyauction.app.auction.dto.ProductReqDto;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

//    public ProductReqDto CreateProduct(ProductReqDto productReqDto) {
//        MultipartFile file = productReqDto.getImage();
//        String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
//        String fileName = System.currentTimeMillis() + fileExtension;
//        Path filePath = Paths.get(uploadDir + File.separator + fileName);
//        System.out.println("사진 저장");
//        try {
//            Files.createDirectories(filePath.getParent()); // 디렉토리가 없는 경우 생성
//            Files.write(filePath, file.getBytes());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Delete the partially uploaded file if there is a failure
//            try {
//                Files.deleteIfExists(filePath);// 사진 삭제
//                System.out.println("사진 삭제");
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//            throw new RuntimeException("Failed to insert product and save image", e);
//        }
//        finally {
//            String imagePath = filePath.toString(); // 저장된 파일 경로를 설정
//
//            ProductDto productDto = productRegistDto.toProductDto(fileName);
//            return productMapper.insertProduct(productDto);
//        }
//    }


}

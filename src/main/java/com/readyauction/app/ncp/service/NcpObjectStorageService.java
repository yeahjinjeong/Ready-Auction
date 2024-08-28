package com.readyauction.app.ncp.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.readyauction.app.ncp.dto.FileDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class NcpObjectStorageService {
    private static final Logger log = LoggerFactory.getLogger(NcpObjectStorageService.class);
    private final AmazonS3Client amazonS3Client;
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${spring.s3.bucket}")
    private String bucketName;

    public String getUuidFileName(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        return UUID.randomUUID().toString() + "." + ext;
    }

    // ncp에 파일 업로드
    public List<FileDto> uploadFiles(List<MultipartFile> multipartFiles, String filePath) {

        List<FileDto> s3files = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {

            String originalFileName = multipartFile.getOriginalFilename();
            String uploadFileName = getUuidFileName(originalFileName);
            String uploadFileUrl = "";
            String keyName = filePath + "/" + uploadFileName;

            // S3에 파일이 이미 있는지 검사
            if (amazonS3Client.doesObjectExist(bucketName, keyName)) {
                // 파일이 존재하면 url 가져오기
                uploadFileUrl = "https://kr.object.ncloudstorage.com/" + bucketName + "/" + keyName;
            } else {
                // 파일이 존재하지 않을 시 upload
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(multipartFile.getSize());
                objectMetadata.setContentType(multipartFile.getContentType());

                try (InputStream inputStream = multipartFile.getInputStream()) {

                    // s3에 파일 upload
                    amazonS3Client.putObject(
                            new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata)
                                    .withCannedAcl(CannedAccessControlList.PublicRead));

                    // 업로드한 파일 url 설정
                    uploadFileUrl = "https://kr.object.ncloudstorage.com/" + bucketName + "/" + keyName;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 리스트에 파일 정보 추가
            s3files.add(
                    FileDto.builder()
                            .originalFileName(originalFileName)
                            .uploadFileName(uploadFileName)
                            .uploadFilePath(filePath)
                            .uploadFileUrl(uploadFileUrl)
                            .build());
        }

        return s3files;
    }

    // ncp에 auction 파일 업로드
    public List<FileDto> uploadAuctionFiles(List<MultipartFile> multipartFiles, String filePath) {

        List<FileDto> s3files = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {

            String originalFileName = multipartFile.getOriginalFilename();
            String uploadFileName = getUuidFileName(originalFileName);
            String uploadFileUrl = "";
            String keyName = filePath + "/" + uploadFileName;

            // S3에 파일이 이미 있는지 검사
            if (amazonS3Client.doesObjectExist(bucketName, keyName)) {
                // 파일이 존재하면 url 가져오기
                uploadFileUrl = "https://kr.object.ncloudstorage.com/" + bucketName + "/" + keyName;
            } else {
                // 파일이 존재하지 않을 시 upload
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(multipartFile.getSize());
                objectMetadata.setContentType(multipartFile.getContentType());

                try (InputStream inputStream = multipartFile.getInputStream()) {

                    // s3에 파일 upload
                    amazonS3Client.putObject(
                            new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata)
                                    .withCannedAcl(CannedAccessControlList.PublicRead));

                    // 업로드한 파일 url 설정
                    uploadFileUrl = "https://kr.object.ncloudstorage.com/" + bucketName + "/" + keyName;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 리스트에 파일 정보 추가
            s3files.add(
                    FileDto.builder()
                            .originalFileName(originalFileName)
                            .uploadFileName(uploadFileName)
                            .uploadFilePath(filePath)
                            .uploadFileUrl(uploadFileUrl)
                            .build());
            String pid = ManagementFactory.getRuntimeMXBean().getName();

            String key =  "Auction:ImageId:" + uploadFileUrl;
            redisTemplate.opsForValue().set(key,pid);
            redisTemplate.expire(key,5, TimeUnit.MINUTES);

            log.info("Auction for Image ID: " + uploadFileUrl);

        }

        return s3files;
    }

    // ncp에 업로드한 파일 삭제
    public void deleteFile(String key) {
        try {
            amazonS3Client.deleteObject(bucketName, key);
            System.out.println("파일이 성공적으로 삭제되었습니다.");
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            System.out.println("파일 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}

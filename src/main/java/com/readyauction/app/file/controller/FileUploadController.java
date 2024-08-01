package com.readyauction.app.file.controller;

import com.readyauction.app.file.model.service.NcpObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FileUploadController {
    private final NcpObjectStorageService ncpObjectStorageService;

    // 파일 업로드
    @GetMapping("/upload")
    public String getUpload(){
        return "upload";
    }

    // 파일 업로드
    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFilesSample(
            @RequestPart(value = "files") List<MultipartFile> multipartFiles) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ncpObjectStorageService.uploadFilesSample(multipartFiles));
    }
}

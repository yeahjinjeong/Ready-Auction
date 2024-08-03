package com.readyauction.app.file.controller;

import com.readyauction.app.file.model.dto.FileDto;
import com.readyauction.app.file.model.service.NcpObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    // 파일 불러오기
    @GetMapping("/list")
    public String listFiles(Model model) {
        // 가져올 파일의 폴더명 설정
        List<FileDto> files = ncpObjectStorageService.listFiles("sample-folder");
        System.out.println(files);
        model.addAttribute("files", files);
        return "list";
    }
}

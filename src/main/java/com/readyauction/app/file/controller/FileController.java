package com.readyauction.app.file.controller;

import com.readyauction.app.file.model.dto.FileDto;
import com.readyauction.app.file.model.service.NcpObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {
    private final NcpObjectStorageService ncpObjectStorageService;

    // 파일 업로드
    @GetMapping("/upload")
    public String getUpload(){
        return "file/upload";
    }

    // 파일 업로드
    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFilesSample(
            @RequestPart(value = "files") List<MultipartFile> multipartFiles) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ncpObjectStorageService.uploadFilesSample(multipartFiles));
    }

    // 전체 파일 불러오기
    @GetMapping("/list")
    public String listFiles(Model model) {
        // 가져올 파일의 폴더명 직접 설정 필요
        List<FileDto> files = ncpObjectStorageService.listFiles("sample-folder");
        System.out.println(files);
        model.addAttribute("files", files);
        return "file/list";
    }

    // 파일 삭제
    @PostMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam String filePath, @RequestParam String fileName) {
        ncpObjectStorageService.deleteFile(filePath, fileName);
        return new ResponseEntity<>("File Delete Success", HttpStatus.OK);
    }
}

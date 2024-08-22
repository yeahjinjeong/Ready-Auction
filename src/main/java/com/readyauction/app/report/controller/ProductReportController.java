package com.readyauction.app.report.controller;

import com.readyauction.app.report.dto.ProductReportDto;
import com.readyauction.app.report.entity.ProductReport;
import com.readyauction.app.report.repository.ProductReportRepository;
import com.readyauction.app.report.service.ProductReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/report")
@Slf4j
public class ProductReportController {

    @Autowired
    private ProductReportService productReportService;

    @PostMapping("/save")
    public ResponseEntity<?> report(@RequestBody ProductReportDto productReportDto) {
        log.info("productReportDto: {}", productReportDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 신고 데이터를 데이터베이스에 저장
        ProductReport savedReport = productReportService.saveReport(email,productReportDto);

        return ResponseEntity.ok().body("신고가 성공적으로 접수되었습니다. ID: " + savedReport.getId());
    }
}
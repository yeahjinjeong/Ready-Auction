package com.readyauction.app.report.controller;

import com.readyauction.app.report.dto.ProductReportReqDto;
import com.readyauction.app.report.entity.ProductReport;
import com.readyauction.app.report.service.ProductReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@Slf4j
@RequiredArgsConstructor
public class ProductReportController {

    private final ProductReportService productReportService;

    @PostMapping("/save")
    public ResponseEntity<?> report(@RequestBody ProductReportReqDto productReportReqDto) {
        log.info("productReportDto: {}", productReportReqDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 신고 데이터를 데이터베이스에 저장
        ProductReport savedReport = productReportService.saveReport(email, productReportReqDto);

        return ResponseEntity.ok().body("신고가 성공적으로 접수되었습니다. ID: " + savedReport.getId());
    }
}
package com.readyauction.app.report.controller;

import com.readyauction.app.report.dto.ChatReportResDto;
import com.readyauction.app.report.entity.ProductReport;
import com.readyauction.app.report.service.ChatReportService;
import com.readyauction.app.report.service.ProductReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/report")
public class AdminReportController {

    private final ChatReportService chatReportService;
    private final ProductReportService productReportService;

    @GetMapping("/list")
    public String findReport(Model model) {
        List<ProductReport> productReports = productReportService.findAllProductReports();
        List<ChatReportResDto> chatReportResDtos = chatReportService.findAll();
        model.addAttribute("productReports", productReports);
        model.addAttribute("chatReports", chatReportResDtos);
        return "report/admin-list";
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<?> deleteReport(@PathVariable Long productId) {
        productReportService.deleteReportById(productId);
        return ResponseEntity.ok().body("삭제 되었습니다.");
    }

}

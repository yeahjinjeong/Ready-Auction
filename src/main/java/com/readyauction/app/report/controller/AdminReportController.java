package com.readyauction.app.report.controller;

import com.readyauction.app.auction.entity.PurchaseStatus;
import com.readyauction.app.auction.service.ProductService;
import com.readyauction.app.report.dto.ChatReportResDto;
import com.readyauction.app.report.entity.ProductReport;
import com.readyauction.app.report.service.ChatReportService;
import com.readyauction.app.report.service.ProductReportService;
import com.readyauction.app.user.entity.UserStatus;
import com.readyauction.app.user.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/report")
public class AdminReportController {

    private final ChatReportService chatReportService;
    private final ProductReportService productReportService;
    private final ProductService productService;

    @GetMapping("/list")
    public String findReport(Model model) {
        List<ProductReport> productReports = productReportService.findAllProductReports();
        List<ChatReportResDto> chatReportResDtos = chatReportService.findAll();
        model.addAttribute("productReports", productReports);
        model.addAttribute("chatReports", chatReportResDtos);
        return "report/admin-list";
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> processProductReport(@PathVariable Long productId) {
        // 경매글 상태 END로 바꾸기
        // 롤백하기 (환불)
        // 입찰자 지우기
        return ResponseEntity.ok().body("삭제 되었습니다.");
    }

    @GetMapping("/chat/{reportId}")
    public String processChatReport(@PathVariable Long reportId,
                                  RedirectAttributes redirectAttributes) {
        log.info("reportId : {}", reportId);
        // 신고대상 패널티만 주는 걸로
        String message = chatReportService.processChatReport(reportId);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/admin/report/list";
    }

}

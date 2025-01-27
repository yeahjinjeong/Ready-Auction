package com.readyauction.app.report.controller;

import com.readyauction.app.auth.principal.AuthPrincipal;
import com.readyauction.app.report.dto.ChatReportReqDto;
import com.readyauction.app.report.entity.ReportReason;
import com.readyauction.app.report.service.MannerReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/report/chat")
public class ChatReportController {
    private final MannerReportService mannerReportService;

    @GetMapping("/{productId}/{reportedMemberId}")
    public String chatReportForm(
            @PathVariable("productId") Long productId,
            @PathVariable("reportedMemberId") Long reportedMemberId,
            Model model
    ) {
        log.info("productId : {}", productId);
        model.addAttribute("reportedMemberId", reportedMemberId);
        model.addAttribute("productId", productId);
        model.addAttribute("reportReasons", ReportReason.values());  // Enum 값을 뷰에 전달

        // 디버그용 로그 출력
        log.info("Report Reasons: {}", (Object) ReportReason.values());

        return "report/chat-form";
    }

    @PostMapping("/submit")
    public String submitChatReport(
            @ModelAttribute ChatReportReqDto chatReportReqDto,
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        log.info("chatReportDto : {}", chatReportReqDto);
        chatReportReqDto.setReporterId(principal.getUser().getId());
        mannerReportService.reportChat(chatReportReqDto);
        return "redirect:/chat/list";
    }
}

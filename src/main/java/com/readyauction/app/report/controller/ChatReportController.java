package com.readyauction.app.report.controller;

import com.readyauction.app.auth.principal.AuthPrincipal;
import com.readyauction.app.report.dto.ChatReportDto;
import com.readyauction.app.report.entity.ReportReason;
import com.readyauction.app.report.service.ReportService;
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
    private final ReportService reportService;

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

        return "report/chat-report-form";
    }

    @PostMapping("/submit")
    public String submitChatReport(
            @ModelAttribute ChatReportDto chatReportDto,
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        log.info("chatReportDto : {}", chatReportDto);
        chatReportDto.setReporterId(principal.getMember().getId());
        reportService.reportChat(chatReportDto);
        return "redirect:/chat/list";
    }
}

//    @GetMapping("/report/chat/{reportedMemberId}/{chatRoomId}")
//    public String reportForm(@AuthenticationPrincipal AuthPrincipal principal, @PathVariable Long reportedMemberId, @PathVariable Long chatRoomId, Model model) {
//        model.addAttribute("reportedMemberId", reportedMemberId);
//        model.addAttribute("chatRoomId", chatRoomId);
//        model.addAttribute("reporterId", principal.getMember().getId()); // 로그인한 사용자의 ID 추가
//        model.addAttribute("chatReportDto", new ChatReportDto());  // 빈 DTO 객체 추가
//        return "report/report-form";
//    }

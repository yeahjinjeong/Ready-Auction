package com.readyauction.app.report.controller;

import com.readyauction.app.auth.principal.AuthPrincipal;
import com.readyauction.app.report.dto.ChatReportDto;
import com.readyauction.app.report.dto.MannerReportDto;
import com.readyauction.app.report.entity.Dislike;
import com.readyauction.app.report.entity.Like;
import com.readyauction.app.report.entity.ReportReason;
import com.readyauction.app.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/report/manner/{memberId}")
    public String mannerFrm(
            @PathVariable Long memberId,
            Model model
    ) {
        model.addAttribute("memberId", memberId);
        return "report/manner-form";
    }

    @PostMapping("/report/manner/register")
    public String register(
//            @RequestParam Map<String, String> allParams,
//            @RequestParam("likes") List<Like> likes,
            @ModelAttribute MannerReportDto mannerReportDto,
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
//        log.info("allParams : {}", allParams);
        log.info("mannerReportDto : {}", mannerReportDto);
//        log.info("likes : {}", likes);
        // 매너폼을 저장하기
        // + 매너스코어 반영하기 => 멤버 엔티티 조회 후 change 하기~!
        reportService.applyMannerScore(principal.getMember().getId(), mannerReportDto);
        return "redirect:/chat/list";
    }

    @GetMapping("/report/chat/{productId}/{reportedMemberId}")
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

    @PostMapping("/report/chat/submit")
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

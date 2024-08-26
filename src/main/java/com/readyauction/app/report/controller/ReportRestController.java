//package com.readyauction.app.report.controller;
//
//import com.readyauction.app.report.dto.ChatReportDto;
//import com.readyauction.app.report.service.ReportService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//@RestController
//@RequiredArgsConstructor
//@Slf4j
//@RequestMapping("/report")
//public class ReportRestController {
//
//    private final ReportService reportService;
//
//    @PostMapping("/report/submit")
//    public String submitReport(@Valid @ModelAttribute ChatReportDto reportDto, BindingResult result, RedirectAttributes redirectAttributes) {
//        if (result.hasErrors()) {
//            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.chatReportDto", result);
//            redirectAttributes.addFlashAttribute("chatReportDto", reportDto);
//            return "redirect:/report/chat/" + reportDto.getReportedMemberId() + "/" + reportDto.getChatRoomId();
//        }
//        reportService.saveReport(reportDto);
//        redirectAttributes.addFlashAttribute("message", "신고가 성공적으로 접수되었습니다.");
//        return "redirect:/chat/list";
//    }
//
//
//}

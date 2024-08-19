package com.readyauction.app.report.controller;

import com.readyauction.app.report.dto.MannerReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReportController {
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
//            @ModelAttribute MannerReportDto mannerReportDto,
            @RequestParam Map<String, String> allParams
            ) {
//        log.info("mannerReport : {}", mannerReportDto);
        log.info("allParams : {}", allParams);
        return "redirect:chat/list";
    }
}

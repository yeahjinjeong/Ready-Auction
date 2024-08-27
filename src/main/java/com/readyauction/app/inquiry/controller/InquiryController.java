package com.readyauction.app.inquiry.controller;

import com.readyauction.app.inquiry.dto.InquiryDetailDto;
import com.readyauction.app.inquiry.dto.InquiryDto;
import com.readyauction.app.inquiry.service.InquiryService;
import com.readyauction.app.report.entity.ProductReport;
import com.readyauction.app.report.service.ProductReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/inquiry")
@Slf4j
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;

    @Autowired
    ProductReportService productReportService;

    @GetMapping("/faq")
    public String inquiry() {
        return "inquiry/faq";
    }

    @GetMapping("/admin-faq")
    public String adminInquiry(Model model) {
        List<InquiryDto> inquiryDtos = inquiryService.findAll();
        model.addAttribute("inquiryList", inquiryDtos);
        log.info("inquiryDtos : {}", inquiryDtos);
        return "inquiry/admin-faq";
    }

    @GetMapping("/detail/{id}")
    public String adminInquiryDetail(
            @PathVariable Long id,
            Model model) {
        InquiryDetailDto inquiryDetailDto = inquiryService.findAndNicknameById(id);
        model.addAttribute("inquiry", inquiryDetailDto);
        log.info("inquiryDetailDto : {}", inquiryDetailDto);
        return "inquiry/admin-faq-detail";
    }

    @GetMapping("/admin-report")
    public String adminReport(Model model) {

        List<ProductReport> list = productReportService.getAllProduct();

        model.addAttribute("productList", list);

        return "inquiry/admin-report";

    }
}

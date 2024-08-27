package com.readyauction.app.inquiry.controller;


import com.readyauction.app.inquiry.service.AdminReportService;
import com.readyauction.app.report.dto.ProductReportDto;
import com.readyauction.app.report.entity.ProductReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RequestMapping("/adminProductReport")
@Slf4j
@RequiredArgsConstructor
public class AdminReportController {

    public final AdminReportService adminReportService;


    public String getProductList(Model model) {

        List<ProductReport> list = adminReportService.getProductList();
        model.addAttribute("productList", list);


        return "inquiry/admin-report";
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<?> deleteReport(@PathVariable Long productId) {
        adminReportService.deleteReportById(productId);
        return ResponseEntity.ok().body("삭제 되었습니다.");
    }



}

package com.readyauction.app.report.service;

import com.readyauction.app.report.dto.ProductReportReqDto;
import com.readyauction.app.report.entity.ProductReport;
import com.readyauction.app.report.repository.ProductReportRepository;
import com.readyauction.app.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductReportService {
    private final ProductReportRepository productReportRepository;
    private final  MemberService memberService;
    public ProductReport saveReport(String email, ProductReportReqDto productReportReqDto) {
        // 엔티티 저장
        ProductReport report = productReportReqDto.toEntity();
        report.setUserId(memberService.findByEmail(email).getId());
        return productReportRepository.save(report);
    }

    public List<ProductReport> findAllProductReports(){
        List<ProductReport> productReports = productReportRepository.findAll();
        return productReports;
    }

    public void deleteReportById(Long id) {
        productReportRepository.deleteById(id);
    }
}

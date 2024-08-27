package com.readyauction.app.inquiry.service;

import com.readyauction.app.inquiry.repository.AdminReportRepository;
import com.readyauction.app.report.entity.ProductReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminReportService {

    @Autowired
    AdminReportRepository adminReportRepository;

    public List<ProductReport> getProductList () {

        return adminReportRepository.findAll();
    }


    public void deleteReportById(Long id) {

        adminReportRepository.deleteById(id);
    }
}

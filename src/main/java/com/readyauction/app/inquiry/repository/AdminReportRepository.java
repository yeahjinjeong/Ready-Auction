package com.readyauction.app.inquiry.repository;

import com.readyauction.app.report.entity.ProductReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminReportRepository extends JpaRepository<ProductReport, Long> {
}

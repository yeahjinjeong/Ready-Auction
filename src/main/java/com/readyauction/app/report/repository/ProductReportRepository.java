package com.readyauction.app.report.repository;

import com.readyauction.app.report.entity.ProductReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductReportRepository extends JpaRepository<ProductReport,Long > {

}

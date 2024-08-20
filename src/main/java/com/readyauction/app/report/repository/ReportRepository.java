package com.readyauction.app.report.repository;

import com.readyauction.app.report.entity.MannerReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ReportRepository extends JpaRepository<MannerReport, Long> {

}

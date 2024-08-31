package com.readyauction.app.report.repository;

import com.readyauction.app.report.entity.MannerReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MannerReportRepository extends JpaRepository<MannerReport, Long> {

    List<MannerReport> findByMemberId(Long memberId);
}

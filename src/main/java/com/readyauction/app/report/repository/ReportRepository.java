package com.readyauction.app.report.repository;

import com.readyauction.app.report.entity.MannerReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<MannerReport, Long> {

    List<MannerReport> findByMemberId(Long memberId);
}

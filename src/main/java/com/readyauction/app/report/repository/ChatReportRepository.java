package com.readyauction.app.report.repository;

import com.readyauction.app.report.entity.ChatReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatReportRepository extends JpaRepository<ChatReport, Long> {
}

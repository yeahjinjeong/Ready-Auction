package com.readyauction.app.report.service;

import com.readyauction.app.chat.dto.MessageDto;
import com.readyauction.app.report.dto.ChatReportResDto;
import com.readyauction.app.report.entity.ChatReport;
import com.readyauction.app.report.entity.ProductReport;
import com.readyauction.app.report.entity.ReportStatus;
import com.readyauction.app.report.repository.ChatReportRepository;
import com.readyauction.app.user.entity.UserStatus;
import com.readyauction.app.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatReportService {
    private final ChatReportRepository chatReportRepository;
    private final MemberService memberService;

    public List<ChatReportResDto> findAll() {
        List<ChatReport> chatReports = chatReportRepository.findAll();
        return chatReports.stream().map(ChatReportResDto::toChatReportResDto).toList();
    }

    public String processChatReport(Long reportId) {
        Optional<ChatReport> chatReport = chatReportRepository.findById(reportId);
        memberService.changeStatus(chatReport.get().getReportedMemberId(), UserStatus.suspended);
        chatReport.get().changeStatus(ReportStatus.COMPLETED);
        return "활동정지되었습니다.";
    }
}

package com.readyauction.app.report.service;

import com.readyauction.app.chat.dto.MessageDto;
import com.readyauction.app.report.dto.ChatReportResDto;
import com.readyauction.app.report.entity.ChatReport;
import com.readyauction.app.report.entity.ProductReport;
import com.readyauction.app.report.repository.ChatReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatReportService {
    private final ChatReportRepository chatReportRepository;

    public List<ChatReportResDto> findAll() {
        List<ChatReport> chatReports = chatReportRepository.findAll();
        return chatReports.stream().map(ChatReportResDto::toChatReportResDto).toList();
    }
}

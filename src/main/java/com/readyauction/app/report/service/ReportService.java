package com.readyauction.app.report.service;

//import com.readyauction.app.report.dto.ChatReportDto;
import com.readyauction.app.report.dto.ChatReportDto;
import com.readyauction.app.report.dto.MannerReportDto;
//import com.readyauction.app.report.entity.ChatReport;
import com.readyauction.app.report.entity.ChatReport;
import com.readyauction.app.report.entity.ChatReportStatus;
import com.readyauction.app.report.entity.MannerReport;
//import com.readyauction.app.report.repository.ChatReportRepository;
import com.readyauction.app.report.repository.ChatReportRepository;
import com.readyauction.app.report.repository.ReportRepository;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {
    public final ReportRepository reportRepository;
    public final MemberRepository memberRepository;
    private final ChatReportRepository chatReportRepository;


    public void applyMannerScore(Long id, MannerReportDto mannerReportDto) {
        MannerReport mannerReport = mannerReportDto.toMannerReportEntity(id);
        reportRepository.save(mannerReport);
        Member member = memberRepository.findById(mannerReportDto.getMemberId()).get();
        if (mannerReportDto.getIsLiked().equals("true")) {
            member.increaseMannerScore();
        } else {
            member.decreaseMannerScore();
        }
    }

    public void reportChat(ChatReportDto chatReportDto) {
        ChatReport chatReport = ChatReport.builder()
                .reporterId(chatReportDto.getReporterId())
                .reportedMemberId(chatReportDto.getReportedMemberId())
                .reason(chatReportDto.getReason())
                .productId(chatReportDto.getProductId())
                .createdAt(LocalDateTime.now())
                .detail(chatReportDto.getDetail())
                .status(ChatReportStatus.PENDING)
                .build();

        chatReportRepository.save(chatReport);
    }

}

//    private final ChatReportRepository chatReportRepository;

//    public void saveReport(ChatReportDto reportDto) {
//        ChatReport chatReport = ChatReport.builder()
//                .reporterId(reportDto.getReporterId())
//                .reportedMemberId(reportDto.getReportedMemberId())
//                .reason(reportDto.getReason())
//                .chatRoomId(reportDto.getChatRoomId())
//                .build();
//
//        chatReportRepository.save(chatReport);
//    }


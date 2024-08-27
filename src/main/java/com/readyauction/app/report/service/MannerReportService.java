package com.readyauction.app.report.service;

//import com.readyauction.app.report.dto.ChatReportDto;
import com.readyauction.app.report.dto.ChatReportReqDto;
import com.readyauction.app.report.dto.MannerReportReqDto;
//import com.readyauction.app.report.entity.ChatReport;
import com.readyauction.app.report.entity.ChatReport;
import com.readyauction.app.report.entity.ReportStatus;
import com.readyauction.app.report.entity.MannerReport;
//import com.readyauction.app.report.repository.ChatReportRepository;
import com.readyauction.app.report.repository.ChatReportRepository;
import com.readyauction.app.report.repository.MannerReportRepository;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class MannerReportService {
    public final MannerReportRepository mannerReportRepository;
    public final MemberRepository memberRepository;
    private final ChatReportRepository chatReportRepository;


    public void applyMannerScore(Long id, MannerReportReqDto mannerReportReqDto) {
        MannerReport mannerReport = mannerReportReqDto.toMannerReportEntity(id);
        mannerReportRepository.save(mannerReport);
        Member member = memberRepository.findById(mannerReportReqDto.getMemberId()).get();
        if (mannerReportReqDto.getIsLiked().equals("true")) {
            member.increaseMannerScore();
        } else {
            member.decreaseMannerScore();
        }
    }

    public void reportChat(ChatReportReqDto chatReportReqDto) {
        ChatReport chatReport = ChatReport.builder()
                .reporterId(chatReportReqDto.getReporterId())
                .reportedMemberId(chatReportReqDto.getReportedMemberId())
                .reason(chatReportReqDto.getReason())
                .productId(chatReportReqDto.getProductId())
                .createdAt(LocalDateTime.now())
                .detail(chatReportReqDto.getDetail())
                .status(ReportStatus.PENDING)
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


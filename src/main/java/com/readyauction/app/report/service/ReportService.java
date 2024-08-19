package com.readyauction.app.report.service;

import com.readyauction.app.report.dto.MannerReportDto;
import com.readyauction.app.report.entity.MannerReport;
import com.readyauction.app.report.repository.ReportRepository;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {
    public final ReportRepository reportRepository;
    public final MemberRepository memberRepository;
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
}

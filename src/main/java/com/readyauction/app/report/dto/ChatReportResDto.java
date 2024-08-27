package com.readyauction.app.report.dto;

import com.readyauction.app.report.entity.ChatReport;
import com.readyauction.app.report.entity.ReportStatus;
import com.readyauction.app.report.entity.ReportReason;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatReportResDto {
    private Long id;
    private Long reporterId;
    private Long reportedMemberId;
    private Long productId;
    private ReportReason reason;
    private String detail;
    private LocalDateTime createdAt;
    private ReportStatus status;

    public static ChatReportResDto toChatReportResDto(ChatReport chatReport) {
        return new ChatReportResDto(
                chatReport.getId(),
                chatReport.getReporterId(),
                chatReport.getReportedMemberId(),
                chatReport.getProductId(),
                chatReport.getReason(),
                chatReport.getDetail(),
                chatReport.getCreatedAt(),
                chatReport.getStatus()
        );
    }
}

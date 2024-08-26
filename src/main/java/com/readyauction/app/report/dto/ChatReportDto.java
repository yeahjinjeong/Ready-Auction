package com.readyauction.app.report.dto;

import com.readyauction.app.report.entity.ReportReason;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatReportDto {
    private Long reporterId;
    private Long reportedMemberId;
    private Long productId;
    private ReportReason reason;
    private String detail;
}

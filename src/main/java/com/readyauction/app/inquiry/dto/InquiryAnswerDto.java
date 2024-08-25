package com.readyauction.app.inquiry.dto;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
//@Setter(AccessLevel.PRIVATE)
public class InquiryAnswerDto {
    private Long inquiryId;
    private String content;
    private LocalDateTime answeredAt;
}

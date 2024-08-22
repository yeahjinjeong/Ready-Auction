package com.readyauction.app.inquiry.dto;

import com.readyauction.app.inquiry.entity.Inquiry;
import com.readyauction.app.inquiry.entity.InquiryCategory;
import com.readyauction.app.inquiry.entity.InquiryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDto {
    private Long id;
    private InquiryCategory category;
    private String title;
    private String author;
    private Timestamp date;
    private InquiryStatus status;

    public static InquiryDto toInquiryDto(Inquiry inquiry, String nickname) {
        return new InquiryDto(
                inquiry.getId(),
                inquiry.getCategory(),
                inquiry.getTitle(),
                nickname,
                inquiry.getCreatedAt(),
                inquiry.getStatus()
        );
    }
}

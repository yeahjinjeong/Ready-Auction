package com.readyauction.app.inquiry.entity;

import lombok.Getter;

@Getter
public enum InquiryStatus {
    PENDING("답변대기"),
    COMPLETE("답변완료");

    private final String description;

    InquiryStatus(String description) {
        this.description = description;
    }

}
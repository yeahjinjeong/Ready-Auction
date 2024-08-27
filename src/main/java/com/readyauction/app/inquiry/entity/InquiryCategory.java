package com.readyauction.app.inquiry.entity;

import lombok.Getter;

@Getter
public enum InquiryCategory {
    CASH("결제 관련 문의"),
    AUCTION("경매 관련 문의"),
    SYSTEM("시스템 오류 제보");

    private final String description;

    InquiryCategory(String description) {
        this.description = description;
    }

}

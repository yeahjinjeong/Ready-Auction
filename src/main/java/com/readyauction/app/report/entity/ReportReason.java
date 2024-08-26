package com.readyauction.app.report.entity;

public enum ReportReason {
    비매너사용자("비매너 사용자에요"),
    전문판매업자("전문 판매업자에요"),
    거래중분쟁("거래 중 분쟁이 발생했어요"),
    사기("사기인 것 같아요"),
    욕설비방혐오표현("욕설, 비방, 혐오 표현을 해요"),
    연애목적대화("연애 목적의 대화를 시도해요"),
    성적행위("성적 행위를 해요"),
    기타("기타");

    private final String description;

    ReportReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

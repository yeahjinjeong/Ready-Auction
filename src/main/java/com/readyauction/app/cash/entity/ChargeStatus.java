package com.readyauction.app.cash.entity;

public enum ChargeStatus {
    ROLLBACK, // 환불
    FAILED, // 충전 실패
    SUCCESS // 충전 완료
}
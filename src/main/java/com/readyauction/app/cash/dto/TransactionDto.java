package com.readyauction.app.cash.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionDto {
    private Timestamp date;
    private String type; // 캐시, 판매, 구매, 선입금
    private Integer amount;
    private String status;
    private String productName;
    private Long productId;

    // 캐시 조회
    public TransactionDto(Timestamp date, String type, Integer amount, String status) {
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.status = status;
    }
}

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
    private String type; // CASH, PAYMENT_SENT, PAYMENT_RECEIVED
    private Integer amount;
    private String status;
    private String productName;
}

package com.readyauction.app.cash.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReqDto {
    private Long productId;
    private Timestamp payTime;
    private Integer amount;
}

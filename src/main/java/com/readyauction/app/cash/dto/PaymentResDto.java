package com.readyauction.app.cash.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResDto {
    private Long productId;
    private Timestamp payTime;
    private Integer amount;
}

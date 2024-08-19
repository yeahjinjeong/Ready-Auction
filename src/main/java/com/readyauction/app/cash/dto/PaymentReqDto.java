package com.readyauction.app.cash.dto;


import com.readyauction.app.cash.entity.PaymentCategory;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentReqDto {
    private Long productId;
    private Timestamp payTime;
    private Integer amount;

    @Enumerated(EnumType.STRING)
    private PaymentCategory category;
}

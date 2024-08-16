package com.readyauction.app.cash.dto;

import com.readyauction.app.cash.entity.Account;
import com.readyauction.app.cash.entity.Charge;
import com.readyauction.app.cash.entity.ChargeStatus;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChargeDto {
    private Long id;
    private Long memberId;
    private Account account;
    private Integer amount;
    private Timestamp date;
    private ChargeStatus status;

    public ChargeDto(Charge charge) {
        this.id = charge.getId();
        this.memberId = charge.getMemberId();
        this.account = charge.getAccount();
        this.amount = charge.getAmount();
        this.date = charge.getDate();
        this.status = charge.getStatus();
    }
}

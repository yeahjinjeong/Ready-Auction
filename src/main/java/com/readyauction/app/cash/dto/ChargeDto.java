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
    private Integer chargeAmount;
    private Timestamp date;
    private ChargeStatus chargeStatus;

    public ChargeDto(Charge charge) {
        this.id = charge.getId();
        this.memberId = charge.getMemberId();
        this.account = charge.getAccount();
        this.chargeAmount = charge.getChargeAmount();
        this.date = charge.getDate();
        this.chargeStatus = charge.getChargeStatus();
    }
}

package com.readyauction.app.cash.dto;

import com.readyauction.app.cash.entity.Account;
import com.readyauction.app.cash.entity.Cash;
import com.readyauction.app.cash.entity.CashStatus;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CashDto {
    private Long id;
    private Long memberId;
    private Account account;
    private Integer amount;
    private Timestamp date;
    private CashStatus status;

    public CashDto(Cash cash) {
        this.id = cash.getId();
        this.memberId = cash.getMemberId();
        this.account = cash.getAccount();
        this.amount = cash.getAmount();
        this.date = cash.getDate();
        this.status = cash.getStatus();
    }
}

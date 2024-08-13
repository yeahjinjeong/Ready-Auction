package com.readyauction.app.cash.dto;

import com.readyauction.app.cash.entity.Account;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountDto {
    private Long id;
    private Long memberId;
    private Integer cash;

    public AccountDto(Account account) {
        this.id = account.getId();
        this.memberId = account.getMemberId();
        this.cash = account.getCash();
    }
}

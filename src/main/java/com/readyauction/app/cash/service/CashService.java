package com.readyauction.app.cash.service;

import com.readyauction.app.cash.dto.CashDto;
import com.readyauction.app.cash.entity.Account;
import com.readyauction.app.cash.entity.Cash;
import com.readyauction.app.cash.entity.CashStatus;
import com.readyauction.app.cash.repository.CashRepository;
import com.readyauction.app.common.handler.CashNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;

@Service
@Transactional
@RequiredArgsConstructor
public class CashService {

    private final AccountService accountService;
    private final CashRepository cashRepository;

    public CashDto findCashDtoByMemberId(Long memberId) {
        Cash cash = cashRepository.findCashByMemberId(memberId)
                .orElseThrow(() -> new CashNotFoundException("Cash not found for memberId: " + memberId));
        return new CashDto(cash);
    }

    public Cash save(Cash cash) {
        return cashRepository.save(cash);
    }

    // 캐시 충전
    public void chargeCash(Long memberId, Cash cash) {
        // 현재 사용자의 Account 가져오기
        Account account = accountService.findByMemberId(memberId);

        // Update Account cash
        System.out.println("잔액 : " + account.getCash());
        System.out.println("충전 금액 : " + cash.getAmount());
        account.setCash(account.getCash() + cash.getAmount());
        accountService.save(account);
        System.out.println("총 금액 : " + account.getCash());

        // Insert Cash
        cash.setMemberId(memberId);
        cash.setAccount(account);
        cash.setAmount(cash.getAmount()); // 충전 금액은 양수
        cash.setDate(new Timestamp(System.currentTimeMillis()));
        cash.setStatus(CashStatus.CHARGE); // 충전 상태 설정
        save(cash);
    }

    // 캐시 출금
    public void withdrawCash(Long memberId, Cash cash, @RequestParam("withdrawal") Integer withdrawal) {
        // 현재 사용자의 Account 가져오기
        Account account = accountService.findByMemberId(memberId);

        // Update Account cash
        System.out.println("잔액 : " + account.getCash());
        System.out.println("출금 금액 : " + withdrawal);
        account.setCash(account.getCash() - withdrawal);
        accountService.save(account);
        System.out.println("총 금액 : " + account.getCash());

        // Insert Cash
        cash.setMemberId(memberId);
        cash.setAccount(account);
        cash.setAmount(-Math.abs(withdrawal)); // 출금 금액은 음수
        cash.setDate(new Timestamp(System.currentTimeMillis()));
        cash.setStatus(CashStatus.WITHDRAW); // 출금 상태 설정
        save(cash);
    }
}

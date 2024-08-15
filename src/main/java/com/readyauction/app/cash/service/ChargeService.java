package com.readyauction.app.cash.service;

import com.readyauction.app.cash.entity.Account;
import com.readyauction.app.cash.entity.Charge;
import com.readyauction.app.cash.entity.ChargeStatus;
import com.readyauction.app.cash.repository.ChargeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@Transactional
@RequiredArgsConstructor
public class ChargeService {

    private final AccountService accountService;
    private final ChargeRepository chargeRepository;

    public Charge save(Charge charge) {
        return chargeRepository.save(charge);
    }

    public void chargeCash(Long memberId, Charge charge) {
        // 현재 사용자의 Account 가져오기
        Account account = accountService.findByMemberId(memberId);

        // Update Account의 cash
        System.out.println("잔액 : " + account.getCash());
        System.out.println("충전 금액 : " + charge.getChargeAmount());
        account.setCash(account.getCash() + charge.getChargeAmount());
        accountService.save(account);
        System.out.println("총 금액 : " + account.getCash());

        // Insert Charge
        charge.setMemberId(memberId);
        charge.setAccount(account);
        charge.setChargeAmount(charge.getChargeAmount());
        charge.setDate(new Timestamp(System.currentTimeMillis()));
        charge.setChargeStatus(ChargeStatus.SUCCESS); // 충전 성공 상태 설정
        save(charge);
    }
}

package com.readyauction.app.cash.service;

import com.readyauction.app.cash.dto.ChargeDto;
import com.readyauction.app.cash.entity.Account;
import com.readyauction.app.cash.entity.Charge;
import com.readyauction.app.cash.entity.ChargeStatus;
import com.readyauction.app.cash.repository.ChargeRepository;
import com.readyauction.app.common.handler.ChargeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;

@Service
@Transactional
@RequiredArgsConstructor
public class ChargeService {

    private final AccountService accountService;
    private final ChargeRepository chargeRepository;

    public ChargeDto findChargeDtoByMemberId(Long memberId) {
        Charge charge = chargeRepository.findChargeByMemberId(memberId)
                .orElseThrow(() -> new ChargeNotFoundException("Charge not found for memberId: " + memberId));
        return new ChargeDto(charge);
    }

    public Charge save(Charge charge) {
        return chargeRepository.save(charge);
    }

    // 캐시 충전
    public void chargeCash(Long memberId, Charge charge) {
        // 현재 사용자의 Account 가져오기
        Account account = accountService.findByMemberId(memberId);

        // Update Account cash
        System.out.println("잔액 : " + account.getCash());
        System.out.println("충전 금액 : " + charge.getAmount());
        account.setCash(account.getCash() + charge.getAmount());
        accountService.save(account);
        System.out.println("총 금액 : " + account.getCash());

        // Insert Charge
        charge.setMemberId(memberId);
        charge.setAccount(account);
        charge.setAmount(charge.getAmount());
        charge.setDate(new Timestamp(System.currentTimeMillis()));
        charge.setStatus(ChargeStatus.SUCCESS); // 충전 성공 상태 설정
        save(charge);
    }

    // 캐시 출금 - 롤백
    public void withdrawCash(Long memberId, Charge charge, @RequestParam("withdrawal") Integer withdrawal) {
        Account account = accountService.findByMemberId(memberId);

        System.out.println("잔액 : " + account.getCash());
        System.out.println("출금 금액 : " + withdrawal);
        account.setCash(account.getCash() - withdrawal);
        accountService.save(account);
        System.out.println("총 금액 : " + account.getCash());

        // Insert Charge
        charge.setMemberId(memberId);
        charge.setAccount(account);
        charge.setAmount(-Math.abs(withdrawal)); // 출금 금액은 음수로 넣기
        charge.setDate(new Timestamp(System.currentTimeMillis()));
        charge.setStatus(ChargeStatus.ROLLBACK); // 출금 상태 설정
        save(charge);
    }
}

package com.readyauction.app.cash.service;

import com.readyauction.app.cash.entity.Account;
import com.readyauction.app.cash.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account findByMemberId(Long memberId) {
        return accountRepository.findByMemberId(memberId).orElseThrow();
    }
}

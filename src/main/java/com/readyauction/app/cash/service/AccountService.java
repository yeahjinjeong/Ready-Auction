package com.readyauction.app.cash.service;

import com.readyauction.app.cash.dto.AccountDto;
import com.readyauction.app.cash.entity.Account;
import com.readyauction.app.cash.repository.AccountRepository;
import com.readyauction.app.common.handler.AccountNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account findByMemberId(Long memberId) {
        return accountRepository.findAccountByMemberId(memberId).orElseThrow();
    }

    public Account create(Long userId) {
        try {
            Account account = Account.builder()
                    .cash(0)
                    .memberId(userId)
                    .build();
            return accountRepository.save(account);
        } catch (Exception e) {
            // 예외 처리 로직
            throw new RuntimeException("Failed to create account for memberId: " + userId, e);
        }
    }

    public Account withdrawal(Long userId, Integer withdrawalPrice) {
        try {
            Account account = accountRepository.findAccountByMemberId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Account not found for user ID: " + userId));
            System.out.println("출금 시도");
            // 출금 시도
            if (!account.withdrawal(withdrawalPrice)) {
                throw new RuntimeException("Insufficient funds for withdrawal");
            }

            // 변경된 계좌 정보를 저장
            return accountRepository.save(account);
        } catch (EntityNotFoundException e) {
            // 계좌를 찾지 못했을 때의 예외 처리
            throw new RuntimeException("Error during withdrawal: " + e.getMessage(), e);
        } catch (DataAccessException e) {
            // 데이터베이스 관련 예외 처리
            throw new RuntimeException("Database error during withdrawal: " + e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RuntimeException("Unexpected error occurred during withdrawal: " + e.getMessage(), e);
        }
    }

    public Account deposit(Long receiverAccountId, Integer depositPrice) {
        try {
            //받은이 계 좌조회
            Account account = accountRepository.findById(receiverAccountId)
                    .orElseThrow(() -> new EntityNotFoundException("Account not found for user ID: " + receiverAccountId));

            // 입금 시도
            if (!account.deposit(depositPrice)) {
                throw new RuntimeException("Insufficient funds for error");
            }
            System.out.println(account.getId() +" 계좌 아이디");
            // 변경된 계좌 정보를 저장
            return accountRepository.save(account);
        } catch (EntityNotFoundException e) {
            // 계좌를 찾지 못했을 때의 예외 처리
            throw new RuntimeException("Error during withdrawal: " + e.getMessage(), e);
        } catch (DataAccessException e) {
            // 데이터베이스 관련 예외 처리
            throw new RuntimeException("Database error during withdrawal: " + e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RuntimeException("Unexpected error occurred during withdrawal: " + e.getMessage(), e);
        }
    }



    /** 캐시 **/

    public AccountDto findAccountDtoByMemberId(Long memberId) {
        Account account = accountRepository.findAccountByMemberId(memberId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for memberId: " + memberId));
        return new AccountDto(account);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }
}

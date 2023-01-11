package com.ecommerce.backend.service.query;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.enums.AccountRole;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountQueryService {
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public Account readById(Long id) {
        return accountRepository.findById(id) // select * from cart where account_id = ? 쿼리도 나감
                .orElseThrow(() -> new EntityNotFoundException(Msg.ACCOUNT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Account readByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ACCOUNT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Account readByIdAndEmail(Long accountId, String email) {
        return accountRepository.findByIdAndEmail(accountId, email)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ACCOUNT_NOT_FOUND));
    }

    public boolean checkNotUser(Account account) {
        final AccountRole accountRole = readByEmail(account.getEmail()).getAccountRole();
        return accountRole == AccountRole.ADMIN || accountRole == AccountRole.SELLER;
    }
}

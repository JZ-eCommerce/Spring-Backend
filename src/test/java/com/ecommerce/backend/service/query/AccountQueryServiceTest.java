package com.ecommerce.backend.service.query;

import com.ecommerce.backend.config.SecurityConfig;
import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.enums.AccountRole;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.AccountRepository;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyLong;

@ExtendWith(MockitoExtension.class)
class AccountQueryServiceTest {
    private static final String EMAIL = "js@gmail.com";

    @InjectMocks
    private AccountQueryService sut;

    @Mock
    private AccountRepository accountRepository;

    @Nested
    @DisplayName("readById 테스트")
    class ReadByIdTest {
        @Test
        @DisplayName("정상 케이스")
        void readByIdTest01() {
            // given
            var expected = makeAccount(AccountRole.USER);
            given(accountRepository.findById(any())).willReturn(Optional.of(expected));

            // when
            final Account actual = sut.readById(1L);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("존재하지 않는 계정일 때")
        void readByIdTest02() {
            // given
            given(accountRepository.findById(any())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readById(1L));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ACCOUNT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("readByEmail 테스트")
    class ReadByEmailTest {
        @Test
        @DisplayName("정상 케이스")
        void readByEmailTest01() {
            // given
            var expected = makeAccount(AccountRole.USER);
            given(accountRepository.findByEmail(any())).willReturn(Optional.of(expected));

            // when
            final Account actual = sut.readByEmail(EMAIL);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("존재하지 않는 계정일 때")
        void readByEmailTest02() {
            // given
            given(accountRepository.findByEmail(any())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readByEmail(EMAIL));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ACCOUNT_NOT_FOUND);
        }
    }


    @Nested
    @DisplayName("readByEmail 테스트")
    class ReadByIdAndEmailTest {
        @Test
        @DisplayName("id와 email을 통해 계정을 찾음")
        void readByIdAndEmailTest01() {
            // given
            var expected = makeAccount(AccountRole.USER);
            given(accountRepository.findByIdAndEmail(anyLong(), anyString())).willReturn(Optional.of(expected));

            // when
            final Account actual = sut.readByIdAndEmail(1L, EMAIL);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("id와 email을 통해 계정을 찾지 못함")
        void readByEmailTest02() {
            // given
            given(accountRepository.findByIdAndEmail(anyLong(), anyString())).willReturn(Optional.empty());

            // when
            final AbstractThrowableAssert<?, ? extends Throwable> actual =
                    assertThatThrownBy(() -> sut.readByIdAndEmail(1L, EMAIL));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ACCOUNT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("checkNotUser 테스트")
    class CheckNotUser {
        @Test
        @DisplayName("ADMIN일 떄")
        void checkNotUserTest01() {
            // given
            var account = makeAccount(AccountRole.ADMIN);
            given(accountRepository.findByEmail(anyString())).willReturn(Optional.of(account));

            // when
            final boolean actual = sut.checkNotUser(account);

            // then
            assertTrue(actual);
        }

        @Test
        @DisplayName("SELLERN일 떄")
        void checkNotUserTest02() {
            // given
            var account = makeAccount(AccountRole.SELLER);
            given(accountRepository.findByEmail(anyString())).willReturn(Optional.of(account));

            // when
            final boolean actual = sut.checkNotUser(account);

            // then
            assertTrue(actual);
        }

        @Test
        @DisplayName("USER일 떄")
        void checkNotUserTest03() {
            // given
            var account = makeAccount(AccountRole.USER);
            given(accountRepository.findByEmail(anyString())).willReturn(Optional.of(account));

            // when
            final boolean actual = sut.checkNotUser(account);

            // then
            assertFalse(actual);
        }
    }

    private Account makeAccount(AccountRole accountRole) {
        return Account.builder()
                .id(1L)
                .email(EMAIL)
                .passwordHash(SecurityConfig.makePasswordHash("passwordHash"))
                .name("지수")
                .accountRole(accountRole)
                .phoneNumber("01011112222")
                .build();
    }
}
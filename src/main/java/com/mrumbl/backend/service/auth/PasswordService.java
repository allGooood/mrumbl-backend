package com.mrumbl.backend.service.auth;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.AccountErrorCode;
import com.mrumbl.backend.domain.Member;
import com.mrumbl.backend.repository.member.MemberRepository;
import com.mrumbl.backend.service.auth.dto.PasswordValidationResultInternal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordService {
    public static final int MAX_ATTEMPT = 5;

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Transactional(noRollbackFor = BusinessException.class)
    public PasswordValidationResultInternal validatePasswordAndHandleLock(Member member, String password) {

        // 계정 잠금 상태 확인
        if (member.isLocked()) {
            log.warn("Account is locked. email={}", member.getEmail());
            throw new BusinessException(AccountErrorCode.ACCOUNT_LOCKED);
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, member.getPassword())) {
            // 비밀번호 불일치: 실패 횟수 증가 및 저장
            member.updateFailedAttemptCount();

            int attemptLeft = MAX_ATTEMPT - member.getFailedAttemptCount();
            
            // 최대 시도 횟수 초과 시 계정 잠금
            if (attemptLeft <= 0) {
                log.warn("Account locked due to max failed attempts. email={}", member.getEmail());
                throw new BusinessException(AccountErrorCode.ACCOUNT_LOCKED);
            }

            log.warn("Password validation failed. email={}, attemptLeft={}", member.getEmail(), attemptLeft);
            return PasswordValidationResultInternal.builder()
                    .isValid(false)
                    .attemptLeft(attemptLeft)
                    .build();
        }

        // 비밀번호 일치: 실패 횟수 초기화
        member.resetFailedAttempt();
        log.info("Password validation successful. email={}", member.getEmail());

        return PasswordValidationResultInternal.builder()
                .isValid(true)
                .attemptLeft(MAX_ATTEMPT)
                .build();
    }
}






















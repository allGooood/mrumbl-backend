package com.mrumbl.backend.service.auth;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.AccountErrorCode;
import com.mrumbl.backend.domain.Member;
import com.mrumbl.backend.repository.member.MemberRepository;
import com.mrumbl.backend.service.auth.dto.PasswordValidationResult;
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

    /**
     * 비밀번호 검증 및 계정 잠금 처리
     * 
     * @param member 검증할 회원 엔티티
     * @param password 입력된 비밀번호
     * @return 비밀번호 검증 결과 (유효성, 남은 시도 횟수)
     * @throws BusinessException 계정이 잠긴 경우
     */
    @Transactional(noRollbackFor = BusinessException.class)
    public PasswordValidationResult validatePasswordAndHandleLock(Member member, String password) {

        // 계정 잠금 상태 확인
        if (member.isLocked()) {
            throw new BusinessException(AccountErrorCode.ACCOUNT_LOCKED);
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, member.getPassword())) {
            // 비밀번호 불일치: 실패 횟수 증가 및 저장
            memberRepository.save(member.updateFailedAttemptCount());

            int attemptLeft = MAX_ATTEMPT - member.getFailedAttemptCount();
            
            // 최대 시도 횟수 초과 시 계정 잠금
            if (attemptLeft <= 0) {
                throw new BusinessException(AccountErrorCode.ACCOUNT_LOCKED);
            }

            return PasswordValidationResult.builder()
                    .isValid(false)
                    .attemptLeft(attemptLeft)
                    .build();
        }

        // 비밀번호 일치: 실패 횟수 초기화
        memberRepository.save(member.resetFailedAttempt());

        return PasswordValidationResult.builder()
                .isValid(true)
                .attemptLeft(MAX_ATTEMPT)
                .build();
    }
}






















package com.mrumbl.backend.service.member.validation;

import com.mrumbl.backend.common.enumeration.MemberState;
import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.AccountErrorCode;
import com.mrumbl.backend.domain.Member;
import com.mrumbl.backend.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberValidator {
    private final MemberRepository memberRepository;

    public Member checkAndReturnExistingMember(String email){
        return memberRepository.findByEmailAndState(email, MemberState.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("Member not found or inactive. email={}", email);
                    return new BusinessException(AccountErrorCode.MEMBER_NOT_FOUND);
                });
    }

    public void checkExistingMember(String email){
        if (!memberRepository.existsByEmailAndState(email, MemberState.ACTIVE)) {
            log.warn("Member not found. email={}", email);
            throw new BusinessException(AccountErrorCode.MEMBER_NOT_FOUND);
        }
    }

}

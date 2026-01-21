package com.mrumbl.backend.service.member;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.AccountErrorCode;
import com.mrumbl.backend.controller.member.dto.EmailAvailabilityResponse;
import com.mrumbl.backend.domain.Member;
import com.mrumbl.backend.controller.member.dto.SignUpResponse;
import com.mrumbl.backend.common.enumeration.MemberState;
import com.mrumbl.backend.repository.member.MemberRepository;
import com.mrumbl.backend.service.member.validation.MemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    private final MemberValidator memberValidator;

    private final PasswordEncoder passwordEncoder;


    @Transactional
    public SignUpResponse signUp(String email, String password, String name){
        // 회원 상태와 상관없이 모두 검색
        if(memberRepository.existsByEmail(email)){
            throw new BusinessException(AccountErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Member member = Member.builder()
                .email(email)
                .name(name)
                .password(passwordEncoder.encode(password))
                .state(MemberState.ACTIVE)
                .build();

        memberRepository.save(member);
        log.info("Member signed up successfully. email={}", email);

        return SignUpResponse.builder()
                .email(email)
                .build();
    }

    @Transactional
    public void changePassword(String email, String password){
        Member memberFound = memberValidator.checkAndReturnExistingMember(email);

        String newPassword = passwordEncoder.encode(password);
        memberFound.updatePassword(newPassword);
        log.info("Password changed successfully. email={}", email);
    }

    @Transactional
    public void changeAddress(String email, String address, String addressDetail, String postcode){
        Member memberFound = memberValidator.checkAndReturnExistingMember(email);

        memberFound.changeAddress(address, addressDetail, postcode);
        log.info("Address changed successfully. email={}", email);
    }

    public EmailAvailabilityResponse checkEmailAvailability(String email){
        // 회원 상태와 상관없이 모두 검색
        boolean isAvailable = memberRepository.findByEmail(email).isEmpty();
        log.info("Email availability checked. email={}, isAvailable={}", email, isAvailable);

        return EmailAvailabilityResponse.builder()
                .isAvailable(isAvailable)
                .build();
    }

}

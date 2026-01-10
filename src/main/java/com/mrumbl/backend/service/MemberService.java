package com.mrumbl.backend.service;

import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.AccountErrorCode;
import com.mrumbl.backend.domain.Member;
import com.mrumbl.backend.controller.member.dto.SignUpResDto;
import com.mrumbl.backend.common.enumeration.MemberState;
import com.mrumbl.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignUpResDto signUp(String email, String password, String name){

        if(memberRepository.findByEmailAndState(email, MemberState.ACTIVE).isPresent()){
            throw new BusinessException(AccountErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Member member = Member.builder()
                .email(email)
                .name(name)
                .password(passwordEncoder.encode(password))
                .state(MemberState.ACTIVE)
                .build();

        Member savedMember = memberRepository.save(member);
        return SignUpResDto.builder()
                .email(savedMember.getEmail())
                .build();
    }

}

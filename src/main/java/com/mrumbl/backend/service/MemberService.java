package com.mrumbl.backend.service;

import com.mrumbl.backend.controller.member.dto.JoinReqDto;
import com.mrumbl.backend.domain.Member;
import com.mrumbl.backend.controller.member.dto.JoinResDto;
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
    public JoinResDto join(JoinReqDto reqDto){
        Member member = Member.builder()
                .email(reqDto.getEmail())
                .name(reqDto.getName())
                .password(passwordEncoder.encode(reqDto.getPassword()))
                .state(MemberState.ACTIVE)
                .build();
        Member savedMember = memberRepository.save(member);
        return JoinResDto.builder()
                .email(savedMember.getEmail())
                .build();
    }

}

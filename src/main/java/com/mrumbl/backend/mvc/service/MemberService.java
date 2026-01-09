package com.mrumbl.backend.mvc.service;

import com.mrumbl.backend.mvc.controller.member.dto.JoinReqDto;
import com.mrumbl.backend.mvc.controller.member.dto.JoinResDto;
import com.mrumbl.backend.mvc.domain.Member;
import com.mrumbl.backend.enumeration.MemberState;
import com.mrumbl.backend.mvc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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

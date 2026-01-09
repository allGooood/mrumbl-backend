package com.mrumbl.backend.mvc.service;

import com.mrumbl.backend.enumeration.MemberState;
import com.mrumbl.backend.exception.error_codes.AccountErrorCode;
import com.mrumbl.backend.jwt.JwtToken;
import com.mrumbl.backend.jwt.TokenManager;
import com.mrumbl.backend.mvc.controller.auth.dto.*;
import com.mrumbl.backend.mvc.domain.Member;
import com.mrumbl.backend.exception.BusinessException;
import com.mrumbl.backend.exception.error_codes.AuthErrorCode;
import com.mrumbl.backend.mvc.domain.redis.RedisToken;
import com.mrumbl.backend.mvc.repository.MemberRepository;
import com.mrumbl.backend.mvc.repository.redis.RedisTokenRepository;
import com.mrumbl.backend.properties.JwtProperties;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenManager tokenManager;
    private final RedisTokenRepository redisTokenRepository;
    private final JwtProperties jwtProperties;

    public JwtToken login(LoginReqDto reqDto){
        Member memberFound = memberRepository.findByEmailAndState(reqDto.getEmail(), MemberState.ACTIVE)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_CREDENTIALS));

        if(!passwordEncoder.matches(reqDto.getPassword(), memberFound.getPassword())){
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        JwtToken tokens = tokenManager.createTokens(memberFound);
        redisTokenRepository.save(RedisToken.builder()
                        .email(memberFound.getEmail())
                        .refreshToken(tokens.getRefreshToken())
                        .ttl(jwtProperties.getRefreshTokenExpiration())
                .build());

        memberRepository.save(memberFound.updateLastLoginAt());

        return tokens;
    }

    public LogoutResDto logout(String email){
        redisTokenRepository.deleteById(email);
        return LogoutResDto.builder()
                .email(email)
                .build();
    }

    public JwtToken reissue(String email, String refreshToken) {

        // 1. token 유효성 검증 (유효하지 않으면 BusinessException 발생)
        tokenManager.validateToken(refreshToken);

        // 2. redis에서 토큰 조회
        redisTokenRepository.findById(email)
                .map(RedisToken::getRefreshToken)
                .filter(t -> t.equals(refreshToken))
                .orElseThrow(() -> new BusinessException(AuthErrorCode.EXPIRED_AUTH_TOKEN));


        // 3. 사용자 정보 확인 : 존재하는 유저인가, 활성화 회원인가
        Member memberFound = memberRepository.findByEmailAndState(email, MemberState.ACTIVE)
                .orElseThrow(() -> new BusinessException(AccountErrorCode.MEMBER_NOT_FOUND));

        // 4. 토큰 발행
        JwtToken tokens = tokenManager.createTokens(memberFound);
        redisTokenRepository.save(RedisToken.builder()
                .email(memberFound.getEmail())
                .refreshToken(tokens.getRefreshToken())
                .ttl(jwtProperties.getRefreshTokenExpiration())
                .build());

        return tokens;
    }
}

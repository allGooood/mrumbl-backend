package com.mrumbl.backend.service;

import com.mrumbl.backend.common.enumeration.MemberState;
import com.mrumbl.backend.common.exception.error_codes.AccountErrorCode;
import com.mrumbl.backend.common.jwt.JwtToken;
import com.mrumbl.backend.common.jwt.TokenManager;
import com.mrumbl.backend.common.util.RandomManager;
import com.mrumbl.backend.controller.auth.dto.LoginReqDto;
import com.mrumbl.backend.controller.auth.dto.LogoutResDto;
import com.mrumbl.backend.controller.auth.dto.SendVerificationReqDto;
import com.mrumbl.backend.controller.auth.dto.SendVerificationResDto;
import com.mrumbl.backend.domain.Member;
import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.AuthErrorCode;
import com.mrumbl.backend.domain.redis.RedisToken;
import com.mrumbl.backend.domain.redis.RedisVerificationCode;
import com.mrumbl.backend.repository.MemberRepository;
import com.mrumbl.backend.repository.redis.RedisTokenRepository;
import com.mrumbl.backend.common.properties.JwtProperties;
import com.mrumbl.backend.repository.redis.RedisVerificationCodeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final MemberRepository memberRepository;
    private final RedisTokenRepository redisTokenRepository;
    private final RedisVerificationCodeRepository redisVerificationCodeRepository;

    private final MailService mailService;

    private final PasswordEncoder passwordEncoder;
    private final TokenManager tokenManager;
    private final JwtProperties jwtProperties;
    private final RandomManager randomManager;

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
        if(!tokenManager.isValidateToken(refreshToken)){
            throw new BusinessException(AuthErrorCode.INVALID_AUTH_TOKEN);
        }

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

    public SendVerificationResDto sendVerificationCode(String email) {
        // 1. 난수 생성 -> Redis 저장
        String verificationCode = randomManager.createNumericCode(6);
        redisVerificationCodeRepository.save(RedisVerificationCode.builder()
                        .email(email)
                        .verificationCode(verificationCode)
                .build());

        // 2. Email 전송
        try{
            mailService.sendMimeMessage("인증번호 입니다.", verificationCode, email);
        } catch (Exception e){
            redisVerificationCodeRepository.deleteById(email);
            throw e;
        }

        return SendVerificationResDto.builder()
                .email(email)
                .issuedAt(LocalDateTime.now())
                .ttlSeconds(180)
                .build();
    }
}

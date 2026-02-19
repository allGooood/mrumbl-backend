package com.mrumbl.backend.service.auth;

import com.mrumbl.backend.common.enumeration.MemberState;
import com.mrumbl.backend.common.exception.error_codes.AccountErrorCode;
import com.mrumbl.backend.common.jwt.JwtToken;
import com.mrumbl.backend.common.jwt.TokenManager;
import com.mrumbl.backend.common.util.RandomManager;
import com.mrumbl.backend.controller.auth.dto.*;
import com.mrumbl.backend.domain.Member;
import com.mrumbl.backend.common.exception.BusinessException;
import com.mrumbl.backend.common.exception.error_codes.AuthErrorCode;
import com.mrumbl.backend.domain.redis.RedisToken;
import com.mrumbl.backend.domain.redis.RedisVerificationCode;
import com.mrumbl.backend.repository.member.MemberRepository;
import com.mrumbl.backend.repository.redis.RedisTokenRepository;
import com.mrumbl.backend.common.properties.JwtProperties;
import com.mrumbl.backend.repository.redis.RedisVerificationCodeRepository;
import com.mrumbl.backend.service.auth.dto.LoginResultInternal;
import com.mrumbl.backend.service.auth.dto.PasswordValidationResultInternal;
import com.mrumbl.backend.service.external.MailService;
import com.mrumbl.backend.service.member.validation.MemberValidator;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final PasswordService passwordService;

    private final MemberValidator memberValidator;

    private final TokenManager tokenManager;
    private final JwtProperties jwtProperties;
    private final RandomManager randomManager;


    @Transactional(noRollbackFor = BusinessException.class)
    public LoginResultInternal login(LoginRequest reqDto){
        Member memberFound = memberRepository.findByEmailAndState(reqDto.getEmail(), MemberState.ACTIVE)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_CREDENTIALS));

        PasswordValidationResultInternal passwordValidation
                = passwordService.validatePasswordAndHandleLock(memberFound, reqDto.getPassword());

        JwtToken tokens = null;
        int attemptLeft = passwordValidation.getAttemptLeft();

        if(passwordValidation.isValid()){
            tokens = tokenManager.createTokens(memberFound);

            redisTokenRepository.save(RedisToken.builder()
                    .email(memberFound.getEmail())
                    .refreshToken(tokens.getRefreshToken())
                    .ttl(jwtProperties.getRefreshTokenExpiration())
                    .build());

            memberFound.updateLastLoginAt();

            attemptLeft = PasswordService.MAX_ATTEMPT;
            log.info("Login successful. email={}", memberFound.getEmail());

        } else {
            log.warn("Login failed. email={}, attemptLeft={}", memberFound.getEmail(), attemptLeft);
        }

        return LoginResultInternal.builder()
                .accessToken(tokens != null ? tokens.getAccessToken() : null)
                .refreshToken(tokens != null ? tokens.getRefreshToken() : null)
                .attemptLeft(attemptLeft)
                .email(memberFound.getEmail())

                .build();
    }

    public void logout(String email){
        redisTokenRepository.deleteById(email);
        log.info("Logout successful. email={}", email);
    }

    @Transactional(readOnly = true)
    public JwtToken reissue(String refreshToken) {
        // 1. token 유효성 검증 (유효하지 않으면 BusinessException 발생)
        if(!tokenManager.isValidateToken(refreshToken)){
            throw new BusinessException(AuthErrorCode.INVALID_AUTH_TOKEN);
        }

        String email = tokenManager.getUserEmail(refreshToken);

        // 2. redis에서 토큰 조회
        redisTokenRepository.findById(email)
                .map(RedisToken::getRefreshToken)
                .filter(t -> t.equals(refreshToken))
                .orElseThrow(() -> new BusinessException(AuthErrorCode.EXPIRED_AUTH_TOKEN));

        // 3. 사용자 정보 확인 : 존재하는 유저인가, 활성화 회원인가
        Member memberFound = memberValidator.checkAndReturnExistingMember(email);

        // 4. 토큰 발행
        JwtToken tokens = tokenManager.createTokens(memberFound);
        redisTokenRepository.save(RedisToken.builder()
                .email(memberFound.getEmail())
                .refreshToken(tokens.getRefreshToken())
                .ttl(jwtProperties.getRefreshTokenExpiration())
                .build());

        log.info("Token reissued successfully. email={}", email);

        return tokens;
    }

    public SendVerificationResponse sendVerificationCode(String email) {
        // 1. 난수 생성 -> Redis 저장
        String verificationCode = randomManager.createNumericCode(6);
        redisVerificationCodeRepository.save(RedisVerificationCode.builder()
                        .email(email)
                        .verificationCode(verificationCode)
                .build());

        // 2. Email 전송
        try{
            mailService.sendMimeMessage("인증번호 입니다.", verificationCode, email);
            log.info("Verification code sent successfully. email={}", email);
        } catch (Exception e){
            redisVerificationCodeRepository.deleteById(email);
            log.error("Failed to send verification code. email={}", email, e);
            throw e;
        }

        return SendVerificationResponse.builder()
                .email(email)
                .issuedAt(LocalDateTime.now())
                .ttlSeconds(180)
                .build();
    }

    // TODO - Test 필요 (계정 이의신청 중)
    public VerifyCodeResponse verifyVerificationCode(String email, String verificationCode){
        RedisVerificationCode codeFound = redisVerificationCodeRepository.findById(email)
                .orElseThrow(() -> {
                    log.warn("Verification code expired. email={}", email);
                    return new BusinessException(AuthErrorCode.VERIFICATION_CODE_EXPIRED);
                });

        if(!codeFound.getVerificationCode().equals(verificationCode)){
            log.warn("Verification code mismatch. email={}", email);
            throw new BusinessException(AuthErrorCode.VERIFICATION_CODE_MISMATCH);
        }

        redisVerificationCodeRepository.deleteById(email);
        log.info("Verification code verified successfully. email={}", email);

        return VerifyCodeResponse.builder()
                .email(email)
                .verifiedAt(LocalDateTime.now())
                .build();
    }

    @Transactional(noRollbackFor = BusinessException.class)
    public PasswordValidationResultInternal verifyPassword(String email, String password){
        Member memberFound = memberValidator.checkAndReturnExistingMember(email);

        return passwordService.validatePasswordAndHandleLock(memberFound, password);
    }
}

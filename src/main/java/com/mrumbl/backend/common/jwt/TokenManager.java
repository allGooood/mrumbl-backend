package com.mrumbl.backend.common.jwt;

import com.mrumbl.backend.domain.Member;
import com.mrumbl.backend.common.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenManager {
    private final static String MEMBER_ID = "memberId";

    private final JwtProperties jwtProperties;
    private Key key;

    @PostConstruct
    void init(){
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성
    public JwtToken createTokens(Member member){
        Date issuedAt = new Date();

        Date accessTokenExpiredAt = new Date(issuedAt.getTime() + jwtProperties.getAccessTokenExpiration());
        Date refreshTokenExpiredAt = new Date(issuedAt.getTime() + jwtProperties.getRefreshTokenExpiration());

        String accessToken = createToken(member, issuedAt, accessTokenExpiredAt);
        String refreshToken = createToken(member, issuedAt, refreshTokenExpiredAt);

        return JwtToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(member.getEmail())
                .build();
    }

    // 토큰 유효성 검증 및 파싱
    public boolean isValidateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;

        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("유효하지 않은 토큰입니다.");
//            throw new BusinessException(AuthErrorCode.INVALID_AUTH_TOKEN);
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다.");
//            throw new BusinessException(AuthErrorCode.EXPIRED_AUTH_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 토큰입니다.");
//            throw new BusinessException(AuthErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            log.error("빈 클레임 입니다.");
//            throw new BusinessException(AuthErrorCode.EMPTY_CLAIMS);
        }
        return false;
    }

    // Authentication 객체 생성
    public Authentication createAuthentication(String accessToken){
        Claims claims = parseClaims(accessToken);

        // 1. Principal 생성
        JwtUser principal = JwtUser.builder()
                .email(claims.getSubject())
                .memberId(claims.get(MEMBER_ID, Long.class))
                .build();

        // 2. Authorities 설정
        Collection<? extends GrantedAuthority> authorities =
                Stream.of(new SimpleGrantedAuthority("ROLE_USER"))
                        .toList();

        return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
    }

    private Claims parseClaims(String accessToken){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }

    private String createToken(Member member, Date issuedAt, Date expiredAt) {
        return Jwts.builder()
                .setSubject(member.getEmail())
                .claim(MEMBER_ID, member.getId())
//                .setClaims(Map.of(MEMBER_ID, member.getId()))
                .setIssuedAt(issuedAt)
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}

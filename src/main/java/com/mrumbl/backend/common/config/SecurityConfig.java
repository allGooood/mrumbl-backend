package com.mrumbl.backend.common.config;

import com.mrumbl.backend.common.exception.security.CustomAccessDeniedHandler;
import com.mrumbl.backend.common.exception.security.CustomAuthenticationEntryPoint;
import com.mrumbl.backend.common.jwt.JwtFilter;
import com.mrumbl.backend.common.jwt.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenManager tokenManager;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers(POST, "/api/auth/password/verify").authenticated()
                                .requestMatchers(DELETE,"/api/auth/logout").authenticated()
                                .requestMatchers(POST, "/api/auth/reissue").authenticated()

                                .requestMatchers(PUT, "/api/members/password").authenticated()
                                .requestMatchers(PUT, "/api/members").authenticated()

                                .requestMatchers("/api/v2/carts/**", "/api/carts/**").authenticated()

                                .requestMatchers( "/api/orders/**").authenticated()

                                .anyRequest().permitAll()
                );

        return http.build();
    }

}

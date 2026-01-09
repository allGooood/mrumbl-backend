package com.mrumbl.backend.config;

import com.mrumbl.backend.exception.security.CustomAccessDeniedHandler;
import com.mrumbl.backend.exception.security.CustomAuthenticationEntryPoint;
import com.mrumbl.backend.jwt.JwtFilter;
import com.mrumbl.backend.jwt.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.Customizer.withDefaults;

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
                                .requestMatchers(POST, "/auth/password/verify").authenticated()
                                .requestMatchers(DELETE,"/auth/token").authenticated()
                                .requestMatchers(POST, "/auth/refresh-token").authenticated()

                                .requestMatchers(PUT, "/members/password").authenticated()
                                .requestMatchers(PUT, "/members").authenticated()

                                .requestMatchers( "/carts/**").authenticated()

                                .requestMatchers( "/orders/**").authenticated()

                                // Test용
                                .requestMatchers("/test/hello").authenticated()
//                                .requestMatchers("/test/password").authenticated()
//                                .requestMatchers(GET, "/test/members").authenticated()
                                .requestMatchers(PUT, "/test/members").authenticated()
                                .anyRequest().permitAll()
                );

        return http.build();
    }

}

package com.mrumbl.backend.domain;

import com.mrumbl.backend.common.BaseEntity;
import com.mrumbl.backend.common.enumeration.MemberState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.mrumbl.backend.service.AuthService.MAX_LOGIN_ATTEMPT;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String name;
    private String address;
    private String addressDetail;
    private String postcode;

    @Enumerated(EnumType.STRING)
    private MemberState state;

    private LocalDateTime joinedAt;
    private LocalDateTime lastLoginAt;

    @Column(columnDefinition = "TINYINT UNSIGNED")
    private Integer failedAttemptCount;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean isLocked;

    public Member updateLastLoginAt(){
        this.lastLoginAt = LocalDateTime.now();
        return this;
    }

    public Member updateFailedAttemptCount(){
        this.failedAttemptCount++;

        if(failedAttemptCount >= MAX_LOGIN_ATTEMPT){
            this.isLocked = true;
        }
        return this;
    }

    public Member resetFailedAttempt(){
        this.failedAttemptCount = 0;
        this.isLocked = false;
        return this;
    }

}

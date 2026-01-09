package com.mrumbl.backend.mvc.domain;

import com.mrumbl.backend.common.BaseEntity;
import com.mrumbl.backend.enumeration.MemberState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    public Member updateLastLoginAt(){
        this.lastLoginAt = LocalDateTime.now();
        return this;
    }
}

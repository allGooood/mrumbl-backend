package com.mrumbl.backend.repository.member;

import com.mrumbl.backend.common.enumeration.MemberState;
import com.mrumbl.backend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmailAndPassword(String email, String password);
    Optional<Member> findByEmailAndState(String email, MemberState state);
    Optional<Member> findByIdAndState(Long memberId, MemberState state);
    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);
}

package com.mrumbl.backend.mvc.repository;

import com.mrumbl.backend.enumeration.MemberState;
import com.mrumbl.backend.mvc.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmailAndPassword(String email, String password);
    Optional<Member> findByEmailAndState(String email, MemberState state);

//    @Query("select m from Member m where m.email = :email and m.state = 'ACTIVE'")
//    Optional<Member> findActiveByEmail(@Param("email") String email);
}

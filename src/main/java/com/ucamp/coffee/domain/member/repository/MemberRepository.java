package com.ucamp.coffee.domain.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ucamp.coffee.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
	// 이메일로 회원 조회
	Optional<Member> findByEmail(String email);

    // 전화번호로 회원 조회
    Optional<Member> findByTel(String tel);
}

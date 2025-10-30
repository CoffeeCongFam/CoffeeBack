package com.ucamp.coffee.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ucamp.coffee.domain.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

}

package com.megastudy.surlkdh.domain.member.service.port;

import java.util.Optional;

import com.megastudy.surlkdh.domain.member.entity.Member;

public interface MemberRepository {
	void save(Member member);

	Optional<Member> findByEmail(String email);

	Optional<Member> findById(Long id);
}

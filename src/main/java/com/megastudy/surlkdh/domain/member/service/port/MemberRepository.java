package com.megastudy.surlkdh.domain.member.service.port;

import com.megastudy.surlkdh.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MemberRepository {
    void save(Member member);

    Optional<Member> findByEmail(String email);

    Optional<Member> findById(Long id);

    Page<Member> findAll(Pageable pageable);

    Optional<Member> update(Member member);
}

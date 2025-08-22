package com.megastudy.surlkdh.domain.member.infrastructure.mybatis;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.megastudy.surlkdh.domain.member.entity.Member;
import com.megastudy.surlkdh.domain.member.infrastructure.mybatis.dto.MemberEntity;
import com.megastudy.surlkdh.domain.member.infrastructure.mybatis.mapper.MemberMapper;
import com.megastudy.surlkdh.domain.member.service.port.MemberRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

	private final MemberMapper memberMapper;

	@Override
	public void save(Member member) {
		MemberEntity memberEntity = MemberEntity.from(member);
		memberMapper.insert(memberEntity);
	}

	@Override
	public Optional<Member> findByEmail(String email) {
		return Optional.ofNullable(memberMapper.findByEmail(email))
			.map(MemberEntity::toModel);
	}

	@Override
	public Optional<Member> findById(Long id) {
		return Optional.ofNullable(memberMapper.findById(id))
			.map(MemberEntity::toModel);
	}
}
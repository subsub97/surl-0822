package com.megastudy.surlkdh.domain.member.infrastructure.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.megastudy.surlkdh.domain.member.infrastructure.mybatis.dto.MemberEntity;

@Mapper
public interface MemberMapper {

	void insert(MemberEntity memberEntity);

	MemberEntity findByEmail(String email);

	MemberEntity findById(Long id);
}

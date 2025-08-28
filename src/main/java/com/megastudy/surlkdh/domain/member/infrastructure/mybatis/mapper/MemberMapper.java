package com.megastudy.surlkdh.domain.member.infrastructure.mybatis.mapper;

import com.megastudy.surlkdh.domain.member.infrastructure.mybatis.dto.MemberEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface MemberMapper {

    void insert(MemberEntity memberEntity);

    MemberEntity findByEmail(String email);

    MemberEntity findById(Long id);

    Long countAll();

    List<MemberEntity> findAll(@Param("pageable") Pageable pageable);

    void update(MemberEntity memberEntity);
}

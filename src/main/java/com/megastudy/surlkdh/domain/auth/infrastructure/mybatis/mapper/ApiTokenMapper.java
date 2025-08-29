package com.megastudy.surlkdh.domain.auth.infrastructure.mybatis.mapper;

import com.megastudy.surlkdh.domain.auth.infrastructure.mybatis.dto.ApiTokenEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface ApiTokenMapper {

    ApiTokenEntity findByTokenValue(@Param("tokenValue") String tokenValue);

    void insert(ApiTokenEntity apiTokenEntity);

    ApiTokenEntity findById(@Param("apiTokenId") Long apiTokenId);

    List<ApiTokenEntity> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    List<ApiTokenEntity> findAll(@Param("pageable") Pageable pageable);

    void update(ApiTokenEntity apiTokenEntity);

    void deleteById(@Param("apiTokenId") Long apiTokenId);

    Long countByDepartment(String department);

    List<ApiTokenEntity> findByDepartment(String department, Pageable pageable);

    Long countAll();

    Long countByMemberId(Long memberId);

}

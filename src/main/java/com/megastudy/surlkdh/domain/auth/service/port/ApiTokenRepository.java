package com.megastudy.surlkdh.domain.auth.service.port;

import com.megastudy.surlkdh.domain.auth.entity.ApiToken;
import com.megastudy.surlkdh.domain.member.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ApiTokenRepository {
    Optional<ApiToken> findByTokenValue(String tokenValue);

    ApiToken save(ApiToken apiToken);

    Optional<ApiToken> findById(Long apiTokenId);

    Page<ApiToken> findByMemberId(Long memberId, Pageable pageable);

    Page<ApiToken> findAll(Pageable pageable);

    void deleteById(Long apiTokenId);

    ApiToken update(ApiToken apiToken);

    Page<ApiToken> findByDepartment(Department department, Pageable pageable);
}
package com.megastudy.surlkdh.domain.auth.infrastructure.mybatis;

import com.megastudy.surlkdh.domain.auth.entity.ApiToken;
import com.megastudy.surlkdh.domain.auth.infrastructure.mybatis.dto.ApiTokenEntity;
import com.megastudy.surlkdh.domain.auth.infrastructure.mybatis.mapper.ApiTokenMapper;
import com.megastudy.surlkdh.domain.auth.service.port.ApiTokenRepository;
import com.megastudy.surlkdh.domain.member.entity.Department;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ApiTokenRepositoryImpl implements ApiTokenRepository {

    private final ApiTokenMapper apiTokenMapper;

    @Override
    public Optional<ApiToken> findByTokenValue(String tokenValue) {
        ApiTokenEntity entity = apiTokenMapper.findByTokenValue(tokenValue);
        return Optional.ofNullable(entity).map(ApiTokenEntity::toModel);
    }

    @Override
    public Optional<ApiToken> findById(Long apiTokenId) {
        ApiTokenEntity entity = apiTokenMapper.findById(apiTokenId);
        return Optional.ofNullable(entity).map(ApiTokenEntity::toModel);
    }

    @Override
    public Page<ApiToken> findByMemberId(Long memberId, Pageable pageable) {
        Long count = apiTokenMapper.countByMemberId(memberId);
        List<ApiTokenEntity> entityList = apiTokenMapper.findByMemberId(memberId, pageable);

        List<ApiToken> domainList = entityList.stream()
                .map(ApiTokenEntity::toModel)
                .toList();

        return new PageImpl<>(domainList, pageable, count);
    }

    @Override
    public Page<ApiToken> findAll(Pageable pageable) {
        Long count = apiTokenMapper.countAll();
        List<ApiTokenEntity> entityList = apiTokenMapper.findAll(pageable);

        List<ApiToken> domainList = entityList.stream()
                .map(ApiTokenEntity::toModel)
                .toList();

        return new PageImpl<>(domainList, pageable, count);
    }

    @Override
    public Page<ApiToken> findByDepartment(Department department, Pageable pageable) {
        Long count = apiTokenMapper.countByDepartment(department.getName());
        List<ApiTokenEntity> entityList = apiTokenMapper.findByDepartment(department.getName(), pageable);

        List<ApiToken> domainList = entityList.stream()
                .map(ApiTokenEntity::toModel)
                .toList();

        return new PageImpl<>(domainList, pageable, count);
    }

    @Override
    public ApiToken save(ApiToken apiToken) {
        if (apiToken.getApiTokenId() == null) {
            return insert(apiToken);
        } else {
            return update(apiToken);
        }
    }

    @Override
    public void deleteById(Long apiTokenId) {

        apiTokenMapper.deleteById(apiTokenId);
    }

    @Override
    public ApiToken update(ApiToken apiToken) {
        ApiTokenEntity entity = ApiTokenEntity.from(apiToken);
        entity.setUpdatedAt(LocalDateTime.now());

        apiTokenMapper.update(entity);

        return findById(apiToken.getApiTokenId()).orElseThrow();
    }


    private ApiToken insert(ApiToken apiToken) {
        ApiTokenEntity entity = ApiTokenEntity.from(apiToken);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        apiTokenMapper.insert(entity);

        return findById(entity.getApiTokenId()).orElseThrow();
    }
}

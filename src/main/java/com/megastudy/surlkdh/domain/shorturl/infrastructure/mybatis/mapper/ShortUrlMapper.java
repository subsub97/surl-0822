package com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.mapper;

import com.megastudy.surlkdh.domain.shorturl.infrastructure.mybatis.dto.ShortUrlEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface ShortUrlMapper {

    void insert(ShortUrlEntity shortUrlEntity);

    ShortUrlEntity findByShortCode(String shortCode);

    ShortUrlEntity findByShortUrlId(Long shortUrlId);

    List<ShortUrlEntity> findByDepartment(String department, Pageable pageable);

    Long countByDepartment(String department);

    void update(ShortUrlEntity shortUrlEntity);

    void deleteShortUrlById(Long shortUrlId);

    String findPcUrlByShortCode(String shortCode);

    String findMobileByShortCode(String shortCode);

    void deleteByShortCode(String shortCode);
}
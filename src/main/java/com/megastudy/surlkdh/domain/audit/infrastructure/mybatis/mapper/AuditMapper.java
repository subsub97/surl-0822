package com.megastudy.surlkdh.domain.audit.infrastructure.mybatis.mapper;

import com.megastudy.surlkdh.domain.audit.infrastructure.mybatis.dto.AuditEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditMapper {
    void insert(AuditEntity auditEntity);
}

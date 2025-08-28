package com.megastudy.surlkdh.domain.audit.infrastructure.mybatis;

import com.megastudy.surlkdh.domain.audit.entity.Audit;
import com.megastudy.surlkdh.domain.audit.infrastructure.mybatis.dto.AuditEntity;
import com.megastudy.surlkdh.domain.audit.infrastructure.mybatis.mapper.AuditMapper;
import com.megastudy.surlkdh.domain.audit.service.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuditRepositoryImpl implements AuditRepository {

    private final AuditMapper auditMapper;

    @Override
    public void save(Audit audit) {
        AuditEntity entity = AuditEntity.from(audit);
        auditMapper.insert(entity);
    }
}

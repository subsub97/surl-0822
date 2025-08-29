package com.megastudy.surlkdh.domain.audit.service;

import com.megastudy.surlkdh.domain.audit.entity.Audit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditRepository auditRepository;

    @Async("auditLogExecutor")
    @Transactional
    public void saveAsync(Audit audit) {
        auditRepository.save(audit);
    }
}

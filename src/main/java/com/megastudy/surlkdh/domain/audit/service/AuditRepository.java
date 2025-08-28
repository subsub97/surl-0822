package com.megastudy.surlkdh.domain.audit.service;

import com.megastudy.surlkdh.domain.audit.entity.Audit;

public interface AuditRepository {

    void save(Audit audit);
}

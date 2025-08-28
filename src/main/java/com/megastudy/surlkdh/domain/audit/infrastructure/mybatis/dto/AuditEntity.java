package com.megastudy.surlkdh.domain.audit.infrastructure.mybatis.dto;

import com.megastudy.surlkdh.domain.audit.entity.Audit;
import lombok.Builder;
import lombok.Getter;

@Getter

public class AuditEntity {
    private Long auditId;
    private String action;
    private String actorType;
    private Long actorId;
    private String resourceType;
    private Long resourceId;
    private Boolean success;
    private String errorMessage;
    private String role;

    @Builder
    private AuditEntity(String action, Long auditId, String actorType, Long actorId, String resourceType, Long resourceId, Boolean success, String errorMessage, String role) {
        this.action = action;
        this.auditId = auditId;
        this.actorType = actorType;
        this.actorId = actorId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.success = success;
        this.errorMessage = errorMessage;
        this.role = role;
    }

    public static AuditEntity from(Audit audit) {
        return AuditEntity.builder()
                .action(audit.getAction())
                .actorType(audit.getActorType())
                .actorId(audit.getActorId())
                .resourceType(audit.getResourceType())
                .resourceId(audit.getResourceId())
                .success(audit.getSuccess())
                .errorMessage(audit.getErrorMessage())
                .role(audit.getRole().name())
                .build();
    }
}

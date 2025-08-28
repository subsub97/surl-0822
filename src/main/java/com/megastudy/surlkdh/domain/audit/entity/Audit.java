package com.megastudy.surlkdh.domain.audit.entity;

import com.megastudy.surlkdh.domain.member.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Audit {
    private Long auditId;
    private String action;
    private String actorType;
    private Long actorId;
    private Long resourceId;
    private String resourceType;
    private Boolean success;
    private String errorMessage;
    private Role role;
    private LocalDateTime createdAt;

    public static Audit create(String action, String actorType, Long actorId, Long resourceId,
                               String resourceType, Boolean success, String errorMessage, Role role) {
        return Audit.builder()
                .action(action)
                .actorType(actorType)
                .actorId(actorId)
                .resourceId(resourceId)
                .resourceType(resourceType)
                .success(success)
                .errorMessage(errorMessage)
                .role(role)
                .build();
    }
}

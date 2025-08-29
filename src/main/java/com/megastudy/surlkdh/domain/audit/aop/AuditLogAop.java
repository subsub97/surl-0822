package com.megastudy.surlkdh.domain.audit.aop;

import com.megastudy.surlkdh.domain.audit.entity.Audit;
import com.megastudy.surlkdh.domain.audit.service.AuditLogService;
import com.megastudy.surlkdh.domain.member.entity.Role;
import com.megastudy.surlkdh.infrastructure.security.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAop {

    private final AuditLogService auditLogService;
    private final AuditContext auditContext;

    @Around("@annotation(auditLog)")
    public Object logAudit(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {

        Exception exception = null;

        try {
            Object result = joinPoint.proceed();
            return result;

        } catch (Exception e) {
            exception = e;
            throw e;

        } finally {
            // 성공/실패 관계없이 항상 Audit 로그 기록
            try {
                recordAuditLog(auditLog, exception == null, exception);
            } catch (Exception e) {
                log.error("Failed to record audit log", e);
            }
        }
    }

    private void recordAuditLog(AuditLog auditLog, boolean success, Exception exception) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof MemberPrincipal)) {
            log.warn("인증되지 않은 사용자의 요청 발생");
            return;
        }

        MemberPrincipal principal = (MemberPrincipal) authentication.getPrincipal();

        Role role = Role.fromKey(
                principal.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse(null)
        );
        // ADMIN , LEADER인 경우만 기록
        if (!isAdminUser(role)) {
            return;
        }

        Long resourceId = auditContext.getResourceId();

        Audit audit = Audit.builder()
                .resourceType(auditLog.resourceType())
                .resourceId(resourceId)
                .action(auditLog.action())
                .actorType(principal.getUsername())
                .actorId(principal.getId())
                .success(success)
                .errorMessage(exception != null ? exception.getMessage() : null)
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();

        auditLogService.saveAsync(audit);
    }

    private boolean isAdminUser(Role role) {
        return role.equals(Role.ADMIN) || role.equals(Role.LEADER);
    }
}

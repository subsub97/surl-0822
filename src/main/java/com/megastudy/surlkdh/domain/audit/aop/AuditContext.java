package com.megastudy.surlkdh.domain.audit.aop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Getter
@Setter
public class AuditContext {
    
    private Long resourceId;
}

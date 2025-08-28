package com.megastudy.surlkdh.domain.auth.service.dto;

import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.infrastructure.security.MemberPrincipal;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record AuthenticatedUser(
        Long id,
        Department department,
        Collection<? extends GrantedAuthority> authorities
) {
    public static AuthenticatedUser from(MemberPrincipal principal) {
        return new AuthenticatedUser(
                principal.getId(),
                principal.getDepartment(),
                principal.getAuthorities()
        );
    }
}

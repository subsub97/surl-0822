package com.megastudy.surlkdh.domain.auth.service.dto;

import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.infrastructure.security.MemberPrincipal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class AuthenticatedMember {

    private final Long id;
    private final Department department;
    private final Collection<? extends GrantedAuthority> authorities;


    public static AuthenticatedMember from(MemberPrincipal principal) {
        return new AuthenticatedMember(
                principal.getId(),
                principal.getDepartment(),
                principal.getAuthorities()
        );
    }
}

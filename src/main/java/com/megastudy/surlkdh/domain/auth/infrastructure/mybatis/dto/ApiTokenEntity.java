package com.megastudy.surlkdh.domain.auth.infrastructure.mybatis.dto;

import com.megastudy.surlkdh.domain.auth.entity.ApiToken;
import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiTokenEntity {
    private Long apiTokenId;
    private Long memberId;
    private String tokenName;
    private String tokenValue;
    private String role;
    private String department;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    /**
     * Domain Entity에서 MyBatis Entity로 변환
     */
    public static ApiTokenEntity from(ApiToken apiToken) {
        return ApiTokenEntity.builder()
                .apiTokenId(apiToken.getApiTokenId())
                .memberId(apiToken.getMemberId())
                .tokenName(apiToken.getTokenName())
                .tokenValue(apiToken.getTokenValue())
                .role(apiToken.getRole().getKey())
                .department(apiToken.getDepartment().name())
                .expiresAt(apiToken.getExpiresAt())
                .createdAt(apiToken.getCreatedAt())
                .updatedAt(apiToken.getUpdatedAt())
                .deletedAt(apiToken.getDeletedAt())
                .build();
    }

    /**
     * MyBatis Entity에서 Domain Entity로 변환
     */
    public ApiToken toModel() {
        return ApiToken.builder()
                .apiTokenId(apiTokenId)
                .memberId(memberId)
                .tokenName(tokenName)
                .tokenValue(tokenValue)
                .role(Role.fromKey(role))
                .department(
                        Department.fromName(department))
                .expiresAt(expiresAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }
}
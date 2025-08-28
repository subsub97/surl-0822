package com.megastudy.surlkdh.domain.member.controller.dto.response;

import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Member;
import com.megastudy.surlkdh.domain.member.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MemberResponse {
    Long memberId;
    String email;
    Department department;
    Role role;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime deletedAt;


    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .department(member.getDepartment())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .deletedAt(member.getDeletedAt())
                .build();
    }
}

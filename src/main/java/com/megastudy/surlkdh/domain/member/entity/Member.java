package com.megastudy.surlkdh.domain.member.entity;

import com.megastudy.surlkdh.common.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTimeEntity {

    private Long memberId;
    private String email;
    private String password;
    private Department department;
    private Role role;

    public static Member from(String email, String password, Department department, Role role) {
        return Member.builder()
                .email(email)
                .password(password)
                .department(department)
                .role(role)
                .build();
    }

    public static Member from(Long memberId, String email, String password, Department department,
                              Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return Member.builder()
                .memberId(memberId)
                .email(email)
                .password(password)
                .department(department)
                .role(role)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public Member update(String email, String password, Department department, Role role) {
        if (email != null && !email.isEmpty()) {
            this.email = email;
        }
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
        if (department != null) {
            this.department = department;
        }
        if (role != null) {
            this.role = role;
        }
        this.updatedAt = LocalDateTime.now();

        return this;
    }

    public void delete(LocalDateTime now) {
        this.updatedAt = now;
        this.deletedAt = now;
    }
}

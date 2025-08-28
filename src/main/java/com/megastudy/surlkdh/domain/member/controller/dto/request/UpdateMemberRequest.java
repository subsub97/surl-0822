package com.megastudy.surlkdh.domain.member.controller.dto.request;

import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Role;
import lombok.Getter;

@Getter
public class UpdateMemberRequest {
    private String email;
    private String password;
    private Department department;
    private Role role;
}

package com.megastudy.surlkdh.domain.auth.controller.dto.reqeust;

import com.megastudy.surlkdh.domain.member.entity.Department;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {

    @Email
    @NotBlank
    @Size(min = 13, max = 50)
    private String email;

    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    private Department department;
}

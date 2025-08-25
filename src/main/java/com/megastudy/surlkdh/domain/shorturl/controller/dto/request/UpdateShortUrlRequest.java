package com.megastudy.surlkdh.domain.shorturl.controller.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.megastudy.surlkdh.domain.member.entity.Department;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateShortUrlRequest {

    @Pattern(regexp = "^[a-zA-Z0-9_-]{4,10}$", message = "Short code must be 4-10 alphanumeric characters, hyphens, or underscores.")
    private String shortCode;
    private String note;
    private LocalDateTime expiresAt;
    private Department department;
    private List<UpdateShortUrlRedirectRuleRequest> redirectRules;
}
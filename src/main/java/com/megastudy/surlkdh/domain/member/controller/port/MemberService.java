package com.megastudy.surlkdh.domain.member.controller.port;

import com.megastudy.surlkdh.domain.member.controller.dto.request.UpdateMemberRequest;
import com.megastudy.surlkdh.domain.member.controller.dto.response.MemberResponse;
import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {
    void saveMember(String email, String password, Department department, Role role);

    MemberResponse getMember(Long memberId);

    Page<MemberResponse> getMembers(Pageable pageable);

    MemberResponse updateMember(Long memberId, UpdateMemberRequest updateMemberRequest);

    void deleteMember(Long memberId);
}

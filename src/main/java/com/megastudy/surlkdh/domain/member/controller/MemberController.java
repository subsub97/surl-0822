package com.megastudy.surlkdh.domain.member.controller;

import com.megastudy.surlkdh.common.api.ApiResponse;
import com.megastudy.surlkdh.domain.audit.aop.AuditLog;
import com.megastudy.surlkdh.domain.member.controller.dto.request.UpdateMemberRequest;
import com.megastudy.surlkdh.domain.member.controller.dto.response.MemberResponse;
import com.megastudy.surlkdh.domain.member.controller.port.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "회원 관리 API (관리자 전용) - 비밀번호 재설정 제외")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members/")
@PreAuthorize("hasAuthority('ADMIN')")
public class MemberController {

    private final MemberService memberService;

    @AuditLog(action = "VIEW_MEMBER", resourceType = "MEMBER")
    @Operation(
            summary = "회원 정보 단건 조회",
            description = "회원 ID로 회원 정보를 조회합니다. 관리자(ADMIN)만 접근할 수 있습니다."
    )
    @GetMapping("/{memberId}")
    public ApiResponse<MemberResponse> getMember(@PathVariable Long memberId) {
        return ApiResponse.ok(memberService.getMember(memberId));
    }

    @AuditLog(action = "VIEW_MEMBERS", resourceType = "MEMBER")
    @Operation(
            summary = "회원 정보 목록 조회",
            description = "모든 회원 정보를 조회합니다. 관리자(ADMIN)만 접근할 수 있습니다."
    )
    @GetMapping("/members")
    public ApiResponse<Page<MemberResponse>> getMembers(@PageableDefault(size = 10, sort = "created_at", direction = Sort.Direction.DESC) org.springframework.data.domain.Pageable pageable) {
        Page<MemberResponse> members = memberService.getMembers(pageable);
        return ApiResponse.ok(members);

    }

    @AuditLog(action = "UPDATE_MEMBER", resourceType = "MEMBER")
    @Operation(
            summary = "회원 정보 수정",
            description = "회원 ID로 회원 정보를 수정합니다. 관리자(ADMIN)만 접근할 수 있습니다."
    )
    @PutMapping("/{memberId}")
    public ApiResponse<MemberResponse> updateMember(@PathVariable Long memberId, @RequestBody UpdateMemberRequest updateMemberRequest) {
        return ApiResponse.ok(memberService.updateMember(memberId, updateMemberRequest));
    }

    @AuditLog(action = "DELETE_MEMBER", resourceType = "MEMBER")
    @Operation(
            summary = "회원 정보 삭제",
            description = "회원 ID로 회원 정보를 삭제합니다. 관리자(ADMIN)만 접근할 수 있습니다."
    )
    @DeleteMapping("/{memberId}")
    public ApiResponse<Void> deleteMember(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return ApiResponse.ok(null);
    }
}

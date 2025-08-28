package com.megastudy.surlkdh.domain.member.service;

import com.megastudy.surlkdh.common.exception.BusinessException;
import com.megastudy.surlkdh.domain.audit.aop.AuditContext;
import com.megastudy.surlkdh.domain.member.controller.dto.request.UpdateMemberRequest;
import com.megastudy.surlkdh.domain.member.controller.dto.response.MemberResponse;
import com.megastudy.surlkdh.domain.member.controller.port.MemberService;
import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Member;
import com.megastudy.surlkdh.domain.member.entity.Role;
import com.megastudy.surlkdh.domain.member.exception.MemberErrorCode;
import com.megastudy.surlkdh.domain.member.service.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditContext auditContext;

    @Transactional
    public void saveMember(String email, String password, Department department, Role role) {

        Member newMember = Member.from(
                email,
                password,
                department,
                role
        );

        memberRepository.save(newMember);
        auditContext.setResourceId(newMember.getMemberId());
        log.info("New member saved: {}", newMember.getEmail());
    }

    @Override
    public MemberResponse getMember(Long memberId) {
        auditContext.setResourceId(memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.NOT_FOUND));
        return MemberResponse.from(member);
    }

    @Override
    public Page<MemberResponse> getMembers(Pageable pageable) {
        log.info("회원 목록 조회 요청 : pageNumber={}, pageSize={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Member> members = memberRepository.findAll(pageable);
        return members.map(MemberResponse::from);
    }

    @Override
    public MemberResponse updateMember(Long memberId, UpdateMemberRequest request) {
        auditContext.setResourceId(memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.NOT_FOUND));

        member.update(
                request.getEmail(),
                request.getPassword() == null ? null : passwordEncoder.encode(request.getPassword()),
                request.getDepartment(),
                request.getRole()
        );

        Member updatedMember = memberRepository.update(member).orElseThrow(() -> new BusinessException(MemberErrorCode.UPDATE_FAILED));
        return MemberResponse.from(updatedMember);
    }

    @Override
    public void deleteMember(Long memberId) {
        auditContext.setResourceId(memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.NOT_FOUND));

        LocalDateTime deleteTime = LocalDateTime.now();

        member.delete(deleteTime);
        memberRepository.update(member);
    }
}

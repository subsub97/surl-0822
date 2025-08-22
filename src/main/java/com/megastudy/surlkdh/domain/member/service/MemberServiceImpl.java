package com.megastudy.surlkdh.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.megastudy.surlkdh.domain.member.controller.port.MemberService;
import com.megastudy.surlkdh.domain.member.entity.Member;
import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Role;
import com.megastudy.surlkdh.domain.member.service.port.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;

	@Transactional
	public void saveMember(String email, String password, Department departmentName, Role role) {

		Member newMember = Member.from(
			email,
			password,
			departmentName,
			role
		);

		memberRepository.save(newMember);
		log.info("New member saved: {}", newMember.getEmail());
	}
}

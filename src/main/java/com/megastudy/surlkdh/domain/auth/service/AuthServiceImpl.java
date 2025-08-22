package com.megastudy.surlkdh.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.megastudy.surlkdh.domain.auth.controller.dto.reqeust.SignUpRequest;
import com.megastudy.surlkdh.domain.auth.controller.port.AuthService;
import com.megastudy.surlkdh.domain.member.controller.port.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final MemberService memberService;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void signUp(SignUpRequest request) {
		String encryptedPassword = passwordEncoder.encode(request.getRawPassword());

		memberService
			.saveMember(
				request.getEmail(),
				encryptedPassword,
				request.getDepartment(),
				request.getRole()
			);
	}
}

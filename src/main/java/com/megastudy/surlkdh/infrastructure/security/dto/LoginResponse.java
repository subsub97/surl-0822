package com.megastudy.surlkdh.infrastructure.security.dto;

import com.megastudy.surlkdh.infrastructure.security.MemberPrincipal;

public record LoginResponse(String message, MemberInfo user, String accessToken) {
	public static LoginResponse success(MemberPrincipal member, String accessToken) {
		return new LoginResponse("로그인 성공",
			new MemberInfo(member.getId(), member.getUsername()), accessToken);
	}

	public static LoginResponse failure() {
		return new LoginResponse("로그인 실패", null, null);
	}

	public record MemberInfo(Long id, String username) {
	}
}
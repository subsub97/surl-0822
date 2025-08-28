package com.megastudy.surlkdh.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
	ADMIN("ADMIN", "관리자"),
	LEADER("LEADER", "리더"),
	EMPLOYEE("EMPLOYEE", "사원");

	private final String key;
	private final String title;

	public static Role fromKey(String key) {
		for (Role role : Role.values()) {
			if (role.getKey().equalsIgnoreCase(key)) {
				return role;
			}
		}
		throw new IllegalArgumentException("Unknown role: " + key);
	}
}
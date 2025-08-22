package com.megastudy.surlkdh.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Department {
	ADMIN("ADMIN"),
	DEVELOPMENT("DEVELOPMENT"),
	MARKETING("MARKETING"),
	SALES("SALES"),
	HR("HR"),
	FINANCE("FINANCE");

	private final String name;

	public static Department fromName(String name) {
		for (Department department : Department.values()) {
			if (department.getName().equalsIgnoreCase(name)) {
				return department;
			}
		}
		throw new IllegalArgumentException("Unknown department: " + name);
	}
}
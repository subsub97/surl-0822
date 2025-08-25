package com.megastudy.surlkdh.domain.member.controller.port;

import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Role;

public interface MemberService {
	void saveMember(String email, String password, Department department, Role role);
}

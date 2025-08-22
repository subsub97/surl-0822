package com.megastudy.surlkdh.domain.auth.controller.port;

import com.megastudy.surlkdh.domain.auth.controller.dto.reqeust.SignUpRequest;

public interface AuthService {
	void signUp(SignUpRequest signUpRequest);
}

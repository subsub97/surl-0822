package com.megastudy.surlkdh.domain.member.controller;

import org.springframework.web.bind.annotation.RestController;

import com.megastudy.surlkdh.domain.member.controller.port.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberController {

	private final MemberService memberService;

	// TODO : 회원정보 관련 CRUD 개발 시 작성
}

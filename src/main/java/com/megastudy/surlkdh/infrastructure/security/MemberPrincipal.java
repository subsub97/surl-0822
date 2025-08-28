package com.megastudy.surlkdh.infrastructure.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Member;

import lombok.Getter;

@Getter
public class MemberPrincipal implements UserDetails {

	private final Long id;
	private final String username;
	private final String password;
	private final Department department;
	private final Collection<? extends GrantedAuthority> authorities;

	private MemberPrincipal(Long id,
		String username,
		String password,
		Department department,
		Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.department = department;
		this.authorities = authorities;
	}

	private MemberPrincipal(Long id, String username, Department department,
		Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.password = null;
		this.department = department;
		this.authorities = authorities;
	}

	public static MemberPrincipal from(Member member) {
		List<SimpleGrantedAuthority> auths = List.of(new SimpleGrantedAuthority(member.getRole().getKey()));

		return new MemberPrincipal(
			member.getMemberId(),
			member.getEmail(),
			member.getPassword(),
			member.getDepartment(),
			auths
		);
	}

	public static MemberPrincipal of(Long id, String username, Department department,
		Collection<? extends GrantedAuthority> authorities) {
		return new MemberPrincipal(id, username, department, authorities);
	}

	@Override
	public boolean isAccountNonExpired() {
		return UserDetails.super.isAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return UserDetails.super.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return UserDetails.super.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return UserDetails.super.isEnabled();
	}
}
package com.megastudy.surlkdh.infrastructure.security.jwt;

import static com.megastudy.surlkdh.domain.auth.entity.UserType.*;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.megastudy.surlkdh.domain.member.entity.Department;
import com.megastudy.surlkdh.domain.member.entity.Role;
import com.megastudy.surlkdh.infrastructure.security.MemberPrincipal;
import com.megastudy.surlkdh.infrastructure.security.jwt.exception.TokenErrorCode;
import com.megastudy.surlkdh.common.exception.BusinessException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	private final Key key;
	private static Long ONE_DAY = 60 * 60 * 1000 * 24L; // 1일
	private static Long ONE_WEEK = ONE_DAY * 14L; // 2주

	private static final String CLAIM_ROLE = "role";
	private static final String CLAIM_ADD = "add";
	private static final String CLAIM_DEPARTMENT = "department";

	public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * 유저 정보를 이용해서 AccessToken과 RefreshToken을 생성
	 */
	public JwtToken generateToken(Authentication authentication, MemberPrincipal memberPrincipal) {
		String authoritiesCsv = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		Long now = (new Date()).getTime();
		Date issuedAt = new Date();

		String accessToken = createAccessToken(memberPrincipal, authoritiesCsv, now, issuedAt);
		String refreshToken = createRefreshToken(memberPrincipal, authoritiesCsv, now, issuedAt);

		return JwtToken.builder()
			.grantType("Bearer")
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	public Authentication getAuthentication(String token) {
		Claims claims = parseClaims(token);

		String username = claims.getSubject();

		if (username == null || username.isBlank()) {
			Object aud = claims.get("aud");
			if (aud instanceof String s) {
				username = s;
			} else if (aud instanceof Collection<?> c && !c.isEmpty()) {
				Object first = c.iterator().next();
				username = (first != null) ? first.toString() : null;
			}
		}

		// 2) 권한 CSV 파싱
		String authCsv = claims.get(CLAIM_ROLE, String.class);
		if (authCsv == null || authCsv.isBlank()) {
			throw new BusinessException(TokenErrorCode.UNAUTHORIZED);
		}
		Collection<? extends GrantedAuthority> authorities = Arrays.stream(authCsv.split(","))
			.map(String::trim)
			.filter(s -> !s.isEmpty())
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());

		Long memberId = getMemberIdFromToken(token);
		Department department = getDepartmentFromToken(token);

		MemberPrincipal memberPrincipal = MemberPrincipal.of(memberId, JWT.getType(), department, authorities);
		return new UsernamePasswordAuthenticationToken(memberPrincipal, token, authorities);
	}

	// JWT 유효성 검증 메서드
	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith((SecretKey)key)
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (JwtException e) {
			log.debug("Invalid JWT: {}", e.getMessage());
			return false;
		}
	}

	public JwtToken getRefreshToken(String refreshToken) {
		try {
			Authentication authentication = getAuthentication(refreshToken);
			// TODO : redis 사용시 레디스에 저장된 refreshToken 가져오기
			// refreshToken 가져오는 코드 작성하기

			JwtToken refreshGetToken = null;
			return refreshGetToken;
		} catch (Exception e) {
			throw new JwtException(e.getMessage());
		}
	}

	public Long getMemberIdFromToken(String token) {
		Claims claims = parseClaims(token);
		return Long.parseLong(claims.getSubject());
	}

	public Department getDepartmentFromToken(String token) {
		Claims claims = parseClaims(token);
		return Department.valueOf(claims.get(CLAIM_DEPARTMENT, String.class));
	}

	public Role getRoleFromToken(String token) {
		Claims claims = parseClaims(token);
		return Role.fromKey(claims.get(CLAIM_ROLE, String.class));
	}

	private static Map<String, Object> createHeader() {
		Map<String, Object> headers = new HashMap<>();
		headers.put("alg", "HS256");
		headers.put("typ", "JWT");
		return headers;
	}

	private String createAccessToken(MemberPrincipal memberPrincipal, String authoritiesCsv, long currentTime,
		Date issuedAt) {
		return Jwts.builder()
			.header().add(createHeader()).and()
			.issuer("temp") // 미정
			.subject(memberPrincipal.getId().toString())
			.audience().add("surl-api").and()
			.claim(CLAIM_ROLE, authoritiesCsv)
			.claim(CLAIM_DEPARTMENT, memberPrincipal.getDepartment().name())
			.issuedAt(issuedAt)
			.expiration(new Date(currentTime + ONE_DAY))
			.signWith(key)
			.compact();
	}

	private String createRefreshToken(MemberPrincipal memberPrincipal, String authoritiesCsv, long currentTime,
		Date issuedAt) {
		return Jwts.builder()
			.header().add(createHeader()).and()
			.issuer("temp") // 미정
			.subject(memberPrincipal.getId().toString())
			.audience().add("surl-api").and()
			.claim(CLAIM_ROLE, authoritiesCsv)
			.claim(CLAIM_ADD, "ref") // refresh token을 구분하기 위한 claim
			.claim(CLAIM_DEPARTMENT, memberPrincipal.getDepartment().name())
			.issuedAt(issuedAt)
			.expiration(new Date(currentTime + ONE_WEEK))
			.signWith(key)
			.compact();
	}

	private Claims parseClaims(String token) {
		try {
			return Jwts.parser()
				.verifyWith((SecretKey)key)
				.build()
				.parseSignedClaims(token)
				.getPayload();

		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

}
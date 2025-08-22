package com.megastudy.surlkdh.domain.audit.entity;

import java.time.LocalDateTime;

import com.megastudy.surlkdh.common.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAudits extends BaseTimeEntity {
	private Long auditId;
	private String resourceType;
	private String resourceId;
	private String action;
	private String actorType;
	private Long actorId;

	public static AdminAudits create(String resourceType, String resourceId, String action,
		String actorType, Long actorId) {
		return AdminAudits.builder()
			.resourceType(resourceType)
			.resourceId(resourceId)
			.action(action)
			.actorType(actorType)
			.actorId(actorId)
			.build();
	}
}

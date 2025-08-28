package com.megastudy.surlkdh.common;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseTimeEntity {
	protected LocalDateTime createdAt;
	protected LocalDateTime updatedAt;
	protected LocalDateTime deletedAt;
}

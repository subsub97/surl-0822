package com.megastudy.surlkdh.domain.shorturl.util;

public interface ShortCodeUtil {

	String encode(long id);

	long decode(String shortCode);
}
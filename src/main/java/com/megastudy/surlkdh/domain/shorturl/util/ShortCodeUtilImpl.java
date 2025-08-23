package com.megastudy.surlkdh.domain.shorturl.util;

import org.springframework.stereotype.Component;

@Component
public class ShortCodeUtilImpl implements ShortCodeUtil {

	@Override
	public String encode(long id) {
		return Base62Util.encode(id);
	}

	@Override
	public long decode(String shortCode) {
		return Base62Util.decode(shortCode);
	}
}
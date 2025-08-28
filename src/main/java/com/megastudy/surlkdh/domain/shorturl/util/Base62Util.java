package com.megastudy.surlkdh.domain.shorturl.util;

public class Base62Util {

	private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public static String encode(long num) {
		if (num == 0)
			return "0";
		StringBuilder sb = new StringBuilder();

		long x = num;

		while (x > 0) {
			int rem = (int)(x % 62);
			sb.append(ALPHABET.charAt(rem));
			x = x / 62;
		}
		return sb.reverse().toString();
	}

	public static long decode(String str) {
		long num = 0;
		for (char c : str.toCharArray()) {
			num = num * 62 + ALPHABET.indexOf(c);
		}
		return num;
	}
}
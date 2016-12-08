package fr.skyost.serialkey.utils;

public class ROT47 {
	
	public static final String rotate(final String value) {
		final int length = value.length();
		final StringBuilder result = new StringBuilder();
		for(int i = 0; i < length; i++) {
			char c = value.charAt(i);
			if(c != ' ') {
				c += 47;
				if(c > '~') {
					c -= 94;
				}
			}
			result.append(c);
		}
		return result.toString();
	}
	
}
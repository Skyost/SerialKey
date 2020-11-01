package fr.skyost.serialkey.core.util;

/**
 * <a href="https://en.wikipedia.org/wiki/ROT13#Variants">ROT47</a> algorithm implementation.
 */

public class ROT47 {

	/**
	 * Rotates a string.
	 *
	 * @param value The string.
	 *
	 * @return The rotated string.
	 */
	
	public static String rotate(final String value) {
		final StringBuilder result = new StringBuilder();
		final int n = value.length();
		for(int i = 0; i < n; i++) {
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
package fr.skyost.serialkey.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;

public class Utils {
	
	public static final ChatColor randomChatColor(final ChatColor... exclude) {
		final List<ChatColor> excludeList = Arrays.asList(exclude);
		final ChatColor[] values = ChatColor.values();
		final Random random = new Random();
		while(true) {
			final ChatColor randomColor = values[random.nextInt(values.length)];
			if(!excludeList.contains(randomColor)) {
				return randomColor;
			}
		}
	}

}

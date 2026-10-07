package com.hedario.areaforge;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;

import com.hedario.areaforge.configuration.ConfigManager;

import net.md_5.bungee.api.ChatColor;

public class Methods {
	
	public static String formatColors(final String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	public static void sendMessage(final CommandSender sender, final String message, final boolean prefix) {
		if (sender == null || message == null) {
			return;
		}
		sender.sendMessage(formatColors((prefix ? getPrefix() : "") +  message));
	}
	
	public static String setPlaceholder(final String string, final String placeholder, final Object value) {
		return string.replace(placeholder, String.valueOf(value));
	}
	
	public static String setPlaceholders(String string, final Map<String, Object> placeholders) {
		for (Entry<String, Object> entry : placeholders.entrySet()) {
			string = string.replace(entry.getKey(), String.valueOf(entry.getValue()));
		}
		return string;
	}
	
	public static String getPrefix() {
		return ConfigManager.get().getString("Language.Messages.Prefix");
	}
	
	public static String formatTime(final long time) {
		long hours = (time / 3600000);
		long minutes = (time / 60000) % 60;
		long seconds = (time / 1000) % 60;
		long millis = time % 1000;
		return "" + (hours > 0 ? hours + "h " : "") + (minutes > 0 ? minutes + "m " : "") + (seconds > 0 ? seconds + "s " : "") + (millis > 0 ? millis + "ms" : "");
	}
	
	public static long parseTime(final String time) {
		if (!isValidTimeFormat(time)) {
			return 0;
		}
		String[] parts = time.split(":");

		int hours = Integer.parseInt(parts[0]);
		int minutes = Integer.parseInt(parts[1]);
		int seconds = Integer.parseInt(parts[2]);
		long millis = Long.parseLong(parts[3]);
		return (hours * 3600000) + (minutes * 60000) + (seconds * 1000) + millis;
	}
	
	public static boolean isValidTimeFormat(final String time) throws IllegalArgumentException, NumberFormatException {
		String[] parts = time.split(":");
		if (parts.length != 4) {
			return false;
		}
		try {
			Integer.parseInt(parts[0]);
			Integer.parseInt(parts[1]);
			Integer.parseInt(parts[2]);
			Long.parseLong(parts[3]);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
		
	}
}

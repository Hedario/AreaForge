package com.hedario.areaforge.commands;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hedario.areaforge.configuration.ConfigManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class AFCommand implements Command {
	private final String name, usage, description;
	private final String[] aliases;
	public static Map<String, AFCommand> commands = new HashMap<String, AFCommand>();

	public AFCommand(final String name, final String usage, final String description, final String[] aliases) {
		this.name = name;
		this.usage = usage;
		this.description = description;
		this.aliases = aliases;
		commands.put(name, this);
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public String getPermission() {
		return "areaforge.command." + this.name;
	}

	public String getUsage() {
		return this.usage;
	}

	public String[] getAliases() {
		return this.aliases;
	}
	
	public String getPrefix() {
		return ConfigManager.get().getString("Language.Messages.Prefix");
	}
	
	public boolean isPlayer(final CommandSender sender) {
		if (!(sender instanceof Player)) {
			sendMessage(sender, ConfigManager.get().getString("Language.Messages.Must_be_player"));
			return false;
		}
		return true;
	}

	public boolean hasPermission(final CommandSender sender) {
		if (!sender.hasPermission(getPermission())) {
			sendMessage(sender, ConfigManager.get().getString("Language.Messages.No_permission"));
			return false;
		}
		return true;
	}
	
	public boolean hasPermission(final CommandSender sender, final String extra) {
		if (!sender.hasPermission(getPermission() + "." + extra)) {
			sendMessage(sender, ConfigManager.get().getString("Language.Messages.No_permission"));
			return false;
		}
		return true;
	}
	
	public boolean isCorrectLength(final CommandSender sender, final int min, final int max, final int size) {
		if (size < min || size > max) {
			sendMessage(sender, getWrongUsage(sender));
			return false;
		}
		return true;
	}
	
	protected String getWrongUsage(final CommandSender sender) {
		return ConfigManager.get().getString("Language.Messages.Wrong_arguments");
	}
	
	public void sendMessage(final CommandSender sender, final String message, final boolean prefix) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix ? getPrefix() + message : message));
	}
	
	public void sendMessage(final CommandSender sender, final String message) {
		sendMessage(sender, message, true);
	}
	
	protected boolean isNumeric(String id) {
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(id, pos);
		return id.length() == pos.getIndex();
	}

	protected List<String> getPage(List<String> entries, int page, boolean sort) {
		List<String> strings = new ArrayList<>();
		if (sort) {
			Collections.sort(entries);
		}
		if (page < 1) {
			page = 1;
		}
		if (page * 8 - 8 >= entries.size()) {
			page = Math.round(entries.size() / 8) + 1;
			if (page < 1) {
				page = 1;
			}
		}
		strings.add(ChatColor.translateAlternateColorCodes('&', ("&8&m-----&e AreaForge &7- &8[&7" + page + "&8/" + "&6" + (int) Math.ceil((entries.size() + 0.0D) / 8.0D) + "&8] &m-----&r")));
		if (entries.size() > page * 8 - 8) {
			for (int i = page * 8 - 8; i < entries.size(); i++) {
				if (entries.get(i) != null) {
					strings.add(entries.get(i));
				}
				if (i >= page * 8 - 1) {
					break;
				}
			}
		}
		return strings;
	}
	
	protected List<TextComponent> getPage(List<TextComponent> entries, int page) {
		List<TextComponent> strings = new ArrayList<>();
		if (page < 1) {
			page = 1;
		}
		if (page * 8 - 8 >= entries.size()) {
			page = Math.round(entries.size() / 8) + 1;
			if (page < 1) {
				page = 1;
			}
		}
		strings.add(new TextComponent(ChatColor.translateAlternateColorCodes('&', ("&6&m-----&e AreaForge &7- &8[&e" + page + "&8/&6" + (int) Math.ceil(entries.size()/ 8.0D) + "&8] &6&m-----&r"))));
		if (entries.size() > page * 8 - 8) {
			for (int i = page * 8 - 8; i < entries.size(); i++) {
				if (entries.get(i) != null) {
					strings.add(entries.get(i));
				}
				if (i >= page * 8 - 1) {
					break;
				}
			}
		}
		return strings;
	}

}

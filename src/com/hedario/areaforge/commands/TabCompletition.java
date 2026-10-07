package com.hedario.areaforge.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TabCompletition implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1) {
			return getCommands(sender, false);
		}
		if (args.length > 1) {
			// map command's aliases
			for (AFCommand cmd : AFCommand.commands.values()) {
				for (int i = 0; i < cmd.getAliases().length; i++) {
					if (cmd.getAliases()[i].equalsIgnoreCase(args[0])) {
						args[0] = cmd.getName();
						return getCompletitions(sender, args);
					}
				}
			}
			return getCompletitions(sender, args);
		}
		return new ArrayList<String>();
	}

	private List<String> getCompletitions(CommandSender sender, String[] args) {
		String command = args[0];
		AFCommand cmd = AFCommand.commands.get(command);
		if (cmd == null) {
			return new ArrayList<String>();
		}
		return cmd.getTabCompletion(sender, Arrays.asList(args));
	}

	private List<String> getCommands(CommandSender sender, boolean filter) {
		Set<String> instances = new HashSet<>(AFCommand.commands.keySet());
		instances.remove("confirm");
		List<String> commands = new ArrayList<String>();
		for (String key : instances) {
			if (filter) {
				if (sender.hasPermission("areaforge.command." + key)) {
					commands.add(key);
				}
			} else {
				commands.addAll(instances);
				return commands;
			}
		}
		return commands;

	}

}

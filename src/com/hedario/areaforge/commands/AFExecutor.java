package com.hedario.areaforge.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import com.hedario.areaforge.AreaForge;
import com.hedario.areaforge.Methods;

public class AFExecutor {
	public static String[] aliases = { "af", "areaforge", "area" };
	public static List<String> helpLines;

	public static void init() {
		PluginCommand af = AreaForge.getInstance().getCommand("af");
		new HelpCommand();
		new CreateCommand();
		new LoadCommand();
		new ListCommand();
		new DeleteCommand();
		new InfoCommand();
		new ConfirmCommand();
		new ReloadCommand();
		new CancelCommand();
		new VersionCommand();
		new SettingsCommand();
		CommandExecutor executor = new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if ((args.length == 0) && (Arrays.asList(aliases).contains(label.toLowerCase()))) {
					AFCommand.commands.get("help").execute(sender, new ArrayList<>());
					return true;
				}
				List<String> sent = Arrays.asList(args).subList(1, args.length);
				for (AFCommand cmd : AFCommand.commands.values()) {
					if (Arrays.asList(cmd.getAliases()).contains(args[0].toLowerCase())) {
						try {
							cmd.execute(sender, sent);
						} catch (Exception e) {
							Methods.sendMessage(sender, "&cSomething wrong happened while executing this command!", true);
							e.printStackTrace();
							return false;
						}
						return true;
					}
				}
				return false;
			}
		};
		af.setExecutor(executor);
		af.setTabCompleter(new TabCompletition());
	}
}

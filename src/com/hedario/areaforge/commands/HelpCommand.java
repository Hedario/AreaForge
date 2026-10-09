package com.hedario.areaforge.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.hedario.areaforge.configuration.ConfigManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class HelpCommand extends AFCommand {

	public HelpCommand() {
		super("help", "/af help [page, topic]", ConfigManager.get().getString("Language.Commands.Help.Description"), new String[] { "help", "h" });
	}

	@SuppressWarnings("deprecation")
	public void execute(CommandSender sender, List<String> args) {
		if ((!hasPermission(sender))) {
			return;
		}
		List<TextComponent> comps = new ArrayList<TextComponent>();
		for (AFCommand cmd : AFCommand.commands.values()) {
			if (cmd.getName().equalsIgnoreCase("confirm")) {
				continue;
			}
			TextComponent text = new TextComponent(cmd.getUsage());
			if (!hasPermission(sender, this.getName())) {
				text.setColor(ChatColor.RED);
				text.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "You don't own enough permissions!")).create()));
			} else {
				text.setText(ChatColor.translateAlternateColorCodes('&', "&6" + text.getText().substring(0, 3) + "&e" + text.getText().substring(3, text.getText().length())));
				text.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Click for help on this command").create()));
				text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/af help " + cmd.getName()));
			}
			comps.add(text);
		}
		int n = 1;
		if (args.size() > 0) {
			if (isNumeric(args.get(0))) {
				n = Integer.parseInt(args.get(0));
			} else {
				if (AFCommand.commands.containsKey(args.get(0))) {
					AFCommand cmd = AFCommand.commands.get(args.get(0));
					this.sendMessage(sender, "&6&m---&e " + cmd.getName().substring(0, 1).toUpperCase() + cmd.getName().substring(1, cmd.getName().length()) + " command &6&m---", false);
					this.sendMessage(sender, "&6Usage: &e" + cmd.getUsage(), false);
					String aliases = cmd.getAliases()[0];
					for (int i = 1; i < cmd.getAliases().length; i++) {
						aliases += ", " + cmd.getAliases()[i];
					}
					this.sendMessage(sender, "&6Aliases: &e" + aliases, false);
					this.sendMessage(sender, "&7" + cmd.getDescription(), false);
					return;
				}
			}
		}
		for (TextComponent text : getPage(comps, n)) {
			sender.spigot().sendMessage(text);
		}
	}

	@Override
	public List<String> getTabCompletion(CommandSender sender, List<String> args) {
		List<String> sugg = new ArrayList<String>();
		if (args.size() > 0) {
			AFCommand.commands.keySet().forEach(name -> sugg.add(name));
		}
		return sugg;
	}
}

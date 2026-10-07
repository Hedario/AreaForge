package com.hedario.areaforge.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.hedario.areaforge.AreaForge;
import com.hedario.areaforge.Methods;
import com.hedario.areaforge.configuration.ConfigManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class VersionCommand extends AFCommand {

	public VersionCommand() {
		super("version", "/af version", ConfigManager.get().getString("Language.Commands.Version.Description"), new String[] {"version", "v"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!this.hasPermission(sender)) {
			return;
		}
		TextComponent text = new TextComponent(Methods.formatColors("&6&m-----&e AreaForge &6&m-----") + "\n");
		Text openLink = new Text(ChatColor.GRAY + "Click to open the link");
		TextComponent modrinth = new TextComponent(Methods.formatColors("&8&l[&a&lModrinth&8&l]"));
		TextComponent discord = new TextComponent(Methods.formatColors("&8&l[&d&lDISCORD&8&l]"));
		TextComponent github = new TextComponent(Methods.formatColors("&8&l[&f&lGITHUB&8&l]"));
		TextComponent paypal = new TextComponent(Methods.formatColors("&8&l[&b&lPAYPAL&8&l]"));
		
		text.addExtra(Methods.formatColors("&6Author: &eHedario\n"));
		text.addExtra(Methods.formatColors("&6Version: &7" + AreaForge.getInstance().getDescription().getVersion() + "\n"));
		text.addExtra(Methods.formatColors("&6Loading engine in use: &e" + AreaForge.getInstance().getAreaHandler().getEngine().getVersion() + "\n\n"));
		text.addExtra(Methods.formatColors("&6Pages: "));
		modrinth.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, openLink));
		modrinth.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/project/area-forge"));
		discord.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, openLink));
		discord.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/yqs9UJs"));
		github.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, openLink));
		github.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.paypal.me/Hetag1216"));
		text.addExtra(modrinth);
		text.addExtra(" ");
		text.addExtra(github);
		text.addExtra("\n\n");
		text.addExtra(Methods.formatColors("&6AreaForge is brought to you freely, if you wish to support my work and contribute to the plugin, you can consider making a donation through: "));
		paypal.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, openLink));
		paypal.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.paypal.me/Hetag1216"));
		text.addExtra(paypal);
		sender.spigot().sendMessage(text);
	}

	@Override
	public List<String> getTabCompletion(CommandSender sender, List<String> args) {
		return null;
	}

}

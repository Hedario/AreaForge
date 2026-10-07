package com.hedario.areaforge.commands;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.hedario.areaforge.Area;
import com.hedario.areaforge.AreaForge;
import com.hedario.areaforge.AreaHandler;
import com.hedario.areaforge.configuration.ConfigManager;
import com.hedario.areaforge.data.AreaForger;
import com.hedario.areaforge.storage.AreaRepository;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class ListCommand extends AFCommand {
	private AreaHandler handler;

	public ListCommand() {
		super("list", "/af list [page]", ConfigManager.get().getString("Language.Commands.List.Description"), new String[] {"list", "li"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !isCorrectLength(sender, 0, 1, args.size())) {
			return;
		}
		if (AreaRepository.getAreaNames().isEmpty()) {
			this.sendMessage(sender, ConfigManager.get().getString("Language.Messages.No_areas_found"));
			return;
		}
		handler = AreaForge.getInstance().getAreaHandler();
		List<TextComponent> comps = new ArrayList<TextComponent>();
		for (AreaForger forger : handler.getForger().values()) {
			TextComponent text = new TextComponent(ChatColor.GRAY + "- ");
			text.addExtra(ChatColor.RED + forger.getArea().getName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + NumberFormat.getInstance().format(forger.getProcessed()));
			text.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text(ChatColor.RED + "This area is still being created.")));
			comps.add(text);
		}
		for (Area area : AreaRepository.getAreas()) {
			TextComponent text = new TextComponent(ChatColor.GRAY + "- ");
			if (handler.isLoading(area)) {
				text.addExtra(ChatColor.RED + area.getName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + NumberFormat.getInstance().format(handler.getLoader().get(area.getName()).getProcessed()) + ChatColor.GRAY + "/" + ChatColor.GOLD + NumberFormat.getInstance().format(area.getBlocks()));
				text.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text(ChatColor.RED + "This area is currently being loaded.")));
			} else {
				text.addExtra(ChatColor.GRAY + (handler.isLoading(area) ? ChatColor.RED + "" : ChatColor.GOLD + "") + area.getName());
				text.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text(ChatColor.GRAY + "Click for more information.")));
				text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "af info " + area.getName()));
			}
			comps.add(text);
		}
		
		int n = 1;
		if (args.size() == 1 && isNumeric(args.get(0))) {
			n = Integer.valueOf(args.get(0));
		}
		for (TextComponent t : getPage(comps, n)) {
			sender.spigot().sendMessage(t);
		}
	}

	@Override
	public List<String> getTabCompletion(CommandSender sender, List<String> args) {
		return new ArrayList<>();
	}

}

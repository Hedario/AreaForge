package com.hedario.areaforge.commands;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.hedario.areaforge.Area;
import com.hedario.areaforge.AreaForge;
import com.hedario.areaforge.AreaHandler;
import com.hedario.areaforge.AreaScheduler;
import com.hedario.areaforge.Methods;
import com.hedario.areaforge.configuration.ConfigManager;
import com.hedario.areaforge.data.ScheduledArea;
import com.hedario.areaforge.storage.AreaRepository;
import com.hedario.areaforge.util.PendingConfirmation;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class InfoCommand extends AFCommand {
	AreaHandler handler;

	public InfoCommand() {
		super("info", "/af info <area>", ConfigManager.get().getString("Language.Commands.Info.Description"), new String[] {"info", "i"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!this.hasPermission(sender) || !this.isCorrectLength(sender, 1, 1, args.size())) {
			return;
		}
		if (PendingConfirmation.getConfirmations().containsKey(sender.getName())) {
			PendingConfirmation.getConfirmations().remove(sender.getName());
		}
		final String name = args.get(0);
		if (!AreaRepository.getAreaNames().contains(name)) {
			this.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Messages.Not_found"), "%area%", name));
			return;
		}
		handler = AreaForge.getInstance().getAreaHandler();
		final Area area = AreaRepository.getArea(name);
		ScheduledArea scheduledArea = AreaRepository.getScheduledArea(name);
		if (scheduledArea == null) {
			scheduledArea = new ScheduledArea(name, false, "01:00:00:000");
		}
		TextComponent text = new TextComponent(Methods.formatColors("&6&m-----&e AreaForge &6&m-----") + "\n");
		TextComponent first = new TextComponent(Methods.formatColors("&6First bound: &7" + area.getMinX() + ", " +  area.getMinY() + ", " + area.getMinZ()) + "\n");
		TextComponent second = new TextComponent(Methods.formatColors("&6Second bound: &7" + area.getMaxX() + ", " +  area.getMaxY() + ", " + area.getMaxZ()) + "\n");
		TextComponent autoLoading = new TextComponent(scheduledArea.isEnabled() ? Methods.formatColors("&8&l[&a&lENABLED&8&l]") : Methods.formatColors("&8&l[&c&lDISABLED&8&l]"));
		TextComponent safeLocation = new TextComponent();
		TextComponent build = new TextComponent(Methods.formatColors("&8[&a&lLOAD&8]"));
		TextComponent delete = new TextComponent(Methods.formatColors("&8[&c&lDELETE&8]"));
		TextComponent cancel = new TextComponent(Methods.formatColors("&8[&6&lCANCEL&8]"));
		
		String status = "";
		if (handler.getForger().containsKey(area.getName())) {
			status += "&cCreating\n";
			status += "&6Blocks processed: &e" + NumberFormat.getInstance().format(handler.getForger().get(area.getName()).getProcessed());
		} else if (handler.getLoader().containsKey(area.getName())) {
			status += "&aLoading\n";
			status += "&6Blocks processed: &e" + NumberFormat.getInstance().format(handler.getLoader().get(area.getName()).getProcessed()) + "&7/&6" + NumberFormat.getInstance().format(area.getBlocks());
		} else {
			status += "&7Idle";
		}
		status += "\n";
		
		
		text.addExtra(Methods.formatColors("&6Name: &7" + area.getName()) + "\n");
		
		first.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Methods.formatColors("&7Click to teleport to this location"))));
		first.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "minecraft:teleport " + sender.getName() + " " + area.getMinX() + " " +  area.getMinY() + " " + area.getMinZ()));
		text.addExtra(first);
		
		second.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Methods.formatColors("&7Click to teleport to this location"))));
		second.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "minecraft:teleport " + sender.getName() + " " + area.getMaxX() + " " +  area.getMaxY() + " " + area.getMaxZ()));
		text.addExtra(second);
		
		text.addExtra(Methods.formatColors("&6Total blocks: &7" + NumberFormat.getInstance().format(area.getBlocks())) + "\n");
		text.addExtra(Methods.formatColors("&6Saved containers: &7" + (area.isSaveContainers() ? "Yes" : "No") + "\n"));
		text.addExtra(Methods.formatColors("&6Saved entities: &7" + (area.isSaveEntities() ? "Yes" : "No") + "\n"));
		text.addExtra(Methods.formatColors("&6Created at: &7" + area.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))) + "\n\n");
		
		text.addExtra(Methods.formatColors("&6Status: " + status));
		text.addExtra(Methods.formatColors("&6Automatically loading: "));
		autoLoading.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "af confirm auto_load " + name));
		autoLoading.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Methods.formatColors("&7Click to enable/disable automatic loading"))));
		text.addExtra(autoLoading);
		text.addExtra("\n");
		text.addExtra(Methods.formatColors("&6Automatic loading interval: &7" + scheduledArea.getTime() + "\n"));
		ScheduledArea running = AreaScheduler.getAreas().get(name);
		if (running != null) {
			long time = (running.getParsedTime() + running.getLastLoad()) - System.currentTimeMillis();
			text.addExtra(Methods.formatColors("&6Next load: &7" + Methods.formatTime(time) + "\n"));
		}
		Location loc = AreaRepository.getSafeLocation(name);
		if (loc != null) {
			text.addExtra(Methods.formatColors("&6Safe location: "));
			safeLocation.addExtra(Methods.formatColors("&7" + loc.getWorld().getName() + ", " + String.format("%.2f", loc.getX()) + ", " + String.format("%.2f", loc.getY()) + ", " + String.format("%.2f",loc.getZ()) + "\n"));
			safeLocation.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Methods.formatColors("&7Click to teleport to this location"))));
			safeLocation.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "minecraft:teleport " + sender.getName() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ()));
			text.addExtra(safeLocation);
		}
		
		text.addExtra(Methods.formatColors("&6Loading engine in use: &e" + AreaForge.getInstance().getAreaHandler().getEngine().getVersion() + "\n\n"));
		
		build.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Methods.formatColors("&7Click to build the area"))));
		build.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "af confirm load " + name));
		text.addExtra(build);
		text.addExtra("   ");
		
		delete.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Methods.formatColors("&7Click to delete the area"))));
		delete.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "af confirm delete " + name));
		text.addExtra(delete);
		text.addExtra("   ");
		
		cancel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Methods.formatColors("&7Click to cancel the loading of the area"))));
		cancel.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "af cancel " + name));
		text.addExtra(cancel);
		
		sender.spigot().sendMessage(text);
	}

	@Override
	public List<String> getTabCompletion(CommandSender sender, List<String> args) {
		return AreaRepository.getAreaNames();
	}

}

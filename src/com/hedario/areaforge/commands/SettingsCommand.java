package com.hedario.areaforge.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hedario.areaforge.Area;
import com.hedario.areaforge.AreaScheduler;
import com.hedario.areaforge.Methods;
import com.hedario.areaforge.configuration.ConfigManager;
import com.hedario.areaforge.storage.AreaRepository;

public class SettingsCommand extends AFCommand {

	public SettingsCommand() {
		super("settings", "/af settings", ConfigManager.get().getString("Language.Commands.Settings.Description"), new String[] { "settings", "s" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!this.hasPermission(sender) || !this.isCorrectLength(sender, 3, 3, args.size())) {
			return;
		}
		if (AreaRepository.getAreas().isEmpty()) {
			this.sendMessage(sender, ConfigManager.get().getString("Language.Messages.No_areas_found"));
			return;
		}
		final String name = args.get(0);
		Area area = AreaRepository.getArea(name);
		if (area == null) {
			this.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Messages.Not_found"), "%area%", name));
			return;
		}
		final String setting = args.get(1);
		switch (setting.toLowerCase()) {
		case "location":
			if (!isPlayer(sender)) {
				return;
			}
			setLocation(name, (Player) sender);
			break;
		case "auto_load":
			enable(sender, name, args.get(2));
			break;
		case "auto_time":
			updateTime(sender, name, args.get(2));
			break;
			default:
				this.sendMessage(sender, getWrongUsage(sender));
				break;
		}
	}
	
	private void setLocation(String area, Player player) {
		Location loc = player.getLocation();
		Location old = AreaRepository.getSafeLocation(area);
		if (old == null) {
			AreaRepository.setSafeLocation(area, player.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
			Methods.sendMessage(player, Methods.setPlaceholder(ConfigManager.get().getString("Language.Commands.Settings.Set_location"), "%area%", area), true);
		} else {
			AreaRepository.updateSafeLocation(area, player.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
			Methods.sendMessage(player, Methods.setPlaceholder(ConfigManager.get().getString("Language.Commands.Settings.Update_location"), "%area%", area), true);
		}
	}
	
	private void enable(final CommandSender sender, final String area, final String value) {
		boolean val = Boolean.valueOf(value);
		AreaScheduler.setEnabled(area, val);
		if (val == true) {
			Methods.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Commands.Settings.Enable"), "%area%", area), true);
		} else {
			Methods.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Commands.Settings.Disable"), "%area%", area), true);
		}
	}
	
	private void updateTime(final CommandSender sender, final String area, final String value) {
		if (!Methods.isValidTimeFormat(value)) {
			Methods.sendMessage(sender, "&cInvalid time format, expected: hh:mm:ss:sss", true);
			return;
		}
		AreaScheduler.setTime(area, value);
		Methods.sendMessage(sender,
				Methods.setPlaceholders(ConfigManager.get().getString("Language.Commands.Settings.Time"),
						Map.of("%time%", value, "%area%", area)),
				true);
	}

	@Override
	public List<String> getTabCompletion(CommandSender sender, List<String> args) {
		List<String> list = new ArrayList<>();
		if (args.size() == 2) {
			list.addAll(AreaRepository.getAreaNames());
		} else if (args.size() == 3) {
			list.add("location");
			list.add("auto_load");
			list.add("auto_time");
		} else if (args.size() == 4) {
			if (args.get(2).equals("auto_load")) {
				list.add("true");
				list.add("false");
			} else if (args.get(2).equals("auto_time")) {
				list.add("00:01:00:000");
			} else {
				list.add("set");
			}
		} else {
			return new ArrayList<String>();
		}
		return list;
	}

}

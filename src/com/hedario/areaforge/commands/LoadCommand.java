package com.hedario.areaforge.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.hedario.areaforge.Area;
import com.hedario.areaforge.AreaForge;
import com.hedario.areaforge.Methods;
import com.hedario.areaforge.configuration.ConfigManager;
import com.hedario.areaforge.data.AreaLoader;
import com.hedario.areaforge.storage.AreaRepository;

public class LoadCommand extends AFCommand {
	public LoadCommand() {
		super("load", "/af load <area>", ConfigManager.get().getString("Language.Commands.Load.Description"), new String[] {"load", "l"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!this.hasPermission(sender) || !this.isCorrectLength(sender, 1, 1, args.size())) {
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

		AreaLoader loader = new AreaLoader(sender, area);
		AreaForge.getInstance().getAreaHandler().queue(loader);
	}

	@Override
	public List<String> getTabCompletion(CommandSender sender, List<String> args) {
		return AreaRepository.getAreaNames();
	}

}

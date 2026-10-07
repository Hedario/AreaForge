package com.hedario.areaforge.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hedario.areaforge.Area;
import com.hedario.areaforge.Methods;
import com.hedario.areaforge.configuration.ConfigManager;
import com.hedario.areaforge.data.AreaForger;
import com.hedario.areaforge.storage.AreaRepository;
import com.hedario.areaforge.util.Selection;
import com.hedario.areaforge.util.Selector;

public class CreateCommand extends AFCommand {

	public CreateCommand() {
		super("create", "/af create <area> [save containers] [save entities]", ConfigManager.get().getString("Language.Commands.Create.Description"), new String[] { "create", "c" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender) || !this.isCorrectLength(sender, 1, 3, args.size())) {
			return;
		}
		final String name = args.get(0);
		final Player player = (Player) sender;
		if (name == null || name.isBlank()) {
			this.sendMessage(sender, ConfigManager.get().getString("Language.Commands.Create.Invalid"));
			return;
		}
		if (AreaRepository.getAreaNames().contains(name)) {
			this.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Commands.Create.Already_exists"), "%area%", name));
			return;
		}
		Selection selection = Selector.getSELECTIONS().get(player.getName());
		if (selection == null || !selection.isValid()) {
			this.sendMessage(sender, ConfigManager.get().getString("Language.Commands.Create.Invalid_selection"));
			return;
		}
		final AreaForger forger;
		if (args.size() == 2) {
			forger = new AreaForger(sender, new Area(name, selection), Boolean.valueOf(args.get(1)), true);
		} else if (args.size() == 3) {
			forger = new AreaForger(sender, new Area(name, selection), Boolean.valueOf(args.get(1)), Boolean.valueOf(args.get(2)));
		} else {
			forger = new AreaForger(sender, new Area(name, selection));
		}
		
		forger.start();
	}

	@Override
	public List<String> getTabCompletion(CommandSender sender, List<String> args) {
		if (args.size() == 3) {
			return new ArrayList<String>(List.of("true", "false"));
		}
		if (args.size() == 4) {
			return new ArrayList<String>(List.of("true", "false"));
		}
		return new ArrayList<>();
	}

}

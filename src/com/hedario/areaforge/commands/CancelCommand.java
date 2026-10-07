package com.hedario.areaforge.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.hedario.areaforge.AreaForge;
import com.hedario.areaforge.AreaHandler;
import com.hedario.areaforge.Methods;
import com.hedario.areaforge.configuration.ConfigManager;
import com.hedario.areaforge.storage.AreaRepository;

public class CancelCommand extends AFCommand {
	private AreaHandler handler;

	public CancelCommand() {
		super("cancel", "/af cancel <area|all>", ConfigManager.get().getString("Language.Commands.Cancel.Description"), new String[] {"cancel"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!this.hasPermission(sender) || !this.isCorrectLength(sender, 1, 1, args.size())) {
			return;
		}
		handler = AreaForge.getInstance().getAreaHandler();
		String arg = args.get(0);
		if (AreaRepository.getAreaNames().isEmpty()) {
			this.sendMessage(sender, ConfigManager.get().getString("Language.Messages.No_areas_found"));
			return;
		}
		if (handler.getLoader().isEmpty()) {
			this.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Commands.Cancel.All_idle"), "%area%", arg));
			return;
		}
		if (arg.equalsIgnoreCase("all")) {
			handler.getLoader().clear();
			this.sendMessage(sender, ConfigManager.get().getString("Language.Commands.Cancel.Cancelled_all"));
		} else {
			if (!AreaRepository.getAreaNames().contains(arg)) {
				this.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Messages.Not_found"), "%area%", arg));
				return;
			}
			if (!handler.isLoading(arg)) {
				this.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Commands.Cancel.Idle"), "%area%", arg));
				return;
			}
			handler.getLoader().remove(arg);
			this.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Commands.Cancel.Cancelled_single"), "%area%", arg));
		}
	}

	@Override
	public List<String> getTabCompletion(CommandSender sender, List<String> args) {
		List<String> names = new ArrayList<>(AreaForge.getInstance().getAreaHandler().getLoader().keySet());
		names.add("ALL");
		return names;
	}

}

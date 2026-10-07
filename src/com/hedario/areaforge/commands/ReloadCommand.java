package com.hedario.areaforge.commands;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import com.hedario.areaforge.AreaForge;
import com.hedario.areaforge.AreaHandler;
import com.hedario.areaforge.AreaScheduler;
import com.hedario.areaforge.configuration.Config;
import com.hedario.areaforge.configuration.ConfigManager;

public class ReloadCommand extends AFCommand {

	public ReloadCommand() {
		super("reload", "/af reload", ConfigManager.get().getString("Language.Commands.Reload.Description"), new String[] {"reload", "r"});
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!this.hasPermission(sender)) {
			return;
		}
		Config config = ConfigManager.getDef();
		try {
			config.reload();
			this.sendMessage(sender, ConfigManager.get().getString("Language.Commands.Reload.Success"));
		} catch (FileNotFoundException e) {
			this.sendMessage(sender, "Failed to load " + config.get().getName() + ", does it exist?");
			AreaForge.getInstance().getLogger().warning("Failed to load " + config.get().getName() + ", does it exist?");
			e.printStackTrace();
		} catch (IOException e) {
			AreaForge.getInstance().getLogger().warning("Error whileloading " + config.get().getName());
			this.sendMessage(sender, "Error whileloading " + config.get().getName());
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			AreaForge.getInstance().getLogger().warning("An invalid configuration was loaded for " + config.get().getName());
			this.sendMessage(sender, "An invalid configuration was loaded for " + config.get().getName());
			e.printStackTrace();
		}
		AreaForge.getInstance().getAreaHandler().clear();
		AreaForge.getInstance().setAreaHandler(new AreaHandler());
		AreaScheduler.cancelTask();
		AreaScheduler.getAreas().clear();
		AreaScheduler.init();
	}

	@Override
	public List<String> getTabCompletion(CommandSender sender, List<String> args) {
		return null;
	}

}

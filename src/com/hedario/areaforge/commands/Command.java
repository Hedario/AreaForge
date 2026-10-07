package com.hedario.areaforge.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

public interface Command {
	public String getName();

	public String getDescription();

	public String getPermission();
	
	public String getUsage();
	
	public String[] getAliases();
	
	public void execute(CommandSender sender, List<String> args);
	
	public List<String> getTabCompletion(CommandSender sender, List<String> args);

}

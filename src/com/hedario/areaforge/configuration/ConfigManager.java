package com.hedario.areaforge.configuration;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
	private static Config def;
	
	public static void init() {
		def = new Config(new File("config.yml"));
		configure();
	}
	
	public static void configure() {
		get().addDefault("Settings.Metrics.Enabled", true);
		get().addDefault("Settings.Storage.Engine", "SQLITE");
		get().addDefault("Settings.Storage.MySQL.Host", "localhost");
		get().addDefault("Settings.Storage.MySQL.Port", 3306);
		get().addDefault("Settings.Storage.MySQL.Pass", "");
		get().addDefault("Settings.Storage.MySQL.Db", "minecraft");
		get().addDefault("Settings.Storage.MySQL.User", "root");
		get().addDefault("Settings.Budget.Interval", 1);
		get().addDefault("Settings.Budget.Time", 0.02);
		get().addDefault("Settings.Loading.NSM.Enabled", true);
		get().addDefault("Settings.Loading.Apply_physics", false);
		
		get().addDefault("Language.Messages.Prefix", "&6AreaForge &e⛏ &6");
		get().addDefault("Language.Messages.No_permission", "&cYou don't own enough permissions to do this!");
		get().addDefault("Language.Messages.Wrong_arguments", "&cThe command wasn't used correctly, refer to its proper usage!");
		get().addDefault("Language.Messages.Must_be_player", "&cYou must be a player to do this!");
		get().addDefault("Language.Messages.No_areas_found", "&6No areas have been found, create one using &e/af create <name>&6.");
		get().addDefault("Language.Messages.Not_found", "&e%area%&6 doesn't exist, check for the correct name.");
		get().addDefault("Language.Messages.First_selection", "&6First bound has been selected.");
		get().addDefault("Language.Messages.Second_selection", "&6Second bound has been selected.");
		get().addDefault("Language.Messages.Choice_cancel", "&6Exited choice menu.");
		get().addDefault("Language.Messages.Choice_delete", "&6Are you sure you want to delete &e%area%&6?");
		get().addDefault("Language.Messages.Choice_load", "&6Are you sure you want to load &e%area%&6?");
		
		get().addDefault("Language.Commands.Help.Description", "Displays a list of available commands and provides additional information about each command.");
		get().addDefault("Language.Commands.Load.Description", "Loads and restores an existing area from its saved state.");
		get().addDefault("Language.Commands.Load.Already_loading", "&e%area%&6 is already loading.");
		get().addDefault("Language.Commands.Load.Creating", "&6Starting loading process for &e%area%&6...");
		get().addDefault("Language.Commands.Load.Complete", "&e%area% &6has been loaded succesfully.\n&6Processed &e%blocks%&6 blocks in &e%time%&6.");
		get().addDefault("Language.Commands.Load.Fail", "&6Something wrong happened while loading &e%area%&6...\n&6The process has been stopped.");
		get().addDefault("Language.Commands.Create.Description", "Creates a new area using the two positions selected with a wooden axe in the world.");
		get().addDefault("Language.Commands.Create.Already_creating", "&e%area%&6 is already being created.");
		get().addDefault("Language.Commands.Create.Already_exists", "&6An area with the name &e%area%&6 already exists, choose a different name.");
		get().addDefault("Language.Commands.Create.Complete", "&e%area%&6 has been succesfully created; saved &e%blocks% &6blocks in &e%time%&6.");
		get().addDefault("Language.Commands.Create.Fail", "&6Something wrong happened while creating &e%area%&6...\n&6The process has been stopped.");
		get().addDefault("Language.Commands.Create.Invalid", "&6Invalid area name given, specificy one.");
		get().addDefault("Language.Commands.Create.Invalid_selection", "&6The area selection isn't valid; you must select two locations in the same world.\n&6First location (left click), second location (right click).");
		get().addDefault("Language.Commands.Create.Creating", "&6Starting creation process for &e%area%&6...");
		get().addDefault("Language.Commands.List.Description", "&6Provides a list of all existing areas.");
		get().addDefault("Language.Commands.Delete.Description", "&6Deletes an existing area");
		get().addDefault("Language.Commands.Delete.Success", "&e%area%&6 has been succesfully deleted!");
		get().addDefault("Language.Commands.Delete.Fail", "&6Something went wrong when deleting &e%area%&6, check for logs.");
		get().addDefault("Language.Commands.Delete.Error", "&6Something went wrong while deleting the area, the area exists but the directory couldn't be found?");
		get().addDefault("Language.Commands.Info.Description", "Displays information for the specified area.");
		get().addDefault("Language.Commands.Reload.Description", "Reloads the plugin's configuration file.");
		get().addDefault("Language.Commands.Reload.Success", "&6Configurations have been succesfully reloaded!");
		get().addDefault("Language.Commands.Cancel.Description", "Cancels the loading process of one or all areas.");
		get().addDefault("Language.Commands.Cancel.Idle", "&6This area is currently not being loaded.");
		get().addDefault("Language.Commands.Cancel.All_idle", "&6No areas are currently being loaded.");
		get().addDefault("Language.Commands.Cancel.Cancelled_all", "&6All area loading processes have been cancelled!");
		get().addDefault("Language.Commands.Cancel.Cancelled_single", "&6The loading process for &e%area%&6 has been cancelled!");
		get().addDefault("Language.Commands.Settings.Description", "Manage and customise the settings of each area.");
		get().addDefault("Language.Commands.Settings.Time", "&e%area%&6's automatic loading interval set to: &e%time%&6.");
		get().addDefault("Language.Commands.Settings.Disable", "&e%area%&6 will no longer automatically load.");
		get().addDefault("Language.Commands.Settings.Enable", "&e%area%&6 will now automatically load.");
		get().addDefault("Language.Commands.Settings.Set_location", "&6Set safe location for &e%area%&6, players will be teleported here whenever the area loads.");
		get().addDefault("Language.Commands.Settings.Update_location", "&6Updated existing safe location for &e%area%&6, players will be teleported here whenever the area loads.");
		
		get().setComments("Settings.Budget.Interval", List.of(
				"The budget time expressed in seconds.",
				"This tells how much time can be dedicated to creating/loading an area.",
				"Depending on performace, it's strongly advised to keep this value equal or below 0.1."));
		get().setComments("Settings.Loading.NSM.Enabled", List.of(
				"NMS implementations can drastically improve the loading speed of areas by using interal structures.",
				"While it can benefit server's performance it may create entity and/or light glitches in certain scenarios."));
		get().setComments("Settings.Loading.Apply_physics", List.of(
				"By disabling this setting, physics won't be applied to blocks when loading areas.",
				"For large areas with million of blocks, keeping this disabled may benefit server's performance and loading speed."));
		
		get().options().copyDefaults(true);
		def.save();
	}
	
	public static FileConfiguration get() {
		return def.get();
	}

	public static Config getDef() {
		return def;
	}

}

package com.hedario.areaforge;

import java.util.logging.Logger;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.hedario.areaforge.commands.AFExecutor;
import com.hedario.areaforge.configuration.ConfigManager;
import com.hedario.areaforge.storage.Database;
import com.hedario.areaforge.util.PAPIExpansion;
import com.hedario.areaforge.util.Selector;

public class AreaForge extends JavaPlugin {
	private static Logger log;
	private static AreaForge instance;
	public AreaHandler handler;

	public void onEnable() {
		Bukkit.getConsoleSender().sendMessage("----- " + ChatColor.GOLD + "AreaForge" + ChatColor.RESET + " -----");
		instance = this;
		log = getLogger();
		log.info("Loading configurations...");
		ConfigManager.init();
		
		log.info("Enabling selector listener...");
		getServer().getPluginManager().registerEvents(new Selector(), instance);

		log.info("Connecting to database...");
		Database.init(this);

		log.info("Creating area handler...");
		handler = new AreaHandler();

		log.info("Registering commands...");
		AFExecutor.init();
		
		log.info("Fetching for automatic areas...");
		AreaScheduler.init();
		
		log.info("Setting up metrics...");
		setMetrics();
		
		log.info("Setting PAPI...");
		if (Bukkit.getPluginManager().getPlugin("PlaceHolderAPI") != null) {
			log.info("PlaceHolderAPI succesfully hooked to AreaForge.");
			new PAPIExpansion().register();
		} else {
			log.info("This server is not using PlaceHolderAPI, expansion will be disabled.");
		}
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "AreaForge has been succesfully enabled!");
	}
	
	private void setMetrics() {
		if (!ConfigManager.get().getBoolean("Settings.Metrics.Enabled")) {
			return;
		}
		
		int pluginId = 34556;
        Metrics metrics = new Metrics(this, pluginId);
		metrics.addCustomChart(new SimplePie("chart_id", () -> "My value"));
	}

	public void onDisable() {
		log.info("Disabling AreaForge...");
		log.info("Shutting off " + handler.getPool().size() + " threads.");
		log.info("Stopping " + handler.getLoader().size() + " area loads.");
		log.info("Stopping " + handler.getForger().size() + " area creations.");
		handler.clear();
	}

	public static AreaForge getInstance() {
		return instance;
	}

	public static Logger getLog() {
		return log;
	}

	public AreaHandler getAreaHandler() {
		return handler;
	}

	public void setAreaHandler(AreaHandler handler) {
		this.handler = handler;
	}
}

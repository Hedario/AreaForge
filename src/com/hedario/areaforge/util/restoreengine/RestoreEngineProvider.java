package com.hedario.areaforge.util.restoreengine;

import org.bukkit.Bukkit;

import com.hedario.areaforge.AreaForge;
import com.hedario.areaforge.configuration.ConfigManager;
import com.hedario.areaforge.util.restoreengine.versions.BukkitRestoreEngine;
import com.hedario.areaforge.util.restoreengine.versions.NMS_1_21_10;

public final class RestoreEngineProvider {

	public static RestoreEngine create() {
		boolean useNMS = ConfigManager.get().getBoolean("Settings.Loading.NSM.Enabled");
		if (!isPaper() || !useNMS) {
			AreaForge.getInstance().getLogger().info("This server is not running on paper, NMS utils will be disabled.");
			return new BukkitRestoreEngine();
		}

		switch (getVersion()) {
		case "1.21.10":
			return new NMS_1_21_10();
		default:
			AreaForge.getInstance().getLogger().warning("Couldn't load any NMS implementation for this server version!");
			AreaForge.getInstance().getLogger().info("AreaForge will work with the bukkit engine.");
			return new BukkitRestoreEngine();
		}
	}

	private static boolean isPaper() {
		try {
			Class.forName("io.papermc.paper.configuration.PaperConfigurations");
			return true;
		} catch (ClassNotFoundException exception) {
			return false;
		}
	}

	public static String getVersion() {
		String v = Bukkit.getVersion().strip().replace("(", "").replace(")", "");
		return v.substring(v.indexOf("MC:") + 4);
	}
}
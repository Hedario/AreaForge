package com.hedario.areaforge.util;

import java.text.NumberFormat;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import com.hedario.areaforge.Area;
import com.hedario.areaforge.AreaForge;
import com.hedario.areaforge.AreaScheduler;
import com.hedario.areaforge.Methods;
import com.hedario.areaforge.data.AreaLoader;
import com.hedario.areaforge.data.ScheduledArea;
import com.hedario.areaforge.storage.AreaRepository;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PAPIExpansion extends PlaceholderExpansion {

	@Override
	public String onRequest(OfflinePlayer player, @NotNull String params) {
		if (params.startsWith("remaining_time_")) {
			String name = params.substring("remaining_time_".length());
			ScheduledArea running = AreaScheduler.getAreas().get(name);
			if (running != null) {
				long time = (running.getParsedTime() + running.getLastLoad()) - System.currentTimeMillis();
				return Methods.formatTime(time);
			}
			return "Area is not automatically loading.";
		} else if (params.startsWith("world_")) {
			String name = params.substring("world_".length());
			Area area = AreaRepository.getArea(name);
			if (area != null) {
				return area.getWorld().getName();
			}
			return "Area doesn't exist.";
		} else if (params.startsWith("first_")) {
			String name = params.substring("first_".length());
			Area area = AreaRepository.getArea(name);
			if (area != null) {
				return area.getMinX() + ", " + area.getMinY() + ", " + area.getMinZ();
			}
			return "Area doesn't exist.";
		} else if (params.startsWith("second_")) {
			String name = params.substring("second_".length());
			Area area = AreaRepository.getArea(name);
			if (area != null) {
				return area.getMaxX() + ", " + area.getMaxY() + ", " + area.getMaxZ();
			}
			return "Area doesn't exist.";
		} else if (params.startsWith("max_blocks_")) {
			String name = params.substring("max_blocks_".length());
			Area area = AreaRepository.getArea(name);
			if (area != null) {
				return "" + NumberFormat.getInstance().format(area.getBlocks());
			}
			return "Area doesn't exist.";
		} else if (params.startsWith("processed_blocks")) {
			String name = params.substring("processed_blocks".length());
			AreaLoader loader = AreaForge.getInstance().getAreaHandler().getLoader().get(name);
			if (loader != null) {
				return "" + NumberFormat.getInstance().format(loader.getProcessed());
			}
			return "0";
		} else {
			return null;
		}
	}

	@Override
	public @NotNull String getIdentifier() {
		return "AreaForge";
	}

	@Override
	public @NotNull String getAuthor() {
		return "Hedario";
	}

	@Override
	public @NotNull String getVersion() {
		return "1.0";
	}

	@Override
	public boolean persist() {
		return true;
	}
}

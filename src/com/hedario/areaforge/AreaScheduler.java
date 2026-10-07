package com.hedario.areaforge;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import com.hedario.areaforge.data.ScheduledArea;
import com.hedario.areaforge.storage.AreaRepository;

public class AreaScheduler {
	private static Map<String, ScheduledArea> areas;
	private boolean enabled;
	private static BukkitTask task;

	public static void init() {
		areas = new ConcurrentHashMap<String, ScheduledArea>();
		for (ScheduledArea area : AreaRepository.getScheduledAreas()) {
			if (area.isEnabled()) {
				areas.put(area.getName(), area);
			}
		}
		AreaForge.getLog().info("Areas scheduled to automatically load: " + areas.size());
		if (areas.isEmpty()) {
			return;
		}
		startTask();
	}

	public static void check() {
		for (ScheduledArea area : areas.values()) {
			long time = area.getLastLoad() + area.getParsedTime();
			if (System.currentTimeMillis() >= time) {
				AreaForge.getInstance().getAreaHandler().queue(area.getName());
				area.setLastLoad(System.currentTimeMillis());
			}
		}
		if (areas.isEmpty()) {
			cancelTask();
		}
	}

	public static void update(String name, boolean enabled, String time) {
		ScheduledArea repoArea = AreaRepository.getScheduledArea(name);
		if (repoArea == null) {
			repoArea = AreaRepository.addScheduledArea(name, enabled, time);
		}
		ScheduledArea area = areas.get(name);
		if (area == null) {
			if (enabled) {
				areas.put(name, repoArea);
			}
		} else {
			area.setEnabled(enabled);
			area.setTime(time);
			if (!enabled) {
				areas.remove(name);
			}
		}
		AreaRepository.updateScheduledArea(name, enabled, time);
		if (!areas.isEmpty() && task == null) {
			startTask();
		}
	}

	public static void setEnabled(String name, boolean enabled) {
		ScheduledArea repoArea = AreaRepository.getScheduledArea(name);
		String time = "00:00:00:000";
		if (repoArea != null) {
			time = repoArea.getTime();
		}
		update(name, enabled, time);
	}

	public static void setTime(String name, String time) {
		ScheduledArea repoArea = AreaRepository.getScheduledArea(name);
		boolean enabled = false;
		if (repoArea != null) {
			enabled = repoArea.isEnabled();
		}
		update(name, enabled, time);
	}

	private static void startTask() {
		task = Bukkit.getScheduler().runTaskTimerAsynchronously(AreaForge.getInstance(), () -> check(), 1200L, 200L);
	}
	
	public static void cancelTask() {
		if (task != null && !task.isCancelled()) {
			task.cancel();
			task = null;
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public static BukkitTask getTask() {
		return task;
	}

	public static Map<String, ScheduledArea> getAreas() {
		return areas;
	}
}

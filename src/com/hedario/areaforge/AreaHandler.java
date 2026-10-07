package com.hedario.areaforge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import com.hedario.areaforge.configuration.ConfigManager;
import com.hedario.areaforge.data.AreaForger;
import com.hedario.areaforge.data.AreaLoader;
import com.hedario.areaforge.exceptions.AreaNotFoundException;
import com.hedario.areaforge.storage.AreaRepository;
import com.hedario.areaforge.util.restoreengine.RestoreEngine;
import com.hedario.areaforge.util.restoreengine.RestoreEngineProvider;

public class AreaHandler {

	private final Map<String, AreaLoader> LOADER;
	private final Map<String, AreaForger> FORGER;
	private final List<Thread> POOL;
	private float budget;
	private long interval;
	private BukkitTask task;
	private RestoreEngine engine;

	public AreaHandler() {
		this.FORGER = new HashMap<String, AreaForger>();
		this.LOADER = new HashMap<String, AreaLoader>();
		this.budget = (float) (ConfigManager.get().getDouble("Settings.Budget.Time") * 1000000000);
		this.interval = ConfigManager.get().getLong("Settings.Budget.Interval") * 20;
		this.engine = RestoreEngineProvider.create();
		this.POOL = new ArrayList<Thread>();
		task = Bukkit.getScheduler().runTaskTimer(AreaForge.getInstance(), this::loadAll, 1L, interval);
	}

	public void loadAll() {
		long start = System.nanoTime();
		while (System.nanoTime() - start < budget) {
			for (Iterator<AreaLoader> iterator = LOADER.values().iterator(); iterator.hasNext();) {
				AreaLoader loader = iterator.next();
				if (loader.complete) {
					engine.update(loader.getArea().getWorld());
					iterator.remove();
					continue;
				}
				loader.progress();
				if (System.nanoTime() - start >= budget) {
					engine.update(loader.getArea().getWorld());
					return;
				}
			}
		}
		if (LOADER.isEmpty()) {
			cancel();
			return;
		}
	}
	
	public void cancel() {
		if (this.task != null && !task.isCancelled()) {
			task.cancel();
			task = null;
		}
	}
	
	public void clear() {
		cancel();
		LOADER.clear();
		FORGER.values().forEach(f -> f.cancelTask());
		FORGER.clear();
		POOL.forEach(Thread::interrupt);
		POOL.clear();
	}

	public void kill(final String name) {
		AreaLoader loader = LOADER.get(name);
		if (loader == null) {
			return;
		}
		
		Thread thread = loader.getWorker().getThread();
		thread.interrupt();
		POOL.remove(thread);
		LOADER.remove(name);
	}
	
	public void queue(final AreaLoader loader) {
		if (isLoading(loader.getArea())) {
			Methods.sendMessage(loader.getSender(), Methods.setPlaceholder(ConfigManager.get().getString("Language.Commands.Load.Already_loading"), "%area%", loader.getArea().getName()), true);
			return;
		}
		if (LOADER.isEmpty()) {
			task = Bukkit.getScheduler().runTaskTimer(AreaForge.getInstance(), this::loadAll, 1L, interval);
		}
		LOADER.put(loader.getArea().getName(), loader);
		loader.start();
	}
	
	public void queue(final String name) {
		Area area = AreaRepository.getArea(name);
		if (area == null) {
			throw new AreaNotFoundException(name);
		}
		this.queue(new AreaLoader(area));
	}

	public boolean isLoading(final String name) {
		return LOADER.containsKey(name);
	}

	public boolean isLoading(final Area area) {
		return isLoading(area.getName());
	}

	public boolean isCreating(final String name) {
		return FORGER.containsKey(name);
	}

	public boolean isCreating(final Area area) {
		return isCreating(area.getName());
	}
	
	public RestoreEngine getEngine() {
		return engine;
	}

	public void setEngine(RestoreEngine engine) {
		this.engine = engine;
	}

	public Map<String, AreaLoader> getLoader() {
		return LOADER;
	}

	public Map<String, AreaForger> getForger() {
		return FORGER;
	}

	/**
	 * @return the pool
	 */
	public List<Thread> getPool() {
		return POOL;
	}
}

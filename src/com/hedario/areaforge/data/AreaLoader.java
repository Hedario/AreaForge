package com.hedario.areaforge.data;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.text.NumberFormat;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.hedario.areaforge.Area;
import com.hedario.areaforge.AreaForge;
import com.hedario.areaforge.AreaHandler;
import com.hedario.areaforge.Methods;
import com.hedario.areaforge.WorkerLoader;
import com.hedario.areaforge.configuration.ConfigManager;
import com.hedario.areaforge.storage.AreaRepository;
import com.hedario.areaforge.util.restoreengine.RestoreEngine;

public class AreaLoader {
	private final Area area;
	private World world;
	private int x, y, z, maxX, maxY, maxZ, processed;
	private long start;
	private CommandSender sender;
	private final AreaHandler handler;
	private EntityHandler entityHandler;
	private Location safeLocation;
	
	private WorkerLoader worker;
	public boolean complete;
	public RestoreEngine engine;

	public AreaLoader(final CommandSender sender, final Area area) {
		this.handler = AreaForge.getInstance().getAreaHandler();
		this.area = area;
		this.world = area.getWorld();
		this.sender = sender;
		calculateBounds();
		engine = handler.getEngine();
		entityHandler = new EntityHandler();
		safeLocation = AreaRepository.getSafeLocation(area.getName());
	}
	
	private void calculateBounds() {
		this.x = area.getMinBoundX();
		this.y = area.getMinBoundY();
		this.z = area.getMinBoundZ();
		this.maxX = area.getMaxBoundX();
		this.maxY = area.getMaxBoundY();
		this.maxZ = area.getMaxBoundZ();
	}
	
	public AreaLoader(final Area area) {
		this(null, area);
	}

	public void start() {
		if (world == null) {
			fail(new IllegalStateException("The world for the selected area couldn't be retrieved, try creating a brand new area."));
			Methods.sendMessage(sender, "The world for the selected area couldn't be retrieved, try creating a brand new area.", true);
			return;
		}
		try {
			worker = new WorkerLoader(area, area.isSaveContainers(), area.isSaveEntities());
			handler.getPool().add(worker.getThread());
			worker.start();
		} catch (NoSuchFileException e) {
			fail(e);
			return;
		} catch (IOException ex) {
			AreaForge.getLog().severe("An error occurred while initialising the area's writer, check for file.");
			Methods.sendMessage(sender, "An error occurred while initialising the area's writer, check for file.", true);
			ex.printStackTrace();
			return;
		}
		start = System.currentTimeMillis();
		Methods.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Commands.Load.Creating"), "%area%", area.getName()), true);
	}
	
	public void progress() {
		Integer id = worker.getBlockQueue().poll();
		if (id != null) {
			BlockData data = worker.getPalette().getDeserialized(id);
			entityHandler.teleport(new Location(world, x, y, z), safeLocation);
			engine.setBlock(world, x, y, z, data);
			processed++;
			if (!advance()) {
				worker.setProcessingBlocks(false);
				return;
			}
		} else if (worker.isProcessingBlocks()) {
			return;
		} else {
			loadContainers();
			loadEntity();
			if (!worker.isActive() && worker.getContainersQueue().isEmpty() && worker.getEntityQueue().isEmpty()) {
				complete();
				return;
			}
		}
	}
 
	private void loadContainers() {
		if (!area.isSaveContainers()) {
			return;
		}
		ContainerSnapshot snapshot = worker.getContainersQueue().poll();
		if (snapshot == null) {
			return;
		}
		Block block = area.getWorld().getBlockAt(snapshot.getX(), snapshot.getY(), snapshot.getZ());
		if (block.getState() instanceof Container container) {
			container.getInventory().setContents(snapshot.getContents());
		}
	}
	
	private void loadEntity() {
		if (!area.isSaveEntities()) {
			return;
		}
		EntitySnapshot snapshot = worker.getEntityQueue().poll();
		if (snapshot == null) {
			return;
		}
		LivingEntity entity = (LivingEntity) area.getWorld().spawnEntity(new Location(area.getWorld(), snapshot.getX(), snapshot.getY(), snapshot.getZ(), snapshot.getYaw(), snapshot.getPitch()), snapshot.getType());
		entity.getEquipment().setArmorContents(snapshot.getContents());
	}
	
	public boolean advance() {
		z++;
		if (z <= maxZ) {
			return true;
		}
		z = area.getMinBoundZ();

		y++;
		if (y <= maxY) {
			return true;
		}
		y = area.getMinBoundY();

		x++;
		return x <= maxX;
	}

	public void complete() {
		this.handler.getPool().remove(this.worker.getThread());
		this.worker.stop();
		long time = System.currentTimeMillis() - start;
		Methods.sendMessage(sender, Methods.setPlaceholders(ConfigManager.get().getString("Language.Commands.Load.Complete"),
						Map.of("%area%", area.getName(), "%blocks%", NumberFormat.getInstance().format(processed), "%time%", Methods.formatTime(time))), true);
		if (sender != null && sender instanceof Player player) {
			player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
		}
		this.complete = true;
	}

	public void fail(final Exception ex) {
		this.handler.getPool().remove(this.worker.getThread());
		this.worker.stop();
		handler.getLoader().remove(area.getName());
		if (sender != null && sender instanceof Player player) {
			player.playSound(player, Sound.BLOCK_ANVIL_PLACE, 1, 2);
		}
		AreaForge.getLog().severe("Something went wrong while trying to load the area.");
		AreaForge.getLog().severe("Cause: " + ex.getCause().getMessage());
		AreaForge.getLog().severe("The loading process has been stopped.");
		ex.printStackTrace();
		Methods.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Commands.Load.Fail"), "%area%", area.getName()), true);
	}
	

	public CommandSender getSender() {
		return sender;
	}

	public void setSender(CommandSender sender) {
		this.sender = sender;
	}

	public int getProcessed() {
		return processed;
	}

	public void setProcessed(int processed) {
		this.processed = processed;
	}
	
	public Area getArea() {
		return area;
	}

	public WorkerLoader getWorker() {
		return worker;
	}

	public void setWorker(WorkerLoader worker) {
		this.worker = worker;
	}
}

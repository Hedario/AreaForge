package com.hedario.areaforge.data;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.hedario.areaforge.Area;
import com.hedario.areaforge.AreaForge;
import com.hedario.areaforge.AreaHandler;
import com.hedario.areaforge.Methods;
import com.hedario.areaforge.WorkerForger;
import com.hedario.areaforge.configuration.ConfigManager;
import com.hedario.areaforge.storage.AreaRepository;

public class AreaForger {
	private final Area area;
	private int x, y, z, maxX, maxY, maxZ, processed;
	private long start;
	private BukkitTask task;
	private CommandSender sender;
	private final AreaHandler handler;
	private EntityHandler entityHandler;
	
	private boolean saveContainers, saveEntities;
	private float budget;
	private long interval;
	private Set<String> chests = new HashSet<>();
	
	private WorkerForger worker;
	
	public AreaForger(final CommandSender sender, final Area area, final boolean saveContainers, final boolean saveEntities) {
		this.handler = AreaForge.getInstance().getAreaHandler();
		this.entityHandler = new EntityHandler();
		this.area = area;
		if (handler.isCreating(area)) {
			Methods.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Commands.Create.Already_creating"), "%area%", area.getName()), true);
			return;
		}
		this.sender = sender;
		calculateBounds();
		this.saveContainers = saveContainers;
		this.saveEntities = saveEntities;
		this.budget = (float) (ConfigManager.get().getDouble("Settings.Budget.Time") * 1000000000);
		this.interval = ConfigManager.get().getLong("Settings.Budget.Interval") * 20;
		handler.getForger().put(area.getName(), this);
		Methods.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Commands.Create.Creating"), "%area%", area.getName()), true);
	}
	
	public AreaForger(final CommandSender sender, final Area area) {
		this(sender, area, true, true);
	}
	
	private void calculateBounds() {
		this.x = area.getMinBoundX();
		this.y = area.getMinBoundY();
		this.z = area.getMinBoundZ();
		this.maxX = area.getMaxBoundX();
		this.maxY = area.getMaxBoundY();
		this.maxZ = area.getMaxBoundZ();
	}

	public void start() {
		if (area.getWorld() == null) {
			fail(new IllegalStateException("The world for the selected area couldn't be retrieved, try creating a new selection."));
			Methods.sendMessage(sender, "The world for the selected area couldn't be retrieved, try creating a new selection.", true);
			return;
		}
		start = System.currentTimeMillis();
		try {
			this.worker = new WorkerForger(area, saveContainers, saveEntities);
			this.handler.getPool().add(this.worker.getThread());
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
		task = Bukkit.getScheduler().runTaskTimer(AreaForge.getInstance(), this::progress, 1L, interval);
	}
	
	public void progress() {
		long start = System.nanoTime();
		while (System.nanoTime() - start < budget) {
			final Block block = area.getWorld().getBlockAt(x, y, z);
			final String serialized = block.getBlockData().getAsString();
			final int paletteId = worker.getPalette().mapData(serialized);
			try {
				worker.queueBlock(paletteId);
			} catch (InterruptedException e) {
				fail(e);
				e.printStackTrace();
				return;
			}
			saveContainer(block);
			if (saveEntities) {
				Set<EntitySnapshot> map = entityHandler.map(block.getLocation());
				if (!map.isEmpty())
					worker.queueEntity(map);
			}
			processed++;
			if (!advance()) {
				cancelTask();
				worker.setActive(false);
				return;
			}
		}
	}
	
	private void saveContainer(Block block) {
		if (!saveContainers) {
			return;
		}
	    BlockState state = block.getState();
	    if (state instanceof Chest chest) {
	        if (chest.getInventory().getHolder() instanceof DoubleChest dchest) {
	            Location left = dchest.getLeftSide().getInventory().getLocation();
	            Location right = dchest.getRightSide().getInventory().getLocation();

	            String leftKey = left.getBlockX() + ":" + left.getBlockY() + ":" + left.getBlockZ();
	            String rightKey = right.getBlockX() + ":" + right.getBlockY() + ":" + right.getBlockZ();

	            if (chests.contains(leftKey) || chests.contains(rightKey)) {
	                return;
	            }

	            chests.add(leftKey);
	            chests.add(rightKey);

	            ContainerSnapshot snap = new ContainerSnapshot(left.getBlockX(), left.getBlockY(), left.getBlockZ(), dchest.getInventory().getContents().clone());
	            worker.queueContainer(snap);
	            return;
	        }
	        ContainerSnapshot snap = new ContainerSnapshot(chest.getX(), chest.getY(), chest.getZ(), chest.getInventory().getContents().clone());
	        worker.queueContainer(snap);
	        return;
	    }

	    if (state instanceof Container container) {
	    	ContainerSnapshot snap = new ContainerSnapshot(container.getX(), container.getY(), container.getZ(), container.getInventory().getContents().clone());
	    	worker.queueContainer(snap);
	    }
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
		area.setBlocks(processed);
		area.setSaveContainers(saveContainers);
		area.setSaveEntities(saveEntities);
		AreaRepository.createArea(area);
		long time = System.currentTimeMillis() - start;
		handler.getForger().remove(area.getName());
		Methods.sendMessage(sender, Methods.setPlaceholders(ConfigManager.get().getString("Language.Commands.Create.Complete"),
						Map.of("%area%", area.getName(), "%blocks%", NumberFormat.getInstance().format(processed), "%time%", Methods.formatTime(time))), true);
		if (sender != null && sender instanceof Player player) {
			player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
		}
	}
	
	public void fail(final Exception ex) {
		cancelTask();
		this.handler.getPool().remove(this.worker.getThread());
		this.worker.stop();
		try {
			if (worker.getBlockWriter() != null) {
				worker.getBlockWriter().close();
			}
			if (worker.getContainerWriter() != null) {
				worker.getBlockWriter().close();
			}
			if (worker.getEntityWriter() != null) {
				worker.getEntityWriter().close();
			}
		} catch (IOException e) {
			AreaForge.getLog().severe("Something went wrong while closing the readers.");
			e.printStackTrace();
		}
		handler.getForger().remove(area.getName());
		if (sender != null && sender instanceof Player player) {
			player.playSound(player, Sound.BLOCK_ANVIL_PLACE, 1, 2);
		}
		AreaForge.getLog().severe("Something went wrong while trying to create the area.");
		AreaForge.getLog().severe("Cause: " + ex.getMessage());
		AreaForge.getLog().severe("The creation process has been stopped.");
		Methods.sendMessage(sender, Methods.setPlaceholder(ConfigManager.get().getString("Language.Commands.Create.Fail"), "%area%", area.getName()), true);
		ex.printStackTrace();
	}
	
	public void cancelTask() {
		if (task != null && !task.isCancelled()) {
			task.cancel();
			task = null;
		}
	}

	public long getTime() {
		return start;
	}

	public void setTime(long time) {
		this.start = time;
	}

	public int getProcessed() {
		return processed;
	}

	public void setProcessed(int processed) {
		this.processed = processed;
	}

	public CommandSender getSender() {
		return sender;
	}

	public void setSender(CommandSender sender) {
		this.sender = sender;
	}

	public Area getArea() {
		return area;
	}
}

package com.hedario.areaforge;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.hedario.areaforge.data.AreaForger;
import com.hedario.areaforge.data.ContainerSnapshot;
import com.hedario.areaforge.data.EntitySnapshot;
import com.hedario.areaforge.data.Palette;
import com.hedario.areaforge.data.Writer;
import com.hedario.areaforge.data.Writer.WriterType;

public class WorkerForger {
	private final BlockingQueue<Integer> blockQueue;
	private final BlockingQueue<ContainerSnapshot> containerQueue;
	private final BlockingQueue<Set<EntitySnapshot>> entityQueue;
	private final Thread thread;
	private volatile boolean active;
	private final DataOutputStream blockWriter, paletteWriter, containerWriter, entityWriter;
	private final Palette palette;
	private final AreaForger forger;

	public WorkerForger(final Area area, boolean saveContainers, boolean saveEntities) throws IOException {
		this.thread = new Thread(this::consume, "AreaForge-Worker-" + area.getName());
		this.blockQueue = new LinkedBlockingQueue<>(10000);
		this.containerQueue = new LinkedBlockingQueue<>();
		this.entityQueue = new LinkedBlockingQueue<>();
		paletteWriter = Writer.createWriter(area, WriterType.BLOCKS_PALETTE);
		blockWriter = Writer.createWriter(area, WriterType.BLOCKS);
		containerWriter = saveContainers ? Writer.createWriter(area, WriterType.CONTAINERS) : null;
		entityWriter = saveEntities ? Writer.createWriter(area, WriterType.ENTITIES) : null;
		palette = new Palette();
		this.forger = AreaForge.getInstance().getAreaHandler().getForger().get(area.getName());
	}

	public void start() {
		this.active = true;
		thread.start();
	}

	private void consume() {
		while (active || !blockQueue.isEmpty() || !containerQueue.isEmpty() || !entityQueue.isEmpty()) {
			try {
				final Integer id = blockQueue.poll(100, TimeUnit.MILLISECONDS);
				if (id != null) {
					blockWriter.writeInt(id);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				fail(e);
				return;
			} catch (IOException e) {
				fail(e);
				e.printStackTrace();
				return;
			}
			
			if (containerWriter != null) {
				try {
					ContainerSnapshot snapshot = containerQueue.poll();
					if (snapshot != null) {
						writeContainer(snapshot);
					}
				} catch (IOException e) {
					fail(e);
					e.printStackTrace();
					return;
				}
			}
			
			if (entityWriter != null) {
				try {
					Set<EntitySnapshot> ents = entityQueue.poll();
					if (ents != null && !ents.isEmpty()) {
						writeEntity(ents);
					}
				} catch (IOException e) {
					fail(e);
					e.printStackTrace();
					return;
				}
			}
		}
		try {
			palette.writeToFile(paletteWriter);
			blockWriter.flush();
			blockWriter.close();
			if (containerWriter != null) {
				containerWriter.flush();
				containerWriter.close();
			}
			if (entityWriter != null) {
				entityWriter.flush();
				entityWriter.close();
			}

		} catch (IOException e) {
			fail(e);
			e.printStackTrace();
			return;
		}
		Bukkit.getScheduler().runTask(AreaForge.getInstance(), () -> forger.complete());
	}

	public void queueBlock(int id) throws InterruptedException {
		blockQueue.put(id);
	}

	public void queueContainer(ContainerSnapshot snapshot) {
		containerQueue.add(snapshot);
	}
	
	public void queueEntity(Set<EntitySnapshot> ents) {
		entityQueue.add(ents);
	}

	private void writeContainer(ContainerSnapshot snapshot) throws IOException {
		containerWriter.writeInt(snapshot.getX());
		containerWriter.writeInt(snapshot.getY());
		containerWriter.writeInt(snapshot.getZ());

		byte[] data = Writer.serializeInventory(snapshot.getContents());

		containerWriter.writeInt(data.length);
		containerWriter.write(data);
	}

	private void writeEntity(Set<EntitySnapshot> ents) throws IOException {
		for (EntitySnapshot entity : ents) {
			entityWriter.writeUTF(entity.getType().name());
			entityWriter.writeFloat(entity.getX());
			entityWriter.writeFloat(entity.getY());
			entityWriter.writeFloat(entity.getZ());
			entityWriter.writeFloat(entity.getYaw());
			entityWriter.writeFloat(entity.getPitch());
			ItemStack[] armor = entity.getContents();
			byte[] data = Writer.serializeInventory(armor);
			entityWriter.writeInt(data.length);
			entityWriter.write(data);
		}
	}
	
	private void fail(Exception e) {
		Bukkit.getScheduler().runTask(AreaForge.getInstance(), () -> forger.fail(e));
	}
	
	public void stop() {
		if (this.thread != null && !this.thread.isInterrupted()) {
			this.thread.interrupt();
		}
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Thread getThread() {
		return thread;
	}

	public BlockingQueue<Integer> getBlockQueue() {
		return blockQueue;
	}

	public BlockingQueue<ContainerSnapshot> getContainerQueue() {
		return containerQueue;
	}

	public DataOutputStream getBlockWriter() {
		return blockWriter;
	}

	public DataOutputStream getContainerWriter() {
		return containerWriter;
	}

	public DataOutputStream getEntityWriter() {
		return entityWriter;
	}

	public Palette getPalette() {
		return palette;
	}
}

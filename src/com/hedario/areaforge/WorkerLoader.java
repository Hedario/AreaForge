package com.hedario.areaforge;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import com.hedario.areaforge.data.AreaLoader;
import com.hedario.areaforge.data.ContainerSnapshot;
import com.hedario.areaforge.data.EntitySnapshot;
import com.hedario.areaforge.data.Palette;
import com.hedario.areaforge.data.Writer;
import com.hedario.areaforge.data.Writer.WriterType;

public class WorkerLoader {
	private DataInputStream blockReader, paletteReader, containerReader, entityReader;
	private Palette palette;
	private Thread thread;
	private volatile boolean active, processingBlocks;

	private BlockingQueue<Integer> blockQueue;
	private BlockingQueue<EntitySnapshot> entityQueue;
	private BlockingQueue<ContainerSnapshot> containersQueue;
	private AreaLoader loader;

	public WorkerLoader(final Area area, final boolean saveContainers, final boolean saveEntities) throws IOException {
		blockReader = Writer.createReader(area, WriterType.BLOCKS);
		paletteReader = Writer.createReader(area, WriterType.BLOCKS_PALETTE);
		containerReader = saveContainers ? Writer.createReader(area, WriterType.CONTAINERS) : null;
		entityReader = saveEntities ? Writer.createReader(area, WriterType.ENTITIES) : null;
		blockQueue = new LinkedBlockingQueue<>(10000);
		entityQueue = new LinkedBlockingQueue<>();
		containersQueue = new LinkedBlockingQueue<>();
		this.thread = new Thread(this::consume, "AreaForge-Loader-" + area.getName());
		this.loader = AreaForge.getInstance().getAreaHandler().getLoader().get(area.getName());
	}

	public void start() {
		this.processingBlocks = true;
		this.active = true;
		this.thread.start();
	}

	public void consume() {
		try {
			loadPalette();
		} catch (IOException e) {
			Bukkit.getScheduler().runTask(AreaForge.getInstance(), () -> loader.fail(e));
			e.printStackTrace();
			return;
		}
		try {
			loadBlocks();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
			return;
		} catch (IOException e) {
			Bukkit.getScheduler().runTask(AreaForge.getInstance(), () -> loader.fail(e));
			e.printStackTrace();
			return;
		}

		while (processingBlocks) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		try {
			loadContainers();
		} catch (ClassNotFoundException | IOException e) {
			Bukkit.getScheduler().runTask(AreaForge.getInstance(), () -> loader.fail(e));
			e.printStackTrace();
			return;
		}
		try {
			loadEntities();
		} catch (ClassNotFoundException | IOException e) {
			Bukkit.getScheduler().runTask(AreaForge.getInstance(), () -> loader.fail(e));
			e.printStackTrace();
			return;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
			return;
		}

		try {
			paletteReader.close();
			blockReader.close();
			if (containerReader != null) {
				containerReader.close();
			}
			if (entityReader != null) {
				entityReader.close();
			}
		} catch (IOException e) {
			Bukkit.getScheduler().runTask(AreaForge.getInstance(), () -> loader.fail(e));
			e.printStackTrace();
			return;
		}
		active = false;
		this.thread.interrupt();
	}

	public void loadPalette() throws IOException {
		palette = new Palette();
		int size = paletteReader.readInt();
		for (int i = 0; i < size; i++) {
			palette.mapData(paletteReader.readUTF());
		}
	}

	private void loadBlocks() throws InterruptedException, IOException {
		while (true) {
			try {
				blockQueue.put(blockReader.readInt());
			} catch (EOFException e) {
				break;
			}
		}
	}

	private void loadContainers() throws IOException, ClassNotFoundException {
		if (!loader.getArea().isSaveContainers()) {
			return;
		}
		while (true) {
			int x;
			try {
				x = containerReader.readInt();
			} catch (EOFException e) {
				break;
			}
			int y = containerReader.readInt();
			int z = containerReader.readInt();
			int dataLength = containerReader.readInt();
			byte[] data = new byte[dataLength];
			containerReader.readFully(data);
			ItemStack[] inventory = Writer.deserializeInventory(data);
			containersQueue.add(new ContainerSnapshot(x, y, z, inventory));
		}
	}

	private void loadEntities() throws IOException, ClassNotFoundException, InterruptedException {
		if (!loader.getArea().isSaveEntities()) {
			return;
		}
		while (true) {
			final String typeSerialized;
			try {
				typeSerialized = entityReader.readUTF();
			} catch (EOFException e) {
				break;
			}
			EntityType type = EntityType.valueOf(typeSerialized);
			float x = entityReader.readFloat();
			float y = entityReader.readFloat();
			float z = entityReader.readFloat();
			float yaw = entityReader.readFloat();
			float pitch = entityReader.readFloat();
			int length = entityReader.readInt();
			byte[] data = new byte[length];
			entityReader.readFully(data);
			ItemStack[] inventory = Writer.deserializeInventory(data);
			EntitySnapshot snapshot = new EntitySnapshot(type,
					new Location(loader.getArea().getWorld(), x, y, z, yaw, pitch), inventory);
			entityQueue.add(snapshot);
		}
	}

	public void stop() {
		if (this.thread != null && !this.thread.isInterrupted()) {
			this.thread.interrupt();
		}
	}

	public DataInputStream getContainerReader() {
		return containerReader;
	}

	public void setContainerReader(DataInputStream containerReader) {
		this.containerReader = containerReader;
	}

	public DataInputStream getEntityReader() {
		return entityReader;
	}

	public void setEntityReader(DataInputStream entityReader) {
		this.entityReader = entityReader;
	}

	public BlockingQueue<EntitySnapshot> getEntityQueue() {
		return entityQueue;
	}

	public void setEntityQueue(BlockingQueue<EntitySnapshot> entityQueue) {
		this.entityQueue = entityQueue;
	}

	public BlockingQueue<ContainerSnapshot> getContainersQueue() {
		return containersQueue;
	}

	public void setContainerQueue(BlockingQueue<ContainerSnapshot> containersQueue) {
		this.containersQueue = containersQueue;
	}

	public AreaLoader getLoader() {
		return loader;
	}

	public void setLoader(AreaLoader loader) {
		this.loader = loader;
	}

	public DataInputStream getBlockReader() {
		return blockReader;
	}

	public void setBlockReader(DataInputStream blockReader) {
		this.blockReader = blockReader;
	}

	public DataInputStream getPaletteReader() {
		return paletteReader;
	}

	public void setPaletteReader(DataInputStream paletteReader) {
		this.paletteReader = paletteReader;
	}

	public Palette getPalette() {
		return palette;
	}

	public void setPalette(Palette palette) {
		this.palette = palette;
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public boolean isProcessingBlocks() {
		return processingBlocks;
	}

	public void setProcessingBlocks(boolean processingBlocks) {
		this.processingBlocks = processingBlocks;
	}

	public BlockingQueue<Integer> getBlockQueue() {
		return blockQueue;
	}

	public void setBlockQueue(BlockingQueue<Integer> blockQueue) {
		this.blockQueue = blockQueue;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}

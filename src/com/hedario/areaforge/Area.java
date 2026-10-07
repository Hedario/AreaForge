package com.hedario.areaforge;

import java.sql.Timestamp;

import org.bukkit.World;

import com.hedario.areaforge.util.Selection;

public class Area {
	private final String name;
	private World world;
	private int minX, minY, minZ, maxX, maxY, maxZ;
	private Timestamp createdAt;
	private int blocks;
	private boolean saveContainers, saveEntities;
	
	public Area(final String name, final World world, final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ) {
		this.name = name;
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		this.world = world;
	}
	
	public Area(final String name, final Selection selection) {
		this(name, selection.getWorld(),
				(int) selection.getFirst().getX(), (int) selection.getFirst().getY(), (int) selection.getFirst().getZ(),
				(int) selection.getSecond().getX(), (int) selection.getSecond().getY(), (int) selection.getSecond().getZ());
	}
	
	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public int getMinX() {
		return minX;
	}

	public void setMinX(int minX) {
		this.minX = minX;
	}

	public int getMinY() {
		return minY;
	}

	public void setMinY(int minY) {
		this.minY = minY;
	}

	public int getMinZ() {
		return minZ;
	}

	public void setMinZ(int minZ) {
		this.minZ = minZ;
	}

	public int getMaxX() {
		return maxX;
	}

	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}

	public int getMaxY() {
		return maxY;
	}

	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}

	public int getMaxZ() {
		return maxZ;
	}

	public void setMaxZ(int maxZ) {
		this.maxZ = maxZ;
	}

	public int getMinBoundX() {
		return Math.min(minX, maxX);
	}

	public int getMinBoundY() {
		return Math.min(minY, maxY);
	}

	public int getMinBoundZ() {
		return Math.min(minZ, maxZ);
	}

	public int getMaxBoundX() {
		return Math.max(minX, maxX);
	}

	public int getMaxBoundY() {
		return Math.max(minY, maxY);
	}

	public int getMaxBoundZ() {
		return Math.max(minZ, maxZ);
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp timestamp) {
		this.createdAt = timestamp;
	}

	public String getName() {
		return name;
	}

	public int getWidth() {
		return maxX - minX + 1;
	}
	
	public int getLength() {
		return maxZ - minZ + 1;
	}
	
	public int getHeight() {
		return maxY - minY + 1;
	}

	public int getBlocks() {
		return blocks;
	}

	public void setBlocks(int blocks) {
		this.blocks = blocks;
	}

	public boolean isSaveContainers() {
		return saveContainers;
	}

	public void setSaveContainers(boolean saveContainers) {
		this.saveContainers = saveContainers;
	}

	public boolean isSaveEntities() {
		return saveEntities;
	}

	public void setSaveEntities(boolean saveEntities) {
		this.saveEntities = saveEntities;
	}

}

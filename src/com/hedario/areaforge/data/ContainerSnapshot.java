package com.hedario.areaforge.data;

import org.bukkit.inventory.ItemStack;

public class ContainerSnapshot {
	private int x, y, z;
	private ItemStack[] contents;

	public ContainerSnapshot(int x, int y, int z, ItemStack[] contents) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.contents = contents;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public ItemStack[] getContents() {
		return contents;
	}

	public void setContents(ItemStack[] contents) {
		this.contents = contents;
	}

}

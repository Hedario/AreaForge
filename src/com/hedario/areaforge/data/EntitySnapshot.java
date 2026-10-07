package com.hedario.areaforge.data;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class EntitySnapshot {
	private EntityType type;
	private float x, y, z, yaw, pitch;
	private ItemStack[] contents;
	
	public EntitySnapshot(EntityType type, Location location, ItemStack[] contents) {
		this.type = type;
		this.x = (float) location.getX();
		this.y = (float) location.getY();
		this.z = (float) location.getZ();
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
		this.contents = contents.clone();
	}

	public EntityType getType() {
		return type;
	}

	public void setType(EntityType type) {
		this.type = type;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public ItemStack[] getContents() {
		return contents;
	}

	public void setContents(ItemStack[] contents) {
		this.contents = contents;
	}

}

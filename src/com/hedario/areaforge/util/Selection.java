package com.hedario.areaforge.util;

import org.bukkit.Location;
import org.bukkit.World;

public class Selection {
	private Location first, second;

	public Location getFirst() {
		return first;
	}

	public void setFirst(Location first) {
		this.first = first;
	}

	public Location getSecond() {
		return second;
	}

	public void setSecond(Location second) {
		this.second = second;
	}
	
	public boolean isValid() {
		if (first == null || second == null) {
			return false;
		}
		if (!first.getWorld().getName().equals(second.getWorld().getName())) {
			return false;
		}
		return true;
	}
	
	public World getWorld() {
		if (!first.getWorld().getName().equals(second.getWorld().getName())) {
			return null;
		}
		return first.getWorld();
	}
	
	public void clear() {
		first = null;
		second = null;
	}

}

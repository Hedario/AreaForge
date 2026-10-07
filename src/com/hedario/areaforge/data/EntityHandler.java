package com.hedario.areaforge.data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class EntityHandler {
	private Set<UUID> capturedEntities;
	
	public EntityHandler() {
		capturedEntities = new HashSet<>();
	}
	
	public Set<EntitySnapshot> map(Location location) {
		Set<EntitySnapshot> ents = new HashSet<>();
		for (Entity entity : location.getWorld().getNearbyEntities(location, 1, 1, 1, entity -> !entity.isDead() && (entity instanceof LivingEntity && (!(entity instanceof Player))))) {
			if (capturedEntities.contains(entity.getUniqueId())) {
				continue;
			}
			ents.add(new EntitySnapshot(entity.getType(), location, ((LivingEntity) entity).getEquipment().getArmorContents()));
			capturedEntities.add(entity.getUniqueId());
		}
		return ents;
	}
	
	public void teleport(Location location, Location destination) {
		if (destination == null) {
			return;
		}
		for (Entity player : location.getWorld().getNearbyEntities(location, 1, 1, 1, player -> player instanceof Player)) {
			player.teleport(destination);
		}
	}
}

package com.hedario.areaforge.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.hedario.areaforge.Methods;
import com.hedario.areaforge.configuration.ConfigManager;

public class Selector implements Listener {
	private static final Map<String, Selection> SELECTIONS = new HashMap<String, Selection>();

	@EventHandler
	public void onInteract(final PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!isValid(event)) {
			return;
		}

		SELECTIONS.putIfAbsent(player.getName(), new Selection());
		Block block = event.getClickedBlock();
		if (block == null) {
			return;
		}
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			SELECTIONS.get(player.getName()).setFirst(block.getLocation());
			Methods.sendMessage(event.getPlayer(), ConfigManager.get().getString("Language.Messages.First_selection"), true);
			event.setCancelled(true);
			return;
		}
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			SELECTIONS.get(player.getName()).setSecond(block.getLocation());
			Methods.sendMessage(event.getPlayer(), ConfigManager.get().getString("Language.Messages.Second_selection"), true);
			event.setCancelled(true);
		}
	}

	private boolean isValid(final PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player == null) {
			return false;
		}
		if (event.getHand() != EquipmentSlot.HAND) {
			return false;
		}
		if (player.getInventory().getItemInMainHand().getType() != Material.WOODEN_AXE) {
			return false;
		}
		return true;
	}

	public static Map<String, Selection> getSELECTIONS() {
		return SELECTIONS;
	}
}

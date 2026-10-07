package com.hedario.areaforge.util.restoreengine.versions;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import com.hedario.areaforge.configuration.ConfigManager;
import com.hedario.areaforge.util.restoreengine.RestoreEngine;

public class BukkitRestoreEngine implements RestoreEngine {
	private final boolean physics = ConfigManager.get().getBoolean("Settings.Loading.Apply_physics");

	@Override
	public void setBlock(World world, int x, int y, int z, BlockData blockData) {
		Block block = world.getBlockAt(x, y, z);
		block.setBlockData(blockData, physics);
	}

	@Override
	public void update(World world) {}

}

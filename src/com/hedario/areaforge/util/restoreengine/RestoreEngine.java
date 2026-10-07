package com.hedario.areaforge.util.restoreengine;

import org.bukkit.World;
import org.bukkit.block.data.BlockData;

public interface RestoreEngine {
	void setBlock(World world, int x, int y, int z, BlockData blockData);

	void update(World world);
	
	default String getVersion() {
		return this.getClass().getSimpleName();
	}
}

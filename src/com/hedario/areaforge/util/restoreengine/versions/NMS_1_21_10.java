package com.hedario.areaforge.util.restoreengine.versions;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

import com.hedario.areaforge.util.restoreengine.RestoreEngine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public class NMS_1_21_10 implements RestoreEngine {
	private final Set<Long> modifiedChunks = new HashSet<>();

	@Override
	public void setBlock(World world, int x, int y, int z, BlockData blockData) {
		CraftWorld craftWorld = (CraftWorld) world;
		int chunkX = x >> 4;
		int chunkZ = z >> 4;
		LevelChunk chunk = craftWorld.getHandle().getChunk(chunkX, chunkZ);
		BlockState state = ((CraftBlockData) blockData).getState();
		BlockPos position = new BlockPos(x, y, z);
		chunk.setBlockState(position, state, 0);
		modifiedChunks.add(ChunkPos.asLong(chunkX, chunkZ));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void update(World world) {
		for (long chunkKey : modifiedChunks) {
			int chunkX = ChunkPos.getX(chunkKey);
			int chunkZ = ChunkPos.getZ(chunkKey);
			world.refreshChunk(chunkX, chunkZ);
		}
		modifiedChunks.clear();
	}
}

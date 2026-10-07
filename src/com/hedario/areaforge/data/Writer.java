package com.hedario.areaforge.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import com.hedario.areaforge.Area;
import com.hedario.areaforge.AreaForge;

public class Writer {
	
	public enum WriterType {
		BLOCKS,
		BLOCKS_PALETTE,
		CONTAINERS,
		ENTITIES;
	}

	public static DataOutputStream createWriter(final Area area, final WriterType type) throws IOException {
		final Path file = AreaForge.getInstance().getDataFolder().toPath().resolve("Areas").resolve(area.getName()).resolve(type.name() + ".dat");
		Files.createDirectories(file.getParent());
		return new DataOutputStream(new GZIPOutputStream(Files.newOutputStream(file)));
	}
	
	public static DataInputStream createReader(final Area area, final WriterType type) throws IOException {
		final Path file = AreaForge.getInstance().getDataFolder().toPath().resolve("Areas").resolve(area.getName()).resolve(type.name() + ".dat");
		return new DataInputStream(new GZIPInputStream(Files.newInputStream(file)));
	}
	
	public static byte[] serializeInventory(final ItemStack[] inventory) throws IOException {
		var baos = new ByteArrayOutputStream();
		var oos = new BukkitObjectOutputStream(baos);
		
		oos.writeInt(inventory.length);
		for (ItemStack item : inventory) {
			oos.writeObject(item);
		}
		oos.flush();
		return baos.toByteArray();
	}
	
	public static ItemStack[] deserializeInventory(final byte[] data) throws IOException, ClassNotFoundException {
	    var bais = new ByteArrayInputStream(data);
	    var ois = new BukkitObjectInputStream(bais);
	    
	    int length = ois.readInt();
	    ItemStack[] inventory = new ItemStack[length];

	    for (int i = 0; i < length; i++) {
	        inventory[i] = (ItemStack) ois.readObject();
	    }

	    return inventory;
	}
}

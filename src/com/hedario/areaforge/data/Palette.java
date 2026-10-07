package com.hedario.areaforge.data;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;

public class Palette {
	private final Map<String, Integer> entries = new HashMap<>();
	private final List<BlockData> data = new ArrayList<>();
	
	public int mapData(final String data) {
		final Integer id = entries.putIfAbsent(data, entries.size());
		if (id == null) {
			this.data.add(Bukkit.createBlockData(data));
			return entries.get(data);
		}
		return id;
	}
	
	public void writeToFile(final DataOutputStream writer) throws IOException {
		if (writer == null) {
			throw new IOException("Palette writer couldn't be found?");
		}
		writer.writeInt(getSize());
		for (int i = 0; i < getSize(); i++) {
			writer.writeUTF(getSerialized(i));
		}
		writer.flush();
		writer.close();
	}
	
	public String getSerialized(final int id) {
		return data.get(id).getAsString();
	}
	
	public BlockData getDeserialized(final int id) {
		return data.get(id);
	}
	
	public int getId(final String data) {
		return entries.get(data);
	}
	
	public int getSize() {
		return data.size();
	}
	
	public List<BlockData> getData() {
		return data;
	}
	
	public Map<String, Integer> getEntries() {
		return entries;
	}
}

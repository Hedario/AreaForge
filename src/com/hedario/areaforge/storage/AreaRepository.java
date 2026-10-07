package com.hedario.areaforge.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.hedario.areaforge.Area;
import com.hedario.areaforge.data.ScheduledArea;

public class AreaRepository {
	
	public static void createArea(final Area area) {
		final String query = """
				INSERT INTO Area
				(name, world, min_x, min_y, min_z, max_x, max_y, max_z, blocks, save_containers, save_entities)
				VALUES
				(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
				""";
		try (Connection c = Database.sql.getConnection(); PreparedStatement stmt = c.prepareStatement(query)) {
			Database.sql.bind(stmt, area.getName(), area.getWorld().getName(), area.getMinX(), area.getMinY(), area.getMinZ(), area.getMaxX(), area.getMaxY(), area.getMaxZ(), area.getBlocks(), area.isSaveContainers(), area.isSaveEntities());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Area getArea(final String name) {
		final String query = """
				SELECT *
				FROM Area
				WHERE name = ?;
				""";
		try (Connection c = Database.sql.getConnection(); PreparedStatement stmt = c.prepareStatement(query)) {
			Database.sql.bind(stmt, name);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Area area = new Area(rs.getString("name"), Bukkit.getWorld(rs.getString("world")), rs.getInt("min_x"), rs.getInt("min_y"), rs.getInt("min_z"), rs.getInt("max_x"), rs.getInt("max_y"), rs.getInt("max_z"));
					area.setBlocks(rs.getInt("blocks"));
					area.setSaveContainers(rs.getBoolean("save_containers"));
					area.setSaveEntities(rs.getBoolean("save_entities"));
					area.setCreatedAt(rs.getTimestamp("created_at"));
					return area;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void delete(final String name) {
		final String query = """
				DELETE
				FROM Area
				WHERE name = ?;
				""";
		try (Connection c = Database.sql.getConnection(); PreparedStatement stmt = c.prepareStatement(query)) {
			Database.sql.bind(stmt, name);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static List<Area> getAreas() {
		List<Area> areas = new ArrayList<Area>();
		final String query = """
				SELECT * 
				FROM Area;
				""";
		try (Connection c = Database.sql.getConnection(); PreparedStatement stmt = c.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				Area area = new Area(rs.getString("Name"), Bukkit.getWorld(rs.getString("World")), rs.getInt("min_x"), rs.getInt("min_y"), rs.getInt("min_z"), rs.getInt("max_x"), rs.getInt("max_y"), rs.getInt("max_z"));
				area.setBlocks(rs.getInt("blocks"));
				area.setSaveContainers(rs.getBoolean("save_containers"));
				area.setSaveEntities(rs.getBoolean("save_entities"));
				area.setCreatedAt(rs.getTimestamp("created_at"));
				areas.add(area);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return areas;
	}
	
	public static List<String> getAreaNames() {
		List<String> areas = new ArrayList<String>();
		final String query = """
				SELECT name 
				FROM Area;
				""";
		try (Connection c = Database.sql.getConnection(); PreparedStatement stmt = c.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				 areas.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return areas;
	}
	
	public static Set<ScheduledArea> getScheduledAreas() {
		Set<ScheduledArea> areas = new HashSet<>();
		final String query = """
				SELECT *
				FROM Scheduler;
				""";
		try (Connection c = Database.sql.getConnection(); PreparedStatement stmt = c.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				areas.add(new ScheduledArea(rs.getString("Name"), rs.getBoolean("loading"), rs.getString("time")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return areas;
	}
	
	public static ScheduledArea addScheduledArea(final String name, final boolean enabled, final String time) {
		final String query = """
				INSERT INTO Scheduler
				(name, loading, time)
				VALUES
				(?, ?, ?);
				""";
		try (Connection c = Database.sql.getConnection(); PreparedStatement stmt = c.prepareStatement(query)) {
			Database.sql.bind(stmt, name, enabled, time);
			stmt.executeUpdate();
			return new ScheduledArea(name, enabled, time);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void updateScheduledArea(final String name, final boolean enabled, final String time) {
		final String query = """
				UPDATE Scheduler
				SET loading = ?, time = ?
				WHERE name = ?;
				""";
		try (Connection c = Database.sql.getConnection(); PreparedStatement stmt = c.prepareStatement(query)) {
			Database.sql.bind(stmt, enabled, time, name);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static ScheduledArea getScheduledArea(final String name) {
		final String query = """
				SELECT *
				FROM Scheduler
				WHERE name = ?;
				""";
		try (Connection c = Database.sql.getConnection(); PreparedStatement stmt = c.prepareStatement(query)) {
			Database.sql.bind(stmt, name);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					ScheduledArea area = new ScheduledArea(name, rs.getBoolean("loading"), rs.getString("time"));
					return area;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void setSafeLocation(final String name, final String world, final double x, final double y, final double z) {
		final String query = """
				INSERT INTO Location
				(name, world, x, y, z)
				VALUES
				(?, ?, ?, ?, ?);
				""";
		try (Connection c = Database.sql.getConnection(); PreparedStatement stmt = c.prepareStatement(query)) {
			Database.sql.bind(stmt, name, world, x, y, z);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Location getSafeLocation(final String name) {
		final String query = """
				SELECT world, x, y, z
				FROM Location
				WHERE name = ?;
				""";
		try (Connection c = Database.sql.getConnection(); PreparedStatement stmt = c.prepareStatement(query)) {
			Database.sql.bind(stmt, name);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					World world = Bukkit.getWorld(rs.getString(1));
					return new Location(world, rs.getDouble(2), rs.getDouble(3), rs.getDouble(4));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void updateSafeLocation(final String name, final String world, final double x, final double y, final double z) {
		final String query = """
				UPDATE Location 
				SET world = ?, x = ?, y = ?, z = ?
				WHERE name = ?;
				""";
		try (Connection c = Database.sql.getConnection(); PreparedStatement stmt = c.prepareStatement(query)) {
			Database.sql.bind(stmt, world, x, y, z, name);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

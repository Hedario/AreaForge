package com.hedario.areaforge.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import com.hedario.areaforge.AreaForge;
import com.hedario.areaforge.configuration.ConfigManager;
import com.hedario.areaforge.storage.DatabaseConnection.DatabaseType;

public class Database {
	public static DatabaseConnection sql;

	public static void init(final AreaForge plugin) {
		String engine = ConfigManager.get().getString("Settings.Storage.Engine").toUpperCase();
		DatabaseType type = DatabaseType.SQLITE;
		if (!engine.isBlank()) {
			type = DatabaseType.valueOf(engine);
		}
		plugin.getLogger().info(type.name() + " has been selected as database engine.");
		sql = new DatabaseConnection(plugin, type);
		if (sql != null) {
			try (Connection c = sql.getConnection()) {
				loadTables(c);
			} catch (SQLException e) {
				AreaForge.getLog().severe("Couldn't establish a database connection.\nThe plugin will be disabled.");
				PluginManager pm = Bukkit.getPluginManager();
				pm.disablePlugin(plugin);
				e.printStackTrace();
				return;
			}
		}
	}

	private static void loadTables(Connection connection) throws SQLException {
		final String area = """
				CREATE TABLE IF NOT EXISTS Area (
				name TEXT PRIMARY KEY,
				world TEXT NOT NULL,
				min_x INTEGER NOT NULL,
				min_y INTEGER NOT NULL,
				min_z INTEGER NOT NULL,
				max_x INTEGER NOT NULL,
				max_y INTEGER NOT NULL,
				max_z INTEGER NOT NULL,
				blocks INTEGER NOT NULL DEFAULT 0,
				created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
				save_containers INTEGER NOT NULL DEFAULT 1,
				save_entities INTEGER NOT NULL DEFAULT 1
				);
				""";
		final String scheduler = """
				CREATE TABLE IF NOT EXISTS Scheduler (
				name TEXT PRIMARY KEY,
				loading INTEGER NOT NULL DEFAULT 0,
				time VARCHAR(12) DEFAULT '01:00:00:000',
				FOREIGN KEY (name) REFERENCES Area(name)
					ON UPDATE CASCADE 
					ON DELETE CASCADE
				);
				""";
		final String safeLocation = """
				CREATE TABLE IF NOT EXISTS Location (
				name TEXT PRIMARY KEY,
				world TEXT NOT NULL,
				x FLOAT NOT NULL,
				y FLOAT NOT NULL,
				z FLOAT NOT NULL,
				FOREIGN KEY (Name) REFERENCES Area(name)
					ON UPDATE CASCADE
					ON DELETE CASCADE
				);
				""";
		try (PreparedStatement stmt = connection.prepareStatement(area)) {
			stmt.executeUpdate();
		}
		try (PreparedStatement stmt = connection.prepareStatement(scheduler)) {
			stmt.executeUpdate();
		}
		try (PreparedStatement stmt = connection.prepareStatement(safeLocation)) {
			stmt.executeUpdate();
		}
		AreaForge.getLog().info("Database tables have been loaded succesfully!");
	}

}

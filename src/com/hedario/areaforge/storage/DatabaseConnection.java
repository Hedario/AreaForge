package com.hedario.areaforge.storage;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;

import com.hedario.areaforge.AreaForge;
import com.hedario.areaforge.configuration.ConfigManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnection {
	public enum DatabaseType {
		SQLITE, MYSQL;
	}

	private HikariDataSource source;
	private String host, db, user, pass;
	private int port;

	public DatabaseConnection(final JavaPlugin plugin, final DatabaseType type) {
		final HikariConfig config = new HikariConfig();
		switch (type) {
			case SQLITE -> configureSQLite(plugin, config);
			case MYSQL -> configureMYSQL(config);
		}
	}

	private void configureSQLite(final JavaPlugin plugin, final HikariConfig config) {
		final File db = new File(plugin.getDataFolder(), "AreaForge.db");
		config.setJdbcUrl("jdbc:sqlite:" + db.getAbsolutePath());
		config.setPoolName("AreaForge-SQLite");

		config.setMaximumPoolSize(1);
		config.setMinimumIdle(1);
		config.setLeakDetectionThreshold(10_000);
		this.source = new HikariDataSource(config);
	}

	private void configureMYSQL(final HikariConfig config) {
		host = ConfigManager.get().getString("Settings.Storage.MySQL.Host");
		port = ConfigManager.get().getInt("Settings.Storage.MySQL.Port");
		pass = ConfigManager.get().getString("Settings.Storage.MySQL.Pass");
		db = ConfigManager.get().getString("Settings.Storage.MySQL.Db");
		user = ConfigManager.get().getString("Settings.Storage.MySQL.User");
		
		config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + db);
		config.setPoolName("AreaForge-MySQL");
		config.setUsername(user);
		config.setPassword(pass);
		config.setMaximumPoolSize(5);
		config.setMinimumIdle(1);
		config.addDataSourceProperty("useSSL", "true");
		config.addDataSourceProperty("characterEncoding", "utf8mb4");
		this.source = new HikariDataSource(config);
	}
	
	public void bind(final PreparedStatement stmt, final Object... objects) throws SQLException {
		for (int i = 0; i < objects.length; i++) {
			stmt.setObject(i + 1, objects[i]);
		}
	}
	
	public int execute(final String query, final Object... objects) {
		try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
			bind(stmt, objects);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			AreaForge.getLog().severe("An error occurred while executing a db update.");
			e.printStackTrace();
		}
		return 0;
	}

	public Connection getConnection() throws SQLException {
		return source.getConnection();
	}

	public void close() {
		source.close();
	}

	public HikariDataSource getSource() {
		return source;
	}
}

package com.hedario.areaforge.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.hedario.areaforge.AreaForge;

public class Config {
	private final File file;
	private final FileConfiguration config;

	/**
	 * Creates a new {@link Config} with the file being the configuration file.
	 *
	 * @param file The file to create/load
	 */
	public Config(final File file) {
		this.file = new File(AreaForge.getInstance().getDataFolder() + File.separator + file);
		this.config = YamlConfiguration.loadConfiguration(this.file);
		try {
			this.reload();
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a file for the {@link FileConfiguration} object. If there are missing
	 * folders, this method will try to create them before create a file for the
	 * config.
	 */
	public void create() {
		if (!this.file.getParentFile().exists()) {
			try {
				this.file.getParentFile().mkdir();
				AreaForge.getInstance().getLogger().info("Generating new directory for " + this.file.getName() + "!");
			} catch (final Exception e) {
				AreaForge.getInstance().getLogger().info("Failed to generate directory!");
				e.printStackTrace();
			}
		}

		if (!this.file.exists()) {
			try {
				this.file.createNewFile();
				AreaForge.getInstance().getLogger().info("Generating new " + this.file.getName() + "!");
			} catch (final Exception e) {
				AreaForge.getInstance().getLogger().info("Failed to generate " + this.file.getName() + "!");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the {@link FileConfiguration} object from the {@link Config}.
	 *
	 * @return the file configuration object
	 */
	public FileConfiguration get() {
		return this.config;
	}

	/**
	 * Reloads the {@link FileConfiguration} object. If the config object does not
	 * exist it will run {@link #create()} first before loading the config.
	 * @throws InvalidConfigurationException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void reload() throws FileNotFoundException, IOException, InvalidConfigurationException {
		this.create();
		this.config.load(this.file);
	}

	/**
	 * Saves the {@link FileConfiguration} object.
	 * {@code config.options().copyDefaults(true)} is called before saving the
	 * config.
	 */
	public void save() {
		try {
			this.config.options().copyDefaults(true);
			this.config.save(this.file);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}

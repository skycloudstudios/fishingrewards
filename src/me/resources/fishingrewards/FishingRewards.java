package me.resources.fishingrewards;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class FishingRewards extends JavaPlugin {
	
	private File configFile;
	private FileConfiguration config;
	public WorldGuardPlugin worldGuardPlugin;
	Logger log = Logger.getLogger("Minecraft");
	public List<String> regions = new ArrayList<String>();
	
	
	public WorldGuardPlugin wg() {
		Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}
		
		return (WorldGuardPlugin) plugin;
		
	}
	
	public FileConfiguration getConfig() {
		return this.config;
	}
	
	public File getConfigFile() {
		return this.configFile;
	}
	
	public void createConfig() {
		configFile = new File(getDataFolder(), "settings.yml");
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			saveResource("settings.yml", false);
		}
		
		config = new YamlConfiguration();
		try {
			config.load(configFile);
		} catch
			(IOException | InvalidConfigurationException e) {
				e.printStackTrace();
		}
	}
	
	
	public void onEnable() {
		
		createConfig();
		
		if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
			log.info("[FishingRewards] Successfully hooked into WorldGuard.");
			regions = this.getConfig().getStringList("Settings.regions");
			worldGuardPlugin = wg();
			log.info("[FishingRewards] Successfully recovered region list.");
		} else {
			Bukkit.getPluginManager().disablePlugin(this);
		}
		
		log.info("[FishingRewards] Successfully recovered commands list.");
		
		Bukkit.getPluginManager().registerEvents(new FishingHandler(this), this);
		log.info("[FishingRewards] Successfully hooked into fishing handler.");
		
		log.info("[FishingRewards] The plugin has been successfully loaded.");
	}
	
	public void onDisable() {
		log.info("[FishingRewards] The plugin has been successfully disabled.");
	}
}

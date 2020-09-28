package me.resources.fishingrewards;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class FishingHandler implements Listener {
	private static FishingRewards plugin;
	public FishingHandler(FishingRewards instance) {
		plugin = instance;
	}
	
	public boolean isWithinRegion(Location loc, String regionName, Player p) {
		
		LocalPlayer player = plugin.worldGuardPlugin.wrapPlayer(p);
		World world = player.getWorld();
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regionm = container.get(world);
        ApplicableRegionSet set = (regionm.getApplicableRegions( BukkitAdapter.asBlockVector(loc)));


        for ( ProtectedRegion region : set )
            if (region.getId().equalsIgnoreCase(regionName))
                return true;
           
        return false;
 
    }
	
	
	@EventHandler
	public void fish(PlayerFishEvent e) {
		List<String> regions = plugin.regions;
		List<String> commands = new ArrayList<String>();
		
		for (String s : plugin.getConfig().getConfigurationSection("Settings.Items").getKeys(false)) {
			commands.add(s);
		}
		
		double chance = 0;
		double chanceRand = Math.random()*100;
		int max = plugin.getConfig().getInt("Settings.max-rewards");
		int start = 0;
		Entity en = e.getCaught();
		for (String r : regions) {
			if (isWithinRegion(e.getPlayer().getLocation(), r, e.getPlayer())) {
				for (String s : commands) {
					chance = plugin.getConfig().getDouble("Settings.Items." + s + ".chance");
					String permission = plugin.getConfig().getString("Settings.Items." + s + ".permission");
					if (start <= max) {
					if (chance >= chanceRand) {
						if (en != null && e.getPlayer().hasPermission(permission)) {
							en = null;
							start++;
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfig().getString("Settings.Items." + s + ".command").replace("%player%", e.getPlayer().getName()));
						} else {
							en = null;
							start++;
							}
						}
					} else {
						start = 0;
					}
				}
			}
		}
	}
}

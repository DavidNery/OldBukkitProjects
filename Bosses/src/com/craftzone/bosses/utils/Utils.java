package com.craftzone.bosses.utils;

import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;

import com.craftzone.bosses.Bosses;

public class Utils {
	
	private final Bosses instance;
	
	private String lastKillerWither, lastKillerDragon;
	private boolean witherLiving, dragonLiving;
	
	public Utils(Bosses instance) {
		this.instance = instance;
		this.lastKillerWither = instance.getConfig().getString("Last_KillerWither");
		this.lastKillerDragon = instance.getConfig().getString("Last_KillerDragon");
		this.witherLiving = false;
		this.dragonLiving = false;
	}
	
	public void setWitherSpawnLocation(Location loc) {
		instance.getConfig().set("Config.WitherSpawnLocation", getStringByLocation(loc));
		instance.saveConfig();
		instance.reloadConfig();
	}
	
	public void setDragonSpawnLocation(Location loc) {
		instance.getConfig().set("Config.DragonSpawnLocation", getStringByLocation(loc));
		instance.saveConfig();
		instance.reloadConfig();
	}
	
	private String getStringByLocation(Location loc) {
		return loc.getWorld().getName() + " " + ((int) loc.getX()) + " " + ((int) loc.getY()) + " " + ((int) loc.getZ());
	}
	
	private Location getLocationByString(String loc) {
		String[] partes = loc.split(" ");
		return new Location(instance.getServer().getWorld(partes[0]), 
				Double.parseDouble(partes[1]), Double.parseDouble(partes[2]), Double.parseDouble(partes[3]));
	}
	
	public void setLastKillerWither(String lastKillerWither) {
		this.lastKillerWither = lastKillerWither;
		this.witherLiving = false;
		instance.getConfig().set("Last_KillerWither", lastKillerWither);
		instance.saveConfig();
		instance.reloadConfig();
	}

	public String getLastKillerWither() {
		return lastKillerWither;
	}
	
	public void setLastKillerDragon(String lastKillerDragon) {
		this.lastKillerDragon = lastKillerDragon;
		this.dragonLiving = false;
		instance.getConfig().set("Last_KillerDragon", lastKillerDragon);
		instance.saveConfig();
		instance.reloadConfig();
	}
	
	public void spawnDragon() {
		Location loc = getLocationByString(instance.getConfig().getString("Config.DragonSpawnLocation"));
		EnderDragon dragon = (EnderDragon) loc.getWorld().spawnEntity(loc, EntityType.ENDER_DRAGON);
		dragon.setCustomName(instance.getConfig().getString("Config.DragonName").replace("&", "§"));
		dragon.setCustomNameVisible(true);
		dragon.setMaxHealth(instance.getConfig().getInt("Config.DragonHealth"));
		dragon.setHealth(dragon.getMaxHealth());
	}
	
	public void spawnWither() {
		Location loc = getLocationByString(instance.getConfig().getString("Config.WitherSpawnLocation"));
		Wither wither = (Wither) loc.getWorld().spawnEntity(loc, EntityType.WITHER);
		wither.setCustomName(instance.getConfig().getString("Config.WitherName").replace("&", "§"));
		wither.setCustomNameVisible(true);
		wither.setMaxHealth(instance.getConfig().getInt("Config.WitherHealth"));
		wither.setHealth(wither.getMaxHealth());
	}

	public String getLastKillerDragon() {
		return lastKillerDragon;
	}
	
	public void setWitherLiving(boolean witherLiving) {
		this.witherLiving = witherLiving;
	}

	public boolean isWitherLiving() {
		return witherLiving;
	}
	
	public void setDragonLiving(boolean dragonLiving) {
		this.dragonLiving = dragonLiving;
	}

	public boolean isDragonLiving() {
		return dragonLiving;
	}
	

}

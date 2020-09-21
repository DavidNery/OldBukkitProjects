package me.zfork.craftzone.mobspawn.utils;

import java.util.ArrayList;
import java.util.List;

import me.zfork.craftzone.mobspawn.MobSpawn;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class MobSpawnerUtils {
	
	private MobSpawn instance;
	private List<MobSpawner> mobspawners;
	
	public MobSpawnerUtils(MobSpawn instance) {
		this.instance = instance;
		this.mobspawners = new ArrayList<MobSpawner>();
	}
	
	public void addMobSpawner(String mobSpawnerName, Location loc, int quantidade, int tempo, EntityType type, int raio){
		MobSpawner mobspawner = new MobSpawner(mobSpawnerName, loc, quantidade, tempo, type, raio);
		mobspawners.add(mobspawner);
		mobspawner.runTaskTimer(instance, 0, tempo*20);
	}
	
	public void delMobSpawner(String mobSpawnerName){
		for(MobSpawner ms : mobspawners)
			if(ms.getMobSpawnerName().equalsIgnoreCase(mobSpawnerName)){
				ms.cancel();
				mobspawners.remove(ms);
				return;
			}
	}
	
	public boolean hasMobSpawner(String nome){
		return mobspawners.stream().anyMatch(mobSpawner -> mobSpawner.getMobSpawnerName().equalsIgnoreCase(nome));
	}
	
	public List<MobSpawner> getMobspawners() {
		return mobspawners;
	}
	
	public Location getLocationByString(String loc){
		String[] partes = loc.split(" ");
		return new Location(instance.getServer().getWorld(partes[0]), 
				Double.parseDouble(partes[1]), Double.parseDouble(partes[2]), Double.parseDouble(partes[3]));
	}
	
	public String getStringByLocation(Location loc){
		return loc.getWorld().getName() + " " + ((int) loc.getX()) + " " + ((int) loc.getY()) + " " + ((int) loc.getZ());
	}

}

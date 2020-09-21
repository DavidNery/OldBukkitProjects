package me.zfork.craftzone.mobspawn.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

public class MobSpawner extends BukkitRunnable{

	private String mobSpawnerName;
	private Location loc;
	private int quantidade, tempo, raio;
	private EntityType type;

	public MobSpawner(String mobSpawnerName, Location loc, int quantidade, int tempo, EntityType type, int raio){
		this.mobSpawnerName = mobSpawnerName;
		this.loc = loc;
		this.quantidade = quantidade;
		this.tempo = tempo;
		this.type = type;
		this.raio = raio;
	}

	public String getMobSpawnerName() {
		return mobSpawnerName;
	}

	public Location getLoc() {
		return loc;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public int getTempo() {
		return tempo;
	}

	public EntityType getType() {
		return type;
	}

	public int getRaio() {
		return raio;
	}

	@Override
	public void run() {
		spawner:
		for(int i = 0; i<quantidade; i++){
			int j = 0;
			Entity entity = loc.getWorld().spawnEntity(loc, type);
			if(j >= 1){
				for(Entity entities : entity.getNearbyEntities(raio, raio, raio)){
					if(entity.getType().equals(entities.getType())){
						entity.remove();
						break spawner;
					}
				}
			}
			j++;
		}
	}

}

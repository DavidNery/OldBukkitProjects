package me.zfork.fx1.arenas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.zfork.fx1.FX1;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ArenaManager {
	
	private FX1 instance;
	private ArrayList<Arena> arenas;
	
	public ArenaManager(FX1 instance){
		this.instance = instance;
		this.arenas = new ArrayList<Arena>();
	}
	
	public void loadAllArenas(){
		int i = 0;
		for(File f : new File(instance.getDataFolder(), "arenas").listFiles()){
			Arena arena = new Arena(f.getName().replace(".yml", ""));
			FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
			arena.setLocationPlayer1(getLocationByString(fc.getString("Arena.Player1")));
			arena.setLocationPlayer2(getLocationByString(fc.getString("Arena.Player2")));
			arena.setLocationCamarote(getLocationByString(fc.getString("Arena.Camarote")));
			arena.setLocationSaida(getLocationByString(fc.getString("Arena.Saida")));
			arena.setFile(f);
			arena.setFC(fc);
			arenas.add(arena);
			i++;
		}
		instance.getServer().getConsoleSender().sendMessage(" §b" + i + " §3arenas carregadas!");
	}
	
	public ArrayList<Arena> getArena(){
		return this.arenas;
	}
	
	public void loadArena(String nome){
		arenas.add(new Arena(nome));
	}
	
	public Arena getArenaLivre(){
		for(Arena arena : arenas){
			if(arena.getUsando() == false) return arena;
		}
		return null;
	}
	
	public Arena getArena(String nome){
		for(Arena arena : arenas){
			if(arena.getNome().equalsIgnoreCase(nome)) return arena;
		}
		return null;
	}
	
	public void setArenaLocation(Arena arena, String location, Location loc){
		switch (location.toLowerCase()) {
		case "player1":
			arena.setLocationPlayer1(loc);
			break;
		case "player2":
			arena.setLocationPlayer2(loc);
			break;
		case "camarote":
			arena.setLocationCamarote(loc);
			break;
		case "saida":
			arena.setLocationSaida(loc);
			break;
		}
		arena.getFC().set("Arena." + location, getStringByLocation(loc));
		try {
			arena.getFC().save(arena.getFile());
			arena.getFC().load(arena.getFile());
		} catch (IOException | InvalidConfigurationException e) {
		}
	}
	
	public Location getLocationByString(String loc){
		String[] partes = loc.split(" ");
		return new Location(instance.getServer().getWorld(partes[0]), Double.parseDouble(partes[1]), Double.parseDouble(partes[2]), Double.parseDouble(partes[3]),
				Float.parseFloat(partes[4]), Float.parseFloat(partes[5]));
	}
	
	public String getStringByLocation(Location loc){
		String string = "";
		string += loc.getWorld().getName() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " " + loc.getYaw() + " " + loc.getPitch();
		return string;
	}

}

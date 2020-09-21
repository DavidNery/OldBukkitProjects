package me.dery.dpulapula;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PulaPulaManager {
	
	static DPulaPula instance = DPulaPula.getDPulaPula();
	
	public static void SetSpawn(Player p){
		File f = new File(instance.getDataFolder(), "locais.yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		fc.set("Locais.Spawn.World", p.getLocation().getWorld().getName());
		fc.set("Locais.Spawn.X", p.getLocation().getX());
		fc.set("Locais.Spawn.Y", p.getLocation().getY());
		fc.set("Locais.Spawn.Z", p.getLocation().getZ());
		fc.set("Locais.Spawn.Yaw", p.getLocation().getYaw());
		fc.set("Locais.Spawn.Pitch", p.getLocation().getPitch());
		try{
			fc.save(f);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void SetExit(Player p){
		File f = new File(instance.getDataFolder(), "locais.yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		fc.set("Locais.Saida.World", p.getLocation().getWorld().getName());
		fc.set("Locais.Saida.X", p.getLocation().getX());
		fc.set("Locais.Saida.Y", p.getLocation().getY());
		fc.set("Locais.Saida.Z", p.getLocation().getZ());
		fc.set("Locais.Saida.Yaw", p.getLocation().getYaw());
		fc.set("Locais.Saida.Pitch", p.getLocation().getPitch());
		try{
			fc.save(f);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void SetLobby(Player p){
		File f = new File(instance.getDataFolder(), "locais.yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		fc.set("Locais.Lobby.World", p.getLocation().getWorld().getName());
		fc.set("Locais.Lobby.X", p.getLocation().getX());
		fc.set("Locais.Lobby.Y", p.getLocation().getY());
		fc.set("Locais.Lobby.Z", p.getLocation().getZ());
		fc.set("Locais.Lobby.Yaw", p.getLocation().getYaw());
		fc.set("Locais.Lobby.Pitch", p.getLocation().getPitch());
		try{
			fc.save(f);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void TPSpawn(Player p){
		File f = new File(instance.getDataFolder(), "locais.yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		World world = Bukkit.getServer().getWorld(fc.getString("Locais.Spawn.World"));
		double x = fc.getDouble("Locais.Spawn.X");
		double y = fc.getDouble("Locais.Spawn.Y");
		double z = fc.getDouble("Locais.Spawn.Z");
		float yaw = (float) fc.getDouble("Locais.Spawn.Yaw");
		float pitch = (float) fc.getDouble("Locais.Spawn.Pitch");
		p.teleport(new Location(world, x, y, z, yaw, pitch));
	}
	
	public static void TPLobby(Player p){
		File f = new File(instance.getDataFolder(), "locais.yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		World world = Bukkit.getServer().getWorld(fc.getString("Locais.Lobby.World"));
		double x = fc.getDouble("Locais.Lobby.X");
		double y = fc.getDouble("Locais.Lobby.Y");
		double z = fc.getDouble("Locais.Lobby.Z");
		float yaw = (float) fc.getDouble("Locais.Lobby.Yaw");
		float pitch = (float) fc.getDouble("Locais.Lobby.Pitch");
		p.teleport(new Location(world, x, y, z, yaw, pitch));
	}
	
	public static void TPExit(Player p){
		File f = new File(instance.getDataFolder(), "locais.yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		World world = Bukkit.getServer().getWorld(fc.getString("Locais.Saida.World"));
		double x = fc.getDouble("Locais.Saida.X");
		double y = fc.getDouble("Locais.Saida.Y");
		double z = fc.getDouble("Locais.Saida.Z");
		float yaw = (float) fc.getDouble("Locais.Saida.Yaw");
		float pitch = (float) fc.getDouble("Locais.Saida.Pitch");
		p.teleport(new Location(world, x, y, z, yaw, pitch));
	}
	
	public static Location Spawn(){
		File f = new File(instance.getDataFolder(), "locais.yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		World world = Bukkit.getServer().getWorld(fc.getString("Locais.Spawn.World"));
		double x = fc.getDouble("Locais.Spawn.X");
		double y = fc.getDouble("Locais.Spawn.Y");
		double z = fc.getDouble("Locais.Spawn.Z");
		float yaw = (float) fc.getDouble("Locais.Saida.Yaw");
		float pitch = (float) fc.getDouble("Locais.Saida.Pitch");
		Location loc = new Location(world, x, y, z, yaw, pitch);
		return loc;
	}
	
	public static Location Lobby(){
		File f = new File(instance.getDataFolder(), "locais.yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		World world = Bukkit.getServer().getWorld(fc.getString("Locais.Lobby.World"));
		double x = fc.getDouble("Locais.Lobby.X");
		double y = fc.getDouble("Locais.Lobby.Y");
		double z = fc.getDouble("Locais.Lobby.Z");
		float yaw = (float) fc.getDouble("Locais.Saida.Yaw");
		float pitch = (float) fc.getDouble("Locais.Saida.Pitch");
		Location loc = new Location(world, x, y, z, yaw, pitch);
		return loc;
	}
	
	public static Location Exit(){
		File f = new File(instance.getDataFolder(), "locais.yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		World world = Bukkit.getServer().getWorld(fc.getString("Locais.Saida.World"));
		double x = fc.getDouble("Locais.Saida.X");
		double y = fc.getDouble("Locais.Saida.Y");
		double z = fc.getDouble("Locais.Saida.Z");
		float yaw = (float) fc.getDouble("Locais.Saida.Yaw");
		float pitch = (float) fc.getDouble("Locais.Saida.Pitch");
		Location loc = new Location(world, x, y, z, yaw, pitch);
		return loc;
	}
	
	public static boolean CheckSpawn(){
		File f = new File(instance.getDataFolder(), "locais.yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		if(!f.exists()){
			return true;
		}else if(fc.getString("Locais.Spawn.World") == null){
			return true;
		}else if(fc.getString("Locais.Spawn.X") == null){
			return true;
		}else if(fc.getString("Locais.Spawn.Y") == null){
			return true;
		}else if(fc.getString("Locais.Spawn.Z") == null){
			return true;
		}else if(fc.getString("Locais.Spawn.Yaw") == null){
			return true;
		}else if(fc.getString("Locais.Spawn.Pitch") == null){
			return true;
		}
		return false;
	}
	
	public static boolean CheckExit(){
		File f = new File(instance.getDataFolder(), "locais.yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		if(!f.exists()){
			return true;
		}else if(fc.getString("Locais.Saida.World") == null){
			return true;
		}else if(fc.getString("Locais.Saida.X") == null){
			return true;
		}else if(fc.getString("Locais.Saida.Y") == null){
			return true;
		}else if(fc.getString("Locais.Saida.Z") == null){
			return true;
		}else if(fc.getString("Locais.Saida.Yaw") == null){
			return true;
		}else if(fc.getString("Locais.Saida.Pitch") == null){
			return true;
		}
		return false;
	}
	
	public static boolean CheckLobby(){
		File f = new File(instance.getDataFolder(), "locais.yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		if(!f.exists()){
			return true;
		}else if(fc.getString("Locais.Lobby.World") == null){
			return true;
		}else if(fc.getString("Locais.Lobby.X") == null){
			return true;
		}else if(fc.getString("Locais.Lobby.Y") == null){
			return true;
		}else if(fc.getString("Locais.Lobby.Z") == null){
			return true;
		}else if(fc.getString("Locais.Lobby.Yaw") == null){
			return true;
		}else if(fc.getString("Locais.Lobby.Pitch") == null){
			return true;
		}
		return false;
	}

}

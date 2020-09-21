package me.zfork.farenas.arena;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.zfork.farenas.FArenas;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ArenaManager {
	
	static FArenas instance = FArenas.getFArenas();
	static ArrayList<Arena> arenasLoaded = new ArrayList<Arena>();
	//private ArrayList<String> players = new ArrayList<String>();
	
	public static void loadAllArenas(){
		for(File file : new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas").listFiles()){
			FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
			Arena arena = new Arena(file.getName().replace(".yml", ""), fc);
			if(fc.contains("Flags") && fc.getStringList("Flags").size() > 0){
				for(String flags : fc.getStringList("Flags")){
					String[] flag = flags.split(" ");
					arena.getFlags().add(flag[0] + " " + flag[1]);
					setFlag(arena.getNome(), flag[0], flag[1]);
				}
			}
			if(fc.contains("Items") && fc.getStringList("Items").size() > 0){
				for(String item : fc.getStringList("Items")){
					arena.getItens().add(instance.buildItemStack(item));
				}
			}
			if(fc.contains("Helmet")){
				arena.getArmor()[0] = instance.buildItemStack(fc.getString("Helmet"));
			}
			if(fc.contains("Peitoral")){
				arena.getArmor()[1] = instance.buildItemStack(fc.getString("Peitoral"));
			}
			if(fc.contains("Calca")){
				arena.getArmor()[2] = instance.buildItemStack(fc.getString("Calca"));
			}
			if(fc.contains("Botas")){
				arena.getArmor()[3] = instance.buildItemStack(fc.getString("Botas"));
			}
			/*if(fc.contains("Signs") && fc.getStringList("Signs").size() > 0){
				for(String signs : fc.getStringList("Signs")){
					Sign sign = (Sign) getLocation(signs).getBlock().getState();
					arena.getSigns().add(sign);
				}
			}*/
			for(String locs : fc.getStringList("Spawns")){
				arena.getSpawns().add(getLocation(locs));
			}
			arena.setExit(getLocation(fc.getString("Exit")));
			arenasLoaded.add(arena);
		}
	}
	
	public static void loadArena(File file, FileConfiguration fc){
		Arena arena = new Arena(file.getName().replace(".yml", ""), fc);
		if(fc.contains("Flags") && fc.getStringList("Flags").size() > 0){
			for(String flags : fc.getStringList("Flags")){
				String[] flag = flags.split(" ");
				setFlag(arena.getNome(), flag[0], flag[1]);
				arena.getFlags().add(flag[0] + " " + flag[1]);
			}
		}
		if(fc.contains("Items") && fc.getStringList("Items").size() > 0){
			for(String item : fc.getStringList("Items")){
				arena.getItens().add(instance.buildItemStack(item));
			}
		}
		if(fc.contains("Helmet")){
			arena.getArmor()[0] = instance.buildItemStack(fc.getString("Helmet"));
		}
		if(fc.contains("Peitoral")){
			arena.getArmor()[1] = instance.buildItemStack(fc.getString("Peitoral"));
		}
		if(fc.contains("Calca")){
			arena.getArmor()[2] = instance.buildItemStack(fc.getString("Calca"));
		}
		if(fc.contains("Botas")){
			arena.getArmor()[3] = instance.buildItemStack(fc.getString("Botas"));
		}
		for(String locs : fc.getStringList("Spawns")){
			arena.getSpawns().add(getLocation(locs));
		}
		arena.setExit(getLocation(fc.getString("Exit")));
		arenasLoaded.add(arena);
	}
	
	public static void reloadArena(String nome){
		Arena arena = getArenaByName(nome);
		arena.getItens().clear();
		for(int i = 0; i < arena.getArmor().length; i++){
			arena.getArmor()[i] = null;
		}
		arena.getFlags().clear();
		arena.getSpawns().clear();
		if(arena.getFC().contains("Flags") && arena.getFC().getStringList("Flags").size() > 0){
			for(String flags : arena.getFC().getStringList("Flags")){
				String[] flag = flags.split(" ");
				arena.getFlags().add(flag[0] + " " + flag[1]);
			}
		}
		if(arena.getFC().contains("Items") && arena.getFC().getStringList("Items").size() > 0){
			for(String item : arena.getFC().getStringList("Items")){
				arena.getItens().add(instance.buildItemStack(item));
			}
		}
		if(arena.getFC().contains("Helmet")){
			arena.getArmor()[0] = instance.buildItemStack(arena.getFC().getString("Helmet"));
		}
		if(arena.getFC().contains("Peitoral")){
			arena.getArmor()[1] = instance.buildItemStack(arena.getFC().getString("Peitoral"));
		}
		if(arena.getFC().contains("Calca")){
			arena.getArmor()[2] = instance.buildItemStack(arena.getFC().getString("Calca"));
		}
		if(arena.getFC().contains("Botas")){
			arena.getArmor()[3] = instance.buildItemStack(arena.getFC().getString("Botas"));
		}
		for(String locs : arena.getFC().getStringList("Spawns")){
			arena.getSpawns().add(getLocation(locs));
		}
		arena.setExit(getLocation(arena.getFC().getString("Exit")));
		try {
			arena.getFC().save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
			arena.getFC().load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public static void criarArena(String nome){
		File f = new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + "example.yml");
		File f2 = null;
		if(f.exists()){
			f.renameTo(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + "example 1.yml"));
			instance.saveResource("arenas/example.yml", true);
			f2 = new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + "example.yml");
			f2.renameTo(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
			new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + "example 1.yml").renameTo(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + "example.yml"));
		}else{
			instance.saveResource("arenas/example.yml", true);
			f2 = new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + "example.yml");
			f2.renameTo(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
		}
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f2);
		fc.set("Permissao", "farenas.arena." + nome);
		try {
			fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
			fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		loadArena(f2, fc);
	}
	
	public static void removeArena(String nome){
		arenasLoaded.remove(getArenaByName(nome));
		new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml").delete();
	}
	
	public static void addSpawn(String nome, Location loc){
		Arena arena = getArenaByName(nome);
		List<String> spawns = arena.getFC().getStringList("Spawns");
		spawns.add(getLocationString(loc));
		arena.getFC().set("Spawns", spawns);
		reloadArena(nome);
	}
	
	public static void removeSpawn(String nome, int spawn){
		Arena arena = getArenaByName(nome);
		List<String> spawns = arena.getFC().getStringList("Spawns");
		spawns.remove(spawn);
		arena.getFC().set("Spawns", spawns);
		reloadArena(nome);
	}
	
	public static void setExit(String nome, Location loc){
		Arena arena = getArenaByName(nome);
		arena.setExit(loc);
		arena.getFC().set("Exit", getLocationString(loc));
		reloadArena(nome);
	}
	
	public static ArrayList<String> getPlayersInArena(String nome){
		for(Arena arenas : arenasLoaded){
			if(arenas.getNome().equalsIgnoreCase(nome)) return arenas.getPlayers();
		}
		return null;
	}
	
	public static ArrayList<Arena> getArenasLoaded(){
		return arenasLoaded;
	}
	
	public static Arena getArenaByPlayer(Player p){
		for(Arena arena : arenasLoaded){
			if(arena.getPlayers().contains(p.getName().toLowerCase())) return arena;
		}
		return null;
	}
	
	public static Arena getArenaByName(String name){
		for(Arena arena : arenasLoaded){
			if(arena.getNome().equalsIgnoreCase(name)) return arena;
		}
		return null;
	}
	
	public static boolean hasArena(String nome){
		for(Arena arena : arenasLoaded){
			if(arena.getNome().equalsIgnoreCase(nome)) return true;
		}
		return false;
	}
	
	public static Location getLocation(String location){
		String[] partes = location.split(" ");
		Location loc = new Location(instance.getServer().getWorld("world"), 0, 0, 0, 0, 0);
		loc.setWorld(instance.getServer().getWorld(partes[0]));
		loc.setX(Double.valueOf(partes[1]));
		loc.setY(Double.valueOf(partes[2]));
		loc.setZ(Double.valueOf(partes[3]));
		loc.setYaw(Float.valueOf(partes[4]));
		loc.setYaw(Float.valueOf(partes[5]));
		return loc;
	}
	
	public static String getLocationString(Location location){
		return location.getWorld().getName() + " " + location.getX() + " " + location.getY() + " " + location.getZ() + " " + location.getYaw() + " " + location.getPitch();
	}
	
	public static boolean hasFlag(String nome, String flag){
		Arena arena = getArenaByName(nome);
		for(String flags : arena.getFlags())
			if(flags.startsWith(flag.toLowerCase())) return true;
		return false;
	}
	
	public static String getFlagValue(String nome, String flag){
		Arena arena = getArenaByName(nome);
		for(String flags : arena.getFlags()){
			if(flags.startsWith(flag.toLowerCase())){
				return flags.split(" ")[1];
			}
		}
		return null;
	}
	
	public static void delFlag(String nome, String flag){
		Arena arena = getArenaByName(nome);
		for(String flags : arena.getFlags()){
			if(flags.startsWith(flag.toLowerCase())){
				arena.getFlags().remove(flags);
				try{
					arena.getFC().set("Flags", arena.getFlags());
					arena.getFC().save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + arena.getNome() + ".yml"));
					arena.getFC().load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + arena.getNome() + ".yml"));
				}catch(Exception e){}
				return;
			}
		}
	}
	
	public static void setFlag(String nome, String flag, Object value){
		Arena arena = getArenaByName(nome);
		switch(flag.toLowerCase()){
		case "maxplayers":
			if(Integer.parseInt(String.valueOf(value)) <= 1){
				arena.setMaxPlayers(0);
			}else{
				arena.setMaxPlayers(Integer.parseInt(String.valueOf(value)));
			}
			break;
		case "manutencao":
			try{
				arena.setManutencao(Boolean.parseBoolean(String.valueOf(value)));
			}catch(Exception e){}
			break;
		case "keepinventory":
			try{
				arena.setKeepInventory(Boolean.parseBoolean(String.valueOf(value)));
			}catch(Exception e){}
			break;
		case "dropinventory":
			try{
				arena.setDropInventory(Boolean.parseBoolean(String.valueOf(value)));
			}catch(Exception e){}
			break;
		case "dckill":
			try{
				arena.setDCKill(Boolean.parseBoolean(String.valueOf(value)));
			}catch(Exception e){}
			break;
		case "healonkill":
			try{
				arena.setHealOnKill(Boolean.parseBoolean(String.valueOf(value)));
			}catch(Exception e){}
			break;
		case "moneyonkill":
			try{
				arena.setMoneyOnKill(Integer.parseInt(String.valueOf(value)));
			}catch(Exception e){}
			break;
		default:
			try{
				arena.setMoneyOnKill(0);
			}catch(Exception e){}
			break;
		}
	}

}

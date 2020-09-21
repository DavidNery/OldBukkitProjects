package me.zfork.farenas.arena;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class Arena {
	
	private String nome;
	//private int maxplayers, moneyonkill;
	//private boolean manutencao, keepinventory, dropinventory, dckill, healonkill;
	private ArrayList<String> players;
	private ArrayList<Location> spawns;
	//private ArrayList<Sign> signs;
	private Location exit;
	private FileConfiguration fc;
	private List<String> flags;
	private ItemStack[] armor;
	private ArrayList<ItemStack> inv;
	
	public Arena(String nome, FileConfiguration fc){
		this.nome = nome;
		/*this.maxplayers = 0;
		this.manutencao = false;
		this.keepinventory = false;
		this.dropinventory = true;
		this.dckill = false;
		this.healonkill = false;
		this.moneyonkill = 0;*/
		this.players = new ArrayList<String>();
		this.spawns = new ArrayList<Location>();
		this.inv = new ArrayList<ItemStack>();
		this.exit = null;
		this.fc = fc;
		this.flags = new ArrayList<String>();
		this.armor = new ItemStack[4];
	}
	
	public String getNome(){
		return this.nome;
	}
	
	/*public int getMaxPlayers(){
		return this.maxplayers;
	}*/
	
	public void setMaxPlayers(int maxplayers){
		//this.maxplayers = maxplayers;
		for(int i = 0; i<flags.size(); i++){
			if(flags.get(i).startsWith("maxplayers")){
				flags.set(i, "maxplayers " + maxplayers);
				try{
					fc.set("Flags", flags);
					fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
					fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
				}catch(Exception e){}
				return;
			}
		}
		flags.add("maxplayers " + maxplayers);
		try{
			fc.set("Flags", flags);
			fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
			fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
		}catch(Exception e){}
	}
	
	/*public boolean getManutencao(){
		return this.manutencao;
	}*/
	
	public void setManutencao(boolean manutencao){
		//this.manutencao = manutencao;
		for(int i = 0; i<flags.size(); i++){
			if(flags.get(i).startsWith("manutencao")){
				flags.set(i, "manutencao " + manutencao);
				try{
					fc.set("Flags", flags);
					fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
					fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
				}catch(Exception e){}
				return;
			}
		}
		flags.add("manutencao " + manutencao);
		try{
			fc.set("Flags", flags);
			fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
			fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
		}catch(Exception e){}
	}
	
	/*public boolean getKeepInventory(){
		return this.keepinventory;
	}*/
	
	public void setKeepInventory(boolean keepinventory){
		//this.keepinventory = keepinventory;
		for(int i = 0; i<flags.size(); i++){
			if(flags.get(i).startsWith("keepinventory")){
				flags.set(i, "keepinventory " + keepinventory);
				try{
					fc.set("Flags", flags);
					fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
					fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
				}catch(Exception e){}
				return;
			}
		}
		flags.add("keepinventory " + keepinventory);
		try{
			fc.set("Flags", flags);
			fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
			fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
		}catch(Exception e){}
	}
	
	/*public boolean getDropInventory(){
		return this.dropinventory;
	}*/
	
	public void setDropInventory(boolean dropinventory){
		//this.dropinventory = dropinventory;
		for(int i = 0; i<flags.size(); i++){
			if(flags.get(i).startsWith("dropinventory")){
				flags.set(i, "dropinventory " + dropinventory);
				try{
					fc.set("Flags", flags);
					fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
					fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
				}catch(Exception e){}
				return;
			}
		}
		flags.add("dropinventory " + dropinventory);
		try{
			fc.set("Flags", flags);
			fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
			fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
		}catch(Exception e){}
	}
	
	/*public boolean getDCKill(){
		return this.dckill;
	}*/
	
	public void setDCKill(boolean dckill){
		//this.dckill = dckill;
		for(int i = 0; i<flags.size(); i++){
			if(flags.get(i).startsWith("dckill")){
				flags.set(i, "dckill " + dckill);
				try{
					fc.set("Flags", flags);
					fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
					fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
				}catch(Exception e){}
				return;
			}
		}
		flags.add("dckill " + dckill);
		try{
			fc.set("Flags", flags);
			fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
			fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
		}catch(Exception e){}
	}
	
	/*public boolean getHealOnKill(){
		return this.healonkill;
	}*/
	
	public void setHealOnKill(boolean healonkill){
		//this.healonkill = healonkill;
		for(int i = 0; i<flags.size(); i++){
			if(flags.get(i).startsWith("healonkill")){
				flags.set(i, "healonkill " + healonkill);
				try{
					fc.set("Flags", flags);
					fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
					fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
				}catch(Exception e){}
				return;
			}
		}
		flags.add("healonkill " + healonkill);
		try{
			fc.set("Flags", flags);
			fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
			fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
		}catch(Exception e){}
	}
	
	/*public int getMoneyOnKill(){
		return this.moneyonkill;
	}*/
	
	public void setClearedInventory(boolean clearedinventory){
		//this.healonkill = healonkill;
		for(int i = 0; i<flags.size(); i++){
			if(flags.get(i).startsWith("clearedinventory")){
				flags.set(i, "clearedinventory " + clearedinventory);
				try{
					fc.set("Flags", flags);
					fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
					fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
				}catch(Exception e){}
				return;
			}
		}
		flags.add("clearedinventory " + clearedinventory);
		try{
			fc.set("Flags", flags);
			fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
			fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
		}catch(Exception e){}
	}
	
	public void setClearInventory(boolean clearinventory){
		//this.healonkill = healonkill;
		for(int i = 0; i<flags.size(); i++){
			if(flags.get(i).startsWith("clearinventory")){
				flags.set(i, "clearinventory " + clearinventory);
				try{
					fc.set("Flags", flags);
					fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
					fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
				}catch(Exception e){}
				return;
			}
		}
		flags.add("clearinventory " + clearinventory);
		try{
			fc.set("Flags", flags);
			fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
			fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
		}catch(Exception e){}
	}
	
	public void setMoneyOnKill(int moneyonkill){
		//this.moneyonkill = moneyonkill;
		for(int i = 0; i<flags.size(); i++){
			if(flags.get(i).startsWith("moneyonkill")){
				flags.set(i, "moneyonkill " + moneyonkill);
				try{
					fc.set("Flags", flags);
					fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
					fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
				}catch(Exception e){}
				return;
			}
		}
		flags.add("moneyonkill " + moneyonkill);
		try{
			fc.set("Flags", flags);
			fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
			fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
		}catch(Exception e){}
	}
	
	public void setNoDrop(boolean clearinventory){
		//this.healonkill = healonkill;
		for(int i = 0; i<flags.size(); i++){
			if(flags.get(i).startsWith("nodrop")){
				flags.set(i, "nodrop " + clearinventory);
				try{
					fc.set("Flags", flags);
					fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
					fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
				}catch(Exception e){}
				return;
			}
		}
		flags.add("nodrop " + clearinventory);
		try{
			fc.set("Flags", flags);
			fc.save(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
			fc.load(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + nome + ".yml"));
		}catch(Exception e){}
	}
	
	public ArrayList<String> getPlayers(){
		return this.players;
	}
	
	/*public ArrayList<Sign> getSigns(){
		return this.signs;
	}*/
	
	public List<String> getFlags(){
		return this.flags;
	}
	
	public FileConfiguration getConfig(){
		return this.fc;
	}
	
	public ArrayList<Location> getSpawns(){
		return this.spawns;
	}
	
	public Location getExit(){
		return this.exit;
	}
	
	public void setExit(Location exit){
		this.exit = exit;
	}
	
	public FileConfiguration getFC(){
		return this.fc;
	}
	
	public ArrayList<ItemStack> getItens(){
		return this.inv;
	}
	
	public ItemStack[] getArmor(){
		return this.armor;
	}

}

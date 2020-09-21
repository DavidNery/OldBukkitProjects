package com.seekinggames.seekingmoney;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.seekinggames.seekingmoney.databases.MySQL;
import com.seekinggames.seekingmoney.databases.SQLite;

public class SeekingMoney extends JavaPlugin{
	
	public static ArrayList<Conta> players = new ArrayList<Conta>();
	public static HashMap<String, Double> tops = new HashMap<String, Double>();
	public static ArrayList<String> topsarray = new ArrayList<String>();
	public static int bdtype = 0;
	public static Contas contas;
	public static MySQL mysql;
	public static SQLite sqlite;
	public static UpdateMoney um;
	
	public void onEnable() {
		getServer().getConsoleSender().sendMessage("§bHabilitando Plugin...");
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			getServer().getConsoleSender().sendMessage("§2Config criada!");
		}
		if(getConfig().getBoolean("MySQL.Ativar") == true){
			try{
				mysql = new MySQL(getConfig().getString("MySQL.User"), getConfig().getString("MySQL.Password")
						, getConfig().getString("MySQL.Host"), getConfig().getString("MySQL.DataBase"));
				mysql.putAllUsers();
				mysql.putTops();
				bdtype = 1;
			}catch(Exception e){
				getServer().getConsoleSender().sendMessage("§4Nao foi possivel se conectar ao MySQL");
				if(!new File(getDataFolder(), "contas.db").exists()){
					try {
						new File(getDataFolder(), "contas.db").createNewFile();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				sqlite = new SQLite();
				sqlite.putTops();
				bdtype = 2;
			}
		}else{
			if(!new File(getDataFolder(), "contas.db").exists()){
				try {
					new File(getDataFolder(), "contas.db").createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			sqlite = new SQLite();
			sqlite.putAllUsers();
			sqlite.putTops();
			bdtype = 2;
		}
		um = new UpdateMoney(this);
		new BukkitRunnable() {
			public void run() {
				um.start();
			}
		}.runTaskLater(this, 60*20);
		getCommand("money").setExecutor(new Comandos());
		Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
		getServer().getConsoleSender().sendMessage("§aPlugin habilitado!");
		if(getServer().getPluginManager().isPluginEnabled("Vault")) {
			getServer().getServicesManager().register(Economy.class, new VaultIntegration(this), this, ServicePriority.Highest);
		}
	}

	public void onDisable() {
		HandlerList.unregisterAll(this);
		getServer().getConsoleSender().sendMessage("§cPlugin Desabilitado!");
	}

	public static SeekingMoney getSeekingMoney() {
		return (SeekingMoney) Bukkit.getServer().getPluginManager().getPlugin("SeekingMoney");
	}
	
	public ArrayList<Conta> getPlayers(){
		return players;
	}
	
	public HashMap<String, Double> getTops(){
		return tops;
	}
	
	public ArrayList<String> getTopsArray(){
		return topsarray;
	}
	
	public int getBDType(){
		return bdtype;
	}
	
	public UpdateMoney getUM(){
		return um;
	}
	
	public Contas getAPI(){
		return contas;
	}
	
	public MySQL getMySQL(){
		return mysql;
	}
	
	public SQLite getSQLite(){
		return sqlite;
	}

}

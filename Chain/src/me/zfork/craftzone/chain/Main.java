package me.zfork.craftzone.chain;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import me.zfork.craftzone.chain.utils.SQLite;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	
	public static Economy economy;
	private static String PLUGIN_NAME;
	private ChainManager chainmanager;
	private SQLite sqlite;
	
	public void onEnable(){
		PLUGIN_NAME = getDescription().getName();
		ConsoleCommandSender sender = getServer().getConsoleSender();
		sender.sendMessage("§3==========[§b" + PLUGIN_NAME + "§3]==========");
		sender.sendMessage(" §3Status: §bAtivado");
		sender.sendMessage(" §3By: §bzFork");
		sender.sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			sender.sendMessage(" §3Config: §bCriada");
		}else{
			sender.sendMessage(" §3Config: §bJa Existente");
		}
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec != null) economy = (Economy) ec.getProvider();
		this.chainmanager = new ChainManager(this);
		try {
			this.sqlite = new SQLite(this);
		} catch (ClassNotFoundException | SQLException | IOException e) {}
		getCommand("chain").setExecutor(new Comandos());
		getServer().getPluginManager().registerEvents(new Eventos(), this);
		if(getConfig().getBoolean("Config.Dar_Tag_Top_Killer")) getServer().getPluginManager().registerEvents(new LegendChat(), this);
		sender.sendMessage("§3==========[§b" + PLUGIN_NAME + "§3]==========");
	}

	public void onDisable(){
		ConsoleCommandSender sender = getServer().getConsoleSender();
		sender.sendMessage("§4==========[§c" + PLUGIN_NAME + "§4]==========");
		sender.sendMessage(" §4Status: §cDesativado");
		sender.sendMessage(" §4By: §czFork");
		sender.sendMessage(" §4Versao: §c" + getDescription().getVersion());
		sender.sendMessage("§4==========[§c" + PLUGIN_NAME + "§4]==========");
	}

	public static Main getMain(){
		return (Main) Bukkit.getServer().getPluginManager().getPlugin(PLUGIN_NAME);
	}
	
	public ChainManager getChainmanager() {
		return chainmanager;
	}
	
	public SQLite getSqlite() {
		return sqlite;
	}
	
}

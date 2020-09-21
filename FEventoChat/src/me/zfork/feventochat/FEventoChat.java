package me.zfork.feventochat;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class FEventoChat extends JavaPlugin{
	
	public static BukkitTask task = null;
	public static String frase = "";
	public static Economy econ = null;
	
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§aPlugin Ativado!");
		hookEconomy();
		getCommand("eventochat").setExecutor(new Comandos());
		getServer().getPluginManager().registerEvents(new Eventos(), this);
	}
	
	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§4Plugin Desativado!");
	}
	
	public static FEventoChat getFEventoChat(){
		return (FEventoChat) Bukkit.getServer().getPluginManager().getPlugin("FEventoChat");
	}
	
	public boolean hookEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec != null){
			econ = (Economy) ec.getProvider();
		}
		return econ != null;
	}

}

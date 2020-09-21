package me.dery.hcaixas;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class HCaixas extends JavaPlugin{
	
	@Override
	public void onEnable() {
		getServer().getConsoleSender().sendMessage("§3Plugin Habilitado!");
		saveDefaultConfig();
		getCommand("caixa").setExecutor(new Comandos());
		Bukkit.getServer().getPluginManager().registerEvents(new Eventos(), this);
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		getServer().getConsoleSender().sendMessage("§4Plugin Desabilitado!");
	}
	
	public static HCaixas getHCaixas(){
		return (HCaixas) Bukkit.getServer().getPluginManager().getPlugin("HCaixas");
	}

}

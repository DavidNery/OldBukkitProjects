package me.zfork.fadmin;

import me.zfork.fadmin.comandos.Comandos;
import me.zfork.fadmin.listeners.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class FAdmin extends JavaPlugin{
	
	private static String PLUGIN_NAME;
	
	public void onEnable(){
		PLUGIN_NAME = getDescription().getName();
		ConsoleCommandSender sender = getServer().getConsoleSender();
		sender.sendMessage("§3==========[§b" + PLUGIN_NAME + "§3]==========");
		sender.sendMessage(" §3Status: §bAtivado");
		sender.sendMessage(" §3By: §bzFork");
		sender.sendMessage(" §3Versao: §b" + getDescription().getVersion());
		new Comandos(this);
		new Listeners(this);
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

	public static FAdmin getFAdmin(){
		return (FAdmin) Bukkit.getServer().getPluginManager().getPlugin(PLUGIN_NAME);
	}

}

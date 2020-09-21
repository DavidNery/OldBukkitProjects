package me.zfork.fstafflovers;

import java.io.File;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FStaffLovers extends JavaPlugin{
	
	private String PLUGIN_NAME;
	
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
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e){
		Player p = e.getPlayer();
		if(p.hasPermission("fstafflovers.staff")){
			
		}
	}

}

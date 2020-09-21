package me.zfork.spartanoscraft.spartanosmention;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.channels.types.Channel;

public class SpartanosMention extends JavaPlugin{
	
	public static String PLUGIN_NAME;
	
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
		for(Channel channel : Legendchat.getChannelManager().getChannels()){
			if(getConfig().contains("Canais." + channel.getName())) continue;
			getConfig().set("Canais." + channel.getName() + ".Ativar", true);
			getConfig().set("Canais." + channel.getName() + ".Cor_Marcacao", "&b");
			getConfig().set("Canais." + channel.getName() + ".Mensagem_Marcacao", "&cPlayer &7{player} &clhe marcou no canal &7" + channel.getName());
			getConfig().set("Canais." + channel.getName() + ".Sound", "LEVEL_UP");
		}
		saveConfig();
		reloadConfig();
		new Listeners(this);
		sender.sendMessage("§3==========[§b" + PLUGIN_NAME + "§3]==========");
	}

	public void onDisable(){
		HandlerList.unregisterAll(this);
		ConsoleCommandSender sender = getServer().getConsoleSender();
		sender.sendMessage("§4==========[§c" + PLUGIN_NAME + "§4]==========");
		sender.sendMessage(" §4Status: §cDesativado");
		sender.sendMessage(" §4By: §czFork");
		sender.sendMessage(" §4Versao: §c" + getDescription().getVersion());
		sender.sendMessage("§4==========[§c" + PLUGIN_NAME + "§4]==========");
	}

	public static SpartanosMention getSpartanosMention(){
		return (SpartanosMention) Bukkit.getServer().getPluginManager().getPlugin(PLUGIN_NAME);
	}

}

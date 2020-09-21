package me.zfork.hconsole;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class HConsole extends JavaPlugin implements Listener{
	
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bDP§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bzFork");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
		}else{
			getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
		}
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		getServer().getConsoleSender().sendMessage("§3==========[§bDP§3]==========");
	}

	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§4==========[§cDP§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §czFork");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cDP§4]==========");
	}
	
	@EventHandler
	public void Join(PlayerJoinEvent e){
		Player p = e.getPlayer();
		for(String nome : getConfig().getStringList("Nomes")){
			if(p.getName().toLowerCase().contains(nome.toLowerCase())){
				String msg = "";
				for(String mensagem : getConfig().getStringList("Kick")){
					msg += (mensagem + "\n");
				}
				p.kickPlayer(msg.replace("&", "§"));
				return;
			}
		}
	}

}

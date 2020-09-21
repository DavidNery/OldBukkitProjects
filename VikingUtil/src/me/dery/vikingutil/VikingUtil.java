package me.dery.vikingutil;

import java.io.File;
import java.text.NumberFormat;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;

public class VikingUtil extends JavaPlugin implements Listener{
	
	public static Economy econ = null;
	
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bVikingUtil§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bDery");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null){
			getServer().getConsoleSender().sendMessage(" §3Vault: §bNao Encontrado");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}else{
			getServer().getConsoleSender().sendMessage(" §3Vault: §bHooked (Economy)");
			if(!new File(getDataFolder(), "config.yml").exists()){
				saveDefaultConfig();
				getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
			}else{
				getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
			}
			setupEconomy();
			Bukkit.getServer().getPluginManager().registerEvents(this, this);
		}
		getServer().getConsoleSender().sendMessage("§3==========[§bVikingUtil§3]==========");
	}
	
	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§4==========[§cVikingUtil§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §cDery");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cVikingUtil§4]==========");
	}

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}
	
	@EventHandler(ignoreCancelled=true)
	public void Chat(ChatMessageEvent e){
		Player p = e.getSender();
		if(p.hasPermission("vikingutil.admin")) return;
		if(e.getChannel().getName().equalsIgnoreCase("global")){
			if(econ.has(p.getName(), getConfig().getDouble("Preco_Falar"))){
				econ.depositPlayer(p.getName(), e.getChannel().getCostPerMessage());
			}else{
				p.sendMessage(getConfig().getString("Sem_Dinheiro").replace("&", "§").replace("@valor", NumberFormat.getNumberInstance().format(getConfig().getDouble("Preco_Falar"))));
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void Fly(PlayerTeleportEvent e){
		Player p = e.getPlayer();
		if(p.hasPermission("vikingutil.admin")) return;
		if(!getConfig().getStringList("Mundos_Fly").contains(e.getTo().getWorld().getName())){
			if(p.getAllowFlight() == true || p.isFlying()){
				p.setAllowFlight(false);
				p.setFlying(false);
			}
		}
	}

}

package com.seekinggames.seekingmito;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;

public class Eventos implements Listener{
	
	private SeekingMito instance = SeekingMito.getSeekingMito();
	
	@EventHandler
	public void Chat(ChatMessageEvent e){
		Player p = e.getSender();
		if(p.getName().equalsIgnoreCase(instance.getConfig().getString("Mito")) && e.getTags().contains("seekingmito")){
			e.setTagValue("seekingmito", instance.getConfig().getString("TAG").replace("&", "§"));
		}
	}
	
	@EventHandler
	public void Death(PlayerDeathEvent e){
		if(e.getEntity() instanceof Player && e.getEntity().getKiller() instanceof Player){
			Player p = e.getEntity();
			if(p.getName().equals(instance.getConfig().getString("Mito"))){
				Player killer = p.getKiller();
				instance.setMito(killer);
			}
		}
	}
	
	@EventHandler
	public void Entrar(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(p.getName().equals(instance.getConfig().getString("Mito"))){
			for(String msg : instance.getConfig().getStringList("Mito_Entrou")){
				for(Player player : Bukkit.getOnlinePlayers()){
					player.sendMessage(msg.replace("&", "§").replace("{mito}", p.getName()));
				}
			}
			p.setDisplayName(instance.getConfig().getString("TAG").replace("&", "§") + p.getDisplayName());
		}
	}
	
	@EventHandler
	public void Sair(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(p.getName().equals(instance.getConfig().getString("Mito"))){
			for(String msg : instance.getConfig().getStringList("Mito_Saiu")){
				for(Player player : Bukkit.getOnlinePlayers()){
					player.sendMessage(msg.replace("&", "§").replace("{mito}", p.getName()));
				}
			}
			p.setDisplayName(instance.getConfig().getString("TAG").replace("&", "§") + p.getDisplayName());
		}
	}

}

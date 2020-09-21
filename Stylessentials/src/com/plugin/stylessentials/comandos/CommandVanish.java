package com.plugin.stylessentials.comandos;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CommandVanish implements CommandExecutor, Listener {
	
	/*
	 * vanish
	 */
	
	public static ArrayList<String> vanishPlayers = new ArrayList<String>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Vanish") && sender instanceof Player) {
			if(!sender.hasPermission("cmds.vanish")) {
				sender.sendMessage("§cVocê precisa ser [Admin] ou superior para executar este comando!");
				return true;
			}
			Player player = (Player)sender;
			if(!vanishPlayers.contains(player.getName())) {
				vanishPlayers.add(player.getName());
				for(Player all : Bukkit.getOnlinePlayers()) {
					if(vanishPlayers.contains(player.getName())) {
						all.hidePlayer(player);
						if(all.hasPermission("cmds.vanish")) {
							all.showPlayer(player);
						}
					}
				}
				player.sendMessage("§aVanish: ligado.");
				return true;
			}
			vanishPlayers.remove(player.getName());
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(vanishPlayers.contains(player.getName())) {
					all.showPlayer(player);
				}
			}
			player.sendMessage("§cVanish: desligado.");
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onJoinVanish(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		for(Player all : Bukkit.getOnlinePlayers()) {
			if(vanishPlayers.contains(p.getName())) {
				all.hidePlayer(p);
				if(all.hasPermission("cmds.vanish")) {
					all.showPlayer(p);
				}
			}
		}
	}

}

package com.plugin.stylessentials.comandos;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class CommandGod implements CommandExecutor, Listener {
	
	/*
	 * god
	 */
	
	public static ArrayList<String> godMode = new ArrayList<>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("God") && sender instanceof Player) {
			if(!sender.hasPermission("cmds.god")) {
				sender.sendMessage("§cVocê precisa ser [Admin] ou superior para executar este comando!");
				return true;
			}
			Player player = (Player)sender;
			if(!godMode.contains(player.getName())) {
				godMode.add(player.getName());
				sender.sendMessage("§aGod: ligado.");
				return true;
			}
			godMode.remove(player.getName());
			sender.sendMessage("§cGod: desligado.");
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if(godMode.contains(player.getName())) {
				event.setCancelled(true);
				return;
			}
		}
	}

}

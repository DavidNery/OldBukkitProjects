package com.plugin.stylessentials.comandos;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.plugin.stylessentials.Util;

public class CommandKillall extends Util implements CommandExecutor {
	
	/*
	 * killall
	 */
	
	public static ArrayList<String> players = new ArrayList<String>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Killall")) {
			if(!sender.hasPermission("cmds.killall")) {
				sender.sendMessage("§cVocê precisa ser [Admin] ou superior para executar este comando!");
				return true;
			}
			sender.sendMessage("§aDando kill em todos os players.");
			for(Player all : Bukkit.getServer().getOnlinePlayers()) {
				players.add(all.getName());
				players.remove(sender.getName());
				players.remove(CommandGod.godMode);
				if(players.contains(all.getName())) {
					all.setHealth(0.0);
					all.sendMessage("");
					all.sendMessage("§aTodos os players receberam kill.");
					all.sendMessage("");
				}
			}
			players.clear();
			return true;
		}
		return false;
	}

}

package com.plugin.stylessentials.comandos;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.plugin.stylessentials.Util;

public class CommandTpall extends Util implements CommandExecutor {
	
	/*
	 * tpall
	 */
	
public static ArrayList<String> players = new ArrayList<String>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Tpall") && sender instanceof Player) {
			if(!sender.hasPermission("cmds.tpall")) {
				sender.sendMessage("§cVocê precisa ser [Admin] ou superior para executar este comando!");
				return true;
			}
			sender.sendMessage("§aTeleportando todos os players até você.");
			Player player = (Player)sender;
			for(Player all : Bukkit.getServer().getOnlinePlayers()) {
				players.add(all.getName());
				players.remove(player.getName());
				if(players.contains(all.getName())) {
					teleportPlayer(all, player);
					all.sendMessage("");
					all.sendMessage("§aTodos os players teleportados até: " + player.getName() + ".");
					all.sendMessage("");
				}
			}
			players.clear();
			return true;
		}
		return false;
	}

}

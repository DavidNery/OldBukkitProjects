package com.plugin.stylessentials.comandos;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.plugin.stylessentials.Util;

public class CommandTitle extends Util implements CommandExecutor {
	
	/*
	 * title <mensagem>
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Title")) {
			if(!sender.hasPermission("cmds.title")) {
				sender.sendMessage("§cVocê precisa ser [Admin] ou superior para executar este comando!");
				return true;
			}
			if(args.length == 0) {
				sender.sendMessage("§cUtilize /title <mensagem>");
				return true;
			}else if(args.length >= 1) {
				String msg = "";
				for(int i = 0; i < args.length; i++) {
					msg = msg + args[i] + " ";
				}
				for(Player all : Bukkit.getServer().getOnlinePlayers()) {
					sendTitle(all, msg.replace("&", "§"));
				}
				return true;
			}
			return true;
		}
		return false;
	}

}

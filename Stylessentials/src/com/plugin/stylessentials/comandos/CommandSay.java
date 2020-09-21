package com.plugin.stylessentials.comandos;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSay implements CommandExecutor {
	
	/*
	 * say <mensagem>
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Say")) {
			if(!sender.hasPermission("cmds.say")) {
				sender.sendMessage("§cVocê precisa ser [Admin] ou superior para executar este comando!");
				return true;
			}
			if(args.length == 0) {
				sender.sendMessage("§cUtilize /say <mensagem>");
				return true;
			}else if(args.length >= 1) {
				String msg = "";
				for(int i = 0; i < args.length; i++) {
					msg = msg + args[i] + " ";
				}
				for(Player all : Bukkit.getServer().getOnlinePlayers()) {
					all.sendMessage("");
					all.sendMessage("§d§l[Anúncio] §d" + msg);
					all.sendMessage("");
				}
				return true;
			}
			return true;
		}
		return false;
	}

}

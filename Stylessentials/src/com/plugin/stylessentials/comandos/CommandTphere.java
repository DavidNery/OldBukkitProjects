package com.plugin.stylessentials.comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.plugin.stylessentials.Util;

public class CommandTphere extends Util implements CommandExecutor {
	
	/*
	 * tphere <player>
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Tphere")) {
			if(!sender.hasPermission("cmds.tphere") && sender instanceof Player) {
				sender.sendMessage("§cVocê precisa ser [Admin] ou superior para executar este comando!");
				return true;
			}
			if(args.length == 0) {
				sender.sendMessage("§cUtilize /tphere <player>");
				return true;
			}else if(args.length >= 1) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage("§c" + args[0] + " não encontrado!");
					return true;
				}
				Player player = (Player)sender;
				teleportPlayer(target, player);
				player.sendMessage("§a" + target.getName() + " teleportado para: " + player.getName() + ".");
				return true;
			}
			return true;
		}
		return false;
	}

}

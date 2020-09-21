package com.plugin.stylessentials.comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.plugin.stylessentials.Util;

public class CommandTp extends Util implements CommandExecutor {
	
	/*
	 * tp <player> [player]
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Tp")) {
			if(!sender.hasPermission("cmds.tp")) {
				sender.sendMessage("§cVocê precisa ser [Admin] ou superior para executar este comando!");
				return true;
			}
			if(args.length == 0) {
				sender.sendMessage("§cUtilize /tp <player> [player]");
				return true;
			}else if((args.length == 1) && sender instanceof Player) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage("§c" + args[0] + " não encontrado!");
					return true;
				}
				Player player = (Player)sender;
				teleportPlayer(player, target);
				player.sendMessage("§a" + player.getName() + " teleportado para: " + target.getName() + ".");
				return true;
			}else if(args.length >= 2) {
				Player target = sender.getServer().getPlayer(args[0]);
				Player target2 = sender.getServer().getPlayer(args[1]);
				if(target == null) {
					sender.sendMessage("§c" + args[0] + " não encontrado!");
					return true;
				}
				if(target2 == null) {
					sender.sendMessage("§c" + args[1] + " não encontrado!");
					return true;
				}
				teleportPlayer(target, target2);
				sender.sendMessage("§a" + target.getName() + " teleportado para: " + target2.getName() + ".");
				return true;
			}
			return true;
		}
		return false;
	}

}

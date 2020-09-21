package com.plugin.stylessentials.comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRocket implements CommandExecutor {
	
	/*
	 * rocket [player]
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Rocket")) {
			if(!sender.hasPermission("cmds.rocket")) {
				sender.sendMessage("§cVocê precisa ser [Admin] ou superior para executar este comando!");
				return true;
			}
			if((args.length == 0) && sender instanceof Player) {
				Player player = (Player)sender;
				player.setVelocity(player.getVelocity().setY(2));
				return true;
			}else if(args.length >= 1) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage("§c" + args[0] + " não encontrado!");
					return true;
				}
				target.setVelocity(target.getVelocity().setY(2));
				return true;
			}
			return true;
		}
		return false;
	}

}

package com.plugin.stylessentials.comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandKill implements CommandExecutor {
	
	/*
	 * kill [player]
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Kill")) {
			if(!sender.hasPermission("cmds.kill")) {
				sender.sendMessage("§cVocê precisa ser [Admin] ou superior para executar este comando!");
				return true;
			}
			if((args.length == 0) && sender instanceof Player) {
				Player player = (Player)sender;
				if(CommandGod.godMode.contains(player.getName())) {
					sender.sendMessage("§cVocê esta em modo god.");
					return true;
				}
				player.setHealth(0.0);
				player.sendMessage("§aVocê se matou.");
				return true;
			}else if(args.length >= 1) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage("§c" + args[0] + " não encontrado!");
					return true;
				}
				if(CommandGod.godMode.contains(target.getName())) {
					sender.sendMessage("§c" + target.getName() + " esta em modo god.");
					return true;
				}
				target.setHealth(0.0);
				target.sendMessage("§aVocê recebeu kill.");
				sender.sendMessage("§aKill em: " + target.getName() + ".");
				return true;
			}
			return true;
		}
		return false;
	}

}

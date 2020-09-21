package com.plugin.stylessentials.comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHeal implements CommandExecutor {
	
	/*
	 * heal [player]
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Heal")) {
			if(!sender.hasPermission("cmds.heal")) {
				sender.sendMessage("§cVocê precisa ser [Admin] ou superior para executar este comando!");
				return true;
			}
			if((args.length == 0) && sender instanceof Player) {
				Player player = (Player)sender;
				player.setHealth(player.getMaxHealth());
				player.setFoodLevel(30);
				player.setFireTicks(0);
				player.sendMessage("§aVocê foi curado.");
				return true;
			}else if(args.length >= 1) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage("§c" + args[0] + " não encontrado!");
					return true;
				}
				target.setHealth(target.getMaxHealth());
				target.setFoodLevel(30);
				target.setFireTicks(0);
				target.sendMessage("§aVocê foi curado.");
				sender.sendMessage("§aCurou a vida de: " + target.getName() + ".");
				return true;
			}
			return true;
		}
		return false;
	}

}

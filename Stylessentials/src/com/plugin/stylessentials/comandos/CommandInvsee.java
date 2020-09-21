package com.plugin.stylessentials.comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CommandInvsee implements CommandExecutor {
	
	/*
	 * invsee <player>
	 */
	
	public static Inventory inv;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Invsee") && sender instanceof Player) {
			if(!sender.hasPermission("cmds.invsee")) {
				sender.sendMessage("§cVocê precisa ser [Admin] ou superior para executar este comando!");
				return true;
			}
			if(args.length == 0) {
				sender.sendMessage("§cUtilize /invsee <player>");
				return true;
			}else if(args.length >= 1) {
				Player player = (Player)sender;
				Player target = player.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage("§c" + args[0] + " não encontrado!");
					return true;
				}
				inv = target.getInventory();
				player.openInventory(inv);
				return true;
			}
			return true;
		}
		return false;
	}

}

package com.plugin.stylessentials.comandos;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CommandHat implements CommandExecutor {
	
	/*
	 * hat
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Hat") && sender instanceof Player) {
			if(!sender.hasPermission("cmds.hat")) {
				sender.sendMessage("§cVocê precisa ser [Admin] ou superior para executar este comando!");
				return true;
			}
			Player player = (Player)sender;
			final PlayerInventory inv = player.getInventory();
			final ItemStack head = inv.getHelmet();
			if(player.getItemInHand().getType() != Material.AIR) {
				final ItemStack hand = player.getItemInHand();
				inv.setHelmet(hand);
				inv.remove(hand);
				inv.setItemInHand(head);
			}else {
				player.sendMessage("§cVocê não tem um item em sua mão.");
			}
			return true;
		}
		return false;
	}

}

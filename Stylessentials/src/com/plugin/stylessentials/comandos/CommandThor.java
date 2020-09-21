package com.plugin.stylessentials.comandos;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandThor implements CommandExecutor {
	
	/*
	 * thor [player]
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Thor")) {
			if(!sender.hasPermission("cmds.thor")) {
				sender.sendMessage("§cVocê precisa ser [Admin] ou superior para executar este comando!");
				return true;
			}
			if((args.length == 0) && sender instanceof Player) {
				Player player = (Player)sender;
				Block b = player.getTargetBlock((Set<Material>)null, 101);
				player.getWorld().strikeLightning(b.getLocation());
				return true;
			}else if(args.length >= 1) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage("§c" + args[0] + " não encontrado!");
					return true;
				}
				target.getWorld().strikeLightning(target.getLocation());
				sender.sendMessage("§aThor em " + target.getName() + ".");
				return true;
			}
			return true;
		}
		return false;
	}

}

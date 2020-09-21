package com.plugin.stylessentials.comandos;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.plugin.stylessentials.Util;

public class CommandTppos extends Util implements CommandExecutor {
	
	/*
	 * tppos <player> <x> <y> <z> [yaw] [pitch]
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Tppos")) {
			if(!sender.hasPermission("cmds.tppos")) {
				sender.sendMessage("§cVocê precisa ser [Admin] ou superior para executar este comando!");
				return true;
			}
			if(args.length <= 3) {
				sender.sendMessage("§cUtilize /tppos <player> <x> <y> <z> [yaw] [pitch]");
				return true;
			}else if(args.length == 4) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage("§c" + args[0] + " não encontrado!");
					return true;
				}
				double x = Double.valueOf(args[1]);
				double y = Double.valueOf(args[2]);
				double z = Double.valueOf(args[3]);
				Location loc = new Location(target.getWorld(), x, y, z);
				target.teleport(loc);
				sender.sendMessage("§a" + target.getName() + " teleportado para: x:" + x + " y:" + y + " z:" + z);
				return true;
			}else if(args.length == 5) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage("§c" + args[0] + " não encontrado!");
					return true;
				}
				double x = Double.valueOf(args[1]);
				double y = Double.valueOf(args[2]);
				double z = Double.valueOf(args[3]);
				double yaw = Double.valueOf(args[4]);
				Location loc = new Location(target.getWorld(), x, y, z, (int)yaw, (int)0);
				target.teleport(loc);
				sender.sendMessage("§a" + target.getName() + " teleportado para: x:" + x + " y:" + y + " z:" + z + " yaw:" + yaw);
				return true;
			}else if(args.length >= 6) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage("§c" + args[0] + " não encontrado!");
					return true;
				}
				double x = Double.valueOf(args[1]);
				double y = Double.valueOf(args[2]);
				double z = Double.valueOf(args[3]);
				double yaw = Double.valueOf(args[4]);
				double pitch = Double.valueOf(args[5]);
				Location loc = new Location(target.getWorld(), x, y, z, (int)yaw, (int)pitch);
				target.teleport(loc);
				sender.sendMessage("§a" + target.getName() + " teleportado para: x:" + x + " y:" + y + " z:" + z + " yaw: " + yaw + " pitch:" + pitch);
				return true;
			}
			return true;
		}
		return false;
	}

}

package com.plugin.stylessentials.comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.plugin.stylessentials.Util;

public class CommandSpeed implements CommandExecutor {
	
	/*
	 * speed <player> <andar/voar> <0-10>
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Speed")) {
			if(!sender.hasPermission("cmds.speed")) {
				sender.sendMessage("§cVocê precisa ser [Admin] ou superior para executar este comando!");
				return true;
			}
			if(args.length <= 2) {
				if(args.length == 2) {
					Player target = sender.getServer().getPlayer(args[0]);
					if(target == null) {
						sender.sendMessage("§c" + args[0] + " não encontrado!");
						return true;
					}
					if(args[1].equalsIgnoreCase("reset")) {
						target.setWalkSpeed((float) 0.2);
						target.setFlySpeed((float) 0.2);
						sender.sendMessage("§aSpeed de: " + target.getName() + " foi resetado.");
						return true;
					}
					return true;
				}
				sender.sendMessage("§cUtilize /speed <player> <[reset]/andar/voar> <0-10>");
				return true;
			}else if(args.length >= 3) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage("§c" + args[0] + " não encontrado!");
					return true;
				}
				if(!Util.containsNumber(args[2])) {
					sender.sendMessage("§c" + args[2] + " não é número.");
					return true;
				}
				double speed = Double.valueOf(args[2]);
				if(speed < 0) {
					sender.sendMessage("§cA velocidade não pode ser menor do que 0.");
					return true;
				}
				if(speed > 10) {
					sender.sendMessage("§cA velocidade não pode ser maior do que 10.");
					return true;
				}
				if(args[1].equalsIgnoreCase("andar")) {
					target.setWalkSpeed((float) (speed / 10));
					sender.sendMessage("§aSpeed de: " + target.getName() + " andar foi definido para: " + speed);
					return true;
				}else if(args[1].equalsIgnoreCase("voar")) {
					target.setFlySpeed((float) (speed / 10));
					sender.sendMessage("§aSpeed de: " + target.getName() + " voar foi definido para: " + speed);
					return true;
				}else {
					sender.sendMessage("§cUtilize /speed <player> <[reset]/andar/voar> <0-10>");
				}
				return true;
			}
			return true;
		}
		return false;
	}

}

package com.plugin.stylessentials.comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.plugin.stylessentials.Stylessentials;
import com.plugin.stylessentials.Util;

public class CommandFirework extends Util implements CommandExecutor {
	
	private Stylessentials instance = Stylessentials.getStylessentials();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Firework")) {
			if(!sender.hasPermission("stylessentials.firework")) {
				sender.sendMessage(instance.getConfig().getString("Sem_Permissao").replace("&", "§").replace("{cmd}", label));
				return true;
			}
			if((args.length == 0) && sender instanceof Player) {
				Player player = (Player)sender;
				shootFireWork(player);
				return true;
			}else if(args.length == 1) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage(instance.getConfig().getString("Player_Nao_Encontrado").replace("&", "§").replace("{player}", args[0]));
					return true;
				}
				shootFireWork(target);
				return true;
			}else if(args.length >= 2) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage(instance.getConfig().getString("Player_Nao_Encontrado").replace("&", "§").replace("{player}", args[0]));
					return true;
				}
				if(!containsNumber(args[1])) {
					sender.sendMessage(instance.getConfig().getString("Nao_E_Numero").replace("&", "§"));
					return true;
				}
				int number = Integer.valueOf(args[1]);
				for(int i = 0; i < number; i++) {
					shootFireWork(target);
				}
				return true;
			}
			return true;
		}
		return false;
	}

}

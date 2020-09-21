package com.plugin.stylessentials.comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.plugin.stylessentials.Stylessentials;

public class CommandClear implements CommandExecutor {
	
	private Stylessentials instance = Stylessentials.getStylessentials();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("clear")) {
			if(!sender.hasPermission("stylessentials.clear")) {
				sender.sendMessage(instance.getConfig().getString("Sem_Permissao").replace("&", "§").replace("{cmd}", label));
				return true;
			}
			if((args.length == 0) && sender instanceof Player) {
				Player player = (Player) sender;
				player.getInventory().clear();
				player.sendMessage(instance.getConfig().getString("Clear.Limpou").replace("&", "§"));
				return true;
			}else if(args.length >= 1) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage(instance.getConfig().getString("Player_Nao_Encontrado").replace("&", "§").replace("{player}", args[0]));
					return true;
				}
				target.getInventory().clear();
				target.sendMessage(instance.getConfig().getString("Clear.Limpou").replace("&", "§"));
				sender.sendMessage(instance.getConfig().getString("Clear.Limpou_Outro").replace("&", "§").replace("{player}", target.getName()));
				return true;
			}
			return true;
		}
		return false;
	}

}

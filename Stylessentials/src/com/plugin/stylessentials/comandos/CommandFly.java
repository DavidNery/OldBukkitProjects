package com.plugin.stylessentials.comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.plugin.stylessentials.Stylessentials;

public class CommandFly implements CommandExecutor {
	
	private Stylessentials instance = Stylessentials.getStylessentials();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Fly")) {
			if(!sender.hasPermission("stylessentials.fly")) {
				sender.sendMessage(instance.getConfig().getString("Sem_Permissao").replace("&", "§").replace("{cmd}", label));
				return true;
			}
			Player player = (Player)sender;
			if(args.length == 0){
				if(player.isFlying()) {
					player.setAllowFlight(false);
					player.setFlying(false);
					sender.sendMessage(instance.getConfig().getString("Fly.Desativado").replace("&", "§"));
				}else {
					player.setAllowFlight(true);
					player.setFlying(true);
					sender.sendMessage(instance.getConfig().getString("Fly.Ativado").replace("&", "§"));
				}
			}else{
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null){
					sender.sendMessage(instance.getConfig().getString("Player_Nao_Encontrado").replace("&", "§").replace("{player}", args[0]));
					return true;
				}
				if(target.isFlying()) {
					target.setAllowFlight(false);
					target.setFlying(false);
					target.sendMessage(instance.getConfig().getString("Fly.Desativado").replace("&", "§"));
					sender.sendMessage(instance.getConfig().getString("Fly.Desativou").replace("&", "§").replace("{player}", target.getName()));
				}else {
					target.setAllowFlight(true);
					target.setFlying(true);
					target.sendMessage(instance.getConfig().getString("Fly.Ativado").replace("&", "§"));
					sender.sendMessage(instance.getConfig().getString("Fly.Ativou").replace("&", "§").replace("{player}", target.getName()));
				}
			}
			return true;
		}
		return false;
	}

}

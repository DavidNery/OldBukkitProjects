package com.seekinggames.seekingmito;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Mito implements CommandExecutor{
	
	private SeekingMito instance = SeekingMito.getSeekingMito();
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("mito")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				instance.sendTitle(p, instance.getConfig().getString("Mensagem_Mito").replace("{mito}", instance.getConfig().getString("Mito")).replace("&", "§"));
			}
		}else if(cmd.getName().equalsIgnoreCase("setmito")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				if(!p.hasPermission("seekinggames.setmito")){
					p.sendMessage(instance.getConfig().getString("Sem_Permissao").replace("&", "§"));
					return true;
				}
				if(args.length == 0){
					p.sendMessage(instance.getConfig().getString("Uso_Correto").replace("&", "§"));
					return true;
				}
				Player player = instance.getServer().getPlayer(args[0]);
				if(player == null){
					p.sendMessage(instance.getConfig().getString("Player_Offline").replace("{player}", args[0]).replace("&", "§"));
					return true;
				}
				p.sendMessage(instance.getConfig().getString("Setou_Mito").replace("&", "§").replace("{mito}", player.getName()));
				instance.setMito(player);
			}else{
				if(args.length == 0){
					sender.sendMessage(instance.getConfig().getString("Uso_Correto").replace("&", "§"));
					return true;
				}
				Player player = instance.getServer().getPlayer(args[0]);
				if(player == null){
					sender.sendMessage(instance.getConfig().getString("Player_Offline").replace("{player}", args[0]).replace("&", "§"));
					return true;
				}
				sender.sendMessage(instance.getConfig().getString("Setou_Mito").replace("&", "§").replace("{mito}", player.getName()));
				instance.setMito(player);
			}
		}
		return false;
	}

}

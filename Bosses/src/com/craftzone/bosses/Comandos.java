package com.craftzone.bosses;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.craftzone.bosses.utils.Utils;

public class Comandos implements CommandExecutor {
	
	private final Bosses instance = Bosses.getBosses();
	private final Utils utils = instance.getUtils();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player){
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("setwither")){
				if(!p.hasPermission("bosses.setWither")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}
				utils.setWitherSpawnLocation(p.getLocation());
				p.sendMessage(instance.getConfig().getString("Mensagem.WitherSpawn_Setado").replace("&", "§"));
			}else if(cmd.getName().equalsIgnoreCase("setdragon")){
				if(!p.hasPermission("bosses.setdragon")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}
				utils.setDragonSpawnLocation(p.getLocation());
				p.sendMessage(instance.getConfig().getString("Mensagem.DragonSpawn_Setado").replace("&", "§"));
			}else if(cmd.getName().equalsIgnoreCase("lastkwither")){
				if(utils.getLastKillerWither() == null || utils.getLastKillerWither().equalsIgnoreCase("nenhum")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Ninguem_Matou_Wither").replace("&", "§"));
					return true;
				}
				p.sendMessage(instance.getConfig().getString("Mensagem.UltimoWitherKiller").replace("&", "§").replace("{killer}", utils.getLastKillerWither()));
			}else if(cmd.getName().equalsIgnoreCase("lastkdragon")){
				if(utils.getLastKillerDragon() == null || utils.getLastKillerDragon().equalsIgnoreCase("nenhum")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Ninguem_Matou_Dragon").replace("&", "§"));
					return true;
				}
				p.sendMessage(instance.getConfig().getString("Mensagem.UltimoDragonKiller").replace("&", "§").replace("{killer}", utils.getLastKillerDragon()));
			}else if(cmd.getName().equalsIgnoreCase("spawnwither")){
				if(!p.hasPermission("bosses.spanwither")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}
				if(!utils.isWitherLiving()){
					utils.spawnWither();
					for(Player on : instance.getServer().getOnlinePlayers())
						on.playSound(on.getLocation(), Sound.WITHER_DEATH, 10.0F, 1.0F);
					String msg = "";
					for(String s : instance.getConfig().getStringList("Mensagem.WitherSpawnado"))
						msg += s.replace("&", "§") + "\n";
					msg = msg.substring(0, msg.length()-2);
					for(Player on : instance.getServer().getOnlinePlayers())
						on.sendMessage(msg);
				}else{
					String msg = "";
					for(String s : instance.getConfig().getStringList("Mensagem.WitherAindaVivo"))
						msg += s.replace("&", "§") + "\n";
					msg = msg.substring(0, msg.length()-2);
					p.sendMessage(msg);
				}
			}else if(cmd.getName().equalsIgnoreCase("spawndragon")){
				if(!p.hasPermission("bosses.spawndragon")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}
				if(!utils.isDragonLiving()){
					utils.spawnDragon();
					for(Player on : instance.getServer().getOnlinePlayers())
						on.playSound(on.getLocation(), Sound.ENDERDRAGON_DEATH, 10.0F, 1.0F);
					String msg = "";
					for(String s : instance.getConfig().getStringList("Mensagem.DragonSpawnado"))
						msg += s.replace("&", "§") + "\n";
					msg = msg.substring(0, msg.length()-2);
					for(Player on : instance.getServer().getOnlinePlayers())
						on.sendMessage(msg);
				}else{
					String msg = "";
					for(String s : instance.getConfig().getStringList("Mensagem.DragonAindaVivo"))
						msg += s.replace("&", "§") + "\n";
					msg = msg.substring(0, msg.length()-2);
					p.sendMessage(msg);
				}
			}
		}
		return false;
	}

}

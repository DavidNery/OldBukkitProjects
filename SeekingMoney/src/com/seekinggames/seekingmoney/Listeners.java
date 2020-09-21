package com.seekinggames.seekingmoney;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class Listeners implements Listener{
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void Command(PlayerCommandPreprocessEvent e){
		String cmd = e.getMessage();
		if(cmd.toLowerCase().startsWith("/stop") || cmd.toLowerCase().startsWith("/end")){
			e.setCancelled(true);
			if(SeekingMoney.getSeekingMoney().getBDType() == 1){
				SeekingMoney.getSeekingMoney().getMySQL().updateAllUsers();
			}else{
				SeekingMoney.getSeekingMoney().getSQLite().updateAllUsers();
			}
			SeekingMoney.getSeekingMoney().getUM().stop();
			SeekingMoney.getSeekingMoney().getServer().shutdown();
		}else if(cmd.toLowerCase().startsWith("/reload") || cmd.toLowerCase().startsWith("/rl")){
			e.setCancelled(true);
			if(SeekingMoney.getSeekingMoney().getBDType() == 1){
				SeekingMoney.getSeekingMoney().getMySQL().updateAllUsers();
			}else{
				SeekingMoney.getSeekingMoney().getSQLite().updateAllUsers();
			}
			SeekingMoney.getSeekingMoney().getUM().stop();
			SeekingMoney.getSeekingMoney().getServer().reload();
		}else if(cmd.toLowerCase().startsWith("/plugman reload seekingmoney")){
			e.setCancelled(true);
			if(SeekingMoney.getSeekingMoney().getBDType() == 1){
				SeekingMoney.getSeekingMoney().getMySQL().updateAllUsers();
			}else{
				SeekingMoney.getSeekingMoney().getSQLite().updateAllUsers();
			}
			SeekingMoney.getSeekingMoney().getUM().stop();
			e.getPlayer().chat("/plugman reload seekingmoney");
		}else if(cmd.toLowerCase().startsWith("/plugman unload seekingmoney")){
			e.setCancelled(true);
			if(SeekingMoney.getSeekingMoney().getBDType() == 1){
				SeekingMoney.getSeekingMoney().getMySQL().updateAllUsers();
			}else{
				SeekingMoney.getSeekingMoney().getSQLite().updateAllUsers();
			}
			SeekingMoney.getSeekingMoney().getUM().stop();
			e.getPlayer().chat("/plugman unload seekingmoney");
		}else if(cmd.toLowerCase().startsWith("/plugman disable seekingmoney")){
			e.setCancelled(true);
			if(SeekingMoney.getSeekingMoney().getBDType() == 1){
				SeekingMoney.getSeekingMoney().getMySQL().updateAllUsers();
			}else{
				SeekingMoney.getSeekingMoney().getSQLite().updateAllUsers();
			}
			SeekingMoney.getSeekingMoney().getUM().stop();
			e.getPlayer().chat("/plugman disable seekingmoney");
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void Comando(ServerCommandEvent e){
		String cmd = e.getCommand();
		if(cmd.toLowerCase().startsWith("/stop") || cmd.toLowerCase().startsWith("/end")){
			e.setCancelled(true);
			if(SeekingMoney.getSeekingMoney().getBDType() == 1){
				SeekingMoney.getSeekingMoney().getMySQL().updateAllUsers();
			}else{
				SeekingMoney.getSeekingMoney().getSQLite().updateAllUsers();
			}
			SeekingMoney.getSeekingMoney().getUM().stop();
			SeekingMoney.getSeekingMoney().getServer().shutdown();
		}else if(cmd.toLowerCase().startsWith("/reload") || cmd.toLowerCase().startsWith("/rl")){
			e.setCancelled(true);
			if(SeekingMoney.getSeekingMoney().getBDType() == 1){
				SeekingMoney.getSeekingMoney().getMySQL().updateAllUsers();
			}else{
				SeekingMoney.getSeekingMoney().getSQLite().updateAllUsers();
			}
			SeekingMoney.getSeekingMoney().getUM().stop();
			SeekingMoney.getSeekingMoney().getServer().reload();
		}else if(cmd.toLowerCase().startsWith("/plugman reload seekingmoney")){
			e.setCancelled(true);
			if(SeekingMoney.getSeekingMoney().getBDType() == 1){
				SeekingMoney.getSeekingMoney().getMySQL().updateAllUsers();
			}else{
				SeekingMoney.getSeekingMoney().getSQLite().updateAllUsers();
			}
			SeekingMoney.getSeekingMoney().getUM().stop();
			SeekingMoney.getSeekingMoney().getServer().dispatchCommand(SeekingMoney.getSeekingMoney().getServer().getConsoleSender(), "/plugman reload seekingmoney");
		}else if(cmd.toLowerCase().startsWith("/plugman unload seekingmoney")){
			e.setCancelled(true);
			if(SeekingMoney.getSeekingMoney().getBDType() == 1){
				SeekingMoney.getSeekingMoney().getMySQL().updateAllUsers();
			}else{
				SeekingMoney.getSeekingMoney().getSQLite().updateAllUsers();
			}
			SeekingMoney.getSeekingMoney().getUM().stop();
			SeekingMoney.getSeekingMoney().getServer().dispatchCommand(SeekingMoney.getSeekingMoney().getServer().getConsoleSender(), "/plugman unload seekingmoney");
		}else if(cmd.toLowerCase().startsWith("/plugman disable seekingmoney")){
			e.setCancelled(true);
			if(SeekingMoney.getSeekingMoney().getBDType() == 1){
				SeekingMoney.getSeekingMoney().getMySQL().updateAllUsers();
			}else{
				SeekingMoney.getSeekingMoney().getSQLite().updateAllUsers();
			}
			SeekingMoney.getSeekingMoney().getUM().stop();
			SeekingMoney.getSeekingMoney().getServer().dispatchCommand(SeekingMoney.getSeekingMoney().getServer().getConsoleSender(), "/plugman disable seekingmoney");
		}
	}

}

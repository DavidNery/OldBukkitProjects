package com.seekinggames.seekingmoney;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UpdateMoney extends Thread{
	
	private SeekingMoney plugin;
	
	public UpdateMoney(SeekingMoney plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run(){
		while(true){
			if(plugin.getBDType() == 1){
				plugin.getMySQL().updateContas();
				plugin.getTopsArray().clear();
				plugin.getTops().clear();
				plugin.getMySQL().putTops();
			}else{
				plugin.getSQLite().updateContas();
				plugin.getTopsArray().clear();
				plugin.getTops().clear();
				plugin.getSQLite().putTops();
			}
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(all.hasPermission("seekingmoney.admin")) {
					all.sendMessage("§7[Todas as contas foram atualizadas]");
				}
			}
			try {
				Thread.sleep(1000*60*5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

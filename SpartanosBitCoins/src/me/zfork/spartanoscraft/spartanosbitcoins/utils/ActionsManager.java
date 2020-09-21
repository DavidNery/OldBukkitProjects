package me.zfork.spartanoscraft.spartanosbitcoins.utils;

import me.zfork.spartanoscraft.spartanosbitcoins.SpartanosBitCoins;

import org.bukkit.entity.Player;

public class ActionsManager {
	
	private SpartanosBitCoins instance;
	private InventoryUtils inventoryutils;
	private FileLogger fileLogger;

	public ActionsManager(SpartanosBitCoins instance) {
		this.instance = instance;
	}
	
	public void loadOtherClasses() {
		this.inventoryutils = instance.getInventoryUtils();
		this.fileLogger = instance.getFileLogger();
	}
	
	public boolean checkAction(Player p, String inventoryName, int slot, String action) {
		if(action.startsWith("console executar cmd")){
			String comando = action.split("console executar cmd ")[1];
			instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), comando
					.replace("{player}", p.getName()));
			fileLogger.logToFile(p.getName() + " clicou no slot \"" + slot + "\" do inventario \"" + inventoryName + "\" e foi executado o comando \""
					+ comando + "\"!");
		}else if(action.startsWith("player executar cmd")){
			String comando = action.split("player executar cmd ")[1];
			instance.getServer().dispatchCommand(p, comando
					.replace("{player}", p.getName()));
			fileLogger.logToFile(p.getName() + " clicou no slot \"" + slot + "\" do inventario \"" + inventoryName + "\" e ele executou o comando \""
					+ comando + "\"!");
		}else if(action.startsWith("abrir inv")){
			if(p.getOpenInventory() != null) p.getOpenInventory().close();
			String inv = action.split("abrir inv ")[1];
			inventoryutils.getInventarios().get(inv.equalsIgnoreCase("maininventory") ? "mainInventory" : inv).openToPlayer(p);
		}else if(action.startsWith("confirmar->") && action.contains(" ")){
			if(p.getOpenInventory() != null) p.getOpenInventory().close();
			String[] partes = action.split("->");
			inventoryutils.getConfirmInventory().openToPlayer(instance.getConfig().getString("Config.ConfirmInv.Nome").replace("&", "§")
					.replace("{player}", p.getName()).replace("{pontos}", partes[1]), partes[1].split("&&"), p);
		}else if(action.startsWith("fechar inventario")){
			if(p.getOpenInventory() != null) p.getOpenInventory().close();
			return true;
		}else if(action.startsWith("retirar pontos")){
			int qnt = Integer.parseInt(action.split("retirar pontos ")[1]);
			if(instance.getPlayerPointsAPI().look(p.getUniqueId()) < qnt){
				p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Pontos_Suficientes").replace("&", "§"));
				return true;
			}
			instance.getPlayerPointsAPI().take(p.getUniqueId(), qnt);
			fileLogger.logToFile(p.getName() + " clicou no slot \"" + slot + "\" do inventario \"" + inventoryName + "\" e foi retirado \""
					+ qnt + "\" pontos dele!");
		}else if(action.startsWith("enviar mensagem")){
			p.sendMessage(action.split("enviar mensagem ")[1].replace("{player}", p.getName()).replace("&", "§"));
		}
		return false;
	}

}

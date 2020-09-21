package me.zfork.feventochat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Eventos implements Listener {
	
	private FEventoChat instance = FEventoChat.getFEventoChat();
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void Chat(AsyncPlayerChatEvent e){
		if(instance.task != null){
			Player p = e.getPlayer();
			String msg = e.getMessage();
			if(msg.equals(instance.frase)){
				for(Player on : instance.getServer().getOnlinePlayers()){
					on.sendMessage("");
					on.sendMessage("");
					on.sendMessage("§d§l[EVENTOCHAT] §fEvento chat finalizado!");
					on.sendMessage("§d§l[EVENTOCHAT] §fPlayer §7" + p.getName() + "§f venceu!");
					on.sendMessage("§d§l[EVENTOCHAT] §fPremio de §7R$1.000,00 §fentregue!");
					on.sendMessage("");
					on.sendMessage("");
					instance.econ.depositPlayer(p, 1000);
					instance.task.cancel();
					instance.task = null;
					instance.frase = "";
				}
			}
		}
	}

}

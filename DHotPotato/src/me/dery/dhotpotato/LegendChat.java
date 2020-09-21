package me.dery.dhotpotato;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;

public class LegendChat implements Listener{
	
	static DHotPotato instance = DHotPotato.getDHotPotato();
	
	@EventHandler
	public void Falou(ChatMessageEvent e){
		Player p = e.getSender();
		if(instance.getConfig().getString("Config.Vencedor") == p.getName() && e.getTags().contains("{hotpotato}")){
			e.setTagValue("hotpotato", instance.getConfig().getString("Config.Tag").replace("&", "§"));
		}
	}

}

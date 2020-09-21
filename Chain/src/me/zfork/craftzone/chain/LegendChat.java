package me.zfork.craftzone.chain;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;

public class LegendChat implements Listener{
	
	private Main instance = Main.getMain();
	
	@EventHandler
	public void Chat(ChatMessageEvent e){
		Player p = e.getSender();
		if(e.getTags().contains("chaintopkiller") && instance.getChainmanager().getTopkiller().getName().equals(p.getName()))
			e.setTagValue("chaintopkiller", instance.getConfig().getString("Config.TAG_Top_Killer").replace("&", "§"));
	}

}

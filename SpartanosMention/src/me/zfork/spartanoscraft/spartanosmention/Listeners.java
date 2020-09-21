package me.zfork.spartanoscraft.spartanosmention;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;

public class Listeners implements Listener {
	
	private SpartanosMention instance;
	
	public Listeners(SpartanosMention instance) {
		this.instance = instance;
		instance.getServer().getPluginManager().registerEvents(this, instance);
	}
	
	@EventHandler(ignoreCancelled=true)
	public void Chat(ChatMessageEvent e){
		if(instance.getConfig().getBoolean("Canais." + e.getChannel().getName() + ".Ativar")){
			String msg = e.getMessage().toLowerCase();
			for(Player on : e.getRecipients()){
				if(msg.contains(on.getName().toLowerCase())){
					msg = "";
					String[] partes = e.getMessage().split("(?i)(&\\w{1})*" + on.getName());
					boolean match = e.getMessage().matches("(?i).*" + on.getName() + "$");
					for(int i = 0; i<partes.length; i++){
						msg += partes[i];
						if(i != partes.length-1 || match) 
							msg += instance.getConfig().getString("Canais." + e.getChannel().getName() + ".Cor_Marcacao").replace("&", "§") + on.getName();
						int lastIndexOf = partes[i].lastIndexOf("&");
						if(lastIndexOf != -1)
							msg += String.valueOf(partes[i].charAt(lastIndexOf)) + partes[i].charAt(lastIndexOf+1);
						else
							msg += e.getChannel().getColor();
					}
					e.setMessage(msg);
					on.sendMessage(instance.getConfig().getString("Canais." + e.getChannel().getName() + ".Mensagem_Marcacao").replace("&", "§")
							.replace("{player}", e.getSender().getName()));
					if(instance.getConfig().getString("Canais." + e.getChannel().getName() + ".Sound") != null) 
						on.playSound(on.getLocation(), Sound.valueOf(instance.getConfig().getString("Canais." + e.getChannel().getName() + ".Sound")), 5F, 1F);
					return;
				}
			}
		}
	}

}

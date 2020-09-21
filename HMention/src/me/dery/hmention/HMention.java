package me.dery.hmention;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;

public class HMention extends JavaPlugin implements Listener{
	
	ArrayList<String> player = new ArrayList<String>();
	
	@Override
	public void onEnable() {
		getServer().getConsoleSender().sendMessage("§3Plugin Habilitado!");
		saveDefaultConfig();
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		getServer().getConsoleSender().sendMessage("§4Plugin Desabilitado!");
	}
	
	/*@EventHandler
	public void Chat(ChatMessageEvent e){
		Player p = e.getSender();
		boolean ok = false;
		String msgnova = "";
		String[] partes = e.getMessage().split(" ");
		for(int i = 0; i<partes.length; i++){
			if(Bukkit.getServer().getPlayer(partes[i].replaceAll("\\W\\D", "")) != null){
				String palavra = partes[i];
				if(partes[i].replaceAll("\\W\\D", "").equalsIgnoreCase("")){
					if(msgnova.equalsIgnoreCase("")){
						msgnova += partes[i];
					}else{
						msgnova += " " + partes[i];
					}
				}else{
					String format = getConfig().getString("Cor").replace("&", "§") + palavra + e.getChannel().getColor();
					if(msgnova.equalsIgnoreCase("")){
						msgnova += format;
					}else{
						msgnova += " " + format;
					}
					ok = true;
					player.add(Bukkit.getServer().getPlayer(palavra.replaceAll("\\W\\D", "")).getName());
				}
			}else{
				if(msgnova.equalsIgnoreCase("")){
					msgnova += partes[i];
				}else{
					msgnova += " " + partes[i];
				}
			}
		}
		e.setMessage(msgnova);
		if(ok == true){
			String marcados = "";
			for(String players : player){
				Bukkit.getServer().getPlayer(players).sendMessage(getConfig().getString("Marcado").replace("&", "§").replace("@player", p.getName()));
				marcados += players.replace("\\W\\D", "") + ", ";
			}
			p.sendMessage(getConfig().getString("Marcou").replace("&", "§").replace("@player", marcados.substring(0, marcados.length() - 2)));
		}
		player.clear();
	}*/
	
	@EventHandler(ignoreCancelled=true)
	public void Chat(ChatMessageEvent e){
		Player p = e.getSender();
		e.setMessage(e.getMessage().substring(0, 1).toUpperCase() + e.getMessage().substring(1).toLowerCase());
		for(Player online : Bukkit.getServer().getOnlinePlayers()){
			if(e.getMessage().toLowerCase().contains("@" + online.getName().toLowerCase())){
				String format = getConfig().getString("Cor").replace("&", "§") + online.getName() + e.getChannel().getColor();
				e.setMessage(e.getMessage().replace("@" + online.getName().toLowerCase(), format));
				p.sendMessage(getConfig().getString("Marcou").replace("&", "§").replace("@player", online.getName()));
				online.sendMessage(getConfig().getString("Marcado").replace("&", "§").replace("@player", p.getName()));
			}
		}
	}
}

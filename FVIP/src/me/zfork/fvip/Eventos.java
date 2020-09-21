package me.zfork.fvip;

import java.util.concurrent.TimeUnit;

import me.zfork.fvip.vendakey.VendaKey;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Eventos implements Listener{
	
	private FVip instance = FVip.getFVIP();
	
	@EventHandler
	public void Join(PlayerJoinEvent e){
		Player p = e.getPlayer();
		for(String player : instance.getPerderam()){
			if(player.equalsIgnoreCase(p.getName())){
				for(String msg : instance.getConfig().getStringList("Mensagem.Sucesso.Acabou_VIP")){
					p.sendMessage(msg.replace("&", "§").replace("{player}", p.getName()));
				}
				instance.getPerderam().remove(p.getName().toLowerCase());
				return;
			}
		}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		for(String vk : instance.getTasks().keySet()){
			VendaKey vendakey = instance.getTasks().get(vk);
			if(vendakey.getVendedor().equals(p.getName())){
				instance.getTasks().get(p.getName()).cancel();
				instance.getTasks().remove(p.getName());
				instance.getVendakey().put(p.getName(), System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.VendaKey.Delay")));
				new BukkitRunnable() {
					@Override
					public void run() {
						instance.getVendakey().remove(p.getName());
					}
				}.runTaskLater(instance, instance.getConfig().getInt("Config.VendaKey.Delay")*20);
				return;
			}
		}
	}

}

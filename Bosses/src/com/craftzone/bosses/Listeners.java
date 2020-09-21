package com.craftzone.bosses;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;

import com.craftzone.bosses.utils.Utils;

public class Listeners implements Listener {
	
	private final Bosses instance = Bosses.getBosses();
	private final Utils utils = instance.getUtils();
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void Morreu(EntityDeathEvent e){
		LivingEntity entity = (LivingEntity) e.getEntity();
		if(entity.isCustomNameVisible() && e.getEntity().getKiller() != null){
			if(entity instanceof EnderDragon){
				if(entity.getCustomName().equalsIgnoreCase(instance.getConfig().getString("Config.DragonName").replace("&", "§"))){
					utils.setLastKillerDragon(((EnderDragon) entity).getKiller().getName());
					instance.econ.depositPlayer(((EnderDragon) entity).getKiller().getName(), instance.getConfig().getDouble("Config.PremioDragon"));
					if(instance.getConfig().getBoolean("Config.AvisarMatouDragon")){
						String msg = "";
						for(String s : instance.getConfig().getStringList("Mensagem.MatouDragon"))
							msg += s.replace("{player}", ((EnderDragon) entity).getKiller().getName()).replace("&", "§") + "\n";
						msg = msg.substring(0, msg.length() - 2);
						for(Player on : instance.getServer().getOnlinePlayers())
							on.sendMessage(msg);
					}
				}
			}else if(entity instanceof Wither){
				if(entity.getCustomName().equalsIgnoreCase(instance.getConfig().getString("Config.WitherName").replace("&", "§"))){
					utils.setLastKillerWither(((Wither) entity).getKiller().getName());
					instance.econ.depositPlayer(((Wither) entity).getKiller().getName(), instance.getConfig().getDouble("Config.PremioWither"));
					if(instance.getConfig().getBoolean("Config.AvisarMatouWither")){
						String msg = "";
						for(String s : instance.getConfig().getStringList("Mensagem.MatouWither"))
							msg += s.replace("{player}", ((Wither) entity).getKiller().getName()).replace("&", "§") + "\n";
						msg = msg.substring(0, msg.length() - 2);
						for(Player on : instance.getServer().getOnlinePlayers())
							on.sendMessage(msg);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void Chat(ChatMessageEvent e){
		Player p = e.getSender();
		if(e.getTags().contains("bosses")){
			if(utils.getLastKillerDragon().equalsIgnoreCase(p.getName())
					&& utils.getLastKillerWither().equalsIgnoreCase(p.getName())){
				e.setTagValue("bosses", instance.getConfig().getString("Config.TAGAll").replace("&", "§"));
			}else if(utils.getLastKillerDragon().equalsIgnoreCase(p.getName())){
				e.setTagValue("bosses", instance.getConfig().getString("Config.TAGDragon").replace("&", "§"));
			}else if(utils.getLastKillerWither().equalsIgnoreCase(p.getName())){
				e.setTagValue("bosses", instance.getConfig().getString("Config.TAGWither").replace("&", "§"));
			}
		}
	}

}

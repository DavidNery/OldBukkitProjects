package me.zfork.hjungle;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener{
	
	private HJungle instance = HJungle.getHJungle();
	private JungleManager jm = instance.getJungleManager();
	
	@EventHandler
	public void Death(PlayerDeathEvent e){
		Player p = e.getEntity();
		if(jm.getEstapa() != 0){
			if(jm.containsPlayer(p)){
				jm.delPlayerNormalmente(p);
			}
		}
	}
	
	@EventHandler
	public void Death(PlayerKickEvent e){
		Player p = e.getPlayer();
		if(jm.getEstapa() != 0){
			if(jm.containsPlayer(p)){
				jm.delPlayerNormalmente(p);
			}
		}
	}
	
	@EventHandler
	public void Build(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(jm.getEstapa() != 0){
			if(jm.containsPlayer(p)){
				jm.delPlayerNormalmente(p);
			}
		}
	}
	
	@EventHandler
	public void Build(BlockBreakEvent e){
		Player p = e.getPlayer();
		if(jm.getEstapa() != 0){
			if(jm.containsPlayer(p)) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Build(BlockPlaceEvent e){
		Player p = e.getPlayer();
		if(jm.getEstapa() != 0){
			if(jm.containsPlayer(p)) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Build(PlayerBucketEmptyEvent e){
		Player p = e.getPlayer();
		if(jm.getEstapa() != 0){
			if(jm.containsPlayer(p)) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Build(PlayerBucketFillEvent e){
		Player p = e.getPlayer();
		if(jm.getEstapa() != 0){
			if(jm.containsPlayer(p)) e.setCancelled(true);
		}
	}
	
	/*@EventHandler
	public void TP(PlayerTeleportEvent e){
		Player p = e.getPlayer();
		if(jm.getEstapa() != 0){
			Location exit = jm.getLocationFromString(instance.getConfig().getString("Config.Saida"));
			Location spawn = jm.getLocationFromString(instance.getConfig().getString("Config.Spawn"));
			Location lobby = jm.getLocationFromString(instance.getConfig().getString("Config.Lobby"));
			if(jm.containsPlayer(p) && e.getTo().getX() != exit.getX() && e.getTo().getY() != exit.getY() && e.getTo().getZ() != exit.getZ()
					&& e.getTo().getX() != spawn.getX() && e.getTo().getY() != spawn.getY() && e.getTo().getZ() != spawn.getZ()
					&& e.getTo().getX() != lobby.getX() && e.getTo().getY() != lobby.getY() && e.getTo().getZ() != lobby.getZ()
					&& e.getTo().getX() != e.getFrom().getX() && e.getTo().getZ() != e.getFrom().getZ()) e.setCancelled(true);
		}
	}*/

}

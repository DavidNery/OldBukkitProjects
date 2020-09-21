package me.zfork.craftzone.warps;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class Eventos implements Listener{
	
	private static final Main instance = Main.getWarps();
	private static final FileConfiguration warpsfile = instance.getWarpsFile();
	
	@EventHandler
	public void Move(PlayerMoveEvent e){
		Player p = e.getPlayer();
		if((int)e.getFrom().getX() != (int)e.getTo().getX() || (int)e.getFrom().getY() != (int)e.getTo().getY() || (int)e.getFrom().getZ() != (int)e.getTo().getZ()){
			if(instance.getPlayersinDelay().containsKey(p.getName().toLowerCase())){
				TitleBuilder.sendTitle(20, 40, 20, 
						instance.getConfig().getString("Mensagem.Erro.Se_Mecheu_Title").replace("&", "§"),
						instance.getConfig().getString("Mensagem.Erro.Se_Mecheu_SubTitle").replace("&", "§"), p);
				instance.getPlayersinDelay().get(p.getName().toLowerCase()).cancel();
				instance.getPlayersinDelay().remove(p.getName().toLowerCase());
				if(instance.getPlayersindelayActionBar().containsKey(p.getName().toLowerCase())){
					instance.getPlayersindelayActionBar().get(p.getName()).cancel();
					instance.getPlayersindelayActionBar().remove(p.getName().toLowerCase());
				}
			}
		}
	}
	
	@EventHandler
	public void Click(InventoryClickEvent e){
		if(e.getCurrentItem() != null){
			if(e.getInventory().getTitle().equalsIgnoreCase(instance.getConfig().getString("GUI.Nome").replace("&", "§"))){
				e.setCancelled(true);
				final Player p = (Player) e.getWhoClicked();
				p.getOpenInventory().close();
				for(String warp : warpsfile.getConfigurationSection("Warps").getKeys(false)){
					int local = warpsfile.getInt("Warps." + warp + ".Local");
					if(e.getSlot() == local){
						instance.tpPlayer(p, warp);
						return;
					}
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	public void Command(PlayerCommandPreprocessEvent e){
		if(e.isCancelled()) return;
		Player p = e.getPlayer();
		if(e.getMessage().length() > 1 && e.getMessage().startsWith("/") && warpsfile.contains("Warps") && warpsfile.getConfigurationSection("Warps").getKeys(false).size() > 0){
			for(String warp : warpsfile.getConfigurationSection("Warps").getKeys(false)){
				if(e.getMessage().equalsIgnoreCase("/" + warp.toLowerCase()) && warpsfile.getBoolean("Warps." + warp + ".Simplificar")){
					instance.tpPlayer(p, warp);
					e.setCancelled(true);
					return;
				}
			}
		}
	}

}

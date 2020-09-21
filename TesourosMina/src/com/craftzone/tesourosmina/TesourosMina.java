package com.craftzone.tesourosmina;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class TesourosMina extends JavaPlugin implements Listener{

	private static String PLUGIN_NAME;
	private static WorldGuardPlugin wg;

	public void onEnable(){
		PLUGIN_NAME = getDescription().getName();
		if(!new File(getDataFolder(), "config.yml").exists())
			saveDefaultConfig();
		wg = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
		getServer().getPluginManager().registerEvents(this, this);
	}

	public void onDisable(){
		HandlerList.unregisterAll(getServer().getPluginManager().getPlugin(PLUGIN_NAME));
	}
	
	@EventHandler(ignoreCancelled=true)
	public void Interact(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				&& e.getClickedBlock().getType().equals(Material.CHEST)){
			Block b = e.getClickedBlock();
			if(b.hasMetadata("donotesouro")){
				if(!b.getMetadata("donotesouro").get(0).asString().equals(p.getName())){
					e.setCancelled(true);
					p.sendMessage(getConfig().getString("Nao_Pode_Abrir_Outros_Baus").replace("&", "§"));
				}
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void Break(BlockBreakEvent e) {
		Player p = e.getPlayer();
		ApplicableRegionSet set = wg.getRegionContainer().get(p.getWorld()).getApplicableRegions(e.getBlock().getLocation());
		if(set.size() == 0) return;
		ProtectedRegion region = set.iterator().next();
		for(String regions : getConfig().getConfigurationSection("Regioes").getKeys(false)){
			if(region.getId().equalsIgnoreCase(regions)){
				if(Math.random()*100 <= getConfig().getDouble("Regioes." + regions + ".Chance")){
					if(!getConfig().getIntegerList("Regioes." + regions + ".Blocos_Possiveis").contains(e.getBlock().getTypeId())) return;
					e.setCancelled(true);
					e.getBlock().setType(Material.CHEST);
					p.playSound(p.getLocation(), Sound.LEVEL_UP, 5F, 1F);
					Chest chest = (Chest) e.getBlock().getState();
					boolean raro = false;
					String raros = "";
					for(String items : getConfig().getStringList("Regioes." + regions + ".Items"))
						if(chest.getInventory().firstEmpty() == -1){
							break;
						}else{
							ItemStack item = Utils.criarItem(items);
							if(items.endsWith("raro:true")){
								raro = true;
								raros += item.getItemMeta().getDisplayName() + "§7, ";
							}
							if(item != null) chest.getInventory().setItem(chest.getInventory().firstEmpty(), item);
						}
					if(raro) raros = raros.substring(0, raros.length()-2);
					chest.setMetadata("donotesouro", new FixedMetadataValue(this, p.getName()));
					Utils.shootFirework(e.getBlock().getLocation());
					for(String msg : getConfig().getStringList("Regioes." + regions + ".Mensagem_Player"))
						p.sendMessage(msg.replace("&", "§"));
					if(getConfig().getBoolean("Regioes." + regions + ".Ativar_BC"))
						if(raro)
							for(Player on : getServer().getOnlinePlayers())
								on.sendMessage(getConfig().getString("Regioes." + regions + ".Broadcast_Raro").replace("&", "§").replace("{player}", p.getName())
										.replace("{raros}", raros));
						else
							for(Player on : getServer().getOnlinePlayers())
								on.sendMessage(getConfig().getString("Regioes." + regions + ".Broadcast").replace("&", "§").replace("{player}", p.getName()));
					new BukkitRunnable() {
						@Override
						public void run() {
							if(e.getBlock().getType().equals(Material.CHEST)){
								chest.getInventory().clear();
								e.getBlock().setType(Material.AIR);
							}
						}
					}.runTaskLater(this, getConfig().getInt("Regioes." + regions + ".Remover")*20);
				}
				return;
			}
		}
	}

}

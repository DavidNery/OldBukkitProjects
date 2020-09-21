package me.dery.bigorna;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class Bigorna extends JavaPlugin implements Listener{
	
	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@EventHandler(ignoreCancelled=true)
	public void Regenerar(PlayerInteractEvent e){
		if(!(e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
		if(e.getClickedBlock().getType() == Material.ANVIL){
			e.setCancelled(true);
			AnvilInventory anvil = (AnvilInventory) Bukkit.getServer().createInventory(null, InventoryType.ANVIL);
			e.getPlayer().openInventory(anvil);
		}
	}

}

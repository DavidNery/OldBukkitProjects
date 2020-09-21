package me.dery.hnoenchant;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class HNoEnchant extends JavaPlugin implements Listener{
	
	@Override
	public void onEnable() {
		getServer().getConsoleSender().sendMessage("§3Ativado!");
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		getServer().getConsoleSender().sendMessage("§4Desativado!");
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void Damage(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			for(ItemStack item : p.getInventory().getArmorContents()){
				if(item != null && item.getType() != Material.AIR){
					boolean ok = false;
					for(Enchantment enchants : item.getEnchantments().keySet()){
						if(item.getEnchantmentLevel(enchants) > getConfig().getInt("Maximo_Armadura")){
							item.removeEnchantment(enchants);
							ok = true;
						}
					}
					if(ok == true){
						p.updateInventory();
						p.sendMessage(getConfig().getString("Item_Removido").replace("&", "§"));
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void Remove(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) return;
		if(p.getItemInHand().getEnchantments().size() != 0){
			boolean ok = false;
			for(Enchantment enchants : p.getItemInHand().getEnchantments().keySet()){
				if(p.getItemInHand().getEnchantmentLevel(enchants) > getConfig().getInt("Maximo")){
					p.getItemInHand().removeEnchantment(enchants);
					ok = true;
				}
			}
			if(ok == true){
				p.updateInventory();
				p.sendMessage(getConfig().getString("Item_Removido").replace("&", "§"));
			}
		}
	}

}

package me.zfork.spartanoscraft.spartanosbitcoins.utils;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import me.zfork.spartanoscraft.spartanosbitcoins.SpartanosBitCoins;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BitInventory {

	private SpartanosBitCoins instance;
	private Inventory inv;
	private LinkedHashMap<Integer, String> itemWithVariables;
	
	public BitInventory(SpartanosBitCoins instance, Inventory inv) {
		this.instance = instance;
		this.inv = inv;
		this.itemWithVariables = new LinkedHashMap<>();
	}

	public void setItemWithVariables(LinkedHashMap<Integer, String> itemWithVariables) {
		this.itemWithVariables = itemWithVariables;
	}
	
	public void openToPlayer(Player p) {
		openToPlayer(null, p);
	}
	
	public void openToPlayer(String title, Player p) {
		if(itemWithVariables.size() > 0){
			Inventory inventory = instance.getServer().createInventory(null, 
					inv.getSize(), title == null ? inv.getTitle() : title);
			for(int i = 0; i<inv.getSize(); i++){
				ItemStack item = inv.getItem(i);
				if(item == null) continue;
				inventory.setItem(i, item);
			}
			for(Entry<Integer, String> items : itemWithVariables.entrySet())
				inventory.setItem(items.getKey(), (ItemStack) instance.getInventoryUtils().criarItem(items.getValue()
						.replace("{player}", p.getName()).replace("{pontos}", instance.getPlayerPointsAPI().look(p.getUniqueId())+"")));
			p.openInventory(inventory);
		}else{
			p.openInventory(inv);
		}
	}

}

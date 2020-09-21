package me.zfork.spartanoscraft.spartanosbitcoins.utils;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import me.zfork.spartanoscraft.spartanosbitcoins.SpartanosBitCoins;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ConfirmInventory {

	private SpartanosBitCoins instance;
	private Inventory inv;
	private LinkedHashMap<Integer, LinkedHashSet<String>> slotsAction;
	private LinkedHashMap<Integer, String> itemWithVariables;

	public ConfirmInventory(SpartanosBitCoins instance, Inventory inv, 
			LinkedHashMap<Integer, LinkedHashSet<String>> slotsAction, LinkedHashMap<Integer, String> itemWithVariables) {
		this.instance = instance;
		this.inv = inv;
		this.slotsAction = slotsAction;
		this.itemWithVariables = itemWithVariables;
	}

	public void openToPlayer(String title, String[] actions, Player p) {
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

		Listener listener = new Listener() {

			@EventHandler
			public void InventoryClick(InventoryClickEvent e){
				Inventory inventario = e.getInventory();
				if(inventario.getTitle().equalsIgnoreCase(inventory.getTitle())){
					e.setCancelled(true);
					for(Entry<Integer, LinkedHashSet<String>> slot : slotsAction.entrySet()){
						if(e.getSlot() == slot.getKey()){
							for(String action : slot.getValue()){
								instance.getActionsManager().checkAction((Player) e.getWhoClicked(), "confirmInventory", e.getSlot(), action);
								if(action.startsWith("fechar inventario") || action.startsWith("abrir inv")) return;
							}
							for(String action : actions){
								boolean checkaction = instance.getActionsManager().checkAction((Player) e.getWhoClicked(), "confirmInventory", e.getSlot(), 
										action);
								if(checkaction) return;
							}
							return;
						}
					}
				}
			}

			@EventHandler
			public void InventoryClose(InventoryCloseEvent e){
				if(e.getInventory().getTitle().equalsIgnoreCase(inventory.getTitle()))
					HandlerList.unregisterAll(this);
			}

		};
		instance.getServer().getPluginManager().registerEvents(listener, instance);
	}

}

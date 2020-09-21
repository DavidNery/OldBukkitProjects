package me.zfork.spartanoscraft.spartanosreport.listeners;

import java.util.Map.Entry;

import me.zfork.spartanoscraft.spartanosreport.SpartanosReport;
import me.zfork.spartanoscraft.spartanosreport.utils.PlayerReportsManager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Listeners implements Listener {
	
	private SpartanosReport instance;
	private PlayerReportsManager playerReportsManager;
	
	public Listeners(SpartanosReport instance) {
		this.instance = instance;
		instance.getServer().getPluginManager().registerEvents(this, instance);
		this.playerReportsManager = instance.getPlayerReportsManager();
	}
	
	@EventHandler
	public void Click(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		Player p = (Player) e.getWhoClicked();
		ItemStack item = e.getCurrentItem();
		if(item == null || item.getType().equals(Material.AIR)) return;
		if(inv.getTitle().matches("§c§lReports - §f§l\\d+")){
			e.setCancelled(true);
			if(e.getSlot() == 53 && item.isSimilar(playerReportsManager.getProximo())){
				p.closeInventory();
				p.openInventory(playerReportsManager.getReportsInventories().get(Integer.parseInt(inv.getTitle().split("§f§l")[1])));
			}else if(e.getSlot() == 45 && item.isSimilar(playerReportsManager.getAnterior())){
				p.closeInventory();
				p.openInventory(playerReportsManager.getReportsInventories().get(Integer.parseInt(inv.getTitle().split("§f§l")[1])-2));
			}else{
				p.getOpenInventory().close();
				playerReportsManager.getStaffLook().put(p.getName(), ((SkullMeta) item.getItemMeta()).getOwner());
				if(e.getClick().equals(ClickType.LEFT))
					p.openInventory(playerReportsManager.getOptionsInv());
				else
					p.openInventory(playerReportsManager.getPlayerReportsInventories().get(((SkullMeta) item.getItemMeta()).getOwner()).get(0));
			}
		}else if(inv.getTitle().matches("§c§lFechados - §f§l\\d+")){
			e.setCancelled(true);
			if(e.getSlot() == 53 && item.isSimilar(playerReportsManager.getProximo())){
				p.closeInventory();
				p.openInventory(playerReportsManager.getClosedReportsInventories().get(Integer.parseInt(inv.getTitle().split("§f§l")[1])));
			}else if(e.getSlot() == 45 && item.isSimilar(playerReportsManager.getAnterior())){
				p.closeInventory();
				p.openInventory(playerReportsManager.getClosedReportsInventories().get(Integer.parseInt(inv.getTitle().split("§f§l")[1])-2));
			}else{
				p.getOpenInventory().close();
				playerReportsManager.getStaffLook().put(p.getName(), ((SkullMeta) item.getItemMeta()).getOwner()+"->");
				p.openInventory(playerReportsManager.getPlayerClosedReportsInventories().get(((SkullMeta) item.getItemMeta()).getOwner()).get(0));
			}
		}else if(inv.getTitle().matches("§c§lPlayer - §f§l\\d+")){
			e.setCancelled(true);
			String player = "", fechado = null, stafflook = playerReportsManager.getStaffLook().get(p.getName());
			if(stafflook.contains("->")){
				player = stafflook.split("->")[0];
				fechado = ".";
			}else{
				player = stafflook;
			}
			if(e.getSlot() == 53 && item.isSimilar(playerReportsManager.getProximo())){
				p.closeInventory();
				if(fechado == null)
					p.openInventory(playerReportsManager.getPlayerReportsInventories().get(player).get(Integer.parseInt(inv.getTitle().split("§f§l")[1])));
				else
					p.openInventory(playerReportsManager.getPlayerClosedReportsInventories().get(player).get(Integer.parseInt(inv.getTitle().split("§f§l")[1])));
			}else if(e.getSlot() == 45 && item.isSimilar(playerReportsManager.getAnterior())){
				p.closeInventory();
				if(fechado == null)
					p.openInventory(playerReportsManager.getPlayerReportsInventories().get(player).get(Integer.parseInt(inv.getTitle().split("§f§l")[1])-2));
				else
					p.openInventory(playerReportsManager.getPlayerClosedReportsInventories().get(player).get(Integer.parseInt(inv.getTitle().split("§f§l")[1])-2));
			}else if(e.getSlot() == 49 && item.isSimilar(playerReportsManager.getVoltar())){
				p.closeInventory();
				if(fechado == null)
					p.openInventory(playerReportsManager.getReportsInventories().get(0));
				else
					p.openInventory(playerReportsManager.getClosedReportsInventories().get(0));
			}else{
				if(fechado == null){
					p.closeInventory();
					p.openInventory(playerReportsManager.getOptionsInv());
					int i = 0;
					for(Entry<String, Integer> s : playerReportsManager.getPlayerReports(player).getReports().entrySet()){
						if(e.getSlot() == i){
							playerReportsManager.getStaffLook().put(p.getName(), player+"->"+s.getKey());
							break;
						}
						i++;
					}
				}
			}
		}else if(inv.getTitle().equalsIgnoreCase(instance.getConfig().getString("Config.Invs.Options_Inv_Name").replace("&", "§"))){
			e.setCancelled(true);
			String player, report = null, stafflook = playerReportsManager.getStaffLook().get(p.getName());
			if(stafflook.contains("->")){
				player = stafflook.split("->")[0];
				report = stafflook.split("->")[1];
			}else{
				player = stafflook;
			}
			if(e.getSlot() == 1){ // Confirm
				if(report == null)
					playerReportsManager.closePlayer(player);
				else
					playerReportsManager.closePlayerReport(player, report);
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Report_Fechado").replace("&", "§"));
			}else if(e.getSlot() == 3){ // Voltar
				p.closeInventory();
				p.openInventory(playerReportsManager.getPlayerReportsInventories().get(player).get(0));
				return;
			}/*else if(e.getSlot() == 5){ // Cancel
				playerReportsManager.getStaffLook().remove(p.getName());
				p.closeInventory();
			}*/else if(e.getSlot() == 7){
				if(report == null)
					playerReportsManager.removePlayer(player);
				else
					playerReportsManager.removePlayerReport(player, report);
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Report_Deletado").replace("&", "§"));
			}
			playerReportsManager.getStaffLook().remove(p.getName());
			p.closeInventory();
		}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent e){
		if(e.getPlayer().getOpenInventory() != null)
		if(e.getPlayer().getOpenInventory().getTitle().matches("§c§lPlayer - §f§l\\d+"))
			playerReportsManager.getStaffLook().remove(e.getPlayer().getName());
	}

}

package me.zfork.spartanoscraft.spartanoskits;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.earth2me.essentials.Kit;
import com.earth2me.essentials.User;

public class Eventos implements Listener {
	
	private SpartanosKits instance = SpartanosKits.getSpartanosKits();
	private KitsManager kitsmanager = instance.getKitsManager();
	private LinkedHashSet<String> players = new LinkedHashSet<String>();
	private LinkedHashMap<String, LinkedHashMap<String, Integer>> playerkitview = new LinkedHashMap<>();
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void Kit(PlayerCommandPreprocessEvent e){
		Player p = e.getPlayer();
		if(e.getMessage().toLowerCase().startsWith("/kit") || e.getMessage().toLowerCase().startsWith("/kits")){
			if(players.contains(p.getName().toLowerCase())){
				players.remove(p.getName().toLowerCase());
			}else{
				e.setCancelled(true);
				Inventory inv = instance.getServer().createInventory(null, kitsmanager.getMainInv().getSize(),
						kitsmanager.getMainInv().getTitle());
				for(ItemStack i : kitsmanager.getMainInv().getContents())
					inv.setItem(inv.firstEmpty(), i);
				for(Map.Entry<Integer, String> slot : kitsmanager.getMainInvSlots().entrySet()){
					if(slot.getValue().startsWith("dar kit ")){
						try {
							long total = new Kit(slot.getValue().split(" ")[2].toLowerCase(), instance.getEssentials()).getNextUse(new User(p, instance.getEssentials()));
							if(total > System.currentTimeMillis()){
								inv.setItem(slot.getKey(), (ItemStack) instance.criarItem(instance.getConfig().getString("MainInv.Slots." + (slot.getKey()+1) + ".Kit_Usado").replace("{tempo}", instance.getTime(total-System.currentTimeMillis()).replace(" ", "_"))));
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				p.openInventory(inv);
			}
		}else if(e.getMessage().toLowerCase().startsWith("/skreload")){
			e.setCancelled(true);
			if(!e.getPlayer().isOp()){
				e.getPlayer().sendMessage("§cVocê não tem permissão para utilizar este comando!");
				return;
			}
			instance.saveConfig();
			instance.reloadConfig();
			new BukkitRunnable() {
				@Override
				public void run() {
					instance.getKitsManager().reload();
					e.getPlayer().sendMessage("§aPlugin recarregado!");
				}
			}.runTaskAsynchronously(instance);
		}
	}
	
	@EventHandler
	public void Close(InventoryCloseEvent e){
		Player p = (Player) e.getPlayer();
		if(players.contains(p.getName().toLowerCase()))
			players.remove(p.getName().toLowerCase());
		if(playerkitview.containsKey(p.getName().toLowerCase()))
			playerkitview.remove(p.getName().toLowerCase());
	}
	
	@EventHandler
	public void Click(InventoryClickEvent e){
		if(e.getCurrentItem() != null){
			Player p = (Player) e.getWhoClicked();
			if(e.getInventory().getTitle().equalsIgnoreCase(instance.getConfig().getString("MainInv.Nome").replace("&", "§"))){
				e.setCancelled(true);
				if(kitsmanager.getMainInvSlots().containsKey(e.getSlot())){
					String acao = kitsmanager.getMainInvSlots().get(e.getSlot()).toLowerCase();
					if(acao.startsWith("dar kit ")){
						if(e.getClick() == ClickType.LEFT){
							players.add(p.getName().toLowerCase());
							instance.getServer().dispatchCommand(p, "kit " + acao.split(" ")[2]);
							p.getOpenInventory().close();
						}else{
							Inventory inv = kitsmanager.getPreviewInventory(acao.split(" ")[2], 1);
							if(inv != null){
								p.getOpenInventory().close();
								LinkedHashMap<String, Integer> lhm = new LinkedHashMap<>();
								lhm.put(acao.split(" ")[2], 1);
								playerkitview.put(p.getName(), lhm);
								p.openInventory(inv);
							}
						}
					}else if(acao.startsWith("abrir inv ")){
						p.getOpenInventory().close();
						if(acao.split(" ")[2].equalsIgnoreCase("main")){
							p.openInventory(kitsmanager.getMainInv());
							return;
						}
						Inventory inventory;
						try{
							inventory = kitsmanager.getOtherInvs().get(acao.split(" ")[2]);
						}catch(NullPointerException e1){
							p.getOpenInventory().close();
							return;
						}
						Inventory inv = instance.getServer().createInventory(null,inventory.getSize(), inventory.getTitle());
						for(ItemStack i : inventory.getContents())
							inv.setItem(inv.firstEmpty(), i);
						for(Map.Entry<Integer, String> slot : kitsmanager.getOtherInvsSlots().get(acao.split(" ")[2]).entrySet()){
							if(slot.getValue().startsWith("dar kit ")){
								try {
									long total = new Kit(slot.getValue().split(" ")[2].toLowerCase(), instance.getEssentials()).getNextUse(new User(p, instance.getEssentials()));
									if(total > System.currentTimeMillis()){
										inv.setItem(slot.getKey(), (ItemStack) instance.criarItem(instance.getConfig().getString("OtherInvs." + acao.split(" ")[2] + ".Slots." + (slot.getKey()+1) + ".Kit_Usado").replace("{tempo}", instance.getTime(total-System.currentTimeMillis()).replace(" ", "_"))));
									}
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}
						}
						p.openInventory(inv);
					}
				}
				return;
			}
			for(String inventarios : instance.getConfig().getConfigurationSection("OtherInvs").getKeys(false)){
				if(instance.getConfig().getString("OtherInvs." + inventarios + ".Nome").replace("&", "§")
						.equalsIgnoreCase(e.getInventory().getTitle())){
					e.setCancelled(true);
					LinkedHashMap<Integer, String> otherinvsslots = kitsmanager.getOtherInvsSlots().get(inventarios);
					if(otherinvsslots.containsKey(e.getSlot())){
						String acao = otherinvsslots.get(e.getSlot()).toLowerCase();
						if(acao.startsWith("dar kit ")){
							if(e.getClick() == ClickType.LEFT){
								players.add(p.getName().toLowerCase());
								instance.getServer().dispatchCommand(p, "kit " + acao.split(" ")[2]);
								p.getOpenInventory().close();
							}else{
								Inventory inv = kitsmanager.getPreviewInventory(acao.split(" ")[2], 1);
								if(inv != null){
									p.getOpenInventory().close();
									LinkedHashMap<String, Integer> lhm = new LinkedHashMap<>();
									lhm.put(acao.split(" ")[2], 1);
									playerkitview.put(p.getName(), lhm);
									p.openInventory(inv);
								}
							}
						}else if(acao.startsWith("abrir inv ")){
							p.getOpenInventory().close();
							if(acao.split(" ")[2].equalsIgnoreCase("main")){
								p.openInventory(kitsmanager.getMainInv());
								return;
							}
							Inventory inventory;
							try{
								inventory = kitsmanager.getOtherInvs().get(acao.split(" ")[2]);
							}catch(NullPointerException e1){
								p.getOpenInventory().close();
								return;
							}
							Inventory inv = instance.getServer().createInventory(null,inventory.getSize(), inventory.getTitle());
							for(ItemStack i : inventory.getContents())
								inv.setItem(inv.firstEmpty(), i);
							for(Map.Entry<Integer, String> slot : kitsmanager.getOtherInvsSlots().get(acao.split(" ")[2]).entrySet()){
								if(slot.getValue().startsWith("dar kit ")){
									try {
										long total = new Kit(slot.getValue().split(" ")[2].toLowerCase(), instance.getEssentials()).getNextUse(new User(p, instance.getEssentials()));
										if(total > System.currentTimeMillis()){
											inv.setItem(slot.getKey(), (ItemStack) instance.criarItem(instance.getConfig().getString("OtherInvs." + acao.split(" ")[2] + ".Slots." + (slot.getKey()+1) + ".Kit_Usado").replace("{tempo}", instance.getTime(total-System.currentTimeMillis()).replace(" ", "_"))));
										}
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								}
							}
							p.openInventory(inv);
						}
					}
					return;
				}
			}
			String previewname = instance.getConfig().getString("Preview_Inv.Nome");
			if(e.getInventory().getTitle().startsWith(previewname.substring(0, previewname.lastIndexOf("{pagina}")).replace("&", "§"))
					|| e.getInventory().getTitle().startsWith(previewname.substring(0, previewname.lastIndexOf("{kit}")).replace("&", "§"))){
				if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
				e.setCancelled(true);
				if(e.getSlot() == instance.getConfig().getInt("Preview_Inv.NextItem_Local")-1){
					for(Map.Entry<String, Integer> pagina : playerkitview.get(p.getName().toLowerCase()).entrySet()){
						p.openInventory(kitsmanager.getPreviewInventory(pagina.getKey(), pagina.getValue()+1));
						pagina.setValue(pagina.getValue()+1);
					}
				}else if(e.getSlot() == instance.getConfig().getInt("Preview_Inv.PreviousItem_Local")-1){
					for(Map.Entry<String, Integer> pagina : playerkitview.get(p.getName().toLowerCase()).entrySet()){
						p.openInventory(kitsmanager.getPreviewInventory(pagina.getKey(), pagina.getValue()-1));
						pagina.setValue(pagina.getValue()-1);
					}
				}
				return;
			}
		}
	}

}

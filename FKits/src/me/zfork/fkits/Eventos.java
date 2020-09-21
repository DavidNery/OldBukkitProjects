package me.zfork.fkits;

import me.zfork.fkits.kits.Kit;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class Eventos implements Listener{

	private FKits instance = FKits.getFKits();

	@EventHandler(ignoreCancelled=true)
	public void Comando(PlayerCommandPreprocessEvent e){
		Player p = e.getPlayer();
		if(e.getMessage().toLowerCase().startsWith("/kit") || e.getMessage().toLowerCase().startsWith("/kits")){
			p.openInventory(instance.getKitsInventory());
			p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Kits_Aberto").replace("&", "§"));
			p.playSound(p.getLocation(), Sound.CHEST_OPEN, 5.0F, 1.0F);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void Click(InventoryClickEvent e){
		if(e.getWhoClicked() instanceof Player){
			Player p = (Player) e.getWhoClicked();
			Inventory inv = e.getInventory();
			if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
			if(/*inv.getTitle().matches("Kits - .+") || */inv.getTitle().matches("Kit .+ - \\d+") || inv.getTitle().equalsIgnoreCase("Kits")){
				e.setCancelled(true);
				p.updateInventory();
				if(inv.getTitle().equalsIgnoreCase("Kits")){
					if(!(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) || e.getClickedInventory().equals(p.getInventory())) return;
					String kitname = e.getCurrentItem().getItemMeta().getDisplayName().split("§e§lKit ")[1];
					//if(p.getOpenInventory() != null) p.getOpenInventory().close();
					//p.openInventory(instance.getInv(kitname));
					if(!(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName())) return;
					Kit kit = instance.getKitsManager().getKit(kitname);
					if(e.getClick() == ClickType.LEFT){
						if(!p.hasPermission("fkits." + kitname.toLowerCase())){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS_GUITAR, 5.0F, 1.0F);
							return;
						}
						long delay = instance.getKitsManager().getDelay(kit, p.getName());
						if(delay != -1 && delay > System.currentTimeMillis()){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde").replace("&", "§").replace("{restante}", instance.getTime(delay - System.currentTimeMillis())));
							p.playSound(p.getLocation(), Sound.NOTE_BASS_GUITAR, 5.0F, 1.0F);
							return;
						}
						for(Object item : kit.getItems()){
							if(p.getInventory().firstEmpty() != -1){
								p.getInventory().setItem(p.getInventory().firstEmpty(), (ItemStack) item);
							}else{
								instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
									@Override
									public void run(){
										p.getWorld().dropItem(p.getLocation(), ((ItemStack) item).clone());
									}
								});
							}
						}
						instance.getKitsManager().setPlayer(kit, p.getName());
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Pegou_Kit").replace("&", "§").replace("{kit}", kitname));
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 5.0F, 1.0F);
						return;
					}else{
						if(p.getOpenInventory() != null) p.getOpenInventory().close();
						p.openInventory(instance.getKitsManager().getInventoryKit(kitname, 1));
					}
					p.playSound(p.getLocation(), Sound.NOTE_PLING, 5.0F, 1.0F);
				}/*else if(inv.getTitle().matches("Kits - .+")){
					String kitname = e.getInventory().getTitle().split("Kits - ")[1];
					if(!(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()) || 
							e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§e§l" + e.getInventory().getTitle())) return;
					Kit kit = instance.getKitsManager().getKit(kitname);
					if(e.getClick() == ClickType.LEFT){
						if(!p.hasPermission("fkits." + kitname.toLowerCase())){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
							p.playSound(p.getLocation(), Sound.NOTE_BASS_GUITAR, 5.0F, 1.0F);
							return;
						}
						long delay = instance.getKitsManager().getDelay(kit, p.getName());
						if(delay != -1 && delay > System.currentTimeMillis()){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde").replace("&", "§").replace("{restante}", instance.getTime(delay - System.currentTimeMillis())));
							p.playSound(p.getLocation(), Sound.NOTE_BASS_GUITAR, 5.0F, 1.0F);
							return;
						}
						for(Object item : kit.getItems()){
							if(p.getInventory().firstEmpty() != -1){
								p.getInventory().setItem(p.getInventory().firstEmpty(), (ItemStack) item);
							}else{
								instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
									@Override
									public void run(){
										p.getWorld().dropItem(p.getLocation(), ((ItemStack) item).clone());
									}
								});
							}
						}
						instance.getKitsManager().setPlayer(kit, p.getName());
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Pegou_Kit").replace("&", "§").replace("{kit}", kitname));
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 5.0F, 1.0F);
						return;
					}else{
						if(p.getOpenInventory() != null) p.getOpenInventory().close();
						p.openInventory(instance.getKitsManager().getInventoryKit(kitname, 1));
					}
				}*/else if(inv.getTitle().matches("Kit .+ - \\d+")){
					if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()){
						if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§c§lVoltar")){
							if(p.getOpenInventory() != null) p.getOpenInventory().close();
							p.openInventory(instance.getKitsInventory());
							p.playSound(p.getLocation(), Sound.NOTE_PIANO, 5.0F, 1.0F);
						}else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§a§lPróxima página")){
							String kitname = e.getInventory().getTitle().split("Kit ")[1].replaceAll(" - .+", "");
							int page = Integer.parseInt(e.getInventory().getTitle().split("Kit " + kitname + " - ")[1]);
							if(instance.getKitsManager().getInventoryKit(kitname, page + 1) == null){
								p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Pagina_Inexistente").replace("&", "§"));
								p.playSound(p.getLocation(), Sound.NOTE_BASS_GUITAR, 5.0F, 1.0F);
							}else{
								if(p.getOpenInventory() != null) p.getOpenInventory().close();
								p.openInventory(instance.getKitsManager().getInventoryKit(kitname, page + 1));
								p.playSound(p.getLocation(), Sound.CHEST_OPEN, 5.0F, 1.0F);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void Drop(PlayerDropItemEvent e){
		Player p = e.getPlayer();
		if(p.getOpenInventory() != null){
			InventoryView inv = p.getOpenInventory();
			if(inv.getTitle().matches("Kits - .+") || inv.getTitle().matches("Kit .+ - \\d+") || inv.getTitle().equalsIgnoreCase("Kits")){
				e.setCancelled(true);
				p.updateInventory();
			}
		}
	}

}

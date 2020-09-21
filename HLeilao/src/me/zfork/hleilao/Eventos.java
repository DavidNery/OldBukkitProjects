package me.zfork.hleilao;

import java.util.concurrent.TimeUnit;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Eventos implements Listener{
	
	private HLeilao instance = HLeilao.getHLeilao();
	private LeilaoManager lm = instance.getLeilaoManager();
	
	@EventHandler
	public void Join(PlayerJoinEvent e){
		final Player p = e.getPlayer();
		new BukkitRunnable() {
			@Override
			public void run() {
				if(instance.getDevolver().containsKey(p.getName())){
					for(int i = 0; i<100; i++) p.sendMessage("");
					p.sendMessage(instance.getConfig().getString("Mensagem.Tem_Item_Para_Recuperar").replace("&", "§"));
					p.playSound(p.getLocation(), Sound.ANVIL_USE, 10.0F, 1.0F);
				}
			}
		}.runTaskLater(instance, 5*20);
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void Quit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(lm.getLeiloando() != null && instance.getTask() != null){
			if(instance.getServer().getPlayer(lm.getLeiloando()).getName().equals(p.getName())){
				instance.getTask().cancel();
				instance.setTask(null);
				for(String msg : instance.getConfig().getStringList("Mensagem.Dono_Saiu")){
					for(Player on : instance.getServer().getOnlinePlayers()){
						on.sendMessage(msg.replace("&", "§").replace("{player}", lm.getLeiloando()).replace("{item}", lm.getItem().getType().name()));
					}
				}
				if(instance.getConfig().getBoolean("Config.Retirar_Dinheiro_Apostar")){
					for(String player : instance.getApostado().keySet()){
						instance.econ.depositPlayer(player, instance.getApostado().get(player));
					}
				}
				if(p.getInventory().firstEmpty() == -1){
					instance.getDevolver().put(p.getName(), lm.getItem());
				}else{
					p.getInventory().addItem(lm.getItem());
				}
				lm.resetar(System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.Tempo_Entre_Leiloes")));
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void Click(InventoryClickEvent e){
		if(e.getInventory().getTitle() != null && e.getInventory().getTitle().equals("Leilao")){
			if(e.getCurrentItem() != null){
				e.setCancelled(true);
				((Player) e.getWhoClicked()).updateInventory();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void Drop(PlayerDropItemEvent e){
		Player p = e.getPlayer();
		if(p.getOpenInventory() != null){
			if(p.getOpenInventory().getTitle() != null && p.getOpenInventory().getTitle().equals("Leilao")){
				if(e.getItemDrop() != null){
					e.setCancelled(true);
					p.updateInventory();
				}
			}
		}
	}
	
	@EventHandler
	public void Close(final InventoryCloseEvent e){
		final Player p = (Player) e.getPlayer();
		if(e.getInventory().getTitle() != null && e.getInventory().getTitle().equals("Seu item")){
			if(isNotVazio(e.getInventory())){
				new BukkitRunnable() {
					@Override
					public void run(){
						p.openInventory(e.getInventory());
					}
				}.runTaskLater(instance, 1L);
			}
		}
	}
	
	public boolean isNotVazio(Inventory inv){
		for(ItemStack item : inv.getContents())
			if(item != null && item.getType() != Material.AIR) return true;
		return false;
	}

}

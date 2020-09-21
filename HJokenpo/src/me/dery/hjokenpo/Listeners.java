package me.dery.hjokenpo;

import me.dery.hjokenpo.desafio.Desafio;
import me.dery.hjokenpo.desafio.DesafioManager;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Listeners implements Listener{
	
	public static HJokenpo instance = HJokenpo.getHJokenpo();
	public static DesafioManager dm = instance.getDesafioManager();
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Click(InventoryPickupItemEvent e){
		if(e.getInventory().getTitle().replace("&", "§").equalsIgnoreCase(instance.getConfig().getString("Jokenpo_Inv_Nome").replace("&", "§"))){
			e.setCancelled(true);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Click(InventoryMoveItemEvent e){
		if(e.getDestination().getTitle().replace("&", "§").equalsIgnoreCase(instance.getConfig().getString("Jokenpo_Inv_Nome").replace("&", "§")) || e.getInitiator().getTitle().equalsIgnoreCase(instance.getConfig().getString("Jokenpo_Inv_Nome").replace("&", "§"))){
			e.setCancelled(true);
			((Player) e.getSource().getHolder()).updateInventory();
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Click(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		if(inv.getTitle().replace("&", "§").equalsIgnoreCase(instance.getConfig().getString("Jokenpo_Inv_Nome").replace("&", "§"))){
			if(dm.hasPlayer(p.getName())){
				if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
					e.setCancelled(true);
					if(e.getCurrentItem().getTypeId() == instance.getConfig().getInt("Item_Completar")) return;
					Desafio desafio = null;
					for(Desafio desafios : dm.getDesafios()){
						if(desafios.getP1().equalsIgnoreCase(p.getName()) || desafios.getP2().equalsIgnoreCase(p.getName())){
							desafio = desafios;
							break;
						}
					}
					ItemMeta im = e.getCurrentItem().getItemMeta();
					if(im.getDisplayName().equalsIgnoreCase("§6Pedra")){
						p.getOpenInventory().setItem(31, e.getCurrentItem());
						if(desafio.getP1().equalsIgnoreCase(p.getName())){
							desafio.setEscolhaP1(0);
							desafio.setEscolheuP1(true);
						}else{
							desafio.setEscolhaP2(0);
							desafio.setEscolheuP2(true);
						}
					}else if(im.getDisplayName().equalsIgnoreCase("§6Tesoura")){
						p.getOpenInventory().setItem(31, e.getCurrentItem());
						if(desafio.getP1().equalsIgnoreCase(p.getName())){
							desafio.setEscolhaP1(1);
							desafio.setEscolheuP1(true);
						}else{
							desafio.setEscolhaP2(1);
							desafio.setEscolheuP2(true);
						}
					}else if(im.getDisplayName().equalsIgnoreCase("§6Papel")){
						p.getOpenInventory().setItem(31, e.getCurrentItem());
						if(desafio.getP1().equalsIgnoreCase(p.getName())){
							desafio.setEscolhaP1(2);
							desafio.setEscolheuP1(true);
						}else{
							desafio.setEscolhaP2(2);
							desafio.setEscolheuP2(true);
						}
					}else if(im.getDisplayName().equalsIgnoreCase("§aPronto.")){
						if(!desafio.getP1().equalsIgnoreCase(p.getName())){
							ItemStack amarela = new ItemStack(Material.WOOL, 1, (byte) 4);
							ItemMeta amarelameta = amarela.getItemMeta();
							amarelameta.setDisplayName("§aOponente ja escolheu, agora sua vez!");
							amarela.setItemMeta(amarelameta);
							instance.getServer().getPlayer(desafio.getP1()).getOpenInventory().setItem(33, amarela);
							if(desafio.getEscolheuP1() == true){
								checarVitoria(desafio);
							}
						}else if(!desafio.getP2().equalsIgnoreCase(p.getName())){
							ItemStack amarela = new ItemStack(Material.WOOL, 1, (byte) 4);
							ItemMeta amarelameta = amarela.getItemMeta();
							amarelameta.setDisplayName("§aOponente ja escolheu, agora sua vez!");
							amarela.setItemMeta(amarelameta);
							instance.getServer().getPlayer(desafio.getP2()).getOpenInventory().setItem(33, amarela);
							if(desafio.getEscolheuP2() == true){
								checarVitoria(desafio);
							}
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Close(InventoryCloseEvent e){
		Player p = (Player) e.getPlayer();
		if(dm.hasPlayer(p.getName())){
			Desafio desafio = null;
			for(Desafio desafios : dm.getDesafios()){
				if(desafios.getP1().equalsIgnoreCase(p.getName()) || desafios.getP2().equalsIgnoreCase(p.getName())){
					desafio = desafios;
					break;
				}
			}
			instance.econ.depositPlayer(desafio.getP1(), desafio.getPremio() / 2);
			instance.econ.depositPlayer(desafio.getP2(), desafio.getPremio() / 2);
			if(desafio.getP1().equalsIgnoreCase(p.getName())){
				dm.removePlayer(desafio.getP1());
				dm.removePlayer(desafio.getP2());
				instance.getServer().getPlayer(desafio.getP2()).getOpenInventory().close();
				dm.removeDesafio(desafio);
			}else{
				dm.removePlayer(desafio.getP1());
				dm.removePlayer(desafio.getP2());
				instance.getServer().getPlayer(desafio.getP1()).getOpenInventory().close();
				dm.removeDesafio(desafio);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Sair(PlayerQuitEvent e){
		Player p = (Player) e.getPlayer();
		if(dm.hasPlayer(p.getName())){
			Desafio desafio = null;
			for(Desafio desafios : dm.getDesafios()){
				if(desafios.getP1().equalsIgnoreCase(p.getName()) || desafios.getP2().equalsIgnoreCase(p.getName())){
					desafio = desafios;
					break;
				}
			}
			instance.econ.depositPlayer(desafio.getP1(), desafio.getPremio() / 2);
			instance.econ.depositPlayer(desafio.getP2(), desafio.getPremio() / 2);
			if(desafio.getP1().equalsIgnoreCase(p.getName())){
				dm.removePlayer(desafio.getP1());
				dm.removePlayer(desafio.getP2());
				instance.getServer().getPlayer(desafio.getP2()).getOpenInventory().close();
				instance.getServer().getPlayer(desafio.getP2()).sendMessage(instance.getConfig().getString("Deslogou").replace("&", "§"));
				dm.removeDesafio(desafio);
			}else{
				dm.removePlayer(desafio.getP1());
				dm.removePlayer(desafio.getP2());
				instance.getServer().getPlayer(desafio.getP1()).getOpenInventory().close();
				instance.getServer().getPlayer(desafio.getP1()).sendMessage(instance.getConfig().getString("Deslogou").replace("&", "§"));
				dm.removeDesafio(desafio);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Sair(PlayerKickEvent e){
		Player p = (Player) e.getPlayer();
		if(dm.hasPlayer(p.getName())){
			Desafio desafio = null;
			for(Desafio desafios : dm.getDesafios()){
				if(desafios.getP1().equalsIgnoreCase(p.getName()) || desafios.getP2().equalsIgnoreCase(p.getName())){
					desafio = desafios;
					break;
				}
			}
			instance.econ.depositPlayer(desafio.getP1(), desafio.getPremio() / 2);
			instance.econ.depositPlayer(desafio.getP2(), desafio.getPremio() / 2);
			if(desafio.getP1().equalsIgnoreCase(p.getName())){
				dm.removePlayer(desafio.getP1());
				dm.removePlayer(desafio.getP2());
				instance.getServer().getPlayer(desafio.getP2()).getOpenInventory().close();
				instance.getServer().getPlayer(desafio.getP2()).sendMessage(instance.getConfig().getString("Deslogou").replace("&", "§"));
				dm.removeDesafio(desafio);
			}else{
				dm.removePlayer(desafio.getP1());
				dm.removePlayer(desafio.getP2());
				instance.getServer().getPlayer(desafio.getP1()).getOpenInventory().close();
				instance.getServer().getPlayer(desafio.getP1()).sendMessage(instance.getConfig().getString("Deslogou").replace("&", "§"));
				dm.removeDesafio(desafio);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	public void premiar(String player, Desafio desafio){
		Player p = instance.getServer().getPlayer(player);
		instance.econ.depositPlayer(player, desafio.getPremio());
		for(int j = 0; j<5; j++){
			Firework fw = p.getWorld().spawn(p.getLocation(), Firework.class);
			FireworkMeta fm = fw.getFireworkMeta();
			fm.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.AQUA, Color.RED)
					.withFade(Color.BLACK).flicker(true).build());
			fm.setPower(0);
			fw.setFireworkMeta(fm);
		}
		instance.getServer().getPlayer(desafio.getP1()).getOpenInventory().close();
		instance.getServer().getPlayer(desafio.getP2()).getOpenInventory().close();
		dm.removeDesafio(desafio);
	}
	
	@SuppressWarnings("static-access")
	public void checarVitoria(Desafio desafio){
		if(desafio.getEscolhaP1() == 0 && desafio.getEscolhaP2() == 1){
			instance.getServer().getPlayer(desafio.getP1()).sendMessage(instance.getConfig().getString("Venceu").replace("&", "§"));
			instance.getServer().getPlayer(desafio.getP2()).sendMessage(instance.getConfig().getString("Perdeu").replace("&", "§"));
			instance.econ.withdrawPlayer(desafio.getP2(), desafio.getPremio() / 2);
			premiar(desafio.getP1(), desafio);
		}else if(desafio.getEscolhaP2() == 0 && desafio.getEscolhaP1() == 1){
			instance.getServer().getPlayer(desafio.getP1()).sendMessage(instance.getConfig().getString("Perdeu").replace("&", "§"));
			instance.getServer().getPlayer(desafio.getP2()).sendMessage(instance.getConfig().getString("Venceu").replace("&", "§"));
			instance.econ.withdrawPlayer(desafio.getP1(), desafio.getPremio() / 2);
			premiar(desafio.getP2(), desafio);
		}else if(desafio.getEscolhaP1() == 1 && desafio.getEscolhaP2() == 2){
			instance.getServer().getPlayer(desafio.getP1()).sendMessage(instance.getConfig().getString("Venceu").replace("&", "§"));
			instance.getServer().getPlayer(desafio.getP2()).sendMessage(instance.getConfig().getString("Perdeu").replace("&", "§"));
			instance.econ.withdrawPlayer(desafio.getP2(), desafio.getPremio() / 2);
			premiar(desafio.getP1(), desafio);
		}else if(desafio.getEscolhaP2() == 2 && desafio.getEscolhaP1() == 1){
			instance.getServer().getPlayer(desafio.getP1()).sendMessage(instance.getConfig().getString("Perdeu").replace("&", "§"));
			instance.getServer().getPlayer(desafio.getP2()).sendMessage(instance.getConfig().getString("Venceu").replace("&", "§"));
			instance.econ.withdrawPlayer(desafio.getP1(), desafio.getPremio() / 2);
			premiar(desafio.getP2(), desafio);
		}else if(desafio.getEscolhaP1() == 2 && desafio.getEscolhaP2() == 0){
			instance.getServer().getPlayer(desafio.getP1()).sendMessage(instance.getConfig().getString("Venceu").replace("&", "§"));
			instance.getServer().getPlayer(desafio.getP2()).sendMessage(instance.getConfig().getString("Perdeu").replace("&", "§"));
			instance.econ.withdrawPlayer(desafio.getP2(), desafio.getPremio() / 2);
			premiar(desafio.getP1(), desafio);
		}else if(desafio.getEscolhaP2() == 0 && desafio.getEscolhaP1() == 2){
			instance.getServer().getPlayer(desafio.getP1()).sendMessage(instance.getConfig().getString("Perdeu").replace("&", "§"));
			instance.getServer().getPlayer(desafio.getP2()).sendMessage(instance.getConfig().getString("Venceu").replace("&", "§"));
			instance.econ.withdrawPlayer(desafio.getP1(), desafio.getPremio() / 2);
			premiar(desafio.getP2(), desafio);
		}else if(desafio.getEscolhaP1() == desafio.getEscolhaP2()){
			instance.getServer().getPlayer(desafio.getP1()).getOpenInventory().close();
			instance.getServer().getPlayer(desafio.getP2()).getOpenInventory().close();
			/*instance.econ.depositPlayer(desafio.getP1(), desafio.getPremio() / 2);
			instance.econ.depositPlayer(desafio.getP2(), desafio.getPremio() / 2);*/
			instance.getServer().getPlayer(desafio.getP1()).sendMessage(instance.getConfig().getString("Empate").replace("&", "§"));
			instance.getServer().getPlayer(desafio.getP2()).sendMessage(instance.getConfig().getString("Empate").replace("&", "§"));
			dm.removePlayer(desafio.getP1());
			dm.removePlayer(desafio.getP2());
			dm.removeDesafio(desafio);
		}
	}

}

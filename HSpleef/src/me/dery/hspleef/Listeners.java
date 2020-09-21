package me.dery.hspleef;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;

public class Listeners implements Listener{
	
	private HSpleef instance = HSpleef.getHSpleef();
	
	@EventHandler
	public void Chat(ChatMessageEvent e){
		Player p = e.getSender();
		if(instance.getConfig().getString("Config.Vencedor").equalsIgnoreCase(p.getName()) && e.getTags().contains("spleef")){
			e.setTagValue("spleef", instance.getConfig().getString("Config.TAG").replace("&", "§"));
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void SetarChao(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR && p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName() && p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§eSete o chao do Spleef")){
				if(!p.hasPermission("hspleef.admin")) return;
				instance.getSpleefManager().setLoc2(e.getClickedBlock().getLocation());
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Pos2_Setada").replace("&", "§"));
				e.setCancelled(true);
			}
		}else if(e.getAction() == Action.LEFT_CLICK_BLOCK){
			if(e.getClickedBlock().getType() == Material.AIR) return;
			if(p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR && p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName() && p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§eSete o chao do Spleef")){
				if(!p.hasPermission("hspleef.admin")) return;
				instance.getSpleefManager().setLoc1(e.getClickedBlock().getLocation());
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Pos1_Setada").replace("&", "§"));
				e.setCancelled(true);
			}
		}
	}
	
	@SuppressWarnings({ "deprecation", "static-access" })
	@EventHandler
	public void Move(PlayerMoveEvent e){
		Player p = e.getPlayer();
		if(instance.getSpleefManager().getEstagio() == 2){
			if(instance.getSpleefManager().getPlayers().contains(p.getName())){
				if(p.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.getMaterial(8) || p.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.getMaterial(9)){
					instance.getSpleefManager().getPlayers().remove(p.getName());
					p.getInventory().clear();
					p.getInventory().setArmorContents(null);
					p.updateInventory();
					instance.getSpleefManager().hasVencedor(p);
					p.teleport(instance.getSpleefManager().getExit());
				}
			}
		}
	}
	
	@SuppressWarnings({ "deprecation", "static-access" })
	@EventHandler
	public void Death(PlayerDeathEvent e){
		Player p = e.getEntity();
		if(instance.getSpleefManager().getEstagio() != 0){
			if(instance.getSpleefManager().getPlayers().contains(p.getName())){
				instance.getSpleefManager().getPlayers().remove(p.getName());
				e.setDroppedExp(0);
				e.getDrops().clear();
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
				p.updateInventory();
				instance.getSpleefManager().hasVencedor(p);
				p.teleport(instance.getSpleefManager().getExit());
			}
		}
	}
	
	@SuppressWarnings({ "deprecation", "static-access" })
	@EventHandler
	public void Death(PlayerKickEvent e){
		Player p = e.getPlayer();
		if(instance.getSpleefManager().getEstagio() != 0){
			if(instance.getSpleefManager().getPlayers().contains(p.getName())){
				instance.getSpleefManager().getPlayers().remove(p.getName());
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
				p.updateInventory();
				instance.getSpleefManager().hasVencedor(p);
				p.teleport(instance.getSpleefManager().getExit());
			}
		}
	}
	
	@SuppressWarnings({ "deprecation", "static-access" })
	@EventHandler
	public void Death(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(instance.getSpleefManager().getEstagio() != 0){
			if(instance.getSpleefManager().getPlayers().contains(p.getName())){
				instance.getSpleefManager().getPlayers().remove(p.getName());
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
				p.updateInventory();
				instance.getSpleefManager().hasVencedor(p);
				p.teleport(instance.getSpleefManager().getExit());
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void Tp(PlayerTeleportEvent e){
		if(instance.getSpleefManager().getEstagio() != 0){
			if(instance.getSpleefManager().getPlayers().contains(e.getPlayer().getName())){
				if(e.getTo().equals(instance.getSpleefManager().getSpawn())) return;
				if(e.getTo().equals(instance.getSpleefManager().getLobby())) return;
				if(e.getTo().equals(instance.getSpleefManager().getExit())) return;
				e.setCancelled(true);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void PvP(EntityDamageByEntityEvent e){
		if(instance.getSpleefManager().getEstagio() != 0){
			if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
				Player p = (Player) e.getEntity();
				Player damager = (Player) e.getDamager();
				if(instance.getSpleefManager().getPlayers().contains(damager.getName())){
					e.setCancelled(true);
				}else if(instance.getSpleefManager().getPlayers().contains(p.getName())){
					e.setCancelled(true);
				}
			}else if(e.getEntity() instanceof Player && e.getDamager() instanceof Projectile){
				Player p = (Player) e.getEntity();
				Projectile projectile = (Projectile) e.getDamager();
				if(projectile.getShooter() instanceof Player){
					Player damager = (Player) projectile.getShooter();
					if(instance.getSpleefManager().getPlayers().contains(damager.getName())){
						e.setCancelled(true);
					}else if(instance.getSpleefManager().getPlayers().contains(p.getName())){
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.MONITOR)
	public void Comandos(PlayerCommandPreprocessEvent e){
		Player p = e.getPlayer();
		if(instance.getSpleefManager().getEstagio() != 0){
			if(instance.getSpleefManager().getPlayers().contains(p.getName())){
				for(String cmds : instance.getConfig().getStringList("Config.Comandos_Liberados")){
					if(e.getMessage().toLowerCase().startsWith("/" + cmds)){
						return;
					}
				}
				e.setCancelled(true);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void Construir(BlockBreakEvent e){
		Player p = e.getPlayer();
		if(instance.getSpleefManager().getEstagio() != 0){
			if(instance.getSpleefManager().getPlayers().contains(p.getName())){
				e.setCancelled(true);
				if(e.getBlock().getType() != Material.SNOW_BLOCK) return;
				e.getBlock().setType(Material.AIR);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void Construir(BlockPlaceEvent e){
		Player p = e.getPlayer();
		if(instance.getSpleefManager().getEstagio() != 0){
			if(instance.getSpleefManager().getPlayers().contains(p.getName())){
				e.setCancelled(true);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void Construir(PlayerBucketEmptyEvent e){
		Player p = e.getPlayer();
		if(instance.getSpleefManager().getEstagio() != 0){
			if(instance.getSpleefManager().getPlayers().contains(p.getName())){
				e.setCancelled(true);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void Construir(PlayerBucketFillEvent e){
		Player p = e.getPlayer();
		if(instance.getSpleefManager().getEstagio() != 0){
			if(instance.getSpleefManager().getPlayers().contains(p.getName())){
				e.setCancelled(true);
			}
		}
	}

}

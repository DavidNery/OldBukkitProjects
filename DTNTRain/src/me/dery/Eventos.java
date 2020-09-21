package me.dery;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitScheduler;

public class Eventos implements Listener{
	
	DTNTRain instance = DTNTRain.getDTNTRain();
	int id, tempo = 6;
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void MorreuEvento(PlayerDeathEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			if(instance.getcmds().participantes.contains(p.getName()) || instance.getcmds().noevento.contains(p.getName())){
				instance.getcmds().participantes.remove(p.getName());
				instance.getcmds().noevento.remove(p.getName());
			}
			for(String tp : instance.getcmds().participantes){
				Player player = instance.getServer().getPlayer(tp);
				for(String msgs : instance.getConfig().getStringList("Mensagem.Saiu_Evento")){
					player.sendMessage(msgs.replace("{player}", p.getName()).replace("&", "§"));
				}
			}
			instance.gettntrm().TPExit(p);
			if(instance.getcmds().noevento.size() == 1){
				Bukkit.getScheduler().cancelTask(instance.getcmds().id);
				Bukkit.getScheduler().cancelTask(instance.getcmds().id2);
				for(String vencedor : instance.getcmds().participantes){
					Player tp = instance.getServer().getPlayer(vencedor);
					instance.gettntrm().TPExit(tp);
					instance.econ.depositPlayer(tp.getName(), instance.getConfig().getInt("Config.Premio"));
					if(instance.getConfig().getBoolean("Config.Comandos")){
						for(String cmds : instance.getConfig().getStringList("Config.Comandos_Executados")){
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmds.replace("{player}", tp.getName()));
						}
					}
					for(String fim : instance.getConfig().getStringList("Mensagem.Venceu")){
						for(Player on : Bukkit.getOnlinePlayers()){
							on.sendMessage(fim.replace("{ganhador}", tp.getName()).replace("{premio}", String.valueOf(instance.getConfig().getDouble("Config.Premio"))).replace("&", "§"));
						}
					}
				}
				instance.getcmds().participantes.clear();
				instance.getcmds().noevento.clear();
				BukkitScheduler scheduler = instance.getServer().getScheduler();
				id = scheduler.scheduleSyncDelayedTask(instance, new Runnable(){
					public void run(){
						instance.getcmds().ocorrendo = false;
						instance.getcmds().aberto = false;
					}
				}, 5 * 20);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void SaiuEvento(PlayerQuitEvent e){
		if(instance.getcmds().participantes.contains(e.getPlayer().getName()) || instance.getcmds().participantes.contains(e.getPlayer().getName())){
			instance.getcmds().participantes.remove(e.getPlayer().getName());
			instance.getcmds().participantes.remove(e.getPlayer().getName());
			for(String tp : instance.getcmds().participantes){
				Player player = instance.getServer().getPlayer(tp);
				player.sendMessage(instance.getConfig().getString("Mensagem.Saiu_Evento").replace("{player}", e.getPlayer().getName()).replace("&", "§"));
			}
			instance.gettntrm().TPExit(e.getPlayer());
			if(instance.getcmds().noevento.size() == 1){
				Bukkit.getScheduler().cancelTask(instance.getcmds().id);
				Bukkit.getScheduler().cancelTask(instance.getcmds().id2);
				for(String vencedor : instance.getcmds().participantes){
					Player tp = instance.getServer().getPlayer(vencedor);
					instance.gettntrm().TPExit(tp);
					instance.econ.depositPlayer(tp.getName(), instance.getConfig().getInt("Config.Premio"));
					if(instance.getConfig().getBoolean("Config.Comandos")){
						for(String cmds : instance.getConfig().getStringList("Config.Comandos_Executados")){
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmds.replace("{player}", tp.getName()));
						}
					}
					for(String fim : instance.getConfig().getStringList("Mensagem.Venceu")){
						for(Player on : Bukkit.getOnlinePlayers()){
							on.sendMessage(fim.replace("{ganhador}", tp.getName()).replace("{premio}", String.valueOf(instance.getConfig().getDouble("Config.Premio"))).replace("&", "§"));
						}
					}
				}
				instance.getcmds().participantes.clear();
				instance.getcmds().noevento.clear();
				BukkitScheduler scheduler = instance.getServer().getScheduler();
				id = scheduler.scheduleSyncDelayedTask(instance, new Runnable(){
					public void run(){
						instance.getcmds().ocorrendo = false;
						instance.getcmds().aberto = false;
					}
				}, 5 * 20);
			}
		}else if(instance.getcmds().noevento.contains(e.getPlayer())){
			instance.getcmds().noevento.remove(e.getPlayer());
			for(String tp : instance.getcmds().noevento){
				Player player = instance.getServer().getPlayer(tp);
				player.sendMessage(instance.getConfig().getString("Mensagem.Saiu_Evento").replace("{player}", e.getPlayer().getName()).replace("&", "§"));
			}
			instance.gettntrm().TPExit(e.getPlayer());
			if(instance.getcmds().noevento.size() == 1){
				Bukkit.getScheduler().cancelTask(instance.getcmds().id);
				Bukkit.getScheduler().cancelTask(instance.getcmds().id2);
				for(String vencedor : instance.getcmds().participantes){
					Player tp = instance.getServer().getPlayer(vencedor);
					instance.gettntrm().TPExit(tp);
					instance.econ.depositPlayer(tp.getName(), instance.getConfig().getInt("Config.Premio"));
					if(instance.getConfig().getBoolean("Config.Comandos")){
						for(String cmds : instance.getConfig().getStringList("Config.Comandos_Executados")){
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmds.replace("{player}", tp.getName()));
						}
					}
					for(String fim : instance.getConfig().getStringList("Mensagem.Venceu")){
						for(Player on : Bukkit.getOnlinePlayers()){
							on.sendMessage(fim.replace("{ganhador}", tp.getName()).replace("{premio}", String.valueOf(instance.getConfig().getDouble("Config.Premio"))).replace("&", "§"));
						}
					}
				}
				instance.getcmds().participantes.clear();
				instance.getcmds().noevento.clear();
				BukkitScheduler scheduler = instance.getServer().getScheduler();
				id = scheduler.scheduleSyncDelayedTask(instance, new Runnable(){
					public void run(){
						instance.getcmds().ocorrendo = false;
						instance.getcmds().aberto = false;
					}
				}, 5 * 20);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void SaiuEvento(PlayerKickEvent e){
		if(instance.getcmds().participantes.contains(e.getPlayer().getName()) || instance.getcmds().participantes.contains(e.getPlayer().getName())){
			instance.getcmds().participantes.remove(e.getPlayer().getName());
			instance.getcmds().participantes.remove(e.getPlayer().getName());
			for(String tp : instance.getcmds().participantes){
				Player player = instance.getServer().getPlayer(tp);
				player.sendMessage(instance.getConfig().getString("Mensagem.Saiu_Evento").replace("{player}", e.getPlayer().getName()).replace("&", "§"));
			}
			instance.gettntrm().TPExit(e.getPlayer());
			if(instance.getcmds().noevento.size() == 1){
				Bukkit.getScheduler().cancelTask(instance.getcmds().id);
				Bukkit.getScheduler().cancelTask(instance.getcmds().id2);
				for(String vencedor : instance.getcmds().participantes){
					Player tp = instance.getServer().getPlayer(vencedor);
					instance.gettntrm().TPExit(tp);
					instance.econ.depositPlayer(tp.getName(), instance.getConfig().getInt("Config.Premio"));
					if(instance.getConfig().getBoolean("Config.Comandos")){
						for(String cmds : instance.getConfig().getStringList("Config.Comandos_Executados")){
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmds.replace("{player}", tp.getName()));
						}
					}
					for(String fim : instance.getConfig().getStringList("Mensagem.Venceu")){
						for(Player on : Bukkit.getOnlinePlayers()){
							on.sendMessage(fim.replace("{ganhador}", tp.getName()).replace("{premio}", String.valueOf(instance.getConfig().getDouble("Config.Premio"))).replace("&", "§"));
						}
					}
				}
				instance.getcmds().participantes.clear();
				instance.getcmds().noevento.clear();
				BukkitScheduler scheduler = instance.getServer().getScheduler();
				id = scheduler.scheduleSyncDelayedTask(instance, new Runnable(){
					public void run(){
						instance.getcmds().ocorrendo = false;
						instance.getcmds().aberto = false;
					}
				}, 5 * 20);
			}
		}else if(instance.getcmds().noevento.contains(e.getPlayer())){
			instance.getcmds().noevento.remove(e.getPlayer());
			for(String tp : instance.getcmds().noevento){
				Player player = instance.getServer().getPlayer(tp);
				player.sendMessage(instance.getConfig().getString("Mensagem.Saiu_Evento").replace("{player}", e.getPlayer().getName()).replace("&", "§"));
			}
			instance.gettntrm().TPExit(e.getPlayer());
			if(instance.getcmds().noevento.size() == 1){
				Bukkit.getScheduler().cancelTask(instance.getcmds().id);
				Bukkit.getScheduler().cancelTask(instance.getcmds().id2);
				for(String vencedor : instance.getcmds().participantes){
					Player tp = instance.getServer().getPlayer(vencedor);
					instance.gettntrm().TPExit(tp);
					instance.econ.depositPlayer(tp.getName(), instance.getConfig().getInt("Config.Premio"));
					if(instance.getConfig().getBoolean("Config.Comandos")){
						for(String cmds : instance.getConfig().getStringList("Config.Comandos_Executados")){
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmds.replace("{player}", tp.getName()));
						}
					}
					for(String fim : instance.getConfig().getStringList("Mensagem.Venceu")){
						for(Player on : Bukkit.getOnlinePlayers()){
							on.sendMessage(fim.replace("{ganhador}", tp.getName()).replace("{premio}", String.valueOf(instance.getConfig().getDouble("Config.Premio"))).replace("&", "§"));
						}
					}
				}
				instance.getcmds().participantes.clear();
				instance.getcmds().noevento.clear();
				BukkitScheduler scheduler = instance.getServer().getScheduler();
				id = scheduler.scheduleSyncDelayedTask(instance, new Runnable(){
					public void run(){
						instance.getcmds().ocorrendo = false;
						instance.getcmds().aberto = false;
					}
				}, 5 * 20);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.MONITOR)
	public void Comando(PlayerCommandPreprocessEvent e){
		Player p = e.getPlayer();
		if(instance.getcmds().participantes.contains(p.getName()) || instance.getcmds().noevento.contains(p.getName())){
			if(p.hasPermission("dtntrain.admin")){
				return;
			}
		    for(String cmd : instance.getConfig().getStringList("Config.Comandos_Liberados")){
		    	if(e.getMessage().startsWith("/" + cmd)){
		    		return;
		    	}
		    }
		    e.setCancelled(true);
	    	p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Comandos_Liberados").replace("&", "§"));
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void EnablePvP(EntityDamageByEntityEvent e){
		if(instance.getConfig().getBoolean("Config.Force_PvP_Off")){
			if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
				Player player = (Player) e.getEntity();
				Player damager = (Player) e.getDamager();
				if((instance.getcmds().participantes.contains(player.getName()) || instance.getcmds().noevento.contains(player.getName()))
						&& (instance.getcmds().ocorrendo == true || instance.getcmds().aberto == true)){
					for(String msg : instance.getConfig().getStringList("Mensagem.Erro.PvP_Desativado")){
	            		damager.sendMessage(msg.replace("&", "§"));
	            	}
	                e.setCancelled(true);
				}
				if((instance.getcmds().participantes.contains(player.getName()) || instance.getcmds().noevento.contains(player.getName()))
						&& (instance.getcmds().ocorrendo == true || instance.getcmds().aberto == true)){
					for(String msgp : instance.getConfig().getStringList("Mensagem.Erro.PvP_Desativado")){
	            		damager.sendMessage(msgp.replace("&", "§"));
	            	}
	            	e.setCancelled(true);
				}
			}
			if(e.getDamager() instanceof Projectile && e.getEntity() instanceof Player){
				Player player = (Player) e.getEntity();
				Projectile p = (Projectile) e.getDamager();
				if (p.getShooter() instanceof Player && e.getEntity() instanceof Player){
	                Player damager = (Player) p.getShooter();
	                if((instance.getcmds().participantes.contains(damager.getName()) || instance.getcmds().noevento.contains(damager.getName()))
							&& (instance.getcmds().ocorrendo == true || instance.getcmds().aberto == true)){
	    				for(String msg : instance.getConfig().getStringList("Mensagem.Erro.PvP_Desativado")){
	                		damager.sendMessage(msg.replace("&", "§"));
	                	}
	                    e.setCancelled(true);
	    			}
	                if((instance.getcmds().participantes.contains(player.getName()) || instance.getcmds().noevento.contains(player.getName()))
							&& (instance.getcmds().ocorrendo == true || instance.getcmds().aberto == true)){
	    				for(String msgp : instance.getConfig().getStringList("Mensagem.Erro.PvP_Desativado")){
	                		damager.sendMessage(msgp.replace("&", "§"));
	                	}
	                	e.setCancelled(true);
	    			}
	            }
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void ContruiuEvento(BlockPlaceEvent e){
		Player p = e.getPlayer();
		if(instance.getConfig().getBoolean("Config.Desativar_Construcoes")){
			if(p.hasPermission("dtntrain.admin")){
				return;
			}else{
				if((instance.getcmds().participantes.contains(p.getName()) || instance.getcmds().noevento.contains(p.getName()))
						&& (instance.getcmds().ocorrendo == true || instance.getcmds().aberto == true)){
					e.setCancelled(true);
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Proibido_Construir").replace("&", "§"));
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void ContruiuEvento(BlockBreakEvent e){
		Player p = e.getPlayer();
		if(instance.getConfig().getBoolean("Config.Desativar_Construcoes")){
			if(p.hasPermission("dtntrain.admin")){
				return;
			}else{
				if((instance.getcmds().participantes.contains(p.getName()) || instance.getcmds().noevento.contains(p.getName()))
						&& (instance.getcmds().ocorrendo == true || instance.getcmds().aberto == true)){
					e.setCancelled(true);
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Proibido_Construir").replace("&", "§"));
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void ContruiuEvento(PlayerBucketFillEvent e){
		Player p = e.getPlayer();
		if(instance.getConfig().getBoolean("Config.Desativar_Construcoes")){
			if(p.hasPermission("dtntrain.admin")){
				return;
			}else{
				if((instance.getcmds().participantes.contains(p.getName()) || instance.getcmds().noevento.contains(p.getName()))
						&& (instance.getcmds().ocorrendo == true || instance.getcmds().aberto == true)){
					e.setCancelled(true);
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Proibido_Construir").replace("&", "§"));
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void ContruiuEvento(PlayerBucketEmptyEvent e){
		Player p = e.getPlayer();
		if(instance.getConfig().getBoolean("Config.Desativar_Construcoes")){
			if(p.hasPermission("dtntrain.admin")){
				return;
			}else{
				if((instance.getcmds().participantes.contains(p.getName()) || instance.getcmds().noevento.contains(p.getName()))
						&& (instance.getcmds().ocorrendo == true || instance.getcmds().aberto == true)){
					e.setCancelled(true);
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Proibido_Construir").replace("&", "§"));
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void DanoEvento(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			if(instance.getConfig().getBoolean("Config.Desativar_Damage")){
				if(p.hasPermission("dtntrain.admin")){
					return;
				}else{
					if((instance.getcmds().participantes.contains(p.getName()) || instance.getcmds().noevento.contains(p.getName()))
							&& (instance.getcmds().ocorrendo == true || instance.getcmds().aberto == true)){
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void TP(PlayerTeleportEvent e){
		Player p = e.getPlayer();
		Location spawn = instance.gettntrm().CheckSpawn2();
		Location saida = instance.gettntrm().CheckExit2();
		Location lobby = instance.gettntrm().CheckLobby2();
		if(instance.getConfig().getBoolean("Config.Desativar_Teleporte")){
			if(e.getTo().equals(spawn)){
				return;
			}else if(e.getTo().equals(saida)){
				return;
			}else if(e.getTo().equals(lobby)){
				return;
			}
			File f = new File(instance.getDataFolder(), "locais.yml");
			if(f.exists()){
				if(instance.getcmds().participantes.contains(p.getName())){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.No_Teleport").replace("&", "§"));
					e.setCancelled(true);
				}
			}else{
				return;
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void PegarItemEvento(PlayerPickupItemEvent e){
		Player p = e.getPlayer();
		if(instance.getConfig().getBoolean("Config.Desativar_PickUp")){
			if((instance.getcmds().participantes.contains(p.getName()) || instance.getcmds().noevento.contains(p.getName()))
					&& (instance.getcmds().ocorrendo == true || instance.getcmds().aberto == true)){
				if(p.hasPermission("dtntrain.admin")){
					return;
				}else{
					e.setCancelled(true);
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
    public void CancelarBlockExplosion(EntityExplodeEvent e){
    	if((instance.getcmds().ocorrendo == true) || (instance.getcmds().aberto == true)){
    		for(Block b : e.blockList()){
            	final BlockState state = b.getState();
            	b.setType(Material.AIR);
            	int delay = 0;
    			if((b.getType() == Material.SAND) || (b.getType() == Material.GRAVEL)){
    		    	delay += 1;
    		 	}
    			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(instance, new Runnable(){
    		    	public void run(){
    		        	state.update(true, false);
    		    	}
    		    }, delay);
        	}
    	}
    }

}

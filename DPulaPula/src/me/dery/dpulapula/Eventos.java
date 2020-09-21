package me.dery.dpulapula;

import java.io.File;
import java.text.NumberFormat;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class Eventos implements Listener{
	
	static DPulaPula instance = DPulaPula.getDPulaPula();
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGH)
	public void CaiuAgua(PlayerMoveEvent e){
		if(instance.getConfig().getBoolean("Config.Lose_Fall_Water")){
			Player p = e.getPlayer();
			Location loc = p.getLocation();
			int block = loc.getWorld().getBlockTypeIdAt(loc);
			if(instance.getComandos().players.contains(p.getName()) && (block == 8 || block == 9) && instance.getComandos().ocorrendo == true){
				p.sendMessage(instance.getConfig().getString("Mensagem.Eliminado").replace("&", "§"));
				instance.getPulaPulaManager().TPExit(p);
				instance.getComandos().players.remove(p.getName());
				if(instance.getComandos().players.size() == 0){
					for(String msgs : instance.getConfig().getStringList("Mensagem.BC.Sem_Vencedor")){
						instance.getServer().broadcastMessage(msgs.replace("&", "§"));
					}
					instance.getComandos().ocorrendo = false;
					instance.getComandos().aberto = false;
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void CriouPlaca(SignChangeEvent e){
		Player p = e.getPlayer();
		if(e.getLine(0).equalsIgnoreCase("[PulaPula]")){
			if(p.hasPermission("dpulapula.admin")){
				e.setLine(0, instance.getConfig().getString("Config.Placa.Linha_1").replace("&", "§"));
				e.setLine(1, instance.getConfig().getString("Config.Placa.Linha_2").replace("&", "§"));
				e.setLine(2, instance.getConfig().getString("Config.Placa.Linha_3").replace("&", "§"));
				e.setLine(3, instance.getConfig().getString("Config.Placa.Linha_4").replace("&", "§"));
			}else{
				e.getBlock().breakNaturally();
				e.setCancelled(true);
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao_Placa").replace("&", "§"));
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
    public void Ganhou(PlayerInteractEvent e){
	    if(!(e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
	    if(e.getClickedBlock().getState() instanceof Sign){
	        Sign s = (Sign) e.getClickedBlock().getState();
	        if(s.getLine(0).equalsIgnoreCase(instance.getConfig().getString("Config.Placa.Linha_1").replace("&", "§"))
	        		&& s.getLine(1).equalsIgnoreCase(instance.getConfig().getString("Config.Placa.Linha_2").replace("&", "§"))
	        		&& s.getLine(2).equalsIgnoreCase(instance.getConfig().getString("Config.Placa.Linha_3").replace("&", "§"))
	        		&& s.getLine(3).equalsIgnoreCase(instance.getConfig().getString("Config.Placa.Linha_4").replace("&", "§"))){
	        	Player p = e.getPlayer();
	            if(instance.getComandos().players.contains(p.getName()) && instance.getComandos().ocorrendo == true){
					for(String msgs : instance.getConfig().getStringList("Mensagem.BC.Vencedor")){
						instance.getServer().broadcastMessage(msgs.replace("&", "§").replace("{vencedor}", p.getName()).replace("{premio}", String.valueOf(NumberFormat.getNumberInstance().format(instance.getComandos().premio))));
						instance.getPulaPulaManager().TPExit(p);
					}
					instance.econ.depositPlayer(p.getName(), instance.getComandos().premio);
					if(instance.getConfig().getBoolean("Config.Ativar_Comandos_Vencedor")){
						for(String cmds : instance.getConfig().getStringList("Config.Comandos_Vencedor")){
							instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), cmds.replace("{vencedor}", p.getName()));
						}
					}
					instance.getComandos().aberto = false;
					instance.getComandos().ocorrendo = false;
					instance.getComandos().players.clear();
	            }
	        }
	    }
    }
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Jump(PlayerMoveEvent e){
		Player p = e.getPlayer();
		if(instance.getComandos().players.contains(p.getName()) && instance.getComandos().ocorrendo == true){
			Location loc = p.getLocation().subtract(0, 1, 0);
			if(loc.getBlock().getTypeId() != 0){
				p.setVelocity(new Vector(p.getVelocity().getX(), instance.getConfig().getDouble("Config.Altura_Pulo"), p.getVelocity().getZ()));
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Desativar_Damage(EntityDamageEvent e){
		if(instance.getConfig().getBoolean("Config.Disable_Damage")){
			if(e.getEntity() instanceof Player){
				Player p = (Player) e.getEntity();
				if(instance.getComandos().players.contains(p.getName())){
					e.setCancelled(true);
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void PvP(EntityDamageByEntityEvent e){
		if(instance.getConfig().getBoolean("Config.Disable_PvP")){
			if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
				Player p = (Player) e.getEntity();
				Player damager = (Player) e.getDamager();
				if(instance.getComandos().players.contains(damager.getName())){
					e.setCancelled(true);
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.PvP_Desativado").replace("&", "§"));
				}else if(instance.getComandos().players.contains(p.getName())){
					e.setCancelled(true);
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.PvP_Desativado").replace("&", "§"));
				}
			}else if(e.getEntity() instanceof Player && e.getDamager() instanceof Projectile){
				Player p = (Player) e.getEntity();
				Projectile projectile = (Projectile) e.getDamager();
				Player damager = (Player) projectile.getShooter();
				if(instance.getComandos().players.contains(damager.getName())){
					e.setCancelled(true);
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.PvP_Desativado").replace("&", "§"));
				}else if(instance.getComandos().players.contains(p.getName())){
					e.setCancelled(true);
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.PvP_Desativado").replace("&", "§"));
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void TP(PlayerTeleportEvent e){
		Player p = e.getPlayer();
		Location spawn = instance.getPulaPulaManager().Spawn();
		Location saida = instance.getPulaPulaManager().Exit();
		Location lobby = instance.getPulaPulaManager().Lobby();
		if(e.getTo().equals(spawn)){
			return;
		}else if(e.getTo().equals(saida)){
			return;
		}else if(e.getTo().equals(lobby)){
			return;
		}else if(e.getTo() == p.getLocation().add(0, 1, 0) || e.getTo().equals(p.getLocation().add(0, 1, 0))){
			return;
		}else{
			File f = new File(instance.getDataFolder(), "locais.yml");
			if(f.exists()){
				if(instance.getComandos().players.contains(p.getName())){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.No_Teleport").replace("&", "§"));
					e.setCancelled(true);
				}
			}else{
				return;
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.MONITOR)
	public void Comando(PlayerCommandPreprocessEvent e){
		Player p = e.getPlayer();
		if(instance.getComandos().players.contains(p.getName())){
			if(p.hasPermission("dpulapula.admin")){
				return;
			}
			for(String cmds : instance.getConfig().getStringList("Config.Comandos_Liberados")){
				if(e.getMessage().startsWith("/" + cmds)){
					return;
				}
			}
			e.setCancelled(true);
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Comando_Bloqueado").replace("&", "§").replace("{comando}", e.getMessage()));
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Morreu(PlayerDeathEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			if(instance.getComandos().players.contains(p.getName())){
				if(instance.getComandos().ocorrendo == true){
					if(instance.getComandos().players.contains(p.getName())){
						instance.getComandos().players.remove(p.getName());
						instance.getPulaPulaManager().TPExit(p);
						e.getDrops().clear();
						if(instance.getComandos().players.size() == 0){
							for(String msgs : instance.getConfig().getStringList("Mensagem.BC.Sem_Vencedor")){
								instance.getServer().broadcastMessage(msgs.replace("&", "§"));
							}
							instance.getComandos().ocorrendo = false;
							instance.getComandos().aberto = false;
						}
					}
				}else{
					instance.getComandos().players.remove(p.getName());
					instance.getPulaPulaManager().TPExit(p);
					e.getDrops().clear();
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Kick(PlayerKickEvent e){
		Player p = e.getPlayer();
		if(instance.getComandos().players.contains(p.getName())){
			if(instance.getComandos().ocorrendo == true){
				if(instance.getComandos().players.contains(p.getName())){
					instance.getComandos().players.remove(p.getName());
					instance.getPulaPulaManager().TPExit(p);
					if(instance.getComandos().players.size() == 0){
						for(String msgs : instance.getConfig().getStringList("Mensagem.BC.Sem_Vencedor")){
							instance.getServer().broadcastMessage(msgs.replace("&", "§"));
						}
						instance.getComandos().ocorrendo = false;
						instance.getComandos().aberto = false;
					}
				}
			}else{
				instance.getComandos().players.remove(p.getName());
				instance.getPulaPulaManager().TPExit(p);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Saiu(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(instance.getComandos().players.contains(p.getName())){
			if(instance.getComandos().ocorrendo == true){
				if(instance.getComandos().players.contains(p.getName())){
					instance.getComandos().players.remove(p.getName());
					instance.getPulaPulaManager().TPExit(p);
					if(instance.getComandos().players.size() == 0){
						for(String msgs : instance.getConfig().getStringList("Mensagem.BC.Sem_Vencedor")){
							instance.getServer().broadcastMessage(msgs.replace("&", "§"));
						}
						instance.getComandos().ocorrendo = false;
						instance.getComandos().aberto = false;
					}
				}
			}else{
				instance.getComandos().players.remove(p.getName());
				instance.getPulaPulaManager().TPExit(p);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void ContruiuEvento(BlockPlaceEvent e){
		Player p = e.getPlayer();
		if(p.hasPermission("dpulapula.admin")){
			return;
		}else{
			if(instance.getComandos().players.contains(p.getName())){
				e.setCancelled(true);
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Proibido_Construir").replace("&", "§"));
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void ContruiuEvento(BlockBreakEvent e){
		Player p = e.getPlayer();
		if(p.hasPermission("dpulapula.admin")){
			return;
		}else{
			if(instance.getComandos().players.contains(p.getName())){
				e.setCancelled(true);
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Proibido_Construir").replace("&", "§"));
			}
		}
	}
	
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void ContruiuEvento(PlayerBucketFillEvent e){
		Player p = e.getPlayer();
		if(p.hasPermission("dpulapula.admin")){
			return;
		}else{
			if(instance.getComandos().players.contains(p.getName())){
				e.setCancelled(true);
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Proibido_Construir").replace("&", "§"));
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void ContruiuEvento(PlayerBucketEmptyEvent e){
		Player p = e.getPlayer();
		if(p.hasPermission("dpulapula.admin")){
			return;
		}else{
			if(instance.getComandos().players.contains(p.getName())){
				e.setCancelled(true);
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Proibido_Construir").replace("&", "§"));
			}
		}
	}

}

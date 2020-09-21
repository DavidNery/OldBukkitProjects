package me.dery.dhotpotato;

import java.io.File;
import java.text.NumberFormat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class Eventos implements Listener{
	
	static DHotPotato instance = DHotPotato.getDHotPotato();
	static Comandos cmds = instance.getComandos();
	static PotatoManager pm = instance.getPotatoManager();
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGH)
	public static void Mecheu(InventoryMoveItemEvent e){
		Player p = (Player) e.getInitiator();
		if(cmds.ocorrendo == true && cmds.potator.getName() == p.getName()){
			e.setCancelled(true);
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGH)
	public static void Mecheu(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		if(cmds.ocorrendo == true && cmds.potator.getName() == p.getName()){
			e.setCancelled(true);
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGH)
	public static void Pick(PlayerPickupItemEvent e){
		Player p = e.getPlayer();
		if(cmds.players.contains(p.getName())){
			e.setCancelled(true);
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGH)
	public static void Trocou_Batata(PlayerInteractEntityEvent e){
		Player p = e.getPlayer();
		if(e.getRightClicked().getType() == EntityType.PLAYER){
			Player player = (Player) e.getRightClicked();
			if(cmds.ocorrendo == true && cmds.potator.getName() == p.getName()){
				if(cmds.players.contains(player.getName())){
					if(p.getItemInHand().getType() == Material.POISONOUS_POTATO){
						p.getInventory().remove(new ItemStack(Material.POISONOUS_POTATO));
						p.getInventory().setHelmet(null);
						cmds.potator = player;
						player.getInventory().addItem(new ItemStack(Material.POISONOUS_POTATO));
						player.getInventory().setHelmet(new ItemStack(Material.TNT));
						if(instance.getConfig().getBoolean("Config.Redefine_Time_To_Max")){
							cmds.texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
						}
						for(String pt : cmds.players){
							Player players = instance.getServer().getPlayer(pt);
							players.sendMessage(instance.getConfig().getString("Mensagem.Player_Pegou").replace("{potator}", p.getName()).replace("{player}", player.getName()).replace("&", "§"));
							players.sendMessage(instance.getConfig().getString("Mensagem.Novo_Potator").replace("&", "§").replace("{player}", player.getName()));
						}
					}
				}else{
					e.setCancelled(true);
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Clicado_Nao_Esta").replace("{player}", player.getName()).replace("&", "§"));
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGH)
	public static void Drop(PlayerDropItemEvent e){
		Player p = e.getPlayer();
		if(cmds.players.contains(p.getName())){
			e.setCancelled(true);
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.No_Drop").replace("&", "§"));
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public static void Morreu(PlayerDeathEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			if(cmds.players.contains(p.getName())){
				cmds.players.remove(p.getName());
				pm.TPExit(p);
				e.getDrops().clear();
				p.getInventory().setHelmet(null);
				if(cmds.ocorrendo == true && cmds.players.size() == 0){
					cmds.texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
					cmds.ocorrendo = false;
					cmds.aberto = false;
					cmds.anuncios = instance.getConfig().getInt("Config.Anuncios");
					cmds.tanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
					cmds.texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
					cmds.minplayers = instance.getConfig().getInt("Config.Min_Players");
					instance.getServer().getScheduler().cancelTask(cmds.id2);
					instance.getServer().getScheduler().cancelTask(cmds.id);
					cmds.players.clear();
					for(String fechado : instance.getConfig().getStringList("Mensagem.BC.Fechado")){
						instance.getServer().broadcastMessage(fechado.replace("{motivo}", instance.getConfig().getString("Motivo.Nao_Houve_Vencedores")).replace("{minplayers}", String.valueOf(cmds.minplayers)).replace("{players}", String.valueOf(cmds.players.size())).replace("{premio}", NumberFormat.getNumberInstance().format(cmds.premio)).replace("&", "§"));
					}
					if(cmds.potator != null){
						cmds.potator.getInventory().clear();
					}
					cmds.potator = null;
				}
				if(cmds.ocorrendo == true){
					if(cmds.players.size() == 1){
						Player winner = null;
						for(String vencedor : cmds.players){
							winner = instance.getServer().getPlayer(vencedor);
						}
						if(instance.getConfig().getBoolean("Config.Ativar_Comandos_Vencedor")){
							for(String cmds : instance.getConfig().getStringList("Config.Comandos_Vencedor")){
								instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), cmds.replace("{vencedor}", winner.getName()).replace("/", ""));
							}
						}
						instance.getConfig().set("Config.Vencedor", winner.getName());
						winner.getInventory().setHelmet(null);
						winner.setItemInHand(null);
						instance.econ.depositPlayer(winner.getName(), cmds.premio);
						cmds.ocorrendo = false;
						cmds.aberto = false;
						cmds.potator = null;
						cmds.anuncios = instance.getConfig().getInt("Config.Anuncios");
						cmds.tanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
						cmds.texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
						cmds.minplayers = instance.getConfig().getInt("Config.Min_Players");
						cmds.players.clear();
						pm.TPExit(winner);
						instance.getServer().getScheduler().cancelTask(cmds.id2);
						for(String ganhador : instance.getConfig().getStringList("Mensagem.BC.Vencedor")){
							instance.getServer().broadcastMessage(ganhador.replace("{vencedor}", winner.getName()).replace("{premio}", NumberFormat.getNumberInstance().format(cmds.premio)).replace("&", "§"));
						}
					}else{
						if(cmds.potator.getName() == p.getName()){
							cmds.potator.getInventory().setHelmet(null);
							cmds.potator = instance.getServer().getPlayer(cmds.players.get(cmds.r.nextInt(cmds.players.size())));
							cmds.potator.getInventory().addItem(new ItemStack(Material.POISONOUS_POTATO));
							for(String pt : cmds.players){
								Player playersevento = instance.getServer().getPlayer(pt);
								playersevento.sendMessage(instance.getConfig().getString("Mensagem.Novo_Potator").replace("&", "§").replace("{player}", cmds.potator.getName()));
							}
							cmds.potator.getInventory().setHelmet(new ItemStack(Material.TNT));
							cmds.texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public static void Morreu(PlayerKickEvent e){
		Player p = e.getPlayer();
		if(cmds.players.contains(p.getName())){
			cmds.players.remove(p.getName());
			pm.TPExit(p);
			if(p.getItemInHand() != null) p.getItemInHand().setType(Material.AIR);
			if(p.getInventory().getHelmet() != null) p.getInventory().setHelmet(null);
			if(cmds.ocorrendo == true && cmds.players.size() == 0){
				cmds.texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
				cmds.ocorrendo = false;
				cmds.aberto = false;
				cmds.anuncios = instance.getConfig().getInt("Config.Anuncios");
				cmds.tanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
				cmds.texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
				cmds.minplayers = instance.getConfig().getInt("Config.Min_Players");
				instance.getServer().getScheduler().cancelTask(cmds.id2);
				instance.getServer().getScheduler().cancelTask(cmds.id);
				cmds.players.clear();
				for(String fechado : instance.getConfig().getStringList("Mensagem.BC.Fechado")){
					instance.getServer().broadcastMessage(fechado.replace("{motivo}", instance.getConfig().getString("Motivo.Nao_Houve_Vencedores")).replace("{minplayers}", String.valueOf(cmds.minplayers)).replace("{players}", String.valueOf(cmds.players.size())).replace("{premio}", NumberFormat.getNumberInstance().format(cmds.premio)).replace("&", "§"));
				}
				if(cmds.potator != null){
					cmds.potator.getInventory().clear();
				}
				cmds.potator = null;
			}
			if(cmds.ocorrendo == true){
				if(cmds.players.size() == 1){
					Player winner = null;
					for(String vencedor : cmds.players){
						winner = instance.getServer().getPlayer(vencedor);
					}
					if(instance.getConfig().getBoolean("Config.Ativar_Comandos_Vencedor")){
						for(String cmds : instance.getConfig().getStringList("Config.Comandos_Vencedor")){
							instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), cmds.replace("{vencedor}", winner.getName()).replace("/", ""));
						}
					}
					instance.getConfig().set("Config.Vencedor", winner.getName());
					winner.getInventory().setHelmet(null);
					winner.setItemInHand(null);
					instance.econ.depositPlayer(winner.getName(), cmds.premio);
					cmds.ocorrendo = false;
					cmds.aberto = false;
					cmds.potator = null;
					cmds.anuncios = instance.getConfig().getInt("Config.Anuncios");
					cmds.tanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
					cmds.texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
					cmds.minplayers = instance.getConfig().getInt("Config.Min_Players");
					cmds.players.clear();
					pm.TPExit(winner);
					instance.getServer().getScheduler().cancelTask(cmds.id2);
					for(String ganhador : instance.getConfig().getStringList("Mensagem.BC.Vencedor")){
						instance.getServer().broadcastMessage(ganhador.replace("{vencedor}", winner.getName()).replace("{premio}", NumberFormat.getNumberInstance().format(cmds.premio)).replace("&", "§"));
					}
				}else{
					if(cmds.potator.getName() == p.getName()){
						cmds.potator.getInventory().setHelmet(null);
						cmds.potator = instance.getServer().getPlayer(cmds.players.get(cmds.r.nextInt(cmds.players.size())));
						cmds.potator.getInventory().addItem(new ItemStack(Material.POISONOUS_POTATO));
						for(String pt : cmds.players){
							Player playersevento = instance.getServer().getPlayer(pt);
							playersevento.sendMessage(instance.getConfig().getString("Mensagem.Novo_Potator").replace("&", "§").replace("{player}", cmds.potator.getName()));
						}
						cmds.potator.getInventory().setHelmet(new ItemStack(Material.TNT));
						cmds.texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
					}
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public static void Morreu(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(cmds.players.contains(p.getName())){
			cmds.players.remove(p.getName());
			pm.TPExit(p);
			if(p.getItemInHand() != null) p.getItemInHand().setType(Material.AIR);
			if(p.getInventory().getHelmet() != null) p.getInventory().setHelmet(null);
			if(cmds.ocorrendo == true && cmds.players.size() == 0){
				cmds.texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
				cmds.ocorrendo = false;
				cmds.aberto = false;
				cmds.anuncios = instance.getConfig().getInt("Config.Anuncios");
				cmds.tanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
				cmds.texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
				cmds.minplayers = instance.getConfig().getInt("Config.Min_Players");
				instance.getServer().getScheduler().cancelTask(cmds.id2);
				instance.getServer().getScheduler().cancelTask(cmds.id);
				cmds.players.clear();
				for(String fechado : instance.getConfig().getStringList("Mensagem.BC.Fechado")){
					instance.getServer().broadcastMessage(fechado.replace("{motivo}", instance.getConfig().getString("Motivo.Nao_Houve_Vencedores")).replace("{minplayers}", String.valueOf(cmds.minplayers)).replace("{players}", String.valueOf(cmds.players.size())).replace("{premio}", NumberFormat.getNumberInstance().format(cmds.premio)).replace("&", "§"));
				}
				if(cmds.potator != null){
					cmds.potator.getInventory().clear();
				}
				cmds.potator = null;
			}
			if(cmds.ocorrendo == true){
				if(cmds.players.size() == 1){
					Player winner = null;
					for(String vencedor : cmds.players){
						winner = instance.getServer().getPlayer(vencedor);
					}
					if(instance.getConfig().getBoolean("Config.Ativar_Comandos_Vencedor")){
						for(String cmds : instance.getConfig().getStringList("Config.Comandos_Vencedor")){
							instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), cmds.replace("{vencedor}", winner.getName()).replace("/", ""));
						}
					}
					instance.getConfig().set("Config.Vencedor", winner.getName());
					winner.getInventory().setHelmet(null);
					winner.setItemInHand(null);
					instance.econ.depositPlayer(winner.getName(), cmds.premio);
					cmds.ocorrendo = false;
					cmds.aberto = false;
					cmds.potator = null;
					cmds.anuncios = instance.getConfig().getInt("Config.Anuncios");
					cmds.tanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
					cmds.texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
					cmds.minplayers = instance.getConfig().getInt("Config.Min_Players");
					cmds.players.clear();
					pm.TPExit(winner);
					instance.getServer().getScheduler().cancelTask(cmds.id2);
					for(String ganhador : instance.getConfig().getStringList("Mensagem.BC.Vencedor")){
						instance.getServer().broadcastMessage(ganhador.replace("{vencedor}", winner.getName()).replace("{premio}", NumberFormat.getNumberInstance().format(cmds.premio)).replace("&", "§"));
					}
				}else{
					if(cmds.potator.getName() == p.getName()){
						cmds.potator.getInventory().setHelmet(null);
						cmds.potator = instance.getServer().getPlayer(cmds.players.get(cmds.r.nextInt(cmds.players.size())));
						cmds.potator.getInventory().addItem(new ItemStack(Material.POISONOUS_POTATO));
						for(String pt : cmds.players){
							Player playersevento = instance.getServer().getPlayer(pt);
							playersevento.sendMessage(instance.getConfig().getString("Mensagem.Novo_Potator").replace("&", "§").replace("{player}", cmds.potator.getName()));
						}
						cmds.potator.getInventory().setHelmet(new ItemStack(Material.TNT));
						cmds.texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
					}
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public static void Desativar_Damage(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			if(instance.getComandos().players.contains(p.getName())){
				e.setCancelled(true);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public static void PvP(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
			Player p = (Player) e.getEntity();
			Player damager = (Player) e.getDamager();
			if(instance.getComandos().players.contains(damager.getName())){
				e.setCancelled(true);
				damager.sendMessage(instance.getConfig().getString("Mensagem.Erro.PvP_Desativado").replace("&", "§"));
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
				damager.sendMessage(instance.getConfig().getString("Mensagem.Erro.PvP_Desativado").replace("&", "§"));
			}else if(instance.getComandos().players.contains(p.getName())){
				e.setCancelled(true);
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.PvP_Desativado").replace("&", "§"));
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void TP(PlayerTeleportEvent e){
		if(cmds.ocorrendo == true){
			if( pm.CheckSpawn() || pm.CheckExit() || pm.CheckLobby()) return;
			File f = new File(instance.getDataFolder(), "locais.yml");
			if(!f.exists()) return;
			Player p = e.getPlayer();
			Location spawn = pm.CheckSpawn2();
			Location saida = pm.CheckExit2();
			Location lobby = pm.CheckLobby2();
			if(e.getTo().equals(spawn)){
				return;
			}else if(e.getTo().equals(saida)){
				return;
			}else if(e.getTo().equals(lobby)){
				return;
			}else if(e.getTo().equals(p.getLocation().subtract(0, 1, 0))) return;
			if(f.exists()){
				if(cmds.players.contains(p.getName())){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.No_Teleport").replace("&", "§"));
					e.setCancelled(true);
				}
			}else{
				return;
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGH)
	public static void ReloadStop(PlayerCommandPreprocessEvent e){
		if(e.getMessage().startsWith("/rl") || e.getMessage().startsWith("/reload") || e.getMessage().startsWith("/stop")){
			if(cmds.ocorrendo == true || cmds.aberto == true){
				e.setCancelled(true);
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "hotpotato parar");
				if(e.getMessage().startsWith("/rl") || e.getMessage().startsWith("/reload")){
					Bukkit.getServer().reload();
				}else if(e.getMessage().startsWith("/stop")){
					Bukkit.getServer().shutdown();
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.MONITOR)
	public static void Comando(PlayerCommandPreprocessEvent e){
		Player p = e.getPlayer();
		if(instance.getComandos().players.contains(p.getName())){
			if(p.hasPermission("dhotpotato.admin")){
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
	public static void ContruiuEvento(BlockPlaceEvent e){
		Player p = e.getPlayer();
		if(p.hasPermission("dhotpotato.admin")){
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
	public static void ContruiuEvento(BlockBreakEvent e){
		Player p = e.getPlayer();
		if(p.hasPermission("dhotpotato.admin")){
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
	public static void ContruiuEvento(PlayerBucketFillEvent e){
		Player p = e.getPlayer();
		if(p.hasPermission("dhotpotato.admin")){
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
	public static void ContruiuEvento(PlayerBucketEmptyEvent e){
		Player p = e.getPlayer();
		if(p.hasPermission("dhotpotato.admin")){
			return;
		}else{
			if(instance.getComandos().players.contains(p.getName())){
				e.setCancelled(true);
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Proibido_Construir").replace("&", "§"));
			}
		}
	}

}

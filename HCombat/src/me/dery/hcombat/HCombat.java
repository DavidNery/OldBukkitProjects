package me.dery.hcombat;

import java.io.File;
import java.text.NumberFormat;
import java.util.HashMap;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class HCombat extends JavaPlugin implements Listener{

	public static Economy econ = null;
	private HashMap<String, BukkitTask> players = new HashMap<String, BukkitTask>();
	//private ArrayList<String> relog = new ArrayList<String>();

	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bHCombat§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bDery");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null){
			getServer().getConsoleSender().sendMessage(" §3Vault: §bNao Encontrado");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}else{
			getServer().getConsoleSender().sendMessage(" §3Vault: §bHooked (Economy)");
			if(!new File(getDataFolder(), "config.yml").exists()){
				saveDefaultConfig();
				getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
			}else{
				getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
			}
			setupEconomy();
			Bukkit.getServer().getPluginManager().registerEvents(this, this);
		}
		getServer().getConsoleSender().sendMessage("§3==========[§bHCombat§3]==========");
	}

	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§4==========[§cHCombat§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §cDery");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cHCombat§4]==========");
	}

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}

	@EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void Command(PlayerCommandPreprocessEvent e){
		Player p = e.getPlayer();
		if(p.hasPermission("hcombat.admin")) return;
		if(players.containsKey(p.getName())){
			for(String cmds : getConfig().getStringList("Comandos_Liberados")){
				if(cmds.toLowerCase().startsWith(e.getMessage().toLowerCase().substring(1, e.getMessage().length()))) return;
			}
			p.sendMessage(getConfig().getString("Nao_Pode_Comandos").replace("&", "§").replace("@comando", e.getMessage()));
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void Tp(PlayerTeleportEvent e){
		Player p = e.getPlayer();
		if(p.hasPermission("hcombat.admin")) return;
		if(players.containsKey(p.getName())){
			e.setCancelled(true);
			p.sendMessage(getConfig().getString("Teleporte").replace("&", "§"));
		}
	}

	@EventHandler
	public void Deah(PlayerDeathEvent e){
		Player p = e.getEntity();
		if(players.containsKey(p.getName())){
			if(players.get(p.getName()) != null) players.get(p.getName()).cancel();
			players.remove(p.getName());
		}
	}

	/*@EventHandler
	public void Join(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(relog.contains(p.getName())){
			relog.remove(p.getName());
			if(p.hasPermission("hcombat.admin")) return;
			for(Player on : Bukkit.getOnlinePlayers()){
				on.sendMessage(getConfig().getString("Covarde_Voltou").replace("&", "§").replace("@player", p.getName()));
			}
		}
	}*/

	@EventHandler
	public void Quit(PlayerKickEvent e){
		Player p = e.getPlayer();
		if(players.containsKey(p.getName())){
			if(econ.has(p.getName(), getConfig().getDouble("Valor_A_Retirar"))){
				if(!p.hasPermission("hcombat.admin")){
					econ.withdrawPlayer(p.getName(), getConfig().getDouble("Valor_A_Retirar"));
					for(Player on : Bukkit.getOnlinePlayers()){
						on.sendMessage(getConfig().getString("Deslogou_Em_PvP_Money").replace("&", "§").replace("@player", p.getName()).replace("@valor", NumberFormat.getNumberInstance().format(getConfig().getDouble("Valor_A_Retirar"))));
					}
				}
			}else{
				if(!p.hasPermission("hcombat.admin")){
					p.setHealth(0);
					for(Player on : Bukkit.getOnlinePlayers()){
						on.sendMessage(getConfig().getString("Deslogou_Em_PvP_Kill").replace("&", "§").replace("@player", p.getName()));
					}
					//relog.add(p.getName());
				}
				if(players.get(p.getName()) != null) players.get(p.getName()).cancel();
				players.remove(p.getName());
			}
		}
	}

	@EventHandler
	public void Quit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(players.containsKey(p.getName())){
			if(econ.has(p.getName(), getConfig().getDouble("Valor_A_Retirar"))){
				if(!p.hasPermission("hcombat.admin")){
					econ.withdrawPlayer(p.getName(), getConfig().getDouble("Valor_A_Retirar"));
					for(Player on : Bukkit.getOnlinePlayers()){
						on.sendMessage(getConfig().getString("Deslogou_Em_PvP_Money").replace("&", "§").replace("@player", p.getName()).replace("@valor", NumberFormat.getNumberInstance().format(getConfig().getDouble("Valor_A_Retirar"))));
					}
				}
			}else{
				if(!p.hasPermission("hcombat.admin")){
					p.setHealth(0);
					for(Player on : Bukkit.getOnlinePlayers()){
						on.sendMessage(getConfig().getString("Deslogou_Em_PvP_Kill").replace("&", "§").replace("@player", p.getName()));
					}
					//relog.add(p.getName());
				}
				if(players.get(p.getName()) != null) players.get(p.getName()).cancel();
				players.remove(p.getName());
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority=EventPriority.HIGHEST)
	public void PvP(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
			final Player p = (Player) e.getEntity();
			final Player damager = (Player) e.getDamager();
			if(p.getName().equals(damager.getName())) return;
			if(!players.containsKey(damager.getName())){
				damager.sendMessage(getConfig().getString("Entrou_PvP").replace("@player", p.getName()).replace("&", "§"));
				players.put(damager.getName(), new BukkitRunnable(){
					@Override
					public void run(){
						players.remove(damager.getName());
						damager.sendMessage(getConfig().getString("Saiu_PvP").replace("&", "§"));
					}
				}.runTaskLater(this, getConfig().getInt("Tempo_Sair")*20));
				if(damager.getActivePotionEffects().size() > 0){
					damager.removePotionEffect(PotionEffectType.INVISIBILITY);
				}
				if(damager.getAllowFlight() == true || damager.isFlying()){
					damager.setAllowFlight(false);
					damager.setFlying(false);
				}
			}
			if(!players.containsKey(p.getName())){
				p.sendMessage(getConfig().getString("Entrou_PvP").replace("@player", damager.getName()).replace("&", "§"));
				players.put(p.getName(), new BukkitRunnable(){
					@Override
					public void run(){
						players.remove(p.getName());
						p.sendMessage(getConfig().getString("Saiu_PvP").replace("&", "§"));
					}
				}.runTaskLater(this, getConfig().getInt("Tempo_Sair")*20));
				if(p.getActivePotionEffects().size() > 0){
					p.removePotionEffect(PotionEffectType.INVISIBILITY);
				}
				if(p.getAllowFlight() == true){
					p.setAllowFlight(false);
					p.setFlying(false);
				}
				if(players.get(damager.getName()) != null){
					players.get(damager.getName()).cancel();
					players.remove(damager.getName());
				}
			}
			if(players.containsKey(damager.getName())){
				players.get(damager.getName()).cancel();
				players.put(damager.getName(), new BukkitRunnable(){
					@Override
					public void run(){
						players.remove(damager.getName());
						damager.sendMessage(getConfig().getString("Saiu_PvP").replace("&", "§"));
					}
				}.runTaskLater(this, getConfig().getInt("Tempo_Sair")*20));
			}
			if(players.containsKey(p.getName())){
				players.get(p.getName()).cancel();
				players.put(p.getName(), new BukkitRunnable(){
					@Override
					public void run(){
						players.remove(p.getName());
						p.sendMessage(getConfig().getString("Saiu_PvP").replace("&", "§"));
					}
				}.runTaskLater(this, getConfig().getInt("Tempo_Sair")*20));
			}
		}else if(e.getEntity() instanceof Player && e.getDamager() instanceof Projectile){
			final Player p = (Player) e.getEntity();
			Projectile projectile = (Projectile) e.getDamager();
			if(projectile.getShooter() instanceof Player){
				final Player damager = (Player) projectile.getShooter();
				if(p.getName().equals(damager.getName())) return;
				if(!players.containsKey(damager.getName())){
					damager.sendMessage(getConfig().getString("Entrou_PvP").replace("@player", p.getName()).replace("&", "§"));
					players.put(damager.getName(), new BukkitRunnable(){
						@Override
						public void run(){
							players.remove(damager.getName());
							damager.sendMessage(getConfig().getString("Saiu_PvP").replace("&", "§"));
						}
					}.runTaskLater(this, getConfig().getInt("Tempo_Sair")*20));
					if(damager.getActivePotionEffects().size() > 0){
						damager.removePotionEffect(PotionEffectType.INVISIBILITY);
					}
					if(damager.getAllowFlight() == true || damager.isFlying()){
						damager.setAllowFlight(false);
						damager.setFlying(false);
					}
				}
				if(!players.containsKey(p.getName())){
					p.sendMessage(getConfig().getString("Entrou_PvP").replace("@player", damager.getName()).replace("&", "§"));
					players.put(p.getName(), new BukkitRunnable(){
						@Override
						public void run(){
							players.remove(p.getName());
							p.sendMessage(getConfig().getString("Saiu_PvP").replace("&", "§"));
						}
					}.runTaskLater(this, getConfig().getInt("Tempo_Sair")*20));
					if(p.getActivePotionEffects().size() > 0){
						p.removePotionEffect(PotionEffectType.INVISIBILITY);
					}
					if(p.getAllowFlight() == true){
						p.setAllowFlight(false);
						p.setFlying(false);
					}
					if(players.get(damager.getName()) != null){
						players.get(damager.getName()).cancel();
						players.remove(damager.getName());
					}
				}
				if(players.containsKey(damager.getName())){
					players.get(damager.getName()).cancel();
					players.put(damager.getName(), new BukkitRunnable(){
						@Override
						public void run(){
							players.remove(damager.getName());
							damager.sendMessage(getConfig().getString("Saiu_PvP").replace("&", "§"));
						}
					}.runTaskLater(this, getConfig().getInt("Tempo_Sair")*20));
				}
				if(players.containsKey(p.getName())){
					players.get(p.getName()).cancel();
					players.put(p.getName(), new BukkitRunnable(){
						@Override
						public void run(){
							players.remove(p.getName());
							p.sendMessage(getConfig().getString("Saiu_PvP").replace("&", "§"));
						}
					}.runTaskLater(this, getConfig().getInt("Tempo_Sair")*20));
				}
			}
		}
	}

}

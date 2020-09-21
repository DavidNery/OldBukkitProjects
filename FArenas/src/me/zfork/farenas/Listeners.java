package me.zfork.farenas;

import java.util.Random;

import me.zfork.farenas.arena.Arena;
import me.zfork.farenas.arena.ArenaManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.help.HelpTopic;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Listeners implements Listener {
	
	private FArenas instance = FArenas.getFArenas();
	private ArenaManager am = instance.getArenaManager();
	private final Random r = new Random();
	
	@SuppressWarnings("static-access")
	@EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void EntrarArenas(PlayerCommandPreprocessEvent e){
		Player p = e.getPlayer();
		String msg = e.getMessage().split(" ")[0];
		Arena arena = am.getArenaByPlayer(p);
    	HelpTopic topic = Bukkit.getServer().getHelpMap().getHelpTopic(msg);
    	if(topic == null){
    		if(am.getArenaByPlayer(p) == null){
    			if(am.hasArena(msg.substring(1, msg.length()))){
    				Arena arena2 = am.getArenaByName(msg.substring(1, msg.length()));
    				if(arena2.getFC().getBoolean("Ativar_Reducao_Comando") == false) return;
    				e.setCancelled(true);
    				if(am.hasFlag(arena2.getNome(), "manutencao") && am.getFlagValue(arena2.getNome(), "manutencao").equalsIgnoreCase("true")){
    					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Em_Manutencao").replace("&", "§").replace("@arena2", arena2.getNome()));
    					return;
    				}else if(am.hasFlag(arena2.getNome(), "maxplayers") && Integer.parseInt(am.getFlagValue(arena2.getNome(), "maxplayers")) != 0 && arena2.getPlayers().size() == Integer.parseInt(am.getFlagValue(arena2.getNome(), "maxplayers"))){
    					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Lotada").replace("&", "§").replace("@arena2", arena2.getNome()));
    					return;
    				}else if(am.hasFlag(arena2.getNome(), "clearedinventory") && am.getFlagValue(arena2.getNome(), "clearedinventory").equalsIgnoreCase("true") && invVazio(p)){
    					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inventario_Vazio").replace("&", "§").replace("@arena2", arena2.getNome()));
    					return;
    				}else if(arena2.getFC().getBoolean("Ativar_Permissao") && (!p.hasPermission(arena2.getFC().getString("Permissao")))){
    					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao_Arena").replace("&", "§").replace("@arena2", arena2.getNome()));
    					return;
    				}
    				Location spawn = arena2.getSpawns().get(r.nextInt(arena2.getSpawns().size()));
    				if(spawn.getWorld() == null){
    					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Mundo_Nao_Existe").replace("&", "§"));
    					return;
    				}
    				p.playSound(p.getLocation(), Sound.LEVEL_UP, 5.0F, 1.0F);
    				p.teleport(spawn);
    				arena2.getPlayers().add(p.getName().toLowerCase());
    				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Entrou_Arena").replace("&", "§").replace("@arena", arena2.getNome()));
    				if(arena2.getFC().getBoolean("Ativar_BC")){
    					for(Player on : instance.getServer().getOnlinePlayers()){
    						if(on != null) on.sendMessage(instance.getConfig().getString("Mensagem.Entrou_Arena").replace("&", "§").replace("@player", p.getName()).replace("@arena", arena2.getNome()));
    					}
    				}
    				if(arena2.getItens() != null && arena2.getItens().size() != 0){
    					for(ItemStack items : arena2.getItens()){
    						if(p.getInventory().firstEmpty() != -1){
    							p.getInventory().setItem(p.getInventory().firstEmpty(), items);
    						}
    					}
    				}
    				if(arena2.getArmor() != null && arena2.getArmor().length != 0){
    					if(arena2.getArmor()[0] != null && p.getInventory().getHelmet() == null){
    						p.getInventory().setHelmet(arena2.getArmor()[0]);
    					}
    					if(arena2.getArmor()[1] != null && p.getInventory().getChestplate() == null){
    						p.getInventory().setChestplate(arena2.getArmor()[1]);
    					}
    					if(arena2.getArmor()[2] != null && p.getInventory().getLeggings() == null){
    						p.getInventory().setLeggings(arena2.getArmor()[2]);
    					}
    					if(arena2.getArmor()[3] != null && p.getInventory().getBoots() == null){
    						p.getInventory().setBoots(arena2.getArmor()[3]);
    					}
    				}
    			}
    		}else{
    			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta").replace("&", "§"));
    			e.setCancelled(true);
    		}
    	}else{
    		if(arena != null){
				for(String cmd : arena.getFC().getStringList("Comandos_Liberados")){
					if(cmd.toLowerCase().startsWith(msg.toLowerCase())) return;
				}
				e.setCancelled(true);
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Comando_Bloqueado").replace("&", "§"));
			}
    	}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void Quit(PlayerQuitEvent e){
		final Player p = e.getPlayer();
		final Arena arena = am.getArenaByPlayer(p);
		if(arena != null){
			if(am.hasFlag(arena.getNome(), "dckill") && am.getFlagValue(arena.getNome(), "dckill").equalsIgnoreCase("true") && p != null && (!p.isDead())){
				p.setHealth(0);
			}
			arena.getPlayers().remove(p.getName().toLowerCase());
			p.teleport(arena.getExit());
			if(am.hasFlag(arena.getNome(), "clearinventory") && am.getFlagValue(arena.getNome(), "clearinventory").equalsIgnoreCase("true")){
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void Quit(PlayerKickEvent e){
		final Player p = e.getPlayer();
		final Arena arena = am.getArenaByPlayer(p);
		if(arena != null){
			arena.getPlayers().remove(p.getName().toLowerCase());
			p.teleport(arena.getExit());
			if(am.hasFlag(arena.getNome(), "clearinventory") && am.getFlagValue(arena.getNome(), "clearinventory").equalsIgnoreCase("true")){
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void Quit(PlayerDeathEvent e){
		final Player p = e.getEntity();
		Arena arena = am.getArenaByPlayer(p);
		if(arena != null){
			Player killer = null;
			if(p.getKiller() instanceof Player) killer = (Player) p.getKiller();
			if(killer != null && (!p.getName().equalsIgnoreCase(killer.getName()))){
				if(am.hasFlag(arena.getNome(), "moneyonkill")){
					instance.getEcon().depositPlayer(killer.getName(), Double.parseDouble(am.getFlagValue(arena.getNome(), "moneyonkill")));
				}
				if(am.hasFlag(arena.getNome(), "healonkill") && am.getFlagValue(arena.getNome(), "healonkill").equalsIgnoreCase("true")){
					killer.setHealth(20);
				}
			}
			if(am.hasFlag(arena.getNome(), "keepinventory") && am.getFlagValue(arena.getNome(), "keepinventory").equalsIgnoreCase("true")){
				final ItemStack[] inv = p.getInventory().getContents();
				final ItemStack[] armor = p.getInventory().getArmorContents();
				final float xp = p.getExp();
				e.getDrops().clear();
				new BukkitRunnable(){
					@Override
					public void run(){
						p.getInventory().setContents(inv);
						p.getInventory().setArmorContents(armor);
						p.setExp(xp);
					}
				}.runTaskLater(instance, 1L);
			}
			if(am.hasFlag(arena.getNome(), "dropinventory") && am.getFlagValue(arena.getNome(), "dropinventory").equalsIgnoreCase("false")){
				e.getDrops().clear();
			}
			p.sendMessage(instance.getConfig().getString("Mensagem.Morreu").replace("@arena", arena.getNome()).replace("&", "§"));
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Respawn(PlayerRespawnEvent e){
		final Player p = e.getPlayer();
		final Arena arena = am.getArenaByPlayer(p);
		if(arena != null){
			new BukkitRunnable() {
				@Override
				public void run() {
					arena.getPlayers().remove(p.getName().toLowerCase());
					p.teleport(arena.getExit());
				}
			}.runTaskLater(instance, 3L);
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void Drop(PlayerDropItemEvent e){
		Player p = e.getPlayer();
		Arena arena = am.getArenaByPlayer(p);
		if(arena != null && am.hasFlag(arena.getNome(), "nodrop") && am.getFlagValue(arena.getNome(), "nodrop").equalsIgnoreCase("true")){
			e.setCancelled(true);
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void TP(PlayerTeleportEvent e){
		Player p = e.getPlayer();
		Arena arena = am.getArenaByPlayer(p);
		if(arena != null){
			e.setCancelled(true);
		}
	}
	
	public boolean invVazio(Player p){
		for(ItemStack item : p.getInventory().getContents()){
			if(item != null && item.getType() != Material.AIR) return true;
		}
		for(ItemStack item : p.getInventory().getArmorContents()){
			if(item != null && item.getType() != Material.AIR) return true;
		}
		return false;
	}

}

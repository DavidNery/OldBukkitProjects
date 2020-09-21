package me.zfork.fx1;

import java.sql.SQLException;
import java.text.NumberFormat;

import me.zfork.fx1.arenas.Arena;
import me.zfork.fx1.arenas.ArenaManager;
import me.zfork.fx1.x1.X1;
import me.zfork.fx1.x1.X1Manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Eventos implements Listener{
	
	private FX1 instance = FX1.getFX1();
	private X1Manager x1manager = instance.getX1Manager();
	private ArenaManager arenamanager = instance.getArenaManager();
	
	@EventHandler
	public void Inventory(InventoryClickEvent e){
		if(e.getInventory().getTitle().replace("&", "§").equalsIgnoreCase(instance.getConfig().getString("Config.Comando_Arenas.Inv_Title").replace("&", "§"))){
			e.setCancelled(true);
			for(Arena arena : arenamanager.getArena()){
				ItemStack itemlivre = (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Comando_Arenas.Item_Arena_Livre")
						.replace("{arena}", arena.getNome()));
				if(itemlivre.isSimilar(e.getCurrentItem())){
					e.getWhoClicked().teleport(arena.getLocationCamarote());
					return;
				}
			}
			for(Arena arena : arenamanager.getArena()){
				for(X1 x1 : instance.getX1Manager().getX1s()){
					if(x1.getArena().getNome().equals(arena.getNome())){
						System.out.println("2");
						ItemStack itemocupada = (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Comando_Arenas.Item_Arena_Ocupada")
								.replace("{player1}", x1.getPlayer1().getName()).replace("{player2}", x1.getPlayer2().getName())
								.replace("{arena}", arena.getNome()));
						if(itemocupada.isSimilar(e.getCurrentItem())){
							e.getWhoClicked().teleport(arena.getLocationCamarote());
							return;
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void Inventory(InventoryMoveItemEvent e){
		if(e.getInitiator().getTitle().replace("&", "§").equalsIgnoreCase(instance.getConfig().getString("Config.Comando_Arenas.Inv_Title").replace("&", "§"))
				|| e.getDestination().getTitle().replace("&", "§").equalsIgnoreCase(instance.getConfig().getString("Config.Comando_Arenas.Inv_Title").replace("&", "§"))){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Inventory(InventoryPickupItemEvent e){
		if(e.getInventory().getTitle().replace("&", "§").equalsIgnoreCase(instance.getConfig().getString("Config.Comando_Arenas.Inv_Title").replace("&", "§"))){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Move(PlayerMoveEvent e){
		Player p = e.getPlayer();
		if(e.getFrom().getX() != e.getTo().getX() && e.getFrom().getZ() != e.getTo().getZ()){
			X1 x1 = x1manager.getX1ByPlayer(p);
			if(x1 != null && x1.getTempo() > 0 && x1.getTempo() <= instance.getConfig().getInt("Config.Tempo_Iniciar_X1")){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void Respawn(PlayerRespawnEvent e){
		final Player p = e.getPlayer();
		new BukkitRunnable() {
			@Override
			public void run() {
				for(String player : instance.getRespawn().keySet()){
					if(player.equalsIgnoreCase(p.getName())){
						p.teleport(arenamanager.getLocationByString(instance.getRespawn().get(player)));
						instance.getRespawn().remove(player);
					}
				}
			}
		}.runTaskLater(instance, 5L);
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent e){
		final X1 x1 = x1manager.getX1ByPlayer(e.getPlayer());
		if(x1 != null){
			if(x1.getTempo() > 0 && x1.getTempo() <= instance.getConfig().getInt("Config.Tempo_Iniciar_X1")){
				for(String msg : instance.getConfig().getStringList("Mensagem.BC.X1_Cancelado")){
					for(Player on : instance.getServer().getOnlinePlayers()){
						on.sendMessage(msg.replace("&", "§").replace("{player1}", x1.getPlayer1().getName()).replace("{player2}", x1.getPlayer2().getName()));
					}
				}
				x1.getPlayer1().teleport(x1.getArena().getLocationSaida());
				x1.getPlayer2().teleport(x1.getArena().getLocationSaida());
				x1.getArena().setUsando(false);
				x1manager.stopX1(x1, null);
			}else if(x1.getAcontecendo()){
				final Player vencedor = (x1.getPlayer1().getName().equalsIgnoreCase(e.getPlayer().getName()) ? x1.getPlayer2() : x1.getPlayer1());
				final Player perdedor = e.getPlayer();
				for(String msg : instance.getConfig().getStringList("Mensagem.BC.Ganhou")){
					for(Player on : instance.getServer().getOnlinePlayers()){
						on.sendMessage(msg.replace("&", "§").replace("{vencedor}", vencedor.getName()).replace("{perdedor}", perdedor.getName())
								.replace("{aposta}", NumberFormat.getCurrencyInstance().format(x1.getAposta()).replaceAll("[^\\d\\.,]+", "")));
					}
				}
				if(instance.getSQLType() == 1){
					new BukkitRunnable() {
						@Override
						public void run() {
							try {
								instance.getMySQL().openConnection();
								instance.getMySQL().addPontoVitoria(vencedor.getName());
								instance.getMySQL().addPontoDerrota(perdedor.getName());
								instance.getMySQL().closeConnection();
							} catch (ClassNotFoundException | SQLException e) {}
						}
					}.runTaskAsynchronously(instance);
				}else{
					new BukkitRunnable() {
						@Override
						public void run() {
							try {
								instance.getSQLite().openConnection();
								instance.getSQLite().addPontoVitoria(vencedor.getName());
								instance.getSQLite().addPontoDerrota(perdedor.getName());
								instance.getSQLite().closeConnection();
							} catch (ClassNotFoundException | SQLException e) {}
						}
					}.runTaskAsynchronously(instance);
				}
				final Location saida = x1.getArena().getLocationSaida();
				final Arena arena = x1.getArena();
				x1manager.stopX1(x1, vencedor);
				if(instance.getConfig().getBoolean("Config.DC_Kill") && (x1.getKit().getProprio() || x1.getKit().getPerde())){
					perdedor.setHealth(0);
					instance.getRespawn().put(perdedor.getName().toLowerCase(), arenamanager.getStringByLocation(x1.getArena().getLocationSaida()));
				}
				perdedor.teleport(x1.getArena().getLocationSaida());
				if(instance.getConfig().getBoolean("Config.Ativar_Fireworks_Vencedor")){
					new BukkitRunnable() {
						int i = 0;
						@Override
						public void run(){
							if(i == instance.getConfig().getInt("Config.Tempo_Pegar_Items")){
								cancel();
								vencedor.teleport(saida);
								arena.setUsando(false);
							}else{
								instance.shootFireWork(vencedor);
								i++;
							}
						}
					}.runTaskTimer(instance, 0, 1*20);
				}else{
					new BukkitRunnable(){
						@Override
						public void run(){
							vencedor.teleport(saida);
							arena.setUsando(false);
						}
					}.runTaskLater(instance, instance.getConfig().getInt("Config.Tempo_Pegar_Items")*20);
				}
			}
		}
	}
	
	@EventHandler
	public void Quit(PlayerKickEvent e){
		final X1 x1 = x1manager.getX1ByPlayer(e.getPlayer());
		if(x1 != null){
			if(x1.getTempo() > 0 && x1.getTempo() <= instance.getConfig().getInt("Config.Tempo_Iniciar_X1")){
				for(String msg : instance.getConfig().getStringList("Mensagem.BC.X1_Cancelado")){
					for(Player on : instance.getServer().getOnlinePlayers()){
						on.sendMessage(msg.replace("&", "§").replace("{player1}", x1.getPlayer1().getName()).replace("{player2}", x1.getPlayer2().getName()));
					}
				}
				x1.getPlayer1().teleport(x1.getArena().getLocationSaida());
				x1.getPlayer2().teleport(x1.getArena().getLocationSaida());
				x1.getArena().setUsando(false);
				x1manager.stopX1(x1, null);
			}else if(x1.getAcontecendo()){
				final Player vencedor = (x1.getPlayer1().getName().equalsIgnoreCase(e.getPlayer().getName()) ? x1.getPlayer2() : x1.getPlayer1());
				final Player perdedor = e.getPlayer();
				for(String msg : instance.getConfig().getStringList("Mensagem.BC.Ganhou")){
					for(Player on : instance.getServer().getOnlinePlayers()){
						on.sendMessage(msg.replace("&", "§").replace("{vencedor}", vencedor.getName()).replace("{perdedor}", perdedor.getName())
								.replace("{aposta}", NumberFormat.getCurrencyInstance().format(x1.getAposta()).replaceAll("[^\\d\\.,]+", "")));
					}
				}
				if(instance.getSQLType() == 1){
					new BukkitRunnable() {
						@Override
						public void run() {
							try {
								instance.getMySQL().openConnection();
								instance.getMySQL().addPontoVitoria(vencedor.getName());
								instance.getMySQL().addPontoDerrota(perdedor.getName());
								instance.getMySQL().closeConnection();
							} catch (ClassNotFoundException | SQLException e) {}
						}
					}.runTaskAsynchronously(instance);
				}else{
					new BukkitRunnable() {
						@Override
						public void run() {
							try {
								instance.getSQLite().openConnection();
								instance.getSQLite().addPontoVitoria(vencedor.getName());
								instance.getSQLite().addPontoDerrota(perdedor.getName());
								instance.getSQLite().closeConnection();
							} catch (ClassNotFoundException | SQLException e) {}
						}
					}.runTaskAsynchronously(instance);
				}
				final Location saida = x1.getArena().getLocationSaida();
				final Arena arena = x1.getArena();
				x1manager.stopX1(x1, vencedor);
				if(instance.getConfig().getBoolean("Config.DC_Kill") && (x1.getKit().getProprio() || x1.getKit().getPerde())){
					perdedor.setHealth(0);
					instance.getRespawn().put(perdedor.getName().toLowerCase(), arenamanager.getStringByLocation(x1.getArena().getLocationSaida()));
				}
				perdedor.teleport(x1.getArena().getLocationSaida());
				if(instance.getConfig().getBoolean("Config.Ativar_Fireworks_Vencedor")){
					new BukkitRunnable() {
						int i = 0;
						@Override
						public void run(){
							if(i == instance.getConfig().getInt("Config.Tempo_Pegar_Items")){
								cancel();
								vencedor.teleport(saida);
								arena.setUsando(false);
							}else{
								instance.shootFireWork(vencedor);
								i++;
							}
						}
					}.runTaskTimer(instance, 0, 1*20);
				}else{
					new BukkitRunnable(){
						@Override
						public void run(){
							vencedor.teleport(saida);
							arena.setUsando(false);
						}
					}.runTaskLater(instance, instance.getConfig().getInt("Config.Tempo_Pegar_Items")*20);
				}
			}
		}
	}
	
	@EventHandler
	public void Quit(PlayerDeathEvent e){
		final X1 x1 = x1manager.getX1ByPlayer(e.getEntity());
		if(x1 != null){
			if(x1.getTempo() > 0 && x1.getTempo() <= instance.getConfig().getInt("Config.Tempo_Iniciar_X1")){
				for(String msg : instance.getConfig().getStringList("Mensagem.BC.X1_Cancelado")){
					for(Player on : instance.getServer().getOnlinePlayers()){
						on.sendMessage(msg.replace("&", "§").replace("{player1}", x1.getPlayer1().getName()).replace("{player2}", x1.getPlayer2().getName()));
					}
				}
				if((x1.getKit().getProprio() && !x1.getKit().getPerde()) || !x1.getKit().getProprio()){
					e.getDrops().clear();
				}
				x1.getPlayer1().teleport(x1.getArena().getLocationSaida());
				x1.getPlayer2().teleport(x1.getArena().getLocationSaida());
				x1.getArena().setUsando(false);
				x1manager.stopX1(x1, null);
			}else if(x1.getAcontecendo()){
				final Player p = (x1.getPlayer1().isDead() ? x1.getPlayer2() : x1.getPlayer1());
				final Player perdedor = (x1.getPlayer1().isDead() ? x1.getPlayer1() : x1.getPlayer2());
				if((x1.getKit().getProprio() && !x1.getKit().getPerde()) || !x1.getKit().getProprio()){
					e.getDrops().clear();
				}
				for(String msg : instance.getConfig().getStringList("Mensagem.BC.Ganhou")){
					for(Player on : instance.getServer().getOnlinePlayers()){
						on.sendMessage(msg.replace("&", "§").replace("{vencedor}", p.getName()).replace("{perdedor}", perdedor.getName())
								.replace("{aposta}", NumberFormat.getCurrencyInstance().format(x1.getAposta()).replaceAll("[^\\d\\.,]+", "")));
					}
				}
				if(instance.getSQLType() == 1){
					new BukkitRunnable() {
						@Override
						public void run() {
							try {
								instance.getMySQL().openConnection();
								instance.getMySQL().addPontoVitoria(p.getName());
								instance.getMySQL().addPontoDerrota(perdedor.getName());
								instance.getMySQL().closeConnection();
							} catch (ClassNotFoundException | SQLException e) {}
						}
					}.runTaskAsynchronously(instance);
				}else{
					new BukkitRunnable() {
						@Override
						public void run() {
							try {
								instance.getSQLite().openConnection();
								instance.getSQLite().addPontoVitoria(p.getName());
								instance.getSQLite().addPontoDerrota(perdedor.getName());
								instance.getSQLite().closeConnection();
							} catch (ClassNotFoundException | SQLException e) {}
						}
					}.runTaskAsynchronously(instance);
				}
				instance.getRespawn().put(perdedor.getName().toLowerCase(), arenamanager.getStringByLocation(x1.getArena().getLocationSaida()));
				final Location saida = x1.getArena().getLocationSaida();
				final Arena arena = x1.getArena();
				x1manager.stopX1(x1, p);
				if(instance.getConfig().getBoolean("Config.Ativar_Fireworks_Vencedor")){
					new BukkitRunnable() {
						int i = 0;
						@Override
						public void run(){
							if(i == instance.getConfig().getInt("Config.Tempo_Pegar_Items")){
								cancel();
								p.teleport(saida);
								arena.setUsando(false);
							}else{
								instance.shootFireWork(p);
								i++;
							}
						}
					}.runTaskTimer(instance, 0, 1*20);
				}else{
					new BukkitRunnable(){
						@Override
						public void run(){
							p.teleport(saida);
							arena.setUsando(false);
						}
					}.runTaskLater(instance, instance.getConfig().getInt("Config.Tempo_Pegar_Items")*20);
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void Cmd(PlayerCommandPreprocessEvent e){
		Player p = e.getPlayer();
		X1 x1 = x1manager.getX1ByPlayer(p);
		if(x1 != null && x1.getAcontecendo()){
			for(String cmd : instance.getConfig().getStringList("Config.Comandos_Liberados"))
				if(cmd.toLowerCase().startsWith(e.getMessage().toLowerCase()) || cmd.equalsIgnoreCase(e.getMessage()))
					return;
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Comando_Bloqueado_Durante_X1").replace("&", "§"));
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void TP(PlayerTeleportEvent e){
		Player p = e.getPlayer();
		X1 x1 = x1manager.getX1ByPlayer(p);
		if(x1 != null && x1.getAcontecendo()){
			for(String loc : x1.getArena().getFC().getConfigurationSection("Arena").getKeys(false)){
				if(x1.getArena().getFC().getString("Arena." + loc).equalsIgnoreCase(arenamanager.getStringByLocation(e.getTo()))) return;
			}
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Drop(PlayerDropItemEvent e){
		X1 x1 = x1manager.getX1ByPlayer(e.getPlayer());
		if(x1 != null && x1.getAcontecendo() && (x1.getKit().getProprio() == false || x1.getKit().getPerde() == false)){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Shoot(EntityShootBowEvent e){
		if(e.getEntity() instanceof Player){
			X1 x1 = x1manager.getX1ByPlayer((Player) e.getEntity());
			if(x1 != null && x1.getTempo() != 0) e.setCancelled(true);
		}
	}

}

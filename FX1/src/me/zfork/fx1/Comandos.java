/**
OLD

package me.zfork.fx1;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.zfork.fx1.arenas.Arena;
import me.zfork.fx1.arenas.ArenaManager;
import me.zfork.fx1.kits.Kit;
import me.zfork.fx1.kits.KitManager;
import me.zfork.fx1.x1.X1;
import me.zfork.fx1.x1.X1Manager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Comandos implements Listener {

	private FX1 instance = FX1.getFX1();
	private ArenaManager arenamanager = instance.getArenaManager();
	private KitManager kitmanager = instance.getKitManager();
	private X1Manager x1manager = instance.getX1Manager();
	private Map<String, String> desafios = new HashMap<String, String>();
	private Map<String, Long> rank = new HashMap<String, Long>();

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void Cmd(PlayerCommandPreprocessEvent e){
		if(e.isCancelled()) return;
		final Player p = e.getPlayer();
		final String[] args = e.getMessage().split(" ");
		String cmd = args[0];
		if(instance.getServer().getHelpMap().getHelpTopic(cmd) != null) return;
		for(String comandos : instance.getConfig().getStringList("Config.Comandos.Principal")){
			if(cmd.equalsIgnoreCase(comandos)){
				e.setCancelled(true);
				if(args.length == 1){
					for(String msg : instance.getConfig().getStringList("Mensagem.Comandos")){
						p.sendMessage(msg.replace("&", "§").replace("{cmd}", cmd));
					}
					return;
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Desafiar"))){
					if(args.length == 2){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Desafiar").replace("&", "§").replace("{cmd}", cmd));
						return;
					}
					Arena arena = arenamanager.getArenaLivre();
					Kit kit = kitmanager.getDefaultKit();
					double aposta = 0;
					final Player player = instance.getServer().getPlayer(args[2]);
					if(player == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§").replace("{player}", args[2]));
						return;
					}else if(p.getName().equalsIgnoreCase(player.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Pode_Se_Desafiar").replace("&", "§"));
						return;
					}else if(x1manager.hasPlayerInX1(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Ja_Esta_Em_X1").replace("&", "§").replace("{player}", player.getName()));
						return;
					}else if(desafios.containsKey(player.getName()) && desafios.get(player.getName()).equalsIgnoreCase(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Desafiado").replace("&", "§").replace("{player}", player.getName()));
						return;
					}
					if(args.length >= 4){
						if(!isNum(args[3])){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Informe_Apenas_Numeros").replace("&", "§"));
							return;
						}
						aposta = Double.parseDouble(args[3]);
						if(aposta < instance.getConfig().getDouble("Config.Aposta_Minima") || aposta > instance.getConfig().getDouble("Config.Aposta_Maxima")){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aposta_Ideal").replace("&", "§"));
							return;
						}else if(aposta % 2 != 0){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aposta_Par").replace("&", "§"));
							return;
						}
					}
					if(args.length >= 5){
						if(arenamanager.getArena(args[4]) == null){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Informada_Nao_Existe").replace("&", "§").replace("{arena}", args[4]));
							return;
						}else{
							arena = arenamanager.getArena(args[4]);
						}
					}
					if(arena == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Nao_Existe").replace("&", "§"));
						return;
					}else if(arena.getUsando()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Ocupada").replace("&", "§").replace("{arena}", arena.getNome()));
						return;
					}
					if(args.length >= 6){
						if(kitmanager.getKit(args[5]) == null){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Kit_Padrao_Nao_Definido").replace("&", "§").replace("{kit}", args[5]));
							return;
						}else{
							kit = kitmanager.getKit(args[5]);
						}
					}
					if(kit == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Kit_Nao_Existe").replace("&", "§"));
						return;
					}else if(desafios.containsKey(p.getName()) && desafios.get(p.getName()).equalsIgnoreCase(player.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Desafiou").replace("&", "§").replace("{player}", player.getName()));
						return;
					}else if(x1manager.getDelay().containsKey(p.getName()) && x1manager.getDelay().get(p.getName()) > System.currentTimeMillis()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde_Desafiar").replace("&", "§")
								.replace("{tempo}", instance.getTime(x1manager.getDelay().get(p.getName()) - System.currentTimeMillis())));
						return;
					}else if(instance.getEcon().getBalance(p.getName()) < aposta/2 || instance.getEcon().getBalance(player.getName()) < aposta/2){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Dinheiro").replace("&", "§"));
						return;
					}else if(!instance.getEcon().has(p.getName(), kit.getCusto())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Dinheiro_Kit").replace("&", "§"));
						return;
					}
					X1 playerx1 = x1manager.getX1ByPlayer(p);
					if(playerx1 != null){
						if(playerx1.getAcontecendo()){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta_Em_X1").replace("&", "§"));
							return;
						}
						playerx1.getTask().cancel();
						playerx1.setTask(null);
						x1manager.removeX1(playerx1);
					}
					if(instance.getConfig().getBoolean("Config.Avisar_Desafio_A_Todos")){
						for(String msg : instance.getConfig().getStringList("Mensagem.BC.Desafio")){
							for(Player on : instance.getServer().getOnlinePlayers()){
								if(!on.getName().equalsIgnoreCase(player.getName())){
									on.sendMessage(msg.replace("&", "§").replace("{player1}", p.getName()).replace("{player2}", player.getName()).replace("{kit}", kit.getNome())
											.replace("{cmd}", cmd)
											.replace("{arena}", arena.getNome()).replace("{aposta}", NumberFormat.getCurrencyInstance().format(aposta).replaceAll("[^\\d\\.,]+", "")));
								}
							}
						}
					}else{
						for(String msg : instance.getConfig().getStringList("Mensagem.BC.Desafio")){
							p.sendMessage(msg.replace("&", "§").replace("{player1}", p.getName()).replace("{player2}", player.getName()).replace("{kit}", kit.getNome())
									.replace("{cmd}", cmd)
									.replace("{arena}", arena.getNome()).replace("{aposta}", NumberFormat.getCurrencyInstance().format(aposta).replaceAll("[^\\d\\.,]+", "")));
						}
					}
					for(String msg : instance.getConfig().getStringList("Mensagem.Sucesso.Desafio")){
						player.sendMessage(msg.replace("&", "§").replace("{player}", p.getName()).replace("{kit}", kit.getNome())
										.replace("{cmd}", cmd)
										.replace("{arena}", arena.getNome()).replace("{aposta}", NumberFormat.getCurrencyInstance().format(aposta).replaceAll("[^\\d\\.,]+", "")));
					}
					desafios.put(p.getName(), player.getName());
					final X1 x1 = new X1(p, player, arena, kit);
					x1.setAposta(aposta);
					x1.setTask(new BukkitRunnable() {
						@Override
						public void run() {
							desafios.remove(p.getName(), player.getName());
							for(String msg : instance.getConfig().getStringList("Mensagem.BC.Desafio_Expirou")){
								for(Player on : instance.getServer().getOnlinePlayers()){
									on.sendMessage(msg.replace("&", "§").replace("{player1}", p.getName()).replace("{player2}", player.getName()));
								}
							}
							x1manager.removeX1(x1);
						}
					}.runTaskLater(instance, instance.getConfig().getInt("Config.Tempo_Aceitar")*20));
					x1manager.addX1(x1);
					x1manager.getDelay().put(p.getName(), System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.Tempo_Desafiar")));
					return;
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Aceitar"))){
					if(args.length == 2){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Aceitar").replace("&", "§").replace("{cmd}", cmd));
						return;
					}
					final Player player = instance.getServer().getPlayer(args[2]);
					if(player == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§").replace("{player}", args[2]));
						return;
					}else if(!(desafios.containsKey(player.getName()) && desafios.get(player.getName()).equalsIgnoreCase(p.getName()))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Desafiou").replace("&", "§").replace("{player}", player.getName()));
						return;
					}
					final X1 x1 = x1manager.getX1ByPlayerAndPlayer(p, player);
					if(x1 == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Desafiou").replace("&", "§").replace("{player}", player.getName()));
						return;
					}else if(instance.getEcon().getBalance(p.getName()) < x1.getAposta()/2 || instance.getEcon().getBalance(player.getName()) < x1.getAposta()/2){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Dinheiro").replace("&", "§"));
						return;
					}else if(x1.getArena().getUsando()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Ocupada").replace("&", "§").replace("{arena}", x1.getArena().getNome()));
						return;
					}else if(player.isDead()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Morto").replace("&", "§"));
						return;
					}else if(!instance.getEcon().has(x1.getPlayer1().getName(), x1.getKit().getCusto())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Dinheiro_Kit_Outro").replace("&", "§"));
						return;
					}
					X1 x11 = x1manager.getX1ByPlayer(player);
					if(x11 != null){
						if(x11.getAcontecendo()){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Ja_Esta_Em_X1").replace("&", "§"));
							return;
						}
					}
					for(X1 x1s : x1manager.getX1s()){
						if(x1s.getPlayer1().getName().equalsIgnoreCase(player.getName()) && !x1s.getPlayer2().getName().equalsIgnoreCase(p.getName())){
							desafios.remove(x1.getPlayer1().getName(), x1.getPlayer2().getName());
							x1s.getTask().cancel();
							x1s.setTask(null);
							x1s.setPlayer1(null);
							x1s.setPlayer2(null);
							x1manager.removeX1(x1s);
						}
					}
					desafios.remove(player.getName(), p.getName());
					for(String msg : instance.getConfig().getStringList("Mensagem.BC.Aceitou_Desafio")){
						for(Player on : instance.getServer().getOnlinePlayers()){
							on.sendMessage(msg.replace("&", "§").replace("{player1}", p.getName()).replace("{player2}", player.getName())
									.replace("{arena}", x1.getArena().getNome()).replace("{kit}", x1.getKit().getNome())
									.replace("{aposta}", NumberFormat.getCurrencyInstance().format(x1.getAposta()).replaceAll("[^\\d\\.,]+", "")));
						}
					}
					if((x1.getKit().getProprio() && !x1.getKit().getPerde()) || !x1.getKit().getProprio()){
						instance.getInventario().put(x1.getPlayer1().getName(), x1.getPlayer1().getInventory().getContents());
						instance.getArmor().put(x1.getPlayer1().getName(), x1.getPlayer1().getInventory().getArmorContents());
						instance.getInventario().put(x1.getPlayer2().getName(), x1.getPlayer2().getInventory().getContents());
						instance.getArmor().put(x1.getPlayer2().getName(), x1.getPlayer2().getInventory().getArmorContents());
					}
					if(!x1.getKit().getProprio()){
						x1.getPlayer1().getInventory().clear();
						x1.getPlayer1().getInventory().setArmorContents(null);
						x1.getPlayer2().getInventory().clear();
						x1.getPlayer2().getInventory().setArmorContents(null);
						for(ItemStack item : x1.getKit().getItems()){
							x1.getPlayer1().getInventory().addItem(item);
							x1.getPlayer2().getInventory().addItem(item);
						}
						x1.getPlayer1().getInventory().setArmorContents(x1.getKit().getArmadura());
						x1.getPlayer2().getInventory().setArmorContents(x1.getKit().getArmadura());
					}
					if(x1manager.getDelay().containsKey(x1.getPlayer1().getName())) x1manager.getDelay().remove(x1manager.getDelay().get(x1.getPlayer1().getName()));
					if(x1manager.getDelay().containsKey(x1.getPlayer2().getName())) x1manager.getDelay().remove(x1manager.getDelay().get(x1.getPlayer2().getName()));
					instance.getEcon().withdrawPlayer(p.getName(), x1.getAposta()/2);
					instance.getEcon().withdrawPlayer(player.getName(), (x1.getAposta()/2)+x1.getKit().getCusto());
					if(x1.getTask() != null) x1.getTask().cancel();
					if(x11.getTask() != null) x11.getTask().cancel();
					x1.setTempo(instance.getConfig().getInt("Config.Tempo_Iniciar_X1"));
					x1.getPlayer1().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, x1.getTempo()*20, 9));
					x1.getPlayer2().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, x1.getTempo()*20, 9));
					x1.getPlayer1().teleport(x1.getArena().getLocationPlayer1());
					x1.getPlayer2().teleport(x1.getArena().getLocationPlayer2());
					if(x1.getPlayer1().getAllowFlight()) x1.getPlayer1().setAllowFlight(false);
					if(x1.getPlayer2().getAllowFlight()) x1.getPlayer2().setAllowFlight(false);
					x1.setAcontecendo(true);
					x1.getArena().setUsando(true);
					if(x1.getTask() != null) x1.getTask().cancel();
					x1.setTask(new BukkitRunnable() {
						@Override
						public void run() {
							if(x1.getTempo() > 0){
								x1.getPlayer1().sendMessage(instance.getConfig().getString("Mensagem.Sucesso.X1_Iniciando").replace("&", "§").replace("{tempo}", x1.getTempo() + ""));
								x1.getPlayer2().sendMessage(instance.getConfig().getString("Mensagem.Sucesso.X1_Iniciando").replace("&", "§").replace("{tempo}", x1.getTempo() + ""));
								x1.setTempo(x1.getTempo()-1);
							}else{
								x1.getTask().cancel();
								x1.setTask(null);
								x1manager.startX1(x1);
								for(String msg : instance.getConfig().getStringList("Mensagem.BC.X1_Comecou")){
									for(Player on : instance.getServer().getOnlinePlayers()){
										on.sendMessage(msg.replace("&", "§").replace("{player1}", p.getName()).replace("{player2}", player.getName())
												.replace("{arena}", x1.getArena().getNome()).replace("{kit}", x1.getKit().getNome())
												.replace("{aposta}", NumberFormat.getCurrencyInstance().format(x1.getAposta()).replaceAll("[^\\d\\.,]+", "")));
									}
								}
								x1.getPlayer1().teleport(x1.getArena().getLocationPlayer1());
								x1.getPlayer2().teleport(x1.getArena().getLocationPlayer2());
								if(instance.getConfig().getBoolean("Config.Use_SimpleClans")){
									if(instance.getSC().getClanManager().getClanPlayer(x1.getPlayer1()) != null && instance.getSC().getClanManager().getClanPlayer(x1.getPlayer2()) != null){
										if(instance.getSC().getClanManager().getClanPlayer(x1.getPlayer1()).getTag()
												.equalsIgnoreCase(instance.getSC().getClanManager().getClanPlayer(x1.getPlayer2()).getTag())){
											instance.getSC().getClanManager().getClanPlayer(x1.getPlayer1()).setFriendlyFire(true);
										}
									}
								}
							}
						}
					}.runTaskTimer(instance, 0, 20));
					return;
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Recusar"))){
					if(args.length == 2){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Recusar").replace("&", "§").replace("{cmd}", cmd));
						return;
					}
					Player player = instance.getServer().getPlayer(args[2]);
					if(player == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§").replace("{player}", args[2]));
						return;
					}else if(!(desafios.containsKey(player.getName()) && desafios.get(player.getName()).equalsIgnoreCase(p.getName()))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Desafiou").replace("&", "§").replace("{player}", player.getName()));
						return;
					}
					X1 x1 = x1manager.getX1ByPlayerAndPlayer(p, player);
					if(x1 == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Desafiou").replace("&", "§").replace("{player}", player.getName()));
						return;
					}else if(x1.getAcontecendo()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.X1_Ja_Comecou").replace("&", "§"));
						return;
					}
					for(Player on : instance.getServer().getOnlinePlayers()){
						for(String msg : instance.getConfig().getStringList("Mensagem.BC.Recusou_X1")){
							on.sendMessage(msg.replace("&", "§").replace("{player}", p.getName()).replace("{desafiador}", player.getName()).replace("{kit}", x1.getKit().getNome())
									.replace("{arena}", x1.getArena().getNome())
									.replace("{aposta}", NumberFormat.getCurrencyInstance().format(x1.getAposta()).replaceAll("[^\\d\\.,]+", "")));
						}
					}
					if(x1.getTask() != null){
						x1.getTask().cancel();
						x1.setTask(null);
					}
					desafios.remove(player.getName(), p.getName());
					x1manager.removeX1(x1);
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Camarote"))){
					if(args.length == 2){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Camarote").replace("&", "§"));
						return;
					}
					Arena arena = arenamanager.getArena(args[2]);
					if(arena == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Informada_Nao_Existe").replace("&", "§"));
						return;
					}
					p.teleport(arena.getLocationCamarote());
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.TP_Camarote").replace("&", "§").replace("{arena}", arena.getNome()));
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Rank"))){
					if(args.length == 2){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Rank").replace("&", "§").replace("{cmd}", cmd));
						return;
					}else if(rank.containsKey(p.getName()) && rank.get(p.getName()) > System.currentTimeMillis()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde_Rank").replace("&", "§").replace("{tempo}", instance.getTime(rank.get(p.getName()) - System.currentTimeMillis())));
						return;
					}else if(!instance.getConfig().getBoolean("Config.Rank.Ativar")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Rank_Desativado").replace("&", "§"));
						return;
					}
					if(args[2].equalsIgnoreCase("perdeu")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Fazendo_Consulta").replace("&", "§").replace("{vp}", args[2]));
						if(instance.getSQLType() == 1){
							new BukkitRunnable() {
								@Override
								public void run() {
									try {
										instance.getMySQL().openConnection();
										instance.getMySQL().getTOPDerrotas(p);
										instance.getMySQL().closeConnection();
										p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Consulta_Finalizada").replace("&", "§").replace("{vp}", args[2]));
									} catch (ClassNotFoundException | SQLException e) {}
								}
							}.runTaskAsynchronously(instance);
						}else{
							new BukkitRunnable() {
								@Override
								public void run() {
									try {
										instance.getSQLite().openConnection();
										instance.getSQLite().getTOPDerrotas(p);
										instance.getSQLite().closeConnection();
										p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Consulta_Finalizada").replace("&", "§").replace("{vp}", args[2]));
									} catch (ClassNotFoundException | SQLException e) {}
								}
							}.runTaskAsynchronously(instance);
						}
					}else if(args[2].equalsIgnoreCase("venceu")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Fazendo_Consulta").replace("&", "§").replace("{vp}", args[2]));
						if(instance.getSQLType() == 1){
							new BukkitRunnable() {
								@Override
								public void run() {
									try {
										instance.getMySQL().openConnection();
										instance.getMySQL().getTOPVitorias(p);
										instance.getMySQL().closeConnection();
										p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Consulta_Finalizada").replace("&", "§").replace("{vp}", args[2]));
									} catch (ClassNotFoundException | SQLException e) {}
								}
							}.runTaskAsynchronously(instance);
						}else{
							new BukkitRunnable() {
								@Override
								public void run() {
									try {
										instance.getSQLite().openConnection();
										instance.getSQLite().getTOPVitorias(p);
										instance.getSQLite().closeConnection();
										p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Consulta_Finalizada").replace("&", "§").replace("{vp}", args[2]));
									} catch (ClassNotFoundException | SQLException e) {}
								}
							}.runTaskAsynchronously(instance);
						}
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Rank").replace("&", "§"));
						return;
					}
					rank.put(p.getName(), System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.Tempo_Ver_Rank")));
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Arenas"))){
					String arenas = "";
					for(Arena arena : arenamanager.getArena()){
						arenas += (arena.getUsando() ? ("§c" + arena.getNome()) : ("§a" + arena.getNome())) + ", ";
					}
					if(arenas.equals("")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Arenas").replace("&", "§"));
						return;
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Lista_Arenas").replace("&", "§").replace("{arenas}", arenas.substring(0, arenas.length()-2)));
					return;
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Kits"))){
					String kits = "";
					for(Kit kit : kitmanager.getKits()){
						kits += (kit.getCusto() == 0 ? ("§f" + kit.getNome()) : ("§b" + kit.getNome() + " (" + NumberFormat.getCurrencyInstance().format(kit.getCusto()).replaceAll("[^\\d\\.,]+", "") + ")")) + ", ";
					}
					if(kits.equals("")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Kits").replace("&", "§"));
						return;
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Lista_Kits").replace("&", "§").replace("{kits}", kits.substring(0, kits.length()-2)));
					return;
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.SetLocation"))){
					if(!p.hasPermission("fx1.admin")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return;
					}
					if(args.length <= 3){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.SetLocation").replace("&", "§").replace("{cmd}", cmd));
						return;
					}
					Arena arena = arenamanager.getArena(args[2]);
					if(arena == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Informada_Nao_Existe").replace("&", "§").replace("{arena}", args[2]));
						return;
					}
					if(args[3].equalsIgnoreCase("Player1")){
						arenamanager.setArenaLocation(arena, "Player1", p.getLocation());
					}else if(args[3].equalsIgnoreCase("Player2")){
						arenamanager.setArenaLocation(arena, "Player2", p.getLocation());
					}else if(args[3].equalsIgnoreCase("Saida")){
						arenamanager.setArenaLocation(arena, "Saida", p.getLocation());
					}else if(args[3].equalsIgnoreCase("Camarote")){
						arenamanager.setArenaLocation(arena, "Camarote", p.getLocation());
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.SetLocation").replace("&", "§").replace("{cmd}", cmd));
						return;
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Location").replace("&", "§").replace("{arena}", arena.getNome()).replace("{loc}", args[3]));
					return;
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Cancelar"))){
					if(!p.hasPermission("fx1.admin")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return;
					}else if(args.length == 2){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Cancelar").replace("&", "§").replace("{cmd}", cmd));
						return;
					}
					Player player = instance.getServer().getPlayer(args[2]);
					if(player == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§").replace("{player}", args[2]));
						return;
					}
					X1 x1 = null;
					if(args.length == 3){
						x1 = x1manager.getX1ByPlayer(player);
						if(x1 == null){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Nao_Esta_Em_X1").replace("&", "§").replace("{player}", player.getName()));
							return;
						}
					}else{
						Player player2 = instance.getServer().getPlayer(args[3]);
						if(player2 == null){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§").replace("{player}", args[3]));
							return;
						}
						x1 = x1manager.getX1ByPlayerAndPlayer(player, player2);
						if(x1 == null){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.X1_Nao_Encontrado").replace("&", "§"));
							return;
						}
					}
					for(String msg : instance.getConfig().getStringList("Mensagem.BC.Staff_Cancelou")){
						for(Player on : instance.getServer().getOnlinePlayers()){
							on.sendMessage(msg.replace("&", "§").replace("{staff}", p.getName())
									.replace("{player1}", x1.getPlayer1().getName()).replace("{player2}", x1.getPlayer2().getName()));
						}
					}
					x1.getPlayer1().teleport(x1.getArena().getLocationSaida());
					x1.getPlayer2().teleport(x1.getArena().getLocationSaida());
					x1manager.stopX1(x1, null);
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Reset"))){
					if(!p.hasPermission("fx1.admin")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return;
					}else if(args.length <= 3){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Reset").replace("&", "§").replace("{cmd}", cmd));
						return;
					}
					final Player player = instance.getServer().getPlayer(args[2]);
					if(player == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§").replace("{player}", args[2]));
						return;
					}else if(!(args[3].equalsIgnoreCase("derrotas") || args[3].equalsIgnoreCase("vitorias"))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Vitorias_Ou_Derrotas").replace("&", "§"));
						return;
					}
					if(instance.getSQLType() == 1){
						new BukkitRunnable() {
							@Override
							public void run() {
								try {
									instance.getMySQL().openConnection();
									instance.getMySQL().resetPlayer(player.getName(), args[3]);
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
									instance.getSQLite().resetPlayer(player.getName(), args[3]);
									instance.getSQLite().closeConnection();
								} catch (ClassNotFoundException | SQLException e) {}
							}
						}.runTaskAsynchronously(instance);
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Resetou").replace("&", "§").replace("{tipo}", args[3]).replace("{player}", player.getName()));
				}else{
					for(String msg : instance.getConfig().getStringList("Mensagem.Comandos")){
						p.sendMessage(msg.replace("&", "§").replace("{cmd}", cmd));
					}
				}
				return;
			}
		}
	}

	public boolean isNum(String string){
		try{
			Double.parseDouble(string);
			return true;
		}catch(Exception e){return false;}
	}

}

 **/

package me.zfork.fx1;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.zfork.fx1.arenas.Arena;
import me.zfork.fx1.arenas.ArenaManager;
import me.zfork.fx1.kits.Kit;
import me.zfork.fx1.kits.KitManager;
import me.zfork.fx1.x1.X1;
import me.zfork.fx1.x1.X1Manager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Comandos implements Listener {

	private FX1 instance = FX1.getFX1();
	private ArenaManager arenamanager = instance.getArenaManager();
	private KitManager kitmanager = instance.getKitManager();
	private X1Manager x1manager = instance.getX1Manager();
	private Map<String, String> desafios = new HashMap<String, String>();
	private Map<String, Long> rank = new HashMap<String, Long>();

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void Cmd(PlayerCommandPreprocessEvent e){
		if(e.isCancelled()) return;
		final Player p = e.getPlayer();
		final String[] args = e.getMessage().split(" ");
		String cmd = args[0];
		if(instance.getServer().getHelpMap().getHelpTopic(cmd) != null) return;
		for(String comandos : instance.getConfig().getStringList("Config.Comandos.Principal")){
			if(cmd.equalsIgnoreCase(comandos)){
				e.setCancelled(true);
				if(args.length == 1){
					for(String msg : instance.getConfig().getStringList("Mensagem.Comandos")){
						p.sendMessage(msg.replace("&", "§").replace("{cmd}", cmd));
					}
					return;
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Desafiar"))){
					if(args.length == 2){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Desafiar").replace("&", "§").replace("{cmd}", cmd));
						return;
					}
					Arena arena = arenamanager.getArenaLivre();
					Kit kit = kitmanager.getDefaultKit();
					double aposta = 0;
					final Player player = instance.getServer().getPlayer(args[2]);
					if(player == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§").replace("{player}", args[2]));
						return;
					}else if(p.getName().equalsIgnoreCase(player.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Pode_Se_Desafiar").replace("&", "§"));
						return;
					}else if(x1manager.hasPlayerInX1(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Ja_Esta_Em_X1").replace("&", "§").replace("{player}", player.getName()));
						return;
					}else if(desafios.containsKey(player.getName()) && desafios.get(player.getName()).equalsIgnoreCase(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Desafiado").replace("&", "§").replace("{player}", player.getName()));
						return;
					}
					if(args.length >= 4){
						if(!isNum(args[3])){
							/*p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Informe_Apenas_Numeros").replace("&", "§"));
							return;*/
							//Informe_Apenas_Numeros: "&6&l[FX1] &cInforme apenas números!"
							if(arenamanager.getArena(args[3]) == null){
								/*p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Informada_Nao_Existe").replace("&", "§").replace("{arena}", args[3]));
								return;*/
								if(kitmanager.getKit(args[3]) == null){
									p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Kit_Padrao_Nao_Definido").replace("&", "§").replace("{kit}", args[3]));
									return;
								}else{
									kit = kitmanager.getKit(args[3]);
								}
							}else{
								arena = arenamanager.getArena(args[3]);
							}
						}else{
							aposta = Double.parseDouble(args[3]);
							if(aposta < instance.getConfig().getDouble("Config.Aposta_Minima") || aposta > instance.getConfig().getDouble("Config.Aposta_Maxima")){
								p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aposta_Ideal").replace("&", "§"));
								return;
							}else if(aposta % 2 != 0){
								p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aposta_Par").replace("&", "§"));
								return;
							}
						}
					}
					if(args.length >= 5){
						if(!isNum(args[4])){
							/*p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Informe_Apenas_Numeros").replace("&", "§"));
							return;*/
							if(arenamanager.getArena(args[4]) == null){
								/*p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Informada_Nao_Existe").replace("&", "§").replace("{arena}", args[4]));
								return;*/
								if(kitmanager.getKit(args[4]) == null){
									p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Kit_Padrao_Nao_Definido").replace("&", "§").replace("{kit}", args[4]));
									return;
								}else{
									kit = kitmanager.getKit(args[4]);
								}
							}else{
								arena = arenamanager.getArena(args[4]);
							}
						}else{
							aposta = Double.parseDouble(args[4]);
							if(aposta < instance.getConfig().getDouble("Config.Aposta_Minima") || aposta > instance.getConfig().getDouble("Config.Aposta_Maxima")){
								p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aposta_Ideal").replace("&", "§"));
								return;
							}else if(aposta % 2 != 0){
								p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aposta_Par").replace("&", "§"));
								return;
							}
						}
					}
					if(args.length >= 6){
						if(!isNum(args[5])){
							/*p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Informe_Apenas_Numeros").replace("&", "§"));
							return;*/
							if(arenamanager.getArena(args[5]) == null){
								/*p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Informada_Nao_Existe").replace("&", "§").replace("{arena}", args[5]));
								return;*/
								if(kitmanager.getKit(args[5]) == null){
									p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Kit_Padrao_Nao_Definido").replace("&", "§").replace("{kit}", args[5]));
									return;
								}else{
									kit = kitmanager.getKit(args[5]);
								}
							}else{
								arena = arenamanager.getArena(args[5]);
							}
						}else{
							aposta = Double.parseDouble(args[5]);
							if(aposta < instance.getConfig().getDouble("Config.Aposta_Minima") || aposta > instance.getConfig().getDouble("Config.Aposta_Maxima")){
								p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aposta_Ideal").replace("&", "§"));
								return;
							}else if(aposta % 2 != 0){
								p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aposta_Par").replace("&", "§"));
								return;
							}
						}
					}
					if(arena == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Nao_Existe").replace("&", "§"));
						return;
					}else if(arena.getUsando()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Ocupada").replace("&", "§").replace("{arena}", arena.getNome()));
						return;
					}else if(kit == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Kit_Nao_Existe").replace("&", "§"));
						return;
					}else if(desafios.containsKey(p.getName()) && desafios.get(p.getName()).equalsIgnoreCase(player.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Desafiou").replace("&", "§").replace("{player}", player.getName()));
						return;
					}else if(x1manager.getDelay().containsKey(p.getName()) && x1manager.getDelay().get(p.getName()) > System.currentTimeMillis()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde_Desafiar").replace("&", "§")
								.replace("{tempo}", instance.getTime(x1manager.getDelay().get(p.getName()) - System.currentTimeMillis())));
						return;
					}else if(instance.getEcon().getBalance(p.getName()) < aposta/2 || instance.getEcon().getBalance(player.getName()) < aposta/2){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Dinheiro").replace("&", "§"));
						return;
					}else if(!instance.getEcon().has(p.getName(), kit.getCusto())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Dinheiro_Kit").replace("&", "§"));
						return;
					}
					X1 playerx1 = x1manager.getX1ByPlayer(p);
					if(playerx1 != null){
						if(playerx1.getAcontecendo()){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta_Em_X1").replace("&", "§"));
							return;
						}
						playerx1.getTask().cancel();
						playerx1.setTask(null);
						x1manager.removeX1(playerx1);
					}
					if(instance.getConfig().getBoolean("Config.Avisar_Desafio_A_Todos")){
						for(String msg : instance.getConfig().getStringList("Mensagem.BC.Desafio")){
							for(Player on : instance.getServer().getOnlinePlayers()){
								if(!on.getName().equalsIgnoreCase(player.getName())){
									on.sendMessage(msg.replace("&", "§").replace("{player1}", p.getName()).replace("{player2}", player.getName()).replace("{kit}", kit.getNome())
											.replace("{cmd}", cmd)
											.replace("{arena}", arena.getNome()).replace("{aposta}", NumberFormat.getCurrencyInstance().format(aposta).replaceAll("[^\\d\\.,]+", "")));
								}
							}
						}
					}else{
						for(String msg : instance.getConfig().getStringList("Mensagem.BC.Desafio")){
							p.sendMessage(msg.replace("&", "§").replace("{player1}", p.getName()).replace("{player2}", player.getName()).replace("{kit}", kit.getNome())
									.replace("{cmd}", cmd)
									.replace("{arena}", arena.getNome()).replace("{aposta}", NumberFormat.getCurrencyInstance().format(aposta).replaceAll("[^\\d\\.,]+", "")));
						}
					}
					for(String msg : instance.getConfig().getStringList("Mensagem.Sucesso.Desafio")){
						player.sendMessage(msg.replace("&", "§").replace("{player}", p.getName()).replace("{kit}", kit.getNome())
								.replace("{cmd}", cmd)
								.replace("{arena}", arena.getNome()).replace("{aposta}", NumberFormat.getCurrencyInstance().format(aposta).replaceAll("[^\\d\\.,]+", "")));
					}
					desafios.put(p.getName(), player.getName());
					final X1 x1 = new X1(p, player, arena, kit);
					x1.setAposta(aposta);
					x1.setTask(new BukkitRunnable() {
						@Override
						public void run() {
							desafios.remove(p.getName());
							for(String msg : instance.getConfig().getStringList("Mensagem.BC.Desafio_Expirou")){
								for(Player on : instance.getServer().getOnlinePlayers()){
									on.sendMessage(msg.replace("&", "§").replace("{player1}", p.getName()).replace("{player2}", player.getName()));
								}
							}
							x1manager.removeX1(x1);
						}
					}.runTaskLater(instance, instance.getConfig().getInt("Config.Tempo_Aceitar")*20));
					x1manager.addX1(x1);
					x1manager.getDelay().put(p.getName(), System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.Tempo_Desafiar")));
					return;
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Aceitar"))){
					if(args.length == 2){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Aceitar").replace("&", "§").replace("{cmd}", cmd));
						return;
					}
					final Player player = instance.getServer().getPlayer(args[2]);
					if(player == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§").replace("{player}", args[2]));
						return;
					}else if(!(desafios.containsKey(player.getName()) && desafios.get(player.getName()).equalsIgnoreCase(p.getName()))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Desafiou").replace("&", "§").replace("{player}", player.getName()));
						return;
					}
					final X1 x1 = x1manager.getX1ByPlayerAndPlayer(p, player);
					if(x1 == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Desafiou").replace("&", "§").replace("{player}", player.getName()));
						return;
					}else if(instance.getEcon().getBalance(p.getName()) < x1.getAposta()/2 || instance.getEcon().getBalance(player.getName()) < x1.getAposta()/2){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Dinheiro").replace("&", "§"));
						return;
					}else if(x1.getArena().getUsando()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Ocupada").replace("&", "§").replace("{arena}", x1.getArena().getNome()));
						return;
					}else if(player.isDead()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Morto").replace("&", "§"));
						return;
					}else if(!instance.getEcon().has(x1.getPlayer1().getName(), x1.getKit().getCusto())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Dinheiro_Kit_Outro").replace("&", "§"));
						return;
					}
					X1 x11 = x1manager.getX1ByPlayer(player);
					if(x11 != null){
						if(x11.getAcontecendo()){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Ja_Esta_Em_X1").replace("&", "§"));
							return;
						}
					}
					for(X1 x1s : x1manager.getX1s()){
						if(x1s.getPlayer1().getName().equalsIgnoreCase(player.getName()) && !x1s.getPlayer2().getName().equalsIgnoreCase(p.getName())){
							desafios.remove(x1.getPlayer1().getName());
							x1s.getTask().cancel();
							x1s.setTask(null);
							x1s.setPlayer1(null);
							x1s.setPlayer2(null);
							x1manager.removeX1(x1s);
						}
					}
					desafios.remove(player.getName());
					for(String msg : instance.getConfig().getStringList("Mensagem.BC.Aceitou_Desafio")){
						for(Player on : instance.getServer().getOnlinePlayers()){
							on.sendMessage(msg.replace("&", "§").replace("{player1}", p.getName()).replace("{player2}", player.getName())
									.replace("{arena}", x1.getArena().getNome()).replace("{kit}", x1.getKit().getNome())
									.replace("{aposta}", NumberFormat.getCurrencyInstance().format(x1.getAposta()).replaceAll("[^\\d\\.,]+", "")));
						}
					}
					if((x1.getKit().getProprio() && !x1.getKit().getPerde()) || !x1.getKit().getProprio()){
						instance.getInventario().put(x1.getPlayer1().getName(), x1.getPlayer1().getInventory().getContents());
						instance.getArmor().put(x1.getPlayer1().getName(), x1.getPlayer1().getInventory().getArmorContents());
						instance.getInventario().put(x1.getPlayer2().getName(), x1.getPlayer2().getInventory().getContents());
						instance.getArmor().put(x1.getPlayer2().getName(), x1.getPlayer2().getInventory().getArmorContents());
					}
					if(!x1.getKit().getProprio()){
						x1.getPlayer1().getInventory().clear();
						x1.getPlayer1().getInventory().setArmorContents(null);
						x1.getPlayer2().getInventory().clear();
						x1.getPlayer2().getInventory().setArmorContents(null);
						for(String s : x1.getKit().getItems()){
							ItemStack item = (ItemStack) instance.criarItem(s);
							if(item != null)
								x1.getPlayer1().getInventory().addItem(item);
						}
						for(String s : x1.getKit().getItems()){
							ItemStack item = (ItemStack) instance.criarItem(s);
							if(item != null)
								x1.getPlayer2().getInventory().addItem(item);
						}
						ItemStack[] items = new ItemStack[4];
						for(int i = 0; i<4; i++){
							ItemStack item = (ItemStack) instance.criarItem(x1.getKit().getArmadura()[i]);
							if(item != null)
								items[i] = item;
							else
								items[i] = new ItemStack(Material.AIR);
						}
						x1.getPlayer1().getInventory().setArmorContents(items);
						for(int i = 0; i<4; i++){
							ItemStack item = (ItemStack) instance.criarItem(x1.getKit().getArmadura()[i]);
							if(item != null)
								items[i] = item;
							else
								items[i] = new ItemStack(Material.AIR);
						}
						x1.getPlayer2().getInventory().setArmorContents(items);
					}
					if(x1manager.getDelay().containsKey(x1.getPlayer1().getName())) x1manager.getDelay().remove(x1manager.getDelay().get(x1.getPlayer1().getName()));
					if(x1manager.getDelay().containsKey(x1.getPlayer2().getName())) x1manager.getDelay().remove(x1manager.getDelay().get(x1.getPlayer2().getName()));
					instance.getEcon().withdrawPlayer(p.getName(), x1.getAposta()/2);
					instance.getEcon().withdrawPlayer(player.getName(), (x1.getAposta()/2)+x1.getKit().getCusto());
					if(x1.getTask() != null) x1.getTask().cancel();
					if(x11.getTask() != null) x11.getTask().cancel();
					x1.setTempo(instance.getConfig().getInt("Config.Tempo_Iniciar_X1"));
					//x1.getPlayer1().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, x1.getTempo()*20, 9));
					//x1.getPlayer2().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, x1.getTempo()*20, 9));
					x1.getPlayer1().teleport(x1.getArena().getLocationPlayer1());
					x1.getPlayer2().teleport(x1.getArena().getLocationPlayer2());
					if(x1.getPlayer1().getAllowFlight()) x1.getPlayer1().setAllowFlight(false);
					if(x1.getPlayer2().getAllowFlight()) x1.getPlayer2().setAllowFlight(false);
					x1.setAcontecendo(true);
					x1.getArena().setUsando(true);
					if(x1.getTask() != null) x1.getTask().cancel();
					x1.setTask(new BukkitRunnable() {
						@Override
						public void run() {
							if(x1.getTempo() > 0){
								x1.getPlayer1().sendMessage(instance.getConfig().getString("Mensagem.Sucesso.X1_Iniciando").replace("&", "§").replace("{tempo}", x1.getTempo() + ""));
								x1.getPlayer2().sendMessage(instance.getConfig().getString("Mensagem.Sucesso.X1_Iniciando").replace("&", "§").replace("{tempo}", x1.getTempo() + ""));
								x1.setTempo(x1.getTempo()-1);
							}else{
								x1.getTask().cancel();
								x1.setTask(null);
								x1manager.startX1(x1);
								for(String msg : instance.getConfig().getStringList("Mensagem.BC.X1_Comecou")){
									for(Player on : instance.getServer().getOnlinePlayers()){
										on.sendMessage(msg.replace("&", "§").replace("{player1}", p.getName()).replace("{player2}", player.getName())
												.replace("{arena}", x1.getArena().getNome()).replace("{kit}", x1.getKit().getNome())
												.replace("{aposta}", NumberFormat.getCurrencyInstance().format(x1.getAposta()).replaceAll("[^\\d\\.,]+", "")));
									}
								}
								x1.getPlayer1().teleport(x1.getArena().getLocationPlayer1());
								x1.getPlayer2().teleport(x1.getArena().getLocationPlayer2());
								if(instance.getConfig().getBoolean("Config.Use_SimpleClans")){
									if(instance.getSC().getClanManager().getClanPlayer(x1.getPlayer1()) != null && instance.getSC().getClanManager().getClanPlayer(x1.getPlayer2()) != null){
										if(instance.getSC().getClanManager().getClanPlayer(x1.getPlayer1()).getName()
												.equalsIgnoreCase(instance.getSC().getClanManager().getClanPlayer(x1.getPlayer2()).getName())){
											instance.getSC().getClanManager().getClanPlayer(x1.getPlayer1()).setFriendlyFire(true);
											instance.getSC().getClanManager().getClanPlayer(x1.getPlayer2()).setFriendlyFire(true);
										}
									}
								}
							}
						}
					}.runTaskTimer(instance, 0, 20));
					return;
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Recusar"))){
					if(args.length == 2){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Recusar").replace("&", "§").replace("{cmd}", cmd));
						return;
					}
					Player player = instance.getServer().getPlayer(args[2]);
					if(player == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§").replace("{player}", args[2]));
						return;
					}else if(!(desafios.containsKey(player.getName()) && desafios.get(player.getName()).equalsIgnoreCase(p.getName()))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Desafiou").replace("&", "§").replace("{player}", player.getName()));
						return;
					}
					X1 x1 = x1manager.getX1ByPlayerAndPlayer(p, player);
					if(x1 == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Desafiou").replace("&", "§").replace("{player}", player.getName()));
						return;
					}else if(x1.getAcontecendo()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.X1_Ja_Comecou").replace("&", "§"));
						return;
					}
					for(Player on : instance.getServer().getOnlinePlayers()){
						for(String msg : instance.getConfig().getStringList("Mensagem.BC.Recusou_X1")){
							on.sendMessage(msg.replace("&", "§").replace("{player}", p.getName()).replace("{desafiador}", player.getName()).replace("{kit}", x1.getKit().getNome())
									.replace("{arena}", x1.getArena().getNome())
									.replace("{aposta}", NumberFormat.getCurrencyInstance().format(x1.getAposta()).replaceAll("[^\\d\\.,]+", "")));
						}
					}
					if(x1.getTask() != null){
						x1.getTask().cancel();
						x1.setTask(null);
					}
					desafios.remove(player.getName());
					x1manager.removeX1(x1);
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Camarote"))){
					if(args.length == 2){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Camarote").replace("&", "§"));
						return;
					}
					Arena arena = arenamanager.getArena(args[2]);
					if(arena == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Informada_Nao_Existe").replace("&", "§"));
						return;
					}
					p.teleport(arena.getLocationCamarote());
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.TP_Camarote").replace("&", "§").replace("{arena}", arena.getNome()));
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Rank"))){
					if(args.length == 2){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Rank").replace("&", "§").replace("{cmd}", cmd));
						return;
					}else if(rank.containsKey(p.getName()) && rank.get(p.getName()) > System.currentTimeMillis()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde_Rank").replace("&", "§").replace("{tempo}", instance.getTime(rank.get(p.getName()) - System.currentTimeMillis())));
						return;
					}else if(!instance.getConfig().getBoolean("Config.Rank.Ativar")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Rank_Desativado").replace("&", "§"));
						return;
					}
					if(args[2].equalsIgnoreCase("perdeu")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Fazendo_Consulta").replace("&", "§").replace("{vp}", args[2]));
						if(instance.getSQLType() == 1){
							new BukkitRunnable() {
								@Override
								public void run() {
									try {
										instance.getMySQL().openConnection();
										instance.getMySQL().getTOPDerrotas(p);
										instance.getMySQL().closeConnection();
										p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Consulta_Finalizada").replace("&", "§").replace("{vp}", args[2]));
									} catch (ClassNotFoundException | SQLException e) {}
								}
							}.runTaskAsynchronously(instance);
						}else{
							new BukkitRunnable() {
								@Override
								public void run() {
									try {
										instance.getSQLite().openConnection();
										instance.getSQLite().getTOPDerrotas(p);
										instance.getSQLite().closeConnection();
										p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Consulta_Finalizada").replace("&", "§").replace("{vp}", args[2]));
									} catch (ClassNotFoundException | SQLException e) {}
								}
							}.runTaskAsynchronously(instance);
						}
					}else if(args[2].equalsIgnoreCase("venceu")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Fazendo_Consulta").replace("&", "§").replace("{vp}", args[2]));
						if(instance.getSQLType() == 1){
							new BukkitRunnable() {
								@Override
								public void run() {
									try {
										instance.getMySQL().openConnection();
										instance.getMySQL().getTOPVitorias(p);
										instance.getMySQL().closeConnection();
										p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Consulta_Finalizada").replace("&", "§").replace("{vp}", args[2]));
									} catch (ClassNotFoundException | SQLException e) {}
								}
							}.runTaskAsynchronously(instance);
						}else{
							new BukkitRunnable() {
								@Override
								public void run() {
									try {
										instance.getSQLite().openConnection();
										instance.getSQLite().getTOPVitorias(p);
										instance.getSQLite().closeConnection();
										p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Consulta_Finalizada").replace("&", "§").replace("{vp}", args[2]));
									} catch (ClassNotFoundException | SQLException e) {}
								}
							}.runTaskAsynchronously(instance);
						}
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Rank").replace("&", "§"));
						return;
					}
					rank.put(p.getName(), System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.Tempo_Ver_Rank")));
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Arenas"))){
					if(instance.getConfig().getBoolean("Config.Comando_Arenas.Usar_GUI")){
						Inventory arenas = instance.getArenas();
						arenas.clear();
						for(Arena arena : arenamanager.getArena()){
							if(arenas.firstEmpty() == -1){
								break;
							}else{
								if(arena.getUsando()){
									for(X1 x1 : instance.getX1Manager().getX1s()){
										if(x1.getArena().getNome().equals(arena.getNome())){
											ItemStack itemocupada = (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Comando_Arenas.Item_Arena_Ocupada")
													.replace("{player1}", x1.getPlayer1().getName()).replace("{player2}", x1.getPlayer2().getName())
													.replace("{arena}", arena.getNome()));
											instance.getArenas().setItem(instance.getArenas().firstEmpty(), (ItemStack) itemocupada);
											return;
										}
									}
								}else{
									ItemStack itemlivre = (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Comando_Arenas.Item_Arena_Livre")
											.replace("{arena}", arena.getNome()));
									instance.getArenas().setItem(instance.getArenas().firstEmpty(), (ItemStack) itemlivre);
								}
							}
						}
						p.openInventory(arenas);
					}else{
						String arenas = "";
						for(Arena arena : arenamanager.getArena()){
							arenas += (arena.getUsando() ? ("§c" + arena.getNome()) : ("§a" + arena.getNome())) + ", ";
						}
						if(arenas.equals("")){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Arenas").replace("&", "§"));
							return;
						}
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Lista_Arenas").replace("&", "§").replace("{arenas}", arenas.substring(0, arenas.length()-2)));
					}
					return;
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Kits"))){
					String kits = "";
					for(Kit kit : kitmanager.getKits()){
						kits += (kit.getCusto() == 0 ? ("§f" + kit.getNome()) : ("§b" + kit.getNome() + " (" + NumberFormat.getCurrencyInstance().format(kit.getCusto()).replaceAll("[^\\d\\.,]+", "") + ")")) + ", ";
					}
					if(kits.equals("")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Kits").replace("&", "§"));
						return;
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Lista_Kits").replace("&", "§").replace("{kits}", kits.substring(0, kits.length()-2)));
					return;
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.SetLocation"))){
					if(!p.hasPermission("fx1.admin")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return;
					}
					if(args.length <= 3){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.SetLocation").replace("&", "§").replace("{cmd}", cmd));
						return;
					}
					Arena arena = arenamanager.getArena(args[2]);
					if(arena == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Informada_Nao_Existe").replace("&", "§").replace("{arena}", args[2]));
						return;
					}
					if(args[3].equalsIgnoreCase("Player1")){
						arenamanager.setArenaLocation(arena, "Player1", p.getLocation());
					}else if(args[3].equalsIgnoreCase("Player2")){
						arenamanager.setArenaLocation(arena, "Player2", p.getLocation());
					}else if(args[3].equalsIgnoreCase("Saida")){
						arenamanager.setArenaLocation(arena, "Saida", p.getLocation());
					}else if(args[3].equalsIgnoreCase("Camarote")){
						arenamanager.setArenaLocation(arena, "Camarote", p.getLocation());
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.SetLocation").replace("&", "§").replace("{cmd}", cmd));
						return;
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Location").replace("&", "§").replace("{arena}", arena.getNome()).replace("{loc}", args[3]));
					return;
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Cancelar"))){
					if(!p.hasPermission("fx1.admin")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return;
					}else if(args.length == 2){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Cancelar").replace("&", "§").replace("{cmd}", cmd));
						return;
					}
					Player player = instance.getServer().getPlayer(args[2]);
					if(player == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§").replace("{player}", args[2]));
						return;
					}
					X1 x1 = null;
					if(args.length == 3){
						x1 = x1manager.getX1ByPlayer(player);
						if(x1 == null){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Nao_Esta_Em_X1").replace("&", "§").replace("{player}", player.getName()));
							return;
						}
					}else{
						Player player2 = instance.getServer().getPlayer(args[3]);
						if(player2 == null){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§").replace("{player}", args[3]));
							return;
						}
						x1 = x1manager.getX1ByPlayerAndPlayer(player, player2);
						if(x1 == null){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.X1_Nao_Encontrado").replace("&", "§"));
							return;
						}
					}
					for(String msg : instance.getConfig().getStringList("Mensagem.BC.Staff_Cancelou")){
						for(Player on : instance.getServer().getOnlinePlayers()){
							on.sendMessage(msg.replace("&", "§").replace("{staff}", p.getName())
									.replace("{player1}", x1.getPlayer1().getName()).replace("{player2}", x1.getPlayer2().getName()));
						}
					}
					x1.getPlayer1().teleport(x1.getArena().getLocationSaida());
					x1.getPlayer2().teleport(x1.getArena().getLocationSaida());
					x1manager.stopX1(x1, null);
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Comandos.Reset"))){
					if(!p.hasPermission("fx1.admin")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return;
					}else if(args.length <= 3){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Reset").replace("&", "§").replace("{cmd}", cmd));
						return;
					}
					final Player player = instance.getServer().getPlayer(args[2]);
					if(player == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§").replace("{player}", args[2]));
						return;
					}else if(!(args[3].equalsIgnoreCase("derrotas") || args[3].equalsIgnoreCase("vitorias"))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Vitorias_Ou_Derrotas").replace("&", "§"));
						return;
					}
					if(instance.getSQLType() == 1){
						new BukkitRunnable() {
							@Override
							public void run() {
								try {
									instance.getMySQL().openConnection();
									instance.getMySQL().resetPlayer(player.getName(), args[3]);
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
									instance.getSQLite().resetPlayer(player.getName(), args[3]);
									instance.getSQLite().closeConnection();
								} catch (ClassNotFoundException | SQLException e) {}
							}
						}.runTaskAsynchronously(instance);
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Resetou").replace("&", "§").replace("{tipo}", args[3]).replace("{player}", player.getName()));
				}else{
					for(String msg : instance.getConfig().getStringList("Mensagem.Comandos")){
						p.sendMessage(msg.replace("&", "§").replace("{cmd}", cmd));
					}
				}
				return;
			}
		}
	}

	public boolean isNum(String string){
		try{
			Double.parseDouble(string);
			return true;
		}catch(Exception e){return false;}
	}

}

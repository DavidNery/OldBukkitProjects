package me.dery;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

public class Comandos implements CommandExecutor{
	
	DTNTRain instance = DTNTRain.getDTNTRain();
	
	static ArrayList<String> participantes = new ArrayList<String>();
	static ArrayList<String> noevento = new ArrayList<String>();
	static boolean aberto = false, ocorrendo = false;
	static int id, id2, id3, anuncios, tanuncios, tempo = 6;
	Random r = new Random();
	
	DecimalFormat df = new DecimalFormat("0.00");
	
	public boolean InvVazio(Player p){
		for(ItemStack item : p.getInventory().getContents()){
			if(item != null && item.getType()!=Material.AIR)
				return false;
		}
		for(ItemStack item : p.getInventory().getArmorContents()){
			if(item != null && item.getType()!=Material.AIR)
				return false;
		}
		return true;
	}
	
	@SuppressWarnings("static-access")
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args){
		if(sender instanceof Player){
			// Player
			final Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("tntrain")){
				if(args.length == 0){
					for(int i = 0; i<100; i++){
						p.sendMessage("");
					}
					p.sendMessage("§6=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
					p.sendMessage("§7Comandos do Evento TNTRain:");
					p.sendMessage("§a#Só irao aparecer os comandos que você pode utilizar!");
					if(p.hasPermission("tntrain.entrar")){
						p.sendMessage("§c/tntrain entrar §7• §eEntre no Evento TNTRain!");
					}
					if(p.hasPermission("tntrain.sair")){
						p.sendMessage("§c/tntrain sair §7• §eSaia do Evento TNTRain!");
					}
					if(p.hasPermission("tntrain.iniciar")){
						p.sendMessage("§c/tntrain iniciar §7• §eInicie no Evento TNTRain!");
					}
					if(p.hasPermission("tntrain.parar")){
						p.sendMessage("§c/tntrain parar §7• §ePare o Evento TNTRain!");
					}
					if(p.hasPermission("tntrain.setlobby")){
						p.sendMessage("§c/tntrain setlobby §7• §eSete o lobby do Evento TNTRain!");
					}
					if(p.hasPermission("tntrain.setspawn")){
						p.sendMessage("§c/tntrain setspawn §7• §eSete o spawn do Evento TNTRain!");
					}
					if(p.hasPermission("tntrain.setexit")){
						p.sendMessage("§c/tntrain setexit §7• §eSete a saida do Evento TNTRain!");
					}
					p.sendMessage("§6=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
					return true;
				}else if(args.length == 1 && args[0].equalsIgnoreCase("entrar")){
					if(!p.hasPermission("dtntrain.entrar")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("{cmd}", "entrar").replace("&", "§"));
						return true;
					}
					if(ocorrendo == false || aberto == false){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Aberto_Ocorrendo").replace("&", "§"));
						return true;
					}
					if(participantes.contains(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta").replace("&", "§"));
						return true;
					}
					if(instance.getConfig().getBoolean("Config.Force_Empty_Inventory")){
						if(!InvVazio(p)){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Contem_Itens").replace("&", "§"));
							return true;
						}
					}
					if(instance.getConfig().getBoolean("Config.Force_Fly_Off") && p.getAllowFlight() == true){
						if(!p.hasPermission("dtntrain.admin")){
							p.setFlying(false);
				        	p.setAllowFlight(false);
						}
					}
					if(instance.getConfig().getBoolean("Config.Force_GM_0") && p.getGameMode() != GameMode.SURVIVAL){
						if(!p.hasPermission("dtntrain.admin")){
							p.setGameMode(GameMode.SURVIVAL);
						}
					}
					participantes.add(p.getName());
					for(String entrou : instance.getConfig().getStringList("Mensagem.Entrou")){
						for(Player on : Bukkit.getOnlinePlayers()){
							on.sendMessage(entrou.replace("&", "§").replace("{player}", p.getName()).replace("{players}", String.valueOf(participantes.size())));
						}
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Entrou").replace("&", "§"));
					instance.gettntrm().TPLobby(p);
					return true;
				}else if(args.length == 1 && args[0].equalsIgnoreCase("sair")){
					if(!p.hasPermission("dtntrain.sair")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("{cmd}", "sair").replace("&", "§"));
						return true;
					}
					if(!participantes.contains(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta").replace("&", "§"));
						return true;
					}
					participantes.remove(p.getName());
					noevento.remove(p.getName());
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Saiu").replace("&", "§"));
					instance.gettntrm().TPExit(p);
					if(noevento.size() == 1){
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
					return true;
				}else if(args.length == 1 && args[0].equalsIgnoreCase("iniciar")){
					if(!p.hasPermission("dtntrain.iniciar")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("{cmd}", "iniciar").replace("&", "§"));
						return true;
					}
					if(ocorrendo == true || aberto == true){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta_Ocorrendo").replace("&", "§"));
						return true;
					}
					if(instance.gettntrm().CheckSpawn() || instance.gettntrm().CheckExit() || instance.gettntrm().CheckLobby()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Locais").replace("&", "§"));
						return true;
					}
					anuncios = instance.getConfig().getInt("Config.Anuncios");
					tanuncios = instance.getConfig().getInt("Config.Tempo_Entre_Anuncios");
					final double premio = instance.getConfig().getDouble("Config.Premio");
					final BukkitScheduler scheduler = instance.getServer().getScheduler();
					id = scheduler.scheduleSyncRepeatingTask(instance, new Runnable(){
						public void run() {
							if(anuncios > 0){
								ocorrendo = true;
								aberto = true;
								int tempo = anuncios * tanuncios;
								for(String anuncios : instance.getConfig().getStringList("Mensagem.Anuncios")){
									for(Player on : Bukkit.getOnlinePlayers()){
										on.sendMessage(anuncios.replace("{premio}", String.valueOf(premio)).replace("{players}", String.valueOf(participantes.size())).replace("{tempo}", String.valueOf(tempo)).replace("&", "§"));
									}
								}
								anuncios -= 1;
							}else{
								if(participantes.size() >= instance.getConfig().getInt("Config.Min_Participantes")){
									for(String fim : instance.getConfig().getStringList("Mensagem.Iniciado")){
										for(Player on : Bukkit.getOnlinePlayers()){
											on.sendMessage(fim.replace("&", "§"));
										}
									}
									ocorrendo = true;
									aberto = false;
									for(String players : participantes){
										Player tp = instance.getServer().getPlayer(players);
										instance.gettntrm().TPSpawn(tp);
										noevento.add(tp.getName());
									}
									Bukkit.getServer().getScheduler().cancelTask(id);
									id2 = scheduler.scheduleSyncRepeatingTask(instance, new Runnable(){
										public void run(){
											Player player = Bukkit.getServer().getPlayer(participantes.get(r.nextInt(participantes.size())));
											Location loc = player.getLocation();
											World world = loc.getWorld();
											for(int x = -6; x <= 6; x += 3){
												for(int z = -6; z <= 6; z += 3){
													Location tntlocation = new Location(world, loc.getBlockX() + x, world.getHighestBlockYAt(loc) + 10, loc.getBlockZ() + z);
													world.spawn(tntlocation, TNTPrimed.class);
												}
											}
										}
									}, 0, instance.getConfig().getInt("Config.Tempo_Cair") * 20L);
								}else{
									Bukkit.getServer().getScheduler().cancelTask(id);
									ocorrendo = false;
									aberto = false;
									for(String players : participantes){
										Player tp = instance.getServer().getPlayer(players);
										instance.gettntrm().TPExit(tp);
									}
									for(String fim : instance.getConfig().getStringList("Mensagem.Finalizado")){
										for(Player on : Bukkit.getOnlinePlayers()){
											on.sendMessage(fim.replace("{motivo}", instance.getConfig().getString("Motivo.Poucos_Players")).replace("&", "§"));
										}
									}
									participantes.clear();
									noevento.clear();
								}
							}
						}
					}, 0, tanuncios * 20L);
					return true;
				}else if(args.length == 1 && args[0].equalsIgnoreCase("parar")){
					if(!p.hasPermission("dtntrain.parar")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("{cmd}", "parar").replace("&", "§"));
						return true;
					}
					if(ocorrendo == false && aberto == false){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Ocorrendo").replace("&", "§"));
						return true;
					}
					if(instance.gettntrm().CheckSpawn() || instance.gettntrm().CheckExit() || instance.gettntrm().CheckLobby()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Locais").replace("&", "§"));
						return true;
					}
					BukkitScheduler scheduler = instance.getServer().getScheduler();
					id3 = scheduler.scheduleSyncRepeatingTask(instance, new Runnable(){
						public void run(){
							if(tempo > 1){
								tempo -= 1;
								p.sendMessage(instance.getConfig().getString("Mensagem.Sendo_Parado").replace("&", "§").replace("{tempo}", String.valueOf(tempo)));
								Bukkit.getServer().getScheduler().cancelTask(id2);
							}else{
								for(String parou : participantes){
									Player tp = instance.getServer().getPlayer(parou);
									instance.gettntrm().TPExit(tp);
								}
								for(String parou : noevento){
									Player tp = instance.getServer().getPlayer(parou);
									instance.gettntrm().TPExit(tp);
								}
								for(String fim : instance.getConfig().getStringList("Mensagem.Finalizado")){
									for(Player on : Bukkit.getOnlinePlayers()){
										on.sendMessage(fim.replace("{motivo}", instance.getConfig().getString("Motivo.Staff_Cancelou")).replace("&", "§"));
									}
								}
								participantes.clear();
								noevento.clear();
								Bukkit.getServer().getScheduler().cancelTask(id3);
								Bukkit.getServer().getScheduler().cancelTask(id2);
							}
						}
					}, 0, 1 * 20L);
					return true;
				}else if(args.length == 1 && args[0].equalsIgnoreCase("setspawn")){
					if(!p.hasPermission("dtntrain.setspawn")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("{cmd}", "setspawn").replace("&", "§"));
						return true;
					}
					instance.gettntrm().SetSpawn(p);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Spawn_Setado").replace("{world}", p.getLocation().getWorld().getName())
							.replace("{x}", df.format(p.getLocation().getX()))
							.replace("{y}", df.format(p.getLocation().getY()))
							.replace("{z}", df.format(p.getLocation().getZ()))
							.replace("&", "§"));
					return true;
				}else if(args.length == 1 && args[0].equalsIgnoreCase("setexit")){
					if(!p.hasPermission("dtntrain.setexit")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("{cmd}", "setexit").replace("&", "§"));
						return true;
					}
					instance.gettntrm().SetExit(p);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Saida_Setada").replace("{world}", p.getLocation().getWorld().getName())
							.replace("{x}", df.format(p.getLocation().getX()))
							.replace("{y}", df.format(p.getLocation().getY()))
							.replace("{z}", df.format(p.getLocation().getZ()))
							.replace("&", "§"));
					return true;
				}else if(args.length == 1 && args[0].equalsIgnoreCase("setlobby")){
					if(!p.hasPermission("dtntrain.setlobby")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("{cmd}", "setlobby").replace("&", "§"));
						return true;
					}
					instance.gettntrm().SetLobby(p);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Lobby_Setado").replace("{world}", p.getLocation().getWorld().getName())
							.replace("{x}", df.format(p.getLocation().getX()))
							.replace("{y}", df.format(p.getLocation().getY()))
							.replace("{z}", df.format(p.getLocation().getZ()))
							.replace("&", "§"));
					return true;
				}
			}
		}else{
			// Console
			if(cmd.getName().equalsIgnoreCase("tntrain")){
				if(args.length == 0){
					sender.sendMessage("§6=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
					sender.sendMessage("§7Comandos do Evento TNTRain:");
					sender.sendMessage("§c/tntrain iniciar §7- §eInicie no Evento TNTRain!");
					sender.sendMessage("§c/tntrain parar §7- §ePare o Evento TNTRain!");
					sender.sendMessage("§6=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
					return true;
				}else if(args.length == 1 && args[0].equalsIgnoreCase("iniciar")){
					if(!sender.hasPermission("dtntrain.iniciar")){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("{cmd}", "iniciar").replace("&", "§"));
						return true;
					}
					if(ocorrendo == true || aberto == true){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta_Ocorrendo").replace("&", "§"));
						return true;
					}
					if(instance.gettntrm().CheckSpawn() || instance.gettntrm().CheckExit() || instance.gettntrm().CheckLobby()){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Locais").replace("&", "§"));
						return true;
					}
					anuncios = instance.getConfig().getInt("Config.Anuncios");
					tanuncios = instance.getConfig().getInt("Config.Tempo_Entre_Anuncios");
					final double premio = instance.getConfig().getDouble("Config.Premio");
					final BukkitScheduler scheduler = instance.getServer().getScheduler();
					id = scheduler.scheduleSyncRepeatingTask(instance, new Runnable(){
						public void run() {
							if(anuncios > 0){
								ocorrendo = true;
								aberto = true;
								int tempo = anuncios * tanuncios;
								for(String anuncios : instance.getConfig().getStringList("Mensagem.Anuncios")){
									for(Player on : Bukkit.getOnlinePlayers()){
										on.sendMessage(anuncios.replace("{premio}", String.valueOf(premio)).replace("{players}", String.valueOf(participantes.size())).replace("{tempo}", String.valueOf(tempo)).replace("&", "§"));
									}
								}
								anuncios -= 1;
							}else{
								if(participantes.size() >= instance.getConfig().getInt("Config.Min_Participantes")){
									for(String fim : instance.getConfig().getStringList("Mensagem.Iniciado")){
										for(Player on : Bukkit.getOnlinePlayers()){
											on.sendMessage(fim.replace("&", "§"));
										}
									}
									ocorrendo = true;
									aberto = false;
									for(String players : participantes){
										Player tp = instance.getServer().getPlayer(players);
										instance.gettntrm().TPSpawn(tp);
										noevento.add(tp.getName());
									}
									Bukkit.getServer().getScheduler().cancelTask(id);
									id2 = scheduler.scheduleSyncRepeatingTask(instance, new Runnable(){
										public void run(){
											Player player = Bukkit.getServer().getPlayer(participantes.get(r.nextInt(participantes.size())));
											Location loc = player.getLocation();
											World world = loc.getWorld();
											for(int x = -6; x <= 6; x += 3){
												for(int z = -6; z <= 6; z += 3){
													Location tntlocation = new Location(world, loc.getBlockX() + x, world.getHighestBlockYAt(loc) + 10, loc.getBlockZ() + z);
													world.spawn(tntlocation, TNTPrimed.class);
												}
											}
										}
									}, 0, instance.getConfig().getInt("Config.Tempo_Cair") * 20L);
								}else{
									Bukkit.getServer().getScheduler().cancelTask(id);
									ocorrendo = false;
									aberto = false;
									for(String players : participantes){
										Player tp = instance.getServer().getPlayer(players);
										instance.gettntrm().TPExit(tp);
									}
									for(String fim : instance.getConfig().getStringList("Mensagem.Finalizado")){
										for(Player on : Bukkit.getOnlinePlayers()){
											on.sendMessage(fim.replace("{motivo}", instance.getConfig().getString("Motivo.Poucos_Players")).replace("&", "§"));
										}
									}
									participantes.clear();
									noevento.clear();
								}
							}
						}
					}, 0, tanuncios * 20L);
					return true;
				}else if(args.length == 1 && args[0].equalsIgnoreCase("parar")){
					if(!sender.hasPermission("dtntrain.parar")){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("{cmd}", "parar").replace("&", "§"));
						return true;
					}
					if(ocorrendo == false && aberto == false){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Ocorrendo").replace("&", "§"));
						return true;
					}
					if(instance.gettntrm().CheckSpawn() || instance.gettntrm().CheckExit() || instance.gettntrm().CheckLobby()){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Locais").replace("&", "§"));
						return true;
					}
					BukkitScheduler scheduler = instance.getServer().getScheduler();
					id3 = scheduler.scheduleSyncRepeatingTask(instance, new Runnable(){
						public void run(){
							if(tempo > 1){
								tempo -= 1;
								sender.sendMessage(instance.getConfig().getString("Mensagem.Sendo_Parado").replace("&", "§").replace("{tempo}", String.valueOf(tempo)));
								Bukkit.getServer().getScheduler().cancelTask(id2);
							}else{
								if(noevento.size() == 1 || participantes.size() == 1){
									Bukkit.getServer().getScheduler().cancelTask(id);
									ocorrendo = false;
									aberto = false;
									for(String vencedor : noevento){
										Player tp = instance.getServer().getPlayer(vencedor);
										instance.gettntrm().TPExit(tp);
										instance.econ.depositPlayer(tp.getName(), instance.getConfig().getInt("Config.Premio"));
										if(instance.getConfig().getBoolean("Config.Comandos")){
											for(String cmds : instance.getConfig().getStringList("Config.Comandos_Executados")){
												Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmds.replace("{player}", tp.getName()));
											}
										}
									}
									for(String fim : instance.getConfig().getStringList("Mensagem.Finalizado")){
										for(Player on : Bukkit.getOnlinePlayers()){
											on.sendMessage(fim.replace("{motivo}", instance.getConfig().getString("Motivo.Poucos_Players")).replace("&", "§"));
										}
									}
									participantes.clear();
									noevento.clear();
								}
							}
						}
					}, 0, 1 * 20L);
					return true;
				}
			}
		}
		return false;
	}

}

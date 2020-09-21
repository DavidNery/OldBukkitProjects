package me.dery.dpulapula;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

public class Comandos implements CommandExecutor{
	
	static DPulaPula instance = DPulaPula.getDPulaPula();
	static Eventos events = instance.getEventos();
	static PulaPulaManager ppm = instance.getPulaPulaManager();
	
	static boolean ocorrendo = false, aberto = false;
	static ArrayList<String> players = new ArrayList<String>();
	static int anuncios, tanuncios, id, minplayers;
	static double premio;
	
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
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(sender instanceof Player){
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("pulapula")){
				if(args.length == 0){
					sendMessageList(p, instance.getConfig().getStringList("Mensagem.Comandos_Player"));
					if(p.hasPermission("dpulapula.admin")){
						sendMessageList(p, instance.getConfig().getStringList("Mensagem.Comandos_Admin"));
					}
				}else if(args[0].equalsIgnoreCase("iniciar")){
					if((!p.hasPermission("dpulapula.iniciar")) || (!p.hasPermission("dpulapula.*"))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return true;
					}
					if(ocorrendo == true || aberto == true){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Iniciou").replace("&", "§"));
						return true;
					}
					if(ppm.CheckSpawn() || ppm.CheckExit() || ppm.CheckLobby()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Locais").replace("&", "§"));
						return true;
					}
					anuncios = instance.getConfig().getInt("Config.Anuncios");
					tanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
					minplayers = instance.getConfig().getInt("Config.Min_Players");
					premio = instance.getConfig().getDouble("Config.Premio");
					aberto = true;
					ocorrendo = false;
					BukkitScheduler scheduler = instance.getServer().getScheduler();
					id = scheduler.scheduleSyncRepeatingTask(instance, new Runnable(){
						public void run(){
							if(anuncios > 0){
								for(String msgs : instance.getConfig().getStringList("Mensagem.BC.Aberto")){
									instance.getServer().broadcastMessage(msgs.replace("{premio}", String.valueOf(NumberFormat.getNumberInstance().format(premio))).replace("{tempo}", String.valueOf(anuncios * tanuncios)).replace("{chamadas}", String.valueOf(anuncios)).replace("{qplayers}", String.valueOf(players.size())).replace("&", "§"));
								}
								anuncios -= 1;
							}else{
								if(players.size() >= minplayers){
									aberto = false;
			                		ocorrendo = true;
			                		for(String tp : players){
			                			Player tpplayer = instance.getServer().getPlayer(tp);
			                			ppm.TPSpawn(tpplayer);
			                		}
			                		for(String s : instance.getConfig().getStringList("Mensagem.BC.Iniciou")){
			                		    instance.getServer().broadcastMessage(s.replace("{premio}", String.valueOf(NumberFormat.getNumberInstance().format(premio))).replace("{qplayers}", String.valueOf(players.size())).replace("&", "§"));
			                		}
								}else{
									for(String msgs : instance.getConfig().getStringList("Mensagem.BC.Fechado")){
										instance.getServer().broadcastMessage(msgs.replace("{motivo}", instance.getConfig().getString("Motivo.Poucos_Players")).replace("{premio}", String.valueOf(NumberFormat.getNumberInstance().format(premio))).replace("{qplayers}", String.valueOf(players.size())).replace("&", "§"));
									}
									aberto = false;
			                		ocorrendo = false;
			                		for(String tp : players){
			                			Player tpplayer = instance.getServer().getPlayer(tp);
			                			players.remove(tpplayer);
			                			ppm.TPExit(tpplayer);
			                		}
			                		players.clear();
			                		scheduler.cancelTask(id);
								}
							}
						}
					}, 0, tanuncios * 20);
				}else if(args[0].equalsIgnoreCase("parar")){
					if((!p.hasPermission("dpulapula.parar")) || (!p.hasPermission("dpulapula.*"))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return true;
					}
					if(aberto == false && ocorrendo == false){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Aberto").replace("&", "§"));
						return true;
					}
            		for(String s : instance.getConfig().getStringList("Mensagem.BC.Fechado")){
                		instance.getServer().broadcastMessage(s.replace("{premio}", String.valueOf(NumberFormat.getNumberInstance().format(premio))).replace("{qplayers}", String.valueOf(players.size())).replace("{motivo}", instance.getConfig().getString("Motivo.Staff_Cancelou")).replace("&", "§"));
                	}
            		for(String tp : players){
            			Player tpplayer = instance.getServer().getPlayer(tp);
            			players.remove(tpplayer);
            			ppm.TPExit(tpplayer);
            		}
            		aberto = false;
            		ocorrendo = false;
            		players.clear();
            		instance.getServer().getScheduler().cancelTask(id);
				}else if(args.length == 1 && args[0].equalsIgnoreCase("setspawn")){
					if((!p.hasPermission("dpulapula.setspawn")) || (!p.hasPermission("dpulapula.admin"))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return true;
					}
					if(ocorrendo == true || aberto == true){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Esta_Aberto_Ocorrendo").replace("&", "§"));
						return true;
					}
					ppm.SetSpawn(p);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Spawn").replace("&", "§").replace("{x}", String.valueOf(df.format(p.getLocation().getX()))).replace("{y}", String.valueOf(df.format(p.getLocation().getY()))).replace("{z}", String.valueOf(df.format(p.getLocation().getZ()))).replace("{world}", p.getLocation().getWorld().getName()));
				}else if(args.length == 1 && args[0].equalsIgnoreCase("setexit")){
					if((!p.hasPermission("dpulapula.setexit")) || (!p.hasPermission("dpulapula.admin"))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return true;
					}
					if(ocorrendo == true || aberto == true){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Esta_Aberto_Ocorrendo").replace("&", "§"));
						return true;
					}
					ppm.SetExit(p);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Saida").replace("&", "§").replace("{x}", String.valueOf(df.format(p.getLocation().getX()))).replace("{y}", String.valueOf(df.format(p.getLocation().getY()))).replace("{z}", String.valueOf(df.format(p.getLocation().getZ()))).replace("{world}", p.getLocation().getWorld().getName()));
				}else if(args.length == 1 && args[0].equalsIgnoreCase("setlobby")){
					if((!p.hasPermission("dpulapula.setlobby")) || (!p.hasPermission("dpulapula.admin"))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return true;
					}
					if(ocorrendo == true || aberto == true){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Esta_Aberto_Ocorrendo").replace("&", "§"));
						return true;
					}
					ppm.SetLobby(p);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Lobby").replace("&", "§").replace("{x}", String.valueOf(df.format(p.getLocation().getX()))).replace("{y}", String.valueOf(df.format(p.getLocation().getY()))).replace("{z}", String.valueOf(df.format(p.getLocation().getZ()))).replace("{world}", p.getLocation().getWorld().getName()));
				}else if(args.length == 1 && args[0].equalsIgnoreCase("entrar")){
					if(aberto == false){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Aberto").replace("&", "§"));
						return true;
					}else if(players.contains(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta").replace("&", "§"));
						return true;
					}
					if(instance.getConfig().getBoolean("Config.Force_Empty_Inventory")){
						if(!p.hasPermission("dpulapula.admin")){
							if(!InvVazio(p)){
								p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Contem_Itens").replace("&", "§"));
								return true;
							}
						}
					}
					if(instance.getConfig().getBoolean("Config.Force_Fly_Off")){
						if(!p.hasPermission("dpulapula.admin")){
							if(p.getAllowFlight()){
								p.setAllowFlight(false);
								p.setFlying(false);
								p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Fly_Desativado").replace("&", "§"));
							}
						}
					}
					if(instance.getConfig().getBoolean("Config.Force_GM_0")){
						if(!p.hasPermission("dpulapula.admin")){
							if(p.getGameMode() != GameMode.SURVIVAL){
								p.setGameMode(GameMode.SURVIVAL);
								p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.GM_Changed_0").replace("&", "§"));
							}
						}
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Entrou").replace("&", "§"));
					ppm.TPSpawn(p);
					players.add(p.getName());
				}else if(args.length == 1 && args[0].equalsIgnoreCase("sair")){
					if(players.contains(p.getName())){
						players.remove(p.getName());
						ppm.TPExit(p);
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Saiu_Evento").replace("&", "§"));
						if(players.size() == 0 && ocorrendo == true){
							for(String msgs : instance.getConfig().getStringList("Mensagem.BC.Sem_Vencedor")){
								instance.getServer().broadcastMessage(msgs.replace("&", "§"));
							}
							ocorrendo = false;
							aberto = false;
						}
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_No_Evento").replace("&", "§"));
					}
				}else{
					sendMessageList(p, instance.getConfig().getStringList("Mensagem.Comandos_Player"));
					if(p.hasPermission("dpulapula.admin")){
						sendMessageList(p, instance.getConfig().getStringList("Mensagem.Comandos_Admin"));
					}
				}
			}
		}else{
			if(cmd.getName().equalsIgnoreCase("pulapula")){
				if(args.length == 0){
					sendMessageListConsole(sender, instance.getConfig().getStringList("Mensagem.Comandos_Console"));
				}else if(args[0].equalsIgnoreCase("iniciar")){
					if(ocorrendo == true || aberto == true){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Iniciou").replace("&", "§"));
						return true;
					}
					if(ppm.CheckSpawn() || ppm.CheckExit() || ppm.CheckLobby()){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Locais").replace("&", "§"));
						return true;
					}
					anuncios = instance.getConfig().getInt("Config.Anuncios");
					tanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
					minplayers = instance.getConfig().getInt("Config.Min_Players");
					premio = instance.getConfig().getDouble("Config.Premio");
					aberto = true;
					ocorrendo = false;
					BukkitScheduler scheduler = instance.getServer().getScheduler();
					id = scheduler.scheduleSyncRepeatingTask(instance, new Runnable(){
						public void run(){
							if(anuncios > 0){
								for(String msgs : instance.getConfig().getStringList("Mensagem.BC.Aberto")){
									instance.getServer().broadcastMessage(msgs.replace("{premio}", String.valueOf(NumberFormat.getNumberInstance().format(premio))).replace("{tempo}", String.valueOf(anuncios * tanuncios)).replace("{chamadas}", String.valueOf(anuncios)).replace("{qplayers}", String.valueOf(players.size())).replace("&", "§"));
								}
								anuncios -= 1;
							}else{
								if(players.size() >= minplayers){
									aberto = false;
			                		ocorrendo = true;
			                		for(String tp : players){
			                			Player tpplayer = instance.getServer().getPlayer(tp);
			                			ppm.TPSpawn(tpplayer);
			                		}
			                		for(String s : instance.getConfig().getStringList("Mensagem.BC.Iniciou")){
			                		    instance.getServer().broadcastMessage(s.replace("{premio}", String.valueOf(NumberFormat.getNumberInstance().format(premio))).replace("{qplayers}", String.valueOf(players.size())).replace("&", "§"));
			                		}
			                		scheduler.cancelTask(id);
								}else{
									for(String msgs : instance.getConfig().getStringList("Mensagem.BC.Fechado")){
										instance.getServer().broadcastMessage(msgs.replace("{motivo}", instance.getConfig().getString("Motivo.Poucos_Players")).replace("{premio}", String.valueOf(NumberFormat.getNumberInstance().format(premio))).replace("{qplayers}", String.valueOf(players.size())).replace("&", "§"));
									}
									aberto = false;
			                		ocorrendo = false;
			                		for(String tp : players){
			                			Player tpplayer = instance.getServer().getPlayer(tp);
			                			players.remove(tpplayer);
			                			ppm.TPExit(tpplayer);
			                		}
			                		players.clear();
			                		scheduler.cancelTask(id);
								}
							}
						}
					}, 0, tanuncios * 20);
				}else if(args[0].equalsIgnoreCase("parar")){
					if(aberto == false && ocorrendo == false){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Aberto").replace("&", "§"));
						return true;
					}
					for(String s : instance.getConfig().getStringList("Mensagem.BC.Fechado")){
                		instance.getServer().broadcastMessage(s.replace("{premio}", String.valueOf(NumberFormat.getNumberInstance().format(premio))).replace("{qplayers}", String.valueOf(players.size())).replace("{motivo}", instance.getConfig().getString("Motivo.Staff_Cancelou")).replace("&", "§"));
                	}
            		for(String tp : players){
            			Player tpplayer = instance.getServer().getPlayer(tp);
            			players.remove(tpplayer);
            			ppm.TPExit(tpplayer);
            		}
            		aberto = false;
            		ocorrendo = false;
            		players.clear();
            		instance.getServer().getScheduler().cancelTask(id);
				}else{
					sendMessageListConsole(sender, instance.getConfig().getStringList("Mensagem.Comandos_Console"));
				}
			}
		}
		return false;
	}
	
	public void sendMessageList(Player p, List<String> msg){
		for(String s : msg){
			p.sendMessage(s.replace("&", "§"));
		}
	}
	
	public void sendMessageListConsole(CommandSender sender, List<String> msg){
		for(String s : msg){
			sender.sendMessage(s.replace("&", "§"));
		}
	}

}

package me.dery.dhotpotato;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;

public class Comandos implements CommandExecutor{
	
	static DHotPotato instance = DHotPotato.getDHotPotato();
	static PotatoManager pm = instance.getPotatoManager();
	
	static ArrayList<String> players = new ArrayList<String>();
	static boolean ocorrendo = false, aberto = false;
	static int tanuncios, anuncios, minplayers, texplodir, id, id2;
	static double premio;
	Random r = new Random();
	static Player potator = null;
	
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
		if(cmd.getName().equalsIgnoreCase("hotpotato")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				if(args.length == 0){
					sendMessageList(p, instance.getConfig().getStringList("Mensagem.Comandos_Player"));
					if(p.hasPermission("dhotpotato.admin")){
						sendMessageList(p, instance.getConfig().getStringList("Mensagem.Comandos_Admin"));
					}
					return true;
				}else if(args[0].equalsIgnoreCase("iniciar")){
					if(!p.hasPermission("dhotpotato.iniciar")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("{1}", args[0]).replace("{0}", cmd.getName()).replace("&", "§"));
						return true;
					}else if(ocorrendo == true || aberto == true){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta_Aberto").replace("&", "§"));
						return true;
					}else if(pm.CheckExit() || pm.CheckLobby() || pm.CheckSpawn()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Locais").replace("&", "§"));
						return true;
					}
					aberto = true;
					premio = instance.getConfig().getDouble("Config.Premio");
					anuncios = instance.getConfig().getInt("Config.Anuncios");
					tanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
					texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
					minplayers = instance.getConfig().getInt("Config.Min_Players");
					final BukkitScheduler scheduler = instance.getServer().getScheduler();
					id = scheduler.scheduleSyncRepeatingTask(instance, new Runnable(){
						public void run(){
							if(anuncios > 0){
								for(String iniciando : instance.getConfig().getStringList("Mensagem.BC.Iniciando")){
									instance.getServer().broadcastMessage(iniciando.replace("{tempo}", String.valueOf(anuncios * tanuncios)).replace("{chamadas}", String.valueOf(anuncios)).replace("{minplayers}", String.valueOf(minplayers)).replace("{players}", String.valueOf(players.size())).replace("{premio}", NumberFormat.getNumberInstance().format(premio)).replace("&", "§"));
								}
								anuncios -= 1;
							}else{
								if(players.size() >= minplayers){
									for(String iniciou : instance.getConfig().getStringList("Mensagem.BC.Iniciou")){
										instance.getServer().broadcastMessage(iniciou.replace("{players}", String.valueOf(players.size())).replace("{premio}", NumberFormat.getNumberInstance().format(premio)).replace("&", "§"));
									}
									ocorrendo = true;
									aberto = false;
									for(String pt : players){
										Player tp = instance.getServer().getPlayer(pt);
										pm.TPSpawn(tp);
										if(instance.getConfig().getBoolean("Config.Remove_All_Potion_Effects") && tp.getActivePotionEffects().size() > 0){
											for(PotionEffect pocoes : tp.getActivePotionEffects()){
												tp.removePotionEffect(pocoes.getType());
											}
										}
									}
									Bukkit.getServer().getScheduler().cancelTask(id);
									id2 = scheduler.scheduleSyncRepeatingTask(instance, new Runnable(){
										public void run(){
											if(texplodir > 0){
												if(potator == null){
													potator = instance.getServer().getPlayer(players.get(r.nextInt(players.size())));
													potator.getInventory().addItem(new ItemStack(Material.POISONOUS_POTATO));
													potator.getInventory().setHelmet(new ItemStack(Material.TNT));
													for(String pt : players){
														Player playersevento = instance.getServer().getPlayer(pt);
														playersevento.sendMessage(instance.getConfig().getString("Mensagem.Novo_Potator").replace("&", "§").replace("{player}", potator.getName()));
													}
												}
												for(String tempos : instance.getConfig().getStringList("Config.Tempos")){
													String[] partes = tempos.split("->");
													int tempo = Integer.parseInt(partes[0]);
													String msg = partes[1].replace("&", "§").replace("{tempo}", String.valueOf(texplodir));
													if(texplodir == tempo){
														for(String pt : players){
															Player playersevento = instance.getServer().getPlayer(pt);
															playersevento.sendMessage(msg);
														}
													}
												}
												if(texplodir <= 5 && texplodir > 0){
													for(String pt : players){
														Player playersevento = instance.getServer().getPlayer(pt);
														playersevento.sendMessage(instance.getConfig().getString("Mensagem.Explodindo").replace("&", "§").replace("{tempo}", String.valueOf(texplodir)));
														playersevento.playSound(playersevento.getLocation(), Sound.ORB_PICKUP, 1.0F, 7.0F);
													}
												}
												if(instance.getConfig().getBoolean("Config.Enable_FireWorks")){
													Firework f = (Firework) potator.getWorld().spawnEntity(potator.getLocation(), EntityType.FIREWORK);
													FireworkMeta fm = f.getFireworkMeta();
													Type tp = Type.BALL_LARGE;
													fm.addEffect(FireworkEffect.builder().flicker(false).trail(true).with(tp).withColor(Color.RED).withFade(Color.RED).build());
													fm.setPower(2);
													f.setFireworkMeta(fm);
												}
												texplodir -= 1;
											}else{
												players.remove(potator.getName());
												pm.TPExit(potator);
												potator.getInventory().remove(new ItemStack(Material.POISONOUS_POTATO));
												potator.getInventory().setHelmet(null);
												potator.getWorld().createExplosion(potator.getLocation(), 0.0F);
												for(String pt : players){
													Player playersevento = instance.getServer().getPlayer(pt);
													playersevento.sendMessage(instance.getConfig().getString("Mensagem.Explodiu").replace("&", "§").replace("{player}", potator.getName()));
												}
												if(players.size() == 1){
													Player winner = null;
													for(String vencedor : players){
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
													instance.econ.depositPlayer(winner.getName(), premio);
													ocorrendo = false;
													aberto = false;
													potator = null;
													anuncios = instance.getConfig().getInt("Config.Anuncios");
													tanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
													texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
													minplayers = instance.getConfig().getInt("Config.Min_Players");
													players.clear();
													pm.TPExit(winner);
													instance.getServer().getScheduler().cancelTask(id2);
													for(String ganhador : instance.getConfig().getStringList("Mensagem.BC.Vencedor")){
														instance.getServer().broadcastMessage(ganhador.replace("{vencedor}", winner.getName()).replace("{premio}", NumberFormat.getNumberInstance().format(premio)).replace("&", "§"));
													}
												}else{
													potator.getInventory().setHelmet(null);
													potator = instance.getServer().getPlayer(players.get(r.nextInt(players.size())));
													potator.getInventory().addItem(new ItemStack(Material.POISONOUS_POTATO));
													for(String pt : players){
														Player playersevento = instance.getServer().getPlayer(pt);
														playersevento.sendMessage(instance.getConfig().getString("Mensagem.Novo_Potator").replace("&", "§").replace("{player}", potator.getName()));
													}
													potator.getInventory().setHelmet(new ItemStack(Material.TNT));
													texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
												}
											}
										}
									}, 0, 20*1);
								}else{
									ocorrendo = false;
									aberto = false;
									potator = null;
									anuncios = instance.getConfig().getInt("Config.Anuncios");
									tanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
									texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
									minplayers = instance.getConfig().getInt("Config.Min_Players");
									if(players.size() > 0){
										for(String pt : players){
											Player tp = instance.getServer().getPlayer(pt);
											if(tp.getItemInHand() != null) tp.getItemInHand().setType(Material.AIR);
											if(tp.getInventory().getHelmet() != null) tp.getInventory().setHelmet(null);
											pm.TPExit(tp);
										}
									}
									players.clear();
									Bukkit.getServer().getScheduler().cancelTask(id);
									for(String fechado : instance.getConfig().getStringList("Mensagem.BC.Fechado")){
										instance.getServer().broadcastMessage(fechado.replace("{motivo}", instance.getConfig().getString("Motivo.Poucos_Players")).replace("{minplayers}", String.valueOf(minplayers)).replace("{players}", String.valueOf(players.size())).replace("{premio}", NumberFormat.getNumberInstance().format(premio)).replace("&", "§"));
									}
								}
							}
						}
					}, 0, tanuncios*20);
				}else if(args[0].equalsIgnoreCase("parar")){
					if(!p.hasPermission("dhotpotato.parar")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("{1}", args[0]).replace("{0}", cmd.getName()).replace("&", "§"));
						return true;
					}else if(ocorrendo == false && aberto == false){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Aberto").replace("&", "§"));
						return true;
					}else if(pm.CheckExit() || pm.CheckLobby() || pm.CheckSpawn()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Locais").replace("&", "§"));
						return true;
					}
					texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
					ocorrendo = false;
					aberto = false;
					anuncios = instance.getConfig().getInt("Config.Anuncios");
					tanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
					texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
					minplayers = instance.getConfig().getInt("Config.Min_Players");
					if(players.size() > 0){
						for(String pt : players){
							Player tp = instance.getServer().getPlayer(pt);
							if(tp.getItemInHand() != null) tp.getItemInHand().setType(Material.AIR);
							if(tp.getInventory().getHelmet() != null) tp.getInventory().setHelmet(null);
							pm.TPExit(tp);
						}
					}
					instance.getServer().getScheduler().cancelTask(id2);
					instance.getServer().getScheduler().cancelTask(id);
					players.clear();
					for(String fechado : instance.getConfig().getStringList("Mensagem.BC.Fechado")){
						instance.getServer().broadcastMessage(fechado.replace("{motivo}", instance.getConfig().getString("Motivo.Staff_Cancelou")).replace("{minplayers}", String.valueOf(minplayers)).replace("{players}", String.valueOf(players.size())).replace("{premio}", NumberFormat.getNumberInstance().format(premio)).replace("&", "§"));
					}
					if(potator != null){
						potator.getInventory().clear();
					}
					potator = null;
				}else if(args[0].equalsIgnoreCase("setspawn")){
					if(!p.hasPermission("dhotpotato.setspawn")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("{1}", args[0]).replace("{0}", cmd.getName()).replace("&", "§"));
						return true;
					}
					if(ocorrendo == true || aberto == true){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Esta_Aberto_Ocorrendo").replace("&", "§"));
						return true;
					}
					pm.SetSpawn(p);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Spawn").replace("&", "§").replace("{x}", String.valueOf(df.format(p.getLocation().getX()))).replace("{y}", String.valueOf(df.format(p.getLocation().getY()))).replace("{z}", String.valueOf(df.format(p.getLocation().getZ()))).replace("{world}", p.getLocation().getWorld().getName()));
				}else if(args[0].equalsIgnoreCase("setexit")){
					if(!p.hasPermission("dhotpotato.setexit")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("{1}", args[0]).replace("{0}", cmd.getName()).replace("&", "§"));
						return true;
					}
					if(ocorrendo == true || aberto == true){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Esta_Aberto_Ocorrendo").replace("&", "§"));
						return true;
					}
					pm.SetExit(p);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Saida").replace("&", "§").replace("{x}", String.valueOf(df.format(p.getLocation().getX()))).replace("{y}", String.valueOf(df.format(p.getLocation().getY()))).replace("{z}", String.valueOf(df.format(p.getLocation().getZ()))).replace("{world}", p.getLocation().getWorld().getName()));
				}else if(args[0].equalsIgnoreCase("setlobby")){
					if(!p.hasPermission("dhotpotato.setlobby")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("{1}", args[0]).replace("{0}", cmd.getName()).replace("&", "§"));
						return true;
					}
					if(ocorrendo == true || aberto == true){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Esta_Aberto_Ocorrendo").replace("&", "§"));
						return true;
					}
					pm.SetLobby(p);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Lobby").replace("&", "§").replace("{x}", String.valueOf(df.format(p.getLocation().getX()))).replace("{y}", String.valueOf(df.format(p.getLocation().getY()))).replace("{z}", String.valueOf(df.format(p.getLocation().getZ()))).replace("{world}", p.getLocation().getWorld().getName()));
				}else if(args[0].equalsIgnoreCase("reload")){
					if(!p.hasPermission("dhotpotato.reload")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("{1}", args[0]).replace("{0}", cmd.getName()).replace("&", "§"));
						return true;
					}
					if(ocorrendo == true || aberto == true){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Esta_Aberto_Ocorrendo").replace("&", "§"));
						return true;
					}
					File f = new File(instance.getDataFolder(), "locais.yml");
					YamlConfiguration.loadConfiguration(f);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Reload").replace("&", "§"));
				}else if(args[0].equalsIgnoreCase("entrar")){
					if(!p.hasPermission("dhotpotato.entrar")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("{1}", args[0]).replace("{0}", cmd.getName()).replace("&", "§"));
						return true;
					}else if(aberto == false){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Aberto").replace("&", "§"));
						return true;
					}else if(players.contains(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta").replace("&", "§"));
						return true;
					}
					if(!InvVazio(p)){
						if(!p.hasPermission("dhotpotato.admin")){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Contem_Itens").replace("&", "§"));
							return true;
						}
					}
					if(instance.getConfig().getBoolean("Config.Force_Fly_Off")){
						if(!p.hasPermission("dhotpotato.admin")){
							if(p.getAllowFlight()){
								p.setAllowFlight(false);
								p.setFlying(false);
								p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Fly_Desativado").replace("&", "§"));
							}
						}
					}
					if(instance.getConfig().getBoolean("Config.Force_GM_0")){
						if(!p.hasPermission("dhotpotato.admin")){
							if(p.getGameMode() != GameMode.SURVIVAL){
								p.setGameMode(GameMode.SURVIVAL);
								p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.GM_Changed_0").replace("&", "§"));
							}
						}
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Entrou_No_Evento").replace("&", "§"));
					pm.TPLobby(p);
					players.add(p.getName());
				}else if(args[0].equalsIgnoreCase("sair")){
					if(!p.hasPermission("dhotpotato.sair")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("{1}", args[0]).replace("{0}", cmd.getName()).replace("&", "§"));
						return true;
					}else if(!players.contains(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_No_Evento").replace("&", "§"));
						return true;
					}
					players.remove(p.getName());
					pm.TPExit(p);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Saiu").replace("&", "§"));
					if(ocorrendo == true && players.size() == 1){
						Player winner = null;
						for(String vencedor : players){
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
						instance.econ.depositPlayer(winner.getName(), premio);
						ocorrendo = false;
						aberto = false;
						anuncios = instance.getConfig().getInt("Config.Anuncios");
						tanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
						texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
						minplayers = instance.getConfig().getInt("Config.Min_Players");
						players.clear();
						pm.TPExit(winner);
						instance.getServer().getScheduler().cancelTask(id2);
						instance.getServer().getScheduler().cancelTask(id);
						for(String ganhador : instance.getConfig().getStringList("Mensagem.BC.Vencedor")){
							instance.getServer().broadcastMessage(ganhador.replace("{vencedor}", winner.getName()).replace("{premio}", NumberFormat.getNumberInstance().format(premio)).replace("&", "§"));
						}
					}else{
						if(ocorrendo == true){
							if(potator.getName() == p.getName()){
								potator.getInventory().setHelmet(null);
								potator = instance.getServer().getPlayer(players.get(r.nextInt(players.size())));
								potator.getInventory().addItem(new ItemStack(Material.POISONOUS_POTATO));
								for(String pt : players){
									Player playersevento = instance.getServer().getPlayer(pt);
									playersevento.sendMessage(instance.getConfig().getString("Mensagem.Novo_Potator").replace("&", "§").replace("{player}", potator.getName()));
								}
								potator.getInventory().setHelmet(new ItemStack(Material.TNT));
								texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
							}
						}
					}
				}else{
					sendMessageList(p, instance.getConfig().getStringList("Mensagem.Comandos_Player"));
					if(p.hasPermission("dhotpotato.admin")){
						sendMessageList(p, instance.getConfig().getStringList("Mensagem.Comandos_Admin"));
					}
				}
			}else{
				if(args.length == 0){
					sendMessageListConsole(sender, instance.getConfig().getStringList("Mensagem.Comandos_Console"));
					return true;
				}else if(args[0].equalsIgnoreCase("iniciar")){
					if(ocorrendo == true || aberto == true){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta_Aberto").replace("&", "§"));
						return true;
					}else if(pm.CheckExit() || pm.CheckLobby() || pm.CheckSpawn()){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Locais").replace("&", "§"));
						return true;
					}
					aberto = true;
					premio = instance.getConfig().getDouble("Config.Premio");
					anuncios = instance.getConfig().getInt("Config.Anuncios");
					tanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
					texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
					minplayers = instance.getConfig().getInt("Config.Min_Players");
					final BukkitScheduler scheduler = instance.getServer().getScheduler();
					id = scheduler.scheduleSyncRepeatingTask(instance, new Runnable(){
						public void run(){
							if(anuncios > 0){
								for(String iniciando : instance.getConfig().getStringList("Mensagem.BC.Iniciando")){
									instance.getServer().broadcastMessage(iniciando.replace("{tempo}", String.valueOf(anuncios * tanuncios)).replace("{chamadas}", String.valueOf(anuncios)).replace("{minplayers}", String.valueOf(minplayers)).replace("{players}", String.valueOf(players.size())).replace("{premio}", NumberFormat.getNumberInstance().format(premio)).replace("&", "§"));
								}
								anuncios -= 1;
							}else{
								if(players.size() >= minplayers){
									for(String iniciou : instance.getConfig().getStringList("Mensagem.BC.Iniciou")){
										instance.getServer().broadcastMessage(iniciou.replace("{players}", String.valueOf(players.size())).replace("{premio}", NumberFormat.getNumberInstance().format(premio)).replace("&", "§"));
									}
									ocorrendo = true;
									aberto = false;
									for(String pt : players){
										Player tp = instance.getServer().getPlayer(pt);
										pm.TPSpawn(tp);
										if(instance.getConfig().getBoolean("Config.Remove_All_Potion_Effects") && tp.getActivePotionEffects().size() > 0){
											for(PotionEffect pocoes : tp.getActivePotionEffects()){
												tp.removePotionEffect(pocoes.getType());
											}
										}
									}
									Bukkit.getServer().getScheduler().cancelTask(id);
									id2 = scheduler.scheduleSyncRepeatingTask(instance, new Runnable(){
										public void run(){
											if(texplodir > 0){
												if(potator == null){
													potator = instance.getServer().getPlayer(players.get(r.nextInt(players.size())));
													potator.getInventory().addItem(new ItemStack(Material.POISONOUS_POTATO));
													potator.getInventory().setHelmet(new ItemStack(Material.TNT));
													for(String pt : players){
														Player playersevento = instance.getServer().getPlayer(pt);
														playersevento.sendMessage(instance.getConfig().getString("Mensagem.Novo_Potator").replace("&", "§").replace("{player}", potator.getName()));
													}
												}
												for(String tempos : instance.getConfig().getStringList("Config.Tempos")){
													String[] partes = tempos.split("->");
													int tempo = Integer.parseInt(partes[0]);
													String msg = partes[1].replace("&", "§").replace("{tempo}", String.valueOf(texplodir));
													if(texplodir == tempo){
														for(String pt : players){
															Player playersevento = instance.getServer().getPlayer(pt);
															playersevento.sendMessage(msg);
														}
													}
												}
												if(texplodir <= 5 && texplodir > 0){
													for(String pt : players){
														Player playersevento = instance.getServer().getPlayer(pt);
														playersevento.sendMessage(instance.getConfig().getString("Mensagem.Explodindo").replace("&", "§").replace("{tempo}", String.valueOf(texplodir)));
														playersevento.playSound(playersevento.getLocation(), Sound.ORB_PICKUP, 1.0F, 7.0F);
													}
												}
												if(instance.getConfig().getBoolean("Config.Enable_FireWorks")){
													Firework f = (Firework) potator.getWorld().spawnEntity(potator.getLocation(), EntityType.FIREWORK);
													FireworkMeta fm = f.getFireworkMeta();
													Type tp = Type.BALL_LARGE;
													fm.addEffect(FireworkEffect.builder().flicker(false).trail(true).with(tp).withColor(Color.RED).withFade(Color.RED).build());
													fm.setPower(2);
													f.setFireworkMeta(fm);
												}
												texplodir -= 1;
											}else{
												players.remove(potator.getName());
												pm.TPExit(potator);
												potator.getInventory().remove(new ItemStack(Material.POISONOUS_POTATO));
												potator.getInventory().setHelmet(null);
												potator.getWorld().createExplosion(potator.getLocation(), 0.0F);
												for(String pt : players){
													Player playersevento = instance.getServer().getPlayer(pt);
													playersevento.sendMessage(instance.getConfig().getString("Mensagem.Explodiu").replace("&", "§").replace("{player}", potator.getName()));
												}
												if(players.size() == 1){
													Player winner = null;
													for(String vencedor : players){
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
													instance.econ.depositPlayer(winner.getName(), premio);
													ocorrendo = false;
													aberto = false;
													potator = null;
													anuncios = instance.getConfig().getInt("Config.Anuncios");
													tanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
													texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
													minplayers = instance.getConfig().getInt("Config.Min_Players");
													players.clear();
													pm.TPExit(winner);
													instance.getServer().getScheduler().cancelTask(id2);
													for(String ganhador : instance.getConfig().getStringList("Mensagem.BC.Vencedor")){
														instance.getServer().broadcastMessage(ganhador.replace("{vencedor}", winner.getName()).replace("{premio}", NumberFormat.getNumberInstance().format(premio)).replace("&", "§"));
													}
												}else{
													potator.getInventory().setHelmet(null);
													potator = instance.getServer().getPlayer(players.get(r.nextInt(players.size())));
													potator.getInventory().addItem(new ItemStack(Material.POISONOUS_POTATO));
													for(String pt : players){
														Player playersevento = instance.getServer().getPlayer(pt);
														playersevento.sendMessage(instance.getConfig().getString("Mensagem.Novo_Potator").replace("&", "§").replace("{player}", potator.getName()));
													}
													potator.getInventory().setHelmet(new ItemStack(Material.TNT));
													texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
												}
											}
										}
									}, 0, 20*1);
								}else{
									ocorrendo = false;
									aberto = false;
									potator = null;
									anuncios = instance.getConfig().getInt("Config.Anuncios");
									tanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
									texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
									minplayers = instance.getConfig().getInt("Config.Min_Players");
									if(players.size() > 0){
										for(String pt : players){
											Player tp = instance.getServer().getPlayer(pt);
											if(tp.getItemInHand() != null) tp.getItemInHand().setType(Material.AIR);
											if(tp.getInventory().getHelmet() != null) tp.getInventory().setHelmet(null);
											pm.TPExit(tp);
										}
									}
									players.clear();
									Bukkit.getServer().getScheduler().cancelTask(id);
									for(String fechado : instance.getConfig().getStringList("Mensagem.BC.Fechado")){
										instance.getServer().broadcastMessage(fechado.replace("{motivo}", instance.getConfig().getString("Motivo.Poucos_Players")).replace("{minplayers}", String.valueOf(minplayers)).replace("{players}", String.valueOf(players.size())).replace("{premio}", NumberFormat.getNumberInstance().format(premio)).replace("&", "§"));
									}
								}
							}
						}
					}, 0, tanuncios*20);
				}else if(args[0].equalsIgnoreCase("parar")){
					if(ocorrendo == false && aberto == false){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Aberto").replace("&", "§"));
						return true;
					}else if(pm.CheckExit() || pm.CheckLobby() || pm.CheckSpawn()){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Locais").replace("&", "§"));
						return true;
					}
					texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
					ocorrendo = false;
					aberto = false;
					anuncios = instance.getConfig().getInt("Config.Anuncios");
					tanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
					texplodir = instance.getConfig().getInt("Config.Tempo_Explodir");
					minplayers = instance.getConfig().getInt("Config.Min_Players");
					if(players.size() > 0){
						for(String pt : players){
							Player tp = instance.getServer().getPlayer(pt);
							pm.TPExit(tp);
							if(tp.getItemInHand() != null) tp.getItemInHand().setType(Material.AIR);
							if(tp.getInventory().getHelmet() != null) tp.getInventory().setHelmet(null);
						}
					}
					instance.getServer().getScheduler().cancelTask(id2);
					instance.getServer().getScheduler().cancelTask(id);
					players.clear();
					for(String fechado : instance.getConfig().getStringList("Mensagem.BC.Fechado")){
						instance.getServer().broadcastMessage(fechado.replace("{motivo}", instance.getConfig().getString("Motivo.Staff_Cancelou")).replace("{minplayers}", String.valueOf(minplayers)).replace("{players}", String.valueOf(players.size())).replace("{premio}", NumberFormat.getNumberInstance().format(premio)).replace("&", "§"));
					}
					if(potator != null){
						potator.getInventory().clear();
					}
					potator = null;
				}else if(args[0].equalsIgnoreCase("reload")){
					if(ocorrendo == true || aberto == true){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Esta_Aberto_Ocorrendo").replace("&", "§"));
						return true;
					}
					File f = new File(instance.getDataFolder(), "locais.yml");
					YamlConfiguration.loadConfiguration(f);
					sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Reload").replace("&", "§"));
				}else{
					sendMessageListConsole(sender, instance.getConfig().getStringList("Mensagem.Comandos_Console"));
				}
			}
		}
		return false;
	}
	
	public void sendMessageList(Player p, List<String> msgs){
		for(String msg : msgs){
			p.sendMessage(msg.replace("&", "§"));
		}
	}
	
	public void sendMessageListConsole(CommandSender sender, List<String> msgs){
		for(String msg : msgs){
			sender.sendMessage(msg.replace("&", "§"));
		}
	}

}

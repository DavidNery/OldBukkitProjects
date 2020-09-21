package me.zfork.hjungle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class JungleManager {
	
	private HJungle instance = HJungle.getHJungle();
	private int etapa, tempo, chamadas, tempoanuncios;
	private BukkitTask task;
	private ArrayList<String> players = new ArrayList<String>();
	private ArrayList<Location> locs = new ArrayList<Location>();
	private Random r = new Random();
	
	public void iniciarEvento(CommandSender player){
		if(player instanceof Player && (!player.hasPermission("jungle.admin"))){
			player.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
			return;
		}else if(etapa != 0 || task != null){
			player.sendMessage(instance.getConfig().getString("Mensagem.Erro.Esta_Aberto").replace("&", "§"));
			return;
		}
		tempo = instance.getConfig().getInt("Config.Tempo");
		chamadas = instance.getConfig().getInt("Config.Chamadas");
		tempoanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
		etapa = 1;
		locs.clear();
		for(String locations : instance.getConfig().getStringList("Config.Locs")){
			locs.add(getLocationFromString(locations));
		}
		task = new BukkitRunnable() {
			@Override
			public void run() {
				if(chamadas > 0){
					for(String msg : instance.getConfig().getStringList("Mensagem.Iniciando")){
						for(Player on : instance.getServer().getOnlinePlayers()){
							if(on != null) on.sendMessage(msg.replace("&", "§").replace("@chamadas", chamadas + "").replace("@tempo", (tempoanuncios * chamadas) + "")
									.replace("@players", players.size() + ""));
						}
					}
					chamadas--;
				}else{
					if(players.size() >= instance.getConfig().getInt("Config.Min_Players")){
						etapa = 2;
						for(Player on : instance.getServer().getOnlinePlayers()){
							if(on != null){
								for(String msg : instance.getConfig().getStringList("Mensagem.Iniciou")){
									on.sendMessage(msg.replace("&", "§").replace("@baus", locs.size() + "").replace("@players", players.size() + ""));
								}
								if(containsPlayer(on)) on.teleport(getLocationFromString(instance.getConfig().getString("Config.Spawn")));
							}
						}
						for(Location loc : locs){
							loc.getBlock().setType(Material.CHEST);
							Chest chest = (Chest) loc.getBlock().getState();
							for(int i = 0; i<instance.getConfig().getInt("Config.Itens_Bau_Quantidade"); i++){
								ItemStack item = instance.buildItemStack(instance.getConfig().getStringList("Config.Itens").get(r.nextInt(instance.getConfig().getStringList("Config.Itens").size())));
								if(chest.getInventory().firstEmpty() != -1) chest.getInventory().addItem(item);
							}
						}
						task.cancel();
						task = new BukkitRunnable() {
							@Override
							public void run() {
								if(tempo > 0){
									for(String player : players){
										Player p = instance.getServer().getPlayer(player);
										p.sendMessage(instance.getConfig().getString("Mensagem.Encerrando").replace("&", "§").replace("@tempo", tempo + ""));
									}
									tempo--;
								}else{
									task.cancel();
									task = null;
									for(Player on : instance.getServer().getOnlinePlayers()){
										if(on != null){
											for(String msg : instance.getConfig().getStringList("Mensagem.Encerrado")){
												on.sendMessage(msg.replace("&", "§").replace("@motivo", instance.getConfig().getString("Motivo.Normal")).replace("@players", players.size() + ""));
											}
											if(containsPlayer(on)) on.teleport(getLocationFromString(instance.getConfig().getString("Config.Saida")));
										}
									}
									players.clear();
									for(Location loc : locs){
										Chest chest = (Chest) loc.getBlock().getState();
										chest.getInventory().clear();
										loc.getBlock().setType(Material.AIR);
									}
									etapa = 0;
								}
							}
						}.runTaskTimer(instance, 0, 60*20);
					}else{
						task.cancel();
						task = null;
						for(Player on : instance.getServer().getOnlinePlayers()){
							if(on != null){
								for(String msg : instance.getConfig().getStringList("Mensagem.Encerrado")){
									on.sendMessage(msg.replace("&", "§").replace("@motivo", instance.getConfig().getString("Motivo.Poucos_Players")).replace("@players", players.size() + ""));
								}
								if(containsPlayer(on)) on.teleport(getLocationFromString(instance.getConfig().getString("Config.Saida")));
							}
						}
						players.clear();
						for(Location loc : locs){
							Chest chest = (Chest) loc.getBlock().getState();
							chest.getInventory().clear();
							loc.getBlock().setType(Material.AIR);
						}
						etapa = 0;
					}
				}
			}
		}.runTaskTimer(instance, 0, tempoanuncios*20);
	}
	
	public void pararEvento(CommandSender player){
		if(player instanceof Player && (!player.hasPermission("jungle.admin"))){
			player.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
			return;
		}else if(etapa == 0 || task == null){
			player.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Aberto").replace("&", "§"));
			return;
		}
		task.cancel();
		task = null;
		for(Player on : instance.getServer().getOnlinePlayers()){
			if(on != null){
				for(String msg : instance.getConfig().getStringList("Mensagem.Encerrado")){
					on.sendMessage(msg.replace("&", "§").replace("@motivo", instance.getConfig().getString("Motivo.Staff_Cancelou")).replace("@players", players.size() + ""));
				}
				if(containsPlayer(on)) on.teleport(getLocationFromString(instance.getConfig().getString("Config.Saida")));
			}
		}
		players.clear();
		for(Location loc : locs){
			Chest chest = (Chest) loc.getBlock().getState();
			chest.getInventory().clear();
			loc.getBlock().setType(Material.AIR);
		}
		player.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Cancelou").replace("&", "§"));
		etapa = 0;
	}
	
	public void setEntrada(Player player){
		if(!player.hasPermission("jungle.admin")){
			player.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
			return;
		}
		instance.getConfig().set("Config.Spawn", getStringFromLocation(player.getLocation()));
		instance.saveConfig();
		instance.reloadConfig();
		player.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Entrada_Setada").replace("&", "§"));
	}
	
	public void setSaida(Player player){
		if(!player.hasPermission("jungle.admin")){
			player.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
			return;
		}
		instance.getConfig().set("Config.Saida", getStringFromLocation(player.getLocation()));
		instance.saveConfig();
		instance.reloadConfig();
		player.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Saida_Setada").replace("&", "§"));
	}
	
	public void setLobby(Player player){
		if(!player.hasPermission("jungle.admin")){
			player.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
			return;
		}
		instance.getConfig().set("Config.Lobby", getStringFromLocation(player.getLocation()));
		instance.saveConfig();
		instance.reloadConfig();
		player.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Lobby_Setado").replace("&", "§"));
	}
	
	public void addPlayer(Player player){
		if(etapa == 0){
			player.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Aberto").replace("&", "§"));
			return;
		}else if(containsPlayer(player)){
			player.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta").replace("&", "§"));
			return;
		}else if(player.isDead()){
			player.sendMessage(instance.getConfig().getString("Mensagem.Erro.Esta_Morto").replace("&", "§"));
			return;
		}
		player.teleport(getLocationFromString(instance.getConfig().getString("Config.Lobby")));
		players.add(player.getName().toLowerCase());
		player.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Entrou").replace("&", "§"));
	}
	
	public void delPlayer(Player player){
		if(etapa == 0){
			player.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Aberto").replace("&", "§"));
			return;
		}else if(!containsPlayer(player)){
			player.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta").replace("&", "§"));
			return;
		}else if(player.isDead()){
			player.sendMessage(instance.getConfig().getString("Mensagem.Erro.Esta_Morto").replace("&", "§"));
			return;
		}
		players.remove(player.getName().toLowerCase());
		player.teleport(getLocationFromString(instance.getConfig().getString("Config.Saida")));
		player.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Saiu").replace("&", "§"));
	}
	
	public void delPlayerNormalmente(Player player){
		players.remove(player.getName().toLowerCase());
		player.teleport(getLocationFromString(instance.getConfig().getString("Config.Saida")));
	}
	
	public void setBau(Player player){
		if(!player.hasPermission("jungle.admin")){
			player.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
			return;
		}
		List<String> locations = instance.getConfig().getStringList("Config.Locs");
		locations.add(getStringFromLocation(player.getLocation()));
		instance.getConfig().set("Config.Locs", locations);
		instance.saveConfig();
		instance.reloadConfig();
		player.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Bau_Adicionado").replace("&", "§"));
	}
	
	public void infoEvento(CommandSender sender){
		String status = "";
		if(etapa == 0)
			status = "fechado";
		else if(etapa == 1)
			status = "iniciando";
		else
			status = "em andamento";
		sender.sendMessage(instance.getConfig().getString("Mensagem.Info").replace("&", "§").replace("@status", status).replace("@players", players.size() + ""));
	}
	
	public boolean containsPlayer(Player player){
		if(players.contains(player.getName().toLowerCase())) return true;
		return false;
	}
	
	public Location getLocationFromString(String location){
		String[] partes = location.split(" ");
		Location loc = new Location(instance.getServer().getWorld("world"), 0, 0, 0, 0, 0);
		loc.setWorld(instance.getServer().getWorld(partes[0]));
		loc.setX(Double.valueOf(partes[1]));
		loc.setY(Double.valueOf(partes[2]));
		loc.setZ(Double.valueOf(partes[3]));
		loc.setYaw(Float.valueOf(partes[1]));
		loc.setPitch(Float.valueOf(partes[1]));
		return loc;
	}
	
	public String getStringFromLocation(Location loc){
		StringBuilder sb = new StringBuilder();
		sb.append(loc.getWorld().getName() + " ");
		sb.append(loc.getX() + " ");
		sb.append(loc.getY() + " ");
		sb.append(loc.getZ() + " ");
		sb.append(loc.getYaw() + " ");
		sb.append(loc.getPitch());
		return sb.toString();
	}
	
	public int getEstapa(){
		return this.etapa;
	}

}

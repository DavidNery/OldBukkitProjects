package me.dery.hspleef;

import java.text.NumberFormat;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SpleefManager{
	
	public static HSpleef instance = HSpleef.getHSpleef();
	public static ArrayList<String> players = new ArrayList<String>();
	public static int estagio, minplayers, premio, anuncios, tempoanuncios;
	public static Location spawn, exit, lobby, loc1, loc2;
	public static BukkitTask id, id2;
	public static Cuboid cuboid;
	
	public static BukkitTask getId(){
		return id;
	}
	
	public static BukkitTask getID2(){
		return id2;
	}
	
	public static int getEstagio(){
		return estagio;
	}
	
	public static int getMinPlayers(){
		return minplayers;
	}
	
	public static int getPremio(){
		return premio;
	}
	
	public static int getAnuncios(){
		return anuncios;
	}
	
	public static int getTempoAnuncios(){
		return tempoanuncios;
	}
	
	public static Location getSpawn(){
		return spawn;
	}
	
	public static Location getExit(){
		return exit;
	}
	
	public static Location getLobby(){
		return lobby;
	}
	
	public static Location getLoc1(){
		return loc1;
	}
	
	public static Location getLoc2(){
		return loc2;
	}
	
	public static ArrayList<String> getPlayers(){
		return players;
	}
	
	public static Cuboid getCuboid(){
		return cuboid;
	}
	
	public static boolean isVazio(Player p){
		for(ItemStack item : p.getInventory().getContents()){
			if(item != null && item.getType() != Material.AIR) return true;
		}
		for(ItemStack item : p.getInventory().getArmorContents()){
			if(item != null && item.getType() != Material.AIR) return true;
		}
		return false;
	}
	
	public static void Iniciar(){
		estagio = 1;
		minplayers = instance.getConfig().getInt("Config.Min_Players");
		premio = instance.getConfig().getInt("Config.Premio");
		anuncios = instance.getConfig().getInt("Config.Anuncios");
		tempoanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
		spawn = getLocation("Locais.Spawn");
		lobby = getLocation("Locais.Lobby");
		exit = getLocation("Locais.Exit");
		loc1 = getLocation("Locais.Loc1");
		loc2 = getLocation("Locais.Loc2");
		cuboid = new Cuboid(loc1, loc2);
		id = new BukkitRunnable() {
			@Override
			public void run(){
				if(anuncios != 0){
					for(String msg : instance.getConfig().getStringList("Mensagem.Iniciando")){
						for(Player p : instance.getServer().getOnlinePlayers()){
							p.sendMessage(msg.replace("&", "§").replace("@players", players.size() + "").replace("@premio", NumberFormat.getCurrencyInstance().format(premio).replace("$", ""))
									.replace("@anuncios", anuncios + "").replace("@tempo", (anuncios * tempoanuncios) + ""));
						}
					}
					anuncios--;
				}else{
					id.cancel();
					if(players.size() >= minplayers){
						for(String msg : instance.getConfig().getStringList("Mensagem.Iniciou")){
							for(Player p : instance.getServer().getOnlinePlayers()){
								p.sendMessage(msg.replace("&", "§").replace("@players", players.size() + "").replace("@premio", NumberFormat.getCurrencyInstance().format(premio).replace("$", "")));
							}
						}
						estagio = 2;
						ItemStack pa = new ItemStack(Material.IRON_SPADE);
						pa.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
						ItemMeta pameta = pa.getItemMeta();
						pameta.setDisplayName("§b§lSPLEEF");
						pa.setItemMeta(pameta);
						for(String player : players){
							Player pl = instance.getServer().getPlayer(player);
							pl.getInventory().addItem(pa);
							pl.teleport(spawn);
						}
						id2 = new BukkitRunnable() {
							@Override
							public void run(){
								for(Block blocks : cuboid.getBlocks()){
									blocks.setType(Material.SNOW_BLOCK);
								}
							}
						}.runTaskTimer(instance, 0, 30*20);
					}else{
						for(String msg : instance.getConfig().getStringList("Mensagem.Cancelado")){
							for(Player p : instance.getServer().getOnlinePlayers()){
								p.sendMessage(msg.replace("&", "§").replace("@motivo", instance.getConfig().getString("Motivos.Poucos_Players")));
							}
						}
						for(String player : players){
							Player pl = instance.getServer().getPlayer(player);
							pl.teleport(exit);
						}
						estagio = 0;
						resetVariaveis();
						for(Block blocks : cuboid.getBlocks()){
							blocks.setType(Material.BEDROCK);
						}
					}
				}
			}
		}.runTaskTimer(instance, 0, tempoanuncios*20);
	}
	
	@SuppressWarnings("deprecation")
	public static void Parar(){
		for(String msg : instance.getConfig().getStringList("Mensagem.Cancelado")){
			for(Player p : instance.getServer().getOnlinePlayers()){
				p.sendMessage(msg.replace("&", "§").replace("@motivo", instance.getConfig().getString("Motivos.Staff_Cancelou")));
			}
		}
		for(String player : players){
			Player pl = instance.getServer().getPlayer(player);
			pl.getInventory().clear();
			pl.getInventory().setArmorContents(null);
			pl.updateInventory();
			pl.teleport(exit);
		}
		for(Block blocks : cuboid.getBlocks()){
			blocks.setType(Material.BEDROCK);
		}
		resetVariaveis();
	}
	
	public static void Entrar(Player p){
		if(players.contains(p.getName())){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta").replace("&", "§"));
			return;
		}
		if(p.isDead()){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Esta_Morto").replace("&", "§"));
			return;
		}
		if(estagio != 1){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Aberto").replace("&", "§"));
			return;
		}
		if(isVazio(p)){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inventario").replace("&", "§"));
			return;
		}
		players.add(p.getName());
		p.teleport(lobby);
		p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Entrou").replace("&", "§"));
	}
	
	@SuppressWarnings("deprecation")
	public static void Sair(Player p){
		if(!players.contains(p.getName())){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta").replace("&", "§"));
			return;
		}
		if(p.isDead()){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Esta_Morto").replace("&", "§"));
			return;
		}
		players.remove(p.getName());
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		p.updateInventory();
		p.teleport(exit);
		p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Saiu").replace("&", "§"));
		hasVencedor(p);
	}
	
	public static String Info(){
		String info = "";
		for(String informacoes : instance.getConfig().getStringList("Mensagem.Info")){
			info += informacoes.replace("&", "§").replace("@players", players.size() + "");
			if(estagio == 0) info = info.replace("@status", "Fechado");
			if(estagio == 1) info = info.replace("@status", "Aberto");
			if(estagio == 2) info = info.replace("@status", "Iniciado");
			info += "\n";
		}
		return info;
	}
	
	public static void setSpawn(Player p){
		setLocation("Spawn", p.getLocation());
	}
	
	public static void setLobby(Player p){
		setLocation("Lobby", p.getLocation());
	}
	
	public static void setExit(Player p){
		setLocation("Exit", p.getLocation());
	}
	
	public static void setLoc1(Location loc){
		setChao(1, loc);
	}
	
	public static void setLoc2(Location loc){
		setChao(2, loc);
	}
	
	public static void setLocation(String setToConfig, Location loc){
		String location = "";
		location += loc.getWorld().getName() + ";";
		location += loc.getX() + ";";
		location += loc.getY() + ";";
		location += loc.getZ() + ";";
		location += loc.getYaw() + ";";
		location += loc.getPitch();
		instance.getConfig().set("Locais." + setToConfig, location);
		instance.saveConfig();
		instance.reloadConfig();
	}
	
	public static void setChao(int parte, Location loc){
		String location = "";
		location += loc.getWorld().getName() + ";";
		location += loc.getX() + ";";
		location += loc.getY() + ";";
		location += loc.getZ();
		instance.getConfig().set("Locais.Loc" + parte, location);
		instance.saveConfig();
		instance.reloadConfig();
	}
	
	public static Location getLocation(String getFromConfig){
		Location loc = new Location(instance.getServer().getWorld("world"), 1.0, 1.0, 1.0);
		String[] partes = instance.getConfig().getString(getFromConfig).split(";");
		loc.setWorld(instance.getServer().getWorld(partes[0]));
		loc.setX(Double.valueOf(partes[1]));
		loc.setY(Double.valueOf(partes[2]));
		loc.setZ(Double.valueOf(partes[3]));
		if(partes.length > 4){
			loc.setYaw(Float.valueOf(partes[4]));
			loc.setPitch(Float.valueOf(partes[5]));
		}
		return loc;
	}
	
	@SuppressWarnings({ "static-access", "deprecation" })
	public static void hasVencedor(Player player){
		if(estagio == 2){
			if(players.size() == 1){
				Player vencedor = instance.getServer().getPlayer(players.get(0));
				instance.econ.depositPlayer(vencedor.getName(), premio);
				for(String msg : instance.getConfig().getStringList("Mensagem.Vencedor")){
					for(Player p : instance.getServer().getOnlinePlayers()){
						p.sendMessage(msg.replace("&", "§").replace("@premio", NumberFormat.getCurrencyInstance().format(premio).replace("$", ""))
								.replace("@vencedor", vencedor.getName()));
					}
				}
				vencedor.teleport(exit);
				vencedor.getInventory().clear();
				vencedor.getInventory().setArmorContents(null);
				vencedor.updateInventory();
				instance.getConfig().set("Config.Vencedor", vencedor.getName());
				instance.saveConfig();
				instance.reloadConfig();
				resetVariaveis();
				for(Block blocks : cuboid.getBlocks()){
					blocks.setType(Material.BEDROCK);
				}
			}else if(players.size() == 0){
				for(String msg : instance.getConfig().getStringList("Mensagem.Cancelado")){
					for(Player p : instance.getServer().getOnlinePlayers()){
						p.sendMessage(msg.replace("&", "§").replace("@motivo", instance.getConfig().getString("Motivos.Ninguem_Ganhou")));
					}
				}
				resetVariaveis();
				for(Block blocks : cuboid.getBlocks()){
					blocks.setType(Material.BEDROCK);
				}
			}else{
				for(String pl : players){
					Player players = instance.getServer().getPlayer(pl);
					players.sendMessage(instance.getConfig().getString("Mensagem.Player_Eliminado").replace("&", "§").replace("@player", player.getName()));
				}
			}
		}
	}
	
	public static void resetVariaveis(){
		players.clear();
		estagio = 0;
		if(id != null) id.cancel();
		if(id2 != null) id2.cancel();
	}

}

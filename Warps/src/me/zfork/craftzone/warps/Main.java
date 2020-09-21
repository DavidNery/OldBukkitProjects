package me.zfork.craftzone.warps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin{
	
	private String PLUGIN_NAME;
	public final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	private FileConfiguration warpsfile;
	private final HashMap<String, BukkitTask> playersindelay = new HashMap<>();
	private final HashMap<String, BukkitTask> playersindelaya = new HashMap<>();
	private final HashMap<String, Long> playersdelay = new HashMap<>();
	private final Random r = new Random();
	
	@Override
	public void onEnable() {
		PLUGIN_NAME = getDescription().getName();
		ConsoleCommandSender sender = getServer().getConsoleSender();
		sender.sendMessage("§3=-=-=-=-= §7" + PLUGIN_NAME + " §3=-=-=-=-=");
		sender.sendMessage(" §bAtivado!");
		sender.sendMessage(" §bVersao: §f" + getDescription().getVersion());
		sender.sendMessage(" §bCriado por §fzFork §bpara o servidor §fCraftZone§b!");
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			sender.sendMessage(" §bConfiguracao padrao criada!");
		}
		File f = new File(getDataFolder(), "warps.yml");
		if(!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				sender.sendMessage(" §4Nao foi possivel criar o arquivo de warps!");
			}
		warpsfile = YamlConfiguration.loadConfiguration(f);
		getServer().getPluginManager().registerEvents(new Eventos(), this);
		getCommand("warp").setExecutor(new Comandos());
		getCommand("warps").setExecutor(new Comandos());
		getCommand("setwarp").setExecutor(new Comandos());
		getCommand("delwarp").setExecutor(new Comandos());
		sender.sendMessage(" §bComandos & Eventos registrados!");
		sender.sendMessage("§3=-=-=-=-= §7" + PLUGIN_NAME + " §3=-=-=-=-=");
	}
	
	@Override
	public void onDisable() {
		ConsoleCommandSender sender = getServer().getConsoleSender();
		sender.sendMessage("§8=-=-=-=-= §7" + PLUGIN_NAME + " §8=-=-=-=-=");
		sender.sendMessage(" §4Desativado!");
		sender.sendMessage(" §4Versao: §f" + getDescription().getVersion());
		sender.sendMessage(" §4Criado por §fzFork §4para o servidor §fCraftZone§4!");
		sender.sendMessage("§8=-=-=-=-= §7" + PLUGIN_NAME + " §8=-=-=-=-=");
	}
	
	public static Main getWarps(){
		return (Main) Bukkit.getServer().getPluginManager().getPlugin("Warps");
	}
	
	public FileConfiguration getWarpsFile(){
		return this.warpsfile;
	}
	
	public void saveWarpsfile() throws FileNotFoundException, IOException, InvalidConfigurationException {
		this.warpsfile.save(new File(getDataFolder(), "warps.yml"));
		this.warpsfile.load(new File(getDataFolder(), "warps.yml"));
	}
	
	public HashMap<String, BukkitTask> getPlayersinDelay(){
		return this.playersindelay;
	}
	
	public HashMap<String, BukkitTask> getPlayersindelayActionBar() {
		return playersindelaya;
	}
	
	public HashMap<String, Long> getPlayersDelay() {
		return this.playersdelay;
	}
	
	public void tpPlayer(Player p, String warp){
		if(warpsfile.getBoolean("Warps." + warp + ".Ativar_Perm") && (!p.hasPermission(getConfig().getString("Config.Permissao_Warps").replace("{warp}", warp)))){
			p.sendMessage(getConfig().getString("Mensagem.Erro.Sem_Permissao_Warp").replace("&", "§").replace("{warp}", warp));
			return;
		}else if(warpsfile.getBoolean("Warps." + warp + ".Ativar_Delay") && getPlayersDelay().containsKey(p.getName().toLowerCase()) && getPlayersDelay().get(p.getName().toLowerCase()) > System.currentTimeMillis()){
			p.sendMessage(getConfig().getString("Mensagem.Erro.Aguarde_Teleportar").replace("&", "§").replace("{warp}", warp).replace("{tempo}", "" + ((getPlayersDelay().get(p.getName().toLowerCase()) - System.currentTimeMillis()) / 1000.0)));
			return;
		}
		final Location warplocation = new Location(getServer().getWorld(warpsfile.getString("Warps." + warp + ".World")), warpsfile.getDouble("Warps." + warp + ".X"), 
				warpsfile.getDouble("Warps." + warp + ".Y"), warpsfile.getDouble("Warps." + warp + ".Z"),(float)warpsfile.getDouble("Warps." + warp + ".Yaw"),
				(float)warpsfile.getDouble("Warps." + warp + ".Pitch"));
		if(warplocation.getWorld() == null){
			p.sendMessage(getConfig().getString("Mensagem.Erro.Mundo_Warp_Invalido").replace("&", "§").replace("{warp}", warp));
			return;
		}
		if(getPlayersinDelay().containsKey(p.getName().toLowerCase())){
			getPlayersinDelay().get(p.getName().toLowerCase()).cancel();
			getPlayersinDelay().remove(p.getName().toLowerCase());
		}
		if(getPlayersindelayActionBar().containsKey(p.getName().toLowerCase())){
			getPlayersindelayActionBar().get(p.getName().toLowerCase()).cancel();
			getPlayersindelayActionBar().remove(p.getName().toLowerCase());
		}
		if(getPlayersDelay().containsKey(p.getName().toLowerCase())) getPlayersDelay().remove(p.getName().toLowerCase());
		boolean perm = p.hasPermission("warps.nodelay." + warp) || p.hasPermission("warps.nodelay");
		if(getConfig().getInt("Config.Tempo_TP") > 0 && !perm){
			TitleBuilder.sendTitle(20, getConfig().getInt("Config.Tempo_TP")*20, 20, 
					getConfig().getString("Mensagem.Sucesso.Teleportando_Title").replace("{warp}", warp).replace("&", "§"),
					getConfig().getString("Mensagem.Sucesso.Teleportando_SubTitle").replace("&", "§"), p);
		}
		getPlayersinDelay().put(p.getName().toLowerCase(), new BukkitRunnable(){
			public void run(){
				p.teleport(warplocation);
				p.sendMessage(getConfig().getString("Mensagem.Sucesso.Teleportado").replace("&", "§").replace("{warp}", warp));
				if(getPlayersinDelay().containsKey(p.getName().toLowerCase())) getPlayersinDelay().remove(p.getName().toLowerCase());
				if(getPlayersindelayActionBar().containsKey(p.getName().toLowerCase())){
					getPlayersindelayActionBar().get(p.getName()).cancel();
					getPlayersindelayActionBar().remove(p.getName().toLowerCase());
				}
				if(warpsfile.getBoolean("Warps." + warp + ".Ativar_Delay") && !perm){
					getPlayersDelay().put(p.getName().toLowerCase(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(getConfig().getInt("Config.Tempo_Entre_TP")));
					new BukkitRunnable(){
						public void run(){
							if(getPlayersDelay().containsKey(p.getName().toLowerCase())) 
								getPlayersDelay().remove(p.getName().toLowerCase());
							if(getConfig().getBoolean("Config.Avisar_Pode_TP_Novamente"))
								p.sendMessage(getConfig().getString("Mensagem.Sucesso.Pode_Teleportar_Novamente").replace("&", "§"));
						}
					}.runTaskLater(getWarps(), getConfig().getInt("Config.Tempo_Entre_TP") * 20);
				}
			}
		}.runTaskLater(this, perm ? 0 : (getConfig().getInt("Config.Tempo_TP") * 20)));
		getPlayersindelayActionBar().put(p.getName(), new BukkitRunnable() {
			int i = getConfig().getInt("Config.Tempo_Entre_TP");
			
			@Override
			public void run() {
				TitleBuilder.sendActionbar(p, getConfig().getString("Mensagem.Sucesso.Teleportando_Action").replace("&", "§").replace("{tempo}", i+""));
				i--;
			}
		}.runTaskTimer(this, 0, 1*20));
		return;
	}
	
	public Object criarItem(String string){
		Object item = null;
		String[] partes = string.split(" ");
		int qnt = 1;
		String tipo = "";
		boolean splash = false;
		int tempo = 1;
		int amplificador = 1;
		try{
			for(String parte : partes){
				if(parte.toLowerCase().startsWith("item:")){
					String[] id = parte.split("item:");
					if(id[1].contains(",")){
						String sorteado = id[1].split(",")[r.nextInt(id[1].split(",").length)];
						if(sorteado.contains(":"))
							item = new ItemStack(Material.getMaterial(Integer.parseInt(sorteado.split(":")[0])), qnt, Byte.parseByte(id[1].split(":")[1]));
						else
							item = new ItemStack(Material.getMaterial(Integer.parseInt(sorteado)), qnt);
					}else{
						if(id[1].contains(":"))
							item = new ItemStack(Material.getMaterial(Integer.parseInt(id[1].split(":")[0])), qnt, Byte.parseByte(id[1].split(":")[1]));
						else
							item = new ItemStack(Material.getMaterial(Integer.parseInt(id[1])), qnt);
					}
				}else if(parte.toLowerCase().startsWith("pocao:")){
					String[] id = parte.split("pocao:");
					if(id[1].contains(","))
						tipo = traduzirPocao(id[1].toLowerCase().split(",")[r.nextInt(id[1].split(",").length)]);
					else
						tipo = traduzirPocao(id[1].toLowerCase());
					item = new Potion(PotionType.getByEffect(PotionEffectType.getByName(tipo)));
					for(String p : partes){
						if(p.toLowerCase().startsWith("splash:")){
							splash = Boolean.parseBoolean(p.toLowerCase().split("splash:")[1]);
							if(splash) ((Potion) item).splash();
						}else if(p.toLowerCase().startsWith("duracao:")){
							tempo = Integer.parseInt(p.toLowerCase().split("duracao:")[1]);
						}else if(p.toLowerCase().startsWith("amplificador:")){
							amplificador = Integer.parseInt(p.toLowerCase().split("amplificador:")[1]);
						}
					}
					item = ((Potion) item).toItemStack(qnt);
					PotionMeta pm = (PotionMeta) ((ItemStack) item).getItemMeta();
					pm.addCustomEffect(new PotionEffect(PotionEffectType.getByName(tipo), tempo*20, amplificador - 1), splash);
					((ItemStack) item).setItemMeta(pm);
				}else if(parte.toLowerCase().startsWith("cabeca:")){
					String[] id = parte.split("cabeca:");
					item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
					SkullMeta meta = (SkullMeta) ((ItemStack) item).getItemMeta();
					if(id[1].contains(","))
						meta.setOwner(id[1].split(",")[r.nextInt(id[1].split(",").length)]);
					else
						meta.setOwner(id[1]);
					((ItemStack) item).setItemMeta(meta);
				}else if(parte.toLowerCase().startsWith("enchants:")){
					String[] enchants = parte.split("enchants:");
					for(String enchant : enchants[1].split(",")){
						String[] partesenchant = enchant.split(":");
						((ItemStack) item).addUnsafeEnchantment(Enchantment.getByName(traduzirEnchant(partesenchant[0])), Integer.parseInt(partesenchant[1]));
					}
				}else if(parte.toLowerCase().startsWith("nome:")){
					ItemMeta im = ((ItemStack) item).getItemMeta();
					im.setDisplayName(parte.split("nome:")[1].replace("_", " ").replace("&", "§"));
					((ItemStack) item).setItemMeta(im);
				}else if(parte.toLowerCase().startsWith("lore:")){
					List<String> lore = new ArrayList<String>();
					for(String l : parte.split("(?i)lore:")[1].split("@")){
						lore.add(l.replace("_", " ").replace("&", "§"));
					}
					ItemMeta im = ((ItemStack) item).getItemMeta();
					im.setLore(lore);
					((ItemStack) item).setItemMeta(im);
				}else if(parte.toLowerCase().startsWith("qnt:")){
					qnt = Integer.parseInt(parte.toLowerCase().split("qnt:")[1]);
					if(item.getClass().equals(ItemStack.class)){
						((ItemStack) item).setAmount(qnt);
					}else{
						((Potion) item).toItemStack(qnt);
					}
				}else if(parte.toLowerCase().startsWith("chance:")){
					if((Math.random()*100) > Integer.parseInt(parte.toLowerCase().split("chance:")[1])){
						return null;
					}
				}
			}
			return item;
		}catch(Exception e){
			return null;
		}	
	}

	public String traduzirEnchant(String enchant){
		String en = "";
		switch(enchant.toLowerCase()){
		case "protecao":
			en = "PROTECTION_ENVIRONMENTAL";
			break;
		case "protecao_fogo":
			en = "PROTECTION_FIRE";
			break;
		case "protecao_queda":
			en = "PROTECTION_FALL";
			break;
		case "protecao_explosao":
			en = "PROTECTION_EXPLOSIONS";
			break;
		case "protecao_flecha":
			en = "PROTECTION_PROJECTILE";
			break;
		case "respiracao":
			en = "OXYGEN";
			break;
		case "afinidade_aquatica":
			en = "WATER_WORKER";
			break;
		case "espinhos":
			en = "THORNS";
			break;
		case "afiada":
			en = "DAMAGE_ALL";
			break;
		case "julgamento":
			en = "DAMAGE_UNDEAD";
			break;
		case "ruina_artropodes":
			en = "DAMAGE_ARTHROPODS";
			break;
		case "repulsao":
			en = "KNOCKBACK";
			break;
		case "aspecto_flamejante":
			en = "FIRE_ASPECT";
			break;
		case "pilhagem":
			en = "LOOT_BONUS_MOBS";
			break;
		case "eficiencia":
			en = "DIG_SPEED";
			break;
		case "toque_suave":
			en = "SILK_TOUCH";
			break;
		case "inquebravel":
			en = "DURABILITY";
			break;
		case "fortuna":
			en = "LOOT_BONUS_BLOCKS";
			break;
		case "forca":
			en = "ARROW_DAMAGE";
			break;
		case "impacto":
			en = "ARROW_KNOCKBACK";
			break;
		case "chama":
			en = "ARROW_FIRE";
			break;
		case "infinidade":
			en = "ARROW_INFINITE";
			break;
		}
		return en;
	}

	public String traduzirPocao(String pocao){
		String po = "";
		switch(pocao.toLowerCase()){
		case "velocidade":
			po = "SPEED";
			break;
		case "forca":
			po = "INCREASE_DAMAGE";
			break;
		case "lentidao":
			po = "SLOW";
			break;
		case "escavar-rapido":
			po = "FAST_DIGGING";
			break;
		case "escavar-lento":
			po = "SLOW_DIGGING";
			break;
		case "vida-instantanea":
			po = "HEAL";
			break;
		case "dano-instantaneo":
			po = "HARM";
			break;
		case "pulo":
			po = "JUMP";
			break;
		case "nausea":
			po = "CONFUSION";
			break;
		case "regeneracao":
			po = "REGENERATION";
			break;
		case "resistencia":
			po = "DAMAGE_RESISTANCE";
			break;
		case "resistencia-fogo":
			po = "FIRE_RESISTANCE";
			break;
		case "resistencia-agua":
			po = "WATER_BREATHING";
			break;
		case "invisibilidade":
			po = "INVISIBILITY";
			break;
		case "cegueira":
			po = "BLINDNESS";
			break;
		case "visao-noturna":
			po = "NIGHT_VISION";
			break;
		case "fome":
			po = "HUNGER";
			break;
		case "fraqueza":
			po = "WEAKNESS";
			break;
		case "veneno":
			po = "POISON";
			break;
		}
		return po;
	}

	public String getTime(long millis){		
		long days = TimeUnit.MILLISECONDS.toDays(millis);
		long hour = TimeUnit.MILLISECONDS.toHours(millis-TimeUnit.DAYS.toMillis(days));
		long min = TimeUnit.MILLISECONDS.toMinutes((millis-TimeUnit.DAYS.toMillis(days))-TimeUnit.HOURS.toMillis(hour));
		StringBuilder msg = new StringBuilder();
		if(days > 0)
			msg.append(days + " " + (days == 1 ? "Dia" : "Dias"));
		if(hour > 0 )
			msg.append(", " + hour + " " + (hour == 1 ? "hora" : "horas"));
		if(min > 0)
			msg.append(", " + min + " " + (min == 1 ? "minuto" : "minutos"));
		if(msg.toString().contains(",")){
			try{
				msg = msg.replace(msg.lastIndexOf(","), msg.lastIndexOf(",")+1, " e");
			}catch(StringIndexOutOfBoundsException ex){
				return "agora";
			}
		}
		return msg.toString();
	}

}

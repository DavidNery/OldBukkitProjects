package me.zfork.fvip;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import me.zfork.fvip.vendakey.VendaKey;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

public class FVip extends JavaPlugin{

	public static Economy econ = null;
	private SQLite sqlite;
	private String[] comandos = {"darvip", "tirarvip", "diasvip", "gerarkey", "removerkey", "keys", "usarkey", "ativarvip", "keyinfo", "trocarvip", "setcabeca"
			, "setsign", "venderkey", "comprarkey", "cancelarvenda", "desbanirvip"};
	private HashMap<String, Long> tempo = new HashMap<String, Long>();
	private HashMap<String, Long> vendakey = new HashMap<String, Long>();
	private HashMap<String, VendaKey> tasks = new HashMap<String, VendaKey>();
	private ArrayList<String> perderam = new ArrayList<String>();
	private Random r = new Random();
	private FileConfiguration fc = null;
	private String mailSMTPServer, mailSMTPServerPort, from, fromName, senha, assunto, mensagem;

	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bFVIP§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bzFork");
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
			File f = new File(getDataFolder(), "cabecas.yml");
			if(!f.exists()){
				try {
					f.createNewFile();
					
					getServer().getConsoleSender().sendMessage(" §3Cabecas: §bArquivo das cabecas criados!");
				} catch (IOException e) {
					getServer().getConsoleSender().sendMessage(" §3Cabecas: §bNao foi possivel criar o arquivo!");
				}
			}
			fc = YamlConfiguration.loadConfiguration(f);
			setupEconomy();
			try {
				this.sqlite = new SQLite(this);
			} catch (ClassNotFoundException | SQLException e) {}
			Comandos cmds = new Comandos();
			for(String cmd : comandos){
				getCommand(cmd).setExecutor(cmds);
			}
			Bukkit.getPluginManager().registerEvents(new Eventos(), this);
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						sqlite.openConnection();
						if(getConfig().getBoolean("Config.Debug.Ativar")){
							if(getConfig().getBoolean("Config.Debug.Staffs"))
								for(Player on : Bukkit.getServer().getOnlinePlayers())
									if(on.hasPermission("fvip.admin"))
										on.sendMessage(getConfig().getString("Config.Debug.Removendo").replace("&", "§"));
							if(getConfig().getBoolean("Config.Debug.Console"))
								getServer().getConsoleSender().sendMessage(getConfig().getString("Config.Debug.Removendo").replace("&", "§"));
						}
						sqlite.removeVIPs();
						Map<String, String> players = sqlite.getPlayers();
						if(players.size() > 0){
							for(Map.Entry<String, String> playersEntry : players.entrySet()){
								String player = playersEntry.getKey();
								Player p = Bukkit.getServer().getPlayer(player);
								Bukkit.broadcastMessage(getConfig().getString("Mensagem.Sucesso.Player_Perdeu_VIP").replace("&", "§")
										.replace("{player}", p == null ? player : p.getName()).replace("{vip}", players.get(player)));
								
								for(String s : getConfig().getStringList("Config.Comandos_Executados_Perder"))
									Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), s.replace("{player}", p == null ? player : p.getName())
											.replace("{grupo}", playersEntry.getValue()));
								if(p == null)
									perderam.add(player);
								else
									for(String msg : getConfig().getStringList("Mensagem.Sucesso.Acabou_VIP"))
										p.sendMessage(msg.replace("&", "§").replace("{player}", p.getName()).replace("{vip}", players.get(player)));
							}
						}
						sqlite.closeConnection();
						if(getConfig().getBoolean("Config.Debug.Ativar")){
							if(getConfig().getBoolean("Config.Debug.Staffs"))
								for(Player on : Bukkit.getServer().getOnlinePlayers())
									if(on.hasPermission("fvip.admin"))
										on.sendMessage(getConfig().getString("Config.Debug.Removeu").replace("&", "§"));
							if(getConfig().getBoolean("Config.Debug.Console"))
								getServer().getConsoleSender().sendMessage(getConfig().getString("Config.Debug.Removeu").replace("&", "§"));
						}
					}catch(ClassNotFoundException | SQLException e){}
				}
			}.runTaskTimerAsynchronously(this, 0, getConfig().getInt("Config.Tempo_Verificar")*20);
			updateConfig();
			this.mailSMTPServer = getConfig().getString("Config.Mail.SMTPServer");
			this.mailSMTPServerPort = getConfig().getString("Config.Mail.SMTPServerPort");
			this.from = getConfig().getString("Config.Mail.Email");
			this.fromName = getConfig().getString("Config.Mail.Nome");
			this.senha = getConfig().getString("Config.Mail.Senha");
			this.assunto = getConfig().getString("Config.Mail.Assunto");
			for(String s : getConfig().getStringList("Config.Mail.Mensagem"))
				this.mensagem += s;
		}
		getServer().getConsoleSender().sendMessage("§3==========[§bFVIP§3]==========");
	}

	public void onDisable(){
		HandlerList.unregisterAll(this);
		getServer().getConsoleSender().sendMessage("§4==========[§cFVIP§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §czFork");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cFVIP§4]==========");
	}

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}

	public static FVip getFVIP(){
		return (FVip) Bukkit.getServer().getPluginManager().getPlugin("FClubWarsVIP");
	}

	public SQLite getSQLite(){
		return this.sqlite;
	}

	public HashMap<String, Long> getTempo(){
		return this.tempo;
	}
	
	public HashMap<String, Long> getVendakey(){
		return this.vendakey;
	}
	
	public HashMap<String, VendaKey> getTasks(){
		return this.tasks;
	}

	public ArrayList<String> getPerderam(){
		return this.perderam;
	}
	
	public FileConfiguration getCabecas(){
		return this.fc;
	}
	
	public String getMailSMTPServer() {
		return mailSMTPServer;
	}
	
	public String getMailSMTPServerPort() {
		return mailSMTPServerPort;
	}
	
	public String getFrom() {
		return from;
	}
	
	public String getFromName() {
		return fromName;
	}
	
	public String getSenha() {
		return senha;
	}
	
	public String getAssunto() {
		return assunto;
	}
	
	public String getMensagem() {
		return mensagem;
	}
	
	private void updateConfig(){
		boolean update = false;
		List<String> keys = new ArrayList<>();
		YamlConfiguration finalyml = new YamlConfiguration();
		try {
			finalyml.load(new File(getDataFolder(), "config.yml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileConfiguration tempConfig = YamlConfiguration.loadConfiguration(getResource("config.yml"));
		for(String key : tempConfig.getKeys(true)){
			Object obj = tempConfig.get(key);
			if(finalyml.get(key) != null){
				obj = finalyml.get(key);
			}
			if(!finalyml.contains(key)){
				keys.add(key);
				finalyml.set(key, obj);
				update = true;
			}
		}
		if(update){
			try {
				finalyml.save(new File(getDataFolder(), "config.yml"));
				finalyml.load(new File(getDataFolder(), "config.yml"));
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
			getServer().getConsoleSender().sendMessage(" §4Config atualizada!");
			for(String k : keys){
				String[] partes = k.split("\\.");
				getServer().getConsoleSender().sendMessage("  §4Elemento §7" + partes[partes.length-1] + " §4adicionado a §7" + partes[partes.length-2] + " §4na config!" );
			}
		}
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
			msg.append(days + " " + (days == 1 ? "Dia" : "Dias") + ", ");
		if(hour > 0)
			msg.append(hour + " " + (hour == 1 ? "hora" : "horas") + ", ");
		if(min > 0)
			msg.append(min + " " + (min == 1 ? "minuto" : "minutos") + ", ");
		if(msg.toString().endsWith(", "))
			msg.delete(msg.length()-2, msg.length());
		if(msg.toString().contains(",")){
			try{
				msg = msg.replace(msg.lastIndexOf(","), msg.lastIndexOf(",")+1, " e");
			}catch(StringIndexOutOfBoundsException ex){
				return getConfig().getString("Mensagem.Sucesso.Menos_De_Um_Minuto");
			}
		}
		return msg.toString();
	}

}

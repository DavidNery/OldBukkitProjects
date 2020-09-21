package me.zfork.ftesouros;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import me.zfork.ftesouros.database.MySQL;
import me.zfork.ftesouros.database.SQLite;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class FTesouros extends JavaPlugin{
	
	private Random r = new Random();
	private int SQLType;
	private MySQL mysql;
	private SQLite sqlite;
	
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bFTesouros§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bzFork");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null){
			getServer().getConsoleSender().sendMessage(" §3mcMMO: §bNao Encontrado");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}else{
			getServer().getConsoleSender().sendMessage(" §3mcMMO: §bHooked (Skills)");
			if(!new File(getDataFolder(), "config.yml").exists()){
				saveDefaultConfig();
				getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
			}else{
				getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
			}
			try {
				//if(CheckKey()){
					getCommand("tesouro").setExecutor(new Comandos());
					Bukkit.getServer().getPluginManager().registerEvents(new Eventos(), this);
					if(getConfig().getInt("Config.SQL.Tipo") == 1){
						try {
							this.mysql = new MySQL(this, getConfig().getString("Config.SQL.Usuario"), getConfig().getString("Config.SQL.Senha"), 
									getConfig().getString("Config.SQL.DataBase"), getConfig().getString("Config.SQL.Host"));
							getServer().getConsoleSender().sendMessage(" §3MySQL: §bOK!");
							this.SQLType = 1;
						} catch (ClassNotFoundException | SQLException e) {
							try {
								this.sqlite = new SQLite(this);
								getServer().getConsoleSender().sendMessage(" §3MySQL: §4Nao foi possivel fazer conexao.");
								this.SQLType = 2;
							} catch (ClassNotFoundException | SQLException e1) {
							}
						}
					}else{
						try {
							this.sqlite = new SQLite(this);
							getServer().getConsoleSender().sendMessage(" §3SQLite: §bOK!");
							this.SQLType = 2;
						} catch (ClassNotFoundException | SQLException e) {
						}
					}
//				}else{
//					return;
//				}
			} catch (Exception e) {
				getServer().getConsoleSender().sendMessage(" §4Ocorreu um erro ao verificar sua key!");
				getServer().getConsoleSender().sendMessage("§3==========[§bFTesouros§3]==========");
				Bukkit.getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}
		getServer().getConsoleSender().sendMessage("§3==========[§bFTesouros§3]==========");
	}

	public void onDisable(){
		HandlerList.unregisterAll(this);
		getServer().getConsoleSender().sendMessage("§4==========[§cFTesouros§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §czFork");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cFTesouros§4]==========");
	}

	public static FTesouros getFTesouros(){
		return (FTesouros) Bukkit.getServer().getPluginManager().getPlugin("FTesouros");
	}
	
	public int getSQLType(){
		return this.SQLType;
	}
	
	public MySQL getMySQL(){
		return this.mysql;
	}
	
	public SQLite getSQLite(){
		return this.sqlite;
	}
	
	/*public boolean CheckKey() throws Exception{
		URL url = new URL("http://pluginsdodery.esy.es/ftesouros.php?key=" + getConfig().getString("Config.Key") + "&ip=" + 
				InetAddress.getLocalHost().getHostAddress().replaceAll("\\s+", "") + ":" + Bukkit.getServer().getPort());
		URLConnection connection = null;
		connection = url.openConnection();
		connection.connect();
		connection.setReadTimeout(5000);
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String key = buffReader.readLine();
		if (key == null){
			buffReader.close();
			getServer().getConsoleSender().sendMessage(" §4Ocorreu um erro ao verificar sua key!");
			getServer().getConsoleSender().sendMessage("§3==========[§bFTesouros§3]==========");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return false;
		}
		if(!key.equalsIgnoreCase("true")){
			buffReader.close();
			getServer().getConsoleSender().sendMessage(" §3Seu IP: §b" + 
					InetAddress.getLocalHost().getHostAddress().replaceAll("\\s+", "") + ":" + Bukkit.getServer().getPort());
			getServer().getConsoleSender().sendMessage(" §4" + key);
			getServer().getConsoleSender().sendMessage("§3==========[§bFTesouros§3]==========");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return false;
		}
		return true;
	}*/
	
	public String getTime(long time) {
		String format = "";
		long hours = TimeUnit.MILLISECONDS.toHours(time);
		long hoursInMillis = TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(time - hoursInMillis);
		long minutesInMillis = TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(time - (hoursInMillis + minutesInMillis));
		if (hours > 0)
			format = hours + (hours > 1 ? " horas" : " hora");
		if (minutes > 0) {
			if ((seconds > 0) && (hours > 0))
				format += ", ";
			else if (hours > 0)
				format += " e ";
			format += minutes + (minutes > 1 ? " minutos" : " minuto");
		}
		if (seconds > 0) {
			if ((hours > 0) || (minutes > 0))
				format += " e ";
			format += seconds + (seconds > 1 ? " segundos" : " segundo");
		}
		if (format.equals("")) {
			long rest = time / 100;
			if (rest == 0)
				rest = 1;
			format = "0." + rest + " segundos";
		}
		return format;
	}
	
	public Object criarItem(String string, String player){
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
			            meta.setOwner(id[1].split(",")[r.nextInt(id[1].split(",").length)].replace("{player}", player));
					else
						meta.setOwner(id[1].replace("{player}", player));
					((ItemStack) item).setItemMeta(meta);
				}else if(parte.toLowerCase().startsWith("enchants:")){
					String[] enchants = parte.split("enchants:");
					for(String enchant : enchants[1].split(",")){
						String[] partesenchant = enchant.split(":");
						((ItemStack) item).addUnsafeEnchantment(Enchantment.getByName(traduzirEnchant(partesenchant[0])), Integer.parseInt(partesenchant[1]));
					}
				}else if(parte.toLowerCase().startsWith("nome:")){
					ItemMeta im = ((ItemStack) item).getItemMeta();
					im.setDisplayName(parte.split("nome:")[1].replace("_", " ").replace("&", "§").replace("{player}", player));
					((ItemStack) item).setItemMeta(im);
				}else if(parte.toLowerCase().startsWith("lore:")){
					List<String> lore = new ArrayList<String>();
					for(String l : parte.split("(?i)lore:")[1].split("@")){
						lore.add(l.replace("_", " ").replace("&", "§").replace("{player}", player));
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

}

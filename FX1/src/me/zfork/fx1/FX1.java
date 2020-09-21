package me.zfork.fx1;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import me.zfork.fx1.arenas.ArenaManager;
import me.zfork.fx1.database.MySQL;
import me.zfork.fx1.database.SQLite;
import me.zfork.fx1.kits.KitManager;
import me.zfork.fx1.x1.X1Manager;
import net.milkbowl.vault.economy.Economy;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class FX1 extends JavaPlugin{

	private Economy econ = null;
	private SimpleClans sc = null;
	private SQLite sqlite;
	private MySQL mysql;
	private ArenaManager am;
	private KitManager km;
	private X1Manager xm;
	private HashMap<String, ItemStack[]> inventario;
	private HashMap<String, ItemStack[]> armor;
	private int SQLType;
	private HashMap<String, String> respawn;
	private Inventory arenas;
	private Random r = new Random();

	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bFX1§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bzFork");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null){
			getServer().getConsoleSender().sendMessage(" §3Vault: §bNao Encontrado");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}else{
			getServer().getConsoleSender().sendMessage(" §3Vault: §bHooked (Economy)");
			if(!new File("plugins" + System.getProperty("file.separator") + "FX1").exists()){
				saveResource("arenas/example.yml", false);
			}
			if(!new File(getDataFolder(), "config.yml").exists()){
				saveDefaultConfig();
				getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
			}else{
				getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
			}
			if(CheckKey()){
				setupEconomy();
				if(getConfig().getBoolean("Config.Use_SimpleClans")){
					if(Bukkit.getServer().getPluginManager().getPlugin("SimpleClans") == null){
						getServer().getConsoleSender().sendMessage(" §3SimpleClans: §bNao Encontrado");
						Bukkit.getPluginManager().disablePlugin(this);
						return;
					}else{
						getServer().getConsoleSender().sendMessage(" §3SimpleClans: §bHooked (Clans)");
						sc = (SimpleClans) Bukkit.getServer().getPluginManager().getPlugin("SimpleClans");
					}
				}
				if(!new File(getDataFolder(), "fx1.db").exists()){
					try {
						new File(getDataFolder(), "fx1.db").createNewFile();
					} catch (IOException e) {}
				}
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
				this.am = new ArenaManager(this);
				this.am.loadAllArenas();
				this.km = new KitManager(this);
				this.km.loadAllKits();
				this.xm = new X1Manager(this);
				this.inventario = new HashMap<String, ItemStack[]>();
				this.armor = new HashMap<String, ItemStack[]>();
				this.respawn = new HashMap<String, String>();
				Bukkit.getServer().getPluginManager().registerEvents(new Eventos(), this);
				Bukkit.getServer().getPluginManager().registerEvents(new Comandos(), this);
				this.arenas = Bukkit.createInventory(null, getConfig().getInt("Config.Comando_Arenas.Inv_Size"), 
						getConfig().getString("Config.Comando_Arenas.Inv_Title").replace("&", "§"));
				updateConfig();
			}else{
				return;
			}
		}
		getServer().getConsoleSender().sendMessage("§3==========[§bFX1§3]==========");
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

	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§4==========[§cFX1§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §czFork");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cFX1§4]==========");
	}

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}

	public static FX1 getFX1(){
		return (FX1) Bukkit.getServer().getPluginManager().getPlugin("FX1");
	}

	public synchronized boolean CheckKey(){
		String ip = "";
		String fakeip = "";
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while(interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				if (iface.isLoopback() || !iface.isUp()) continue;
				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				ip = addresses.nextElement().getHostAddress();
				while(addresses.hasMoreElements()){
					InetAddress ia = addresses.nextElement();
					if(ia.getHostAddress().matches("^(\\d{1,3}(\\.\\d{1,3}){3})$"))
						ip = ia.getHostAddress();
					else
						fakeip = ia.getHostAddress();
				}
			}
			/*URL url = new URL("http://pluginsdodery.esy.es/fx1.php?key=" + getConfig().getString("Config.Key") + "&ip=" + 
					ip + ":" + Bukkit.getServer().getPort());*/
			URL url = new URL("http://pluginsdodery.esy.es/checar_plugin.php?plugin=FX1&key=" + getConfig().getString("Config.Key") + "&ip="
					+ (ip.equals("") ? fakeip : ip) + ":" + Bukkit.getServer().getPort());
			URLConnection connection = null;
			connection = url.openConnection();
			connection.connect();
			connection.setReadTimeout(5000);
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String key = buffReader.readLine();
			getServer().getConsoleSender().sendMessage(" §3Seu IP: §b" + 
					(ip.equals("") ? fakeip : ip) + ":" + Bukkit.getServer().getPort());
			buffReader.close();
			if (key == null){
				getServer().getConsoleSender().sendMessage(" §4Nao foi possivel verificar sua key!");
				getServer().getConsoleSender().sendMessage("§3==========[§bFX1§3]==========");
				Bukkit.getServer().getPluginManager().disablePlugin(this);
				return false;
			}
			if(!key.equalsIgnoreCase("true")){
				getServer().getConsoleSender().sendMessage(" §4" + key);
				getServer().getConsoleSender().sendMessage("§3==========[§bFX1§3]==========");
				Bukkit.getServer().getPluginManager().disablePlugin(this);
				return false;
			}
		} catch (Exception e2) {
			e2.printStackTrace();
			getServer().getConsoleSender().sendMessage(" §3Seu IP: §b" + 
					(ip.equals("") ? fakeip : ip) + ":" + Bukkit.getServer().getPort());
			getServer().getConsoleSender().sendMessage(" §4Ocorreu um erro ao verificar sua key!");
			getServer().getConsoleSender().sendMessage("§3==========[§bFX1§3]==========");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return false;
		}
		return true;
	}

	public Economy getEcon(){
		return this.econ;
	}

	@SuppressWarnings("static-access")
	public SimpleClans getSC(){
		return this.sc.getInstance();
	}

	public MySQL getMySQL(){
		return this.mysql;
	}

	public SQLite getSQLite(){
		return this.sqlite;
	}

	public ArenaManager getArenaManager(){
		return this.am;
	}

	public KitManager getKitManager(){
		return this.km;
	}

	public X1Manager getX1Manager(){
		return this.xm;
	}

	public HashMap<String, ItemStack[]> getInventario(){
		return this.inventario;
	}

	public HashMap<String, ItemStack[]> getArmor(){
		return this.armor;
	}

	public HashMap<String, String> getRespawn(){
		return this.respawn;
	}

	public int getSQLType(){
		return this.SQLType;
	}

	public Inventory getArenas(){
		return this.arenas;
	}

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

	public void shootFireWork(Player player) {
		Location loc = player.getLocation();
		Firework fw = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fm = fw.getFireworkMeta();
		fm.addEffect(FireworkEffect.builder().with(getFireworkRandomType()).withColor(getFireworkRandomColor(), getFireworkRandomColor())
				.withFade(getFireworkRandomColor()).flicker(getFireworkRandomBoolean()).build());
		fm.setPower(getFireworkRandomPower());
		fw.setFireworkMeta(fm);
	}

	public Color getFireworkRandomColor() {
		Color c = null;
		Random r = new Random();
		int next = r.nextInt(17);
		if(next == 0) {
			c = Color.AQUA;
		}else if(next == 1) {
			c = Color.BLACK;
		}else if(next == 2) {
			c = Color.BLUE;
		}else if(next == 3) {
			c = Color.FUCHSIA;
		}else if(next == 4) {
			c = Color.GRAY;
		}else if(next == 5) {
			c = Color.GREEN;
		}else if(next == 6) {
			c = Color.LIME;
		}else if(next == 7) {
			c = Color.MAROON;
		}else if(next == 8) {
			c = Color.NAVY;
		}else if(next == 9) {
			c = Color.OLIVE;
		}else if(next == 10) {
			c = Color.ORANGE;
		}else if(next == 11) {
			c = Color.PURPLE;
		}else if(next == 12) {
			c = Color.RED;
		}else if(next == 13) {
			c = Color.SILVER;
		}else if(next == 14) {
			c = Color.TEAL;
		}else if(next == 15) {
			c = Color.WHITE;
		}else if(next == 16) {
			c = Color.YELLOW;
		}
		return c;
	}

	public Type getFireworkRandomType() {
		Type t = null;
		Random r = new Random();
		int next = r.nextInt(5);
		if(next == 0) {
			t = Type.BALL;
		}else if(next == 1) {
			t = Type.BALL_LARGE;
		}else if(next == 2) {
			t = Type.BURST;
		}else if(next == 3) {
			t = Type.CREEPER;
		}else if(next == 4) {
			t = Type.STAR;
		}
		return t;
	}

	public int getFireworkRandomPower() {
		Random r = new Random();
		return r.nextInt(2);
	}

	public Boolean getFireworkRandomBoolean() {
		Random r = new Random();
		return r.nextBoolean();
	}

}

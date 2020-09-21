package me.zfork.farenas;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import me.zfork.farenas.arena.ArenaManager;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.zfork.farenas.Comandos;
import me.zfork.farenas.Listeners;

public class FArenas extends JavaPlugin{

	public Economy econ = null;
	public ArenaManager am;

	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bFArenas§3]==========");
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
			getServer().getConsoleSender().sendMessage(" §3Verificando KEY...");
			try {
				CheckKey();
			} catch (Exception e) {
				getServer().getConsoleSender().sendMessage(" §4Ocorreu um erro ao verificar sua key!");
				Bukkit.getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}
	}

	public void onDisable(){
		HandlerList.unregisterAll(this);
		getServer().getConsoleSender().sendMessage("§4==========[§cFArenas§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §czFork");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cFArenas§4]==========");
	}
	
	@SuppressWarnings("static-access")
	public void CheckKey() throws Exception{
		URL url = new URL("http://derydery.esy.es/farenas.txt");
		URLConnection connection = null;
		connection = url.openConnection();
		connection.connect();
		connection.setReadTimeout(5000);
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String key = buffReader.readLine();
		if (key == null){
			buffReader.close();
			getServer().getConsoleSender().sendMessage(" §4Ocorreu um erro ao verificar sua key!");
			getServer().getConsoleSender().sendMessage("§3==========[§bFArenas§3]==========");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if(!key.contains(getConfig().getString("Config.Key"))){
			buffReader.close();
			getServer().getConsoleSender().sendMessage(" §4Key invalida!");
			getServer().getConsoleSender().sendMessage("§3==========[§bFArenas§3]==========");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		String[] partes = key.split(";");
		for(String ips : partes){
			if(ips.contains("-")){
				String key1 = ips.split("-")[0];
				if(getConfig().getString("Config.Key").equals(key1)){
					if(ips.split("-")[1].equals(InetAddress.getLocalHost().getHostAddress().replaceAll("\\s+", ""))){
						if(!getDataFolder().exists()){
							getDataFolder().mkdir();
							if(!new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + "example.yml").exists()){
								saveResource("arenas/example.yml", false);
							}
						}
						if(!new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas").exists()){
							new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas").mkdir();
						}
						if(new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas").listFiles().length > 0){
							am.loadAllArenas();
						}
						setupEconomy();
						getCommand("arena").setExecutor(new Comandos());
						Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
						getServer().getConsoleSender().sendMessage(" §bTudo OK!");
						getServer().getConsoleSender().sendMessage("§3==========[§bFArenas§3]==========");
					}else{
						buffReader.close();
						getServer().getConsoleSender().sendMessage(" §4IP invalido!");
						getServer().getConsoleSender().sendMessage(" §4Seu IP: §c" + InetAddress.getLocalHost().getHostAddress().replaceAll("\\s+", ""));
						getServer().getConsoleSender().sendMessage("§3==========[§bFArenas§3]==========");
						Bukkit.getServer().getPluginManager().disablePlugin(this);
						return;
					}
				}
			}
		}
	}

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			this.econ = (Economy)ec.getProvider();
		}
		return this.econ != null;
	}

	public static FArenas getFArenas(){
		return (FArenas) Bukkit.getServer().getPluginManager().getPlugin("FArenas");
	}

	public ArenaManager getArenaManager(){
		return this.am;
	}

	public Economy getEcon(){
		return this.econ;
	}
	
	public ItemStack buildItemStack(String items){
		String[] partes = items.split("&&");
		String item = partes[0];
		int quantidade = Integer.parseInt(partes[1]);
		ItemStack i = null;
		if(item.contains(":")){
			String id = item.split(":")[0];
			int data = Integer.parseInt(item.split(":")[1]);
			if(Material.matchMaterial(id) != null){
				i = new ItemStack(Material.getMaterial(Integer.parseInt(id)), quantidade, (byte) data);
			}
		}else{
			if(Material.matchMaterial(item) != null){
				i = new ItemStack(Material.getMaterial(Integer.parseInt(item)), quantidade);
			}
		}
		if(!partes[2].replaceAll("\\s+", "").equals("")){
			String[] penchants = partes[2].split("-");
			for(String en : penchants){
				String name = translateEnchantmentToEnglish(en.split(":")[0]);
				int level = Integer.parseInt(en.split(":")[1]);
				i.addUnsafeEnchantment(Enchantment.getByName(name), level);
			}
		}
		if(!partes[3].replaceAll("\\s+", "").equals("")){
			String nome = partes[3];
			ItemMeta im = i.getItemMeta();
			im.setDisplayName(nome.replace("&", "§"));
			i.setItemMeta(im);
		}
		if(!partes[4].replaceAll("\\s+", "").equals("")){
			String[] plore = partes[4].split("_");
			List<String> lore = new ArrayList<String>();
			for(String lores : plore){
				lore.add(lores.replace("&", "§"));
			}
			ItemMeta im = i.getItemMeta();
			im.setLore(lore);
			i.setItemMeta(im);
		}
		return i;
	}
	
	public String translateEnchantmentToEnglish(String enchant){
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

}

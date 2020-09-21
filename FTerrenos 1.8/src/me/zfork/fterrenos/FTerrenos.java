package me.zfork.fterrenos;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class FTerrenos extends JavaPlugin{
	
	public static Economy econ = null;
	public static Permission perm = null;
	private Comandos cmds;
	private TerrenoManager tm;
	private AutoUpdate autoupdate;
	
	public void onEnable(){
		try {
			CheckStatus();
		} catch (IOException e) {
			getServer().getConsoleSender().sendMessage("§4==========[§cFTerrenos§4]==========");
			getServer().getConsoleSender().sendMessage(" §4Nao foi possivel ativar o plugin!");
			getServer().getConsoleSender().sendMessage("§4==========[§cFTerrenos§4]==========");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
		}
	}
	
	public void onDisable(){
		HandlerList.unregisterAll(this);
		getServer().getConsoleSender().sendMessage("§4==========[§cFTerrenos§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §cDery");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cFTerrenos§4]==========");
	}
	
	public String getText(String url) throws Exception {
		URL website = new URL(url);
		URLConnection connection = website.openConnection();
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder response = new StringBuilder();
		String inputLine;
		while ((inputLine = in.readLine()) != null){
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}
	
	public void CheckStatus() throws IOException{
		URL url = new URL("http://derydery.esy.es/fterrenos.txt");
		URLConnection connection = null;
		connection = url.openConnection();
		connection.setReadTimeout(5000);
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String key = buffReader.readLine();
		if (key == null){
			getServer().getConsoleSender().sendMessage(" §4Ocorreu um erro ao verificar o status do plugin!");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if(!key.contains("OK")){
			getServer().getConsoleSender().sendMessage(" §4O plugin esta desativado!");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}else{
			getServer().getConsoleSender().sendMessage("§3==========[§bFTerrenos§3]==========");
			getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
			getServer().getConsoleSender().sendMessage(" §3By: §bDery");
			getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
			if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null){
				getServer().getConsoleSender().sendMessage(" §3Vault: §bNao Encontrado");
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}else{
				getServer().getConsoleSender().sendMessage(" §3Vault: §bHooked (Economy)");
				if(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit") == null){
					getServer().getConsoleSender().sendMessage(" §3WorldEdit: §bNao Encontrado");
					Bukkit.getPluginManager().disablePlugin(this);
					return;
				}else{
					getServer().getConsoleSender().sendMessage(" §3WorldEdit: §bHooked");
					if(Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") == null){
						getServer().getConsoleSender().sendMessage(" §3WorldGuard: §bNao Encontrado");
						Bukkit.getPluginManager().disablePlugin(this);
						return;
					}else{
						getServer().getConsoleSender().sendMessage(" §3WorldGuard: §bHooked (Regions)");
					}
				}
			}
			setupEconomy();
			setupPermissions();
			setupSchematics();
			getCommand("terreno").setExecutor(new Comandos());
			getCommand("terrenos").setExecutor(new Comandos());
			Bukkit.getServer().getPluginManager().registerEvents(new Eventos(), this);
			if(!new File(getDataFolder(), "config.yml").exists()){
				saveDefaultConfig();
				getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
				if(Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx") != null && Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx").isEnabled()){
					try{
						for(String grupos : perm.getGroups()){
							getConfig().set("Config.Grupos." + grupos, 1);
						}
						saveConfig();
						reloadConfig();
					}catch(Exception e){}
				}else if(Bukkit.getServer().getPluginManager().getPlugin("GroupManager") != null && Bukkit.getServer().getPluginManager().getPlugin("GroupManager").isEnabled()){
					try{
						for(String grupos : perm.getGroups()){
							getConfig().set("Config.Grupos." + grupos, 1);
						}
						saveConfig();
						reloadConfig();
					}catch(Exception e){}
				}
			}else{
				if(Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx") != null && Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx").isEnabled()){
					try{
						boolean ok = false;
						for(String grupos : perm.getGroups()){
							if(!getConfig().contains("Config.Grupos." + grupos)){
								getConfig().set("Config.Grupos." + grupos, 1);
								ok = true;
							}
						}
						saveConfig();
						reloadConfig();
						if(ok == true){
							getServer().getConsoleSender().sendMessage(" §3Config: §bGrupos Atualizados!");
						}else{
							getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
						}
					}catch(Exception e){}
				}else if(Bukkit.getServer().getPluginManager().getPlugin("GroupManager") != null && Bukkit.getServer().getPluginManager().getPlugin("GroupManager").isEnabled()){
					try{
						boolean ok = false;
						for(String grupos : perm.getGroups()){
							if(!getConfig().contains("Config.Grupos." + grupos)){
								getConfig().set("Config.Grupos." + grupos, 1);
								ok = true;
							}
						}
						saveConfig();
						reloadConfig();
						if(ok == true){
							getServer().getConsoleSender().sendMessage(" §3Config: §bGrupos Atualizados!");
						}else{
							getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
						}
					}catch(Exception e){}
				}
			}
			updateConfig();
			if(getConfig().getBoolean("Config.Auto_Update")){
				autoupdate = new AutoUpdate("http://derydery.esy.es/fterrenos_update.txt", "http://derydery.esy.es/FTerrenos.rar");
			}
			getServer().getConsoleSender().sendMessage("§3==========[§bFTerrenos§3]==========");
		}
	}
	
	public void setupSchematics(){
		if(!new File(getDataFolder(), "pequeno.schematic").exists()){
			saveResource("pequeno.schematic", false);
		}
		if(!new File(getDataFolder(), "medio.schematic").exists()){
			saveResource("medio.schematic", false);
		}
		if(!new File(getDataFolder(), "grande.schematic").exists()){
			saveResource("grande.schematic", false);
		}
	}

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}
	
	public boolean setupPermissions(){
		RegisteredServiceProvider<Permission> pc = getServer().getServicesManager().getRegistration(Permission.class);
		if(pc!=null){
			perm = (Permission)pc.getProvider();
		}
		return perm != null;
	}
	
	public WorldGuardPlugin getWorldGuard(){
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	    if((plugin == null) || (!(plugin instanceof WorldGuardPlugin))){
	      return null;
	    }
	    return (WorldGuardPlugin) plugin;
	}
	  
	public WorldEditPlugin getWorldEdit(){
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");
	    if((plugin == null) || (!(plugin instanceof WorldEditPlugin))){
	    	return null;
	    }
	    return (WorldEditPlugin) plugin;
	}
	
	public static FTerrenos getFTerrenos(){
		return (FTerrenos) Bukkit.getServer().getPluginManager().getPlugin("FTerrenos");
	}
	
	public Comandos getComandos(){
		return cmds;
	}
	
	public TerrenoManager getTerrenoManager(){
		return tm;
	}
	
	public AutoUpdate getAutoupdate() {
		return autoupdate;
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

}

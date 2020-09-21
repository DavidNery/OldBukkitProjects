package me.zfork.fcraftando;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class FCraftando extends JavaPlugin {
	
	public static Economy econ;
	
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bFCraftando§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bDery");
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
			
		}
		getServer().getConsoleSender().sendMessage("§3==========[§bFCraftando§3]==========");
	}

	public void onDisable(){
		HandlerList.unregisterAll(this);
		getServer().getConsoleSender().sendMessage("§4==========[§cFCraftando§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §cDery");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cFCraftando§4]==========");
	}
	
	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}

	public static FCraftando getFCraftando(){
		return (FCraftando) Bukkit.getServer().getPluginManager().getPlugin("FCraftando");
	}

	public void updateConfig(){
		try{
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
				if(!finalyml.contains(key) && !keys.contains(key)){
					keys.add(key);
					update = true;
				}
				finalyml.set(key, obj);
			}
			if(update){
				finalyml.save(new File(getDataFolder(), "config.yml"));
				finalyml.load(new File(getDataFolder(), "config.yml"));
				getServer().getConsoleSender().sendMessage(" §4Config atualizada!");
				for(String k : keys){
					String[] partes = k.split("\\.");
					getServer().getConsoleSender().sendMessage("  §4Elemento §7" + partes[partes.length-1] + " §4adicionado a §7" + partes[partes.length-2] + " §4na config!" );
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}

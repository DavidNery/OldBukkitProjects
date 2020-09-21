package me.dery.hjokenpo;

import java.io.File;

import me.dery.hjokenpo.desafio.DesafioManager;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HJokenpo extends JavaPlugin{
	
	public static Economy econ = null;
	public static DesafioManager dm;
	public static double premiopadrao;
	
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bHJokenpo§3]==========");
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
			setupEconomy();
			premiopadrao = getConfig().getDouble("Premio_Padrao");
			getCommand("jokenpo").setExecutor(new Comandos());
			Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
		}
		getServer().getConsoleSender().sendMessage("§3==========[§bHJokenpo§3]==========");
	}
	
	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§4==========[§cHJokenpo§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §cDery");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cHJokenpo§4]==========");
	}

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}

	public static HJokenpo getHJokenpo(){
		return (HJokenpo) Bukkit.getServer().getPluginManager().getPlugin("HJokenpo");
	}
	
	public double getPadrao(){
		return premiopadrao;
	}
	
	public DesafioManager getDesafioManager(){
		return dm;
	}

}

package me.dery;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class DTNTRain extends JavaPlugin{
	
	public static Economy econ = null;
	boolean Vault = true;
	
	private TNTRainManager tntrainmanager;
	private Comandos comandos;
	
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bDTNTRain§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bDery");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null){
			getServer().getConsoleSender().sendMessage(" §3Vault: §bNao Encontrado");
			Vault = false;
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
		setupEconomy();
		getCommand("tntrain").setExecutor(new Comandos());
		Bukkit.getServer().getPluginManager().registerEvents(new Eventos(), this);
		if(getConfig().getBoolean("Config.Auto_Start.Ativar")){
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
				public void run(){
					CheckStart();
				}
			}, 0L, 1000L);
		}
	}
	
	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§4==========[§cDTNTRain§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §cDery");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cDTNTRain§4]==========");
	}

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}
	
	@SuppressWarnings("deprecation")
	public boolean CheckStart(){
		Calendar c = Calendar.getInstance();
		Date data = c.getTime();
		List<String> dias = getConfig().getStringList("Config.Auto_Start.Dias");
		String iniciarhora = getConfig().getString("Config.Auto_Start.Hora");
		String[] ih = iniciarhora.split(":");
		int hora = Integer.parseInt(ih[0]);
		int minutos = Integer.parseInt(ih[1]);
		switch(c.get(Calendar.DAY_OF_WEEK)){
		case Calendar.SUNDAY:
			if(dias.contains("Domingo") && data.getHours() == hora && data.getMinutes() == minutos){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tntrain iniciar");
			}
			break;
		case Calendar.MONDAY:
			if(dias.contains("Segunda") && data.getHours() == hora && data.getMinutes() == minutos){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tntrain iniciar");
			}
			break;
		case Calendar.TUESDAY:
			if(dias.contains("Terca") && data.getHours() == hora && data.getMinutes() == minutos){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tntrain iniciar");
			}
			break;
		case Calendar.WEDNESDAY:
			if(dias.contains("Quarta") && data.getHours() == hora && data.getMinutes() == minutos){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tntrain iniciar");
			}
			break;
		case Calendar.THURSDAY:
			if(dias.contains("Quinta") && data.getHours() == hora && data.getMinutes() == minutos){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tntrain iniciar");
			}
			break;
		case Calendar.FRIDAY:
			if(dias.contains("Sexta") && data.getHours() == hora && data.getMinutes() == minutos){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tntrain iniciar");
			}
			break;
		case Calendar.SATURDAY:
			if(dias.contains("Sabado") && data.getHours() == hora && data.getMinutes() == minutos){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tntrain iniciar");
			}
			break;
		}
		return false;
	}
	
	public static DTNTRain getDTNTRain(){
		return (DTNTRain) Bukkit.getServer().getPluginManager().getPlugin("DTNTRain");
	}
	
	public TNTRainManager gettntrm(){
		return tntrainmanager;
	}
	
	public Comandos getcmds(){
		return comandos;
	}

}

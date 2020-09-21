package me.dery.dpulapula;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class DPulaPula extends JavaPlugin{
	
	public static Economy econ = null;
	boolean Vault = true;
	
	private Comandos cmds;
	private Eventos events;
	private PulaPulaManager ppm;
	
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bDPulaPula§3]==========");
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
		getServer().getConsoleSender().sendMessage("§3==========[§bDPulaPula§3]==========");
		setupEconomy();
		getCommand("pulapula").setExecutor(new Comandos());
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
		getServer().getConsoleSender().sendMessage("§4==========[§cDPulaPula§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §cDery");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cDPulaPula§4]==========");
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
		for(String dias : getConfig().getStringList("Config.Auto_Start.Dias")){
			String[] autostart = dias.split(" ");
			String tempo = autostart[1];
			String[] hm = tempo.split(":");
			int hora = Integer.valueOf(hm[0]);
			int minutos = Integer.valueOf(hm[1]);
			switch(c.get(Calendar.DAY_OF_WEEK)){
			case Calendar.SUNDAY:
				if(dias.contains("domingo") && data.getHours() == hora && data.getMinutes() == minutos){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pulapula iniciar");
				}
				break;
			case Calendar.MONDAY:
				if(dias.contains("segunda") && data.getHours() == hora && data.getMinutes() == minutos){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pulapula iniciar");
				}
				break;
			case Calendar.TUESDAY:
				if(dias.contains("terca") && data.getHours() == hora && data.getMinutes() == minutos){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pulapula iniciar");
				}
				break;
			case Calendar.WEDNESDAY:
				if(dias.contains("quarta") && data.getHours() == hora && data.getMinutes() == minutos){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pulapula iniciar");
				}
				break;
			case Calendar.THURSDAY:
				if(dias.contains("quinta") && data.getHours() == hora && data.getMinutes() == minutos){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pulapula iniciar");
				}
				break;
			case Calendar.FRIDAY:
				if(dias.contains("sexta") && data.getHours() == hora && data.getMinutes() == minutos){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pulapula iniciar");
				}
				break;
			case Calendar.SATURDAY:
				if(dias.contains("sabado") && data.getHours() == hora && data.getMinutes() == minutos){
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pulapula iniciar");
				}
				break;
			}
		}
		return false;
	}
	
	public static DPulaPula getDPulaPula(){
		return (DPulaPula) Bukkit.getServer().getPluginManager().getPlugin("DPulaPula");
	}
	
	public Comandos getComandos(){
		return cmds;
	}
	
	public Eventos getEventos(){
		return events;
	}
	
	public PulaPulaManager getPulaPulaManager(){
		return ppm;
	}

}

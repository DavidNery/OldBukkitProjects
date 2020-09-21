package me.dery.dhotpotato;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class DHotPotato extends JavaPlugin{
	
	public static Economy econ = null;
	boolean LegendChat = true;
	
	private PotatoManager potatom;
	private Comandos cmds;
	
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bDHotPotato§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bDery");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
		}else{
			getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
		}
		if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null){
			getServer().getConsoleSender().sendMessage(" §3Vault: §bNao Encontrado");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}else{
			getServer().getConsoleSender().sendMessage(" §3Vault: §bHooked (Economy)");
			if(getConfig().getBoolean("Config.Use_LegendChat")){
				if(Bukkit.getServer().getPluginManager().getPlugin("Legendchat") == null){
					getServer().getConsoleSender().sendMessage(" §3LegendChat: §bNao Encontrado");
					LegendChat = false;
					Bukkit.getPluginManager().disablePlugin(this);
					return;
				}else{
					getServer().getConsoleSender().sendMessage(" §3LegendChat: §bHooked (Tag)");
					Bukkit.getServer().getPluginManager().registerEvents(new LegendChat(), this);
				}
			}
			setupEconomy();
			getCommand("hotpotato").setExecutor(new Comandos());
			Bukkit.getServer().getPluginManager().registerEvents(new Eventos(), this);
			if(getConfig().getBoolean("Config.Auto_Start.Ativar")){
				Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
					public void run(){
						CheckStart();
					}
				}, 0L, 20*60);
			}
		}
		try{
			CheckKey();
		}catch(Exception e){
			getServer().getConsoleSender().sendMessage(" §4Ocorreu um Erro Ao Verificar Sua Key!");
			e.printStackTrace();
		}
		getServer().getConsoleSender().sendMessage("§3==========[§bDHotPotato§3]==========");
	}
	
	public void onDisable(){
		HandlerList.unregisterAll(this);
		getServer().getConsoleSender().sendMessage("§4==========[§cDHotPotato§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §cDery");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cDHotPotato§4]==========");
	}
	
	public void CheckKey() throws Exception{
		URL url = new URL("http://derydery.esy.es/dhotpotato.txt");
		URLConnection connection = null;
		connection = url.openConnection();
		connection.connect();
		connection.setReadTimeout(5000);
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String key = buffReader.readLine();
		if (key == null){
			buffReader.close();
			getServer().getConsoleSender().sendMessage(" §4Ocorreu um erro ao verificar sua key!");
			getServer().getConsoleSender().sendMessage("§3==========[§bDHotPotato§3]==========");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if(!key.contains(getConfig().getString("Config.Key"))){
			buffReader.close();
			getServer().getConsoleSender().sendMessage(" §4Key inválida!");
			getServer().getConsoleSender().sendMessage("§3==========[§bDHotPotato§3]==========");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		String[] partes = key.split(";");
		for(String ips : partes){
			if(ips.contains("-")){
				String key1 = ips.split("-")[0];
				if(getConfig().getString("Config.Key").equals(key1)){
					if(ips.split("-")[1].equals(InetAddress.getLocalHost().getHostAddress().replaceAll("\\s+", ""))){
						getServer().getConsoleSender().sendMessage(" §3Usuario, Key e IP Corretos!");
						setupEconomy();
						getCommand("hotpotato").setExecutor(new Comandos());
						Bukkit.getServer().getPluginManager().registerEvents(new Eventos(), this);
						if(getConfig().getBoolean("Config.Auto_Start.Ativar")){
							Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
								public void run(){
									CheckStart();
								}
							}, 0L, 1000L);
						}
						getServer().getConsoleSender().sendMessage("§3==========[§bDHotPotato§3]==========");
					}else{
						buffReader.close();
						getServer().getConsoleSender().sendMessage(" §4IP inválido!");
						getServer().getConsoleSender().sendMessage(" §4Seu IP: §c" + InetAddress.getLocalHost().getHostAddress().replaceAll("\\s+", ""));
						getServer().getConsoleSender().sendMessage("§3==========[§bDHotPotato§3]==========");
						Bukkit.getServer().getPluginManager().disablePlugin(this);
						return;
					}
				}
			}
		}
	}
	
	public void CheckStart(){
		if(getConfig().getBoolean("Config.Auto_Start.Ativar")){
			Calendar c = Calendar.getInstance();
			for(String dias : getConfig().getStringList("Config.Auto_Start.Dias")){
				String[] autostart = dias.split(" ");
				String tempo = autostart[1];
				String[] hm = tempo.split(":");
				int hora = Integer.valueOf(hm[0]);
				int minutos = Integer.valueOf(hm[1]);
				switch(c.get(Calendar.DAY_OF_WEEK)){
				case Calendar.SUNDAY:
					if(dias.contains("domingo") && c.get(Calendar.HOUR_OF_DAY) == hora && c.get(Calendar.MINUTE) == minutos){
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hotpotato iniciar");
					}
					break;
				case Calendar.MONDAY:
					if(dias.contains("segunda") && c.get(Calendar.HOUR_OF_DAY) == hora && c.get(Calendar.MINUTE) == minutos){
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hotpotato iniciar");
					}
					break;
				case Calendar.TUESDAY:
					if(dias.contains("terca") && c.get(Calendar.HOUR_OF_DAY) == hora && c.get(Calendar.MINUTE) == minutos){
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hotpotato iniciar");
					}
					break;
				case Calendar.WEDNESDAY:
					if(dias.contains("quarta") && c.get(Calendar.HOUR_OF_DAY) == hora && c.get(Calendar.MINUTE) == minutos){
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hotpotato iniciar");
					}
					break;
				case Calendar.THURSDAY:
					if(dias.contains("quinta") && c.get(Calendar.HOUR_OF_DAY) == hora && c.get(Calendar.MINUTE) == minutos){
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hotpotato iniciar");
					}
					break;
				case Calendar.FRIDAY:
					if(dias.contains("sexta") && c.get(Calendar.HOUR_OF_DAY) == hora && c.get(Calendar.MINUTE) == minutos){
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hotpotato iniciar");
					}
					break;
				case Calendar.SATURDAY:
					if(dias.contains("sabado") && c.get(Calendar.HOUR_OF_DAY) == hora && c.get(Calendar.MINUTE) == minutos){
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hotpotato iniciar");
					}
					break;
				}
			}
		}
	}

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}
	
	public static DHotPotato getDHotPotato(){
		return (DHotPotato) Bukkit.getServer().getPluginManager().getPlugin("DHotPotato");
	}
	
	public PotatoManager getPotatoManager(){
		return potatom;
	}
	
	public Comandos getComandos(){
		return cmds;
	}

}

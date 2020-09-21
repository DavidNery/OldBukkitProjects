package me.zfork.fkitspro;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import me.zfork.fkitspro.databases.MySQL;
import me.zfork.fkitspro.databases.SQLite;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FKitsPRO extends JavaPlugin{

	public static Economy econ = null;
	private KitsManager km;
	private Plugin tintacoin;

	private MySQL mysql = null;
	private SQLite sqlite = null;
	
	private Verificadores verificadores;

	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bFKitsPRO§3]==========");
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
			verificadores = new Verificadores(this);
			if(verificadores.CheckKey(getServer().getConsoleSender())){
				setupEconomy();
				this.km = new KitsManager(this);
				if(getConfig().getBoolean("Config.Ativar_Inv")) km.loadInvs();
				km.loadKits();
				tintacoin = getServer().getPluginManager().getPlugin("TintaCoin");
				Bukkit.getServer().getPluginManager().registerEvents(new Eventos(), this);
				updateConfig();
				try {
					if(getConfig().getBoolean("Config.MySQL.Ativar"))
						mysql = new MySQL(getConfig().getString("Config.MySQL.Host"), 
								getConfig().getString("Config.MySQL.Usuario"), getConfig().getString("Config.MySQL.Senha"),
								getConfig().getString("Config.MySQL.Database"));
					else
						sqlite = new SQLite(this);
				} catch (ClassNotFoundException | SQLException | IOException e) {
					e.printStackTrace();
				}
				new BukkitRunnable() {
					@Override
					public void run() {
						try {
							getServer().getConsoleSender().sendMessage(" §3Carregando dados anteriores...");
							if(mysql != null){
								mysql.openConnection();
								mysql.downloadPlayersKits();
								mysql.closeConnection();
							}else{
								sqlite.openConnection();
								sqlite.downloadPlayersKits();
								sqlite.closeConnection();
							}
						} catch (ClassNotFoundException | SQLException e) {
							e.printStackTrace();
						}
						getServer().getConsoleSender().sendMessage(" §3Removendo players antigos...");
						removeKits();
						km.setCanPick(true);
					}
				}.runTaskAsynchronously(this);
				new BukkitRunnable() {
					@Override
					public void run() {
						sendToDataBase();
					}
				}.runTaskTimerAsynchronously(this, 60*20, 60*20);
			}
		}
		getServer().getConsoleSender().sendMessage("§3==========[§bFKitsPRO§3]==========");
	}

	public void onDisable(){
		HandlerList.unregisterAll(this);
		sendToDataBase();
		getServer().getConsoleSender().sendMessage("§4==========[§cFKitsPRO§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §czFork");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cFKitsPRO§4]==========");
	}

	public void updateConfig(){
		boolean update = false;
		List<String> keys = new ArrayList<>();
		YamlConfiguration finalyml = new YamlConfiguration();
		try {
			finalyml.load(new File(getDataFolder(), "config.yml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileConfiguration tempConfig = YamlConfiguration.loadConfiguration(getResource("config.yml"));
		for(String key : finalyml.getKeys(true))
			if(!(key.startsWith("Config.Inventarios") || key.startsWith("Config.Kits")) && tempConfig.get(key) == null){
				finalyml.set(key, null);
				update = true;
			}
		for(String key : tempConfig.getKeys(true)){
			Object obj = tempConfig.get(key);
			if(!(key.startsWith("Config.Inventarios") || key.startsWith("Config.Kits"))){
				if(finalyml.get(key) != null){
					obj = finalyml.get(key);
				}
				if(!finalyml.contains(key)){
					keys.add(key);
					finalyml.set(key, obj);
					update = true;
				}
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

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}

	public static FKitsPRO getFKitsPRO(){
		return (FKitsPRO) Bukkit.getServer().getPluginManager().getPlugin("FKitsPRO");
	}

	public KitsManager getKitsManager(){
		return this.km;
	}

	public Plugin getTintaCoin() {
		return tintacoin;
	}
	
	public Verificadores getVerificadores() {
		return verificadores;
	}

	private String info(String mensagem){
		Date atual = new Date();
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
		return "[" + format.format(atual) + "] " + mensagem;
	}

	public void log(String player, String mensagem){
		try{
			File dataFolder = new File(getDataFolder() + File.separator + "logs");
			if(!dataFolder.exists()) dataFolder.mkdir();
			File saveTo = new File(getDataFolder(), "logs" + File.separator + player.toLowerCase() + ".txt");
			if (!saveTo.exists()) saveTo.createNewFile();
			FileWriter fw = new FileWriter(saveTo, true);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(info(mensagem));
			pw.flush();
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public String getTime(long millis){
		long days = TimeUnit.MILLISECONDS.toDays(millis);
		long hour = TimeUnit.MILLISECONDS.toHours(millis-TimeUnit.DAYS.toMillis(days));
		long min = TimeUnit.MILLISECONDS.toMinutes((millis-TimeUnit.DAYS.toMillis(days))-TimeUnit.HOURS.toMillis(hour));
		long second = TimeUnit.MILLISECONDS.toSeconds(((millis-TimeUnit.DAYS.toMillis(days))-TimeUnit.HOURS.toMillis(hour))-TimeUnit.MINUTES.toMillis(min));
		StringBuilder msg = new StringBuilder();
		if(days > 0)
			msg.append(days + " " + (days == 1 ? "Dia" : "Dias") + ", ");
		if(hour > 0)
			msg.append(hour + " " + (hour == 1 ? "hora" : "horas") + ", ");
		if(min > 0)
			msg.append(min + " " + (min == 1 ? "minuto" : "minutos") + ", ");
		if(second > 0)
			msg.append(second + " " + (second == 1 ? "segundo" : "segundos") + ", ");
		if(msg.toString().endsWith(", "))
			msg.delete(msg.length()-2, msg.length());
		if(msg.toString().contains(",") || msg.toString().equals("")){
			try{
				msg = msg.replace(msg.lastIndexOf(","), msg.lastIndexOf(",")+1, " e");
			}catch(StringIndexOutOfBoundsException ex){
				return "menos de um segundo";
			}
		}
		return msg.toString();
	}

	public void removeKits() {
		for(PlayersKits playersKits : PlayersKits.playerKitsCache){
			Iterator<Map.Entry<String,Long>> iterator = playersKits.getPlayerKitsDelay().entrySet().iterator();
			while(iterator.hasNext()){
				Entry<String, Long> entry = iterator.next();
				if(entry.getValue() > 0L && entry.getValue() <= System.currentTimeMillis()){
					iterator.remove();
				}
			}
		}
	}

	public void sendToDataBase() {
		removeKits();
		try {
			if(mysql != null){
				mysql.openConnection();
				mysql.restart();
				for(PlayersKits playersKits : PlayersKits.playerKitsCache){
					for(Map.Entry<String, Long> kits : playersKits.getPlayerKitsDelay().entrySet())
						mysql.addNew(playersKits.getName(), kits.getKey(), kits.getValue()+"");
				}
				mysql.closeConnection();
			}else{
				sqlite.openConnection();
				sqlite.restart();
				for(PlayersKits playersKits : PlayersKits.playerKitsCache){
					for(Map.Entry<String, Long> kits : playersKits.getPlayerKitsDelay().entrySet())
						sqlite.addNew(playersKits.getName(), kits.getKey(), kits.getValue()+"");
				}
				sqlite.closeConnection();
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

}

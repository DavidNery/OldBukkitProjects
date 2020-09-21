package com.craftzone.bosses;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.craftzone.bosses.utils.TitleBuilder;
import com.craftzone.bosses.utils.Utils;

public class Bosses extends JavaPlugin {
	
	private Utils utils;
	private final String[] cmds = {"setwither", "setdragon", "lastkwither", "lastkdragon", "spawndragon", "spawnwither"};
	public static Economy econ = null;
	
	@Override
	public void onEnable() {
		if(!new File(getDataFolder(), "config.yml").exists())
			saveDefaultConfig();
		setupEconomy();
		this.utils = new Utils(this);
		getServer().getPluginManager().registerEvents(new Listeners(), this);
		Comandos comandos = new Comandos();
		for(String cmd : cmds)
			getCommand(cmd).setExecutor(comandos);
		new BukkitRunnable() {
			@Override
			public void run() {
				Calendar c = new GregorianCalendar();
				String[] horaspawnar = getConfig().getString("Config.HoraSpawnar").split(":");
				if(c.get(Calendar.HOUR_OF_DAY) == Integer.parseInt(horaspawnar[0])
						&& c.get(Calendar.MINUTE) == Integer.parseInt(horaspawnar[1])){
					boolean vivo = false;
					if(!utils.isDragonLiving()){
						utils.spawnDragon();
						for(Player on : getServer().getOnlinePlayers())
							on.playSound(on.getLocation(), Sound.ENDERDRAGON_DEATH, 10.0F, 1.0F);
						vivo = true;
						// Mensagem avisando que o enderdragon foi spawnado, se quiser que apareça, retire o comentário
						/*String msg = "";
						for(String s : getConfig().getStringList("Mensagem.DragonSpawnado"))
							msg += s.replace("&", "§") + "\n";
						msg = msg.substring(0, msg.length()-2);
						for(Player on : getServer().getOnlinePlayers())
							on.sendMessage(msg);*/
					}else{
						/*String msg = "";
						for(String s : getConfig().getStringList("Mensagem.DragonAindaVivo"))
							msg += s.replace("&", "§") + "\n";
						msg = msg.substring(0, msg.length()-2);
						for(Player on : getServer().getOnlinePlayers())
							on.sendMessage(msg);*/
					}
					if(!utils.isWitherLiving()){
						utils.spawnWither();
						for(Player on : getServer().getOnlinePlayers())
							on.playSound(on.getLocation(), Sound.WITHER_DEATH, 10.0F, 1.0F);
						vivo = true;
						// Mensagem avisando que o whiter foi spawnado, se quiser que apareça, retire o comentário
						/*String msg = "";
						for(String s : getConfig().getStringList("Mensagem.WitherSpawnado"))
							msg += s.replace("&", "§") + "\n";
						msg = msg.substring(0, msg.length()-2);
						for(Player on : getServer().getOnlinePlayers())
							on.sendMessage(msg);*/
					}else{
						/*String msg = "";
						for(String s : getConfig().getStringList("Mensagem.WitherAindaVivo"))
							msg += s.replace("&", "§") + "\n";
						msg = msg.substring(0, msg.length()-2);
						for(Player on : getServer().getOnlinePlayers())
							on.sendMessage(msg);*/
					}
					if(vivo)
						TitleBuilder.sendTitle(20, getConfig().getInt("Config.Tempo_Title")*20, 20, getConfig().getString("Mensagem.Bosses_Spawnados"), null, getServer().getOnlinePlayers());
				}
			}
		}.runTaskTimer(this, 60*20, 60*20);
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll();
	}
	
	public static Bosses getBosses() {
		return (Bosses) Bukkit.getPluginManager().getPlugin("Bosses");
	}
	
	public Utils getUtils() {
		return utils;
	}
	
	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}

}

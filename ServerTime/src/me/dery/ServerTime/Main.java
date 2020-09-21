package me.dery.ServerTime;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{

	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3[ServerTime] §bPlugin Ativado");
		getServer().getPluginManager().registerEvents(this, this);
		// Dia e Noite
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			public void run(){
				for(World w : Bukkit.getServer().getWorlds()){
					List<String> day = getConfig().getStringList("Worlds_Day");
					if(day.contains(w.getName())){
						w.setTime(1000L);
					}
					List<String> night = getConfig().getStringList("Worlds_Night");
					if(night.contains(w.getName())){
						w.setTime(10000000L);
					}
				}
			}
		}, 0L, 20L);
		saveDefaultConfig();
	}

	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§3[ServerTime] §cPlugin Desativado");
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onWeatherWorld(WeatherChangeEvent e){
		List<String> mundoschuva = Main.this.getConfig().getStringList("Worlds_Rain");
		if(mundoschuva.contains(e.getWorld().getName())){
			if(getConfig().getBoolean("Disable_Rain")){
				if(e.toWeatherState()){
					e.setCancelled(true);
				}
			}
		}
	}

}

package me.zfork.craftzone.mobspawn;

import java.io.File;

import me.zfork.craftzone.mobspawn.utils.MobSpawner;
import me.zfork.craftzone.mobspawn.utils.MobSpawnerUtils;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MobSpawn extends JavaPlugin{
	
	public Economy economy;
	private static String PLUGIN_NAME;
	private MobSpawnerUtils mobspawnerutils;
	
	public void onEnable(){
		PLUGIN_NAME = getDescription().getName();
		ConsoleCommandSender sender = getServer().getConsoleSender();
		sender.sendMessage("§3==========[§b" + PLUGIN_NAME + "§3]==========");
		sender.sendMessage(" §3Status: §bAtivado");
		sender.sendMessage(" §3By: §bzFork");
		sender.sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			sender.sendMessage(" §3Config: §bCriada");
		}else{
			sender.sendMessage(" §3Config: §bJa Existente");
		}
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec != null) economy = (Economy) ec.getProvider();
		mobspawnerutils = new MobSpawnerUtils(this);
		getCommand("setmob").setExecutor(new Comandos());
		if(getConfig().getBoolean("Config.Use_MobSpawner_Changer"))
			getServer().getPluginManager().registerEvents(new Eventos(), this);
		new BukkitRunnable() {
			@Override
			public void run() {
				if(getConfig().getConfigurationSection("Spawners") != null){
					for(String s : getConfig().getConfigurationSection("Spawners").getKeys(false)){
						mobspawnerutils.addMobSpawner(getConfig().getString("Spawners." + s + ".Nome"), 
								mobspawnerutils.getLocationByString(getConfig().getString("Spawners." + s + ".Location")),
								getConfig().getInt("Spawners." + s + ".Quantidade"), 
								getConfig().getInt("Spawners." + s + ".Tempo"), 
								EntityType.fromName(getConfig().getString("Spawners." + s + ".Type")),
								getConfig().getInt("Spawners." + s + ".Raio"));
					}
					getConfig().set("Spawners", null);
					saveConfig();
					reloadConfig();
				}
			}
		}.runTaskAsynchronously(this);
		sender.sendMessage("§3==========[§b" + PLUGIN_NAME + "§3]==========");
	}

	public void onDisable(){
		ConsoleCommandSender sender = getServer().getConsoleSender();
		if(mobspawnerutils.getMobspawners().size() > 0){
			for(MobSpawner s : mobspawnerutils.getMobspawners()){
				getConfig().set("Spawners." + s.getMobSpawnerName() + ".Nome", s.getMobSpawnerName());
				getConfig().set("Spawners." + s.getMobSpawnerName() + ".Location", mobspawnerutils.getStringByLocation(s.getLoc()));
				getConfig().set("Spawners." + s.getMobSpawnerName() + ".Tempo", s.getTempo());
				getConfig().set("Spawners." + s.getMobSpawnerName() + ".Quantidade", s.getQuantidade());
				getConfig().set("Spawners." + s.getMobSpawnerName() + ".Type", s.getType().name());
				getConfig().set("Spawners." + s.getMobSpawnerName() + ".Raio", s.getRaio());
			}
			saveConfig();
			reloadConfig();
		}
		sender.sendMessage("§4==========[§c" + PLUGIN_NAME + "§4]==========");
		sender.sendMessage(" §4Status: §cDesativado");
		sender.sendMessage(" §4By: §czFork");
		sender.sendMessage(" §4Versao: §c" + getDescription().getVersion());
		sender.sendMessage("§4==========[§c" + PLUGIN_NAME + "§4]==========");
	}

	public static MobSpawn getMobSpawn(){
		return (MobSpawn) Bukkit.getServer().getPluginManager().getPlugin(PLUGIN_NAME);
	}
	
	public MobSpawnerUtils getMobSpawnerUtils() {
		return mobspawnerutils;
	}

}

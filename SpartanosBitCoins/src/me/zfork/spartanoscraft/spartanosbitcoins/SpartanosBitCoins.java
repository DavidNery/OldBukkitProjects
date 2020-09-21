package me.zfork.spartanoscraft.spartanosbitcoins;

import java.io.File;

import me.zfork.spartanoscraft.spartanosbitcoins.dbmanager.MySQL;
import me.zfork.spartanoscraft.spartanosbitcoins.tasks.VerifyTask;
import me.zfork.spartanoscraft.spartanosbitcoins.utils.ActionsManager;
import me.zfork.spartanoscraft.spartanosbitcoins.utils.FileLogger;
import me.zfork.spartanoscraft.spartanosbitcoins.utils.InventoryUtils;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class SpartanosBitCoins extends JavaPlugin{ 
	
	private PlayerPointsAPI playerpointsAPI;
	
	private InventoryUtils inventoryutils;
	private ActionsManager actionsmanager;
	private FileLogger filelogger;
	private MySQL mysql;
	
	private BukkitTask verifytask;
	
	@Override
	public void onEnable() {
		ConsoleCommandSender sender = getServer().getConsoleSender();
		PlayerPoints playerpoints = (PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints");
		if(playerpoints == null){
			sender.sendMessage("§4PlayerPoints nao encontrado!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		if(!new File(getDataFolder(), "config.yml").exists())
			saveDefaultConfig();
		this.playerpointsAPI = new PlayerPointsAPI(playerpoints);
		this.actionsmanager = new ActionsManager(this);
		this.inventoryutils = new InventoryUtils(this);
		for(Listener listener : inventoryutils.getInventoryListeners().values())
			getServer().getPluginManager().registerEvents(listener, this);
		this.filelogger = new FileLogger(this);
		this.mysql = new MySQL(this, getConfig().getString("Config.MySQL.User"), getConfig().getString("Config.MySQL.Senha"), 
				getConfig().getString("Config.MySQL.Database"), getConfig().getString("Config.MySQL.Host"), getConfig().getString("Config.MySQL.Tabela"));
		this.verifytask = new VerifyTask(this).runTaskTimerAsynchronously(this, 
				getConfig().getInt("Config.MySQL.CheckTime")*20, getConfig().getInt("Config.MySQL.CheckTime")*20);
		new CommandRegister(this);
		actionsmanager.loadOtherClasses();
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		if(verifytask != null && Bukkit.getScheduler().isCurrentlyRunning(verifytask.getTaskId())) verifytask.cancel();
	}
	
	public static SpartanosBitCoins getSpartanosBitCoins() {
		return (SpartanosBitCoins) Bukkit.getPluginManager().getPlugin("SpartanosBitCoins");
	}
	
	public PlayerPointsAPI getPlayerPointsAPI() {
		return playerpointsAPI;
	}
	
	public InventoryUtils getInventoryUtils() {
		return inventoryutils;
	}
	
	public ActionsManager getActionsManager() {
		return actionsmanager;
	}
	
	public FileLogger getFileLogger() {
		return filelogger;
	}
	
	public MySQL getMySQL() {
		return mysql;
	}
	
	public void setMysql(MySQL mysql) {
		this.mysql = mysql;
	}

}

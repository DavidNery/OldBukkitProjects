package me.zfork.hleilao;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class HLeilao extends JavaPlugin implements Listener{
	
	public static Economy econ = null;
	private LeilaoManager lm;
	private HashMap<String, ItemStack> devolver = null;
	private HashMap<String, Double> apostado = null;
	private BukkitTask task = null;
	
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bHLeilao§3]==========");
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
			setupEconomy();
		}
		this.lm = new LeilaoManager(this);
		this.devolver = new HashMap<String, ItemStack>();
		this.apostado = new HashMap<String, Double>();
		getCommand("leilao").setExecutor(new Comandos());
		Bukkit.getServer().getPluginManager().registerEvents(new Eventos(), this);
		getServer().getConsoleSender().sendMessage("§3==========[§bHLeilao§3]==========");
	}
	
	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§4==========[§cHLeilao§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §czFork");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cHLeilao§4]==========");
	}

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}

	public static HLeilao getHLeilao(){
		return (HLeilao) Bukkit.getServer().getPluginManager().getPlugin("HLeilao");
	}
	
	public LeilaoManager getLeilaoManager(){
		return this.lm;
	}
	
	public HashMap<String, ItemStack> getDevolver(){
		return this.devolver;
	}
	
	public HashMap<String, Double> getApostado(){
		return this.apostado;
	}
	
	public BukkitTask getTask(){
		return this.task;
	}
	
	public void setTask(BukkitTask task){
		this.task = task;
	}
	
	public String getTime(long time) {
		String format = "";
		long hours = TimeUnit.MILLISECONDS.toHours(time);
		long hoursInMillis = TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(time - hoursInMillis);
		long minutesInMillis = TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(time - (hoursInMillis + minutesInMillis));
		if (hours > 0)
			format = hours + (hours > 1 ? " horas" : " hora");
		if (minutes > 0) {
			if ((seconds > 0) && (hours > 0))
				format += ", ";
			else if (hours > 0)
				format += " e ";
			format += minutes + (minutes > 1 ? " minutos" : " minuto");
		}
		if (seconds > 0) {
			if ((hours > 0) || (minutes > 0))
				format += " e ";
			format += seconds + (seconds > 1 ? " segundos" : " segundo");
		}
		if (format.equals("")) {
			long rest = time / 100;
			if (rest == 0)
				rest = 1;
			format = "0." + rest + " segundos";
		}
		return format;
	}

}

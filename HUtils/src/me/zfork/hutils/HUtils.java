package me.zfork.hutils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import me.zfork.hutils.comandos.*;
import me.zfork.hutils.listeners.Join;
import me.zfork.hutils.listeners.Listeners;
import me.zfork.hutils.listeners.Quit;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class HUtils extends JavaPlugin{

	private Economy econ = null;
	private Permission perm = null;

	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bHUtils§3]==========");
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
			setupPermissions();
			registerAll();
			new BukkitRunnable() {
				@Override
				public void run(){
					for(World world : Bukkit.getServer().getWorlds()){
						for(Entity entity : world.getEntities()){
							if(entity != null && entity.getType() == EntityType.ARROW) entity.remove();
						}
					}
				}
			}.runTaskTimer(this, 60*20, 60*20);
		}
		getServer().getConsoleSender().sendMessage("§3==========[§bHUtils§3]==========");
	}

	public void onDisable(){
		HandlerList.unregisterAll(this);
		getServer().getConsoleSender().sendMessage("§4==========[§cHUtils§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §czFork");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cHUtils§4]==========");
	}

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			this.econ = (Economy)ec.getProvider();
		}
		return this.econ != null;
	}
	
	public boolean setupPermissions(){
		RegisteredServiceProvider<Permission> pc = getServer().getServicesManager().getRegistration(Permission.class);
		if(pc!=null){
			this.perm = (Permission)pc.getProvider();
		}
		return this.perm != null;
	}

	public static HUtils getHUtils(){
		return (HUtils) Bukkit.getServer().getPluginManager().getPlugin("HUtils");
	}
	
	public Economy getEcon(){
		return this.econ;
	}
	
	public Permission getPermission(){
		return this.perm;
	}
	
	public void registerAll(){
		getCommand("buff").setExecutor(new ComandoBuff());
		getCommand("megafone").setExecutor(new ComandoMegafone());
		getCommand("ajuda").setExecutor(new ComandoAjuda());
		getCommand("report").setExecutor(new ComandoReport());
		getCommand("fumar").setExecutor(new ComandoFumar());
		getCommand("check").setExecutor(new ComandoCheck());
		getCommand("ping").setExecutor(new ComandoPing());
		getCommand("lixeira").setExecutor(new ComandoLixeira());
		getCommand("lanterna").setExecutor(new ComandoLanterna());
		getCommand("head").setExecutor(new ComandoHead());
		getCommand("bc").setExecutor(new ComandoBc());
		getCommand("enchant").setExecutor(new ComandoEnchant());
		getCommand("banitem").setExecutor(new ComandoBanItem());
		Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Quit(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Join(), this);
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
	
	public boolean isPortuguese(String enchant){
		switch(enchant.toLowerCase()){
		case "protecao":
		case "protecao_fogo":
		case "protecao_queda":
		case "protecao_explosao":
		case "protecao_flecha":
		case "respiracao":
		case "afinidade_aquatica":
		case "espinhos":
		case "afiada":
		case "julgamento":
		case "ruina_artropodes":
		case "repulsao":
		case "aspecto_flamejante":
		case "pilhagem":
		case "eficiencia":
		case "toque_suave":
		case "inquebravel":
		case "fortuna":
		case "forca":
		case "impacto":
		case "chama":
		case "infinidade":
			return true;
		default:
			return false;
		}
	}

	public String translateEnchantmentToEnglish(String enchant){
		String en = null;
		switch(enchant.toLowerCase()){
		case "protecao":
			en = "PROTECTION_ENVIRONMENTAL";
			break;
		case "protecao_fogo":
			en = "PROTECTION_FIRE";
			break;
		case "protecao_queda":
			en = "PROTECTION_FALL";
			break;
		case "protecao_explosao":
			en = "PROTECTION_EXPLOSIONS";
			break;
		case "protecao_flecha":
			en = "PROTECTION_PROJECTILE";
			break;
		case "respiracao":
			en = "OXYGEN";
			break;
		case "afinidade_aquatica":
			en = "WATER_WORKER";
			break;
		case "espinhos":
			en = "THORNS";
			break;
		case "afiada":
			en = "DAMAGE_ALL";
			break;
		case "julgamento":
			en = "DAMAGE_UNDEAD";
			break;
		case "ruina_artropodes":
			en = "DAMAGE_ARTHROPODS";
			break;
		case "repulsao":
			en = "KNOCKBACK";
			break;
		case "aspecto_flamejante":
			en = "FIRE_ASPECT";
			break;
		case "pilhagem":
			en = "LOOT_BONUS_MOBS";
			break;
		case "eficiencia":
			en = "DIG_SPEED";
			break;
		case "toque_suave":
			en = "SILK_TOUCH";
			break;
		case "inquebravel":
			en = "DURABILITY";
			break;
		case "fortuna":
			en = "LOOT_BONUS_BLOCKS";
			break;
		case "forca":
			en = "ARROW_DAMAGE";
			break;
		case "impacto":
			en = "ARROW_KNOCKBACK";
			break;
		case "chama":
			en = "ARROW_FIRE";
			break;
		case "infinidade":
			en = "ARROW_INFINITE";
			break;
		}
		return en;
	}

	public String translatePotionToEnglish(String potion){
		String pocao = null;
		switch(potion.toLowerCase()){
		case "velocidade":
			pocao = "SPEED";
			break;
		case "lentidao":
			pocao = "SLOW";
			break;
		case "forca":
			pocao = "INCREASE_DAMAGE";
			break;
		case "pulo":
			pocao = "JUMP";
			break;
		case "nausea":
			pocao = "CONFUSION";
			break;
		case "regeneracao":
			pocao = "REGENERATION";
			break;
		case "resistencia":
			pocao = "DAMAGE_RESISTANCE";
			break;
		case "resistencia-fogo":
			pocao = "FIRE_RESISTANCE";
			break;
		case "invisibilidade":
			pocao = "INVISIBILITY";
			break;
		case "escuridao":
			pocao = "BLINDNESS";
			break;
		case "visao-noturna":
			pocao = "NIGHT_VISION";
			break;
		case "fome":
			pocao = "HUNGER";
			break;
		case "fraqueza":
			pocao = "WEAKNESS";
			break;
		case "veneno":
			pocao = "POISON";
			break;
		case "wither":
			pocao = "WITHER";
			break;
		}
		return pocao;
	}

}

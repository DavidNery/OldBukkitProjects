package com.plugin.stylessentials;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.plugin.stylessentials.comandos.CommandClear;
import com.plugin.stylessentials.comandos.CommandEffect;
import com.plugin.stylessentials.comandos.CommandEnchant;
import com.plugin.stylessentials.comandos.CommandFeed;
import com.plugin.stylessentials.comandos.CommandFirework;
import com.plugin.stylessentials.comandos.CommandFly;
import com.plugin.stylessentials.comandos.CommandGamemode;
import com.plugin.stylessentials.comandos.CommandGive;
import com.plugin.stylessentials.comandos.CommandGod;
import com.plugin.stylessentials.comandos.CommandHat;
import com.plugin.stylessentials.comandos.CommandHeal;
import com.plugin.stylessentials.comandos.CommandInvsee;
import com.plugin.stylessentials.comandos.CommandKill;
import com.plugin.stylessentials.comandos.CommandKillall;
import com.plugin.stylessentials.comandos.CommandRocket;
import com.plugin.stylessentials.comandos.CommandSay;
import com.plugin.stylessentials.comandos.CommandSpeed;
import com.plugin.stylessentials.comandos.CommandThor;
import com.plugin.stylessentials.comandos.CommandTitle;
import com.plugin.stylessentials.comandos.CommandTp;
import com.plugin.stylessentials.comandos.CommandTpall;
import com.plugin.stylessentials.comandos.CommandTphere;
import com.plugin.stylessentials.comandos.CommandTppos;
import com.plugin.stylessentials.comandos.CommandVanish;

public class Stylessentials extends JavaPlugin{
	
	static FileConfiguration msgs;
	static File msg;
	
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bStylessentials§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bDery & Bitter");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		CarregarArquivos();
		Registrar();
		getServer().getConsoleSender().sendMessage("§3==========[§bStylessentials§3]==========");
	}

	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§4==========[§cStylessentials§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §cDery & Biterr");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cStylessentials§4]==========");
	}
	
	public static Stylessentials getStylessentials(){
		return (Stylessentials) Bukkit.getServer().getPluginManager().getPlugin("Stylessentials");
	}
	
	public void CarregarArquivos(){
		// Config
		/*if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
		}else{
			getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
		}*/
		// Mensagens
		setup();
	}
	
	public void Registrar(){
		getCommand("clear").setExecutor(new CommandClear());
		getCommand("effect").setExecutor(new CommandEffect());
		getCommand("enchant").setExecutor(new CommandEnchant());
		getCommand("feed").setExecutor(new CommandFeed());
		getCommand("firework").setExecutor(new CommandFirework());
		getCommand("fly").setExecutor(new CommandFly());
		getCommand("gamemode").setExecutor(new CommandGamemode());
		getCommand("give").setExecutor(new CommandGive());
		getCommand("god").setExecutor(new CommandGod());
		getCommand("hat").setExecutor(new CommandHat());
		getCommand("heal").setExecutor(new CommandHeal());
		getCommand("invsee").setExecutor(new CommandInvsee());
		getCommand("kill").setExecutor(new CommandKill());
		getCommand("killall").setExecutor(new CommandKillall());
		getCommand("rocket").setExecutor(new CommandRocket());
		getCommand("say").setExecutor(new CommandSay());
		getCommand("speed").setExecutor(new CommandSpeed());
		getCommand("thor").setExecutor(new CommandThor());
		getCommand("title").setExecutor(new CommandTitle());
		getCommand("tp").setExecutor(new CommandTp());
		getCommand("tpall").setExecutor(new CommandTpall());
		getCommand("tphere").setExecutor(new CommandTphere());
		getCommand("tppos").setExecutor(new CommandTppos());
		getCommand("vanish").setExecutor(new CommandVanish());
	}
	
	public void setup(){
		msg = new File(getDataFolder(), "mensagens.yml");
		if(!getDataFolder().exists()){
			getDataFolder().mkdir();
		}
		if(!new File(getDataFolder(), "mensagens.yml").exists()){
			saveResource("mensagens.yml", false);
			getServer().getConsoleSender().sendMessage(" §3Mensagens: §bCriada");
		}else{
			getServer().getConsoleSender().sendMessage(" §3Mensagens: §bJa Existente");
		}
		msgs = YamlConfiguration.loadConfiguration(msg);
	}
	
	public static FileConfiguration getMensagens(){
		return msgs;
	}

}

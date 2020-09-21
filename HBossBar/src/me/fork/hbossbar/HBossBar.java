package me.fork.hbossbar;

import java.io.File;

import me.confuser.barapi.BarAPI;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class HBossBar extends JavaPlugin implements Listener{
	
	private BukkitTask task = null;
	private int qual = 0;
	
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bHBossBar§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bDery");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
		}else{
			getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
		}
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		task = new BukkitRunnable() {
			@Override
			public void run(){
				if(getServer().getOnlinePlayers().length != 0){
					if(getConfig().getStringList("Mensagens").size() == qual){
						qual = 0;
					}
					for(Player p : getServer().getOnlinePlayers()){
						if(p == null) continue;
						BarAPI.setMessage(p, getConfig().getStringList("Mensagens").get(qual).replace("&", "§").replace("@player", p.getName()));
					}
					qual++;
				}
			}
		}.runTaskTimer(this, 120*20, getConfig().getInt("Tempo_Mudar_Mensagem")*20);
		getServer().getConsoleSender().sendMessage("§3==========[§bHBossBar§3]==========");
	}
	
	public void onDisable(){
		if(task != null) task.cancel();
		getServer().getConsoleSender().sendMessage("§4==========[§cHBossBar§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §cDery");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cHBossBar§4]==========");
	}
	
	@EventHandler
	public void Join(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(task == null) return;
		BarAPI.setMessage(p, getConfig().getStringList("Mensagens").get(qual).replace("&", "§").replace("@player", p.getName()));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("bossbar")){
			// /bossbar <player> <tempo> <msg>
			//     0        1        2     3
			//              0        1     2
			if(!sender.hasPermission("bossbar.admin")) return true;
			if(args.length == 0){
				for(Player p : getServer().getOnlinePlayers()){
					if(BarAPI.hasBar(p)){
						BarAPI.removeBar(p);
					}
				}
				sender.sendMessage(getConfig().getString("Removeu_BossBar").replace("&", "§"));
				return true;
			}
			if(args.length <= 2){
				sender.sendMessage(getConfig().getString("Uso_Correto").replace("&", "§"));
				return true;
			}
			Player player = null;
			if(!args[0].toLowerCase().equalsIgnoreCase("all")){
				player = getServer().getPlayer(args[0]);
				if(player == null){
					sender.sendMessage(getConfig().getString("Player_Offline").replace("&", "§"));
					return true;
				}
			}
			if((!args[1].equalsIgnoreCase("sempre")) && (!isNumber(args[1]))){
				sender.sendMessage(getConfig().getString("Nao_E_Numero").replace("&", "§"));
				return true;
			}
			String msg = "";
			for(int i = 2; i<args.length; i++){
				msg += args[i] + " ";
			}
			msg = msg.substring(0, msg.length() - 1);
			if(args[0].equalsIgnoreCase("all")){
				for(Player p : getServer().getOnlinePlayers()){
					if(args[1].equalsIgnoreCase("sempre")){
						BarAPI.setMessage(p, msg.replace("&", "§").replace("@player", p.getName()));
					}else{
						BarAPI.setMessage(p, msg.replace("&", "§").replace("@player", p.getName()), Integer.parseInt(args[1]));
					}
				}
			}else{
				if(args[1].equalsIgnoreCase("sempre")){
					BarAPI.setMessage(player, msg.replace("&", "§").replace("@player", player.getName()));
				}else{
					BarAPI.setMessage(player, msg.replace("&", "§").replace("@player", player.getName()), Integer.parseInt(args[1]));
				}
			}
			sender.sendMessage(getConfig().getString("Setou_BossBar").replace("&", "§").replace("@msg", msg.replace("&", "§")).replace("@tempo", args[1]));
		}
		return false;
	}
	
	public boolean isNumber(String num){
		try{
			Float.parseFloat(num);
			return true;
		}catch(Exception e){}
		return false;
	}

}

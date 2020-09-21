package me.zfork.HLM;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class HLM extends JavaPlugin implements Listener{
	
	private ArrayList<Location> locs = new ArrayList<Location>();
	private Random r = new Random();
	
	@Override
	public void onEnable() {
		getServer().getConsoleSender().sendMessage("§3Ativado!");
		saveDefaultConfig();
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		getServer().getConsoleSender().sendMessage("§4Desativado!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("Unknown command. Type \"help\" for help.");
			return true;
		}
		Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("setmorte")){
			if(!p.isOp()){
				p.sendMessage(getConfig().getString("Sem_Permissao").replace("&", "§"));
				return true;
			}
			if(args.length == 0){
				p.sendMessage(getConfig().getString("Uso_Correto").replace("&", "§"));
				return true;
			}
			if(args[0].equalsIgnoreCase("loc1")){
				getConfig().set("Loc1", getStringFromLocation(p.getLocation()));
				saveConfig();
				reloadConfig();
				p.sendMessage(getConfig().getString("Loc_Setada").replace("&", "§").replace("@loc", "1"));
			}else if(args[0].equalsIgnoreCase("loc2")){
				getConfig().set("Loc2", getStringFromLocation(p.getLocation()));
				saveConfig();
				reloadConfig();
				p.sendMessage(getConfig().getString("Loc_Setada").replace("&", "§").replace("@loc", "2"));
			}else if(args[0].equalsIgnoreCase("loc3")){
				getConfig().set("Loc3", getStringFromLocation(p.getLocation()));
				saveConfig();
				reloadConfig();
				p.sendMessage(getConfig().getString("Loc_Setada").replace("&", "§").replace("@loc", "3"));
			}else{
				p.sendMessage(getConfig().getString("Uso_Correto").replace("&", "§"));
			}
		}if(cmd.getName().equalsIgnoreCase("setlogin")){
			if(!p.isOp()){
				p.sendMessage(getConfig().getString("Sem_Permissao").replace("&", "§"));
				return true;
			}
			getConfig().set("Login", getStringFromLocation(p.getLocation()));
			saveConfig();
			reloadConfig();
			p.sendMessage(getConfig().getString("LoginLoc_Setado").replace("&", "§"));
		}
		return false;
	}
	
	@EventHandler
	public void PlayerJoin(PlayerJoinEvent e){
		final Player p = e.getPlayer();
		if(!p.hasPlayedBefore()){
			new BukkitRunnable() {
				@Override
				public void run() {
					p.teleport(getLocationFromString(getConfig().getString("Login")));
				}
			}.runTaskLater(this, 5L);
		}
	}
	
	@EventHandler
	public void Respawn(PlayerRespawnEvent e){
		final Player p = e.getPlayer();
		locs.clear();
		locs.add(getLocationFromString(getConfig().getString("Loc1")));
		locs.add(getLocationFromString(getConfig().getString("Loc2")));
		locs.add(getLocationFromString(getConfig().getString("Loc3")));
		new BukkitRunnable() {
			@Override
			public void run() {
				p.teleport(locs.get(r.nextInt(locs.size())));
			}
		}.runTaskLater(this, 5L);
	}
	
	public String getStringFromLocation(Location loc){
		StringBuilder sb = new StringBuilder();
		sb.append(loc.getWorld().getName() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " " + loc.getYaw() + " " + loc.getPitch());
		return sb.toString();
	}
	
	public Location getLocationFromString(String loc){
		Location location = new Location(Bukkit.getWorld("world"), 0, 0, 0, 0, 0);
		String[] partes = loc.split(" ");
		location.setWorld(Bukkit.getWorld(partes[0]));
		location.setX(Double.parseDouble(partes[1]));
		location.setY(Double.parseDouble(partes[2]));
		location.setZ(Double.parseDouble(partes[3]));
		location.setYaw(Float.parseFloat(partes[4]));
		location.setPitch(Float.parseFloat(partes[5]));
		return location;
	}

}

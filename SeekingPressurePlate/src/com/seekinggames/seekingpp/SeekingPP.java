package com.seekinggames.seekingpp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SeekingPP extends JavaPlugin implements Listener{
	
	ArrayList<Location> location = new ArrayList<Location>();
	File f = new File(getDataFolder(), "pressureplates.yml");
	FileConfiguration fc;
	
	public void onEnable() {
		getServer().getConsoleSender().sendMessage("§bHabilitando Plugin...");
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		if(f.exists()){
			fc = YamlConfiguration.loadConfiguration(f);
			for(String plates : fc.getStringList("PressurePlates")){
				location.add(new Location(Bukkit.getWorld(plates.split(" ")[0]), Double.parseDouble(plates.split(" ")[1]), Double.parseDouble(plates.split(" ")[2]), Double.parseDouble(plates.split(" ")[3])));
			}
			f.delete();
			new File("plugins/SeekingPressurePlate").delete();
		}
		getServer().getConsoleSender().sendMessage("§aPlugin habilitado!");
	}
	
	public void onDisable() {
		if(!new File("plugins/SeekingPressurePlate").exists())
			try {
				new File("plugins/SeekingPressurePlate").mkdir();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		if(!f.exists())
			try {
				f.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		fc = YamlConfiguration.loadConfiguration(f);
		List<String> lista = (fc.getStringList("PressurePlates") != null ? fc.getStringList("PressurePlates") : new ArrayList<String>());
		for(Location loc : location){
			lista.add(loc.getWorld().getName() + " " + (int) loc.getX() + " " + (int) loc.getY() + " " + (int) loc.getZ());
		}
		fc.set("PressurePlates", lista);
		try {
			fc.save(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		getServer().getConsoleSender().sendMessage("§cPlugin Desabilitado!");
	}
	
	@EventHandler
	public void Interact(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(e.getAction() == Action.PHYSICAL){
			Block block = e.getClickedBlock();
			if(block.getType() == Material.STONE_PLATE){
				if(location.contains(block.getLocation())){
					p.spigot().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 1, 0, 0, 0, 0, 1, 10, 10);
					new BukkitRunnable() {
						@Override
						public void run() {
							p.setVelocity(new Vector(p.getEyeLocation().getDirection().getX(), 0.09F, p.getEyeLocation().getDirection().getZ()).multiply(20));
						}
					}.runTaskLater(this, 1L);
					p.playSound(p.getLocation(), Sound.WITHER_SHOOT, 5.0F, 1.0F);
				}
			}
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return true;
		if(cmd.getName().equalsIgnoreCase("setplate")){
			Player p = (Player) sender;
			if(!p.hasPermission("jogalonge.use")) return true;
			if(p.getTargetBlock((Set<Material>) null, 10).getType() == Material.AIR || p.getTargetBlock((Set<Material>) null, 10).getType() != Material.STONE_PLATE){
				p.sendMessage("§cEsteja olhando para uma STONE_PLATE para poder usar esse comando!");
				return true;
			}
			Block b = p.getTargetBlock((Set<Material>) null, 10);
			location.add(b.getLocation());
			p.sendMessage("§bEste bloco agora joga o player longe!");
		}
		return false;
	}

}

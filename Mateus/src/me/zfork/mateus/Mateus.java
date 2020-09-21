package me.zfork.mateus;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Mateus extends JavaPlugin implements Listener {
	
	private static String PLUGIN_NAME;
	
	public void onEnable(){
		PLUGIN_NAME = getDescription().getName();
		ConsoleCommandSender sender = getServer().getConsoleSender();
		sender.sendMessage("§3==========[§b" + PLUGIN_NAME + "§3]==========");
		sender.sendMessage(" §3Status: §bAtivado");
		sender.sendMessage(" §3By: §bzFork");
		sender.sendMessage(" §3Versao: §b" + getDescription().getVersion());
		getServer().getPluginManager().registerEvents(this, this);
		sender.sendMessage("§3==========[§b" + PLUGIN_NAME + "§3]==========");
	}

	public void onDisable(){
		ConsoleCommandSender sender = getServer().getConsoleSender();
		sender.sendMessage("§4==========[§c" + PLUGIN_NAME + "§4]==========");
		sender.sendMessage(" §4Status: §cDesativado");
		sender.sendMessage(" §4By: §czFork");
		sender.sendMessage(" §4Versao: §c" + getDescription().getVersion());
		sender.sendMessage("§4==========[§c" + PLUGIN_NAME + "§4]==========");
	}
	
	@EventHandler(ignoreCancelled=true)
	public void blockPlace(BlockPlaceEvent e){
		if(e.getBlock().getType().equals(Material.WOOD)){
			Location loc = e.getBlock().getLocation();
			loop:
			for(int i = (int)loc.getX()-4; i<(int)loc.getX()+4; i++){
				for(int j = (int)loc.getY()-4; j<(int)loc.getY()+4; j++){
					for(int k = (int)loc.getZ()-4; k<(int)loc.getZ()+4; k++){
						if(loc.getWorld().getBlockAt(new Location(loc.getWorld(), i, j, k)).getType().equals(Material.DIRT)){
							e.setCancelled(true);
							e.getPlayer().sendMessage("§cVocê não pode colocar este bloco aqui =P");
							break loop;
						}
					}
				}
			}
		}
	}

}

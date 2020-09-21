package me.zfork.landmasks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMasks implements CommandExecutor{

	private LandMasks instance = LandMasks.getLandMasks();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return true;
		Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("mascaras"))
			p.openInventory(instance.getInv());
		return false;
	}
	
	

}

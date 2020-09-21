package me.zfork.hutils.comandos;

import me.zfork.hutils.HUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ComandoLixeira implements CommandExecutor{
	
	private HUtils instance = HUtils.getHUtils();
	private Inventory inv = instance.getServer().createInventory(null, 9*6, "§6§lLIXEIRA");
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage("Unkown command. Type \"help\" for help.");
			return true;
		}
		Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("lixeira")){
			p.openInventory(inv);
			p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Abriu_Lixeira").replace("&", "§"));
		}
		return false;
	}

}

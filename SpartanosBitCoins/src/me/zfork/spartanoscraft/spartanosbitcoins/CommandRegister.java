package me.zfork.spartanoscraft.spartanosbitcoins;

import me.zfork.spartanoscraft.spartanosbitcoins.dbmanager.MySQL;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRegister implements CommandExecutor{
	
	private final SpartanosBitCoins instance;
	
	public CommandRegister(SpartanosBitCoins instance) {
		this.instance = instance;
		instance.getCommand("comprar").setExecutor(this);
		instance.getCommand("sbcreload").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("comprar") && sender instanceof Player){
			instance.getInventoryUtils().getInventarios().get("mainInventory").openToPlayer((Player) sender);
		}else if(cmd.getName().equalsIgnoreCase("sbcreload")){
			if(!sender.isOp()){
				sender.sendMessage("§cVocê não tem permissão para utilizar este comando!");
			}
			instance.saveConfig();
			instance.reloadConfig();
			instance.setMysql(new MySQL(instance, instance.getConfig().getString("Config.MySQL.User"), instance.getConfig().getString("Config.MySQL.Senha"), 
					instance.getConfig().getString("Config.MySQL.Database"), instance.getConfig().getString("Config.MySQL.Host"), instance.getConfig().getString("Config.MySQL.Tabela")));
			sender.sendMessage("§aPlugin recarregado!");
		}
		return false;
	}

}

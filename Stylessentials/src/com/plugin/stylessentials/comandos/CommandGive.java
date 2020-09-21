package com.plugin.stylessentials.comandos;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.plugin.stylessentials.Util;

public class CommandGive extends Util implements CommandExecutor {
	
	/*
	 * give <player> <item> [quantia] [data] [nome]
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(command.getName().equalsIgnoreCase("Give")) {
			
			if(!sender.hasPermission("cmds.give")) {
				sender.sendMessage("§cVocê precisa do grupo [Admin] ou superior para executar este comando!");
				return true;
			}
			
			if(args.length <= 1) {
				sender.sendMessage("§cUtilize /give <player> <item> [quantia] [data] [nome_teste = nome teste]");
				return true;
			}
			
			/*
			 * give <player> <item/id>
			 */
			if(args.length == 2) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage("§cPlayer '" + args[0] + "' não foi encontrado.");
					return true;
				}
				try{
					String item = args[1];
					if(containsNumber(item)) {
						int id = Integer.valueOf(item);
						@SuppressWarnings("deprecation")
						ItemStack is = createItem(Material.getMaterial(id), 1, 0, null, null);
						sender.sendMessage("§aGivando (1)" + is.getType() + " para " + target.getName() + ".");
						target.getInventory().addItem(is);
					}else {
						ItemStack is = createItem(Material.getMaterial(item.toUpperCase()), 1, 0, null, null);
						sender.sendMessage("§aGivando (1)" + is.getType() + " para " + target.getName() + ".");
						target.getInventory().addItem(is);
					}
				}catch(Exception e) {
					sender.sendMessage("§cEste item não existe.");
				}
				return true;
			}
			
			/*
			 * give <player> <item/id> [quantia]
			 */
			if(args.length == 3) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage("§cPlayer '" + args[0] + "' não foi encontrado.");
					return true;
				}
				try{
					if(containsNumber(args[2])) {
						sender.sendMessage("§c" + args[2] + " não é número.");
						return true;
					}
					String item = args[1];
					int quantia = Integer.valueOf(args[2]);
					try{
						if(containsNumber(item)) {
							int id = Integer.valueOf(item);
							@SuppressWarnings("deprecation")
							ItemStack is = createItem(Material.getMaterial(id), quantia, 0, null, null);
							sender.sendMessage("§aGivando (" + quantia + ")" + is.getType() + " para " + target.getName() + ".");
							target.getInventory().addItem(is);
						}else {
							ItemStack is = createItem(Material.getMaterial(item.toUpperCase()), quantia, 0, null, null);
							sender.sendMessage("§aGivando (" + quantia + ")" + is.getType() + " para " + target.getName() + ".");
							target.getInventory().addItem(is);
						}
					}catch(NumberFormatException ex) {
						sender.sendMessage("§cUtilize apenas números.");
					}
				}catch(Exception e) {
					sender.sendMessage("§cEste item não existe.");
				}
				return true;
			}
			
			/*
			 * give <player> <item/id> [quantia] [data]
			 */
			if(args.length == 4) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage("§cPlayer '" + args[0] + "' não foi encontrado.");
					return true;
				}
				try{
					if(containsNumber(args[2])) {
						sender.sendMessage("§c" + args[2] + " não é número.");
						return true;
					}
					if(containsNumber(args[3])) {
						sender.sendMessage("§c" + args[3] + " não é número.");
						return true;
					}
					String item = args[1];
					int id = Integer.valueOf(item);
					int quantia = Integer.parseInt(args[2]);
					int data = Integer.parseInt(args[3]);
					try{
						if(containsNumber(item)) {
							@SuppressWarnings("deprecation")
							ItemStack is = createItem(Material.getMaterial(id), quantia, data, null, null);
							sender.sendMessage("§aGivando (" + quantia + ")" + is.getType() + " para " + target.getName() + ".");
							target.getInventory().addItem(is);
						}else {
							ItemStack is = createItem(Material.getMaterial(item.toUpperCase()), quantia, data, null, null);
							sender.sendMessage("§aGivando (" + quantia + ")" + is.getType() + " para " + target.getName() + ".");
							target.getInventory().addItem(is);
						}
					}catch(NumberFormatException ex) {
						sender.sendMessage("§cUtilize apenas números.");
					}
				}catch(Exception e) {
					sender.sendMessage("§cEste item não existe.");
				}
				return true;
			}
			
			/*
			 * give <player> <item> [quantia] [data] [nome]
			 */
			if(args.length >= 5) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage("§cPlayer '" + args[0] + "' não foi encontrado.");
					return true;
				}
				try{
					if(containsNumber(args[2])) {
						sender.sendMessage("§c" + args[2] + " não é número.");
						return true;
					}
					if(containsNumber(args[3])) {
						sender.sendMessage("§c" + args[3] + " não é número.");
						return true;
					}
					String item = args[1];
					int id = Integer.valueOf(item);
					int quantia = Integer.parseInt(args[2]);
					int data = Integer.parseInt(args[3]);
					String nome = args[4];
					try{
						if(containsNumber(item)) {
							@SuppressWarnings("deprecation")
							ItemStack is = createItem(Material.getMaterial(id), quantia, data, nome.replace("&", "§").replace("_", " "), null);
							sender.sendMessage("§aGivando (" + quantia + ")" + is.getType() + " para " + target.getName() + ".");
							target.getInventory().addItem(is);
						}else {
							ItemStack is = createItem(Material.getMaterial(item.toUpperCase()), quantia, data, nome.replace("&", "§").replace("_", " "), null);
							sender.sendMessage("§aGivando (" + quantia + ")" + is.getType() + " para " + target.getName() + ".");
							target.getInventory().addItem(is);
						}
					}catch(NumberFormatException ex) {
						sender.sendMessage("§cUtilize apenas números.");
					}
				}catch(Exception e) {
					sender.sendMessage("§cEste item não existe.");
				}
				return true;
			}
		}
		
		return false;
	}

}

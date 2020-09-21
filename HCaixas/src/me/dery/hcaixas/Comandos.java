package me.dery.hcaixas;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Comandos implements CommandExecutor{
	
	private HCaixas instance = HCaixas.getHCaixas();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("caixa")){
			if(!sender.hasPermission("caixa.admin")){
				sender.sendMessage(instance.getConfig().getString("Sem_Permissao").replace("&", "§"));
				return true;
			}
			if(args.length == 0){
				sender.sendMessage(instance.getConfig().getString("Argumentos").replace("&", "§"));
				return true;
			}
			if(args[0].equalsIgnoreCase("dar")){
				if(args.length < 3){
					sender.sendMessage(instance.getConfig().getString("Argumentos_Dar").replace("&", "§"));
					return true;
				}
				Player player = instance.getServer().getPlayer(args[2]);
				String caixa = args[1];
				if(player == null){
					sender.sendMessage(instance.getConfig().getString("Player_Offline").replace("&", "§"));
					return true;
				}
				if(!instance.getConfig().contains("Caixa." + caixa)){
					sender.sendMessage(instance.getConfig().getString("Tipo_Nao_Existe").replace("&", "§").replace("{caixa}", caixa));
					return true;
				}
				if(player.getInventory().firstEmpty() == -1){
					sender.sendMessage(instance.getConfig().getString("Inv_Cheio").replace("&", "§"));
					return true;
				}
				ItemStack item = new ItemStack(Material.getMaterial(instance.getConfig().getInt("Caixa." + caixa + ".ID")));
				ItemMeta itemmeta = item.getItemMeta();
				itemmeta.setDisplayName(instance.getConfig().getString("Caixa." + caixa + ".Nome").replace("&", "§"));
				List<String> lore = new ArrayList<String>();
				for(String items : instance.getConfig().getStringList("Caixa." + caixa + ".Lore")){
					lore.add(items.replace("&", "§"));
				}
				itemmeta.setLore(lore);
				item.setItemMeta(itemmeta);
				player.getInventory().addItem(item);
				sender.sendMessage(instance.getConfig().getString("Deu_Caixa").replace("&", "§").replace("@player", player.getName()));
				player.sendMessage(instance.getConfig().getString("Ganhou_Caixa").replace("&", "§").replace("@staff", (sender instanceof Player ? sender.getName() : "CONSOLE")));
				player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10.0F, 5.0F);
			}else if(args[0].equalsIgnoreCase("give")){
				if(!(sender instanceof Player)) return true;
				Player player = (Player) sender;
				if(args.length < 2){
					sender.sendMessage(instance.getConfig().getString("Argumentos_Give").replace("&", "§"));
					return true;
				}
				String caixa = args[1];
				if(!instance.getConfig().contains("Caixa." + caixa)){
					sender.sendMessage(instance.getConfig().getString("Tipo_Nao_Existe").replace("&", "§").replace("{caixa}", caixa));
					return true;
				}
				if(player.getInventory().firstEmpty() == -1){
					sender.sendMessage(instance.getConfig().getString("Inv_Cheio").replace("&", "§"));
					return true;
				}
				ItemStack item = new ItemStack(Material.getMaterial(instance.getConfig().getInt("Caixa." + caixa + ".ID")));
				ItemMeta itemmeta = item.getItemMeta();
				itemmeta.setDisplayName(instance.getConfig().getString("Caixa." + caixa + ".Nome").replace("&", "§"));
				List<String> lore = new ArrayList<String>();
				for(String items : instance.getConfig().getStringList("Caixa." + caixa + ".Lore")){
					lore.add(items.replace("&", "§"));
				}
				itemmeta.setLore(lore);
				item.setItemMeta(itemmeta);
				player.getInventory().addItem(item);
				player.sendMessage(instance.getConfig().getString("Se_Deu_Caixa").replace("&", "§").replace("@player", player.getName()));
			}
		}
		return false;
	}

}

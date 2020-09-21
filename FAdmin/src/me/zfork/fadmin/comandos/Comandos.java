package me.zfork.fadmin.comandos;

import me.zfork.fadmin.FAdmin;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Comandos implements CommandExecutor{
	
	public Comandos(FAdmin instance) {
		instance.getCommand("getnofall").setExecutor(this);
		instance.getCommand("getantikb").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player){
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("getnofall")){
				if(!p.isOp()){
					p.sendMessage("§c§lFADMIN §7Você não tem permissão :P");
					return true;
				}
				ItemStack nofall = new ItemStack(Material.STICK);
				ItemMeta im = nofall.getItemMeta();
				im.setDisplayName("§cTeste de NoFall");
				nofall.setItemMeta(im);
				p.getInventory().addItem(nofall);
			}else if(cmd.getName().equalsIgnoreCase("getantikb")){
				if(!p.isOp()){
					p.sendMessage("§c§lFADMIN §7Você não tem permissão :P");
					return true;
				}
				ItemStack kb = new ItemStack(Material.STICK);
				ItemMeta im = kb.getItemMeta();
				im.setDisplayName("§cTeste de Knockback");
				kb.setItemMeta(im);
				kb.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10);
				p.getInventory().addItem(kb);
			}
		}
		return false;
	}
	
	

}

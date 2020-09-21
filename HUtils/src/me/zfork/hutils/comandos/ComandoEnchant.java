package me.zfork.hutils.comandos;

import me.zfork.hutils.HUtils;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class ComandoEnchant implements CommandExecutor{
	
	private HUtils instance = HUtils.getHUtils();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("enchant")){
			if(!(sender instanceof Player)){
				sender.sendMessage("Unkown command. Type \"help\" for help.");
				return true;
			}
			Player p = (Player) sender;
			if(!p.hasPermission("utils.enchant")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("@cmd", label));
				return true;
			}
			if(args.length <= 1){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Enchant").replace("&", "§"));
				return true;
			}
			if(!isNum(args[1])){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Numero").replace("&", "§"));
				return true;
			}
			if(instance.isPortuguese(args[0])){
				if(instance.translateEnchantmentToEnglish(args[0]) == null){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Enchant_Nao_Existe").replace("&", "§"));
					return true;
				}
			}else{
				if(Enchantment.getByName(args[0].toUpperCase()) == null){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Enchant_Nao_Existe").replace("&", "§"));
					return true;
				}
			}
			if(p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Item").replace("&", "§"));
				return true;
			}
			if(Integer.parseInt(args[1]) > instance.getConfig().getInt("Config.Enchant_Maximo")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Enchant_Maximo").replace("&", "§"));
				return true;
			}
			if(instance.isPortuguese(args[0])){
				p.getItemInHand().addUnsafeEnchantment(Enchantment.getByName(instance.translateEnchantmentToEnglish(args[0])), Integer.parseInt(args[1]));
			}else{
				p.getItemInHand().addUnsafeEnchantment(Enchantment.getByName(args[0].toUpperCase()), Integer.parseInt(args[1]));
			}
			p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Encantou").replace("&", "§").replace("@enchant", args[0]).replace("@level", args[1]));
		}
		return false;
	}
	
	public boolean isNum(String num){
		try{
			Integer.parseInt(num);
			return true;
		}catch(Exception e){}
		return false;
	}

}

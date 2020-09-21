package me.zfork.hutils.comandos;

import java.util.ArrayList;
import java.util.List;

import me.zfork.hutils.HUtils;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ComandoBanItem implements CommandExecutor{
	
	private HUtils instance = HUtils.getHUtils();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("banitem")){
			if(!sender.hasPermission("utils.banitem")){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("@cmd", label));
				return true;
			}
			if(args.length == 0){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.BanItem").replace("&", "§"));
				return true;
			}
			if(isNum(args[0])){
				if(instance.getConfig().getStringList("Config.Items_Banidos") != null && instance.getConfig().getStringList("Config.Items_Banidos").contains(args[0])){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Item_Ja_Banido").replace("&", "§"));
					return true;
				}
				if(Material.getMaterial(Integer.parseInt(args[0])) == null){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Item_Nao_Existe").replace("&", "§"));
					return true;
				}
				List<String> items = instance.getConfig().getStringList("Config.Items_Banidos") == null ? new ArrayList<String>() : instance.getConfig().getStringList("Config.Items_Banidos");
				items.add(args[0]);
				instance.getConfig().set("Config.Items_Banidos", items);
				instance.saveConfig();
				instance.reloadConfig();
			}else{
				if(instance.getConfig().getStringList("Config.Items_Banidos") != null && instance.getConfig().getStringList("Config.Items_Banidos").contains(args[0])){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Item_Ja_Banido").replace("&", "§"));
					return true;
				}
				if(Material.getMaterial(args[0].toUpperCase()) == null){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Item_Nao_Existe").replace("&", "§"));
					return true;
				}
				List<String> items = instance.getConfig().getStringList("Config.Items_Banidos") == null ? new ArrayList<String>() : instance.getConfig().getStringList("Config.Items_Banidos");
				items.add(args[0].toLowerCase());
				instance.getConfig().set("Config.Items_Banidos", items);
				instance.saveConfig();
				instance.reloadConfig();
			}
			sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Baniu_Item").replace("&", "§"));
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

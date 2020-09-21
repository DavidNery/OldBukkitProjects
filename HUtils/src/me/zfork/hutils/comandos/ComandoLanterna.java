package me.zfork.hutils.comandos;

import me.zfork.hutils.HUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ComandoLanterna implements CommandExecutor{
	
	private HUtils instance = HUtils.getHUtils();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage("Unkown command. Type \"help\" for help.");
			return true;
		}
		Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("lanterna")){
			if(!p.hasPermission("hutils.lanterna")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("@cmd", label));
				return true;
			}
			if(p.getActivePotionEffects().size() > 0){
				if(hasPotionEffect(p, PotionEffectType.NIGHT_VISION)){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Desativou_Lanterna").replace("&", "§"));
					p.removePotionEffect(PotionEffectType.NIGHT_VISION);
					return true;
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Ativou_Lanterna").replace("&", "§"));
					p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
					return true;
				}
			}else{
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Ativou_Lanterna").replace("&", "§"));
				p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
				return true;
			}
		}
		return false;
	}
	
	public boolean hasPotionEffect(Player p, PotionEffectType potion){
		if(p.getActivePotionEffects().size() > 0){
			for(PotionEffect pocao : p.getActivePotionEffects()){
				if(pocao.getType().equals(potion)) return true;;
			}
		}
		return false;
	}

}

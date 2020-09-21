package me.zfork.hutils.comandos;

import java.text.DecimalFormat;

import me.zfork.hutils.HUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ComandoCheck implements CommandExecutor{
	
	private HUtils instance = HUtils.getHUtils();
	private DecimalFormat df = new DecimalFormat("0.00");
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("check")){
			if(!sender.hasPermission("utils.check")){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("@cmd", label));
				return true;
			}
			if(args.length == 0){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Check").replace("&", "§").replace("@cmd", label));
				return true;
			}
			Player p = instance.getServer().getPlayer(args[0]);
			if(p == null){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§"));
				return true;
			}
			for(String msg : instance.getConfig().getStringList("Mensagem.Check")){
				sender.sendMessage(msg.replace("&", "§").replace("@player", p.getName()).replace("@vida", p.isDead() ? 0 + "" : p.getHealth() + "")
						.replace("@fome", p.isDead() ? 0 + "" : p.getFoodLevel() + "").replace("@coord", getCoord(p)).replace("@gamemode", p.getGameMode().name())
						.replace("@op", p.isOp() ? "Sim" : "Nao").replace("@ip", p.getAddress().getAddress().getHostAddress()));
			}
		}
		return false;
	}
	
	public String getCoord(Player p){
		String coord = "";
		coord += p.getLocation().getWorld().getName() + " ";
		coord += df.format(p.getLocation().getX()) + " ";
		coord += df.format(p.getLocation().getY()) + " ";
		coord += df.format(p.getLocation().getZ());
		return coord;
	}

}

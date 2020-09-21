package me.zfork.hutils.comandos;

import me.zfork.hutils.HUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ComandoBc implements CommandExecutor{
	
	private HUtils instance = HUtils.getHUtils();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("bc")){
			if(!sender.hasPermission("utils.bc")){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("@cmd", label));
				return true;
			}
			if(args.length == 0){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Bc").replace("&", "§"));
				return true;
			}
			String msg = "";
			for(int i = 0; i<args.length; i++){
				msg += args[i] + " ";
			}
			msg = msg.substring(0, msg.length() - 1);
			for(Player on : instance.getServer().getOnlinePlayers()){
				on.sendMessage(msg.replace("&", "§").replace("@player", on.getName()));
			}
		}
		return false;
	}

}

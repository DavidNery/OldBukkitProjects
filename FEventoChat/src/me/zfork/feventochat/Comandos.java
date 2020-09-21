package me.zfork.feventochat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Comandos implements CommandExecutor {
	
	private FEventoChat instance = FEventoChat.getFEventoChat();
	
	@SuppressWarnings("static-access")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("eventochat")){
			if(args.length == 0){
				sender.sendMessage("§f§lComandos do plugin:");
				sender.sendMessage("§3/eventochat iniciar <frase> §f* §bInicie o evento chat!");
				sender.sendMessage("§3/eventochat parar §f* §bPare o evento chat");
			}else if(args[0].equalsIgnoreCase("iniciar")){
				if(args.length == 1){
					sender.sendMessage("§cUtilize §7/eventochat iniciar <frase>§c!");
					return true;
				}else if(instance.task != null){
					sender.sendMessage("§cEvento já iniciado!");
					return true;
				}else if(!sender.isOp()){
					sender.sendMessage("§cVocê precisa ter OP para iniciar esse evento!");
					return true;
				}
				StringBuilder frase = new StringBuilder();
				for(int i = 1; i<args.length; i++){
					frase.append(args[i]).append(" ");
				}
				instance.frase = frase.toString().trim();
				for(Player on : instance.getServer().getOnlinePlayers()){
					on.sendMessage("");
					on.sendMessage("");
					on.sendMessage("§d§l[EVENTOCHAT] §fEvento chat iniciado!");
					on.sendMessage("§d§l[EVENTOCHAT] §fQuem escrever §7\'" + instance.frase + "\' §fno chat primeiro ganha!");
					on.sendMessage("");
					on.sendMessage("");
				}
				instance.task = new BukkitRunnable() {
					@Override
					public void run() {
						for(Player on : instance.getServer().getOnlinePlayers()){
							on.sendMessage("");
							on.sendMessage("");
							on.sendMessage("§d§l[EVENTOCHAT] §fEvento chat finalizado!");
							on.sendMessage("§d§l[EVENTOCHAT] §fNão houve vencedores!");
							on.sendMessage("");
							on.sendMessage("");
							instance.task = null;
							instance.frase = "";
						}
					}
				}.runTaskLater(instance, 30*20);
			}else if(args[0].equalsIgnoreCase("parar")){
				if(instance.task == null){
					sender.sendMessage("§cEvento não iniciado!");
					return true;
				}else if(!sender.isOp()){
					sender.sendMessage("§cVocê precisa ter OP para iniciar esse evento!");
					return true;
				}
				for(Player on : instance.getServer().getOnlinePlayers()){
					on.sendMessage("");
					on.sendMessage("");
					on.sendMessage("§d§l[EVENTOCHAT] §fEvento chat finalizado!");
					on.sendMessage("§d§l[EVENTOCHAT] §fStaff §7" + (sender instanceof Player ? ((Player)sender).getName() : "console") + "§f cancelou o evento!");
					on.sendMessage("");
					on.sendMessage("");
					instance.task.cancel();
					instance.task = null;
					instance.frase = "";
				}
			}
		}
		return false;
	}

}

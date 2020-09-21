package me.zfork.hjungle;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Comandos implements CommandExecutor{
	
	private HJungle instance = HJungle.getHJungle();
	private JungleManager jm = instance.getJungleManager();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("jungle")){
			if(args.length == 0){
				sendSimpleMessageList(sender, "Comandos");
				return true;
			}else if(args[0].equalsIgnoreCase("participar")){
				if(!(sender instanceof Player)){
					sender.sendMessage("§4Comando somente para players!");
					return true;
				}
				jm.addPlayer((Player) sender);
			}else if(args[0].equalsIgnoreCase("sair")){
				if(!(sender instanceof Player)){
					sender.sendMessage("§4Comando somente para players!");
					return true;
				}
				jm.delPlayer((Player) sender);
			}else if(args[0].equalsIgnoreCase("iniciar")){
				jm.iniciarEvento(sender);
			}else if(args[0].equalsIgnoreCase("fechar")){
				jm.pararEvento(sender);
			}else if(args[0].equalsIgnoreCase("setsaida")){
				if(!(sender instanceof Player)){
					sender.sendMessage("§4Comando somente para players!");
					return true;
				}
				jm.setSaida((Player) sender);
			}else if(args[0].equalsIgnoreCase("setentrada")){
				if(!(sender instanceof Player)){
					sender.sendMessage("§4Comando somente para players!");
					return true;
				}
				jm.setEntrada((Player) sender);
			}else if(args[0].equalsIgnoreCase("setlobby")){
				if(!(sender instanceof Player)){
					sender.sendMessage("§4Comando somente para players!");
					return true;
				}
				jm.setLobby((Player) sender);
			}else if(args[0].equalsIgnoreCase("setbau")){
				if(!(sender instanceof Player)){
					sender.sendMessage("§4Comando somente para players!");
					return true;
				}
				jm.setBau((Player) sender);
			}else if(args[0].equalsIgnoreCase("info")){
				jm.infoEvento(sender);
			}else{
				sendSimpleMessageList(sender, "Comandos");
			}
		}
		return false;
	}
	
	public void sendSimpleMessageList(CommandSender sender, String msg){
		for(String mensagem : instance.getConfig().getStringList("Mensagem." + msg)){
			sender.sendMessage(mensagem.replace("&", "§"));
		}
	}

}

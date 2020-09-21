package me.dery.hspleef;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Comandos implements CommandExecutor{
	
	private HSpleef instance = HSpleef.getHSpleef();
	
	@SuppressWarnings("static-access")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("spleef")){
			if(args.length == 0){
				for(String msg : instance.getConfig().getStringList("Mensagem.Comandos")){
					sender.sendMessage(msg.replace("&", "§"));
				}
				return true;
			}else if(args[0].equalsIgnoreCase("iniciar")){
				if(!sender.hasPermission("hspleef.admin")){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}
				if(instance.getSpleefManager().getEstagio() != 0){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Em_Andamento").replace("&", "§"));
					return true;
				}
				instance.getSpleefManager().Iniciar();
				sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Iniciou").replace("&", "§"));
			}else if(args[0].equalsIgnoreCase("cancelar")){
				if(!sender.hasPermission("hspleef.admin")){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}
				if(instance.getSpleefManager().getEstagio() == 0){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Em_Andamento").replace("&", "§"));
					return true;
				}
				instance.getSpleefManager().Parar();
				sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Parou").replace("&", "§"));
			}else if(args[0].equalsIgnoreCase("setentrada")){
				if(!(sender instanceof Player)){
					sender.sendMessage("Unkown command. Type \"help\" for help.");
					return true;
				}
				Player p = (Player) sender;
				if(!sender.hasPermission("hspleef.admin")){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}
				instance.getSpleefManager().setSpawn(p);
				sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Spawn").replace("&", "§"));
			}else if(args[0].equalsIgnoreCase("setsaida")){
				if(!(sender instanceof Player)){
					sender.sendMessage("Unkown command. Type \"help\" for help.");
					return true;
				}
				Player p = (Player) sender;
				if(!sender.hasPermission("hspleef.admin")){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}
				instance.getSpleefManager().setExit(p);
				sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Saida").replace("&", "§"));
			}else if(args[0].equalsIgnoreCase("setlobby")){
				if(!(sender instanceof Player)){
					sender.sendMessage("Unkown command. Type \"help\" for help.");
					return true;
				}
				Player p = (Player) sender;
				if(!sender.hasPermission("hspleef.admin")){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}
				instance.getSpleefManager().setLobby(p);
				sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Lobby").replace("&", "§"));
			}else if(args[0].equalsIgnoreCase("setar")){
				if(!(sender instanceof Player)){
					sender.sendMessage("Unkown command. Type \"help\" for help.");
					return true;
				}
				Player p = (Player) sender;
				if(!sender.hasPermission("hspleef.admin")){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}
				if(p.getInventory().firstEmpty() == -1){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inventario_Cheio").replace("&", "§"));
					return true;
				}
				ItemStack item = new ItemStack(Material.BLAZE_ROD);
				ItemMeta im = item.getItemMeta();
				im.setDisplayName("§eSete o chao do Spleef");
				item.setItemMeta(im);
				p.getInventory().addItem(item);
				sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Recebeu_Item").replace("&", "§"));
			}else if(args[0].equalsIgnoreCase("info")){
				if(!sender.hasPermission("hspleef.info")){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}
				sender.sendMessage(instance.getSpleefManager().Info());
			}else if(args[0].equalsIgnoreCase("participar")){
				if(!(sender instanceof Player)){
					sender.sendMessage("Unkown command. Type \"help\" for help.");
					return true;
				}
				Player p = (Player) sender;
				if(!sender.hasPermission("hspleef.participar")){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}
				instance.getSpleefManager().Entrar(p);
			}else if(args[0].equalsIgnoreCase("sair")){
				if(!(sender instanceof Player)){
					sender.sendMessage("Unkown command. Type \"help\" for help.");
					return true;
				}
				Player p = (Player) sender;
				if(!sender.hasPermission("hspleef.sair")){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}
				instance.getSpleefManager().Sair(p);
			}else{
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Comando_Desconhecido").replace("&", "§"));
				return true;
			}
		}
		return false;
	}

}

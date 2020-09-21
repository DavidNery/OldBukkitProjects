package me.zfork.craftzone.chain;

import java.sql.SQLException;

import me.zfork.craftzone.chain.utils.SQLite;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Comandos implements CommandExecutor{
	
	private Main instance = Main.getMain();
	private ChainManager chainmanager = instance.getChainmanager();
	private SQLite sqlite = instance.getSqlite();
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("chain")){
			if(!(sender instanceof Player)){
				sender.sendMessage("§9Comando apenas para players!");
				return true;
			}
			Player p = (Player) sender;
			if(args.length == 0){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto").replace("&", "§").replace("{cmd}", "/" + label.split(" ")[0]));
				return true;
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Cmd.Entrar"))){
				if(!isVazio(p)){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inv_Vazio").replace("&", "§"));
					return true;
				}else if(chainmanager.hasPlayerInChain(p.getName())){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta").replace("&", "§"));
					return true;
				}
				try {
					sqlite.openConnection();
					if(sqlite.isBanned(p.getName().toLowerCase())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Esta_Banido").replace("&", "§")
								.replace("{staff}", sqlite.getStaff(p.getName().toLowerCase())).replace("{motivo}", sqlite.getMotivo(p.getName().toLowerCase())));
						sqlite.closeConnection();
						return true;
					}
					sqlite.closeConnection();
					if(instance.getConfig().getBoolean("Config.Avisar_Entrou"))
						chainmanager.getPlayers().forEach(s -> instance.getServer().getPlayer(s).sendMessage(
								instance.getConfig().getString("Mensagem.Sucesso.Player_Entrou").replace("&", "§").replace("{player}", p.getName())));
					chainmanager.addPlayer(p);
					for(String msg : instance.getConfig().getStringList("Mensagem.Sucesso.Entrou")) p.sendMessage(msg.replace("&", "§"));
				} catch (ClassNotFoundException | SQLException e) {
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Entrar").replace("&", "§"));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Cmd.Sair"))){
				if(!chainmanager.hasPlayerInChain(p.getName())){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta").replace("&", "§"));
					return true;
				}
				chainmanager.delPlayer(p, true);
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Saiu").replace("&", "§"));
				if(instance.getConfig().getBoolean("Config.Avisar_Saiu"))
					chainmanager.getPlayers().forEach(s -> instance.getServer().getPlayer(s).sendMessage(
							instance.getConfig().getString("Mensagem.Sucesso.Player_Saiu").replace("&", "§").replace("{player}", p.getName())));
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Cmd.SetLoc"))){
				if(!p.hasPermission(instance.getConfig().getString("Config.Permissoes.SetLoc"))){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}else if(args.length == 1){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.SetLoc").replace("&", "§"));
					return true;
				}else if(!(args[1].equalsIgnoreCase("spawn") || args[1].equalsIgnoreCase("saida") || args[1].equalsIgnoreCase("camarote"))){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.SetLoc1").replace("&", "§"));
					return true;
				}
				chainmanager.setLocation(p, args[1].substring(0, 1).toUpperCase() + args[1].substring(1).toLowerCase());
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Loc").replace("&", "§").replace("{loc}", args[1]));
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Cmd.SetItens"))){
				if(!p.hasPermission(instance.getConfig().getString("Config.Permissoes.SetItens"))){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}
				chainmanager.setItens(p);
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Itens").replace("&", "§"));
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Cmd.Ban"))){
				if(!p.hasPermission(instance.getConfig().getString("Config.Permissoes.Ban"))){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}else if(args.length < 3){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ban").replace("&", "§"));
					return true;
				}
				try {
					sqlite.openConnection();
					if(sqlite.isBanned(args[1].toLowerCase())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Ja_Banido").replace("&", "§"));
						sqlite.closeConnection();
						return true;
					}
					Player player = instance.getServer().getPlayer(args[1]);
					String motivo = "";
					for(int i = 2; i<args.length; i++) motivo += args[i] + " ";
					motivo = motivo.trim();
					sqlite.addNew(args[1].toLowerCase(), motivo, p.getName());
					sqlite.closeConnection();
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Baniu_Player").replace("&", "§").replace("{player}", args[1].toLowerCase())
							.replace("{motivo}", motivo));
					for(String players : chainmanager.getPlayers()){
						Player pl = instance.getServer().getPlayer(players);
						pl.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Staff_Baniu_Player").replace("&", "§")
								.replace("{player}", args[1].toLowerCase()).replace("{staff}", p.getName()).replace("{motivo}", motivo));
					}
					if(player != null) chainmanager.delPlayer(player, true);
				} catch (ClassNotFoundException | SQLException e) {
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Baniu").replace("&", "§"));
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Cmd.UnBan"))){
				if(!p.hasPermission(instance.getConfig().getString("Config.Permissoes.UnBan"))){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}else if(args.length < 2){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.UnBan").replace("&", "§"));
					return true;
				}
				try {
					sqlite.openConnection();
					if(!sqlite.isBanned(args[1].toLowerCase())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Nao_Banido").replace("&", "§"));
						sqlite.closeConnection();
						return true;
					}
					sqlite.delPlayer(args[1].toLowerCase());
					sqlite.closeConnection();
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Desbaniu_Player").replace("&", "§").replace("{player}", args[1].toLowerCase()));
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Desbaniu").replace("&", "§"));
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Cmd.Camarote"))){
				if(!p.hasPermission(instance.getConfig().getString("Config.Permissoes.Camarote"))){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}else if(chainmanager.hasPlayerInChain(p.getName())){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta").replace("&", "§"));
					return true;
				}
				p.teleport(chainmanager.getLocation("Camarote"));
			}else{
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto").replace("&", "§").replace("{cmd}", "/" + label.split(" ")[0]));
				return true;
			}
		}
		return false;
	}
	
	public boolean isVazio(Player p){
		for(ItemStack item : p.getInventory().getContents()) 
			if(item != null && item.getType() != Material.AIR) return false;
		for(ItemStack item : p.getInventory().getArmorContents()) 
			if(item != null && item.getType() != Material.AIR) return false;
		return true;
	}

}

package com.plugin.stylessentials.comandos;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.plugin.stylessentials.Stylessentials;

public class CommandGamemode implements CommandExecutor {
	
	private Stylessentials instance = Stylessentials.getStylessentials();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Gamemode")) {
			if(!sender.hasPermission("stylessentials.gamemode")) {
				sender.sendMessage(instance.getConfig().getString("Sem_Permissao").replace("&", "�").replace("{cmd}", label));
				return true;
			}
			if(args.length == 0) {
				sender.sendMessage(instance.getConfig().getString("GameMode.Poucos_Argumentos").replace("&", "�"));
				return true;
			}
			if((args.length == 1) && sender instanceof Player) {
				Player player = (Player)sender;
				String gamemode = args[0];
				try{
					setGamemode(player, gamemode);
					player.sendMessage("§aGamemode definido para: " + player.getGameMode().name() + ".");
				}catch(Exception ex) {
					sender.sendMessage("§cEste gamemode não existe!");
				}
				return true;
			}else if(args.length >= 1) {
				Player target = sender.getServer().getPlayer(args[1]);
				if(target == null) {
					sender.sendMessage("§c" + args[1] + " não encontrado!");
					return true;
				}
				String gamemode = args[0];
				try{
					setGamemode(target, gamemode);
					sender.sendMessage("§aDefiniu o gamemode de: " + target.getName() + " para: " + target.getGameMode().name() + ".");
					target.sendMessage("§aGamemode definido para: " + target.getGameMode().name() + ".");
				}catch(Exception ex) {
					sender.sendMessage("§cEste gamemode não existe!");
				}
				return true;
			}
			return true;
		}
		return false;
	}
	
	/*
	 * Verifica se um player esta em um gamemode.
	 */
	
	public static boolean isInGamemode(Player player, String gamemode) {
		if(player != null) {
			if(gamemode != null) {
				if(player.getGameMode() == getByName(gamemode)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * Define o gamemode de um player.
	 */
	
	public static void setGamemode(Player player, String gamemode) {
		if(player != null) {
			if(gamemode != null) {
				player.setGameMode(getByName(gamemode));
			}
		}
	}
	
	/*
	 * GetByName gamemode.
	 */
	
	public static GameMode getByName(String gamemode) {
		if(gamemode.equalsIgnoreCase("survival")||gamemode.equalsIgnoreCase("0")) {
			return GameMode.SURVIVAL;
		}
		if(gamemode.equalsIgnoreCase("criativo")||gamemode.equalsIgnoreCase("1")) {
			return GameMode.CREATIVE;
		}
		if(gamemode.equalsIgnoreCase("aventura")||gamemode.equalsIgnoreCase("2")) {
			return GameMode.ADVENTURE;
		}
		if(gamemode.equalsIgnoreCase("espectador")||gamemode.equalsIgnoreCase("3")) {
			return GameMode.SPECTATOR;
		}
		return null;
	}

}

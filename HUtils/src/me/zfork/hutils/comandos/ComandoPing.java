package me.zfork.hutils.comandos;

import me.zfork.hutils.HUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ComandoPing implements CommandExecutor{
	
	private HUtils instance = HUtils.getHUtils();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage("Unkown command. Type \"help\" for help.");
			return true;
		}
		Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("ping")){
			if(args.length == 0){
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Ping").replace("&", "§").replace("@pingms", ((CraftPlayer) p).getHandle().ping + "")
						.replace("@pings", (((CraftPlayer) p).getHandle().ping / 1000) + ""));
				return true;
			}
			Player player = instance.getServer().getPlayer(args[0]);
			if(player == null){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§"));
				return true;
			}
			p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Ping_Outro").replace("&", "§").replace("@player", player.getName())
					.replace("@pingms", ((CraftPlayer) player).getHandle().ping + "")
					.replace("@pings", (((CraftPlayer) p).getHandle().ping / 1000) + ""));
		}
		return false;
	}

}

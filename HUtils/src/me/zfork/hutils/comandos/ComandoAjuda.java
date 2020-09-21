package me.zfork.hutils.comandos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.zfork.hutils.HUtils;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ComandoAjuda implements CommandExecutor{

	private HUtils instance = HUtils.getHUtils();
	private HashMap<String, Long> players = new HashMap<String, Long>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("Unkown command. Type \"help\" for help.");
			return true;
		}
		final Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("ajuda")){
			if(args.length == 0){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Ajuda").replace("&", "§"));
				return true;
			}
			if(players.containsKey(p.getName().toLowerCase())){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde").replace("&", "§").replace("@tempo", instance.getTime(players.get(p.getName().toLowerCase()) - System.currentTimeMillis())));
				return true;
			}
			List<String> list = new ArrayList<String>();
			for(Player on : instance.getServer().getOnlinePlayers()){
				if(on.hasPermission("ajuda.ver")){
					list.add(on.getName());
				}
			}
			if(list.size() == 0){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Staff_On").replace("&", "§"));
				return true;
			}
			String msg = "";
			for(int i = 0; i<args.length; i++){
				msg += args[i] + " ";
			}
			msg = msg.substring(0, msg.length() - 1);
			for(String staffs : list){
				Player player = instance.getServer().getPlayer(staffs);
				if(player != null){
					if(player.hasPermission("ajuda.ver")){
						for(String mensagem : instance.getConfig().getStringList("Mensagem.Sucesso.Pedido_De_Ajuda_Recebido")){
							player.sendMessage(mensagem.replace("&", "§").replace("@ajuda", msg).replace("@player", p.getName()));
						}
						if(!player.isDead()) player.playSound(player.getLocation(), Sound.ANVIL_LAND, 10.0F, 1.0F);
					}
				}
			}
			p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Pedido_De_Ajuda_Enviado").replace("&", "§").replace("@staffs", list.size() + ""));
			players.put(p.getName().toLowerCase(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30));
			new BukkitRunnable() {
				@Override
				public void run() {
					players.remove(p.getName().toLowerCase());
				}
			}.runTaskLater(instance, 30*20);
			list.clear();
		}
		return false;
	}

}

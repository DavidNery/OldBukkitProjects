package me.zfork.hutils.comandos;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import me.zfork.hutils.HUtils;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ComandoMegafone implements CommandExecutor{
	
	private HUtils instance = HUtils.getHUtils();
	private HashMap<String, Long> players = new HashMap<String, Long>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("Unkown command. Type \"help\" for help.");
			return true;
		}
		final Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("megafone")){
			if(!p.hasPermission("vip.megafone")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§").replace("@cmd", label));
				return true;
			}
			if(args.length == 0){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Megafone").replace("&", "§"));
				return true;
			}
			if(players.containsKey(p.getName().toLowerCase())){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde").replace("&", "§").replace("@tempo", instance.getTime(players.get(p.getName().toLowerCase()) - System.currentTimeMillis())));
				return true;
			}
			if(!p.getInventory().contains(Material.SPONGE)){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Esponja").replace("&", "§"));
				return true;
			}
			String msg = "";
			for(int i = 0; i<args.length; i++){
				msg += args[i] + " ";
			}
			msg = msg.substring(0, msg.length() - 1);
			players.put(p.getName().toLowerCase(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.Megafone.Tempo")));
			retirarItem(p, Material.SPONGE);
			new BukkitRunnable() {
				@Override
				public void run(){
					players.remove(p.getName().toLowerCase());
				}
			}.runTaskLater(instance, instance.getConfig().getInt("Config.Megafone.Tempo")*20);
			for(Player on : instance.getServer().getOnlinePlayers()){
				for(String formato : instance.getConfig().getStringList("Config.Megafone.Formato")){
					on.sendMessage(formato.replace("&", "§").replace("@player", p.getName()).replace("@msg", msg.replace("&", "§")));
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public void retirarItem(Player p, Material material){
		for(int i = 0; i<36; i++){
			ItemStack item = p.getInventory().getItem(i);
			if(item != null && item.getType() == material){
				if(item.getAmount() - 1 == 0){
					p.getInventory().setItem(i, null);
					p.updateInventory();
				}else{
					item.setAmount(item.getAmount() - 1);
				}
				return;
			}
		}
	}

}

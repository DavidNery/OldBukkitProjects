package me.zfork.hutils.comandos;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import me.zfork.hutils.HUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ComandoFumar implements CommandExecutor{
	
	private HUtils instance = HUtils.getHUtils();
	private HashMap<String, Long> players = new HashMap<String, Long>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("Unkown command. Type \"help\" for help.");
			return true;
		}
		final Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("fumar")){
			if(players.containsKey(p.getName().toLowerCase())){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde").replace("&", "§").replace("@tempo", instance.getTime(players.get(p.getName().toLowerCase()) - System.currentTimeMillis())));
				return true;
			}
			if(!instance.getEcon().has(p.getName(), instance.getConfig().getDouble("Config.Fumar.Money"))){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Money").replace("&", "§").replace("@money", NumberFormat.getCurrencyInstance().format(instance.getConfig().getDouble("Config.Fumar.Money") - instance.getEcon().getBalance(p.getName())).replace("$", "§")));
				return true;
			}
			p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 30*20, 1));
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30*20, 1));
			instance.getEcon().withdrawPlayer(p.getName(), instance.getConfig().getDouble("Config.Fumar.Money"));
			players.put(p.getName().toLowerCase(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.Fumar.Tempo")));
			new BukkitRunnable() {
				@Override
				public void run(){
					players.remove(p.getName().toLowerCase());
				}
			}.runTaskLater(instance, instance.getConfig().getInt("Config.Fumar.Tempo")*20);
			p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Fumou").replace("&", "§"));
		}
		return false;
	}

}

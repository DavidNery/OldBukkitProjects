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

public class ComandoBuff implements CommandExecutor{
	
	private HUtils instance = HUtils.getHUtils();
	private HashMap<String, Long> players = new HashMap<String, Long>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("Unkown command. Type \"help\" for help.");
			return true;
		}
		final Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("buff")){
			if(!p.hasPermission("vip.buff")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("@cmd", label));
				return true;
			}
			if(players.containsKey(p.getName().toLowerCase())){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde").replace("&", "§").replace("@tempo", instance.getTime(players.get(p.getName().toLowerCase()) - System.currentTimeMillis())));
				return true;
			}
			if(!instance.getEcon().has(p.getName(), instance.getConfig().getDouble("Config.Buff.Money"))){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Money").replace("&", "§").replace("@money", NumberFormat.getCurrencyInstance().format(instance.getConfig().getDouble("Config.Buff.Money") - instance.getEcon().getBalance(p.getName())).replace("$", "§")));
				return true;
			}
			for(String efeitos : instance.getConfig().getStringList("Config.Buff.Efeitos")){
				String[] partes = efeitos.split(" ");
				p.addPotionEffect(new PotionEffect(PotionEffectType.getByName(instance.translatePotionToEnglish(partes[0])), Integer.parseInt(partes[1])*20, Integer.parseInt(partes[2])));
			}
			instance.getEcon().withdrawPlayer(p.getName(), instance.getConfig().getDouble("Config.Buff.Money"));
			players.put(p.getName().toLowerCase(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.Buff.Tempo")));
			new BukkitRunnable() {
				@Override
				public void run(){
					players.remove(p.getName().toLowerCase());
				}
			}.runTaskLater(instance, instance.getConfig().getInt("Config.Buff.Tempo")*20);
			p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Usou_Buff").replace("&", "§").replace("@money", NumberFormat.getCurrencyInstance().format(instance.getConfig().getDouble("Config.Buff.Money")).replace("$", "")));
			for(Player on : instance.getServer().getOnlinePlayers()){
				if(on == null) continue;
				on.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Usou_Buff_All").replace("@player", p.getName()).replace("&", "§"));
			}
		}
		return false;
	}

}

package me.zfork.craftzone.chain;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Eventos implements Listener{

	private Main instance = Main.getMain();
	private ChainManager chainmanager = instance.getChainmanager();
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void Death(PlayerDeathEvent e){
		Player p = e.getEntity();
		if(chainmanager.hasPlayerInChain(p.getName())){
			if(p.getKiller() != null && chainmanager.hasPlayerInChain(p.getKiller().getName())){
				PlayerKillingSpree pks = chainmanager.getKillingspree().get(p.getKiller().getName().toLowerCase());
				pks.setKills(pks.getKills()+1);
				if(pks.getTask() != null) pks.getTask().cancel();
				pks.setTask(new BukkitRunnable() {
					@Override
					public void run() {
						pks.setKills(0);
					}
				}.runTaskLater(instance, instance.getConfig().getInt("Config.Killing_Spree_Tempo_Entre_Kills")*20));
				for(String s : instance.getConfig().getConfigurationSection("Config.Killing_Spree").getKeys(false)){
					if(pks.getKills() == Integer.parseInt(s)){
						for(String players : chainmanager.getPlayers()){
							instance.getServer().getPlayer(players).sendMessage(instance.getConfig().getString("Config.Killing_Spree." + s + ".Mensagem")
									.replace("&", "§").replace("{player}", p.getKiller().getName()));
						}
						break;
					}else if(pks.getKills() == instance.getConfig().getInt("Config.Killing_Spree_Dominar")){
						String msg = "";
						for(String ksm : instance.getConfig().getStringList("Config.Killing_Spree_Mensagem"))
							msg += ksm + "\n";
						if(msg.length() > 0) msg = msg.substring(0, msg.length()-2);
						for(Player on : instance.getServer().getOnlinePlayers())
							on.sendMessage(msg.replace("&", "§").replace("{player}", p.getKiller().getName()));
						break;
					}
				}
				if(!p.getAddress().getAddress().getHostAddress().equalsIgnoreCase(p.getKiller().getAddress().getAddress().getHostAddress())){
					for(String ks : instance.getConfig().getConfigurationSection("Config.Killing_Spree_Kills").getKeys(false)){
						if(ks.equals(chainmanager.getKills().get(p.getName().toLowerCase()) + "")){
							if(instance.getConfig().contains("Config.Killing_Spree_Kills." + ks + ".Mensagem"))
								for(String s : instance.getConfig().getStringList("Config.Killing_Spree_Kills." + ks + ".Mensagem"))
									p.getKiller().sendMessage(s.replace("&", "§")
											.replace("{player}", p.getKiller().getName()).replace("{vitima}", p.getName()));
							if(instance.getConfig().contains("Config.Killing_Spree_Kills." + ks + ".Money"))
								instance.economy.depositPlayer(p.getName(), instance.getConfig().getDouble("Config.Killing_Spree_Kills." + ks + ".Money"));
							if(instance.getConfig().contains("Config.Killing_Spree_Kills." + ks + ".Global")){
								String msg = "";
								for(String s : instance.getConfig().getStringList("Config.Killing_Spree_Kills." + ks + ".Global")) msg += s + "\n";
								msg = msg.substring(0, msg.length()-2);
								for(String player : chainmanager.getPlayers())
									instance.getServer().getPlayer(player).sendMessage(msg.replace("&", "§").replace("{player}", p.getKiller().getName())
											.replace("{vitima}", p.getName()));
							}
							break;
						}
					}
				}
				int kills = chainmanager.getKills().get(p.getKiller().getName().toLowerCase());
				int deaths = chainmanager.getDeaths().get(p.getName().toLowerCase());
				chainmanager.getKills().put(p.getKiller().getName().toLowerCase(), kills+1);
				chainmanager.getDeaths().put(p.getName().toLowerCase(), deaths+1);
				if(chainmanager.getTopkiller() == null){
					chainmanager.setTopkiller(p.getKiller());
					chainmanager.setTopkillerkills(1);
					if(instance.getConfig().getBoolean("Config.Avisar_Novo_Top_Killer")){
						String msg = "";
						for(String s : instance.getConfig().getStringList("Mensagem.Sucesso.Novo_Top_Killer"))
							msg += s.replace("&", "§").replace("{player}", p.getKiller().getName()).replace("{kills}", ""+(kills+1))+"\n";
						msg = msg.substring(0, msg.length()-2);
						for(String pl : chainmanager.getPlayers()) instance.getServer().getPlayer(pl).sendMessage(msg);
					}
					for(int i = 0; i<5; i++) p.getKiller().getWorld().strikeLightningEffect(p.getKiller().getLocation());
				}else if(kills+1 > chainmanager.getTopkillerkills() && (!chainmanager.getTopkiller().getName().equalsIgnoreCase(p.getKiller().getName()))){
					chainmanager.setTopkiller(p.getKiller());
					chainmanager.setTopkillerkills(kills+1);
					if(instance.getConfig().getBoolean("Config.Avisar_Novo_Top_Killer")){
						String msg = "";
						for(String s : instance.getConfig().getStringList("Mensagem.Sucesso.Novo_Top_Killer"))
							msg += s.replace("&", "§").replace("{player}", p.getKiller().getName()).replace("{kills}", ""+(kills+1))+"\n";
						msg = msg.substring(0, msg.length()-2);
						for(String pl : chainmanager.getPlayers()) instance.getServer().getPlayer(pl).sendMessage(msg);
					}
					for(int i = 0; i<5; i++) p.getKiller().getWorld().strikeLightningEffect(p.getKiller().getLocation());
				}
			}
			chainmanager.delPlayer(p, true);
			if(instance.getConfig().getBoolean("Config.Avisar_Morreu"))
				chainmanager.getPlayers().forEach(s -> instance.getServer().getPlayer(s).sendMessage(
						instance.getConfig().getString("Mensagem.Sucesso.Player_Moreu").replace("&", "§").replace("{player}", p.getName())
						.replace("{killer}", p.getKiller() == null ? instance.getConfig().getString("Config.Ninguem") : p.getKiller().getName())));
		}
	}

	@EventHandler
	public void Kick(PlayerKickEvent e){
		Player p = e.getPlayer();
		if(chainmanager.hasPlayerInChain(p.getName())){
			chainmanager.delPlayer(p, true);
			if(instance.getConfig().getBoolean("Config.Avisar_Kick"))
				chainmanager.getPlayers().forEach(s -> instance.getServer().getPlayer(s).sendMessage(
						instance.getConfig().getString("Mensagem.Sucesso.Player_Kicked").replace("&", "§").replace("{player}", p.getName())));
		}
	}

	@EventHandler
	public void Kick(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(chainmanager.hasPlayerInChain(p.getName())){
			chainmanager.delPlayer(p, true);
			if(instance.getConfig().getBoolean("Config.Avisar_Quit"))
				chainmanager.getPlayers().forEach(s -> instance.getServer().getPlayer(s).sendMessage(
						instance.getConfig().getString("Mensagem.Sucesso.Player_Quit").replace("&", "§").replace("{player}", p.getName())));
		}
	}

	/*@EventHandler
	public void TP(PlayerTeleportEvent e){
		Player p = e.getPlayer();
		if(!e.getCause().equals(TeleportCause.PLUGIN) && chainmanager.hasPlayerInChain(p.getName())){
			chainmanager.delPlayer(p, false);
			p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Saiu_Teleporte").replace("&", "§"));
		}
	}*/

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void Command(PlayerCommandPreprocessEvent e){
		Player p = e.getPlayer();
		if(chainmanager.hasPlayerInChain(p.getName())){
			for(String cmds : instance.getConfig().getStringList("Config.Comandos_Liberados")){
				if(e.getMessage().equalsIgnoreCase(cmds) || 
						e.getMessage().toLowerCase().startsWith(cmds.toLowerCase())) return;
			}
			e.setCancelled(true);
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Comando_Bloqueado").replace("&", "§"));
		}
	}

}

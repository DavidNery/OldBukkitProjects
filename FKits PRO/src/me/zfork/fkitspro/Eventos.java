package me.zfork.fkitspro;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Eventos implements Listener{

	private FKitsPRO instance = FKitsPRO.getFKitsPRO();
	private KitsManager kitsmanager = instance.getKitsManager();

	@EventHandler
	public void Join(PlayerJoinEvent e){
		if(e.getPlayer().isOp()){
			final Player p = e.getPlayer();
			new BukkitRunnable() {
				@Override
				public void run() {
					instance.getVerificadores().CheckKey(p);
				}
			}.runTaskAsynchronously(instance);
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void Comando(PlayerCommandPreprocessEvent e){
		Player p = e.getPlayer();
		String[] args = e.getMessage().split(" ");
		//if(instance.instance.getServer().getHelpMap().getHelpTopic(e.getMessage()) == null){
		for(String cmds : instance.getConfig().getStringList("Config.Comandos_Kit")){
			if(args[0].equalsIgnoreCase(cmds.toLowerCase())){
				e.setCancelled(true);
				if(args.length == 1){
					if(instance.getConfig().getBoolean("Config.Ativar_Inv")){
						p.openInventory(instance.getKitsManager().getMain());
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Kits_Aberto").replace("&", "§"));
						p.playSound(p.getLocation(), Sound.CHEST_OPEN, 5.0F, 1.0F);
						return;
					}else{
						String kits = "";
						for(String s : instance.getConfig().getConfigurationSection("Config.Kits").getKeys(false)){
							if(p.hasPermission("fkitspro." + s)) kits += s + ", ";
						}
						if(kits.equals("")){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Kits").replace("&", "§"));
						}else{
							p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Kits_Disponiveis").replace("&", "§").replace("{kits}", kits.substring(0, kits.length()-2)));
						}
					}
				}else{
					if(args.length == 2){
						String kit = "";
						for(String s : instance.getConfig().getConfigurationSection("Config.Kits").getKeys(false))
							if(args[1].equalsIgnoreCase(s)){
								kit = s;
								break;
							}
						if(kit.equalsIgnoreCase("")){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Kit_Nao_Existe").replace("&", "§"));
							return;
						}else if(!p.hasPermission("fkitspro." + kit)){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
							return;
						}
						kitsmanager.darKit(null, p, kit);
						//p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Pegou_Kit").replace("&", "§").replace("{kit}", s));
						return;
					}else{
						if(!p.hasPermission("fkitspro.outros")){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
							return;
						}
						Player player = instance.getServer().getPlayer(args[2]);
						if(player == null){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§"));
							return;
						}
						for(String s : instance.getConfig().getConfigurationSection("Config.Kits").getKeys(false)){
							if(s.equalsIgnoreCase(args[1])){
								kitsmanager.darKit(p.getName(), player, s);
								player.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Ganhou_Kit").replace("&", "§").replace("{kit}", s).replace("{staff}", p.getName()));
								p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Deu_Kit").replace("&", "§").replace("{kit}", s).replace("{player}", player.getName()));
								return;
							}
						}
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Kit_Nao_Existe").replace("&", "§"));
					}
				}
			}
		}
		//}
	}

}

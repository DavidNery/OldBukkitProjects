package br.com.gearblocks.gearblocksapi;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Eventos implements Listener{

	private GearBlocksAPI instance = GearBlocksAPI.getGearBlocksAPI();

	@SuppressWarnings("deprecation")
	@EventHandler
	public void Join(PlayerJoinEvent e){
		Player p = e.getPlayer();
		String header = "";
		for(String s : instance.getConfig().getStringList("Tab.Header"))
			header += s.replace("&", "§").replace("{player}", p.getName()) + "\n";
		String footer = "";
		for(String s : instance.getConfig().getStringList("Tab.Footer"))
			footer += s.replace("&", "§").replace("{player}", p.getName()) + "\n";
		instance.setHeaderAndFooter(p, header.substring(0, header.length()-1), footer.substring(0, footer.length()-1));
		PlayerScoreBoard psb = new PlayerScoreBoard(instance, p, instance.getConfig().getString("ScoreBoard.DisplayName").replace("&", "§"), instance.getConfig().getStringList("ScoreBoard.Linhas"));
		psb.runTaskTimer(instance, 0, 1*20);
		instance.getScores().add(psb);
		String tag = instance.getPex().getGroup(instance.getPex().getUser(p).getParentIdentifiers().get(0)).getPrefix();
		if(!(tag.equals("") || tag == null)){
			for(int i = 0; i<instance.getTeams().size(); i++){
				if(instance.getTeams().get(i).getPrefix().equalsIgnoreCase(instance.getPex().getGroup(instance.getPex().getUser(p).getParentIdentifiers().get(0)).getPrefix().replace("&", "§"))){
					instance.getTeams().get(i).addPlayer(p);
					return;
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void Quit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		for(int i = 0; i<instance.getScores().size(); i++){
			if(instance.getScores().get(i).getPlayer().getName().equals(p.getName())){
				instance.getScores().get(i).cancel();
				instance.getScores().get(i).unregister();
				instance.getScores().remove(i);
				break;
			}
		}
		String tag = instance.getPex().getGroup(instance.getPex().getUser(p).getParentIdentifiers().get(0)).getPrefix();
		if(!((tag == null || tag.equals("")))){
			for(int i = 0; i<instance.getTeams().size(); i++){
				if(instance.getTeams().get(i).getPrefix().equalsIgnoreCase(instance.getPex().getGroup(instance.getPex().getUser(p).getParentIdentifiers().get(0)).getPrefix().replace("&", "§"))){
					if(instance.getTeams().get(i).hasPlayer(p)){
						instance.getTeams().get(i).removePlayer(p);
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void Chat(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		for(String s : instance.getConfig().getStringList("ScoreBoard.Linhas"))
			p.sendMessage(s);
		String grp = instance.getPex().getGroup(instance.getPex().getUser(p).getParentIdentifiers().get(0)).getName();
		String tag = instance.getConfig().getString("Chat." + grp + ".Prefix");
		e.setCancelled(true);
		for(Player on : instance.getServer().getOnlinePlayers())
			on.sendMessage(instance.getConfig().getString("Chat.Formato").replace("&", "§").replace("{tag}", (tag == null || tag.equals("")) ? "§7" : tag.replace("&", "§"))
					.replace("{msg}", e.getMessage())
					.replace("{player}", p.getName()));
	}

	@EventHandler
	public void Command(PlayerCommandPreprocessEvent e){
		Player p = e.getPlayer();
		String msg = e.getMessage().toLowerCase();
		if(msg.startsWith("/server") && (!p.hasPermission("gearblocks.buildserver"))){
			for(String s : instance.getConfig().getStringList("Sem_Permissao_Server")){
				p.sendMessage(s.replace("&", "§"));
			}
			e.setCancelled(true);
		}else if(msg.startsWith("/download") || msg.startsWith("/baixar") || msg.startsWith("/schematic upload")){
			p.performCommand("/download");
			e.setCancelled(true);
		}else if(msg.startsWith("/we toggle") || msg.startsWith("/we on") || msg.startsWith("/we off")){
			p.performCommand("/wea");
			e.setCancelled(true);
		}
	}

}

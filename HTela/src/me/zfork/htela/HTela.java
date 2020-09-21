package me.zfork.htela;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;

public class HTela extends JavaPlugin implements Listener{
	
	private Map<String, String> telando = new HashMap<String, String>();
	
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bHTela§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bzFork");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
		}else{
			getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
		}
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		getServer().getConsoleSender().sendMessage("§3==========[§bHTela§3]==========");
	}

	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§4==========[§cHTela§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §czFork");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cHTela§4]==========");
	}
	
	@EventHandler
	public void TP(PlayerTeleportEvent e){
		if(telando.containsValue(e.getPlayer().getName())){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void LC(ChatMessageEvent e){
		for(String telado : telando.keySet()){
			if(e.getRecipients().contains(telando.get(telado))) e.getRecipients().remove(telando.get(telado));
			if(e.getRecipients().contains(telado)) e.getRecipients().remove(telado);
		}
	}
	
	@EventHandler
	public void Chat(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		if(telando.containsKey(p.getName()) || telando.containsValue(p.getName())){
			e.setCancelled(true);
			for(String telado : telando.keySet()){
				if(telado.equals(p.getName())){
					getServer().getPlayer(telando.get(telado)).sendMessage(getConfig().getString("Config.Chat_Format").replace("&", "§").replace("{msg}", e.getMessage().replace("&", "§")).replace("{player}", p.getName()));
					p.sendMessage(getConfig().getString("Config.Chat_Format").replace("&", "§").replace("{msg}", e.getMessage().replace("&", "§")).replace("{player}", p.getName()));
					return;
				}else if(telando.get(telado).equals(p.getName())){
					getServer().getPlayer(telado).sendMessage(getConfig().getString("Config.Chat_Format").replace("&", "§").replace("{msg}", e.getMessage()).replace("{player}", p.getName()));
					getServer().getPlayer(telando.get(telado)).sendMessage(getConfig().getString("Config.Chat_Format").replace("&", "§").replace("{msg}", e.getMessage()).replace("{player}", p.getName()));
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void Morrer(PlayerDeathEvent e){
		if(telando.containsValue(e.getEntity().getName())){
			for(String telado : telando.keySet()){
				if(telando.get(telado).equals(e.getEntity().getName())){
					Player p = getServer().getPlayer(telado);
					p.teleport(getLocationString(getConfig().getString("Config.Saida")));
					p.sendMessage(getConfig().getString("Mensagem.Sucesso.Player_Morreu").replace("&", "§"));
					getServer().getPlayer(telando.get(telado)).setWalkSpeed(0.2f);
					telando.remove(telado);
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void Sair(PlayerQuitEvent e){
		if(telando.containsValue(e.getPlayer().getName())){
			for(String telado : telando.keySet()){
				if(telando.get(telado).equals(e.getPlayer().getName())){
					Player p = getServer().getPlayer(telado);
					p.teleport(getLocationString(getConfig().getString("Config.Saida")));
					p.sendMessage(getConfig().getString("Mensagem.Sucesso.Player_Deslogou").replace("&", "§"));
					e.getPlayer().setWalkSpeed(0.2f);
					for(String cmd : getConfig().getStringList("Config.Comandos")){
						Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd
								.replace("{ip}", e.getPlayer().getAddress().getAddress().getHostAddress()).replace("{player}", e.getPlayer().getName()));
					}
					telando.remove(telado);
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void Cmd(PlayerCommandPreprocessEvent e){
		Player p = e.getPlayer();
		if(telando.containsValue(e.getPlayer().getName())){
			e.setCancelled(true);
			return;
		}
		String[] args = e.getMessage().split(" ");
    	if(Bukkit.getServer().getHelpMap().getHelpTopic(args[0]) == null){
    		if(args[0].matches("^(\\/(?i)tela)")){
    			e.setCancelled(true);
    			if(!p.hasPermission("tela.usar")){
    				p.sendMessage(getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
    				return;
    			}else if(args.length == 1){
    				p.sendMessage(getConfig().getString("Mensagem.Erro.Informe_o_Player").replace("&", "§"));
    				return;
    			}
    			final Player player = Bukkit.getPlayer(args[1]);
    			if(player == null){
    				p.sendMessage(getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§"));
    				return;
    			}else if(player.getName().equalsIgnoreCase(p.getName())){
    				p.sendMessage(getConfig().getString("Mensagem.Erro.Se_Telar").replace("&", "§"));
    				return;
    			}
    			/*for(Player on : getServer().getOnlinePlayers()){
    				if(on.getName().equals(p.getName())) continue;
    				player.hidePlayer(on);
    			}*/
    			Location loc = getLocationString(getConfig().getString("Config.Spawn"));
    			p.teleport(new Location(loc.getWorld(), loc.getX(), (double) loc.getWorld().getHighestBlockYAt(loc)+1, loc.getZ()));
    			player.teleport(getLocationString(getConfig().getString("Config.Spawn")));
    			for(int i = 0; i<100; i++) player.sendMessage(" ");
    			player.setWalkSpeed(0);
    			if(player.getAllowFlight() == true){
    				player.setFlySpeed(0);
    				player.setAllowFlight(false);
    			}
    			player.sendMessage(getConfig().getString("Mensagem.Sucesso.Sendo_Telado").replace("&", "§"));
    			telando.put(p.getName(), player.getName());
    		}else if(args[0].matches("^(\\/(?i)retirartela)")){
    			e.setCancelled(true);
    			if(!p.hasPermission("tela.usar")){
    				p.sendMessage(getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
    				return;
    			}else if(!telando.containsKey(p.getName())){
    				p.sendMessage(getConfig().getString("Mensagem.Erro.Nao_Esta_Telando").replace("&", "§"));
    				return;
    			}
    			for(String telado : telando.keySet()){
    				if(telado.equals(p.getName())){
    					Player telando = getServer().getPlayer(this.telando.get(telado));
    					/*for(Player on : getServer().getOnlinePlayers()){
    						telando.showPlayer(on);
    					}*/
    					this.telando.remove(p.getName());
    					telando.teleport(getLocationString(getConfig().getString("Config.Saida")));
    	    			for(int i = 0; i<50; i++) telando.sendMessage(" ");
    	    			telando.setWalkSpeed(0.2f);
    	    			telando.sendMessage(getConfig().getString("Mensagem.Sucesso.Nao_Esta_Sendo_Telado").replace("&", "§"));
    	    			p.teleport(getLocationString(getConfig().getString("Config.Saida")));
    	    			p.sendMessage(getConfig().getString("Mensagem.Sucesso.Nao_Esta_Telando").replace("&", "§"));
    					return;
    				}
    			}
    		}else if(args[0].matches("^(\\/(?i)settela)")){
    			e.setCancelled(true);
    			if(!p.hasPermission("tela.usar")){
    				p.sendMessage(getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
    				return;
    			}
    			getConfig().set("Config.Spawn", getLocation(p.getLocation()));
    			saveConfig();
    			reloadConfig();
    			p.sendMessage(getConfig().getString("Mensagem.Sucesso.Setou_Spawn").replace("&", "§"));
    		}else if(args[0].matches("^(\\/(?i)settelasaida)")){
    			e.setCancelled(true);
    			if(!p.hasPermission("tela.usar")){
    				p.sendMessage(getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
    				return;
    			}
    			getConfig().set("Config.Saida", getLocation(p.getLocation()));
    			saveConfig();
    			reloadConfig();
    			p.sendMessage(getConfig().getString("Mensagem.Sucesso.Setou_Saida").replace("&", "§"));
    		}
    	}
	}
	
	public String getLocation(Location loc){
		StringBuilder sb = new StringBuilder();
		sb.append(loc.getWorld().getName()).append(" ").append(String.format("%.2f", loc.getX())).append(" ")
		.append(String.format("%.2f", loc.getY())).append(" ").append(String.format("%.2f", loc.getZ()));
		return sb.toString();
	}
	
	public Location getLocationString(String loc){
		String[] partes = loc.split(" ");
		return new Location(Bukkit.getWorld(partes[0]), Double.parseDouble(partes[1]), Double.parseDouble(partes[2]), Double.parseDouble(partes[3]));
	}

}

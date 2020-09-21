package br.com.gearblocks.gearblocksapi;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;

public class GearBlocksAPI extends JavaPlugin{

	private static String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	private ScoreboardManager sm;
	private Scoreboard scoreboard;
	private SimpleDateFormat sdf;
	private PermissionManager pex;
	private ArrayList<Team> teams;
	private ArrayList<PlayerScoreBoard> scores;

	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§bGearBlocks Team");
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			getServer().getConsoleSender().sendMessage("§3Config criada!");
		}
		this.sdf = new SimpleDateFormat("HH:mm:ss");
		if(getServer().getPluginManager().getPlugin("PermissionsEx") != null){
			this.pex = PermissionsEx.getPermissionManager();
			getServer().getConsoleSender().sendMessage("§3Hook feito com o PEX!");
		}else{
			getServer().getConsoleSender().sendMessage("§4PEX nao encontrado!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		this.sm = Bukkit.getScoreboardManager();
		this.scoreboard = sm.getNewScoreboard();
		this.scores = new ArrayList<>();
		this.teams = new ArrayList<>();
		int i = 0;
		boolean config = false;
		for(String grp : pex.getGroupNames()){
			if(!getConfig().contains("Grupos." + grp)){
				getConfig().set("Grupos." + grp + ".Prefix", pex.getGroup(grp).getPrefix().replaceAll("\\[|\\]", ""));
				config = true;
			}
			if(!getConfig().contains("Chat." + grp)){
				getConfig().set("Chat." + grp + ".Prefix", pex.getGroup(grp).getPrefix());
				config = true;
			}
			if(scoreboard.getTeam(i + grp)!= null)scoreboard.getTeam(i + grp).unregister();
			Team t = scoreboard.registerNewTeam(i + grp);
			t.setPrefix(pex.getGroup(grp).getPrefix().equals(null)? "§7" : pex.getGroup(grp).getPrefix().replace("&", "§"));
			teams.add(t);
			i++;
		}
		if(config){
			getServer().getConsoleSender().sendMessage("§3Grupos atualizados!");
			saveConfig();
			reloadConfig();
		}
		getServer().getConsoleSender().sendMessage("§3Sorted tab criado!");
		Bukkit.getServer().getPluginManager().registerEvents(new Eventos(), this);
		getServer().getConsoleSender().sendMessage("§3Eventos Registrados!");
		final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, new PacketType[] { PacketType.Play.Client.TAB_COMPLETE }){
			@EventHandler(priority = EventPriority.HIGHEST)
			public void onPacketReceiving(final PacketEvent e){
				if(e.getPacketType() == PacketType.Play.Client.TAB_COMPLETE){
					try{
						if(e.getPlayer().hasPermission("gearblocks.admin")) return;
						final PacketContainer packet = e.getPacket();
						final String message = ((String)packet.getSpecificModifier(String.class).read(0)).toLowerCase();
						if(message.startsWith("") && (!message.contains(" "))){
							e.getPlayer().sendMessage(getConfig().getString("Sem_Permissao_Tab").replace("&", "§"));
							e.setCancelled(true);
						}
					}catch(FieldAccessException e1){
						e1.printStackTrace();
					}
				}
			}
		});
		protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, new PacketType[] { PacketType.Play.Client.CHAT }){
			@EventHandler(priority = EventPriority.HIGHEST)
			public void onPacketReceiving(final PacketEvent e){
				if(e.getPacketType() == PacketType.Play.Client.CHAT){
					try{
						final PacketContainer packet = e.getPacket();
						final String message = ((String)packet.getSpecificModifier(String.class).read(0)).toLowerCase();
						if(message.startsWith("") && (!message.contains(" "))){
							e.getPlayer().sendMessage(getConfig().getString("Sem_Permissao_Tab").replace("&", "§"));
							e.setCancelled(true);
						}
					}catch(FieldAccessException e1){
						e1.printStackTrace();
					}
				}
			}
		});
		getServer().getConsoleSender().sendMessage("§3Anti tab ativado!");
		getServer().getConsoleSender().sendMessage("§2Plugin Habilitado");
	}

	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§bGearBlocks Team");
		getServer().getConsoleSender().sendMessage("§4Plugin Desabilitado");
	}

	public static GearBlocksAPI getGearBlocksAPI(){
		return(GearBlocksAPI)Bukkit.getServer().getPluginManager().getPlugin("GearBlocksAPI");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setHeaderAndFooter(Player p, String top, String bottom){
		try{
			Object nmsPlayer = p.getClass().getMethod("getHandle").invoke(p);
			Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
			Class chatSerializer = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer");
			Class chatComponent = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
			Class packetPlayerListHeaderFooter = Class.forName("net.minecraft.server." + version + ".PacketPlayOutPlayerListHeaderFooter");
			Constructor packetPlayerListHeaderFooterConstructor = packetPlayerListHeaderFooter.getDeclaredConstructor(chatComponent);

			Object tabHeader = chatSerializer.getMethod("a", String.class).invoke(chatSerializer, "{\"text\":\"" + top + "\"}");
			Object tabFooter = chatSerializer.getMethod("a", String.class).invoke(chatSerializer, "{\"text\":\"" + bottom + "\"}");
			Object headerPacket = packetPlayerListHeaderFooterConstructor.newInstance(tabHeader);

			Field field = headerPacket.getClass().getDeclaredField("b");
			field.setAccessible(true);
			field.set(headerPacket, tabFooter);
			connection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + version + ".Packet")).invoke(connection, headerPacket);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public ArrayList<PlayerScoreBoard> getScores(){
		return this.scores;
	}

	public Scoreboard getScoreboard(){
		return this.scoreboard;
	}

	public ArrayList<Team> getTeams(){
		return this.teams;
	}

	public PermissionManager getPex(){
		return this.pex;
	}

	public SimpleDateFormat getSdf(){
		return this.sdf;
	}

}

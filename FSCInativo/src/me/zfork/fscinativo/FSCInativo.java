package me.zfork.fscinativo;

import java.io.File;

import me.zfork.fscinativo.utils.JSONMessage;
import me.zfork.fscinativo.utils.JSONMessage.HoverAction;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FSCInativo extends JavaPlugin {

	private static String PLUGIN_NAME;
	private SimpleClans sc;
	private int totalclans, totalplayers;

	public void onEnable(){
		PLUGIN_NAME = getDescription().getName();
		ConsoleCommandSender sender = getServer().getConsoleSender();
		sender.sendMessage("§3==========[§b" + PLUGIN_NAME + "§3]==========");
		sender.sendMessage(" §3Status: §bAtivado");
		sender.sendMessage(" §3By: §bzFork");
		sender.sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			sender.sendMessage(" §3Config: §bCriada");
		}else{
			sender.sendMessage(" §3Config: §bJa Existente");
		}
		sc = (SimpleClans) getServer().getPluginManager().getPlugin("SimpleClans");
		if(sc == null){
			sender.sendMessage(" §4SimpleClans nao encontrado");
			return;
		}
		totalclans = getConfig().getInt("Config.Clans_ToolTip");
		totalplayers = getConfig().getInt("Config.Players_ToolTip");
		new BukkitRunnable() {
			@Override
			public void run() {
				JSONMessage jsonplayers = new JSONMessage(), jsonclans = new JSONMessage();
				String splayers = "", sclans = "";
				int i = 0, j = 0;
				long dias;
				for(ClanPlayer cp : sc.getClanManager().getAllClanPlayers()){
					dias = cp.getInactiveDays();
					if(dias >= getConfig().getInt("Config.Dias_Inativos_Player")){
						Clan clan = cp.getClan();
						if(i < totalplayers) splayers += getConfig().getString("Config.Format_Player_ToolTip").replace("&", "§")
								.replace("{player}", cp.getName()).replace("{clan}", clan == null ? "" : clan.getTagLabel()).replace("{dias}", dias+"") + "\n";
						i++;
						if(clan != null){
							clan.removeMember(cp.getName());
							if(cp.isLeader()){
								clan.disband();
								if(j < totalclans) sclans += getConfig().getString("Config.Format_Clan_ToolTip").replace("&", "§").replace("{clan}", clan.getTagLabel()) + "\n";
								j++;
							}
						}
					}
				}
				if(splayers.length() > 0){
					String tps = "", tcs = "";
					for(String s : getConfig().getStringList("Config.ToolTip_Players_Style"))
						tps += s.replace("&", "§").replace("{players}", splayers.substring(0, splayers.length()-1)) + "\n";
					for(String s : getConfig().getStringList("Config.ToolTip_Clans_Style"))
						tcs += s.replace("&", "§").replace("{clans}", sclans.substring(0, sclans.length()-1)) + "\n";
					tps = tps.substring(0, tps.length()-1);
					tcs = tcs.substring(0, tcs.length()-1);
					jsonplayers.addText(getConfig().getString("Config.Mensagem_Players").replace("{qnt}", i+"")).withHoverAction(HoverAction.SHOW_TEXT, tps);
					jsonclans.addText(getConfig().getString("Config.Mensagem_Clans").replace("{qnt}", j+"")).withHoverAction(HoverAction.SHOW_TEXT, tcs);
					for(Player on : getServer().getOnlinePlayers()){
						jsonplayers.sendJson(on);
						jsonclans.sendJson(on);
					}
				}
			}
		}.runTaskTimerAsynchronously(this, getConfig().getInt("Config.Tempo_Verificar")*20, getConfig().getInt("Config.Tempo_Verificar")*20);
		sender.sendMessage("§3==========[§b" + PLUGIN_NAME + "§3]==========");
	}

	public void onDisable(){
		ConsoleCommandSender sender = getServer().getConsoleSender();
		sender.sendMessage("§4==========[§c" + PLUGIN_NAME + "§4]==========");
		sender.sendMessage(" §4Status: §cDesativado");
		sender.sendMessage(" §4By: §czFork");
		sender.sendMessage(" §4Versao: §c" + getDescription().getVersion());
		sender.sendMessage("§4==========[§c" + PLUGIN_NAME + "§4]==========");
	}

}

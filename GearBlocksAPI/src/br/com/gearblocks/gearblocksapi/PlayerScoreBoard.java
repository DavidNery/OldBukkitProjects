package br.com.gearblocks.gearblocksapi;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerScoreBoard extends BukkitRunnable{

	private GearBlocksAPI instance;
	private Scoreboard scoreboard;
	private Objective objective;
	private Player p;
	private List<String> lines;
	private String SB_NAME;

	public PlayerScoreBoard(GearBlocksAPI instance, Player p, String display, List<String> lines){
		this.instance = instance;
		this.p = p;
		this.lines = lines;
		this.scoreboard = instance.getScoreboard();
		this.SB_NAME = "gba" + p.getName();
		this.SB_NAME = SB_NAME.length() > 16 ? SB_NAME.substring(0, 16) : SB_NAME;
		if(scoreboard.getObjective(SB_NAME) != null)
			this.objective = scoreboard.getObjective(SB_NAME);
		else
			this.objective = scoreboard.registerNewObjective(SB_NAME, "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(display.replace("&", "§").replace("{player}", p.getName()));
		p.setScoreboard(scoreboard);
	}

	@Override
	public void run() {
		for(String s : scoreboard.getEntries())
			scoreboard.resetScores(s);
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("America/Sao_Paulo"));
		String tag = instance.getPex().getGroup(instance.getPex().getUser(p).getParentIdentifiers().get(0)).getPrefix();
		int j = lines.size()-1;
		for(String linha : lines){
			if(linha.replaceAll("((&|§)[a-f0-9]+)?(\\s+)?", "").equals("") && j == lines.size()-1){
					objective.getScore(ChatColor.RESET.toString()).setScore(j);
			}else{
				objective.getScore(linha.replace("&", "§").replace("{player}", p.getName())
						.replace("{data}", instance.getSdf().format(calendar.getTime()))
						.replace("{rank}", (tag == null || tag.equals("")) ? "§7Default" : tag.replace("&", "§"))).setScore(j);
			}
			j--;
		}
	}

	public Player getPlayer(){
		return this.p;
	}

	public void unregister(){
		instance.getScoreboard().getObjective(SB_NAME).unregister();
	}

}

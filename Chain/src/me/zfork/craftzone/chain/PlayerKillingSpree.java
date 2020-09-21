package me.zfork.craftzone.chain;

import org.bukkit.scheduler.BukkitTask;

public class PlayerKillingSpree {
	
	private int kills;
	private BukkitTask task;
	
	public PlayerKillingSpree(){
		this.kills = 0;
		this.task = null;
	}
	
	public void setKills(int kills) {
		this.kills = kills;
	}
	
	public int getKills() {
		return kills;
	}
	
	public void setTask(BukkitTask task) {
		this.task = task;
	}
	
	public BukkitTask getTask() {
		return task;
	}

}

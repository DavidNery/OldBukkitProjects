package me.zfork.hutils.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join implements Listener{
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void EntrarNormal(PlayerJoinEvent e){
		e.setJoinMessage(null);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void EntrarMonitor(PlayerJoinEvent e){
		e.setJoinMessage(null);
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void EntrarLowest(PlayerJoinEvent e){
		e.setJoinMessage(null);
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void EntrarLow(PlayerJoinEvent e){
		e.setJoinMessage(null);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void EntrarHighest(PlayerJoinEvent e){
		e.setJoinMessage(null);
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void EntrarHigh(PlayerJoinEvent e){
		e.setJoinMessage(null);
	}

}

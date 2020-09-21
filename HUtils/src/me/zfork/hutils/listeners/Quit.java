package me.zfork.hutils.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Quit implements Listener{
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void SairNormal(PlayerQuitEvent e){
		e.setQuitMessage(null);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void SairMonitor(PlayerQuitEvent e){
		e.setQuitMessage(null);
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void SairLowest(PlayerQuitEvent e){
		e.setQuitMessage(null);
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void SairLow(PlayerQuitEvent e){
		e.setQuitMessage(null);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void SairHighest(PlayerQuitEvent e){
		e.setQuitMessage(null);
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void SairHigh(PlayerQuitEvent e){
		e.setQuitMessage(null);
	}

}

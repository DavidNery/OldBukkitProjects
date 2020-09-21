package me.dery.dkeyutilities.API;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DeleteKeyEvent extends Event implements Cancellable{
	
	private static final HandlerList handlers = new HandlerList();
	private CommandSender p;
	private String key;
	private boolean cancelled;
	
	public DeleteKeyEvent(CommandSender p, String key){
		this.p = p;
		this.key = key;
	}
	
	public CommandSender getPlayer(){
        return p;
    }
	
	public String getKey(){
        return key;
    }
 
    public boolean isCancelled(){
        return cancelled;
    }
 
    public void setCancelled(boolean cancel){
        cancelled = cancel;
    }
 
    public HandlerList getHandlers(){
        return handlers;
    }
 
    public static HandlerList getHandlerList(){
        return handlers;
    }

}

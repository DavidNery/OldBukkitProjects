package me.dery.dkeyutilities.API;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GetKeyEvent extends Event implements Cancellable{
	
	private static final HandlerList handlers = new HandlerList();
	private CommandSender p;
	private String key;
	private boolean cancelled;
	private double price;
	
	public GetKeyEvent(CommandSender p, String key, double price){
		this.p = p;
		this.key = key;
		this.price = price;
	}
	
	public CommandSender getPlayer(){
        return p;
    }
	
	public String getKey(){
        return key;
    }
	
	public double getPrice(){
        return price;
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

package me.zfork.fvip.api;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DeleteKeyEvent extends Event implements Cancellable {

	public static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private CommandSender sender;
	private String key;

	public DeleteKeyEvent(CommandSender sender, String key){
		this.sender = sender;
		this.key = key;
	}

	public CommandSender getSender(){
		return this.sender;
	}

	public String getKey(){
		return this.key;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public HandlerList getHandlers(){
		return handlers;
	}

	public static HandlerList getHandlerList(){
		return handlers;
	}

}
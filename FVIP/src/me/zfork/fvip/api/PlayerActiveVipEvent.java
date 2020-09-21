package me.zfork.fvip.api;

import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerActiveVipEvent extends Event implements Cancellable {

	public static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private Player player;
	private ActivationType activationtype;
	private String key;
	private String vip;
	private Date tempo;

	public enum ActivationType{
		KEY, MERCADOPAGO, PAGSEGURO, PAYPAL
	}
	
	public PlayerActiveVipEvent(Player player, ActivationType activationtype, String key, String vip, Date tempo){
		this.player = player;
		this.activationtype = activationtype;
		this.key = key;
		this.vip = vip;
		this.tempo = tempo;
	}

	public Player getPlayer(){
		return this.player;
	}

	public ActivationType getActivationType(){
		return this.activationtype;
	}

	public String getKey(){
		return this.key;
	}
	
	public String getVip(){
		return this.vip;
	}
	
	public Date getTempo(){
		return this.tempo;
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

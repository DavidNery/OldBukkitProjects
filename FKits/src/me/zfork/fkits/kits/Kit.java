package me.zfork.fkits.kits;

import java.util.HashMap;
import java.util.List;

import org.bukkit.inventory.Inventory;

public class Kit {
	
	private String nome;
	private List<Object> items;
	private HashMap<String, HashMap<Integer, Inventory>> inventory;
	private int delay;
	
	public Kit(String nome, List<Object> items, int delay){
		this.nome = nome;
		this.items = items;
		this.inventory = new HashMap<String, HashMap<Integer, Inventory>>();
		this.delay = delay;
	}
	
	public String getNome(){
		return this.nome;
	}
	
	public List<Object> getItems(){
		return this.items;
	}
	
	public long getDelay(){
		return this.delay;
	}
	
	public void setDelay(int delay){
		this.delay = delay;
	}
	
	public HashMap<String, HashMap<Integer, Inventory>> getInventory(){
		return this.inventory;
	}

}

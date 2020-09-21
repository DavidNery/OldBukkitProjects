package me.zfork.fkitspro;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

public class Kit {
	
	private String nome;
	private double preco;
	private ArrayList<ItemStack> items;
	private long tempo;
	
	public Kit(String nome, double preco, long tempo){
		this.nome = nome;
		this.preco = preco;
		this.items = new ArrayList<ItemStack>();
		this.tempo = tempo;
	}
	
	public String getNome(){
		return this.nome;
	}
	
	public double getPreco(){
		return this.preco;
	}
	
	public ArrayList<ItemStack> getItems(){
		return this.items;
	}
	
	public long getTempo(){
		return this.tempo;
	}
	

}

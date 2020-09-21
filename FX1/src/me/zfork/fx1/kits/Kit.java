package me.zfork.fx1.kits;

import java.util.ArrayList;

public class Kit {
	
	private String nome;
	private ArrayList<String> items;
	private String[] armadura;
	private int custo;
	private boolean perde;
	private boolean padrao;
	private boolean proprio;
	
	public Kit(String nome){
		this.nome = nome;
		this.items = new ArrayList<String>();
		this.armadura = new String[4];
		for(int i = 0; i<4; i++) armadura[i] = "item:0";
		this.custo = 0;
		this.perde = true;
		this.padrao = false;
		this.proprio = false;
	}
	
	public String getNome(){
		return this.nome;
	}
	
	public ArrayList<String> getItems(){
		return this.items;
	}
	
	public String[] getArmadura(){
		return this.armadura;
	}
	
	public void setItem(int posicao, String item){
		this.armadura[posicao] = item;
	}
	
	public int getCusto(){
		return this.custo;
	}
	
	public void setCusto(int custo){
		this.custo = custo;
	}
	
	public void setPerde(boolean perde){
		this.perde = perde;
	}
	
	public boolean getPerde(){
		return this.perde;
	}
	
	public void setPadrao(boolean padrao){
		this.padrao = padrao;
	}
	
	public boolean getPadrao(){
		return this.padrao;
	}
	
	public void setProprio(boolean proprio){
		this.proprio = proprio;
	}
	
	public boolean getProprio(){
		return this.proprio;
	}

}

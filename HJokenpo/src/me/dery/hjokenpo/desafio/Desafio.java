package me.dery.hjokenpo.desafio;

import org.bukkit.scheduler.BukkitTask;

import me.dery.hjokenpo.HJokenpo;

public class Desafio {
	
	private HJokenpo instance = HJokenpo.getHJokenpo();
	private String p1, p2;
	private double premio;
	private BukkitTask task;
	private boolean acontecendo;
	private int escolhap1, escolhap2;
	private boolean escolheup1, escolheup2;
	
	public Desafio(String p1, String p2){
		this.p1 = p1;
		this.p2 = p2;
		this.premio = instance.getPadrao();
		this.acontecendo = false;
		this.escolhap1 = 0;
		this.escolhap2 = 0;
	}
	
	public Desafio(String p1, String p2, double premio){
		this.p1 = p1;
		this.p2 = p2;
		this.premio = premio;
		this.acontecendo = false;
		this.escolhap1 = 0;
		this.escolhap2 = 0;
	}
	
	public String getP1(){
		return this.p1;
	}
	
	public String getP2(){
		return this.p2;
	}
	
	public double getPremio(){
		return this.premio;
	}
	
	public void setTask(BukkitTask task){
		this.task = task;
	}
	
	public BukkitTask getTask(){
		return this.task;
	}
	
	public boolean getAcontecendo(){
		return this.acontecendo;
	}
	
	public void setAcontecendo(boolean acontecendo){
		this.acontecendo = acontecendo;
	}
	
	public int getEscolhaP1(){
		return this.escolhap1;
	}
	
	public int getEscolhaP2(){
		return this.escolhap2;
	}
	
	public void setEscolhaP1(int escolhap1){
		this.escolhap1 = escolhap1;
	}
	
	public void setEscolhaP2(int escolhap2){
		this.escolhap2 = escolhap2;
	}
	
	public boolean getEscolheuP1(){
		return this.escolheup1;
	}
	
	public boolean getEscolheuP2(){
		return this.escolheup2;
	}
	
	public void setEscolheuP1(boolean escolheup1){
		this.escolheup1 = escolheup1;
	}
	
	public void setEscolheuP2(boolean escolheup2){
		this.escolheup2 = escolheup2;
	}

}

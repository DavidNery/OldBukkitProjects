package me.zfork.fx1.x1;

import me.zfork.fx1.arenas.Arena;
import me.zfork.fx1.kits.Kit;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class X1 {
	
	private Player player1;
	private Player player2;
	private double aposta = 0;
	private Kit kit;
	private Arena arena;
	private BukkitTask task;
	private boolean acontecendo;
	private int tempo;
	
	public X1(Player player1, Player player2, Arena arena, Kit kit){
		this.player1 = player1;
		this.player2 = player2;
		this.arena = arena;
		this.kit = kit;
		this.task = null;
		this.acontecendo = false;
		this.tempo = 0;
	}
	
	public Player getPlayer1(){
		return this.player1;
	}
	
	public Player getPlayer2(){
		return this.player2;
	}
	
	public void setPlayer1(Player player1){
		this.player1 = player1;
	}
	
	public void setPlayer2(Player player2){
		this.player2 = player2;
	}
	
	public double getAposta(){
		return this.aposta;
	}
	
	public void setAposta(double aposta){
		this.aposta = aposta;
	}
	
	public Kit getKit(){
		return this.kit;
	}
	
	public void setKit(Kit kit){
		this.kit = kit;
	}
	
	public Arena getArena(){
		return this.arena;
	}
	
	public void setArena(Arena arena){
		this.arena = arena;
	}
	
	public BukkitTask getTask(){
		return this.task;
	}
	
	public void setTask(BukkitTask task){
		this.task = task;
	}
	
	public boolean getAcontecendo(){
		return this.acontecendo;
	}
	
	public void setAcontecendo(boolean acontecendo){
		this.acontecendo = acontecendo;
	}
	
	public int getTempo(){
		return this.tempo;
	}
	
	public void setTempo(int tempo){
		this.tempo = tempo;
	}

}

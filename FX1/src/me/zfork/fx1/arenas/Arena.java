package me.zfork.fx1.arenas;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;


public class Arena {
	
	private String nome;
	private boolean usando;
	private Location player1, player2, camarote, saida;
	private File f;
	private FileConfiguration fc;
	
	public Arena(String nome){
		this.nome = nome;
		this.usando = false;
	}
	
	public String getNome(){
		return this.nome;
	}
	
	public void setUsando(boolean usando){
		this.usando = usando;
	}
	
	public boolean getUsando(){
		return this.usando;
	}
	
	public void setLocationPlayer1(Location player1){
		this.player1 = player1;
	}
	
	public Location getLocationPlayer1(){
		return this.player1;
	}
	
	public void setLocationPlayer2(Location player2){
		this.player2 = player2;
	}
	
	public Location getLocationPlayer2(){
		return this.player2;
	}
	
	public void setLocationCamarote(Location camarote){
		this.camarote = camarote;
	}
	
	public Location getLocationCamarote(){
		return this.camarote;
	}
	
	public void setLocationSaida(Location saida){
		this.saida = saida;
	}
	
	public Location getLocationSaida(){
		return this.saida;
	}
	
	public void setFC(FileConfiguration fc){
		this.fc = fc;
	}
	
	public FileConfiguration getFC(){
		return this.fc;
	}
	
	public File getFile(){
		return this.f;
	}
	
	public void setFile(File f){
		this.f = f;
	}

}

package me.dery.hjokenpo.desafio;

import java.util.ArrayList;

import me.dery.hjokenpo.HJokenpo;

import org.bukkit.scheduler.BukkitRunnable;

public class DesafioManager {
	
	public static ArrayList<Desafio> desafios = new ArrayList<Desafio>();
	public static ArrayList<String> esta = new ArrayList<String>();
	public static HJokenpo instance = HJokenpo.getHJokenpo();
	
	public static ArrayList<Desafio> getDesafios(){
		return desafios;
	}
	
	public static boolean isDesafiado(String p, String player){
		if(desafios.size() > 0){
			for(Desafio desafio : desafios){
				if(desafio.getP2().equalsIgnoreCase(p) && desafio.getP1().equalsIgnoreCase(player)) return true;
			}
		}
		return false;
	}
	
	public static boolean Desafiou(String p, String player){
		if(desafios.size() > 0){
			for(Desafio desafio : desafios){
				if(desafio.getP1().equalsIgnoreCase(p) && desafio.getP2().equalsIgnoreCase(player)) return true;
			}
		}
		return false;
	}
	
	public static boolean hasDesafio(Desafio desafio){
		if(desafios.size() > 0){
			if(desafios.contains(desafio)) return true;
		}
		return false;
	}
	
	public static Desafio getDesafio(String p1, String p2){
		if(desafios.size() > 0){
			for(Desafio desafio : desafios){
				if(desafio.getP1().equalsIgnoreCase(p1) && desafio.getP2().equalsIgnoreCase(p2)) return desafio;
			}
		}
		return null;
	}
	
	public static void addDesafio(final Desafio desafio){
		if(!hasDesafio(desafio)){
			desafios.add(desafio);
			desafio.setTask(new BukkitRunnable(){
				@Override
				public void run(){
					if(desafio.getTask() != null){
						if(desafio.getAcontecendo() == false) removeDesafio(desafio);
					}
				}
			}.runTaskLater(instance, 30*20));
		}
	}
	
	public static void removeDesafio(Desafio desafio){
		if(hasDesafio(desafio)){
			if(desafio.getTask() != null) desafio.getTask().cancel();
			esta.remove(desafio.getP1());
			esta.remove(desafio.getP2());
			desafios.remove(desafio);
		}
	}
	
	public static void addPlayer(String player){
		if(!esta.contains(player)){
			esta.add(player);
		}
	}
	
	public static void removePlayer(String player){
		if(esta.size() > 0){
			if(esta.contains(player)){
				esta.remove(player);
			}
		}
	}
	
	public static boolean hasPlayer(String player){
		if(esta.size() > 0){
			if(esta.contains(player)){
				return true;
			}
		}
		return false;
	}

}

package me.zfork.fx1.x1;

import java.util.ArrayList;
import java.util.HashMap;

import me.zfork.fx1.FX1;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class X1Manager {
	
	private FX1 instance;
	private ArrayList<X1> x1s;
	private HashMap<String, Long> delay;
	
	public X1Manager(FX1 instance){
		this.instance = instance;
		this.x1s = new ArrayList<X1>();
		this.delay = new HashMap<String, Long>();
	}
	
	public HashMap<String, Long> getDelay(){
		return this.delay;
	}
	
	public boolean hasPlayerInX1(String player){
		for(X1 x1 : x1s){
			if(x1.getAcontecendo() && (x1.getPlayer1().getName().equalsIgnoreCase(player) || x1.getPlayer2().getName().equalsIgnoreCase(player))) return true;
		}
		return false;
	}
	
	public void addX1(X1 x1){
		x1s.add(x1);
	}
	
	public void removeX1(X1 x1){
		x1s.remove(x1);
	}
	
	public ArrayList<X1> getX1s(){
		return this.x1s;
	}
	
	public X1 getX1ByPlayer(Player p){
		for(X1 x1 : x1s) if(x1.getPlayer1().getName().equalsIgnoreCase(p.getName()) || x1.getPlayer2().getName().equalsIgnoreCase(p.getName())) return x1;
		return null;
	}
	
	public X1 getX1ByPlayerAndPlayer(Player p, Player player){
		for(X1 x1 : x1s)
			if((x1.getPlayer1().getName().equalsIgnoreCase(p.getName()) || x1.getPlayer2().getName().equalsIgnoreCase(p.getName()))
					&& (x1.getPlayer1().getName().equalsIgnoreCase(player.getName()) || x1.getPlayer2().getName().equalsIgnoreCase(player.getName()))) return x1;
		return null;
	}
	
	public void startX1(final X1 x1){
		if(x1.getTask() != null) x1.getTask().cancel();
		x1.getArena().setUsando(true);
		if(!x1s.contains(x1)) addX1(x1);
		x1.setTask(new BukkitRunnable() {
			@Override
			public void run(){
				for(String msg : instance.getConfig().getStringList("Mensagem.BC.X1_Encerrado_Tempo")){
					for(Player on : instance.getServer().getOnlinePlayers()){
						on.sendMessage(msg.replace("&", "§").replace("{player1}", x1.getPlayer1().getName()).replace("{player2}", x1.getPlayer2().getName()));
					}
				}
				stopX1(x1, null);
			}
		}.runTaskLater(instance, instance.getConfig().getInt("Config.Tempo_X1")*60*20));
	}
	
	public void stopX1(final X1 x1, final Player vencedor){
		if(x1.getTask() != null){
			x1.getTask().cancel();
			x1.setTask(null);
		}
		if(x1.getKit().getProprio() == false || (x1.getKit().getProprio() && x1.getKit().getPerde() == false)){
			x1.getPlayer1().getInventory().clear();
			x1.getPlayer1().getInventory().setArmorContents(null);
			if(instance.getInventario().containsKey(x1.getPlayer1().getName())){
				for(ItemStack item : instance.getInventario().get(x1.getPlayer1().getName())) if(item != null) x1.getPlayer1().getInventory().addItem(item);
				x1.getPlayer1().getInventory().setArmorContents(instance.getArmor().get(x1.getPlayer1().getName()));
			}
			x1.getPlayer2().getInventory().clear();
			x1.getPlayer2().getInventory().setArmorContents(null);
			if(instance.getInventario().containsKey(x1.getPlayer2().getName())){
				for(ItemStack item : instance.getInventario().get(x1.getPlayer2().getName())) if(item != null) x1.getPlayer2().getInventory().addItem(item);
				x1.getPlayer2().getInventory().setArmorContents(instance.getArmor().get(x1.getPlayer2().getName()));
			}
		}
		if(instance.getInventario().containsKey(x1.getPlayer1().getName())){
			instance.getInventario().remove(x1.getPlayer1().getName());
			instance.getInventario().remove(x1.getPlayer2().getName());
		}
		if(instance.getInventario().containsKey(x1.getPlayer2().getName())){
			instance.getArmor().remove(x1.getPlayer1().getName());
			instance.getArmor().remove(x1.getPlayer2().getName());
		}
		if(vencedor != null){
			instance.getEcon().depositPlayer(vencedor.getName(), x1.getAposta()*2);
		}else{
			instance.getEcon().depositPlayer(x1.getPlayer1().getName(), (x1.getAposta()/2)+x1.getKit().getCusto());
			instance.getEcon().depositPlayer(x1.getPlayer2().getName(), x1.getAposta()/2);
		}
		x1.setPlayer1(null);
		x1.setPlayer2(null);
		removeX1(x1);
	}

}

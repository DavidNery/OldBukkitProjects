package me.zfork.fadmin.listeners;

import java.util.HashMap;

import me.zfork.fadmin.FAdmin;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Listeners implements Listener{

	private HashMap<String, String> innofall;
	private FAdmin instance;

	public Listeners(FAdmin instance) {
		this.instance = instance;
		instance.getServer().getPluginManager().registerEvents(this, instance);
		innofall = new HashMap<>();
	}

	@EventHandler
	public void Interact(PlayerInteractEntityEvent e){
		if(e.getRightClicked() instanceof Player){
			Player p = e.getPlayer();
			if(p.getItemInHand() != null && p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName() && p.isOp()){
				Player target = (Player) e.getRightClicked();
				if(p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§cTeste de NoFall")){
					p.sendMessage("§b§lFADMIN §7Testando NoFall...");
					target.setAllowFlight(true);
					target.setVelocity(new Vector(target.getVelocity().getX(), 10, target.getVelocity().getZ()));
					target.setAllowFlight(false);
					innofall.put(target.getName(), p.getName());
				}
			}
		}
	}
	
	@EventHandler
	public void Knock(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
			Player p = (Player) e.getDamager();
			if(p.getItemInHand() != null && p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName() && p.isOp()){
				Player target = (Player) e.getEntity();
				if(p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§cTeste de Knockback")){
					p.sendMessage("§b§lFADMIN §7Testando Knockback...");
					Location from = target.getLocation();
					new BukkitRunnable() {
						@Override
						public void run() {
							p.sendMessage("§a§lFADMIN §7O player §a§l" + target.getName() + " §7" + (!from.equals(target.getLocation()) ? "não " : "") + "está de antikb.");
						}
					}.runTaskLater(instance, 20L);
				}
			}
		}
	}

	@EventHandler
	public void Move(PlayerMoveEvent e){
		if((int) e.getFrom().getY() > (int) e.getTo().getY()){
			Location to = e.getTo().subtract(0, 1, 0);
			if(!(to.getBlock().isEmpty() || to.getBlock().isLiquid())){
				Player p = e.getPlayer();
				if(innofall.containsKey(p.getName())){
					new BukkitRunnable() {
						Player player = instance.getServer().getPlayer(innofall.get(p.getName()));
						@Override
						public void run() {
							player.sendMessage("§a§lFADMIN §7O player §a§l" + p.getName() + " §7" + (p.getHealth() != 0 ? "não " : "") + "levou dano");
							innofall.remove(p.getName());
						}
					}.runTaskLater(instance, 2L);
				}
			}
		}
	}

}

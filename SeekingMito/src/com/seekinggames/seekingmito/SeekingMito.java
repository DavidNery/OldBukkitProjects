package com.seekinggames.seekingmito;

import java.io.File;
import java.util.Random;

import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SeekingMito extends JavaPlugin{
	
	private BukkitTask task;

	public void onEnable() {
		getServer().getConsoleSender().sendMessage("§bHabilitando Plugin...");
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			getServer().getConsoleSender().sendMessage("§2Config criada!");
		}
		getCommand("mito").setExecutor(new Mito());
		getCommand("setmito").setExecutor(new Mito());
		Bukkit.getServer().getPluginManager().registerEvents(new Eventos(), this);
		getServer().getConsoleSender().sendMessage("§aPlugin habilitado!");
	}

	public void onDisable() {
		HandlerList.unregisterAll(this);
		getServer().getConsoleSender().sendMessage("§cPlugin Desabilitado!");
	}

	public static SeekingMito getSeekingMito() {
		return (SeekingMito) Bukkit.getServer().getPluginManager().getPlugin("SeekingMito");
	}

	public void setMito(Player player) {
		for(String msg : getConfig().getStringList("Novo_Mito")){
			for(Player p : Bukkit.getOnlinePlayers()){
				p.sendMessage(msg.replace("&", "§").replace("{mito}", player.getName()));
				p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 10.0F, 5.0F);
			}
		}
		for(int i = 0; i<10; i++){
			shootFireWork(player);
		}
		sendParticle(player);
		getConfig().set("Mito", player.getName());
		saveConfig();
		reloadConfig();
	}

	public void sendTitle(Player player, String title) {
		PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, 
				ChatSerializer.a("{\"text\":\"" + title + "\"}"), 20, 40, 30);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(titlePacket);
	}

	public void sendParticle(final Player player){
		if(task != null) task.cancel();
		task = new BukkitRunnable(){
			double phi = 0;
			public void run(){
				phi = phi + Math.PI/3;
				double x, y, z;
				Location location1 = player.getLocation();
				for (double t = 0; t <= 2*Math.PI; t = t + Math.PI/16){
					for (double i = 0; i <= 1; i = i + 1){
						x = 0.4*(2*Math.PI-t)*0.5*Math.cos(t + phi + i*Math.PI);
						y = 0.5*t;
						z = 0.4*(2*Math.PI-t)*0.5*Math.sin(t + phi + i*Math.PI);
						location1.add(x, y, z);
						ParticleEffect.REDSTONE.display(0, 0, 0, 0, 2, location1, 5);
						location1.subtract(x,y,z);
					}
				}
				if(phi > 10*Math.PI){
					cancel();
				}
			}
		}.runTaskTimer(getSeekingMito(), 0, 3);
	}
	
	public static void shootFireWork(Player player) {
		Location loc = player.getLocation();
		Firework fw = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fm = fw.getFireworkMeta();
		fm.addEffect(FireworkEffect.builder().with(getFireworkRandomType()).withColor(getFireworkRandomColor(), getFireworkRandomColor())
				.withFade(getFireworkRandomColor()).flicker(true).build());
		fm.setPower(getFireworkRandomPower());
		fw.setFireworkMeta(fm);
	}
	
	public static Color getFireworkRandomColor() {
		Color c = null;
		Random r = new Random();
		int next = r.nextInt(17);
		if(next == 0) {
			c = Color.AQUA;
		}else if(next == 1) {
			c = Color.BLACK;
		}else if(next == 2) {
			c = Color.BLUE;
		}else if(next == 3) {
			c = Color.FUCHSIA;
		}else if(next == 4) {
			c = Color.GRAY;
		}else if(next == 5) {
			c = Color.GREEN;
		}else if(next == 6) {
			c = Color.LIME;
		}else if(next == 7) {
			c = Color.MAROON;
		}else if(next == 8) {
			c = Color.NAVY;
		}else if(next == 9) {
			c = Color.OLIVE;
		}else if(next == 10) {
			c = Color.ORANGE;
		}else if(next == 11) {
			c = Color.PURPLE;
		}else if(next == 12) {
			c = Color.RED;
		}else if(next == 13) {
			c = Color.SILVER;
		}else if(next == 14) {
			c = Color.TEAL;
		}else if(next == 15) {
			c = Color.WHITE;
		}else if(next == 16) {
			c = Color.YELLOW;
		}
		return c;
	}
	
	public static Type getFireworkRandomType() {
		Type t = null;
		Random r = new Random();
		int next = r.nextInt(5);
		if(next == 0) {
			t = Type.BALL;
		}else if(next == 1) {
			t = Type.BALL_LARGE;
		}else if(next == 2) {
			t = Type.BURST;
		}else if(next == 3) {
			t = Type.CREEPER;
		}else if(next == 4) {
			t = Type.STAR;
		}
		return t;
	}
	
	public static int getFireworkRandomPower() {
		Random r = new Random();
		return r.nextInt(2);
	}
	
	/*public static Boolean getFireworkRandomBoolean() {
		Random r = new Random();
		return r.nextBoolean();
	}*/

}

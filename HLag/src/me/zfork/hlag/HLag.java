package me.zfork.hlag;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class HLag extends JavaPlugin implements Listener{
	
	private BukkitTask task;
	private int vez, quantidade = 0, raio, raiomob;
	private boolean drop = true;
	
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bHLag§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bzFork");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
		}else{
			getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
		}
		Iniciar();
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		raio = getConfig().getInt("Config.Mob_Spawn_Raio");
		raiomob = getConfig().getInt("Config.Mob_Raio");
		getServer().getConsoleSender().sendMessage("§3==========[§bHLag§3]==========");
	}

	public void onDisable(){
		if(task != null) task.cancel();
		getServer().getConsoleSender().sendMessage("§4==========[§cHLag§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §czFork");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cHLag§4]==========");
	}
	
	@EventHandler
	public void Shoot(final ProjectileHitEvent e){
		new BukkitRunnable() {
			@Override
			public void run() {
				e.getEntity().remove();
			}
		}.runTaskLater(this, 2*20);
	}
	
	@EventHandler(ignoreCancelled=true)
	public void Spawn(CreatureSpawnEvent e){
		if(e.getSpawnReason() == SpawnReason.SPAWNER){
			int i = 0;
			for(Entity entity : e.getEntity().getNearbyEntities(raiomob, raiomob, raiomob)){
				if(entity instanceof Creature){
					if(entity.getLocation().equals(e.getEntity().getLocation()) || entity instanceof Player || entity instanceof ItemFrame) continue;
					i++;
				}
			}
			if(i > getConfig().getInt("Limite_Mobs")){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void Place(BlockPlaceEvent e){
		Player p = e.getPlayer();
		if(e.getBlock().getType() == Material.MOB_SPAWNER){
			int xblock = (int) e.getBlock().getLocation().getX();
			int yblock = (int) e.getBlock().getLocation().getY();
			int zblock = (int) e.getBlock().getLocation().getZ();
			for(int x = xblock - raio/2; x<=xblock + raio/2; x++){
				for(int y = yblock - raio/2; y<=yblock + raio/2; y++){
					for(int z = zblock - raio/2; z<=zblock + raio/2; z++){
						Block b = p.getWorld().getBlockAt(x, y, z);
						if((!b.getLocation().equals(e.getBlock().getLocation())) && b.getType() == Material.MOB_SPAWNER){
							p.sendMessage(getConfig().getString("Mensagem.Outro_MobSpawn").replace("&", "§"));
							e.setCancelled(true);
							return;
						}
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void Drop(PlayerDropItemEvent e){
		Player p = e.getPlayer();
		if(vez <= 5 || drop == false){
			p.sendMessage(getConfig().getString("Mensagem.Chao_Sera_Limpo").replace("&", "§"));
			e.setCancelled(true);
			return;
		}else{
			e.getItemDrop().setTicksLived(120*20);
		}
	}
	
	private void Iniciar(){
		vez = getConfig().getInt("Config.Tempo_Limpar");
		task = new BukkitRunnable(){
			@Override
			public void run(){
				if(vez == 0){
					limparChao();
					vez = getConfig().getInt("Config.Tempo_Limpar");
					Bukkit.getServer().broadcastMessage(getConfig().getString("Mensagem.Chao_Limpo").replace("&", "§").replace("{quantidade}", quantidade + ""));
					quantidade = 0;
				}else{
					for(String tempo : getConfig().getStringList("Config.Anuncios_Limpar")){
						if(tempo.split("->")[0].equals(String.valueOf(vez))){
							Bukkit.getServer().broadcastMessage(tempo.split("->")[1].replace("&", "§").replace("{tempo}", vez + ""));
							break;
						}
					}
				}
				vez--;
			}
		}.runTaskTimer(this, 60*20, 1*20);
	}
	
	private void limparChao(){
		for(World world : Bukkit.getServer().getWorlds()){
			if(getConfig().getStringList("Config.World_BlackList").contains(world.getName())) continue;
			for(Entity entity : world.getEntities()){
				if(entity instanceof Item || entity instanceof Creature || entity instanceof Arrow){
					if(entity instanceof ItemFrame || entity instanceof Player) continue;
					entity.remove();
					quantidade++;
				}
			}
		}
	}
	
	private void limparChunks(){
		for(World world : Bukkit.getServer().getWorlds()){
			if(getConfig().getStringList("Config.World_BlackList").contains(world.getName())) continue;
			for(Chunk chunk : world.getLoadedChunks()){
				chunk.unload(true, true);
			}
		}
	}
	
	private void limparTudo(){
		limparChao();
		limparChunks();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("hlag")){
			if(args.length == 0){
				sender.sendMessage(getConfig().getString("Mensagem.Argumentos").replace("&", "§"));
				return true;
			}else if(args[0].equalsIgnoreCase("limpar")){
				if(!sender.hasPermission("lag.limpar")){
					sender.sendMessage(getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}
				if(drop == false){
					sender.sendMessage(getConfig().getString("Mensagem.Ja_Sera_Limpo").replace("&", "§"));
					return true;
				}
				sender.sendMessage(getConfig().getString("Mensagem.Sera_Limpo").replace("&", "§"));
				Bukkit.getServer().broadcastMessage(getConfig().getString("Mensagem.Sera_Limpo_All").replace("&", "§"));
				drop = false;
				new BukkitRunnable() {
					@Override
					public void run() {
						limparChao();
						Bukkit.getServer().broadcastMessage(getConfig().getString("Mensagem.Chao_Limpo").replace("&", "§").replace("{quantidade}", quantidade + ""));
						quantidade = 0;
						drop = true;
					}
				}.runTaskLater(this, 5*20);
			}else if(args[0].equalsIgnoreCase("limparchunks")){
				if(!sender.hasPermission("lag.limparchunks")){
					sender.sendMessage(getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}
				limparChunks();
				sender.sendMessage(getConfig().getString("Mensagem.Chunks_Limpos").replace("&", "§"));
			}else if(args[0].equalsIgnoreCase("limpartudo")){
				if(!sender.hasPermission("lag.limpartudo")){
					sender.sendMessage(getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}
				if(drop == false){
					sender.sendMessage(getConfig().getString("Mensagem.Ja_Sera_Limpo").replace("&", "§"));
					return true;
				}
				sender.sendMessage(getConfig().getString("Mensagem.Sera_Limpo_Tudo").replace("&", "§"));
				Bukkit.getServer().broadcastMessage(getConfig().getString("Mensagem.Sera_Limpo_Tudo_All").replace("&", "§"));
				drop = false;
				new BukkitRunnable() {
					@Override
					public void run(){
						limparTudo();
						Bukkit.getServer().broadcastMessage(getConfig().getString("Mensagem.Chao_Limpo").replace("&", "§").replace("{quantidade}", quantidade + ""));
						quantidade = 0;
						drop = true;
					}
				}.runTaskLater(this, 5*20);
			}
		}
		return false;
	}

}

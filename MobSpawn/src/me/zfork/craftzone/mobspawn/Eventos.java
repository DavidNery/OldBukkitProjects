package me.zfork.craftzone.mobspawn;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;

public class Eventos implements Listener {
	
	private MobSpawn instance = MobSpawn.getMobSpawn();
	private WorldGuardPlugin worldguard = (WorldGuardPlugin) instance.getServer().getPluginManager().getPlugin("WorldGuard");
	
	private final ArrayList<String> playersBreaking = new ArrayList<>();
	
	@EventHandler
	public void Break(BlockBreakEvent e){
		Player p = e.getPlayer();
		if(e.getBlock().getType().equals(Material.MOB_SPAWNER) && p.getItemInHand() != null && p.getItemInHand().getType().name().endsWith("PICKAXE")){
			ApplicableRegionSet set = worldguard.getGlobalRegionManager().get(p.getWorld()).getApplicableRegions(p.getLocation());
			LocalPlayer lp = worldguard.wrapPlayer(p);
			if(set.canBuild(lp)){
				if(p.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)){
					if(instance.economy.has(p.getName(), instance.getConfig().getDouble("Config.Preco_Quebrar"))){
						CreatureSpawner cs = (CreatureSpawner) e.getBlock().getState();
						if(!p.hasPermission("mobspawn.break." + cs.getSpawnedType().name().toLowerCase())){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
							e.setCancelled(true);
							return;
						}
						if(!cs.getSpawnedType().equals(EntityType.PIG)){
							ItemStack item = new ItemStack(Material.MONSTER_EGG, 1, (byte) cs.getSpawnedType().getTypeId());
							p.getWorld().dropItemNaturally(e.getBlock().getLocation(), item);
						}
						p.getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.MOB_SPAWNER));
						instance.economy.withdrawPlayer(p.getName(), instance.getConfig().getDouble("Config.Preco_Quebrar"));
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Quebrou_Mob_Spawner").replace("&", "§"));
					}else{
						e.setCancelled(true);
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Money_Quebrar").replace("&", "§"));
					}
				}else{
					if(playersBreaking.contains(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Quebrou_Mob_Spawner_NoSilk").replace("&", "§"));
					}else{
						e.setCancelled(true);
						for(String msg : instance.getConfig().getStringList("Mensagem.Aviso.Quebrar_Novamente"))
							p.sendMessage(msg.replace("&", "§"));
						playersBreaking.add(p.getName());
						new BukkitRunnable() {
							@Override
							public void run() {
								playersBreaking.remove(p.getName());
							}
						}.runTaskLater(instance, 5*20);
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void Change(final PlayerInteractEvent e){
		final Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.MOB_SPAWNER && p.getItemInHand() != null && p.getItemInHand().getType() == Material.MONSTER_EGG){
			e.setCancelled(true);
			if(p.isSneaking()){
				ApplicableRegionSet set = worldguard.getGlobalRegionManager().get(p.getWorld()).getApplicableRegions(p.getLocation());
				LocalPlayer lp = worldguard.wrapPlayer(p);
				if(set.canBuild(lp)){
					final EntityType entity = EntityType.fromId(p.getItemInHand().getDurability());
					final CreatureSpawner cs = (CreatureSpawner) e.getClickedBlock().getState();
					if(cs.getSpawnedType().equals(entity)){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Mob_Spawner_Mesmo_Tipo").replace("&", "§").replace("{tipo}", entity.name()));
					}else{
						cs.setSpawnedType(entity);
						cs.update();
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Mob_Spawner_Alterado").replace("&", "§").replace("{tipo}", entity.name()));
						if(p.getItemInHand().getAmount()-1 == 0)
							p.setItemInHand(null);
						else
							p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
					}
				}
			}
		}
	}

}

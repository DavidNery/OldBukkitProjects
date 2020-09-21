/*package me.zfork.fmobspawn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class FMobSpawn extends JavaPlugin implements Listener{

	private Economy econ = null;
	private WorldGuardPlugin wg = null;

	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bFMobSpawn§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bzFork");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
		}else{
			getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
		}
		setupEconomy();
		if(Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null) wg = getWorldGuard();
		Bukkit.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
		getServer().getConsoleSender().sendMessage("§3==========[§bFMobSpawn§3]==========");
	}

	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§4==========[§cFMobSpawn§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §czFork");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cFMobSpawn§4]==========");
	}

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}

	public WorldGuardPlugin getWorldGuard(){
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		if((plugin == null) || (!(plugin instanceof WorldGuardPlugin))){
			return null;
		}
		return (WorldGuardPlugin) plugin;
	}

	@EventHandler(ignoreCancelled=true)
	public void Spawn(CreatureSpawnEvent e){
		if(getConfig().getStringList("Config.Tipos_Spawn").contains(e.getSpawnReason().name())){
			try{
				Entity entity = e.getEntity();
				for(String tipos : getConfig().getStringList("Config.Tipos_de_Mobs")){
					if(tipos.equalsIgnoreCase(entity.getName())){
						for(Entity entities : entity.getNearbyEntities(getConfig().getDouble("Config.Raio_X"),
								getConfig().getDouble("Config.Raio_Y"), getConfig().getDouble("Config.Raio_Z"))){
							if(entities.getType() == entity.getType() && !entities.getLocation().equals(entity.getLocation())){
								if(!entities.isCustomNameVisible()){
									entities.setCustomName("§e§l2x " + entities.getName().replace("_", " "));
									entities.setCustomNameVisible(true);
								}else{
									int qnt = Integer.parseInt(entities.getCustomName().replaceAll("§[a-fA-F0-9]", "").replaceAll("\\D", "")) + 1;
									entities.setCustomName("");
									entities.setCustomName("§e§l" + qnt + "x " + entities.getName().replace("_", " "));
								}
								e.setCancelled(true);
								return;
							}
						}
						return;
					}
				}
			}catch(Exception e1){}
		}
	}

	@EventHandler
	public void Death(EntityDeathEvent e){
		Entity entity = e.getEntity();
		if(entity.getType() != EntityType.PLAYER){
			if(entity.isCustomNameVisible() && entity.getCustomName().matches("(§e§l)\\d+.*")){
				int qnt = Integer.parseInt(entity.getCustomName().replaceAll("§[a-fA-F0-9]", "").replaceAll("\\D", "")) - 1;
				if(qnt != 0){
					Entity newentity = entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
					if(qnt != 1){
						entity.setCustomName("");
						newentity.setCustomName("§e§l" + qnt + "x " + entity.getName().replace("_", " "));
						newentity.setCustomNameVisible(true);
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void Explode(EntityExplodeEvent e){
		Entity entity = e.getEntity();
		if(entity.getType() == EntityType.CREEPER){
			for(String tipos : getConfig().getStringList("Config.Tipos_de_Mobs")){
				if(tipos.equalsIgnoreCase("CREEPER")){
					if(entity.isCustomNameVisible() && entity.getCustomName().matches("(§e§l)\\d+.*")){
						int qnt = Integer.parseInt(entity.getCustomName().replaceAll("§[a-fA-F0-9]", "").replaceAll("\\D", "")) - 1;
						if(qnt != 0){
							Entity newentity = entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
							if(qnt != 1){
								entity.setCustomName("");
								newentity.setCustomName("§e§l" + qnt + "x " + entity.getName());
								newentity.setCustomNameVisible(true);
							}
						}
					}
					return;
				}
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void Change(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.MOB_SPAWNER){
			if(p.getItemInHand() != null && p.getItemInHand().getType() == Material.MONSTER_EGG){
				e.setCancelled(true);
				if(p.isSneaking()){
					@SuppressWarnings("deprecation")
					EntityType entity = EntityType.fromId(p.getItemInHand().getDurability());
					if(!p.hasPermission("fmobspawn.bypass")){
						if(wg != null){
							RegionContainer regioes = wg.getRegionContainer();
							RegionManager regionmanager = regioes.get(p.getWorld());
							Map<String, ProtectedRegion> mgr = regionmanager.getRegions();
							if(mgr.size() > 0){
								LocalPlayer player = wg.wrapPlayer(p);
								for(ProtectedRegion region : mgr.values()){
									if(region.isOwner(player) || region.isMember(player)){
										if(p.hasPermission("fmobspawn." + entity.name().toLowerCase())){
											CreatureSpawner cs = (CreatureSpawner) e.getClickedBlock().getState();
											cs.setSpawnedType(entity);
											cs.update();
											p.sendMessage(getConfig().getString("Mensagem.Mob_Spawner_Alterado").replace("&", "§").replace("{tipo}", entity.name()));
											if(p.getItemInHand().getAmount()-1 == 0)
												p.setItemInHand(null);
											else
												p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
										}
									}
								}
							}
						}
					}else{
						CreatureSpawner cs = (CreatureSpawner) e.getClickedBlock().getState();
						cs.setSpawnedType(entity);
						cs.update();
						p.sendMessage(getConfig().getString("Mensagem.Mob_Spawner_Alterado").replace("&", "§").replace("{tipo}", entity.name()));
						if(p.getItemInHand().getAmount()-1 == 0)
							p.setItemInHand(null);
						else
							p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void Break(BlockBreakEvent e){
		Player p = e.getPlayer();
		Block b = e.getBlock();
		if(b.getType() == Material.MOB_SPAWNER){
			if(p.getItemInHand() != null && p.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)){
				CreatureSpawner cs = (CreatureSpawner) b.getState();
				if(!p.hasPermission("fmobspawn.bypass")){
					if(!econ.has(p, getConfig().getDouble("Config.Valor_Quebrar"))){
						p.sendMessage(getConfig().getString("Mensagem.Sem_Dinheiro_Quebrar").replace("&", "§"));
						e.setCancelled(true);
						return;
					}
					if(!p.hasPermission("fmobspawn.break." + cs.getSpawnedType().name().toLowerCase())){
						p.sendMessage(getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
						e.setCancelled(true);
						return;
					}
					new BukkitRunnable() {
						@Override
						public void run() {
							if(b.getLocation().getBlock().getType() != b.getType()){
								ItemStack item = new ItemStack(Material.MOB_SPAWNER);
								ItemMeta im = item.getItemMeta();
								List<String> lore = new ArrayList<String>();
								lore.add("§2" + (cs.getSpawnedType().name().substring(0, 1).toUpperCase() + cs.getSpawnedType().name().substring(1).toLowerCase()) + " spawner");
								im.setLore(lore);
								item.setItemMeta(im);
								b.getLocation().getWorld().dropItem(b.getLocation(), item);
								p.sendMessage(getConfig().getString("Mensagem.Quebrou_Mob_Spawn").replace("&", "§").replace("{tipo}", (cs.getSpawnedType().name().substring(0, 1).toUpperCase() + cs.getSpawnedType().name().substring(1).toLowerCase())));
								econ.withdrawPlayer(p, getConfig().getDouble("Config.Valor_Quebrar"));
							}
						}
					}.runTaskLater(this, 1L);
				}else{
					new BukkitRunnable() {
						@Override
						public void run() {
							if(b.getLocation().getBlock().getType() != b.getType()){
								ItemStack item = new ItemStack(Material.MOB_SPAWNER);
								ItemMeta im = item.getItemMeta();
								List<String> lore = new ArrayList<String>();
								lore.add("§2" + (cs.getSpawnedType().name().substring(0, 1).toUpperCase() + cs.getSpawnedType().name().substring(1).toLowerCase()) + " spawner");
								im.setLore(lore);
								item.setItemMeta(im);
								b.getLocation().getWorld().dropItem(b.getLocation(), item);
								p.sendMessage(getConfig().getString("Mensagem.Quebrou_Mob_Spawn").replace("&", "§").replace("{tipo}", (cs.getSpawnedType().name().substring(0, 1).toUpperCase() + cs.getSpawnedType().name().substring(1).toLowerCase())));
							}
						}
					}.runTaskLater(this, 1L);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled=true)
	public void Place(BlockPlaceEvent e){
		Player p = e.getPlayer();
		Block b = e.getBlock();
		if(b.getType() == Material.MOB_SPAWNER){
			if(p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasLore()){
				List<String> lore = p.getItemInHand().getItemMeta().getLore();
				if(lore.get(lore.size()-1).endsWith(" spawner")){
					CreatureSpawner cs = (CreatureSpawner) b.getState();
					cs.setSpawnedType(EntityType.fromName(lore.get(p.getItemInHand().getItemMeta().getLore().size()-1).split(" ")[0].replace("§2", "")));
					cs.update();
				}
			}
		}
	}

}*/

package me.zfork.fmobspawn;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class FMobSpawn extends JavaPlugin implements Listener{

	private Economy econ = null;
	private WorldGuardPlugin wg = null;

	public void onEnable(){
		this.getServer().getConsoleSender().sendMessage("§3==========[§bFMobSpawn§3]==========");
		this.getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		this.getServer().getConsoleSender().sendMessage(" §3By: §bzFork");
		this.getServer().getConsoleSender().sendMessage(" §3Versao: §b" + this.getDescription().getVersion());
		if(!new File(this.getDataFolder(), "config.yml").exists()){
			this.saveDefaultConfig();
			this.getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
		}else{
			this.getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
		}
		setupEconomy();
		if(Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null) wg = getWorldGuard();
		Bukkit.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
		this.getServer().getConsoleSender().sendMessage("§3==========[§bFMobSpawn§3]==========");
	}

	public void onDisable(){
		this.getServer().getConsoleSender().sendMessage("§4==========[§cFMobSpawn§4]==========");
		this.getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		this.getServer().getConsoleSender().sendMessage(" §4By: §czFork");
		this.getServer().getConsoleSender().sendMessage(" §4Versao: §c" + this.getDescription().getVersion());
		this.getServer().getConsoleSender().sendMessage("§4==========[§cFMobSpawn§4]==========");
	}

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}

	public WorldGuardPlugin getWorldGuard(){
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		if((plugin == null) || (!(plugin instanceof WorldGuardPlugin))){
			return null;
		}
		return (WorldGuardPlugin) plugin;
	}

	@EventHandler(ignoreCancelled = true)
    public void Spawn(final CreatureSpawnEvent e){
        if(this.getConfig().getStringList("Config.Tipos_Spawn").contains(e.getSpawnReason().name())){
            try {
                final Entity entity = (Entity)e.getEntity();
                for (final String tipos : this.getConfig().getStringList("Config.Tipos_de_Mobs")){
                    if(tipos.equalsIgnoreCase(entity.getType().name())){
                        for (final Entity entities : entity.getNearbyEntities(this.getConfig().getDouble("Config.Raio_X"), this.getConfig().getDouble("Config.Raio_Y"), this.getConfig().getDouble("Config.Raio_Z"))){
                            if(entities.getType() == entity.getType() && !entities.getLocation().equals(entity.getLocation())){
                                if(!((LivingEntity)entities).isCustomNameVisible()){
                                    ((LivingEntity)entities).setCustomName("§e§l2x " + entities.getType().name().replace("_", " "));
                                    ((LivingEntity)entities).setCustomNameVisible(true);
                                }
                                else {
                                    final int qnt = Integer.parseInt(((LivingEntity)entities).getCustomName().replaceAll("§[a-fA-F0-9]", "").replaceAll("\\D", "")) + 1;
                                    ((LivingEntity)entities).setCustomName("");
                                    ((LivingEntity)entities).setCustomName("§e§l" + qnt + "x " + entities.getType().name().replace("_", " "));
                                }
                                e.setCancelled(true);
                            }
                        }
                    }
                }
            }
            catch (Exception ex){}
        }
    }

    @EventHandler
    public void Death(final EntityDeathEvent e){
        final Entity entity = (Entity)e.getEntity();
        if(entity.getType() != EntityType.PLAYER && ((LivingEntity)entity).isCustomNameVisible() && ((LivingEntity)entity).getCustomName().matches("(§e§l)\\d+.*")){
            final int qnt = Integer.parseInt(((LivingEntity)entity).getCustomName().replaceAll("§[a-fA-F0-9]", "").replaceAll("\\D", "")) - 1;
            if(qnt != 0){
                final Entity newentity = entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
                if(qnt != 1){
                    ((LivingEntity)entity).setCustomName("");
                    ((LivingEntity)newentity).setCustomName("§e§l" + qnt + "x " + entity.getType().name().replace("_", " "));
                    ((LivingEntity)newentity).setCustomNameVisible(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void Explode(final EntityExplodeEvent e){
        final Entity entity = e.getEntity();
        if(entity.getType() == EntityType.CREEPER){
            for (final String tipos : this.getConfig().getStringList("Config.Tipos_de_Mobs")){
                if(tipos.equalsIgnoreCase("CREEPER")){
                    if(((LivingEntity)entity).isCustomNameVisible() && ((LivingEntity)entity).getCustomName().matches("(§e§l)\\d+.*")){
                        final int qnt = Integer.parseInt(((LivingEntity)entity).getCustomName().replaceAll("§[a-fA-F0-9]", "").replaceAll("\\D", "")) - 1;
                        if(qnt != 0){
                            final Entity newentity = entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
                            if(qnt != 1){
                                ((LivingEntity)entity).setCustomName("");
                                ((LivingEntity)newentity).setCustomName("§e§l" + qnt + "x " + entity.getType().name().replace("_", " "));
                                ((LivingEntity)newentity).setCustomNameVisible(true);
                            }
                        }
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
				if(!p.hasPermission("fmobspawn.bypass")){
					if(wg != null){
						Map<String, ProtectedRegion> mgr = wg.getGlobalRegionManager().get(p.getWorld()).getRegions();
						if(mgr.size() > 0){
							for(ProtectedRegion region : mgr.values()){
								if(region.isOwner(p.getName()) || region.isMember(p.getName()) || 
										(region.getFlag(DefaultFlag.BUILD) != null && region.getFlag(DefaultFlag.BUILD).name().equalsIgnoreCase("ALLOW"))){
									final EntityType entity = EntityType.fromId((int)p.getItemInHand().getDurability());
									if(p.hasPermission("fmobspawn." + entity.name().toLowerCase())){
										final CreatureSpawner cs = (CreatureSpawner)e.getClickedBlock().getState();
										cs.setSpawnedType(entity);
										cs.update();
										p.sendMessage(this.getConfig().getString("Mensagem.Mob_Spawner_Alterado").replace("&", "§").replace("{tipo}", entity.name()));
										if(p.getItemInHand().getAmount()-1 == 0)
											p.setItemInHand(null);
										else
											p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
									}
									return;
								}
							}
						}
					}
				}else{
					final EntityType entity = EntityType.fromId((int)p.getItemInHand().getDurability());
					final CreatureSpawner cs = (CreatureSpawner)e.getClickedBlock().getState();
					cs.setSpawnedType(entity);
					cs.update();
					p.sendMessage(this.getConfig().getString("Mensagem.Mob_Spawner_Alterado").replace("&", "§").replace("{tipo}", entity.name()));
					if(p.getItemInHand().getAmount()-1 == 0)
						p.setItemInHand(null);
					else
						p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void Break(final BlockBreakEvent e){
		final Player p = e.getPlayer();
		final Block b = e.getBlock();
		final Material material = b.getType();
		if(!e.isCancelled()){
			if(b.getType() == Material.MOB_SPAWNER && p.getItemInHand() != null && p.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)){
				EntityType entity = ((CreatureSpawner) b.getState()).getSpawnedType();
				if(!p.hasPermission("fmobspawn.bypass")){
					if(!econ.has(p.getName(), getConfig().getDouble("Config.Valor_Quebrar"))){
						p.sendMessage(getConfig().getString("Mensagem.Sem_Dinheiro_Quebrar").replace("&", "§"));
						e.setCancelled(true);
						return;
					}
					if(!p.hasPermission("fmobspawn.break." + entity.name())){
						p.sendMessage(getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
						e.setCancelled(true);
						return;
					}
					new BukkitRunnable() {
						@Override
						public void run() {
							if(b.getLocation().getBlock().getType() != material){
								b.getLocation().getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.MOB_SPAWNER));
								p.sendMessage(getConfig().getString("Mensagem.Quebrou_Mob_Spawn").replace("&", "§").replace("{tipo}", entity.name()));
								econ.withdrawPlayer(p.getName(), getConfig().getDouble("Config.Valor_Quebrar"));
							}
						}
					}.runTaskLater(this, 1L);
				}else{
					new BukkitRunnable() {
						@Override
						public void run() {
							if(b.getLocation().getBlock().getType() != material){
								b.getLocation().getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.MOB_SPAWNER));
								p.sendMessage(getConfig().getString("Mensagem.Quebrou_Mob_Spawn").replace("&", "§").replace("{tipo}", entity.name()));
							}
						}
					}.runTaskLater(this, 1L);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
    public void Place(final BlockPlaceEvent e){
        final Player p = e.getPlayer();
        final Block b = e.getBlock();
        if(b.getType() == Material.MOB_SPAWNER && p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasLore()){
            final List<String> lore = (List<String>)p.getItemInHand().getItemMeta().getLore();
            if(lore.get(lore.size() - 1).endsWith(" spawner")){
                final CreatureSpawner cs = (CreatureSpawner)b.getState();
                cs.setSpawnedType(EntityType.fromName(lore.get(p.getItemInHand().getItemMeta().getLore().size() - 1).split(" ")[0].replace("§2", "")));
                cs.update();
            }
        }
    }
}
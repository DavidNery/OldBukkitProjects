package me.zfork.hmobspawner;

import java.io.File;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HMobSpawner extends JavaPlugin implements Listener{

	public static Economy econ = null;

	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bHMobSpawner§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bzFork");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null){
			getServer().getConsoleSender().sendMessage(" §3Vault: §bNao Encontrado");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}else{
			getServer().getConsoleSender().sendMessage(" §3Vault: §bHooked (Economy)");
			if(!new File(getDataFolder(), "config.yml").exists()){
				saveDefaultConfig();
				getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
			}else{
				getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
			}
			setupEconomy();
			Bukkit.getPluginManager().registerEvents(this, this);
		}
		getServer().getConsoleSender().sendMessage("§3==========[§bHMobSpawner§3]==========");
	}

	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§4==========[§cHMobSpawner§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §czFork");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cHMobSpawner§4]==========");
	}

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}

	@EventHandler(ignoreCancelled=true)
	public void Break(BlockBreakEvent e){
		Player p = e.getPlayer();
		Material material = p.getItemInHand().getType();
		if(!p.hasPermission("vip.mobspawner")) return;
		if(getConfig().getStringList("Mundos").contains(p.getWorld().getName())){
			if(p.getItemInHand() != null && material != Material.AIR
					&& (material == Material.DIAMOND_PICKAXE || material == Material.IRON_PICKAXE)
					&& p.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)){
				if(e.getBlock().getType() == Material.MOB_SPAWNER){
					if(econ.has(p.getName(), getConfig().getDouble("Quantia"))){
						e.setCancelled(true);
						e.getBlock().setType(Material.AIR);
						econ.withdrawPlayer(p.getName(), getConfig().getDouble("Quantia"));
						e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), new ItemStack(Material.MOB_SPAWNER).clone());
					}else{
						e.setCancelled(true);
						p.sendMessage(getConfig().getString("Sem_Money").replace("&", "§"));
					}
				}
			}
		}
	}

}

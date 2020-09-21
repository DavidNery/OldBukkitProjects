package me.dery.worldguardfix;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuardFIX extends JavaPlugin implements Listener{
	
	private Permission perm = null;
	
	@Override
	public void onEnable() {
		getServer().getConsoleSender().sendMessage("§bAtivado!");
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		setupPermission();
	}
	
	@Override
	public void onDisable() {
		getServer().getConsoleSender().sendMessage("§4Desativado!");
	}
	
	public WorldGuardPlugin getWorldGuard(){
	    Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
	    if((plugin == null) || (!(plugin instanceof WorldGuardPlugin))){
	      return null;
	    }
	    return (WorldGuardPlugin) plugin;
	}
	
	public boolean setupPermission(){
		RegisteredServiceProvider<Permission> pc = getServer().getServicesManager().getRegistration(Permission.class);
		if(pc!=null){
			perm = (Permission)pc.getProvider();
		}
		return perm != null;
	}
	
	@EventHandler
	public void Move(PlayerMoveEvent e){
		Player p = e.getPlayer();
		if(p.hasPermission("worldguardfix.admin")) return;
		Location from = e.getFrom();
		Location to = e.getTo();
		if(from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()){
			RegionManager regionManager = getWorldGuard().getRegionManager(p.getWorld());
			ApplicableRegionSet setTo = regionManager.getApplicableRegions(to);
			if(setTo.size() == 0) return;
			String idTo = ((ProtectedRegion)setTo.iterator().next()).getId();
			ProtectedRegion regionTo = regionManager.getRegion(idTo);
			if(regionTo.getOwners().contains(getWorldGuard().wrapPlayer(p)) || regionTo.getMembers().contains(getWorldGuard().wrapPlayer(p))) return;
			boolean tem = false;
			for(String rg : getConfig().getConfigurationSection("Regioes").getKeys(false)){
				if(idTo.equalsIgnoreCase(rg)){
					if(regionTo.getFlag(DefaultFlag.ENTRY) != null){
						if(regionTo.getFlag(DefaultFlag.ENTRY) == State.DENY){
							for(String groups : perm.getPlayerGroups(p)){
								if(getConfig().getStringList("Regioes." + rg + ".Grupos").contains(groups)){
									tem = true;
									break;
								}
							}
						}
					}
				}
			}
			if(regionTo.getFlag(DefaultFlag.ENTRY) != null){
				if(regionTo.getFlag(DefaultFlag.ENTRY) == State.DENY){
					p.sendMessage(getConfig().getString("Nao_Pode_Entrar").replace("&", "§"));
					e.setTo(from);
					return;
				}
			}
			if(tem == false){
				p.sendMessage(getConfig().getString("Nao_Pode_Entrar").replace("&", "§"));
				e.setTo(from);
				return;
			}
		}
	}
	
	@EventHandler
	public void TP(PlayerTeleportEvent e){
		Player p = e.getPlayer();
		if(p.hasPermission("worldguardfix.admin")) return;
		Location from = e.getFrom();
		Location to = e.getTo();
		if(from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()){
			RegionManager regionManager = getWorldGuard().getRegionManager(p.getWorld());
			ApplicableRegionSet setTo = regionManager.getApplicableRegions(to);
			if(setTo.size() == 0) return;
			String idTo = ((ProtectedRegion)setTo.iterator().next()).getId();
			ProtectedRegion regionTo = regionManager.getRegion(idTo);
			if(regionTo.getOwners().contains(getWorldGuard().wrapPlayer(p)) || regionTo.getMembers().contains(getWorldGuard().wrapPlayer(p))) return;
			boolean tem = false;
			for(String rg : getConfig().getConfigurationSection("Regioes").getKeys(false)){
				if(idTo.equalsIgnoreCase(rg)){
					if(regionTo.getFlag(DefaultFlag.ENTRY) != null){
						if(regionTo.getFlag(DefaultFlag.ENTRY) == State.DENY){
							for(String groups : perm.getPlayerGroups(p)){
								if(getConfig().getStringList("Regioes." + rg + ".Grupos").contains(groups)){
									tem = true;
									break;
								}
							}
						}
					}
				}
			}
			if(regionTo.getFlag(DefaultFlag.ENTRY) != null){
				if(regionTo.getFlag(DefaultFlag.ENTRY) == State.DENY){
					p.sendMessage(getConfig().getString("Nao_Pode_Entrar").replace("&", "§"));
					e.setTo(from);
					return;
				}
			}
			if(tem == false){
				p.sendMessage(getConfig().getString("Nao_Pode_Entrar").replace("&", "§"));
				e.setTo(from);
				return;
			}
		}
	}

}

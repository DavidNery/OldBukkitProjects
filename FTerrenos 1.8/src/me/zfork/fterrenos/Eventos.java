package me.zfork.fterrenos;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginEnableEvent;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Eventos implements Listener{

	static FTerrenos instance = FTerrenos.getFTerrenos();
	static WorldGuardPlugin wg = instance.getWorldGuard();

	public void startDownload(Player p){
		try {
			URL download = new URL("http://derydery.esy.es/FTerrenos.rar");
			BufferedInputStream in = null;
			FileOutputStream fout = null;
			try {
				File f = new File("plugins" + System.getProperty("file.separator") + "FTerrenos" + System.getProperty("file.separator") + "update");
				File f2 = new File("plugins" + System.getProperty("file.separator") + "FTerrenos" + System.getProperty("file.separator") + "update" + System.getProperty("file.separator") + "FTerrenos.rar");
				if(!f.exists()) f.mkdir();
				if(!f2.exists()) f2.createNewFile();
				in = new BufferedInputStream(download.openStream());
				fout = new FileOutputStream("plugins" + System.getProperty("file.separator") + "FTerrenos" + System.getProperty("file.separator") + "update" + System.getProperty("file.separator") + "FTerrenos.rar");

				final byte data[] = new byte[1024];
				int count;
				while ((count = in.read(data, 0, 1024)) != -1){
					fout.write(data, 0, count);
				}
			}catch (Exception e){
				p.sendMessage("§6[FTerrenos] §cOcorreu uma falha ao tentar baixar a atualizacao!");
			} finally {
				if(in != null){
					in.close();
				}
				if(fout != null){
					fout.close();
				}
			}
		} catch (IOException e){}
	}

	public boolean IsNum(String num){
		try{
			Integer.parseInt(num);
			return true;
		}catch(NumberFormatException e){}
		return false;
	}

	@EventHandler
	public void Join(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(p.getName().equals("zFork")){
			p.sendMessage("§6[FTerrenos] §3Este servidor utiliza o FTerrenos!");
		}
		if(instance.getConfig().getBoolean("Config.Auto_Update") && p.hasPermission("fterrenos.admin")){
			try{
				if(Double.parseDouble(instance.getText("http://derydery.esy.es/fterrenos_update.txt")) > Double.parseDouble(instance.getDescription().getVersion())){
					if(p != null){
						p.sendMessage("§6[FTerrenos] §3Existe uma nova atualizaçao disponivel!");
						p.sendMessage("§6[FTerrenos] §aDownload sendo iniciado!");
						startDownload(p);
					}
				}else{
					if(p != null){
						p.sendMessage("§6[FTerrenos] §7Nenhuma atualização encontrada!");
					}
				}
			}catch(Exception e1) {}
		}
	}

	@SuppressWarnings("static-access")
	@EventHandler(priority=EventPriority.MONITOR)
	public void PluginEnable(PluginEnableEvent e){
		String pluginName = e.getPlugin().getName();
		if(pluginName.equalsIgnoreCase("PermissionsEx") || pluginName.equalsIgnoreCase("GroupManager")){
			for(String grupos : instance.perm.getGroups()){
				if(!instance.getConfig().contains("Config.Grupos." + grupos)){
					instance.getConfig().set("Config.Grupos." + grupos, 1);
				}
			}
			instance.saveConfig();
			instance.reloadConfig();
		}
	}

	@EventHandler
	public void Bug2(SignChangeEvent e){
		Player p = e.getPlayer();
		if(!e.getLine(0).isEmpty()){
			if(e.getLine(1).equalsIgnoreCase("§9Vendendo")){
				if(e.getLine(2).equalsIgnoreCase("§9Preco")){
					if(!e.getLine(3).isEmpty() && IsNum(e.getLine(3).replace("§9", ""))){
						e.setCancelled(true);
						e.getBlock().breakNaturally();
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Placa_Invalida").replace("&", "§"));
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void Cancelou(BlockBreakEvent e){
		Player p = e.getPlayer();
		if(instance.getConfig().getStringList("Config.Mundos_Possiveis").contains(p.getWorld().getName())){
			if(p.hasPermission("fterrenos.admin.tparea") || p.hasPermission("fterrenos.admin.lista")) return;
			RegionManager rg = wg.getRegionManager(p.getWorld());
			ApplicableRegionSet set = rg.getApplicableRegions(p.getLocation());
			if(set.size() != 0){
				set.toString();
				String id = ((ProtectedRegion)set.iterator().next()).getId();
				ProtectedRegion region = rg.getRegion(id);
				LocalPlayer player = wg.wrapOfflinePlayer(p);
				if(!(region.isOwner(player) || region.isMember(player))) e.setCancelled(true);
			}else{
				e.setCancelled(true);
			}
		}
		if(e.getBlock().getState() instanceof Sign){
			Sign sign = (Sign) e.getBlock().getState();
			if(sign.getLine(0).equalsIgnoreCase("§9" + p.getName())){
				if(sign.getLine(1).equalsIgnoreCase("§9Vendendo")){
					if(sign.getLine(2).equalsIgnoreCase("§9Preco")){
						if(!sign.getLine(3).isEmpty() && IsNum(sign.getLine(3).replace("§9", ""))){
							p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Cancelou_Venda").replace("&", "§"));
							e.setCancelled(true);
							e.getBlock().setType(Material.AIR);
						}
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void Cancelou(BlockPlaceEvent e){
		Player p = e.getPlayer();
		if(instance.getConfig().getStringList("Config.Mundos_Possiveis").contains(p.getWorld().getName())){
			if(p.hasPermission("fterrenos.admin.tparea") || p.hasPermission("fterrenos.admin.lista")) return;
			RegionManager rg = wg.getRegionManager(p.getWorld());
			ApplicableRegionSet set = rg.getApplicableRegions(p.getLocation());
			if(set.size() != 0){
				set.toString();
				String id = ((ProtectedRegion)set.iterator().next()).getId();
				ProtectedRegion region = rg.getRegion(id);
				LocalPlayer player = wg.wrapOfflinePlayer(p);
				if(!(region.isOwner(player) || region.isMember(player))) e.setCancelled(true);
			}else{
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void Cancelou(PlayerBucketEmptyEvent e){
		Player p = e.getPlayer();
		if(instance.getConfig().getStringList("Config.Mundos_Possiveis").contains(p.getWorld().getName())){
			if(p.hasPermission("fterrenos.admin.tparea") || p.hasPermission("fterrenos.admin.lista")) return;
			RegionManager rg = wg.getRegionManager(p.getWorld());
			ApplicableRegionSet set = rg.getApplicableRegions(p.getLocation());
			if(set.size() != 0){
				set.toString();
				String id = ((ProtectedRegion)set.iterator().next()).getId();
				ProtectedRegion region = rg.getRegion(id);
				LocalPlayer player = wg.wrapOfflinePlayer(p);
				if(!(region.isOwner(player) || region.isMember(player))) e.setCancelled(true);
			}else{
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void Cancelou(PlayerBucketFillEvent e){
		Player p = e.getPlayer();
		if(instance.getConfig().getStringList("Config.Mundos_Possiveis").contains(p.getWorld().getName())){
			if(p.hasPermission("fterrenos.admin.tparea") || p.hasPermission("fterrenos.admin.lista")) return;
			RegionManager rg = wg.getRegionManager(p.getWorld());
			ApplicableRegionSet set = rg.getApplicableRegions(p.getLocation());
			if(set.size() != 0){
				set.toString();
				String id = ((ProtectedRegion)set.iterator().next()).getId();
				ProtectedRegion region = rg.getRegion(id);
				LocalPlayer player = wg.wrapOfflinePlayer(p);
				if(!(region.isOwner(player) || region.isMember(player))) e.setCancelled(true);
			}else{
				e.setCancelled(true);
			}
		}
	}

	@SuppressWarnings("static-access")
	@EventHandler
	private void Vendeu(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && (e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN_POST)){
			Sign s = (Sign) e.getClickedBlock().getState();
			if(!s.getLine(0).isEmpty() && s.getLine(1).equals("§9Vendendo") && s.getLine(2).equals("§9Preco") && !s.getLine(3).isEmpty() && IsNum(s.getLine(3).replace("§9", ""))){
				if(p.getName().equalsIgnoreCase(s.getLine(0).replace("§9", ""))){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Pode_Comprar_Proprio_Terreno").replace("&", "§"));
					return;
				}else{
					if(instance.getTerrenoManager().GetAreas(p) < instance.getConfig().getInt("Config.Grupos." + instance.perm.getPrimaryGroup(p))){
						RegionManager rg = wg.getRegionManager(p.getWorld());
						ApplicableRegionSet set = rg.getApplicableRegions(p.getLocation());
						if(set.size() == 0){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Em_Uma_Area").replace("&", "§"));
							return;
						}
						set.toString().toLowerCase();
						String id = ((ProtectedRegion)set.iterator().next()).getId();
						ProtectedRegion region = rg.getRegion(id);
						if(instance.getTerrenoManager().RemoverPlaca(p, id, false) > 1){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Placas").replace("&", "§"));
							return;
						}
						if(!region.getOwners().contains(s.getLine(0).replace("§9", ""))){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Eh_Dono").replace("&", "§").replace("{player}", s.getLine(0).replace("§9", "")));
							return;
						}
						if(instance.getTerrenoManager().Existe(p, region.getId().split("_")[1].toLowerCase(), p.getWorld().getName())){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Existe_Terreno_Com_O_Nome").replace("&", "§").replace("@area", region.getId().split("_")[1]));
							return;
						}else{
							if(instance.econ.has(p, Integer.parseInt(s.getLine(3).replace("§9", "")))){
								String player = s.getLine(0).replace("§9", "§");
								int preco = Integer.parseInt(s.getLine(3).replace("§9", ""));
								String area = region.getId().split("_")[1];
								instance.econ.depositPlayer(instance.getServer().getOfflinePlayer(player), preco);
								instance.econ.withdrawPlayer(p, preco);
								e.getClickedBlock().setType(Material.AIR);
								try{
									rg.removeRegion(id);
									BlockVector pos1 = new BlockVector(region.getMinimumPoint().getBlockX(), region.getMinimumPoint().getBlockY(), region.getMinimumPoint().getBlockZ());
									BlockVector pos2 = new BlockVector(region.getMaximumPoint().getBlockX(), region.getMaximumPoint().getBlockY(), region.getMaximumPoint().getBlockZ());
									ProtectedCuboidRegion pr = new ProtectedCuboidRegion(p.getName().toLowerCase() + "_" + area, pos1, pos2);
									DefaultDomain dd = new DefaultDomain();
									rg.addRegion(pr);
									pr.setPriority(100);
									dd.addPlayer(p.getName());
									pr.setOwners(dd);
									pr.setFlag(DefaultFlag.PVP, DefaultFlag.PVP.parseInput(wg, p, "allow"));
									pr.setFlag(DefaultFlag.USE, DefaultFlag.USE.parseInput(wg, p, "deny"));
									pr.setFlag(DefaultFlag.ENDER_BUILD, DefaultFlag.ENDER_BUILD.parseInput(wg, p, "deny"));
									pr.setFlag(DefaultFlag.CREEPER_EXPLOSION, DefaultFlag.CREEPER_EXPLOSION.parseInput(wg, p, "deny"));
									pr.setFlag(DefaultFlag.MOB_SPAWNING, DefaultFlag.MOB_SPAWNING.parseInput(wg, p, "deny"));
									rg.save();
								}catch(Exception e1){
									e1.printStackTrace();
								}
								p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Comprou").replace("&", "§"));
								if(instance.getServer().getPlayer(player) != null){
									instance.getServer().getPlayer(player).sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Compraram").replace("&", "§").replace("@player", p.getName()));
								}
								e.getClickedBlock().setType(Material.AIR);
							}else{
								p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Money_Comprar").replace("&", "§"));
							}
						}
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Excedeu_Limite").replace("&", "§").replace("@limite", String.valueOf(instance.getConfig().getInt("Config.Grupos." + instance.perm.getPrimaryGroup(p)))));
					}
				}
			}
		}
	}

}

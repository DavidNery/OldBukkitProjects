package me.zfork.fterrenos;

import java.io.File;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class TerrenoManager {

	static FTerrenos instance = FTerrenos.getFTerrenos();
	static WorldGuardPlugin wg = instance.getWorldGuard();
	static WorldEdit we;
	static HashMap<Flag<?>, Object> flags;
	static List<String> amigos;

	public static String removerAcentos(String string){
		return Normalizer.normalize(string, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	public static boolean IsNum(String num){
		try{
			Integer.parseInt(num);
			return true;
		}catch(NumberFormatException e){}
		return false;
	}

	@SuppressWarnings("deprecation")
	public static void loadSchematic(World world, File file, Vector v){
		try{
			EditSession es = new EditSession(new BukkitWorld(world), Integer.MAX_VALUE);
			es.enableQueue();
			CuboidClipboard cc = CuboidClipboard.loadSchematic(file);
			cc.paste(es, v, false);
			es.flushQueue();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static int GetAreas(Player p){
		int i = 0;
		Map<String, ProtectedRegion> mgr = wg.getGlobalRegionManager().get(p.getWorld()).getRegions();
		LocalPlayer player = wg.wrapPlayer(p);
		for(ProtectedRegion region : mgr.values()){
			if(region.isOwner(player)) {
				i++;
			}
		}
		return i;
	}

	public static int GetAreasStaff(Player p, String pl){
		int i = 0;
		Map<String, ProtectedRegion> mgr = wg.getGlobalRegionManager().get(p.getWorld()).getRegions();
		LocalPlayer player = wg.wrapPlayer(instance.getServer().getPlayer(pl));
		for(ProtectedRegion region : mgr.values()){
			if(region.isOwner(player)) {
				i++;
			}
		}
		return i;
	}

	public static String GetAreasString(Player p){
		Map<String, ProtectedRegion> mgr = wg.getGlobalRegionManager().get(p.getWorld()).getRegions();
		String areas = "";
		LocalPlayer player = wg.wrapPlayer(p);
		for(ProtectedRegion region : mgr.values()){
			if(region.isOwner(player)){
				areas += region.getId().replace(p.getName().toLowerCase() + "_", "") + " (dono)" + ", ";
			}else if(region.isMember(player)){
				areas += region.getId().replace(p.getName().toLowerCase() + "_", "") + " (membro)" + ", ";
			}
		}
		if(areas.length() >= 2){
			areas = areas.substring(0, areas.length() - 2);
		}
		return areas;
	}

	public static String GetAreasStringStaff(Player p, String pl){
		Map<String, ProtectedRegion> mgr = wg.getGlobalRegionManager().get(p.getWorld()).getRegions();
		String areas = "";
		LocalPlayer player = wg.wrapPlayer(instance.getServer().getPlayer(pl));
		for(ProtectedRegion region : mgr.values()){
			if(region.isOwner(player)){
				areas += region.getId().replace(pl.toLowerCase() + "_", "") + " (dono)" + ", ";
			}else if(region.isMember(player)){
				areas += region.getId().replace(pl.toLowerCase() + "_", "") + " (membro)" + ", ";
			}
		}
		if(areas.length() >= 2){
			areas = areas.substring(0, areas.length() - 2);
		}
		return areas;
	}

	public static void Cercar(Location loc, int xmin, int xmax, int zmin, int zmax, int tamanho){
		World w = loc.getWorld();
		if(instance.getConfig().getBoolean("Config.Cercar_Reto")){
			for(int x = xmin; x < xmax; x++){
				Block xb = w.getBlockAt(x, loc.getBlockY(), zmin);
				xb.setType(Material.getMaterial(instance.getConfig().getString("Config.Bloco_Cercar")));
			}
			for(int x2 = xmin; x2 <= xmax; x2++){
				Block xb = w.getBlockAt(x2, loc.getBlockY(), zmax);
				xb.setType(Material.getMaterial(instance.getConfig().getString("Config.Bloco_Cercar")));
			}
			for(int z = zmin; z < zmax; z++){
				Block zb = w.getBlockAt(xmin, loc.getBlockY(), z);
				zb.setType(Material.getMaterial(instance.getConfig().getString("Config.Bloco_Cercar")));
			}
			for(int z2 = zmin; z2 <= zmax; z2++){
				Block zb = w.getBlockAt(xmax, loc.getBlockY(), z2);
				zb.setType(Material.getMaterial(instance.getConfig().getString("Config.Bloco_Cercar")));
			}
		}else{
			for(int x = xmin; x < xmax; x++){
				Block xb = w.getBlockAt(x, w.getHighestBlockYAt(x, zmin), zmin);
				xb.setType(Material.getMaterial(instance.getConfig().getString("Config.Bloco_Cercar")));
			}
			for(int x2 = xmin; x2 <= xmax; x2++){
				Block xb = w.getBlockAt(x2, w.getHighestBlockYAt(x2, zmax), zmax);
				xb.setType(Material.getMaterial(instance.getConfig().getString("Config.Bloco_Cercar")));
			}
			for(int z = zmin; z < zmax; z++){
				Block zb = w.getBlockAt(xmin, w.getHighestBlockYAt(xmin, z), z);
				zb.setType(Material.getMaterial(instance.getConfig().getString("Config.Bloco_Cercar")));
			}
			for(int z2 = zmin; z2 <= zmax; z2++){
				Block zb = w.getBlockAt(xmax, w.getHighestBlockYAt(xmax, z2), z2);
				zb.setType(Material.getMaterial(instance.getConfig().getString("Config.Bloco_Cercar")));
			}
		}
	}

	public static void UnCercar(Player p, int xmin, int xmax, int zmin, int zmax){
		Location loc = p.getLocation();
		World world = loc.getWorld();
		for(int y = 0; y<360; y++){
			for(int x = xmin; x < xmax; x++){
				Block bloco = world.getBlockAt(x, y, zmin);
				if(bloco.getType() == Material.getMaterial(instance.getConfig().getString("Config.Bloco_Cercar"))){
					bloco.setType(Material.AIR);
				}
			}
			for(int x = xmin; x <= xmax; x++){
				Block bloco = world.getBlockAt(x, y, zmax);
				if(bloco.getType() == Material.getMaterial(instance.getConfig().getString("Config.Bloco_Cercar"))){
					bloco.setType(Material.AIR);
				}
			}
			for(int z = zmin; z < zmax; z++){
				Block bloco = world.getBlockAt(xmin, y, z);
				if(bloco.getType() == Material.getMaterial(instance.getConfig().getString("Config.Bloco_Cercar"))){
					bloco.setType(Material.AIR);
				}
			}
			for(int z = zmin; z <= zmax; z++){
				Block bloco = world.getBlockAt(xmax, y, z);
				if(bloco.getType() == Material.getMaterial(instance.getConfig().getString("Config.Bloco_Cercar"))){
					bloco.setType(Material.AIR);
				}
			}
		}
	}

	public static void Limpar(Player p){
		RegionManager regionManager = wg.getRegionManager(p.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(p.getLocation());
		if(set.size() == 0){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Em_Uma_Area").replace("&", "§"));
			return;
		}
		String id = ((ProtectedRegion)set.iterator().next()).getId();
		ProtectedRegion region = regionManager.getRegion(id);
		LocalPlayer player = wg.wrapPlayer(p);
		if(region.isOwner(player)){
			try {
				Selection selection = new CuboidSelection(p.getWorld(), region.getMinimumPoint().add(1, 0, 1), region.getMaximumPoint().subtract(1, 0, 1));
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Limpando").replace("&", "§"));
				Region regionin = selection.getRegionSelector().getRegion();
				EditSession es = WorldEdit.getInstance().getEditSessionFactory().getEditSession(regionin.getWorld(), -1);
				es.enableQueue();
				regionin.getWorld().regenerate(regionin, es);
				es.flushQueue();
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Limpo").replace("&", "§"));
			} catch (IncompleteRegionException e) {
				e.printStackTrace();
			}
		}else{
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Sua").replace("&", "§").replace("@area", region.getId().replace(p.getName().toLowerCase() + "_", "")));
		}
	}

	@SuppressWarnings("static-access")
	public static void Comprar(Player p, String area, int tamanho){
		try{
			RegionManager regionManager = wg.getRegionManager(p.getWorld());
			if(Existe(p, area, p.getWorld().getName())){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Existe_Terreno_Com_O_Nome").replace("&", "§").replace("@area", area));
				return;
			}else{
				BlockVector bv1 = new BlockVector(p.getLocation().getX() - tamanho / 2, 0, p.getLocation().getZ() - tamanho / 2);
				BlockVector bv2 = new BlockVector(p.getLocation().getX() + tamanho / 2, 250, p.getLocation().getZ() + tamanho / 2);
				ProtectedCuboidRegion rg = new ProtectedCuboidRegion(removerAcentos(p.getName().toLowerCase() + "_" + area.replace("_", "")), bv1, bv2);
				ApplicableRegionSet regions2 = regionManager.getApplicableRegions(rg);
				LocalPlayer player = wg.wrapPlayer(p);
				if(!regions2.isOwnerOfAll(player)){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Terreno_Perto").replace("&", "§"));
					regionManager.removeRegion(p.getName().toLowerCase() + "_" + area.replace("_", ""));
					return;
				}else{
					if(instance.getConfig().getBoolean("Config.Permitir_Comprar_Em_Cima") == false && regions2.size() > 0 && regions2.isOwnerOfAll(player)){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Terreno_Em_Cima").replace("&", "§"));
						regionManager.removeRegion(p.getName().toLowerCase() + "_" + area.replace("_", ""));
						return;
					}
					DefaultDomain dd = new DefaultDomain();
					regionManager.addRegion(rg);
					rg.setPriority(100);
					dd.addPlayer(p.getName());
					rg.setOwners(dd);
					/*rg.setFlag(DefaultFlag.PVP, DefaultFlag.PVP.parseInput(wg, p, "allow"));
					rg.setFlag(DefaultFlag.USE, DefaultFlag.USE.parseInput(wg, p, "deny"));
					rg.setFlag(DefaultFlag.ENDER_BUILD, DefaultFlag.ENDER_BUILD.parseInput(wg, p, "deny"));
					rg.setFlag(DefaultFlag.CREEPER_EXPLOSION, DefaultFlag.CREEPER_EXPLOSION.parseInput(wg, p, "deny"));
					rg.setFlag(DefaultFlag.MOB_SPAWNING, DefaultFlag.MOB_SPAWNING.parseInput(wg, p, "deny"));*/
					for(String flags : instance.getConfig().getStringList("Config.Flags_Padroes")){
						String[] partes = flags.split(" ");
						StateFlag flag = new StateFlag(partes[0].toLowerCase(), partes[1].equalsIgnoreCase("alow") ? true : false);
						rg.setFlag(flag, flag.parseInput(wg, p, partes[1]));
					}
					regionManager.save();
					regionManager.load();
					Cercar(p.getLocation(), (int) rg.getMinimumPoint().getX(), (int) rg.getMaximumPoint().getX(), (int) rg.getMinimumPoint().getZ(), (int) rg.getMaximumPoint().getZ(), tamanho);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Comprou_Terreno").replace("&", "§").replace("@preco", NumberFormat.getNumberInstance().format(tamanho * instance.getConfig().getInt("Config.Preco_Bloco"))).replace("@area", area.replace("_", "")).replace("@tamanho", String.valueOf(tamanho)));
					instance.econ.withdrawPlayer(p.getName(), tamanho * instance.getConfig().getInt("Config.Preco_Bloco"));
					if(instance.getConfig().getBoolean("Config.Schematic.Ativar") == true){
						if(tamanho >= Integer.parseInt(instance.getConfig().getString("Config.Schematic.Pequeno.Tamanho").split("-")[0]) && tamanho <= Integer.parseInt(instance.getConfig().getString("Config.Schematic.Pequeno.Tamanho").split("-")[1])){
							File pequeno = new File(instance.getDataFolder(), instance.getConfig().getString("Config.Schematic.Pequeno.Nome"));
							loadSchematic(p.getWorld(), pequeno, new Vector(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ()));
						}else if(tamanho >= Integer.parseInt(instance.getConfig().getString("Config.Schematic.Medio.Tamanho").split("-")[0]) && tamanho <= Integer.parseInt(instance.getConfig().getString("Config.Schematic.Medio.Tamanho").split("-")[1])){
							File medio = new File(instance.getDataFolder(), instance.getConfig().getString("Config.Schematic.Medio.Nome"));
							loadSchematic(p.getWorld(), medio, new Vector(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ()));
						}else if(tamanho >= Integer.parseInt(instance.getConfig().getString("Config.Schematic.Grande.Tamanho").split("-")[0]) && tamanho <= Integer.parseInt(instance.getConfig().getString("Config.Schematic.Grande.Tamanho").split("-")[1])){
							File grande = new File(instance.getDataFolder(), instance.getConfig().getString("Config.Schematic.Grande.Nome"));
							loadSchematic(p.getWorld(), grande, new Vector(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ()));
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void Deletar(Player p, String area){
		RegionManager regionManager = wg.getRegionManager(p.getWorld());
		if(regionManager.hasRegion(p.getName().toLowerCase() + "_" + area)){
			ProtectedRegion region = regionManager.getRegion(p.getName().toLowerCase() + "_" + area);
			LocalPlayer player = wg.wrapPlayer(p);
			if(region.isOwner(player)){
				try{
					RemoverPlaca(p, area, true);
					UnCercar(p, region.getMinimumPoint().getBlockX(), region.getMaximumPoint().getBlockX(), region.getMinimumPoint().getBlockZ(), region.getMaximumPoint().getBlockZ());
					if(instance.getConfig().getBoolean("Config.Limpar_Terreno_Ao_Deletar") == true){
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Deletando").replace("&", "§").replace("@area", area));
						Selection selection = new CuboidSelection(p.getWorld(), region.getMinimumPoint(), region.getMaximumPoint());
						Region regionin = selection.getRegionSelector().getRegion();
						regionin.getWorld().regenerate(regionin, WorldEdit.getInstance().getEditSessionFactory().getEditSession(regionin.getWorld(), -1));
					}
					regionManager.removeRegion(p.getName().toLowerCase() + "_" + area);
					regionManager.save();
				}catch(Exception e){
					e.printStackTrace();
				}
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Deletou").replace("&", "§").replace("@area", area));
			}else{
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Sua").replace("&", "§").replace("@area", area));
			}
		}else{
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Area_Nao_Encontrada").replace("&", "§").replace("@area", area));
		}
	}

	@SuppressWarnings("rawtypes")
	public static boolean Existe(Player p, String area, String mundo){
		Map regions = wg.getGlobalRegionManager().get(p.getWorld()).getRegions();
		Object[] set = regions.keySet().toArray();
		for(Object id : set){
			if(id.toString().equals(p.getName().toLowerCase() + "_" + area)){
				return true;
			}
		}
		return false;
	}

	public static void Renomear(Player p, String nome, String nnome){
		try{
			RegionManager rg = wg.getRegionManager(p.getWorld());
			if(rg.hasRegion(p.getName().toLowerCase() + "_" + nome)){
				ProtectedRegion region = rg.getRegion(p.getName().toLowerCase() + "_" + nome);
				LocalPlayer player = wg.wrapPlayer(p);
				if(region.isOwner(player)){
					if(!Existe(p, nnome, p.getWorld().getName())){
						flags = new HashMap<Flag<?>, Object>();
						amigos = new ArrayList<String>();
						flags.putAll(region.getFlags());
						amigos.addAll(region.getMembers().getPlayers());
						rg.removeRegion(p.getName().toLowerCase() + "_" + nome);
						BlockVector pos1 = new BlockVector(region.getMinimumPoint().getBlockX(), region.getMinimumPoint().getBlockY(), region.getMinimumPoint().getBlockZ());
						BlockVector pos2 = new BlockVector(region.getMaximumPoint().getBlockX(), region.getMaximumPoint().getBlockY(), region.getMaximumPoint().getBlockZ());
						ProtectedCuboidRegion pr = new ProtectedCuboidRegion(removerAcentos(p.getName().toLowerCase() + "_" + nnome.replace("_", "")), pos1, pos2);
						DefaultDomain dd = new DefaultDomain();
						rg.addRegion(pr);
						pr.setPriority(100);
						dd.addPlayer(p.getName());
						pr.setOwners(dd);
						pr.setFlags(flags);
						for(String player2 : amigos){
							pr.getMembers().addPlayer(player2.toLowerCase());
						}
						rg.save();
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Terreno_Renomeado").replace("@novonome", nnome).replace("@area", nome).replace("&", "§"));
						return;
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Existe_Terreno_Com_O_Nome").replace("@area", nnome).replace("&", "§"));
						return;
					}
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Sua").replace("&", "§").replace("@area", nome));
					return;
				}
			}else{
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Area_Nao_Encontrada").replace("@area", nome).replace("&", "§"));
				return;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	public static void Transferir(Player p, Player p2){
		try{
			RegionManager rg = wg.getRegionManager(p.getWorld());
			ApplicableRegionSet set = rg.getApplicableRegions(p.getLocation());
			if(set.size() == 0){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Em_Uma_Area").replace("&", "§"));
				return;
			}
			set.toString();
			String id = ((ProtectedRegion)set.iterator().next()).getId();
			ProtectedRegion regiao = rg.getRegion(id);
			LocalPlayer player = wg.wrapPlayer(p);
			if(regiao.isOwner(player)){
				if(!Existe(p2, id.split("_")[1], p.getWorld().getName())){
					if(instance.getTerrenoManager().GetAreas(p2) < instance.getConfig().getInt("Config.Grupos." + instance.perm.getPrimaryGroup(p))){
						flags = new HashMap<Flag<?>, Object>();
						amigos = new ArrayList<String>();
						flags.putAll(regiao.getFlags());
						amigos.addAll(regiao.getMembers().getPlayers());
						rg.removeRegion(p.getName().toLowerCase() + "_" + id.split("_")[1]);
						BlockVector pos1 = new BlockVector(regiao.getMinimumPoint().getBlockX(), regiao.getMinimumPoint().getBlockY(), regiao.getMinimumPoint().getBlockZ());
						BlockVector pos2 = new BlockVector(regiao.getMaximumPoint().getBlockX(), regiao.getMaximumPoint().getBlockY(), regiao.getMaximumPoint().getBlockZ());
						ProtectedCuboidRegion pr = new ProtectedCuboidRegion(removerAcentos(p2.getName().toLowerCase() + "_" + id.split("_")[1]), pos1, pos2);
						DefaultDomain dd = new DefaultDomain();
						rg.addRegion(pr);
						pr.setPriority(100);
						dd.addPlayer(p2.getName());
						pr.setOwners(dd);
						pr.setFlags(flags);
						for(String player2 : amigos){
							pr.getMembers().addPlayer(player2.toLowerCase());
						}
						rg.save();
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Terreno_Transferido").replace("@player", p2.getName()).replace("@area", id.split("_")[1]).replace("&", "§"));
						p2.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Terreno_Transferido_Voce").replace("@player", p.getName()).replace("@area", id.split("_")[1]).replace("&", "§"));
						return;
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Excedeu_Limite_Transferir").replace("@player", p2.getName()).replace("@limite", String.valueOf(instance.getConfig().getInt("Config.Grupos." + instance.perm.getPrimaryGroup(p)))).replace("&", "§"));
					}
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Existe_Terreno_Com_O_Nome_Transferir").replace("@player", p2.getName()).replace("@area", id.split("_")[1]).replace("&", "§"));
					return;
				}
			}else{
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Sua").replace("&", "§").replace("@area", id.split("_")[1]));
				return;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void addAmigo(Player p, String amigo, String nome){
		RegionManager rm = wg.getRegionManager(p.getWorld());
		if(rm.hasRegion(p.getName().toLowerCase() + "_" + nome)){
			ProtectedRegion region = rm.getRegion(p.getName().toLowerCase() + "_" + nome);
			LocalPlayer player = wg.wrapPlayer(p);
			if(region.isOwner(player)){
				if(!region.getMembers().contains(amigo.toLowerCase())){
					region.getMembers().addPlayer(amigo.toLowerCase());
					try{
						rm.save();
					}catch(Exception e){
						e.printStackTrace();
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Amigo_Adicionado").replace("@area", nome).replace("@amigo", amigo).replace("&", "§"));
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta_Adicionado").replace("@area", nome).replace("@amigo", amigo).replace("&", "§"));
				}
			}else{
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Sua").replace("&", "§").replace("@area", nome));
			}
		}else{
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Area_Nao_Encontrada").replace("&", "§").replace("@area", nome));
		}
	}

	public static void delAmigo(Player p, String amigo, String nome){
		RegionManager rm = wg.getRegionManager(p.getWorld());
		if(rm.hasRegion(p.getName().toLowerCase() + "_" + nome)){
			ProtectedRegion region = rm.getRegion(p.getName().toLowerCase() + "_" + nome);
			LocalPlayer player = wg.wrapPlayer(p);
			if(region.isOwner(player)){
				if(region.getMembers().contains(amigo.toLowerCase())){
					region.getMembers().removePlayer(amigo.toLowerCase());
					try{
						rm.save();
					}catch(Exception e){
						e.printStackTrace();
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Amigo_Removido").replace("@area", nome).replace("@amigo", amigo).replace("&", "§"));
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Adicionado").replace("@area", nome).replace("@amigo", amigo).replace("&", "§"));
				}
			}else{
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Sua").replace("&", "§").replace("@area", nome));
			}
		}else{
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Area_Nao_Encontrada").replace("&", "§").replace("@area", nome));
		}
	}

	@SuppressWarnings("static-access")
	public static void PvP(Player p, String pvp){
		RegionManager regionManager = wg.getRegionManager(p.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(p.getLocation());
		if(set.size() == 0){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Em_Uma_Area").replace("&", "§"));
			return;
		}
		set.toString();
		String id = ((ProtectedRegion)set.iterator().next()).getId();
		ProtectedRegion region = regionManager.getRegion(id);
		LocalPlayer player = wg.wrapPlayer(p);
		if(region.isOwner(player)){
			try{
				State a = region.getFlag(DefaultFlag.PVP);
				if(a == State.ALLOW && pvp.equals("allow")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.PvP_Ja_Ativado").replace("&", "§"));
					return;
				}else if(a == State.DENY && pvp.equals("deny")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.PvP_Nao_Ativado").replace("&", "§"));
					return;
				}
				region.setFlag(DefaultFlag.PVP, DefaultFlag.PVP.parseInput(wg, p, pvp));
				instance.econ.withdrawPlayer(p.getName(), instance.getConfig().getInt("Config.Preco_PvP"));
				regionManager.save();
			}catch(Exception e){
				e.printStackTrace();
			}
			if(pvp.equals("allow")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.PvP_Desativado").replace("&", "§").replace("@terreno", region.getId().replace(p.getName().toLowerCase() + "_", "")));
			}else if(pvp.equals("deny")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.PvP_Ativado").replace("&", "§").replace("@terreno", region.getId().replace(p.getName().toLowerCase() + "_", "")));
			}
		}else{
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Sua").replace("&", "§").replace("@area", region.getId().replace(p.getName().toLowerCase() + "_", "")));
		}
	}

	@SuppressWarnings("static-access")
	public static void Mob(Player p, String mob){
		RegionManager regionManager = wg.getRegionManager(p.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(p.getLocation());
		if(set.size() == 0){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Em_Uma_Area").replace("&", "§"));
			return;
		}
		set.toString();
		String id = ((ProtectedRegion)set.iterator().next()).getId();
		ProtectedRegion region = regionManager.getRegion(id);
		LocalPlayer player = wg.wrapPlayer(p);
		if(region.isOwner(player)){
			try{
				State a = region.getFlag(DefaultFlag.MOB_SPAWNING);
				if(a == State.ALLOW && mob.equals("allow")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Mob_Ja_Ativado").replace("&", "§"));
					return;
				}else if(a == State.DENY && mob.equals("deny")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Mob_Nao_Ativado").replace("&", "§"));
					return;
				}
				region.setFlag(DefaultFlag.MOB_SPAWNING, DefaultFlag.MOB_SPAWNING.parseInput(wg, p, mob));
				instance.econ.withdrawPlayer(p.getName(), instance.getConfig().getInt("Config.Preco_Mob"));
				regionManager.save();
			}catch(Exception e){
				e.printStackTrace();
			}
			if(mob.equals("allow")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Mob_Desativado").replace("&", "§").replace("@terreno", region.getId().replace(p.getName().toLowerCase() + "_", "")));
			}else if(mob.equals("deny")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Mob_Ativado").replace("&", "§").replace("@terreno", region.getId().replace(p.getName().toLowerCase() + "_", "")));
			}
		}else{
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Sua").replace("&", "§").replace("@area", region.getId().replace(p.getName().toLowerCase() + "_", "")));
		}
	}

	public static void Msg(Player p, String mensagem){
		RegionManager regionManager = wg.getRegionManager(p.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(p.getLocation());
		if(set.size() == 0){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Em_Uma_Area").replace("&", "§"));
			return;
		}
		set.toString();
		String id = ((ProtectedRegion)set.iterator().next()).getId();
		ProtectedRegion region = regionManager.getRegion(id);
		LocalPlayer player = wg.wrapPlayer(p);
		if(region.isOwner(player)){
			try {
				region.setFlag(DefaultFlag.GREET_MESSAGE, DefaultFlag.GREET_MESSAGE.parseInput(wg, p, "§b" + mensagem));
				regionManager.save();
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Mensagem_De_Boas_Vindas").replace("&", "§").replace("@msg", mensagem));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Sua").replace("&", "§").replace("@area", region.getId().replace(p.getName().toLowerCase() + "_", "")));
		}
	}

	public static void DesativarComando(Player p, String mensagem){
		RegionManager regionManager = wg.getRegionManager(p.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(p.getLocation());
		if(set.size() == 0){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Em_Uma_Area").replace("&", "§"));
			return;
		}
		set.toString();
		String id = ((ProtectedRegion)set.iterator().next()).getId();
		ProtectedRegion region = regionManager.getRegion(id);
		LocalPlayer player = wg.wrapPlayer(p);
		if(region.isOwner(player)){
			try{
				if(!mensagem.contains(",")){
					if(region.getFlag(DefaultFlag.BLOCKED_CMDS) != null){
						region.getFlag(DefaultFlag.BLOCKED_CMDS).add(mensagem);
						regionManager.save();
					}else{
						region.setFlag(DefaultFlag.BLOCKED_CMDS, DefaultFlag.BLOCKED_CMDS.parseInput(wg, p, mensagem));
						regionManager.save();
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Comando_Desativado").replace("&", "§"));
				}else{
					if(mensagem.endsWith(",")){
						while(mensagem.endsWith(",")){
							mensagem.substring(0, mensagem.length() - 1);
						}
					}
					String[] partes = mensagem.split(",");
					for(String cmds : partes){
						if(cmds.startsWith("/")){
							if(region.getFlag(DefaultFlag.BLOCKED_CMDS) != null){
								if(!region.getFlag(DefaultFlag.BLOCKED_CMDS).contains(cmds)){
									region.getFlag(DefaultFlag.BLOCKED_CMDS).add(cmds);
									regionManager.save();
									p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Comandos_Desativados").replace("@comandos", mensagem).replace("&", "§"));
								}
							}else{
								region.getFlag(DefaultFlag.BLOCKED_CMDS).add("/kgjklfjgkldfhjkldjh");
								if(!region.getFlag(DefaultFlag.BLOCKED_CMDS).contains(cmds)){
									region.getFlag(DefaultFlag.BLOCKED_CMDS).add(cmds);
									regionManager.save();
									p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Comandos_Desativados").replace("@comandos", mensagem).replace("&", "§"));
								}
								region.getFlag(DefaultFlag.BLOCKED_CMDS).remove("/kgjklfjgkldfhjkldjh");
							}
						}else{
							if(region.getFlag(DefaultFlag.BLOCKED_CMDS) != null){
								if(!region.getFlag(DefaultFlag.BLOCKED_CMDS).contains("/" + cmds)){
									region.getFlag(DefaultFlag.BLOCKED_CMDS).add("/" + cmds);
									regionManager.save();
									p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Comandos_Desativados").replace("@comandos", mensagem).replace("&", "§"));
								}
							}else{
								region.getFlag(DefaultFlag.BLOCKED_CMDS).add("/kgjklfjgkldfhjkldjh");
								if(!region.getFlag(DefaultFlag.BLOCKED_CMDS).contains("/" + cmds)){
									region.getFlag(DefaultFlag.BLOCKED_CMDS).add("/" + cmds);
									regionManager.save();
									p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Comandos_Desativados").replace("@comandos", mensagem).replace("&", "§"));
								}
								region.getFlag(DefaultFlag.BLOCKED_CMDS).remove("/kgjklfjgkldfhjkldjh");
							}
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Sua").replace("&", "§").replace("@area", region.getId().replace(p.getName().toLowerCase() + "_", "")));
		}
	}

	public static void AtivarComando(Player p, String mensagem){
		RegionManager regionManager = wg.getRegionManager(p.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(p.getLocation());
		if(set.size() == 0){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Em_Uma_Area").replace("&", "§"));
			return;
		}
		set.toString();
		String id = ((ProtectedRegion)set.iterator().next()).getId();
		ProtectedRegion region = regionManager.getRegion(id);
		LocalPlayer player = wg.wrapPlayer(p);
		if(region.isOwner(player)){
			try{
				if(!mensagem.contains(",")){
					if(region.getFlag(DefaultFlag.BLOCKED_CMDS) != null){
						if(region.getFlag(DefaultFlag.BLOCKED_CMDS).contains(mensagem)){
							region.getFlag(DefaultFlag.BLOCKED_CMDS).remove(mensagem);
							regionManager.save();
						}else{
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Comando_Nao_Adicionado").replace("&", "§"));
						}
					}else{
						region.setFlag(DefaultFlag.BLOCKED_CMDS, DefaultFlag.BLOCKED_CMDS.parseInput(wg, p, mensagem));
						regionManager.save();
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Comando_Ativado").replace("&", "§"));
				}else{
					if(mensagem.endsWith(",")){
						while(mensagem.endsWith(",")){
							mensagem.substring(0, mensagem.length() - 1);
						}
					}
					String[] partes = mensagem.split(",");
					for(String cmds : partes){
						if(cmds.startsWith("/")){
							if(region.getFlag(DefaultFlag.BLOCKED_CMDS) != null){
								if(region.getFlag(DefaultFlag.BLOCKED_CMDS).contains(cmds)){
									region.getFlag(DefaultFlag.BLOCKED_CMDS).remove(cmds);
									regionManager.save();
									p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Comandos_Ativados").replace("@comandos", mensagem).replace("&", "§"));
								}
							}else{
								p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Comandos_Bloqueados").replace("&", "§"));
							}
						}else{
							if(region.getFlag(DefaultFlag.BLOCKED_CMDS) != null){
								if(region.getFlag(DefaultFlag.BLOCKED_CMDS).contains("/" + cmds)){
									region.getFlag(DefaultFlag.BLOCKED_CMDS).remove("/" + cmds);
									regionManager.save();
									p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Comandos_Ativados").replace("@comandos", mensagem).replace("&", "§"));
								}
							}else{
								p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Comandos_Bloqueados").replace("&", "§"));
							}
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Sua").replace("&", "§").replace("@area", region.getId().replace(p.getName().toLowerCase() + "_", "")));
		}
	}

	public static void tpArea(Player p, String area){
		RegionManager regionManager = wg.getRegionManager(p.getWorld());
		if(regionManager.hasRegion(p.getName().toLowerCase() + "_" + area)){
			ProtectedRegion region = regionManager.getRegion(p.getName().toLowerCase() + "_" + area);
			LocalPlayer player = wg.wrapPlayer(p);
			if(region.isOwner(player)){
				try{
					p.teleport(new Location(p.getWorld(), region.getMaximumPoint().getX(), p.getWorld().getHighestBlockYAt(region.getMaximumPoint().getBlockX(), region.getMaximumPoint().getBlockZ()), region.getMaximumPoint().getZ()));
				}catch(Exception e){
					e.printStackTrace();
				}
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Teleportado").replace("&", "§"));
			}else{
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Sua").replace("&", "§").replace("@area", area));
			}
		}else{
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Area_Nao_Encontrada").replace("&", "§").replace("@area", area));
		}
	}

	public static void tpAreaStaff(Player p, String player, String area){
		RegionManager regionManager = wg.getRegionManager(p.getWorld());
		if(regionManager.hasRegion(player.toLowerCase() + "_" + area)){
			ProtectedRegion region = regionManager.getRegion(player.toLowerCase() + "_" + area);
			try{
				p.teleport(new Location(p.getWorld(), region.getMaximumPoint().getX(), p.getWorld().getHighestBlockYAt(region.getMaximumPoint().getBlockX(), region.getMaximumPoint().getBlockZ()), region.getMaximumPoint().getZ()));
			}catch(Exception e){
				e.printStackTrace();
			}
			p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Teleportado_Staff").replace("@player", player).replace("&", "§"));
		}else{
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Area_Nao_Encontrada").replace("&", "§").replace("@area", area));
		}
	}

	public static void Info(Player p){
		RegionManager regionManager = wg.getRegionManager(p.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(p.getLocation());
		if(set.size() == 0){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Em_Uma_Area").replace("&", "§"));
			return;
		}
		set.toString();
		String id = ((ProtectedRegion)set.iterator().next()).getId();
		ProtectedRegion region = regionManager.getRegion(id);
		String msg = "";
		for(String s : instance.getConfig().getStringList("Mensagem.Sucesso.Info_Area")) msg += s.replace("&", "§") + "\n";
		msg = msg.substring(0, msg.length()-2);
		List<String> flagspadroes = new ArrayList<>();
		for(String flags : instance.getConfig().getStringList("Config.Flags_Padroes"))
			flagspadroes.add(flags.split(" ")[0].toUpperCase());
		for(Entry<Flag<?>, Object> flags : region.getFlags().entrySet()){
			if(flagspadroes.contains(flags.getKey().getName().toUpperCase())){
				msg = msg.replace("@" + flags.getKey().getName().toLowerCase(), flags.getValue().toString().replace("ALLOW", "Ativado").replace("DENY", "Desativado"));
			}else{
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Em_Um_Terreno").replace("&", "§"));
			}
		}
		if(region.getOwners().size() > 0)
			p.sendMessage(msg.replace("@dono", region.getOwners().getPlayers().toArray()[0] + ""));
	}

	public static void Vender(Player p, int quantia){
		RegionManager regionManager = wg.getRegionManager(p.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(p.getLocation());
		if(set.size() == 0){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Em_Uma_Area").replace("&", "§"));
			return;
		}
		set.toString();
		String id = ((ProtectedRegion)set.iterator().next()).getId();
		ProtectedRegion region = regionManager.getRegion(id);
		LocalPlayer player = wg.wrapPlayer(p);
		if(region.isOwner(player)){
			int estaavenda = RemoverPlaca(p, id, false);
			if(estaavenda == 0){
				p.getLocation().getBlock().setType(Material.SIGN_POST);
				Sign s = (Sign) p.getLocation().getBlock().getState();
				s.setLine(0, "§9" + p.getName());
				s.setLine(1, "§9Vendendo");
				s.setLine(2, "§9Preco");
				s.setLine(3, "§9" + String.valueOf(quantia));
				s.update();
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Vendendo").replace("&", "§"));
			}else{
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta_Sendo_Vendido").replace("&", "§"));
			}
		}else{
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Sua").replace("&", "§"));
		}
	}
	
	public static void getAmigos(Player p){
		RegionManager regionManager = wg.getRegionManager(p.getWorld());
		ApplicableRegionSet set = regionManager.getApplicableRegions(p.getLocation());
		if(set.size() == 0){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Em_Uma_Area").replace("&", "§"));
		}else{
			String id = ((ProtectedRegion)set.iterator().next()).getId();
			ProtectedRegion region = regionManager.getRegion(id);
			LocalPlayer player = wg.wrapPlayer(p);
			if(region.getMembers().size() == 0){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Amigos").replace("&", "§"));
			}else{
				if(region.isOwner(player)){
					String amigos = "";
					for(String membros : region.getMembers().getPlayers()){
						amigos += membros + ", ";
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Amigos").replace("&", "§").replace("@amigos", amigos.substring(0, amigos.length() - 2)));
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Sua").replace("&", "§"));
				}
			}
		}
	}

	public static int RemoverPlaca(Player p, String area, boolean removerplaca){
		RegionManager rm = wg.getRegionManager(p.getWorld());
		int x = rm.getRegion(p.getName().toLowerCase() + "_" + area).getMinimumPoint().getBlockX();
		int y = rm.getRegion(p.getName().toLowerCase() + "_" + area).getMinimumPoint().getBlockY();
		int z = rm.getRegion(p.getName().toLowerCase() + "_" + area).getMinimumPoint().getBlockZ();
		int x1 = rm.getRegion(p.getName().toLowerCase() + "_" + area).getMaximumPoint().getBlockX();
		int y1 = rm.getRegion(p.getName().toLowerCase() + "_" + area).getMaximumPoint().getBlockY();
		int z1 = rm.getRegion(p.getName().toLowerCase() + "_" + area).getMaximumPoint().getBlockZ();
		int i = 0;
		for(int xi = x; xi <= x1; xi++){
			for(int yi = y; yi <= y1; yi++){
				for(int zi = z; zi <= z1; zi++){
					Location l = new Location(p.getWorld(), xi, yi, zi);
					if (l.getBlock().getState().getType() == Material.SIGN_POST){
						Sign s = (Sign)l.getBlock().getState();
						if (s.getLine(0).equalsIgnoreCase("§9" + p.getName()) && s.getLine(1).equalsIgnoreCase("§9Vendendo") && s.getLine(2).equalsIgnoreCase("§9Preco") && !s.getLine(3).isEmpty()){
							if(i == 0 && removerplaca) l.getBlock().setType(Material.AIR);
							i++;
						}
					}
				}
			}
		}
		return i;
	}

	/*public static void paste(String schematicName, Location pasteLoc){
    	try{
            File dir = new File(instance.getDataFolder(), schematicName);

            EditSession editSession = new EditSession(new BukkitWorld(pasteLoc.getWorld()), 999999999);
            editSession.enableQueue();

            SchematicFormat schematic = SchematicFormat.getFormat(dir);
            CuboidClipboard clipboard = schematic.load(dir);

            clipboard.paste(editSession, BukkitUtil.toVector(pasteLoc), true);
            editSession.flushQueue();
        }catch(DataException | IOException ex){
            ex.printStackTrace();
        }catch(MaxChangedBlocksException ex){
            ex.printStackTrace();
        }
    }*/

}

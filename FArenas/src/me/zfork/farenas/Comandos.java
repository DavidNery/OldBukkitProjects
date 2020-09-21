package me.zfork.farenas;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Random;

import me.zfork.farenas.arena.Arena;
import me.zfork.farenas.arena.ArenaManager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Comandos implements CommandExecutor{
	
	private FArenas instance = FArenas.getFArenas();
	private ArenaManager am = instance.getArenaManager();
	private final Random r = new Random();
	private DecimalFormat df = new DecimalFormat("0");
	
	@SuppressWarnings("static-access")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage("Unknown command. Type \"help\" for help.");
			return true;
		}
		Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("arena")){
			if(args.length == 0){
				sendMessageList(p, "Comandos");
				return true;
			}else if(args[0].equalsIgnoreCase("entrar")){
				if(args.length == 1){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Entrar").replace("&", "§"));
					return true;
				}else if(!am.hasArena(args[1])){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Nao_Existe").replace("&", "§").replace("@arena", args[1]));
					return true;
				}
				Arena arena = am.getArenaByName(args[1]);
				if(am.hasFlag(arena.getNome(), "manutencao") && am.getFlagValue(arena.getNome(), "manutencao").equalsIgnoreCase("true")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Em_Manutencao").replace("&", "§").replace("@arena", args[1]));
					return true;
				}else if(am.hasFlag(arena.getNome(), "maxplayers") && Integer.parseInt(am.getFlagValue(arena.getNome(), "maxplayers")) != 0 && arena.getPlayers().size() == Integer.parseInt(am.getFlagValue(arena.getNome(), "maxplayers")) && (!p.hasPermission("farenas.bypass." + args[1]))){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Lotada").replace("&", "§").replace("@arena", args[1]));
					return true;
				}else if(am.hasFlag(arena.getNome(), "clearedinventory") && am.getFlagValue(arena.getNome(), "clearedinventory").equalsIgnoreCase("true") && invVazio(p)){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inventario_Vazio").replace("&", "§").replace("@arena", args[1]));
					return true;
				}else if(am.getArenaByPlayer(p) != null){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta").replace("&", "§"));
					return true;
				}else if(arena.getFC().getBoolean("Ativar_Permissao") && (!p.hasPermission(arena.getFC().getString("Permissao")))){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao_Arena").replace("&", "§").replace("@arena", arena.getNome()));
					return true;
				}
				Location spawn = arena.getSpawns().get(r.nextInt(arena.getSpawns().size()));
				if(spawn.getWorld() == null){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Mundo_Nao_Existe").replace("&", "§"));
					return true;
				}
				p.teleport(spawn);
				arena.getPlayers().add(p.getName().toLowerCase());
				p.playSound(p.getLocation(), Sound.LEVEL_UP, 5.0F, 1.0F);
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Entrou_Arena").replace("&", "§").replace("@arena", args[1]));
				if(arena.getFC().getBoolean("Ativar_BC")){
					for(Player on : instance.getServer().getOnlinePlayers()){
						if(on != null) on.sendMessage(instance.getConfig().getString("Mensagem.Entrou_Arena").replace("&", "§").replace("@player", p.getName()).replace("@arena", args[1]));
					}
				}
				if(arena.getItens() != null && arena.getItens().size() != 0){
					for(ItemStack items : arena.getItens()){
						if(p.getInventory().firstEmpty() != -1){
							p.getInventory().setItem(p.getInventory().firstEmpty(), items);
						}
					}
				}
				if(arena.getArmor() != null && arena.getArmor().length != 0){
					if(arena.getArmor()[0] != null && p.getInventory().getHelmet() == null){
						p.getInventory().setHelmet(arena.getArmor()[0]);
					}
					if(arena.getArmor()[1] != null && p.getInventory().getChestplate() == null){
						p.getInventory().setChestplate(arena.getArmor()[1]);
					}
					if(arena.getArmor()[2] != null && p.getInventory().getLeggings() == null){
						p.getInventory().setLeggings(arena.getArmor()[2]);
					}
					if(arena.getArmor()[3] != null && p.getInventory().getBoots() == null){
						p.getInventory().setBoots(arena.getArmor()[3]);
					}
				}
			}else if(args[0].equalsIgnoreCase("sair")){
				Arena arena = am.getArenaByPlayer(p);
				if(arena == null){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta").replace("&", "§"));
					return true;
				}
				arena.getPlayers().remove(p.getName().toLowerCase());
				p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 5.0F, 1.0F);
				p.teleport(arena.getExit());
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Saiu_Arena").replace("&", "§").replace("@arena", arena.getNome()));
				if(am.hasFlag(arena.getNome(), "clearinventory") && am.getFlagValue(arena.getNome(), "clearinventory").equalsIgnoreCase("true")){
					p.getInventory().clear();
					p.getInventory().setArmorContents(null);
				}
			}else if(args[0].equalsIgnoreCase("criar")){
				if(!p.hasPermission("farenas.criar")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}else if(args.length == 1){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Criar").replace("&", "§"));
					return true;
				}else if(am.hasArena(args[1])){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Ja_Existe").replace("&", "§").replace("@arena", args[1]));
					return true;
				}
				am.criarArena(args[1]);
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Criou_Arena").replace("&", "§").replace("@arena", args[1]));
			}else if(args[0].equalsIgnoreCase("setexit")){
				if(!p.hasPermission("farenas.setexit")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}else if(args.length == 1){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.SetExit").replace("&", "§"));
					return true;
				}else if(!am.hasArena(args[1])){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Nao_Existe").replace("&", "§").replace("@arena", args[1]));
					return true;
				}
				am.setExit(args[1], p.getLocation());
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Saida").replace("@arena", args[1]).replace("&", "§"));
			}else if(args[0].equalsIgnoreCase("addspawn")){
				if(!p.hasPermission("farenas.addspawn")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}else if(args.length == 1){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.AddSpawn").replace("&", "§"));
					return true;
				}else if(!am.hasArena(args[1])){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Nao_Existe").replace("&", "§").replace("@arena", args[1]));
					return true;
				}
				am.addSpawn(args[1], p.getLocation());
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Adicionou_Spawn").replace("@arena", args[1]).replace("&", "§"));
			}else if(args[0].equalsIgnoreCase("delspawn")){
				if(!p.hasPermission("farenas.delspawn")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}else if(args.length <= 2){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.DelSpawn").replace("&", "§"));
					return true;
				}else if(!am.hasArena(args[1])){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Nao_Existe").replace("&", "§").replace("@arena", args[1]));
					return true;
				}
				if(!isNum(args[2])){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Numero").replace("&", "§"));
					return true;
				}
				Arena arena = am.getArenaByName(args[1]);
				if(arena.getSpawns().size() - 1 == 0){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Pode_Remover").replace("&", "§"));
					return true;
				}
				try{
					am.removeSpawn(args[1], Integer.parseInt(args[2]));
				}catch(IndexOutOfBoundsException e){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Tem_Spawn").replace("&", "§"));
					return true;
				}
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Removeu_Spawn").replace("@arena", args[1]).replace("&", "§"));
			}else if(args[0].equalsIgnoreCase("load")){
				if(!p.hasPermission("farenas.load")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}else if(args.length == 1){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Load").replace("&", "§"));
					return true;
				}else if((!args[1].equalsIgnoreCase("all")) && am.hasArena(args[1])){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Ja_Existe").replace("&", "§").replace("@arena", args[1]));
					return true;
				}
				if(args[1].equalsIgnoreCase("all")){
					int i = 0;
					for(File file : new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas").listFiles()){
						if(!am.hasArena(file.getName().replace(".yml", ""))){
							FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
							am.loadArena(file, fc);
							i++;
						}
					}
					if(i == 0){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Arenas").replace("&", "§"));
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Arenas_Carregadas").replace("@qnt", i + "").replace("&", "§"));
					}
					return true;
				}
				if(!am.hasArena(args[1])){
					File f = new File("plugins" + System.getProperty("file.separator") + "FArenas" + System.getProperty("file.separator") + "arenas" + System.getProperty("file.separator") + args[1] + ".yml");
					if(!f.exists()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arquivo_Nao_Encontrado").replace("@arena", args[1]).replace("&", "§"));
						return true;
					}
					FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
					am.loadArena(f, fc);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Arena_Carregada").replace("@arena", args[1]).replace("&", "§"));
				}
			}else if(args[0].equalsIgnoreCase("reload")){
				if(!p.hasPermission("farenas.reload")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}else if(args.length == 1){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Reload").replace("&", "§"));
					return true;
				}else if(!am.hasArena(args[1])){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Nao_Existe").replace("&", "§").replace("@arena", args[1]));
					return true;
				}
				am.reloadArena(args[1]);
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Arena_Recarregada").replace("@arena", args[1]).replace("&", "§"));
			}else if(args[0].equalsIgnoreCase("setflag")){
				if(!p.hasPermission("farenas.setflag")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}else if(args.length <= 3){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Set_Flag").replace("&", "§"));
					return true;
				}
				String arenaa = args[1];
				String flag = args[2].toLowerCase();
				String value = args[3].toLowerCase();
				if(!am.hasArena(arenaa)){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Nao_Existe").replace("&", "§").replace("@arena", arenaa));
					return true;
				}
				Arena arena = am.getArenaByName(arenaa);
				if(!(flag.equalsIgnoreCase("maxplayers") || flag.equalsIgnoreCase("manutencao") || flag.equalsIgnoreCase("moneyonkill")
						 || flag.equalsIgnoreCase("keepinventory") || flag.equalsIgnoreCase("dropinventory") || flag.equalsIgnoreCase("dckill")
						 || flag.equalsIgnoreCase("healonkill") || flag.equalsIgnoreCase("clearedinventory") || flag.equalsIgnoreCase("clearinventory")
						 || flag.equalsIgnoreCase("nodrop"))){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Flag_Nao_Existe").replace("&", "§").replace("@flag", flag));
					return true;
				}else if(am.hasFlag(arena.getNome(), flag) && (!isNum(value)) && am.getFlagValue(arena.getNome(), flag).equalsIgnoreCase(value)){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Tem_Flag").replace("&", "§"));
					return true;
				}else if(am.hasFlag(arena.getNome(), flag) && am.getFlagValue(arena.getNome(), flag).equalsIgnoreCase(value.equalsIgnoreCase("on") ? "true" : "false")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Tem_Flag").replace("&", "§"));
					return true;
				}else if(flag.equalsIgnoreCase("maxplayers")){
					if(!isNum(value)){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Numero").replace("&", "§"));
						return true;
					}
					arena.setMaxPlayers(Integer.parseInt(value));
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Flag").replace("@arena", arena.getNome()).replace("@valor", value).replace("@flag", flag).replace("&", "§"));
				}else if(flag.equalsIgnoreCase("moneyonkill")){
					if(!isNum(value)){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Numero").replace("&", "§"));
						return true;
					}
					arena.setMoneyOnKill(Integer.parseInt(value));
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Flag").replace("@arena", arena.getNome()).replace("@valor", value).replace("@flag", flag).replace("&", "§"));
				}else if(flag.equalsIgnoreCase("manutencao")){
					if(!(value.equalsIgnoreCase("on") || value.equalsIgnoreCase("off"))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Boolean").replace("&", "§"));
						return true;
					}
					arena.setManutencao(value.equalsIgnoreCase("on") ? true : false);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Flag").replace("@arena", arena.getNome()).replace("@valor", value).replace("@flag", flag).replace("&", "§"));
				}else if(flag.equalsIgnoreCase("keepinventory")){
					if(!(value.equalsIgnoreCase("on") || value.equalsIgnoreCase("off"))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Boolean").replace("&", "§"));
						return true;
					}
					arena.setKeepInventory(value.equalsIgnoreCase("on") ? true : false);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Flag").replace("@arena", arena.getNome()).replace("@valor", value).replace("@flag", flag).replace("&", "§"));
				}else if(flag.equalsIgnoreCase("dropinventory")){
					if(!(value.equalsIgnoreCase("on") || value.equalsIgnoreCase("off"))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Boolean").replace("&", "§"));
						return true;
					}
					arena.setDropInventory(value.equalsIgnoreCase("on") ? true : false);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Flag").replace("@arena", arena.getNome()).replace("@valor", value).replace("@flag", flag).replace("&", "§"));
				}else if(flag.equalsIgnoreCase("dckill")){
					if(!(value.equalsIgnoreCase("on") || value.equalsIgnoreCase("off"))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Boolean").replace("&", "§"));
						return true;
					}
					arena.setDCKill(value.equalsIgnoreCase("on") ? true : false);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Flag").replace("@arena", arena.getNome()).replace("@valor", value).replace("@flag", flag).replace("&", "§"));
				}else if(flag.equalsIgnoreCase("healonkill")){
					if(!(value.equalsIgnoreCase("on") || value.equalsIgnoreCase("off"))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Boolean").replace("&", "§"));
						return true;
					}
					arena.setHealOnKill(value.equalsIgnoreCase("on") ? true : false);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Flag").replace("@arena", arena.getNome()).replace("@valor", value).replace("@flag", flag).replace("&", "§"));
				}else if(flag.equalsIgnoreCase("clearedinventory")){
					if(!(value.equalsIgnoreCase("on") || value.equalsIgnoreCase("off"))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Boolean").replace("&", "§"));
						return true;
					}
					arena.setClearedInventory(value.equalsIgnoreCase("on") ? true : false);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Flag").replace("@arena", arena.getNome()).replace("@valor", value).replace("@flag", flag).replace("&", "§"));
				}else if(flag.equalsIgnoreCase("clearinventory")){
					if(!(value.equalsIgnoreCase("on") || value.equalsIgnoreCase("off"))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Boolean").replace("&", "§"));
						return true;
					}
					arena.setClearInventory(value.equalsIgnoreCase("on") ? true : false);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Flag").replace("@arena", arena.getNome()).replace("@valor", value).replace("@flag", flag).replace("&", "§"));
				}else if(flag.equalsIgnoreCase("nodrop")){
					if(!(value.equalsIgnoreCase("on") || value.equalsIgnoreCase("off"))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Boolean").replace("&", "§"));
						return true;
					}
					arena.setNoDrop(value.equalsIgnoreCase("on") ? true : false);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Setou_Flag").replace("@arena", arena.getNome()).replace("@valor", value).replace("@flag", flag).replace("&", "§"));
				}
			}else if(args[0].equalsIgnoreCase("delflag")){
				if(!p.hasPermission("farenas.delflag")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}else if(args.length <= 2){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Del_Flag").replace("&", "§"));
					return true;
				}
				String arenaa = args[1];
				String flag = args[2].toLowerCase();
				if(!am.hasArena(arenaa)){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Nao_Existe").replace("&", "§").replace("@arena", arenaa));
					return true;
				}
				Arena arena = am.getArenaByName(arenaa);
				if(!(flag.equalsIgnoreCase("maxplayers") || flag.equalsIgnoreCase("manutencao") || flag.equalsIgnoreCase("moneyonkill")
						 || flag.equalsIgnoreCase("keepinventory") || flag.equalsIgnoreCase("dropinventory") || flag.equalsIgnoreCase("dckill")
						 || flag.equalsIgnoreCase("healonkill") || flag.equalsIgnoreCase("clearedinventory") || flag.equalsIgnoreCase("clearinventory")
						 || flag.equalsIgnoreCase("nodrop"))){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Flag_Nao_Existe").replace("&", "§").replace("@flag", flag));
					return true;
				}else if(!am.hasFlag(arena.getNome(), flag)){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Tem_Flag").replace("&", "§"));
					return true;
				}
				am.delFlag(arena.getNome(), flag);
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Flag_Deletado").replace("@arena", arena.getNome()).replace("@flag", flag).replace("&", "§"));
			}else if(args[0].equalsIgnoreCase("lista")){
				if(!p.hasPermission("farenas.lista")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}
				StringBuilder arenas = new StringBuilder();
				for(Arena arena : am.getArenasLoaded()){
					arenas.append(arena.getNome() + ", ");
				}
				if(arenas.length() > 0){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Lista_Arenas").replace("@arenas", arenas.toString().substring(0, arenas.length()-2)).replace("&", "§"));
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Arenas").replace("&", "§"));
				}
			}else if(args[0].equalsIgnoreCase("info")){
				if(!p.hasPermission("farenas.info")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}else if(args.length <= 1){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Info").replace("&", "§"));
					return true;
				}else if(!am.hasArena(args[1])){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Nao_Existe").replace("&", "§").replace("@arena", args[1]));
					return true;
				}
				Arena arena = am.getArenaByName(args[1]);
				StringBuilder comandosliberados = new StringBuilder();
				for(String cmds : arena.getFC().getStringList("Comandos_Liberados")){
					comandosliberados.append(cmds + ", ");
				}
				StringBuilder flags = new StringBuilder();
				for(String flag : arena.getFlags()){
					String[] partes = flag.split(" ");
					if(isNum(partes[1])){
						flags.append(partes[0] + " " + partes[1] + ", ");
					}else{
						flags.append(partes[0] + " " + (partes[1].equalsIgnoreCase("true") ? "on" : "off") + ", ");
					}
				}
				StringBuilder spawns = new StringBuilder();
				for(int i = 0; i<arena.getFC().getStringList("Spawns").size(); i++){
					String spawn = arena.getFC().getStringList("Spawns").get(i);
					String[] partes = spawn.split(" ");
					spawns.append(instance.getConfig().getString("Mensagem.Forma_Da_Location")
							.replace("&", "&").replace("@id", i + "").replace("@world", partes[0])
							.replace("@x", df.format(Double.parseDouble(partes[1])))
							.replace("@y", df.format(Double.parseDouble(partes[2]))) 
							.replace("@z", df.format(Double.parseDouble(partes[3]))) 
							.replace("@Yaw", df.format(Double.parseDouble(partes[4]))) 
							.replace("@pitch", df.format(Double.parseDouble(partes[5]))) + "\n");
				}
				for(String msg : instance.getConfig().getStringList("Mensagem.Info")){
					p.sendMessage(msg.replace("@arena", arena.getNome()).replace("@spawns", spawns.toString().substring(0, spawns.length()-2))
							.replace("@flags", flags.length() != 0 ? flags.toString().substring(0, flags.length()-2) : "Nenhum")
							.replace("@comandos_liberados", comandosliberados.length() != 0 ? comandosliberados.toString().substring(0, comandosliberados.length()-2) : "Nenhum")
							.replace("@permissao_ativada", arena.getFC().getString("Ativar_Permissao").equalsIgnoreCase("true") ? "on" : "off")
							.replace("@permissao", arena.getFC().getString("Permissao"))
							.replace("@reducao", arena.getFC().getString("Ativar_Reducao_Comando").equalsIgnoreCase("true") ? "on" : "off").replace("&", "§"));
				}
			}else{
				sendMessageList(p, "Comandos");
				return true;
			}/*else if(args[0].equalsIgnoreCase("deletar")){
				if(!p.hasPermission("farenas.deletar")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§"));
					return true;
				}else if(args.length == 1){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Deletar").replace("&", "§"));
					return true;
				}else if(!am.hasArena(args[1])){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Arena_Nao_Existe").replace("&", "§").replace("@arena", args[1]));
					return true;
				}
				am.removeArena(args[1]);
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Deletou_Arena").replace("&", "§").replace("@nome", args[1]));
			}*/
		}
		return false;
	}
	
	public void sendMessageList(CommandSender sender, String lista){
		for(String msg : instance.getConfig().getStringList("Mensagem." + lista)){
			sender.sendMessage(msg.replace("&", "§"));
		}
	}
	
	public boolean invVazio(Player p){
		for(ItemStack item : p.getInventory().getContents()){
			if(item != null && item.getType() != Material.AIR) return true;
		}
		for(ItemStack item : p.getInventory().getArmorContents()){
			if(item != null && item.getType() != Material.AIR) return true;
		}
		return false;
	}
	
	public boolean isNum(String string){
		try{
			Integer.parseInt(string);
			return true;
		}catch(NumberFormatException e){}
		return false;
	}

}

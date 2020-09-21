package me.zfork.fvip;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import me.zfork.fvip.api.PlayerActiveVipEvent;
import me.zfork.fvip.api.PlayerActiveVipEvent.ActivationType;
import me.zfork.fvip.builders.TitleBuilder;
import me.zfork.fvip.vendakey.VendaKey;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Comandos implements CommandExecutor{

	private FVip instance = FVip.getFVIP();
	private SQLite sqlite = instance.getSQLite();
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private Random r = new Random();

	@SuppressWarnings("static-access")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("darvip")){
			if(!sender.hasPermission("fvip.darvip")){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "з"));
				return true;
			}else if(args.length <= 2){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Dar_VIP").replace("&", "з"));
				return true;
			}else if(!isNum(args[1])){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Numero").replace("&", "з"));
				return true;
			}
			String grupo = null;
			for(String grp : instance.getConfig().getConfigurationSection("Config.Grupos_VIPs").getKeys(false)){
				if(grp.equalsIgnoreCase(args[2])){
					grupo = grp;
					break;
				}
			}
			if(grupo == null){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Grupo_VIP_Nao_Existe").replace("&", "з"));
				return true;
			}
			Player p = instance.getServer().getPlayer(args[0]);
			if(p == null){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "з"));
				return true;
			}
			try {
				String tempo = "";
				sqlite.openConnection();
				if(sqlite.hasPlayerGrupo(p.getName(), grupo)){
					tempo = (TimeUnit.DAYS.toMillis(Integer.parseInt(args[1])) + sqlite.getDias(p.getName(), grupo)) + "";
					sqlite.renew(p.getName(), tempo, grupo);
				}else{
					tempo = (System.currentTimeMillis()+TimeUnit.DAYS.toMillis(Integer.parseInt(args[1]))) + "";
					sqlite.addNew(p.getName(), tempo, grupo);
					if(sqlite.getPlayerGroupUsando(p.getName()) == null)
						sqlite.setUsando(p.getName(), grupo, true);
				}
				sqlite.closeConnection();
				sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Deu_VIP").replace("{player}", p.getName()).replace("{grupo}", grupo)
						.replace("{tempo}", sdf.format(new Date(Long.parseLong(tempo)))).replace("&", "з"));
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Ganhou_VIP")
						.replace("{staff}", sender instanceof Player ? ((Player) sender).getName() : "CONSOLE").replace("{grupo}", grupo)
						.replace("{player}", p.getName()).replace("{tempo}", sdf.format(new Date(Long.parseLong(tempo))))
						.replace("&", "з"));
				for(String s : instance.getConfig().getStringList("Config.Comandos_Executados"))
					instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), s.replace("{player}", p.getName()).replace("{grupo}", grupo));
				for(String items : instance.getConfig().getStringList("Config.Items")){
					String[] grupos = items.split(" ")[0].split("grupo:")[1].split(" ")[0].split(",");
					for(String gr : grupos){
						if(gr.equalsIgnoreCase(grupo)){
							if(items.contains("xp:")){
								p.giveExpLevels(Integer.parseInt(items.split("xp:")[1]));
							}else if(items.contains("cmd:")){
								instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), items.split("cmd:")[1].replace("{player}", p.getName()));
							}else{
								ItemStack item = (ItemStack) instance.criarItem(items);
								if(p.getInventory().firstEmpty() != -1){
									p.getInventory().addItem(item == null ? new ItemStack(Material.AIR) : item);
								}else{
									p.getWorld().dropItemNaturally(p.getLocation(), item == null ? new ItemStack(Material.AIR) : item);
								}
							}
							break;
						}
					}
				}
			} catch (ClassNotFoundException | SQLException e) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
				return true;
			}
		}else if(cmd.getName().equalsIgnoreCase("tirarvip")){
			if(!sender.hasPermission("fvip.tirarvip")){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "з"));
				return true;
			}else if(args.length == 0){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Tirar_VIP").replace("&", "з"));
				return true;
			}
			Player p = instance.getServer().getPlayer(args[0]);
			try {
				sqlite.openConnection();
				if(args.length == 1){
					if(!sqlite.hasPlayer(p == null ? args[0] : p.getName())){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Nao_Eh_VIP").replace("&", "з").replace("{player}", p == null ? args[0] : p.getName()));
						sqlite.closeConnection();
						return true;
					}
					sqlite.delPlayer(p == null ? args[0] : p.getName());
					sqlite.closeConnection();
					sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Tirou_VIP").replace("{player}", p == null ? args[0] : p.getName()).replace("&", "з"));
					if(p != null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Perdeu_VIP")
								.replace("{staff}", sender instanceof Player ? ((Player) sender).getName() : "CONSOLE").replace("{player}", p.getName()).replace("&", "з"));
					}
					for(String s : instance.getConfig().getStringList("Config.Comandos_Executados_Perder"))
						instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), s.replace("{player}", p == null ? args[0] : p.getName()));
				}else{
					String grupo = null;
					for(String grp : instance.getConfig().getConfigurationSection("Config.Grupos_VIPs").getKeys(false)){
						if(grp.equalsIgnoreCase(args[1])){
							grupo = grp;
							break;
						}
					}
					if(grupo == null){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Grupo_VIP_Nao_Existe").replace("&", "з"));
						sqlite.closeConnection();
						return true;
					}else if(!sqlite.hasPlayerGrupo(p == null ? args[0] : p.getName(), grupo)){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Nao_Tem_VIP").replace("&", "з").replace("{player}", p.getName()));
						sqlite.closeConnection();
						return true;
					}
					sqlite.delPlayerGroup(p == null ? args[0] : p.getName(), grupo);
					sqlite.closeConnection();
					sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Tirou_Grupo_VIP").replace("{grupo}", grupo).replace("{player}", p == null ? args[0] : p.getName()).replace("&", "з"));
					if(p != null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Perdeu_Grupo_VIP")
								.replace("{staff}", sender instanceof Player ? ((Player) sender).getName() : "CONSOLE").replace("{grupo}", grupo).replace("{player}", p.getName()).replace("&", "з"));
					}
					for(String s : instance.getConfig().getStringList("Config.Comandos_Executados_Perder"))
						instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), s.replace("{player}", p == null ? args[0] : p.getName()));
				}
			} catch (ClassNotFoundException | SQLException e) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
				return true;
			}
		}else if(cmd.getName().equalsIgnoreCase("diasvip")){
			if(args.length == 0){
				if(!(sender instanceof Player)){
					sender.sendMessage("з4Comando apenas para players!");
					return true;
				}
				Player p = (Player) sender;
				try {
					sqlite.openConnection();
					if(!sqlite.hasPlayer(p.getName())){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Voce_Nao_Eh_VIP").replace("&", "з").replace("{player}", p.getName()));
						sqlite.closeConnection();
						return true;
					}
					if(sqlite.getPlayerGroupUsando(p.getName()) == null){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Usando_VIP").replace("&", "з"));
						sqlite.closeConnection();
						return true;
					}
					String vip = sqlite.getPlayerGroupUsando(p.getName());
					long time = sqlite.getDias(p.getName(), vip)-System.currentTimeMillis();
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Dias_Dias").replace("&", "з").replace("{vip}", vip)
							.replace("{acaba}", instance.getTime(time)));
					sqlite.closeConnection();
				} catch (ClassNotFoundException | SQLException e) {
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
					return true;
				}
				return true;
			}
			if(args.length <= 1){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Dias_VIP").replace("&", "з"));
				return true;
			}
			if(!sender.hasPermission("fvip.diasvip.outros")){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "з"));
				return true;
			}
			Player p = instance.getServer().getPlayer(args[0]);
			if(p == null){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "з"));
				return true;
			}
			String grupo = null;
			for(String grp : instance.getConfig().getConfigurationSection("Config.Grupos_VIPs").getKeys(false)){
				if(grp.equalsIgnoreCase(args[1])){
					grupo = grp;
					break;
				}
			}
			if(grupo == null){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Grupo_VIP_Nao_Existe").replace("&", "з"));
				return true;
			}
			try {
				sqlite.openConnection();
				if(!sqlite.hasPlayerGrupo(p.getName(), grupo)){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Nao_Tem_VIP").replace("&", "з").replace("{player}", p.getName()));
					sqlite.closeConnection();
					return true;
				}
				long time = sqlite.getDias(p.getName(), grupo)-System.currentTimeMillis();
				sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Dias_Dias_Outro").replace("&", "з").replace("{vip}", grupo).replace("{player}", p.getName())
						.replace("{acaba}", instance.getTime(time)));
				sqlite.closeConnection();
			} catch (ClassNotFoundException | SQLException e) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
				return true;
			}
		}else if(cmd.getName().equalsIgnoreCase("gerarkey")){
			if(!sender.hasPermission("fvip.gerarkey")){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "з"));
				return true;
			}else if(args.length <= 1){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Gerar_Key").replace("&", "з"));
				return true;
			}else if(!isNum(args[0])){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Numero").replace("&", "з"));
				return true;
			}
			String grupo = null;
			for(String grp : instance.getConfig().getConfigurationSection("Config.Grupos_VIPs").getKeys(false)){
				if(grp.equalsIgnoreCase(args[1])){
					grupo = grp;
					break;
				}
			}
			if(grupo == null){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Grupo_VIP_Nao_Existe").replace("&", "з"));
				return true;
			}
			StringBuilder key = new StringBuilder();
			String caracteres = instance.getConfig().getString("Config.Caracteres_Key");
			if(args.length == 2){
				for(int i = 0; i<instance.getConfig().getInt("Config.Tamanho_Key"); i++){
					key.append(caracteres.charAt(r.nextInt(caracteres.length())));
				}
				try {
					sqlite.openConnection();
					sqlite.addNewKey(key.toString(), grupo, args[0]);
					sqlite.closeConnection();
				} catch (NumberFormatException | ClassNotFoundException | SQLException e) {
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
					return true;
				}
				sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Key_Gerada").replace("&", "з").replace("{key}", key));
			}else{
				if(!isNum(args[2])){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Numero").replace("&", "з"));
					return true;
				}
				if(Integer.parseInt(args[2]) < 1){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Menor_Que_Um").replace("&", "з"));
					return true;
				}
				StringBuilder keys = new StringBuilder();
				final String grp = grupo;
				new BukkitRunnable() {
					@Override
					public void run(){
						try {
							sqlite.openConnection();
							for(int x = 0; x<Integer.parseInt(args[2]); x++){
								for(int i = 0; i<instance.getConfig().getInt("Config.Tamanho_Key"); i++){
									key.append(caracteres.charAt(r.nextInt(caracteres.length())));
								}
								keys.append(key.toString() + ", ");
								if(!sqlite.hasKey(key.toString())){
									sqlite.addNewKey(key.toString(), grp, args[0]);
								}
								key.setLength(0);
							}
							sqlite.closeConnection();
							for(String msg : instance.getConfig().getStringList("Mensagem.Sucesso.Keys_Geradas")){
								sender.sendMessage(msg.replace("&", "з").replace("{qnt}", args[2]).replace("{keys}", keys.substring(0, keys.length()-2)));
							}
						} catch (NumberFormatException | ClassNotFoundException | SQLException e) {
							sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
							return;
						}
					}
				}.runTaskAsynchronously(instance);
			}
		}else if(cmd.getName().equalsIgnoreCase("removerkey")){
			if(!sender.hasPermission("fvip.removerkey")){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "з"));
				return true;
			}else if(args.length == 0){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Remover_Key").replace("&", "з"));
				return true;
			}
			try {
				int keys = 0;
				StringBuilder keysapagadas = new StringBuilder();
				sqlite.openConnection();
				if(args[0].contains(",")){
					for(String key : args[0].split(",")){
						if(sqlite.hasKey(key)){
							sqlite.delKey(key, false);
							keys++;
							keysapagadas.append(key).append(", ");
						}
					}
				}else{
					if(!args[0].equals("*")){
						if(!sqlite.hasKey(args[0])){
							sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Key_Nao_Existe").replace("&", "з").replace("{key}", args[0]));
							return true;
						}
					}
					sqlite.delKey(args[0], true);
				}
				sqlite.closeConnection();
				if(keys != 0){
					for(String msg : instance.getConfig().getStringList("Mensagem.Sucesso.Keys_Apagadas")){
						sender.sendMessage(msg.replace("&", "з").replace("{qnt}", keys + "")
								.replace("{keys}", keysapagadas.toString().substring(0, keysapagadas.toString().length()-2)));
					}
				}else{
					if(args[0].contains(",")){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nenhuma_Key_Apagada").replace("&", "з"));
					}else{
						if(args[0].equals("*")){
							sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Todas_As_Keys_Apagadas").replace("&", "з"));
						}else{
							sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Key_Apagada").replace("&", "з").replace("{key}", args[0]));
						}
					}
				}
			} catch (ClassNotFoundException | SQLException e) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
				return true;
			}
		}else if(cmd.getName().equalsIgnoreCase("keys")){
			if(!sender.hasPermission("fvip.keys")){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "з"));
				return true;
			}
			try {
				sqlite.openConnection();
				double versao = Double.parseDouble(instance.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1, 4).replace("_", "."));
				if(versao > 1.7 && (sender instanceof Player)){
					sqlite.getKeys1_7((Player) sender);
				}else{
					String keys = sqlite.getKeys();
					sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Keys").replace("&", "з")
							.replace("{keys}", keys.length() == 0 ? "зcnenhumaзr" : keys));
				}
				sqlite.closeConnection();
			} catch (ClassNotFoundException | SQLException e) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
				return true;
			}
		}else if(cmd.getName().equalsIgnoreCase("keyinfo")){
			if(!sender.hasPermission("fvip.keyinfo")){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "з"));
				return true;
			}else if(args.length == 0){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Key_Info").replace("&", "з"));
				return true;
			}
			try {
				sqlite.openConnection();
				if(!sqlite.hasKey(args[0])){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Key_Nao_Existe").replace("&", "з").replace("{key}", args[0]));
					return true;
				}
				sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Key_Info").replace("&", "з")
						.replace("{key}", args[0]).replace("{info}", sqlite.getInfoKey(args[0])));
				sqlite.closeConnection();
			} catch (ClassNotFoundException | SQLException e) {
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
				return true;
			}
		}else if(cmd.getName().equalsIgnoreCase("usarkey")){
			if(args.length == 0){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Usar_Key").replace("&", "з"));
				return true;
			}
			if(!(sender instanceof Player)){
				sender.sendMessage("з4Comando apenas para players!");
				return true;
			}
			Player p = (Player) sender;
			try {
				sqlite.openConnection();
				if(!sqlite.hasKey(args[0])){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Key_Nao_Existe").replace("&", "з").replace("{key}", args[0]));
					sqlite.closeConnection();
					return true;
				}
				int dia = sqlite.getDiaKey(args[0]);
				String grupo = sqlite.getKeyGrupo(args[0]);
				boolean hasPlayerGroup = sqlite.hasPlayerGrupo(p.getName(), grupo);
				String tempo = "";
				if(hasPlayerGroup)
					tempo = (TimeUnit.DAYS.toMillis(dia) + sqlite.getDias(p.getName(), grupo)) + "";
				else
					tempo = (System.currentTimeMillis()+TimeUnit.DAYS.toMillis(dia)) + "";
				PlayerActiveVipEvent event = new PlayerActiveVipEvent(p, ActivationType.KEY, args[0], grupo, new Date(Long.parseLong(tempo)));
				instance.getServer().getPluginManager().callEvent(event);
				if(!event.isCancelled()){
					if(hasPlayerGroup){
						sqlite.renew(p.getName(), tempo, grupo);
					}else{
						sqlite.addNew(p.getName(), tempo, grupo);
						if(sqlite.getPlayerGroupUsando(p.getName()) == null)
							sqlite.setUsando(p.getName(), grupo, true);
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Usou_Key").replace("{grupo}", grupo)
							.replace("{player}", p.getName()).replace("{tempo}", sdf.format(new Date(Long.parseLong(tempo))))
							.replace("&", "з"));
					for(String s : instance.getConfig().getStringList("Config.Comandos_Executados"))
						instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), s.replace("{player}", p.getName()).replace("{grupo}", grupo));
					for(String items : instance.getConfig().getStringList("Config.Items")){
						String[] grupos = items.split(" ")[0].split("grupo:")[1].split(" ")[0].split(",");
						for(String gr : grupos){
							if(gr.equalsIgnoreCase(grupo)){
								if(items.contains("xp:")){
									p.giveExpLevels(Integer.parseInt(items.split("xp:")[1]));
								}else if(items.contains("cmd:")){
									instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), items.split("cmd:")[1].replace("{player}", p.getName()));
								}else{
									ItemStack item = (ItemStack) instance.criarItem(items);
									if(p.getInventory().firstEmpty() != -1){
										p.getInventory().addItem(item == null ? new ItemStack(Material.AIR) : item);
									}else{
										p.getWorld().dropItemNaturally(p.getLocation(), item == null ? new ItemStack(Material.AIR) : item);
									}
								}
								break;
							}
						}
					}
					for(String msg : instance.getConfig().getStringList("Mensagem.Sucesso.Ativou_VIP")){
						for(Player on : instance.getServer().getOnlinePlayers()){
							on.sendMessage(msg.replace("&", "з").replace("{player}", p.getName()).replace("{grupo}", grupo)
									.replace("{tempo}", sdf.format(new Date(Long.parseLong(tempo)))));
						}
					}
					sqlite.delKey(args[0], false);
					sqlite.closeConnection();
					if(instance.getServer().getClass().getPackage().getName().split("\\.")[3].contains("v1_8_R3")){
						for(String grps : instance.getConfig().getConfigurationSection("Config.Grupos_VIPs").getKeys(false)){
							if(grps.equalsIgnoreCase(grupo)){
								if(instance.getConfig().getBoolean("Config.Grupos_VIPs." + grps + ".Ativar_VIP.Ativar_Title")){
									TitleBuilder.sendTitle(instance.getConfig().getInt("Config.Grupos_VIPs." + grps + ".Ativar_VIP.FadeIn")*20, 
											instance.getConfig().getInt("Config.Grupos_VIPs." + grps + ".Ativar_VIP.Stay")*20, instance.getConfig().getInt("Config.Grupos_VIPs." + grps + ".Ativar_VIP.FadeOut")*20,
											instance.getConfig().getString("Config.Grupos_VIPs." + grps + ".Ativar_VIP.Title").replace("{player}", p.getName()).replace("{vip}", grupo)
											.replace("{tempo}", sdf.format(new Date(Long.parseLong(tempo)))), null, instance.getServer().getOnlinePlayers());
								}
								if(instance.getConfig().getBoolean("Config.Grupos_VIPs." + grps + ".Ativar_VIP.Ativar_Subtitle")){
									TitleBuilder.sendTitle(instance.getConfig().getInt("Config.Grupos_VIPs." + grps + ".Ativar_VIP.FadeIn")*20, 
											instance.getConfig().getInt("Config.Grupos_VIPs." + grps + ".Ativar_VIP.Stay")*20, instance.getConfig().getInt("Config.Grupos_VIPs." + grps + ".Ativar_VIP.FadeOut")*20,
											null, instance.getConfig().getString("Config.Grupos_VIPs." + grps + ".Ativar_VIP.SubTitle").replace("{player}", p.getName()).replace("{vip}", grupo)
											.replace("{tempo}", sdf.format(new Date(Long.parseLong(tempo)))), instance.getServer().getOnlinePlayers());
								}
								break;
							}
						}
					}
					if(instance.getCabecas().contains("Cabecas") && instance.getConfig().getBoolean("Config.Mural_Cabecas.Usar_Key")){
						List<String> cabecas = instance.getCabecas().getStringList("Cabecas");
						final String time = tempo;
						final String group = grupo;
						new BukkitRunnable() {
							@Override
							public void run() {
								for(int i = cabecas.size()-1; i>=0; i--){
									Location loc = deserializeLocation(cabecas.get(i));
									if(loc != null){
										if(i == 0){
											String[] partes = cabecas.get(0).split(" ");
											loc.getBlock().setType(Material.SKULL);
											BlockFace face = BlockFace.valueOf(partes[4]);
											if((!loc.getBlock().getRelative(BlockFace.valueOf(cabecas.get(0).split(" ")[4]).getOppositeFace()).getLocation().getBlock().isLiquid())
													&& (!loc.getBlock().getRelative(BlockFace.valueOf(cabecas.get(0).split(" ")[4]).getOppositeFace()).getLocation().getBlock().isEmpty())){
												switch(face){
												case SOUTH:
													loc.getBlock().setData((byte) 0x3);
													break;
												case WEST:
													loc.getBlock().setData((byte) 0x4);
													break;
												case NORTH:
													loc.getBlock().setData((byte) 0x2);
													break;
												case EAST:
													loc.getBlock().setData((byte) 0x5);
													break;
												default:
													loc.getBlock().setData((byte) 0x0);
													break;
												}
											}else{
												loc.getBlock().setData((byte) 0x1);
											}
											Skull skull = (Skull) loc.getBlock().getState();
											skull.setSkullType(SkullType.PLAYER);
											skull.setOwner(p.getName());
											skull.setRotation(face);
											skull.update();
											//Block relative = skull.getBlock().getRelative(skull.getRotation()).getLocation().subtract(0, 1, 0).getBlock();
											Block relative = new Location(instance.getServer().getWorld(partes[0]), Double.parseDouble(partes[5]), 
													Double.parseDouble(partes[6]), Double.parseDouble(partes[7])).getBlock();
											relative.setType(Material.WALL_SIGN);
											Sign sign = (Sign) relative.getState();
											org.bukkit.material.Sign s = new org.bukkit.material.Sign(Material.WALL_SIGN);
											s.setFacingDirection(skull.getRotation());
											sign.setData(s);
											List<String> placa = instance.getConfig().getStringList("Config.Placa_Cabeca");
											for(int j = 0; j<placa.size(); j++){
												if(j > 3) break;
												sign.setLine(j, placa.get(j).replace("&", "з").replace("{player}", p.getName())
														.replace("{tempo}", new SimpleDateFormat(instance.getConfig().getString("Config.Placa_Cabeca_Tempo_Format")).format(new Date(Long.parseLong(time)))).replace("{vip}", group)
														.replace("{ativado}", new SimpleDateFormat(instance.getConfig().getString("Config.Placa_Cabeca_Ativado_Format")).format(Calendar.getInstance().getTime())));
											}
											sign.update();
										}else{
											if(i-1 >= 0) loc = deserializeLocation(cabecas.get(i-1));
											if(i+1 > cabecas.size() || loc.getBlock().getType() != Material.SKULL) continue;
											Location loc2 = deserializeLocation(cabecas.get(i));
											loc2.getBlock().setType(Material.SKULL);
											BlockFace face = BlockFace.valueOf(cabecas.get(i).split(" ")[4]);
											if((!loc2.getBlock().getRelative(BlockFace.valueOf(cabecas.get(i).split(" ")[4]).getOppositeFace()).getLocation().getBlock().isLiquid())
													&& (!loc2.getBlock().getRelative(BlockFace.valueOf(cabecas.get(i).split(" ")[4]).getOppositeFace()).getLocation().getBlock().isEmpty())){
												switch(face){
												case SOUTH:
													loc2.getBlock().setData((byte) 0x3);
													break;
												case WEST:
													loc2.getBlock().setData((byte) 0x4);
													break;
												case NORTH:
													loc2.getBlock().setData((byte) 0x2);
													break;
												case EAST:
													loc2.getBlock().setData((byte) 0x5);
													break;
												default:
													loc2.getBlock().setData((byte) 0x0);
													break;
												}
											}else{
												loc2.getBlock().setData((byte) 0x1);
											}
											Skull skull = (Skull) loc2.getBlock().getState();
											Skull skull2 = ((Skull) loc.getBlock().getState());
											if(skull2.hasOwner()){
												String[] partes = cabecas.get(i/*+1 >= cabecas.size() ? i : i+1*/).split(" ");
												String[] partes2 = cabecas.get(i-1).split(" ");
												skull.setSkullType(SkullType.PLAYER);
												skull.setOwner(skull2.getOwner());
												skull.setRotation(BlockFace.valueOf(partes[4]));
												skull.update();
												//Block relative = skull.getBlock().getRelative(skull.getRotation()).getLocation().subtract(0, 1, 0).getBlock();
												Block relative = new Location(instance.getServer().getWorld(partes[0]), Double.parseDouble(partes[5]), 
														Double.parseDouble(partes[6]), Double.parseDouble(partes[7])).getBlock();
												relative.setType(Material.WALL_SIGN);
												Sign sign = (Sign) relative.getState();
												Sign sign2 = (Sign) new Location(instance.getServer().getWorld(partes2[0]), Double.parseDouble(partes2[5]), 
														Double.parseDouble(partes2[6]), Double.parseDouble(partes2[7])).getBlock().getState();
												org.bukkit.material.Sign s = new org.bukkit.material.Sign(Material.WALL_SIGN);
												s.setFacingDirection(skull.getRotation());
												sign.setData(s);
												for(int j = 0; j<4; j++){
													sign.setLine(j, sign2.getLine(j));
												}
												sign.update();
											}
										}
									}
								}
							}
						}.runTask(instance);
					}
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Cancelado_Plugin_Externo").replace("&", "з"));
				}
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
				return true;
			}
		}else if(cmd.getName().equalsIgnoreCase("ativarvip")){
			if(args.length == 0){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Ativar_VIP").replace("&", "з"));
				return true;
			}
			if(!(sender instanceof Player)){
				sender.sendMessage("з4Comando apenas para players!");
				return true;
			}
			Player p = (Player) sender;
			if(!isVazio(p)){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inv_Tem_Item").replace("&", "з"));
				return true;
			}else if(instance.getTempo().containsKey(p.getName().toLowerCase()) && instance.getTempo().get(p.getName().toLowerCase())>System.currentTimeMillis()){
				long tempo = instance.getTempo().get(p.getName().toLowerCase()) - System.currentTimeMillis();
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde_Ativar_VIP").replace("&", "з")
						.replace("{duracao}", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(tempo) - (TimeUnit.MILLISECONDS.toMinutes(tempo) * 60))));
				return true;
			}
			String key = args[0];
			if(key.matches("^(([A-Z0-9]{17})|(\\d{10})|([A-Z0-9]{8}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{12}))$")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Verificando_Codigo").replace("&", "з"));
				if(key.matches("^([A-Z0-9]{8}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{12})$")){
					if(!instance.getConfig().getBoolean("Config.PagSeguro.Ativar")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.PagSeguro_Desativado").replace("&", "з"));
						return true;
					}
					try {
						sqlite.openConnection();
						if(sqlite.hasCodigo(args[0].toUpperCase())){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Codigo_Ja_Usado").replace("&", "з")
									.replace("{player}", sqlite.getPlayerCodigo(args[0])).replace("{dia}", sdf.format(sqlite.getDiaCodigo(args[0]))));
							sqlite.closeConnection();
							return true;
						}
						sqlite.closeConnection();
					} catch (ClassNotFoundException | SQLException e) {
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
						return true;
					}
					instance.getTempo().put(p.getName().toLowerCase(), System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(1));
					PagSeguro ps = new PagSeguro(instance, args[0], p);
					ps.start();
				}else if(key.matches("^([A-Z0-9]{17})$")){
					if(!instance.getConfig().getBoolean("Config.PayPal.Ativar")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.PayPal_Desativado").replace("&", "з"));
						return true;
					}
					try {
						sqlite.openConnection();
						if(sqlite.hasCodigo(args[0].toUpperCase())){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Codigo_Ja_Usado").replace("&", "з")
									.replace("{player}", sqlite.getPlayerCodigo(args[0])).replace("{dia}", sdf.format(sqlite.getDiaCodigo(args[0]))));
							sqlite.closeConnection();
							return true;
						}
						sqlite.closeConnection();
					} catch (ClassNotFoundException | SQLException e) {
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
						return true;
					}
					instance.getTempo().put(p.getName().toLowerCase(), System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(1));
					PayPal paypal = new PayPal(instance, args[0], p);
					paypal.start();
				}else{
					if(!instance.getConfig().getBoolean("Config.MercadoPago.Ativar")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.MercadoPago_Desativado").replace("&", "з"));
						return true;
					}
					try {
						sqlite.openConnection();
						if(sqlite.hasCodigo(args[0].toUpperCase())){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Codigo_Ja_Usado").replace("&", "з")
									.replace("{player}", sqlite.getPlayerCodigo(args[0])).replace("{dia}", sdf.format(sqlite.getDiaCodigo(args[0]))));
							sqlite.closeConnection();
							return true;
						}
						sqlite.closeConnection();
					} catch (ClassNotFoundException | SQLException e) {
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
						return true;
					}
					instance.getTempo().put(p.getName().toLowerCase(), System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(1));
					MercadoPago mp = new MercadoPago(instance, args[0], p);
					mp.start();
				}
			}else{
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Codigo_Informado_Invalido").replace("&", "з"));
			}
		}else if(cmd.getName().equalsIgnoreCase("trocarvip")){
			if(args.length == 0){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Trocar_VIP").replace("&", "з"));
				return true;
			}
			if(!(sender instanceof Player)){
				sender.sendMessage("з4Comando apenas para players!");
				return true;
			}
			Player p = (Player) sender;
			String grupo = null;
			for(String grp : instance.getConfig().getConfigurationSection("Config.Grupos_VIPs").getKeys(false)){
				if(grp.equalsIgnoreCase(args[0])){
					grupo = grp;
					break;
				}
			}
			if(grupo == null){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Grupo_VIP_Nao_Existe").replace("&", "з"));
				return true;
			}
			try {
				sqlite.openConnection();
				if(!sqlite.hasPlayerGrupo(p.getName(), grupo)){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Tem_VIP").replace("&", "з").replace("{player}", p.getName()));
					sqlite.closeConnection();
					return true;
				}else if(!sqlite.hasPlayer(p.getName())){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Eh_VIP").replace("&", "з").replace("{vip}", grupo).replace("{player}", p.getName()));
					sqlite.closeConnection();
					return true;
				}
				String grp = sqlite.getPlayerGroupUsando(p.getName());
				if(sqlite.getPlayerGroupUsando(p.getName()) != null && grp.equalsIgnoreCase(grupo)){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta_No_Grupo").replace("&", "з").replace("{vip}", grupo).replace("{player}", p.getName()));
					sqlite.closeConnection();
					return true;
				}
				sqlite.changePlayerGroup(p.getName(), grupo);
				sqlite.closeConnection();
				for(String s : instance.getConfig().getStringList("Config.Comandos_Executados"))
					instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), s.replace("{player}", p.getName()).replace("{grupo}", grupo));
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Grupo_Alterado").replace("&", "з").replace("{vip}", grupo));
			} catch (ClassNotFoundException | SQLException e) {
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
				return true;
			}
		}else if(cmd.getName().equalsIgnoreCase("setcabeca")){
			if(!(sender instanceof Player)){
				sender.sendMessage("з4Comando apenas para players!");
				return true;
			}
			Player p = (Player) sender;
			if(!p.hasPermission("fvip.setcabeca")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "з"));
				return true;
			}else if(!new File(instance.getDataFolder(), "cabecas.yml").exists()){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Cabecas_Nao_Existe").replace("&", "з"));
				return true;
			}
			Block b = p.getTargetBlock((HashSet<Byte>) null, 5);
			if(b == null || b.isLiquid() || b.getType() == Material.AIR || b.getType() == Material.LAVA){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Olhe_Para_Um_Bloco").replace("&", "з"));
				return true;
			}
			List<String> cabecas = null;
			if(instance.getCabecas().contains("Cabecas")){
				for(String s : instance.getCabecas().getStringList("Cabecas")){
					if(s.contains(serializeLocation(b.getLocation(), getDirection(p).name()))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Cabeca_Ja_Setada").replace("&", "з"));
						return true;
					}
				}
				cabecas = instance.getCabecas().getStringList("Cabecas");
			}
			if(cabecas != null) cabecas.add(serializeLocation(b.getLocation(), getDirection(p).name()));
			instance.getCabecas().set("Cabecas", cabecas == null ? Arrays.asList(serializeLocation(b.getLocation(), getDirection(p).name())) : cabecas);
			try {
				instance.getCabecas().save(new File(instance.getDataFolder(), "cabecas.yml"));
				instance.getCabecas().load(new File(instance.getDataFolder(), "cabecas.yml"));
			} catch (IOException | InvalidConfigurationException e) {
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
				return true;
			}
			p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Cabeca_Setada").replace("&", "з"));
		}else if(cmd.getName().equalsIgnoreCase("setsign")){
			if(!(sender instanceof Player)){
				sender.sendMessage("з4Comando apenas para players!");
				return true;
			}
			Player p = (Player) sender;
			if(!p.hasPermission("fvip.setcabeca")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "з"));
				return true;
			}else if(!new File(instance.getDataFolder(), "cabecas.yml").exists()){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Cabecas_Nao_Existe").replace("&", "з"));
				return true;
			}
			Block b = p.getTargetBlock((HashSet<Byte>) null, 5);
			if(b == null || b.isLiquid() || b.getType() == Material.AIR || b.getType() == Material.LAVA){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Olhe_Para_Um_Bloco").replace("&", "з"));
				return true;
			}
			List<String> cabecas = null;
			if(instance.getCabecas().contains("Cabecas")){
				for(String s : instance.getCabecas().getStringList("Cabecas")){
					if(s.contains(b.getLocation().getX() + " " + b.getLocation().getY() + " " + b.getLocation().getZ())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Placa_Ja_Setada").replace("&", "з"));
						return true;
					}
				}
				cabecas = instance.getCabecas().getStringList("Cabecas");
			}
			if(args.length == 0){
				if(cabecas != null){
					if(cabecas.get(cabecas.size()-1).split(" ").length == 8){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Precisa_Seta_Placa").replace("&", "з"));
						return true;
					}
					cabecas.set(cabecas.size()-1, cabecas.get(cabecas.size()-1) + " " + b.getLocation().getX() + " " + b.getLocation().getY() + " " + b.getLocation().getZ());
				}
				instance.getCabecas().set("Cabecas", cabecas == null ? Arrays.asList(serializeLocation(b.getLocation(), getDirection(p).name())
						+ " " + b.getLocation().getX() + " " + b.getLocation().getY() + " " + b.getLocation().getZ()) : cabecas);
				try {
					instance.getCabecas().save(new File(instance.getDataFolder(), "cabecas.yml"));
					instance.getCabecas().load(new File(instance.getDataFolder(), "cabecas.yml"));
				} catch (IOException | InvalidConfigurationException e) {
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
					return true;
				}
			}else{
				if(!isNum(args[0])){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Numero").replace("&", "з"));
					return true;
				}
				int posicao = Integer.parseInt(args[0])-1;
				if(cabecas != null){
					if(posicao < 0 || posicao >= cabecas.size()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Posicao_Invalida").replace("&", "з").replace("{max}", cabecas.size() + ""));
						return true;
					}
					String[] partes = cabecas.get(posicao).split(" ");
					cabecas.set(posicao, partes[0] + " " + partes[1] + " " + partes[2] + " " + partes[3] + " " + partes[4] + " " + 
							b.getLocation().getX() + " " + b.getLocation().getY() + " " + b.getLocation().getZ());
				}
				instance.getCabecas().set("Cabecas", cabecas == null ? Arrays.asList(serializeLocation(b.getLocation(), getDirection(p).name())
						+ " " + b.getLocation().getX() + " " + b.getLocation().getY() + " " + b.getLocation().getZ()) : cabecas);
				try {
					instance.getCabecas().save(new File(instance.getDataFolder(), "cabecas.yml"));
					instance.getCabecas().load(new File(instance.getDataFolder(), "cabecas.yml"));
				} catch (IOException | InvalidConfigurationException e) {
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
					return true;
				}
			}
			p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Placa_Setada").replace("&", "з"));
		}else if(cmd.getName().equalsIgnoreCase("venderkey")){
			if(!(sender instanceof Player)){
				sender.sendMessage("з4Comando apenas para players!");
				return true;
			}
			Player p = (Player) sender;
			if(args.length <= 1){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Vender_Key").replace("&", "з"));
				return true;
			}else if(!isNum(args[1])){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Preco_Invalido").replace("&", "з"));
				return true;
			}else if(instance.getVendakey().containsKey(p.getName())){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde_Vender_Key").replace("&", "з")
						.replace("{tempo}", "" + TimeUnit.MILLISECONDS.toSeconds(instance.getVendakey().get(p.getName()) - System.currentTimeMillis())));
				return true;
			}
			try{
				sqlite.openConnection();
				if(!sqlite.hasKey(args[0])){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Key_Nao_Existe").replace("&", "з").replace("{key}", args[0]));
					return true;
				}
				for(String vk : instance.getTasks().keySet()){
					VendaKey vendakey = instance.getTasks().get(vk);
					if(vendakey.getKey().equalsIgnoreCase(args[0])){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Key_Ja_Sendo_Vendida").replace("{player}", vendakey.getVendedor()).replace("&", "з"));
						return true;
					}else if(vendakey.getVendedor().equalsIgnoreCase(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Esta_Vendendo").replace("&", "з"));
						return true;
					}
				}
				String key = args[0];
				double preco = Double.parseDouble(args[1]);
				if(preco < instance.getConfig().getDouble("Config.VendaKey.Preco_Minimo") || preco > instance.getConfig().getDouble("Config.VendaKey.Preco_Maximo")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Preco_Alto_Ou_Baixo").replace("&", "з"));
					return true;
				}
				VendaKey vendakey = new VendaKey(instance, key, preco, p.getName(), sqlite.getKeyGrupo(key), sqlite.getKeyTempo(key));
				vendakey.runTaskTimer(instance, 0, 20*instance.getConfig().getInt("Config.VendaKey.Tempo_Anuncios"));
				instance.getTasks().put(p.getName(), vendakey);
				instance.getVendakey().remove(p.getName());
				sqlite.closeConnection();
			}catch(Exception e){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
				return true;
			}
		}else if(cmd.getName().equalsIgnoreCase("comprarkey")){
			if(!(sender instanceof Player)){
				sender.sendMessage("з4Comando apenas para players!");
				return true;
			}
			Player p = (Player) sender;
			if(args.length == 0){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Informe_O_Player").replace("&", "з"));
				return true;
			}
			Player vendedor = instance.getServer().getPlayer(args[0]);
			if(vendedor == null){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "з"));
				return true;
			}else if(vendedor.getName().equalsIgnoreCase(p.getName())){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Si_Mesmo").replace("&", "з"));
				return true;
			}
			for(String vk : instance.getTasks().keySet()){
				VendaKey vendakey = instance.getTasks().get(vk);
				if(vendakey.getVendedor().equals(vendedor.getName())){
					if(!instance.econ.has(p.getName(), vendakey.getPreco())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Dinheiro_Suficiente").replace("&", "з")
								.replace("{preco}", NumberFormat.getCurrencyInstance().format(vendakey.getPreco()).replace("$", "")));
						return true;
					}
					instance.econ.withdrawPlayer(p.getName(), vendakey.getPreco());
					instance.econ.depositPlayer(vendedor.getName(), vendakey.getPreco());
					instance.getTasks().get(vendedor).cancel();
					instance.getTasks().remove(vendedor);
					for(Player on : instance.getServer().getOnlinePlayers()){
						for(String msg : instance.getConfig().getStringList("Mensagem.VendaKey.Comprou")){
							if(msg.contains("{key}")){
								on.sendMessage(msg.replace("{player}", p.getName()).replace("{vendedor}", vendedor.getName())
										.replace("{preco}", NumberFormat.getCurrencyInstance().format(vendakey.getPreco()).replace("$", ""))
										.replace("{grupo}", vendakey.getGrupo()).replace("{tempokey}", vendakey.getTempo()).replace("{key}", vendakey.getKey()).replace("&", "з"));
							}else{
								on.sendMessage(msg.replace("{player}", p.getName()).replace("{vendedor}", vendedor.getName())
										.replace("{preco}", NumberFormat.getCurrencyInstance().format(vendakey.getPreco()).replace("$", ""))
										.replace("{grupo}", vendakey.getGrupo()).replace("{tempokey}", vendakey.getTempo()).replace("&", "з"));
							}
						}
					}
					instance.getVendakey().put(vendedor.getName(), System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.VendaKey.Delay")));
					new BukkitRunnable() {
						@Override
						public void run() {
							instance.getVendakey().remove(vendedor.getName());
						}
					}.runTaskLater(instance, instance.getConfig().getInt("Config.VendaKey.Delay")*20);
					try{
						if(instance.getConfig().getBoolean("Config.VendaKey.Som_Ao_Comprar_Key"))
							vendedor.playSound(vendedor.getLocation(), Sound.valueOf(instance.getConfig().getString("Config.VendaKey.Som")), 5.0F, 1.0F);
					}catch(Exception e){}
					return true;
				}
			}
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Vendendo").replace("&", "з"));
			return true;
		}else if(cmd.getName().equalsIgnoreCase("cancelarvenda")){
			if(!(sender instanceof Player)){
				sender.sendMessage("з4Comando apenas para players!");
				return true;
			}
			Player p = (Player) sender;
			if(args.length == 0){
				for(String vk : instance.getTasks().keySet()){
					VendaKey vendakey = instance.getTasks().get(vk);
					if(vendakey.getVendedor().equalsIgnoreCase(p.getName())){
						instance.getTasks().get(p.getName()).cancel();
						instance.getTasks().remove(p.getName());
						for(Player on : instance.getServer().getOnlinePlayers()){
							for(String msg : instance.getConfig().getStringList("Mensagem.VendaKey.Cancelou")){
								on.sendMessage(msg.replace("{vendedor}", p.getName())
										.replace("{preco}", NumberFormat.getCurrencyInstance().format(vendakey.getPreco()).replace("$", ""))
										.replace("{grupo}", vendakey.getGrupo()).replace("{tempokey}", vendakey.getTempo()).replace("&", "з"));
							}
						}
						instance.getVendakey().put(p.getName(), System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.VendaKey.Delay")));
						new BukkitRunnable() {
							@Override
							public void run() {
								instance.getVendakey().remove(p.getName());
							}
						}.runTaskLater(instance, instance.getConfig().getInt("Config.VendaKey.Delay")*20);
						return true;
					}
				}
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Voce_Nao_Esta_Vendendo").replace("&", "з"));
				return true;
			}
			if(!p.hasPermission("fvip.vendakey")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "з"));
				return true;
			}
			Player vendedor = instance.getServer().getPlayer(args[0]);
			if(vendedor == null){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "з"));
				return true;
			}
			for(String vk : instance.getTasks().keySet()){
				VendaKey vendakey = instance.getTasks().get(vk);
				if(vendakey.getVendedor().equals(vendedor.getName())){
					instance.getTasks().get(vendedor.getName()).cancel();
					instance.getTasks().remove(vendedor.getName());
					for(Player on : instance.getServer().getOnlinePlayers()){
						for(String msg : instance.getConfig().getStringList("Mensagem.VendaKey.Admin_Cancelou")){
							on.sendMessage(msg.replace("{admin}", p.getName()).replace("{vendedor}", vendedor.getName())
									.replace("{preco}", NumberFormat.getCurrencyInstance().format(vendakey.getPreco()).replace("$", ""))
									.replace("{grupo}", vendakey.getGrupo()).replace("{tempokey}", vendakey.getTempo()).replace("&", "з"));
						}
					}
					instance.getVendakey().put(vendedor.getName(), System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.VendaKey.Delay")));
					new BukkitRunnable() {
						@Override
						public void run() {
							instance.getVendakey().remove(vendedor.getName());
						}
					}.runTaskLater(instance, instance.getConfig().getInt("Config.VendaKey.Delay")*20);
					try{
						if(instance.getConfig().getBoolean("Config.VendaKey.Som_Ao_Admin_Cancelar_Key"))
							vendedor.playSound(vendedor.getLocation(), Sound.valueOf(instance.getConfig().getString("Config.VendaKey.Som_Cancelar")), 5.0F, 1.0F);
					}catch(Exception e){}
					return true;
				}
			}
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Vendendo").replace("&", "з"));
			return true;
		}else if(cmd.getName().equalsIgnoreCase("desbanirvip")){
			if(!(sender instanceof Player)){
				sender.sendMessage("з4Comando apenas para players!");
				return true;
			}
			Player p = (Player) sender;
			if(!instance.getConfig().getBoolean("Config.Habilitar_Comando_DesbanirVIP")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.DesbanirVIP_Desabilitado").replace("&", "з"));
				return true;
			}else if(args.length == 0){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.DesbanirVIP").replace("&", "з"));
				return true;
			}
			Player player = instance.getServer().getOfflinePlayer(args[0]).getPlayer();
			if(player == null){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Nao_Existe").replace("&", "з"));
				return true;
			}else if(!player.isBanned()){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Nao_Banido").replace("&", "з"));
				return true;
			}
			try {
				sqlite.openConnection();
				if(sqlite.getPlayerGroupUsando(p.getName()) == null){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Grupo_Desbanir").replace("&", "з"));
					return true;
				}else if(sqlite.getUnbans(p.getName()) == 0){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Pode_Desbanir").replace("&", "з"));
					return true;
				}
				for(String comando : instance.getConfig().getStringList("Config.Comandos_Desbanir")){
					instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), comando.replace("{player}", player.getName()));
				}
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Desbaniu").replace("{player}", player.getName()).replace("&", "з"));
				String groupusando = sqlite.getPlayerGroupUsando(p.getName());
				sqlite.desbanir(p.getName(), groupusando);
				sqlite.closeConnection();
			} catch (Exception e) {
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "з"));
				return true;
			}
		}
		return false;
	}

	public boolean isNum(String num){
		try{
			Integer.parseInt(num);
			return true;
		}catch(Exception e){return false;}
	}

	public boolean isVazio(Player p){
		for(ItemStack item : p.getInventory().getContents())
			if(item != null && item.getType() != Material.AIR) return false;
		for(ItemStack item : p.getInventory().getArmorContents())
			if(item != null && item.getType() != Material.AIR) return false;
		return true;
	}

	public String serializeLocation(Location loc, String look){
		return loc.getWorld().getName() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " " + look;
	}

	public Location deserializeLocation(String loc){
		String[] partes = loc.split(" ");
		if((!loc.contains(" ")) || partes.length != 8 || instance.getServer().getWorld(partes[0]) == null) return null;
		try{
			return new Location(instance.getServer().getWorld(partes[0]), Double.parseDouble(partes[1]), Double.parseDouble(partes[2]), Double.parseDouble(partes[3]));
		}catch(Exception e){return null;}
	}

	// Retorna o valor invertido a direчуo
	// NORTH щ SOUTH, EAST щ WEST
	public BlockFace getDirection(Player player){
		float yaw = player.getLocation().getYaw();
		if (yaw < 0) yaw += 360;
		if (yaw >= 315 || yaw < 45){
			return BlockFace.NORTH;
		}else if(yaw < 135){
			return BlockFace.EAST;
		}else if(yaw < 225){
			return BlockFace.SOUTH;
		}else if(yaw < 315){
			return BlockFace.WEST;
		}
		return BlockFace.SOUTH;
	}

}

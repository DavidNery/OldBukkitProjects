package me.zfork.fterrenos;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Comandos implements CommandExecutor{
	
	static FTerrenos instance = FTerrenos.getFTerrenos();
	static TerrenoManager tm = instance.getTerrenoManager();
	static Random r = new Random();
	static HashMap<String, Long> terrenosdelay = new HashMap<String, Long>();
	
	public boolean IsNum(String num){
		try{
			Integer.parseInt(num);
			return true;
		}catch(NumberFormatException e){}
		return false;
	}
	
	@SuppressWarnings("static-access")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("terreno")){
			if(!(sender instanceof Player)){
				sender.sendMessage("Unknown command. Type \"help\" for help.");
				return true;
			}
			Player p = (Player) sender;
			if(args.length == 0){
				sendMessageList(p, instance.getConfig().getStringList("Mensagem.Comandos"));
				return true;
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.Comprar"))){
				if(instance.getConfig().getStringList("Config.Mundos_Possiveis").contains(p.getWorld().getName())){
					if(args.length < 3){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_Comprar").replace("&", "§"));
						return true;
					}else if(!args[2].equalsIgnoreCase("pequeno") && !args[2].equalsIgnoreCase("medio") && !args[2].equalsIgnoreCase("grande") && !IsNum(args[2])){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Somente_Numeros").replace("&", "§"));
						return true;
					}else if(!args[2].equalsIgnoreCase("pequeno") && !args[2].equalsIgnoreCase("medio") && !args[2].equalsIgnoreCase("grande") && Integer.parseInt(args[2]) % 2 != 0){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Par").replace("&", "§"));
						return true;
					}else if(IsNum(args[2]) && (Integer.parseInt(args[2]) < instance.getConfig().getInt("Config.Tamanho_Min") || Integer.parseInt(args[2]) > instance.getConfig().getInt("Config.Tamanho_Max"))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Tamanho_Invalido").replace("&", "§"));
						return true;
					}else if(args[1].length() > 8){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Tamanho_Maximo_Nome").replace("&", "§"));
						return true;
					}
					if(tm.GetAreas(p) < instance.getConfig().getInt("Config.Grupos." + instance.perm.getPrimaryGroup(p))){
						String area = args[1];
						if(args[2].equalsIgnoreCase("pequeno")){
							if(p.hasPermission("fterrenos.player") || p.hasPermission("fterrenos.comprar.pequeno") || p.hasPermission("fterrenos.comprar.*")){
								if(instance.econ.has(p, instance.getConfig().getInt("Config.Preco_Bloco") * instance.getConfig().getInt("Config.Tamanho_Terreno_Pequeno"))){
									tm.Comprar(p, area, instance.getConfig().getInt("Config.Tamanho_Terreno_Pequeno"));
								}else{
									p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Money_Comprar").replace("&", "§"));
								}
							}else{
								p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao_Pequeno").replace("&", "§"));
								return true;
							}
						}else if(args[2].equalsIgnoreCase("medio")){
							if(p.hasPermission("fterrenos.player") || p.hasPermission("fterrenos.comprar.medio") || p.hasPermission("fterrenos.comprar.*")){
								if(instance.econ.has(p, instance.getConfig().getInt("Config.Preco_Bloco") * instance.getConfig().getInt("Config.Tamanho_Terreno_Medio"))){
									tm.Comprar(p, area, instance.getConfig().getInt("Config.Tamanho_Terreno_Medio"));
								}else{
									p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Money_Comprar").replace("&", "§"));
								}
							}else{
								p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao_Medio").replace("&", "§"));
								return true;
							}
						}else if(args[2].equalsIgnoreCase("grande")){
							if(p.hasPermission("fterrenos.player") || p.hasPermission("fterrenos.comprar.grande") || p.hasPermission("fterrenos.comprar.*")){
								if(instance.econ.has(p, instance.getConfig().getInt("Config.Preco_Bloco") * instance.getConfig().getInt("Config.Tamanho_Terreno_Grande"))){
									tm.Comprar(p, area, instance.getConfig().getInt("Config.Tamanho_Terreno_Grande"));
								}else{
									p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Money_Comprar").replace("&", "§"));
								}
							}else{
								p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao_Grande").replace("&", "§"));
								return true;
							}
						}else{
							if(p.hasPermission("fterrenos.player") || p.hasPermission("fterrenos.comprar.personalizado") || p.hasPermission("fterrenos.comprar.*")){
								if(instance.econ.has(p, instance.getConfig().getInt("Config.Preco_Bloco") * Integer.parseInt(args[2]))){
									int tamanho = Integer.parseInt(args[2]);
									tm.Comprar(p, area, tamanho);
								}else{
									p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Money_Comprar").replace("&", "§"));
								}
							}else{
								p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao_Personalizado").replace("&", "§"));
								return true;
							}
						}
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Excedeu_Limite").replace("@limite", String.valueOf(instance.getConfig().getInt("Config.Grupos." + instance.perm.getPrimaryGroup(p)))).replace("&", "§"));
					}
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Mundo_Desativado").replace("&", "§"));
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.Deletar"))){
				if(p.hasPermission("fterrenos.deletar") || p.hasPermission("fterrenos.player")){
					if(args.length == 1){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_Deletar").replace("&", "§"));
						return true;
					}
					String area = args[1];
					tm.Deletar(p, area);
					return true;
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.Renomear"))){
				if(p.hasPermission("fterrenos.renomear") || p.hasPermission("fterrenos.player")){
					if(args.length < 3){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_Renomear").replace("&", "§"));
						return true;
					}
					String nome = args[1];
					String nnome = args[2];
					tm.Renomear(p, nome, nnome);
					return true;
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.AddAmigo"))){
				if(p.hasPermission("fterrenos.addamigo") || p.hasPermission("fterrenos.player")){
					if(args.length < 3){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_AddAmigo").replace("&", "§"));
						return true;
					}else if(args[2].equalsIgnoreCase(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Pode_Se_Adicionar").replace("&", "§"));
						return true;
					}
					tm.addAmigo(p, args[2], args[1]);
					return true;
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.DelAmigo"))){
				if(p.hasPermission("fterrenos.delamigo") || p.hasPermission("fterrenos.player")){
					if(args.length < 3){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_DelAmigo").replace("&", "§"));
						return true;
					}
					tm.delAmigo(p, args[2], args[1]);
					return true;
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.PvP"))){
				if(p.hasPermission("fterrenos.pvp") || p.hasPermission("fterrenos.player")){
					if(args.length == 1){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_PvP").replace("&", "§"));
						return true;
					}
					if(args[1].equalsIgnoreCase("ativar") || args[1].equalsIgnoreCase("desativar")){
						if(instance.econ.has(p, instance.getConfig().getInt("Config.Preco_PvP"))){
							if(args[1].equalsIgnoreCase("ativar")){
								tm.PvP(p, "allow");
							}else{
								tm.PvP(p, "deny");
							}
						}else{
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Money_PvP").replace("&", "§"));
						}
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.PvP").replace("&", "§"));
					}
					return true;
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.Mobs"))){
				if(p.hasPermission("fterrenos.mob") || p.hasPermission("fterrenos.player")){
					if(args.length == 1){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_Mob").replace("&", "§"));
						return true;
					}
					if(args[1].equalsIgnoreCase("ativar") || args[1].equalsIgnoreCase("desativar")){
						if(instance.econ.has(p, instance.getConfig().getInt("Config.Preco_Mob"))){
							if(args[1].equalsIgnoreCase("ativar")){
								tm.Mob(p, "allow");
							}else{
								tm.Mob(p, "deny");
							}
						}else{
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Money_Mob").replace("&", "§"));
						}
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Mob").replace("&", "§"));
					}
					return true;
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.Msg"))){
				if(p.hasPermission("fterrenos.msg") || p.hasPermission("fterrenos.player")){
					if(args.length == 1){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_Msg").replace("&", "§"));
						return true;
					}
					String mensagem = "";
					for(int i = 1; i<args.length; i++){
						mensagem += args[i] + " ";
					}
					mensagem = mensagem.substring(0, mensagem.length() - 1);
					tm.Msg(p, mensagem);
					return true;
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.BlockCmd"))){
				if(p.hasPermission("fterrenos.blockcmd") || p.hasPermission("fterrenos.player")){
					if(args.length == 1){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_Block_Cmd").replace("&", "§"));
						return true;
					}
					tm.DesativarComando(p, args[1]);
					return true;
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.UnBlockCmd"))){
				if(p.hasPermission("fterrenos.unblockcmd") || p.hasPermission("fterrenos.player")){
					if(args.length == 1){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_UnBlock_Cmd").replace("&", "§"));
						return true;
					}
					tm.AtivarComando(p, args[1]);
					return true;
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.TpArea"))){
				if(p.hasPermission("fterrenos.tparea") || p.hasPermission("fterrenos.player")){
					if(args.length == 1){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_TpArea").replace("&", "§"));
						return true;
					}
					tm.tpArea(p, args[1]);
					return true;
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.Lista"))){
				if(p.hasPermission("fterrenos.lista") || p.hasPermission("fterrenos.player")){
					if(tm.GetAreas(p) == 0){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Terrenos").replace("&", "§"));
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Lista").replace("&", "§").replace("@areas", tm.GetAreasString(p)).replace("@nareas", String.valueOf(tm.GetAreas(p))));
					}
					return true;
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.Info"))){
				if(p.hasPermission("fterrenos.lista") || p.hasPermission("fterrenos.player")){
					tm.Info(p);
					return true;
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.Vender"))){
				if(p.hasPermission("fterrenos.vender") || p.hasPermission("fterrenos.player")){
					if(args.length == 1){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_Vender").replace("&", "§"));
						return true;
					}
					if(!IsNum(args[1])){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Somente_Numeros").replace("&", "§"));
						return true;
					}
					tm.Vender(p, Integer.parseInt(args[1]));
					return true;
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.Transferir"))){
				if(p.hasPermission("fterrenos.transferir") || p.hasPermission("fterrenos.player")){
					if(args.length == 1){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_Transferir").replace("&", "§"));
						return true;
					}
					Player player = instance.getServer().getPlayer(args[1]);
					if(player == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("@player", args[1]).replace("&", "§"));
						return true;
					}
					if(player.getName().equalsIgnoreCase(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Transferir_Si_Mesmo").replace("&", "§"));
						return true;
					}
					tm.Transferir(p, player);
					return true;
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.Limpar"))){
				if(p.hasPermission("fterrenos.limpar") || p.hasPermission("fterrenos.player")){
					tm.Limpar(p);
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.VerAmigos"))){
				if(p.hasPermission("fterrenos.veramigos") || p.hasPermission("fterrenos.player")){
					tm.getAmigos(p);
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.Admin.Principal"))){
				if(args.length == 1){
					for(String admin : instance.getConfig().getStringList("Mensagem.Comandos_Admin")){
						p.sendMessage(admin.replace("&", "§"));
					}
					return true;
				}
				if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.Admin.TpArea"))){
					if(args.length <= 3){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_TpArea_Admin").replace("&", "§"));
						return true;
					}
					if(p.hasPermission("fterrenos.admin.tparea") || p.hasPermission("fterrenos.admin")){
						tm.tpAreaStaff(p, args[2], args[3]);
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					}
				}else if(args[1].equalsIgnoreCase(instance.getConfig().getString("Config.Sub_Comandos.Admin.Lista"))){
					if(args.length == 2){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Erro_Lista_Admin").replace("&", "§"));
						return true;
					}
					if(p.hasPermission("fterrenos.admin.lista") || p.hasPermission("fterrenos.admin")){
						if(tm.GetAreasStaff(p, args[2]) == 0){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Terrenos_Staff").replace("&", "§"));
						}else{
							p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Lista_Staff").replace("&", "§").replace("@player", (instance.getServer().getPlayer(args[2]) == null ? args[2] : instance.getServer().getPlayer(args[2]).getName())).replace("@areas", tm.GetAreasStringStaff(p, args[2])).replace("@nareas", String.valueOf(tm.GetAreasStaff(p, args[2]))));
						}
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{1}", args[0]));
					}
				}
			}else{
				sendMessageList(p, instance.getConfig().getStringList("Mensagem.Comandos"));
			}
		}else if(cmd.getName().equalsIgnoreCase("terrenos")){
			if(!(sender instanceof Player)){
				sender.sendMessage("Unknown command. Type \"help\" for help.");
				return true;
			}
			final Player p = (Player) sender;
			if(!instance.getConfig().getBoolean("Config.Ativar_Terrenos")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Terrenos_Desativado").replace("&", "§"));
				return true;
			}
			if(terrenosdelay.containsKey(p.getName().toLowerCase())){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde_Terrenos").replace("&", "§")
						.replace("{tempo}", "" + ((terrenosdelay.get(p.getName().toLowerCase()) - System.currentTimeMillis())/1000.0)));
				return true;
			}
			if(!p.hasPermission("fterrenos.terrenos")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao_Terrenos").replace("&", "§"));
				return true;
			}
			if(instance.getServer().getWorld(instance.getConfig().getString("Config.Mundo_Terrenos")) == null){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Terrenos").replace("&", "§"));
				return true;
			}
			int x = r.nextInt(20000 - (-2000) + 1) + (-2000);
			int z = r.nextInt(2000 - (-2000) + 1) + (-2000);
			Location loc = new Location(instance.getServer().getWorld(instance.getConfig().getString("Config.Mundo_Terrenos")), (double) x, p.getWorld().getHighestBlockYAt(x, z)+1, (double) z, p.getLocation().getYaw(), p.getLocation().getPitch());
			while(loc.subtract(0, 1, 0).getBlock().isLiquid()){
				x = r.nextInt(2000 - (-2000) + 1) + (-2000);
				z = r.nextInt(2000 - (-2000) + 1) + (-2000);
				loc = new Location(instance.getServer().getWorld(instance.getConfig().getString("Config.Mundo_Terrenos")), (double) x, p.getWorld().getHighestBlockYAt(x, z)+1, (double) z, p.getLocation().getYaw(), p.getLocation().getPitch());
			}
			while(!loc.getBlock().isEmpty()){
				x = r.nextInt(2000 - (-2000) + 1) + (-2000);
				z = r.nextInt(2000 - (-2000) + 1) + (-2000);
				loc = new Location(instance.getServer().getWorld(instance.getConfig().getString("Config.Mundo_Terrenos")), (double) x, p.getWorld().getHighestBlockYAt(x, z)+1, (double) z, p.getLocation().getYaw(), p.getLocation().getPitch());
			}
			p.teleport(loc);
			p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Teleportado_Mundo").replace("&", "§"));
			terrenosdelay.put(p.getName().toLowerCase(), System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.Tempo_Terrenos")));
			new BukkitRunnable() {
				@Override
				public void run() {
					if(terrenosdelay.containsKey(p.getName().toLowerCase()))
						terrenosdelay.remove(p.getName().toLowerCase());
				}
			}.runTaskLater(instance, instance.getConfig().getInt("Config.Tempo_Terrenos")*20);
		}
		return false;
	}
	
	public void sendMessageList(Player p, List<String> msgs){
		for(String msg : msgs){
			p.sendMessage(msg.replace("&", "§"));
		}
	}

}

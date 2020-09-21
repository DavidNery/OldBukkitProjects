package me.zfork.hleilao;

import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class Comandos implements CommandExecutor {
	
	private HLeilao instance = HLeilao.getHLeilao();
	private LeilaoManager lm = instance.getLeilaoManager();
	private int tempoanuncios, anuncios;
	
	@SuppressWarnings({ "deprecation", "static-access" })
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("leilao")){
			if(args.length == 0){
				for(String msg : instance.getConfig().getStringList("Mensagem.Comandos")){
					sender.sendMessage(msg.replace("&", "§"));
				}
				return true;
			}
			if(sender instanceof Player){
				final Player p = (Player) sender;
				if(args[0].equalsIgnoreCase("iniciar")){
					if(!p.hasPermission("leilao.iniciar")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return true;
					}
					if(args.length == 1){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Iniciar").replace("&", "§"));
						return true;
					}else if(!isNum(args[1])){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Informe_Apenas_Numeros").replace("&", "§"));
						return true;
					}else if(p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Item_Mao").replace("&", "§"));
						return true;
					}else if(instance.getDevolver().containsKey(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Pegue_O_Item").replace("&", "§"));
						return true;
					}else if(!instance.getConfig().getIntegerList("Config.White_List").contains(p.getItemInHand().getTypeId())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Pode_Leiloar_Item").replace("&", "§"));
						return true;
					}else if(!instance.econ.has(p.getName(), instance.getConfig().getDouble("Config.Valor_Necessario_Iniciar"))){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Valor_Necessario").replace("&", "§")
								.replace("{valor}", NumberFormat.getCurrencyInstance().format(instance.getConfig().getDouble("Config.Valor_Necessario_Iniciar")-instance.econ.getBalance(p.getName())).replace("$", "")));
						return true;
					}
					double valor = Double.parseDouble(args[1]);
					if(valor < 1 || valor > instance.getConfig().getDouble("Config.Valor_Inicial_Maximo")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Valor_Improprio").replace("&", "§"));
						return true;
					}else if(lm.getLeiloando() != null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ja_Havendo").replace("&", "§"));
						return true;
					}else if(lm.getProximo() != 0 && lm.getProximo() > System.currentTimeMillis()){
						long time = lm.getProximo() - System.currentTimeMillis();
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde_Proximo").replace("&", "§").replace("{restante}", instance.getTime(time)));
						return true;
					}
					lm.setItem(p.getItemInHand());
					p.setItemInHand(null);
					p.updateInventory();
					lm.setLeiloando(p.getName());
					lm.setLiderando("");
					lm.setMaiorLance(0);
					lm.setMinimo(valor);
					lm.changeInfo();
					tempoanuncios = instance.getConfig().getInt("Config.Tempo_Anuncios");
					anuncios = instance.getConfig().getInt("Config.Anuncios");
					instance.setTask(new BukkitRunnable(){
						@Override
						public void run() {
							if(anuncios > 0){
								for(String msg : instance.getConfig().getStringList("Mensagem.Iniciou_Leilao")){
									for(Player on : instance.getServer().getOnlinePlayers()){
										on.sendMessage(msg.replace("&", "§").replace("{player}", p.getName()).replace("{item}", lm.getItem().getType().name())
												.replace("{apostaminima}", NumberFormat.getCurrencyInstance().format(lm.getMinimo()).replaceAll("[^\\d\\.,]+", ""))
												.replace("{maiorlance}", NumberFormat.getCurrencyInstance().format(lm.getMaiorLance()).replaceAll("[^\\d\\.,]+", ""))
												.replace("{liderando}", lm.getLiderando().equals("") ? "Ninguém" : lm.getLiderando()));
									}
								}
								anuncios -= 1;
							}else{
								instance.getTask().cancel();
								instance.setTask(null);
								if(lm.getLiderando().equals("")){
									for(String msg : instance.getConfig().getStringList("Mensagem.Nao_Houve_Lances")){
										for(Player on : instance.getServer().getOnlinePlayers()){
											on.sendMessage(msg.replace("&", "§").replace("{player}", p.getName()).replace("{item}", lm.getItem().getType().name()));
										}
									}
									if(p.getInventory().firstEmpty() == -1){
										Inventory inv = instance.getServer().createInventory(null, 9, "Seu item");
										inv.addItem(lm.getItem());
										p.openInventory(inv);
									}else{
										p.getInventory().addItem(lm.getItem());
									}
								}else{
									instance.econ.depositPlayer(p.getName(), (lm.getMaiorLance()*90)/100);
									instance.econ.depositPlayer("Banco", (lm.getMaiorLance()*10)/100);
									if(!instance.getConfig().getBoolean("Config.Retirar_Dinheiro_Apostar")){
										instance.econ.withdrawPlayer(lm.getLiderando(), instance.getApostado().get(lm.getLiderando()));
									}
									for(String msg : instance.getConfig().getStringList("Mensagem.Ganhador")){
										for(Player on : instance.getServer().getOnlinePlayers()){
											on.sendMessage(msg.replace("&", "§").replace("{ganhador}", lm.getLiderando())
													.replace("{valor}", NumberFormat.getCurrencyInstance().format(lm.getMaiorLance()).replaceAll("[^\\d\\.,]+", ""))
													.replace("{player}", p.getName()).replace("{item}", lm.getItem().getType().name()));
										}
									}
									Player liderando = instance.getServer().getPlayer(lm.getLiderando());
									if(liderando != null){
										if(liderando.getInventory().firstEmpty() == -1){
											Inventory inv = instance.getServer().createInventory(null, 9, "Seu item");
											inv.addItem(lm.getItem());
											liderando.openInventory(inv);
										}else{
											liderando.getInventory().addItem(lm.getItem());
										}
									}else{
										if(!instance.getServer().getOfflinePlayer(lm.getLiderando()).isBanned()){
											instance.getDevolver().put(lm.getLiderando(), lm.getItem());
										}
									}
								}
								lm.resetar(System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.Tempo_Entre_Leiloes")));
								instance.getApostado().clear();
							}
						}
					}.runTaskTimer(instance, 0, tempoanuncios*20));
				}else if(args[0].equalsIgnoreCase("info")){
					if(!p.hasPermission("leilao.info")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return true;
					}else if(lm.getLeiloando() == null || instance.getTask() == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Acontecendo_Leilao").replace("&", "§"));
						return true;
					}
					p.openInventory(lm.getInv());
				}else if(args[0].equalsIgnoreCase("recuperar")){
					if(!p.hasPermission("leilao.recuperar")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return true;
					}else if(!instance.getDevolver().containsKey(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nada_Para_Recuperar").replace("&", "§"));
						return true;
					}else if(p.getInventory().firstEmpty() == -1){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inventario_Cheio").replace("&", "§"));
						return true;
					}
					p.getInventory().addItem(instance.getDevolver().get(p.getName()));
					instance.getDevolver().remove(p.getName());
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Recuperou_Item").replace("&", "§"));
				}else if(args[0].equalsIgnoreCase("cancelar")){
					if(!p.hasPermission("leilao.cancelar")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return true;
					}else if(lm.getLeiloando() == null || instance.getTask() == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Acontecendo_Leilao").replace("&", "§"));
						return true;
					}else if(!instance.getServer().getPlayer(lm.getLeiloando()).getName().equals(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_E_Seu").replace("&", "§"));
						return true;
					}
					instance.getTask().cancel();
					instance.setTask(null);
					for(String msg : instance.getConfig().getStringList("Mensagem.Cancelou")){
						for(Player on : instance.getServer().getOnlinePlayers()){
							on.sendMessage(msg.replace("&", "§").replace("{player}", lm.getLeiloando()).replace("{item}", lm.getItem().getType().name()));
						}
					}
					if(instance.getConfig().getBoolean("Config.Retirar_Dinheiro_Apostar")){
						for(String player : instance.getApostado().keySet()){
							instance.econ.depositPlayer(player, instance.getApostado().get(player));
						}
					}
					if(p.getInventory().firstEmpty() == -1){
						Inventory inv = instance.getServer().createInventory(null, 9, "Seu item");
						inv.addItem(lm.getItem());
						p.openInventory(inv);
					}else{
						p.getInventory().addItem(lm.getItem());
					}
					lm.resetar(System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.Tempo_Entre_Leiloes")));
				}else if(args[0].equalsIgnoreCase("lance")){
					if(!p.hasPermission("leilao.lance")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return true;
					}else if(lm.getLeiloando() == null || instance.getTask() == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Acontecendo_Leilao").replace("&", "§"));
						return true;
					}else if(lm.getLeiloando().equals(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Pode_Apostar_Proprio_Leilao").replace("&", "§"));
						return true;
					}else if(lm.getLiderando().equals(p.getName())){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde_Apostar").replace("&", "§"));
						return true;
					}else if(args.length == 1){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Lance").replace("&", "§"));
						return true;
					}else if(!isNum(args[1])){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Informe_Apenas_Numeros").replace("&", "§"));
						return true;
					}
					double aposta = Double.parseDouble(args[1]);
					if(aposta < lm.getMinimo() || aposta <= lm.getMaiorLance()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Lance_Maior").replace("&", "§"));
						return true;
					}else if(!instance.econ.has(p.getName(), aposta)){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Tem_Suficiente").replace("&", "§"));
						return true;
					}
					if(instance.getApostado().containsKey(p.getName())){
						if(instance.getApostado().get(p.getName()) + aposta > instance.econ.getBalance(p.getName())){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Tem_Suficiente").replace("&", "§"));
							return true;
						}
					}
					if(instance.getConfig().getBoolean("Config.Retirar_Dinheiro_Apostar")){
						if(instance.getConfig().getBoolean("Config.Devolver_Dinheiro_Ultrapassar")){
							instance.econ.depositPlayer(lm.getLiderando(), lm.getMaiorLance());
						}
					}
					instance.getApostado().remove(lm.getLiderando());
					lm.setMaiorLance(aposta);
					lm.setLiderando(p.getName());
					lm.changeInfo();
					for(Player on : instance.getServer().getOnlinePlayers()){
						on.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Player_Apostou").replace("&", "§")
								.replace("{player}", p.getName()).replace("{valor}", NumberFormat.getCurrencyInstance().format(lm.getMaiorLance()).replaceAll("[^\\d\\.,]+", "")));
					}
					if(instance.getConfig().getBoolean("Config.Retirar_Dinheiro_Apostar")){
						instance.econ.withdrawPlayer(p.getName(), aposta);
					}
					if(!instance.getApostado().containsKey(p.getName())){
						instance.getApostado().put(p.getName(), aposta);
					}else{
						instance.getApostado().put(p.getName(), aposta+instance.getApostado().get(p.getName()));
					}
				}else if(args[0].equalsIgnoreCase("admin")){
					if(!p.hasPermission("leilao.admin")){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return true;
					}else if(args.length == 1){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Admin").replace("&", "§"));
						return true;
					}
					if(args[1].equalsIgnoreCase("cancelar")){
						if(lm.getLeiloando() == null || instance.getTask() == null){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Esta_Acontecendo_Leilao").replace("&", "§"));
							return true;
						}
						instance.getTask().cancel();
						instance.setTask(null);
						for(String msg : instance.getConfig().getStringList("Mensagem.Admin_Cancelou")){
							for(Player on : instance.getServer().getOnlinePlayers()){
								on.sendMessage(msg.replace("&", "§").replace("{admin}", p.getName()).replace("{player}", lm.getLeiloando()).replace("{item}", lm.getItem().getType().name()));
							}
						}
						if(instance.getConfig().getBoolean("Config.Retirar_Dinheiro_Apostar")){
							for(String player : instance.getApostado().keySet()){
								instance.econ.depositPlayer(player, instance.getApostado().get(player));
							}
						}
						Player leiloando = instance.getServer().getPlayer(lm.getLeiloando());
						if(leiloando != null){
							if(leiloando.getInventory().firstEmpty() == -1){
								Inventory inv = instance.getServer().createInventory(null, 9, "Seu item");
								inv.addItem(lm.getItem());
								leiloando.openInventory(inv);
							}else{
								leiloando.getInventory().addItem(lm.getItem());
							}
						}else{
							if(!instance.getServer().getOfflinePlayer(lm.getLeiloando()).isBanned()){
								instance.getDevolver().put(lm.getLiderando(), lm.getItem());
							}
						}
						lm.resetar(System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.Tempo_Entre_Leiloes")));
					}else if(args[1].equalsIgnoreCase("reload")){
						instance.saveConfig();
						instance.reloadConfig();
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Recarregado").replace("&", "§"));
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Admin").replace("&", "§"));
						return true;
					}
				}else{
					for(String msg : instance.getConfig().getStringList("Mensagem.Comandos")){
						sender.sendMessage(msg.replace("&", "§"));
					}
				}
			}
			// sender instanceof player
		}
		return false;
	}
	
	public boolean isNum(String num){
		try{
			Double.parseDouble(num);
			return true;
		}catch(Exception e){
			return false;
		}
	}

}

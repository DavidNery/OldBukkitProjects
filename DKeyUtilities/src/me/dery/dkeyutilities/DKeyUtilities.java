package me.dery.dkeyutilities;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Random;
import java.util.Set;

import me.dery.dkeyutilities.API.CreateKeyEvent;
import me.dery.dkeyutilities.API.DeleteKeyEvent;
import me.dery.dkeyutilities.API.GetKeyEvent;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DKeyUtilities extends JavaPlugin implements CommandExecutor{
	
	public static Economy econ = null;
	boolean Vault = true;
	Random r = new Random();
	
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bDKeyUtilities§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bDery");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null){
			getServer().getConsoleSender().sendMessage(" §3Vault: §bNao Encontrado");
			Vault = false;
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
			if(!new File(getDataFolder(), "info.yml").exists()){
				saveResource("info.yml", false);
			}
		}
		getServer().getConsoleSender().sendMessage("§3==========[§bDKeyUtilities§3]==========");
		setupEconomy();
	}
	
	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§4==========[§cDKeyUtilities§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §cDery");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cDKeyUtilities§4]==========");
	}

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}
	
	public boolean IsNum(String arg){
		try{
			Integer.parseInt(arg);
			return true;
		}catch(NumberFormatException e){}
		return false;
	}
	
	public void createKEY(CommandSender p, Double preco, String key, Integer qnt){
		if(preco > 0){
			p.sendMessage(getConfig().getString("Mensagem.Sucesso.Criou_Key_Free").replace("&", "§").replace("{key}", key));
		}else{
			p.sendMessage(getConfig().getString("Mensagem.Sucesso.Criou_Key_Paga").replace("&", "§").replace("{key}", key).replace("{preco}", NumberFormat.getNumberInstance().format(preco)));
		}
		getConfig().set("Keys." + key + ".Preco", preco);
		getConfig().set("Keys." + key + ".Quantidade", qnt);
		saveConfig();
		reloadConfig();
	}
	
	public void delKEY(CommandSender p, String key){
		p.sendMessage(getConfig().getString("Mensagem.Sucesso.Deletou_Key").replace("&", "§").replace("{key}", key));
		getConfig().set("Keys." + key, null);
		saveConfig();
		reloadConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("createkey")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				if(!p.hasPermission("dkeyutilities.createkey")){
					p.sendMessage(getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{0}", cmd.getName()));
					return true;
				}
				if(args.length < 3){
					p.sendMessage(getConfig().getString("Mensagem.Erro.Poucos_Argumentos_Create").replace("&", "§"));
					return true;
				}
				if(!IsNum(args[0])){
					p.sendMessage(getConfig().getString("Mensagem.Erro.Somente_Numeros").replace("&", "§"));
					return true;
				}else if(!IsNum(args[2])){
					p.sendMessage(getConfig().getString("Mensagem.Erro.Somente_Numeros").replace("&", "§"));
					return true;
				}
				Double preco = Double.parseDouble(args[0]);
				String key = args[1];
				int qnt = Integer.parseInt(args[2]);
				if(qnt < 1){
					p.sendMessage(getConfig().getString("Mensagem.Erro.Quantidade_Maior_Que_Zero").replace("&", "§"));
					return true;
				}
				if(getConfig().getConfigurationSection("Keys").getKeys(false).contains(key)){
					p.sendMessage(getConfig().getString("Mensagem.Erro.Key_Ja_Existe").replace("&", "§").replace("{key}", key));
					return true;
				}
				if(preco < 0){
					p.sendMessage(getConfig().getString("Mensagem.Erro.Menor_Que_Zero").replace("&", "§"));
					return true;
				}
				CreateKeyEvent create = new CreateKeyEvent(p, key, preco);
				Bukkit.getServer().getPluginManager().callEvent(create);
				if(!create.isCancelled()){
					createKEY(p, preco, key, qnt);
				}
			}else{
				if(args.length == 0){
					sender.sendMessage(getConfig().getString("Mensagem.Erro.Poucos_Argumentos_Create").replace("&", "§"));
					return true;
				}
				if(!IsNum(args[0])){
					sender.sendMessage(getConfig().getString("Mensagem.Erro.Somente_Numeros").replace("&", "§"));
					return true;
				}else if(!IsNum(args[2])){
					sender.sendMessage(getConfig().getString("Mensagem.Erro.Somente_Numeros").replace("&", "§"));
					return true;
				}
				Double preco = Double.parseDouble(args[0]);
				String key = args[1];
				int qnt = Integer.parseInt(args[2]);
				if(qnt < 1){
					sender.sendMessage(getConfig().getString("Mensagem.Erro.Quantidade_Maior_Que_Zero").replace("&", "§"));
					return true;
				}
				if(getConfig().getConfigurationSection("Keys").getKeys(false).contains(key)){
					sender.sendMessage(getConfig().getString("Mensagem.Erro.Key_Ja_Existe").replace("&", "§").replace("{key}", key));
					return true;
				}
				if(preco < 0){
					sender.sendMessage(getConfig().getString("Mensagem.Erro.Menor_Que_Zero").replace("&", "§"));
					return true;
				}
				CreateKeyEvent create = new CreateKeyEvent(sender, key, preco);
				Bukkit.getServer().getPluginManager().callEvent(create);
				if(!create.isCancelled()){
					createKEY(sender, preco, key, qnt);
				}
			}
		}else if(cmd.getName().equalsIgnoreCase("deletekey")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				if(!p.hasPermission("dkeyutilities.deletekey")){
					p.sendMessage(getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{0}", cmd.getName()));
					return true;
				}
				if(args.length == 0){
					p.sendMessage(getConfig().getString("Mensagem.Erro.Poucos_Argumentos_Delete").replace("&", "§").replace("{0}", cmd.getName()));
					return true;
				}
				String key = args[0];
				if(!getConfig().getConfigurationSection("Keys").getKeys(false).contains(key)){
					p.sendMessage(getConfig().getString("Mensagem.Erro.Key_Nao_Existe").replace("&", "§").replace("{key}", key));
					return true;
				}
				DeleteKeyEvent delete = new DeleteKeyEvent(p, key);
				Bukkit.getServer().getPluginManager().callEvent(delete);
				if(!delete.isCancelled()){
					delKEY(p, key);
				}
			}else{
				if(args.length == 0){
					sender.sendMessage(getConfig().getString("Mensagem.Erro.Poucos_Argumentos_Delete").replace("&", "§").replace("{0}", cmd.getName()));
					return true;
				}
				String key = args[0];
				if(!getConfig().getConfigurationSection("Keys").getKeys(false).contains(key)){
					sender.sendMessage(getConfig().getString("Mensagem.Erro.Key_Nao_Existe").replace("&", "§").replace("{key}", key));
					return true;
				}
				DeleteKeyEvent delete = new DeleteKeyEvent(sender, key);
				Bukkit.getServer().getPluginManager().callEvent(delete);
				if(!delete.isCancelled()){
					delKEY(sender, key);
				}
			}
		}else if(cmd.getName().equalsIgnoreCase("premio")){
			if(!(sender instanceof Player)){
				sender.sendMessage("Unkown command. Type \"help\" for help.");
				return true;
			}
			Player p = (Player) sender;
			if(args.length == 0){
				sender.sendMessage(getConfig().getString("Mensagem.Erro.Poucos_Argumentos_Premio").replace("&", "§").replace("{0}", cmd.getName()));
				return true;
			}
			String key = args[0];
			Double preco = getConfig().getDouble("Keys." + key + ".Preco");
			if(!getConfig().getConfigurationSection("Keys").getKeys(false).contains(key)){
				p.sendMessage(getConfig().getString("Mensagem.Erro.Key_Nao_Existe").replace("&", "§").replace("{key}", key));
				return true;
			}
			if(getConfig().getStringList("Keys." + key + ".Usaram").contains(p.getName())){
				p.sendMessage(getConfig().getString("Mensagem.Erro.Ja_Utilizou").replace("&", "§"));
				return true;
			}
			if(getConfig().getDouble("Keys." + key + ".Preco") > 0){
				GetKeyEvent get = new GetKeyEvent(p, key, preco);
				Bukkit.getServer().getPluginManager().callEvent(get);
				if(!get.isCancelled()){
					if(econ.has(p.getName(), preco)){
						econ.withdrawPlayer(p.getName(), preco);
						if(getConfig().contains("Keys." + key + ".Items")){
							for(String items : getConfig().getStringList("Keys." + key + ".Items")){
								String[] partes = items.split(";");
								String item = partes[0];
								String[] itempartes = item.split(":");
								int id = Integer.parseInt(itempartes[0]);
								int data;
								if(itempartes.length == 1){
									data = 0;
								}else{
									data = Integer.parseInt(itempartes[1]);
								}
								int qnt = Integer.parseInt(partes[1]);
								ItemStack itemstack = new ItemStack(Material.getMaterial(id), qnt, (byte) data);
								/*if(partes.length == 3){
									String[] parteenchants = partes[2].split("-");
									for(String enchants : parteenchants){
										String[] enchants1 = enchants.split(":");
										String enchant = enchants1[0];
										int level = Integer.parseInt(enchants1[1]);
										itemstack.addUnsafeEnchantment(Enchantment.getByName(enchant), level);
									}
								}*/
								if(!partes[2].isEmpty()){
									String[] parteenchants = partes[2].split("-");
									for(String enchants : parteenchants){
										String[] enchants1 = enchants.split(":");
										String enchant = enchants1[0];
										int level = Integer.parseInt(enchants1[1]);
										itemstack.addUnsafeEnchantment(Enchantment.getByName(enchant), level);
									}
								}
								int chance = Integer.parseInt(partes[3]);
								int result = r.nextInt(100);
								if(chance>=result){
									p.getInventory().addItem(itemstack);
								}
							}
						}
						if(getConfig().contains("Keys." + key + ".Comandos")){
							for(String cmds : getConfig().getStringList("Keys." + key + ".Comandos")){
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmds.replace("{key}", key).replace("{player}", p.getName()).replace("@player", p.getName()).replace("@p", p.getName()));
							}
						}
						if(getConfig().contains("Keys." + key + ".Pocoes")){
							for(String efeitos : getConfig().getStringList("Keys." + key + ".Pocoes")){
								String[] partes = efeitos.split(" ");
								String pocao = partes[0];
								int tempo = Integer.parseInt(partes[1]);
								int amplifidade = Integer.parseInt(partes[2]);
								p.addPotionEffect(new PotionEffect(PotionEffectType.getByName(pocao), tempo * 20, amplifidade));
							}
						}
						getConfig().set("Keys." + key + ".Vezes_Utilizadas", getConfig().getInt("Keys." + key + ".Vezes_Utilizadas") + 1);
						saveConfig();
						reloadConfig();
						if(getConfig().getInt("Keys." + key + ".Vezes_Utilizadas") >= getConfig().getInt("Keys." + key + ".Quantidade")){
							delKEY(p, key);
						}else{
							List<String> usou = getConfig().getStringList("Keys." + key + ".Usaram");
							usou.add(p.getName());
							getConfig().set("Keys." + key + ".Usaram", usou);
							saveConfig();
							reloadConfig();
							usou.clear();
							p.sendMessage(getConfig().getString("Mensagem.Sucesso.Pegou_Key").replace("&", "§").replace("{preco}", "0.0").replace("{key}", key));
						}
					}else{
						p.sendMessage(getConfig().getString("Mensagem.Erro.Sem_Money").replace("&", "§").replace("{preciso}", String.valueOf(preco - econ.getBalance(p.getName()))).replace("{preco}", String.valueOf(NumberFormat.getNumberInstance().format(preco))).replace("{key}", key));
					}
				}
			}else{
				GetKeyEvent get = new GetKeyEvent(p, key, preco);
				Bukkit.getServer().getPluginManager().callEvent(get);
				if(!get.isCancelled()){
					if(getConfig().contains("Keys." + key + ".Items")){
						for(String items : getConfig().getStringList("Keys." + key + ".Items")){
							String[] partes = items.split(";");
							String item = partes[0];
							String[] itempartes = item.split(":");
							int id = Integer.parseInt(itempartes[0]);
							int data;
							if(itempartes.length == 1){
								data = 0;
							}else{
								data = Integer.parseInt(itempartes[1]);
							}
							int qnt = Integer.parseInt(partes[1]);
							ItemStack itemstack = new ItemStack(Material.getMaterial(id), qnt, (byte) data);
							/*if(partes.length == 3){
							String[] parteenchants = partes[2].split("-");
							for(String enchants : parteenchants){
								String[] enchants1 = enchants.split(":");
								String enchant = enchants1[0];
								int level = Integer.parseInt(enchants1[1]);
								itemstack.addUnsafeEnchantment(Enchantment.getByName(enchant), level);
							}
						}*/
						if(!partes[2].isEmpty()){
							String[] parteenchants = partes[2].split("-");
							for(String enchants : parteenchants){
								String[] enchants1 = enchants.split(":");
								String enchant = enchants1[0];
								int level = Integer.parseInt(enchants1[1]);
								itemstack.addUnsafeEnchantment(Enchantment.getByName(enchant), level);
							}
						}
						int chance = Integer.parseInt(partes[3]);
						int result = r.nextInt(100);
						if(chance>=result){
							p.getInventory().addItem(itemstack);
						}
						}
					}
					if(getConfig().contains("Keys." + key + ".Comandos")){
						for(String cmds : getConfig().getStringList("Keys." + key + ".Comandos")){
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmds.replace("{key}", key).replace("{player}", p.getName()).replace("@player", p.getName()).replace("@p", p.getName()));
						}
					}
					if(getConfig().contains("Keys." + key + ".Pocoes")){
						for(String efeitos : getConfig().getStringList("Keys." + key + ".Pocoes")){
							String[] partes = efeitos.split(" ");
							String pocao = partes[0];
							int tempo = Integer.parseInt(partes[1]);
							int amplifidade = Integer.parseInt(partes[2]);
							p.addPotionEffect(new PotionEffect(PotionEffectType.getByName(pocao), tempo * 20, amplifidade));
						}
					}
					getConfig().set("Keys." + key + ".Vezes_Utilizadas", getConfig().getInt("Keys." + key + ".Vezes_Utilizadas") + 1);
					saveConfig();
					reloadConfig();
					if(getConfig().getInt("Keys." + key + ".Vezes_Utilizadas") >= getConfig().getInt("Keys." + key + ".Quantidade")){
						delKEY(p, key);
					}else{
						List<String> usou = getConfig().getStringList("Keys." + key + ".Usaram");
						usou.add(p.getName());
						getConfig().set("Keys." + key + ".Usaram", usou);
						saveConfig();
						reloadConfig();
						usou.clear();
						p.sendMessage(getConfig().getString("Mensagem.Sucesso.Pegou_Key").replace("&", "§").replace("{preco}", "0.0").replace("{key}", key));
					}
				}
			}
		}else if(cmd.getName().equalsIgnoreCase("dkeys")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				if(!p.hasPermission("dkeyutilities.dkeys")){
					p.sendMessage(getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("{0}", cmd.getName()));
					return true;
				}
				Set<String> keys = getConfig().getConfigurationSection("Keys").getKeys(false);
				if(keys.size() == 0){
					p.sendMessage(getConfig().getString("Mensagem.Sucesso.Sem_Keys").replace("&", "§"));
				}else{
					String k = "";
					for(String key : keys){
						k += key + ", ";
					}
					p.sendMessage(getConfig().getString("Mensagem.Sucesso.Keys").replace("&", "§").replace("{keys}", k.substring(0, k.length()-2)));
				}
			}else{
				Set<String> keys = getConfig().getConfigurationSection("Keys").getKeys(false);
				if(keys.size() == 0){
					sender.sendMessage(getConfig().getString("Mensagem.Sucesso.Sem_Keys").replace("&", "§"));
				}else{
					String k = "";
					for(String key : keys){
						k += key + ", ";
					}
					sender.sendMessage(getConfig().getString("Mensagem.Sucesso.Keys").replace("&", "§").replace("{keys}", k.substring(0, k.length()-2)));
				}
			}
		}
		return false;
	}

}

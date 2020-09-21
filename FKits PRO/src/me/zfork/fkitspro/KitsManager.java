package me.zfork.fkitspro;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class KitsManager {

	private FKitsPRO instance;
	private HashSet<Kit> kits = new HashSet<Kit>();
	private HashMap<String, Inventory> invs = new HashMap<String, Inventory>();
	private HashMap<String, Listener> invsListeners = new HashMap<String, Listener>();
	private Inventory main;
	
	private boolean canPick;

	public KitsManager(FKitsPRO instance){
		this.instance = instance;
		canPick = false;
	}

	public HashSet<Kit> getKits(){
		return this.kits;
	}

	public HashMap<String, Inventory> getInvs(){
		return this.invs;
	}
	
	public HashMap<String, Listener> getInvsListeners() {
		return invsListeners;
	}

	public Inventory getMain(){
		return this.main;
	}
	
	public void setCanPick(boolean canPick) {
		this.canPick = canPick;
	}
	
	public boolean getCanPick() {
		return this.canPick;
	}

	public void loadKits(){
		for(String kits : instance.getConfig().getConfigurationSection("Config.Kits").getKeys(false)){
			Kit kit = new Kit(kits, instance.getConfig().contains("Config.Kits." + kits + ".Preco") ? 
					instance.getConfig().getDouble("Config.Kits." + kits + ".Preco") : 0, instance.getConfig().getLong("Config.Kits." + kits + ".Tempo"));
			if(instance.getConfig().contains("Config.Kits." + kits + ".Items"))
				for(String items : instance.getConfig().getStringList("Config.Kits." + kits + ".Items")) kit.getItems().add((ItemStack) criarItem(items));
			this.kits.add(kit);
		}
	}

	public void loadInvs(){
		for(String inventarios : instance.getConfig().getConfigurationSection("Config.Inventarios").getKeys(false)){
			Inventory inv = instance.getServer().createInventory(null, instance.getConfig().getInt("Config.Inventarios." + inventarios + ".Tamanho")
					, instance.getConfig().getString("Config.Inventarios." + inventarios + ".Nome").replace("&", "§"));
			if(instance.getConfig().getBoolean("Config.Inventarios." + inventarios + ".Completar_Inventario")){
				ItemStack completar = (ItemStack) criarItem(instance.getConfig().getString("Config.Inventarios." + inventarios + ".Item_Completar_Inventario"));
				for(int i = 0; i<instance.getConfig().getInt("Config.Inventarios." + inventarios + ".Tamanho"); i++){
					inv.setItem(i, completar);
				}
			}
			if(instance.getConfig().contains("Config.Inventarios." + inventarios + ".Slots")){
				for(String slot : instance.getConfig().getString("Config.Inventarios." + inventarios + ".Slots").split(";")){
					String[] partes = slot.split("->");
					for(String parte : partes[0].split(",")){
						inv.setItem(Integer.parseInt(parte)-1, new ItemStack(Material.getMaterial(Integer.parseInt(partes[1])), 1, 
								(byte) (partes[1].contains(":") ? Integer.parseInt(partes[1].split(":")[1]) : 0)));
					}
				}
			}
			for(String items : instance.getConfig().getConfigurationSection("Config.Inventarios." + inventarios + ".Custom_Items").getKeys(false)){
				inv.setItem(Integer.parseInt(items)-1, 
						(ItemStack) criarItem(instance.getConfig().getString("Config.Inventarios." + inventarios + ".Custom_Items." + items + ".Item")));
			}
			invs.put(inventarios, inv);
			if(instance.getConfig().getBoolean("Config.Ativar_Inv")){
				Listener listener = new Listener() {

					@EventHandler
					public void Click(InventoryClickEvent e){
						Player p = (Player) e.getWhoClicked();
						if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
						if(e.getInventory().getType() == InventoryType.CHEST){
							if(e.getInventory().getTitle().equalsIgnoreCase(inv.getTitle())){
								e.setCancelled(true);
								for(String items : instance.getConfig().getConfigurationSection("Config.Inventarios." + inventarios + ".Custom_Items").getKeys(false)){
									if(e.getSlot()+1 == Integer.parseInt(items)){
										if(e.getClick() == ClickType.LEFT && instance.getConfig().contains("Config.Inventarios." + inventarios + ".Custom_Items." + items + ".Esquerdo")){
											for(String comandos : instance.getConfig().getStringList("Config.Inventarios." + inventarios + ".Custom_Items." + items + ".Esquerdo")){
												if(checarAcao(p, comandos)) break;
											}
										}else if(e.getClick() == ClickType.RIGHT && instance.getConfig().contains("Config.Inventarios." + inventarios + ".Custom_Items." + items + ".Direito")){
											for(String comandos : instance.getConfig().getStringList("Config.Inventarios." + inventarios + ".Custom_Items." + items + ".Direito")){
												if(checarAcao(p, comandos)) break;
											}
										}
										return;
									}
								}
								return;
							}
						}
					}
				};
				invsListeners.put(inventarios, listener);
				instance.getServer().getPluginManager().registerEvents(listener, instance);
			}
			if(instance.getConfig().contains("Config.Inventarios." + inventarios + ".Main")) main = inv;
		}
	}

	@SuppressWarnings("static-access")
	public boolean checarAcao(Player p, String label){
		if(label.toLowerCase().startsWith("dar kit")){
			darKit(null, p, label.toLowerCase().split("dar kit ")[1]);
			//p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Pegou_Kit").replace("&", "§").replace("{kit}", label.toLowerCase().split("dar kit ")[1]));
		}else if(label.toLowerCase().startsWith("abrir inv")){
			abrirInventario(p, label.toLowerCase().split("abrir inv ")[1]);
		}else if(label.toLowerCase().startsWith("player executar cmd")){
			darComando(p, label.toLowerCase().split("player executar cmd ")[1], p);
		}else if(label.toLowerCase().startsWith("console executar cmd")){
			darComando(instance.getServer().getConsoleSender(), label.toLowerCase().split("console executar cmd ")[1], p);
		}else if(label.toLowerCase().startsWith("checar permissao")){
			if(!p.hasPermission(label.split("checar permissao ")[1].split("->")[0])){
				String acao = "";
				for(String s : label.split("->")[1].split(" ")) acao += s + " ";
				checarAcao(p, acao.trim());
				return true;
			}
		}else if(label.toLowerCase().startsWith("enviar mensagem ")){
			String msg = " ";
			for(String s : label.split("enviar mensagem ")[1].split(" ")){
				msg += s + " ";
			}
			p.sendMessage(msg.trim().replace("&", "§").replace("{player}", p.getName()));
		}else if(label.toLowerCase().startsWith("tem money")){
			if(!instance.econ.has(p.getName(), Double.parseDouble(label.toLowerCase().split("tem money ")[1]))){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Dinheiro").replace("&", "§"));
				return true;
			}
			instance.econ.withdrawPlayer(p.getName(), Double.parseDouble(label.toLowerCase().split("tem money ")[1]));
			p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Pagou_Item").replace("&", "§")
					.replace("{preco}", NumberFormat.getCurrencyInstance().format(Double.parseDouble(label.toLowerCase().split("tem money ")[1]))
							.replace("$", "")));
		}else if(label.toLowerCase().startsWith("tem tintacoin ")){
			File f = new File(instance.getTintaCoin().getDataFolder(), "playerdata.yml");
			if(f.exists()){
				FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
				if(fc.contains("Coins")){
					for(String s : fc.getConfigurationSection("Coins").getKeys(false)){
						if(s.equalsIgnoreCase(p.getName())){
							if(fc.getDouble("Coins." + s) >= Double.parseDouble(label.toLowerCase().split("tem tintacoin ")[1])){
								fc.set("Coins." + s, Double.parseDouble(label.toLowerCase().split("tem tintacoin ")[1]));
								try {
									fc.save(f);
									fc.load(f);
								} catch (Exception e) {}
								return false;
							}else{
								p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Coins").replace("&", "§"));
								return true;
							}
						}
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Coins").replace("&", "§"));
					return true;
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Coins").replace("&", "§"));
					return true;
				}
			}
		}
		return false;
	}

	public void darComando(CommandSender sender, String comando, Player p){
		instance.getServer().dispatchCommand(sender, comando
				.replace("_", " ").replace("{player}", p.getName()));
		if(p.getOpenInventory() != null) p.getOpenInventory().close();
	}

	public void abrirInventario(Player p, String inventario){
		if(p.getOpenInventory() != null) p.getOpenInventory().close();
		if(instance.getConfig().contains("Config.Inventarios." + inventario)){
			p.openInventory(instance.getKitsManager().getInvs().get(inventario));
		}
	}

	@SuppressWarnings("static-access")
	public void darKit(String giver, Player p, String kitname){
		if(!canPick){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde_Pegar").replace("&", "§"));
			return;
		}
		if(p.getOpenInventory() != null) p.getOpenInventory().close();
		for(Kit kits : instance.getKitsManager().getKits()){
			if(kits.getNome().equalsIgnoreCase(kitname)){
				PlayersKits playersKits = null;
				for(PlayersKits pk : PlayersKits.playerKitsCache){
					if(pk.getName().equalsIgnoreCase(p.getName())){
						playersKits = pk;
						break;
					}
				}
				if(playersKits == null) playersKits = new PlayersKits(p.getName());
				String log = "KitName: " + kitname;
				boolean delay;
				if(giver == null)
					delay = !p.hasPermission("fkitspro.nodelay");
				else
					delay = false;
				log += " <|> Player tem nodelay: " + delay;
				long pegou = 0;
				if(delay){
					pegou = playersKits.getUsedKit(kits.getNome());
					log += " <|> Pegou: " + pegou;
					if(pegou > 0){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde").replace("&", "§")
								.replace("{tempo}", instance.getTime(pegou-System.currentTimeMillis())));
						if(instance.getConfig().getBoolean("Config.Ativar_Logs"))
							instance.log(p.getName(), log);
						return;
					}else if(pegou == -2L){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.So_Pode_Uma_Vez").replace("&", "§"));
						if(instance.getConfig().getBoolean("Config.Ativar_Logs"))
							instance.log(p.getName(), log);
						return;
					}
				}
				if(giver == null && !instance.econ.has(p.getName(), kits.getPreco())){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Dinheiro").replace("&", "§"));
					return;
				}
				if(delay){
					if(kits.getTempo() == 0){
						playersKits.addDelay(kits.getNome(), -2L);
						log += " <|> Kit perma.";
					}else{
						long newPegou = System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(kits.getTempo());
						playersKits.addDelay(kits.getNome(), newPegou);
						log += " <|> Novo Pegou: " + newPegou;
					}
				}
				if(giver == null){
					instance.econ.withdrawPlayer(p.getName(), kits.getPreco());
					if(kits.getPreco() > 0)
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Pagou_Kit").replace("&", "§").replace("{kit}", kitname)
								.replace("{preco}", NumberFormat.getCurrencyInstance().format(kits.getPreco()).replace("$", "")));
				}
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Pegou_Kit").replace("&", "§").replace("{kit}", kitname));
				if(instance.getConfig().getBoolean("Config.Ativar_Logs"))
					instance.log(p.getName(), log);
				for(ItemStack item : kits.getItems()){
					if(p.getInventory().firstEmpty() != -1){
						p.getInventory().setItem(p.getInventory().firstEmpty(), item);
					}else{
						if(item != null) p.getWorld().dropItemNaturally(p.getLocation(), item).setVelocity(p.getLocation().getDirection());
					}
				}
				return;
			}
		}
	}

	public Object criarItem(String string){
		Object item = null;
		String[] partes = string.split(" ");
		int qnt = 1;
		String tipo = "";
		boolean splash = false;
		int tempo = 1;
		int amplificador = 1;
		try{
			for(String parte : partes){
				if(parte.toLowerCase().startsWith("item:")){
					String[] id = parte.split("item:");
					if(id[1].contains(":"))
						item = new ItemStack(Material.getMaterial(Integer.parseInt(id[1].split(":")[0])), qnt, Byte.parseByte(id[1].split(":")[1]));
					else
						item = new ItemStack(Material.getMaterial(Integer.parseInt(id[1].split(":")[0])), qnt);
				}else if(parte.toLowerCase().startsWith("pocao:")){
					String[] id = parte.split("pocao:");
					tipo = traduzirPocao(id[1].toLowerCase());
					item = new Potion(PotionType.getByEffect(PotionEffectType.getByName(tipo)));
					for(String p : partes){
						if(p.toLowerCase().startsWith("splash:")){
							splash = Boolean.parseBoolean(p.toLowerCase().split("splash:")[1]);
							if(splash) ((Potion) item).splash();
						}else if(p.toLowerCase().startsWith("duracao:")){
							tempo = Integer.parseInt(p.toLowerCase().split("duracao:")[1]);
						}else if(p.toLowerCase().startsWith("amplificador:")){
							amplificador = Integer.parseInt(p.toLowerCase().split("amplificador:")[1]);
						}
					}
					item = ((Potion) item).toItemStack(qnt);
					PotionMeta pm = (PotionMeta) ((ItemStack) item).getItemMeta();
					pm.addCustomEffect(new PotionEffect(PotionEffectType.getByName(tipo), tempo*20, amplificador - 1), splash);
					((ItemStack) item).setItemMeta(pm);
				}else if(parte.toLowerCase().startsWith("enchants:")){
					String[] enchants = parte.split("enchants:");
					for(String enchant : enchants[1].split(",")){
						String[] partesenchant = enchant.split(":");
						((ItemStack) item).addUnsafeEnchantment(Enchantment.getByName(traduzirEnchant(partesenchant[0])), Integer.parseInt(partesenchant[1]));
					}
				}else if(parte.toLowerCase().startsWith("nome:")){
					ItemMeta im = ((ItemStack) item).getItemMeta();
					im.setDisplayName(parte.split("nome:")[1].replace("_", " ").replace("&", "§"));
					((ItemStack) item).setItemMeta(im);
				}else if(parte.toLowerCase().startsWith("lore:")){
					List<String> lore = new ArrayList<String>();
					for(String l : parte.split("(?i)lore:")[1].split("@")){
						lore.add(l.replace("_", " ").replace("&", "§"));
					}
					ItemMeta im = ((ItemStack) item).getItemMeta();
					im.setLore(lore);
					((ItemStack) item).setItemMeta(im);
				}else if(parte.toLowerCase().startsWith("qnt:")){
					qnt = Integer.parseInt(parte.toLowerCase().split("qnt:")[1]);
					if(item.getClass().equals(ItemStack.class)){
						((ItemStack) item).setAmount(qnt);
					}else{
						((Potion) item).toItemStack(qnt);
					}
				}
			}
		}catch(Exception e){}
		return item;
	}

	public String traduzirEnchant(String enchant){
		String en = "";
		switch(enchant.toLowerCase()){
		case "protecao":
			en = "PROTECTION_ENVIRONMENTAL";
			break;
		case "protecao_fogo":
			en = "PROTECTION_FIRE";
			break;
		case "protecao_queda":
			en = "PROTECTION_FALL";
			break;
		case "protecao_explosao":
			en = "PROTECTION_EXPLOSIONS";
			break;
		case "protecao_flecha":
			en = "PROTECTION_PROJECTILE";
			break;
		case "respiracao":
			en = "OXYGEN";
			break;
		case "afinidade_aquatica":
			en = "WATER_WORKER";
			break;
		case "espinhos":
			en = "THORNS";
			break;
		case "afiada":
			en = "DAMAGE_ALL";
			break;
		case "julgamento":
			en = "DAMAGE_UNDEAD";
			break;
		case "ruina_artropodes":
			en = "DAMAGE_ARTHROPODS";
			break;
		case "repulsao":
			en = "KNOCKBACK";
			break;
		case "aspecto_flamejante":
			en = "FIRE_ASPECT";
			break;
		case "pilhagem":
			en = "LOOT_BONUS_MOBS";
			break;
		case "eficiencia":
			en = "DIG_SPEED";
			break;
		case "toque_suave":
			en = "SILK_TOUCH";
			break;
		case "inquebravel":
			en = "DURABILITY";
			break;
		case "fortuna":
			en = "LOOT_BONUS_BLOCKS";
			break;
		case "forca":
			en = "ARROW_DAMAGE";
			break;
		case "impacto":
			en = "ARROW_KNOCKBACK";
			break;
		case "chama":
			en = "ARROW_FIRE";
			break;
		case "infinidade":
			en = "ARROW_INFINITE";
			break;
		}
		return en;
	}

	public String traduzirPocao(String pocao){
		String po = "";
		switch(pocao.toLowerCase()){
		case "velocidade":
			po = "SPEED";
			break;
		case "forca":
			po = "INCREASE_DAMAGE";
			break;
		case "lentidao":
			po = "SLOW";
			break;
		case "escavar-rapido":
			po = "FAST_DIGGING";
			break;
		case "escavar-lento":
			po = "SLOW_DIGGING";
			break;
		case "vida-instantanea":
			po = "HEAL";
			break;
		case "dano-instantaneo":
			po = "HARM";
			break;
		case "pulo":
			po = "JUMP";
			break;
		case "nausea":
			po = "CONFUSION";
			break;
		case "regeneracao":
			po = "REGENERATION";
			break;
		case "resistencia":
			po = "DAMAGE_RESISTANCE";
			break;
		case "resistencia-fogo":
			po = "FIRE_RESISTANCE";
			break;
		case "resistencia-agua":
			po = "WATER_BREATHING";
			break;
		case "invisibilidade":
			po = "INVISIBILITY";
			break;
		case "cegueira":
			po = "BLINDNESS";
			break;
		case "visao-noturna":
			po = "NIGHT_VISION";
			break;
		case "fome":
			po = "HUNGER";
			break;
		case "fraqueza":
			po = "WEAKNESS";
			break;
		case "veneno":
			po = "POISON";
			break;
		}
		return po;
	}

}

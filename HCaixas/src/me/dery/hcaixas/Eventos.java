package me.dery.hcaixas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Eventos implements Listener{
	
	private HCaixas instance = HCaixas.getHCaixas();
	private HashMap<String, BukkitTask> id = new HashMap<String, BukkitTask>();
	private HashMap<String, Integer> qual = new HashMap<String, Integer>();
	private HashMap<String, String> playercaixa = new HashMap<String, String>();
	private ArrayList<ItemStack> items = new ArrayList<ItemStack>();
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGH)
	public void Interact(PlayerInteractEvent e){
		Player p = e.getPlayer();
		ItemStack item = p.getItemInHand();
		if(item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().hasLore()) return;
		ItemMeta itemmeta = item.getItemMeta();
		for(String caixaname : instance.getConfig().getConfigurationSection("Caixa").getKeys(false)){
			if(instance.getConfig().getString("Caixa." + caixaname + ".Nome").replace("&", "§").equals(itemmeta.getDisplayName().replace("&", "§"))){
				for(int i = 0; i<instance.getConfig().getStringList("Caixa." + caixaname + ".Lore").size(); i++){
					if(!itemmeta.getLore().get(i).replace("&", "§").equalsIgnoreCase(instance.getConfig().getStringList("Caixa." + caixaname + ".Lore").get(i).replace("&", "§"))){
						return;
					}
				}
				if(!playercaixa.containsKey(p.getName())){
					playercaixa.put(p.getName(), p.getItemInHand().getType().name());
				}
				e.setCancelled(true);
				p.updateInventory();
				Inventory inv = instance.getServer().createInventory(null, 3*9, instance.getConfig().getString("Inv_Nome").replace("&", "§"));
				ItemStack dima = new ItemStack(Material.DIAMOND_BLOCK);
				ItemMeta dimameta = dima.getItemMeta();
				dimameta.setDisplayName("§6" + caixaname);
				dima.setItemMeta(dimameta);
				inv.setItem(13, dima);
				ItemStack verde = new ItemStack(Material.WOOL, 1, (byte) 5);
				ItemMeta verdemeta = verde.getItemMeta();
				verdemeta.setDisplayName("§aRoletar caixa");
				verde.setItemMeta(verdemeta);
				inv.setItem(25, verde);
				ItemStack vermelho = new ItemStack(Material.WOOL, 1, (byte) 14);
				ItemMeta vermelhometa = vermelho.getItemMeta();
				vermelhometa.setDisplayName("§cFechar caixa");
				vermelho.setItemMeta(vermelhometa);
				inv.setItem(26, vermelho);
				for(int i = 0; i<3*9; i++){
					if(inv.getItem(i) == null){
						inv.setItem(i, new ItemStack(Material.getMaterial(instance.getConfig().getInt("Completar_ID"))));
					}
				}
				p.openInventory(inv);
				p.playSound(p.getLocation(), Sound.CHEST_OPEN, 5.0F, 1.0F);
				break;
			}
		}
	}
	
	@EventHandler
	public void Close(InventoryCloseEvent e){
		Player p = (Player) e.getPlayer();
		Inventory inv = e.getInventory();
		if(inv.getTitle().replace("&", "§").equalsIgnoreCase(instance.getConfig().getString("Inv_Nome").replace("&", "§"))){
			if(id.get(p.getName()) != null){
				id.get(p.getName()).cancel();
				qual.remove(p.getName());
				id.remove(p.getName());
				playercaixa.remove(p.getName());
				p.sendMessage(instance.getConfig().getString("Cancelou_Caixa").replace("&", "§"));
			}
		}
	}
	
	@EventHandler
	public void Quit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(p.getOpenInventory() != null){
			InventoryView inv = p.getOpenInventory();
			if(inv.getTitle().replace("&", "§").equalsIgnoreCase(instance.getConfig().getString("Inv_Nome").replace("&", "§"))){
				inv.close();
				if(id.get(p.getName()) != null) id.get(p.getName()).cancel();
				qual.remove(p.getName());
				id.remove(p.getName());
				playercaixa.remove(p.getName());
			}
		}
	}
	
	@EventHandler
	public void Quit(PlayerKickEvent e){
		Player p = e.getPlayer();
		if(p.getOpenInventory() != null){
			InventoryView inv = p.getOpenInventory();
			if(inv.getTitle().replace("&", "§").equalsIgnoreCase(instance.getConfig().getString("Inv_Nome").replace("&", "§"))){
				inv.close();
				if(id.get(p.getName()) != null) id.get(p.getName()).cancel();
				qual.remove(p.getName());
				id.remove(p.getName());
				playercaixa.remove(p.getName());
			}
		}
	}
	
	@EventHandler
	public void Drop(PlayerDropItemEvent e){
		Player p = e.getPlayer();
		if(p.getOpenInventory() == null) return;
		InventoryView inv = p.getOpenInventory();
		if(inv.getTitle().replace("&", "§").equalsIgnoreCase(instance.getConfig().getString("Inv_Nome").replace("&", "§"))){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void Click(InventoryClickEvent e){
		final Player p = (Player) e.getWhoClicked();
		final Inventory inv = e.getInventory();
		if(inv.getTitle().replace("&", "§").equalsIgnoreCase(instance.getConfig().getString("Inv_Nome").replace("&", "§"))){
			e.setCancelled(true);
			if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
			ItemStack itemclicked = e.getCurrentItem();
			if(!(itemclicked.hasItemMeta() || itemclicked.getItemMeta().hasDisplayName() || itemclicked.getItemMeta().hasLore())) return;
			ItemMeta itemmeta = itemclicked.getItemMeta();
			if(itemmeta.getDisplayName().equalsIgnoreCase("§cFechar caixa")){
				p.getOpenInventory().close();
				p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1.0F, 1.0F);
			}else if(itemmeta.getDisplayName().equalsIgnoreCase("§aRoletar caixa")){
				items.clear();
				final String caixa = inv.getItem(13).getItemMeta().getDisplayName().replace("§6", "");
				final Random r = new Random();
				for(String itens : instance.getConfig().getStringList("Caixa." + caixa + ".Items")){
					String[] partes = itens.split("%");
					String item = partes[0];
					int quantidade = Integer.parseInt(partes[1]);
					ItemStack i = null;
					if(item.contains(":")){
						String id = item.split(":")[0];
						int data = Integer.parseInt(item.split(":")[1]);
						if(Material.matchMaterial(id) != null){
							i = new ItemStack(Material.getMaterial(Integer.parseInt(id)), quantidade, (byte) data);
						}
					}else{
						if(Material.matchMaterial(item) != null){
							i = new ItemStack(Material.getMaterial(Integer.parseInt(item)), quantidade);
						}
					}
					String enchants = partes[2];
					if(!enchants.replaceAll("\\s+", "").equals("")){
						String[] penchants = enchants.split("-");
						for(String en : penchants){
							String name = replaceEnchant(en.split(":")[0]);
							int level = Integer.parseInt(en.split(":")[1]);
							i.addUnsafeEnchantment(Enchantment.getByName(name), level);
						}
					}
					if(!partes[3].replaceAll("\\s+", "").equals("")){
						String nome = partes[3];
						ItemMeta im = i.getItemMeta();
						im.setDisplayName(nome.replace("&", "§"));
						i.setItemMeta(im);
					}
					if(!partes[4].replaceAll("\\s+", "").equals("")){
						String[] plore = partes[4].split("_");
						List<String> lore = new ArrayList<String>();
						for(String lores : plore){
							lore.add(lores.replace("&", "§"));
						}
						ItemMeta im = i.getItemMeta();
						im.setLore(lore);
						i.setItemMeta(im);
					}
					items.add(i);
				}
				final int itemsorteado = r.nextInt(items.size());
				if(!qual.containsKey(p.getName())){
					qual.put(p.getName(), 0);
				}
				ItemStack itemm = new ItemStack(Material.BEDROCK);
				ItemMeta itemmetaa = itemm.getItemMeta();
				itemmetaa.setDisplayName("§e§lCAIXA JA USADA!");
				itemm.setItemMeta(itemmetaa);
				e.setCurrentItem(itemm);
				if(!id.containsKey(p.getName())){
					id.put(p.getName(), new BukkitRunnable() {
						@Override
						public void run(){
							if(qual.get(p.getName()) <= 50){
								inv.setItem(13, items.get(r.nextInt(items.size())));
								qual.put(p.getName(), qual.get(p.getName()) + 1);
								p.playSound(p.getLocation(), Sound.NOTE_PLING, 5.0F, 1.0F);
							}else{
								qual.remove(p.getName());
								inv.setItem(13, items.get(itemsorteado));
								if(p.getInventory().firstEmpty() == -1){
									p.getLocation().getWorld().dropItem(p.getLocation(), items.get(itemsorteado).clone());
								}else{
									p.getInventory().addItem(items.get(itemsorteado));
								}
								p.playSound(p.getLocation(), Sound.LEVEL_UP, 5.0F, 1.0F);
								for(Player player : instance.getServer().getOnlinePlayers()){
									player.sendMessage(instance.getConfig().getString("Roletou").replace("&", "§").replace("@player", p.getName()).replace("@caixa", caixa.substring(0, 1).toUpperCase() + caixa.substring(1).toLowerCase()));
								}
								id.get(p.getName()).cancel();
								id.remove(p.getName());
								retirarItem(p, playercaixa.get(p.getName()));
								playercaixa.remove(p.getName());
							}
						}
					}.runTaskTimer(instance, 0, 1));
				}
			}
 		}
	}
	
	@SuppressWarnings("deprecation")
	public void retirarItem(Player p, String material){
		for(int i = 0; i<p.getInventory().getSize(); i++){
			ItemStack item = p.getInventory().getItem(i);
			if(item != null && item.getType().name().equalsIgnoreCase(material)){
				if(item.getAmount() - 1 == 0){
					p.getInventory().setItem(i, null);
				}else{
					p.getInventory().getItem(i).setAmount(item.getAmount() - 1);
				}
				p.updateInventory();
				return;
			}
		}
	}
	
	public static String replaceEnchant(String enchant){
		switch(enchant.toLowerCase()){
		case "protecao":
			enchant = "PROTECTION_ENVIRONMENTAL";
			break;
		case "protecao_fogo":
			enchant = "PROTECTION_FIRE";
			break;
		case "protecao_queda":
			enchant = "PROTECTION_FALL";
			break;
		case "protecao_explosao":
			enchant = "PROTECTION_EXPLOSIONS";
			break;
		case "protecao_flecha":
			enchant = "PROTECTION_PROJECTILE";
			break;
		case "respiracao":
			enchant = "OXYGEN";
			break;
		case "afinidade_aquatica":
			enchant = "WATER_WORKER";
			break;
		case "espinhos":
			enchant = "THORNS";
			break;
		case "afiada":
			enchant = "DAMAGE_ALL";
			break;
		case "julgamento":
			enchant = "DAMAGE_UNDEAD";
			break;
		case "ruina_artropodes":
			enchant = "DAMAGE_ARTHROPODS";
			break;
		case "repulsao":
			enchant = "KNOCKBACK";
			break;
		case "aspecto_flamejante":
			enchant = "FIRE_ASPECT";
			break;
		case "pilhagem":
			enchant = "LOOT_BONUS_MOBS";
			break;
		case "eficiencia":
			enchant = "DIG_SPEED";
			break;
		case "toque_suave":
			enchant = "SILK_TOUCH";
			break;
		case "inquebravel":
			enchant = "DURABILITY";
			break;
		case "fortuna":
			enchant = "LOOT_BONUS_BLOCKS";
			break;
		case "forca":
			enchant = "ARROW_DAMAGE";
			break;
		case "impacto":
			enchant = "ARROW_KNOCKBACK";
			break;
		case "chama":
			enchant = "ARROW_FIRE";
			break;
		case "infinidade":
			enchant = "ARROW_INFINITE";
			break;
		}
		return enchant;
	}

}

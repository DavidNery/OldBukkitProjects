package me.zfork.spartanoscraft.spartanosinvrestorer.listeners;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import me.zfork.spartanoscraft.spartanosinvrestorer.SpartanosInvRestorer;
import me.zfork.spartanoscraft.spartanosinvrestorer.utils.PlayerInventory;
import me.zfork.spartanoscraft.spartanosinvrestorer.utils.PlayerInventoryUtils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;

public class Listeners implements Listener {
	
	private SpartanosInvRestorer instance;
	private PlayerInventoryUtils playerInventoryUtils;
	
	public Listeners(SpartanosInvRestorer instance) {
		this.instance = instance;
		this.playerInventoryUtils = instance.getPlayerInventoryUtils();
		
		instance.getServer().getPluginManager().registerEvents(this, instance);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void Death(PlayerDeathEvent e){
		Player p = e.getEntity();
		double preco = 0;
		boolean temItemRaro = false;
		for(ItemStack item : p.getInventory().getContents()){
			if(item != null && !item.getType().equals(Material.AIR)){
				if(instance.getConfig().getConfigurationSection("Config.Custom_Prices").getKeys(false).contains(item.getTypeId()+"")){
					for(String list : instance.getConfig().getStringList("Config.Custom_Prices." + item.getTypeId() + ".Custom_Price_List")){
						String[] partes = list.split(":");
						if(partes[0].equalsIgnoreCase("enchant")){
							String[] value = partes[1].split(" ");
							if(item.containsEnchantment(Enchantment.getByName(instance.traduzirEnchant(value[0])))
									&& item.getEnchantmentLevel(Enchantment.getByName(instance.traduzirEnchant(value[0]))) == Integer.parseInt(value[1])){
								preco += Double.parseDouble(partes[2]);
								if(instance.getConfig().getBoolean("Config.Custom_Prices." + item.getTypeId() + ".Item_Raro"))
									temItemRaro = true;
							}
						}else if(partes[0].equalsIgnoreCase("nome")){
							if(item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
									item.getItemMeta().getDisplayName().equalsIgnoreCase(partes[1].replace("&", "§").replace("{player}", p.getName()))){
								preco += Double.parseDouble(partes[2]);
								if(instance.getConfig().getBoolean("Config.Custom_Prices." + item.getTypeId() + ".Item_Raro"))
									temItemRaro = true;
							}
						}else if(partes[0].equalsIgnoreCase("pocao")){
							if(item.getType().equals(Material.POTION) && instance.hasEffect(Potion.fromItemStack(item), PotionEffectType.getByName(instance.traduzirPocao(partes[1])))){
								preco += Double.parseDouble(partes[2]);
								if(instance.getConfig().getBoolean("Config.Custom_Prices." + item.getTypeId() + ".Item_Raro"))
									temItemRaro = true;
							}
						}
					}
				}else{
					preco += instance.getConfig().getDouble("Config.Preco_Default");
				}
			}
		}
		for(ItemStack item : p.getInventory().getArmorContents()){
			if(item != null && !item.getType().equals(Material.AIR)){
				if(instance.getConfig().getConfigurationSection("Config.Custom_Prices").getKeys(false).contains(item.getTypeId()+"")){
					for(String list : instance.getConfig().getStringList("Config.Custom_Prices." + item.getTypeId() + ".Custom_Price_List")){
						String[] partes = list.split(":");
						if(partes[0].equalsIgnoreCase("enchant")){
							String[] value = partes[1].split(" ");
							if(item.containsEnchantment(Enchantment.getByName(instance.traduzirEnchant(value[0])))
									&& item.getEnchantmentLevel(Enchantment.getByName(instance.traduzirEnchant(value[0]))) == Integer.parseInt(value[1])){
								preco += Double.parseDouble(partes[2]);
								if(instance.getConfig().getBoolean("Config.Custom_Prices." + item.getTypeId() + ".Item_Raro"))
									temItemRaro = true;
							}
						}else if(partes[0].equalsIgnoreCase("nome")){
							if(item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
									item.getItemMeta().getDisplayName().equalsIgnoreCase(partes[1].replace("&", "§").replace("{player}", p.getName()))){
								preco += Double.parseDouble(partes[2]);
								if(instance.getConfig().getBoolean("Config.Custom_Prices." + item.getTypeId() + ".Item_Raro"))
									temItemRaro = true;
							}
						}
					}
				}else{
					preco += instance.getConfig().getDouble("Config.Preco_Default");
				}
			}
		}
		if(preco >= 1){
			PlayerInventory playerInventory = new PlayerInventory(p.getName(), 
					System.currentTimeMillis()+TimeUnit.HOURS.toMillis(instance.getConfig().getInt("Config.Tempo_Expirar_Player_Inventory")), preco, false, temItemRaro);
			playerInventoryUtils.getSavedInventories().add(playerInventory);
			Inventory inventario = instance.getServer().createInventory(null, 54, "§7Inventário §c§l" + playerInventory.getId());
			for(int i = 0; i<p.getInventory().getSize(); i++){
				ItemStack itemstack = p.getInventory().getItem(i);
				if(itemstack != null)
					inventario.setItem(i, itemstack);
			}
			int i = 48;
			for(ItemStack armor : p.getInventory().getArmorContents()){
				if(armor != null){
					inventario.setItem(i, armor);
				}
				i--;
			}
			inventario.setItem(49, (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Inventories.Item_Info").replace("{preco}", playerInventory.getPreco()+"")));
			ItemStack comprar = (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Inventories.Item_Confirm"));
			ItemStack close = (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Inventories.Item_Close"));
			inventario.setItem(53, comprar);
			inventario.setItem(52, close);
			playerInventory.setPreviewInventory(inventario);
			playerInventoryUtils.addPlayerInventory(playerInventory);
			for(String msg : instance.getConfig().getStringList("Mensagem.Sucesso.Pode_Comprar_Inv"))
				p.sendMessage(msg.replace("&", "§"));
		}
	}
	
	@SuppressWarnings({ "deprecation" })
	@EventHandler
	public void Click(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
		if(inv.getTitle().matches("^§7Comprar Inventários §f- §c§l\\d+$")){
			e.setCancelled(true);
			if(e.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
			int slot = Integer.parseInt(inv.getTitle().split("§c§l")[1]);
			p.closeInventory();
			if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(playerInventoryUtils.getVoltar().getItemMeta().getDisplayName())){
				Inventory clonedInv = playerInventoryUtils.cloneInventory(new ArrayList<>(playerInventoryUtils.getInventarios()).get(slot-2));
				ItemStack item = playerInventoryUtils.getHead(p.getName());
				ItemMeta im = item.getItemMeta();
				im.setDisplayName("§7Seus inventários");
				item.setItemMeta(im);
				clonedInv.setItem(49, item);
				p.openInventory(clonedInv);
			}else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(playerInventoryUtils.getProximo().getItemMeta().getDisplayName())){
				Inventory clonedInv = playerInventoryUtils.cloneInventory(new ArrayList<>(playerInventoryUtils.getInventarios()).get(slot));
				ItemStack item = playerInventoryUtils.getHead(p.getName());
				ItemMeta im = item.getItemMeta();
				im.setDisplayName("§7Seus inventários");
				item.setItemMeta(im);
				clonedInv.setItem(49, item);
				p.openInventory(clonedInv);
			}else if(e.getSlot() == 49){
				try{
					Inventory inventario = playerInventoryUtils.getPlayersInventories().get(p.getName()).get(0);
					p.openInventory(inventario);
				}catch(Exception e1){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Inventarios").replace("&", "§"));
				}
			}else{
				Inventory inventario = new ArrayList<>(playerInventoryUtils.getPlayersInventoriesGeral().get(((SkullMeta) e.getCurrentItem().getItemMeta()).getOwner())).get(0);
				p.openInventory(inventario);
			}
		}else if(inv.getTitle().matches("^§7Inventários §f- §c§l\\w+ §f- §c§l\\d+$")){
			e.setCancelled(true);
			if(e.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
			String player = inv.getTitle().split("§7Inventários §f- §c§l")[1].split(" §f- §c§l")[0];
			int slot = Integer.parseInt(inv.getTitle().split("§7Inventários §f- §c§l\\w+ §f- §c§l")[1]);
			p.closeInventory();
			if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(playerInventoryUtils.getVoltar().getItemMeta().getDisplayName())){
				if(player.equalsIgnoreCase(p.getName()))
					p.openInventory(new ArrayList<>(playerInventoryUtils.getPlayersInventories().get(player)).get(slot-2));
				else
					p.openInventory(new ArrayList<>(playerInventoryUtils.getPlayersInventoriesGeral().get(player)).get(slot-2));
			}else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(playerInventoryUtils.getProximo().getItemMeta().getDisplayName())){
				if(player.equalsIgnoreCase(p.getName()))
					p.openInventory(new ArrayList<>(playerInventoryUtils.getPlayersInventories().get(player)).get(slot));
				else
					p.openInventory(new ArrayList<>(playerInventoryUtils.getPlayersInventoriesGeral().get(player)).get(slot));
			}else if(e.getClick().equals(ClickType.RIGHT)){
				int id = Integer.parseInt(e.getCurrentItem().getItemMeta().getDisplayName().split("§c§l")[1]);
				for(PlayerInventory inventory : new ArrayList<>(playerInventoryUtils.getSavedInventories()))
					if(inventory.getId() == id){
						p.openInventory(inventory.getPreviewInventory());
						break;
					}
			}else{
				double preco = 1;
				int id = Integer.parseInt(e.getCurrentItem().getItemMeta().getDisplayName().split("§c§l")[1]);
				for(PlayerInventory playerInventory : playerInventoryUtils.getSavedInventories()){
					if(id == playerInventory.getId()){
						preco = playerInventory.getPreco();
						break;
					}
				}
				Inventory buyInventory = instance.getServer().createInventory(null, InventoryType.HOPPER, "§7Comprar §c§l" + e.getCurrentItem().getItemMeta().getDisplayName().split("§c§l")[1]);
				ItemStack comprar = (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Inventories.Item_Confirm"));
				ItemStack close = (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Inventories.Item_Close"));
				ItemStack info = (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Inventories.Item_Info").replace("{preco}", preco+""));
				buyInventory.setItem(0, comprar);
				buyInventory.setItem(2, info);
				buyInventory.setItem(4, close);
				p.openInventory(buyInventory);
			}
		}else if(inv.getTitle().matches("^§7Inventário §c§l\\d+$")){
			if(e.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
			e.setCancelled(true);
			if(e.getSlot() == 52){
				p.getOpenInventory().close();
			}else if(e.getSlot() == 53){
				int id = Integer.parseInt(inv.getTitle().split("§c§l")[1]);
				PlayerInventory playerInventory = null;
				for(PlayerInventory playerInventories : playerInventoryUtils.getSavedInventories()){
					if(id == playerInventories.getId()){
						playerInventory = playerInventories;
						break;
					}
				}
				if(playerInventory == null){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inventario_Comprado").replace("&", "§"));
				}else{
					Inventory inventory = playerInventory.getPreviewInventory();
					double preco = playerInventory.getPreco();
					if(instance.getPlayerPointsAPI().look(p.getName()) >= preco){
						if(!playerInventoryUtils.isVazio(p)){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inventario_Vazio").replace("&", "§"));
							return;
						}
						for(int i = 0; i<36; i++){
							ItemStack item = inventory.getItem(i);
							if(item != null) p.getInventory().setItem(i, item);
						}
						if(inventory.getItem(45) != null) p.getInventory().setHelmet(inventory.getItem(45));
						if(inventory.getItem(46) != null) p.getInventory().setChestplate(inventory.getItem(46));
						if(inventory.getItem(47) != null) p.getInventory().setLeggings(inventory.getItem(47));
						if(inventory.getItem(48) != null) p.getInventory().setBoots(inventory.getItem(48));
						instance.getPlayerPointsAPI().take(p.getName(), (int) playerInventory.getPreco());
						p.closeInventory();
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Comprou")
								.replace("{id}", playerInventory.getId()+"").replace("{player}", playerInventory.getOwnerName())
								.replace("{preco}", NumberFormat.getCurrencyInstance().format(playerInventory.getPreco()).replace("$", "")).replace("&", "§"));
						playerInventoryUtils.removePlayerInventory(playerInventory);
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Money").replace("&", "§"));
					}
				}
			}
		}else if(inv.getTitle().matches("^§7Comprar §c§l\\d+$")){
			if(e.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
			e.setCancelled(true);
			if(e.getSlot() == 0){
				int id = Integer.parseInt(inv.getTitle().split("§c§l")[1]);
				PlayerInventory playerInventory = null;
				for(PlayerInventory playerInventories : playerInventoryUtils.getSavedInventories()){
					if(id == playerInventories.getId()){
						playerInventory = playerInventories;
						break;
					}
				}
				if(playerInventory == null){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inventario_Comprado").replace("&", "§"));
				}else{
					if(!p.getName().equalsIgnoreCase(playerInventory.getOwnerName()) && !playerInventory.getPodeComprar()){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Ainda_Nao_Pode_Comprar").replace("&", "§"));
						return;
					}
					Inventory inventory = playerInventory.getPreviewInventory();
					double preco = playerInventory.getPreco();
					if(instance.getPlayerPointsAPI().look(p.getName()) >= preco){
						if(!playerInventoryUtils.isVazio(p)){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inventario_Vazio").replace("&", "§"));
							return;
						}
						for(int i = 0; i<36; i++){
							ItemStack item = inventory.getItem(i);
							if(item != null) p.getInventory().setItem(i, item);
						}
						if(inventory.getItem(45) != null) p.getInventory().setHelmet(inventory.getItem(45));
						if(inventory.getItem(46) != null) p.getInventory().setChestplate(inventory.getItem(46));
						if(inventory.getItem(47) != null) p.getInventory().setLeggings(inventory.getItem(47));
						if(inventory.getItem(48) != null) p.getInventory().setBoots(inventory.getItem(48));
						instance.getPlayerPointsAPI().take(p.getName(), (int) playerInventory.getPreco());
						p.closeInventory();
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Comprou")
								.replace("{id}", playerInventory.getId()+"").replace("{player}", playerInventory.getOwnerName())
								.replace("{preco}", NumberFormat.getCurrencyInstance().format(playerInventory.getPreco()).replace("$", "")).replace("&", "§"));
						playerInventoryUtils.removePlayerInventory(playerInventory);
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Money").replace("&", "§"));
					}
				}
			}else if(e.getSlot() == 4){
				p.getOpenInventory().close();
			}
		}
	}
	
	@EventHandler
	public void Drop(PlayerDropItemEvent e){
		Player p = e.getPlayer();
		if(p.getOpenInventory() != null){
			InventoryView inv = p.getOpenInventory();
			if(inv.getTitle().matches("(§7Inventarios §f- §c§l\\w+ §f- §c§l\\d+)|(§7Comprar Inventarios §f- §c§l\\d+)|(§7Comprar Inventarios §f- §c§l\\d+)"
					+ "|(§7Inventario §c§l\\d+)"))
				e.setCancelled(true);
		}
	}

}

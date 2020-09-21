package me.zfork.landmasks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Events implements Listener{

	private LandMasks instance = LandMasks.getLandMasks();

	@EventHandler
	public void Click(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		ItemStack item = e.getCurrentItem();
		if(inv.getTitle().equalsIgnoreCase(instance.getConfig().getString("Nome_Inventario").replace("&", "§"))
				&& item != null && item.getType() == Material.SKULL_ITEM && item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
			e.setCancelled(true);
			for(String s : instance.getConfig().getConfigurationSection("Mascaras").getKeys(false)){
				if(item.getItemMeta().getDisplayName().equalsIgnoreCase(instance.getConfig().getString("Mascaras." + s + ".Nome").replace("&", "§"))){
					if(hasItem(p.getInventory(), item, instance.getConfig().getInt("Mascaras." + s + ".Quantidade"))){
						if(p.getInventory().firstEmpty() == -1){
							p.sendMessage(instance.getConfig().getString("Inventario_Cheio").replace("&", "§"));
						}else{
							removeHead(p, item, instance.getConfig().getInt("Mascaras." + s + ".Quantidade"));
							p.getInventory().addItem(item);
							p.sendMessage(instance.getConfig().getString("Pegou_Mascara").replace("&", "§")
									.replace("{qnt}", instance.getConfig().getInt("Mascaras." + s + ".Quantidade") + ""));
						}
						return;
					}else{
						p.sendMessage(instance.getConfig().getString("Sem_Cabecas_Suficientes").replace("&", "§")
								.replace("{qnt}", instance.getConfig().getInt("Mascaras." + s + ".Quantidade") + ""));
					}
					return;
				}
			}
		}
	}

	@EventHandler
	public void Click(InventoryMoveItemEvent e){
		if(e.getInitiator().getTitle().equalsIgnoreCase(instance.getConfig().getString("Nome_Inventario").replace("&", "§"))
				|| e.getDestination().getTitle().equalsIgnoreCase(instance.getConfig().getString("Nome_Inventario").replace("&", "§"))){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void Click(InventoryInteractEvent e){
		if(e.getInventory().getTitle().equalsIgnoreCase(instance.getConfig().getString("Nome_Inventario").replace("&", "§"))){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public final void onInventoryClick(final InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		boolean shift = false, numberkey = false;
		if(e.isCancelled()) return;
		if(e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) shift = true;
		if(e.getClick() == ClickType.NUMBER_KEY) numberkey = true;
		if(e.getSlotType() != SlotType.ARMOR && e.getSlotType() != SlotType.QUICKBAR && e.getSlotType() != SlotType.CONTAINER) return;
		if(e.getClickedInventory() != null && e.getClickedInventory().getType() != InventoryType.PLAYER) return;
		if(e.getInventory().getType() != InventoryType.CRAFTING && e.getInventory().getType() != InventoryType.PLAYER) return;
		if(!(e.getWhoClicked() instanceof Player)) return;
		if(e.getCurrentItem() == null) return;
		ItemStack item = shift ? e.getCurrentItem() : e.getCursor();
		if((!shift) && (item != null) && e.getRawSlot() != 5) return;
		ItemStack newArmorPiece = e.getCursor();
		ItemStack oldArmorPiece = e.getCurrentItem();
		if(numberkey){
			if(e.getClickedInventory().getType().equals(InventoryType.PLAYER)){
				ItemStack hotbarItem = e.getClickedInventory().getItem(e.getHotbarButton());
				if(hotbarItem != null){
					newArmorPiece = hotbarItem;
					oldArmorPiece = e.getClickedInventory().getItem(e.getSlot());
				}else{
					item = e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR ? e.getCurrentItem() : e.getCursor();
				}
			}
		}else{
			item = e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR ? e.getCurrentItem() : e.getCursor();
		}
		if(item != null && e.getRawSlot() == 5){
			if(newArmorPiece != null && newArmorPiece.getType() == Material.SKULL_ITEM){
				SkullMeta skull = (SkullMeta) newArmorPiece.getItemMeta();
				if(skull.hasOwner() && skull.getOwner().startsWith("MHF_")){
					String mascara = skull.getOwner().split("MHF_")[1];
					if(mascara.equalsIgnoreCase("PIG")){
						if(isMask(newArmorPiece, "PIG"))
							p.sendMessage(instance.getConfig().getString("Pig").replace("&", "§"));
					}else if(mascara.equalsIgnoreCase("OCELOT")){
						if(isMask(newArmorPiece, "OCELOT")){
							p.setWalkSpeed(Float.valueOf(instance.getConfig().getInt("Mascaras.OCELOT.Velocidade"))/10);
							p.sendMessage(instance.getConfig().getString("Ocelot").replace("&", "§"));
						}
					}else if(mascara.equalsIgnoreCase("BLAZE")){
						if(isMask(newArmorPiece, "BLAZE"))
							p.sendMessage(instance.getConfig().getString("Blaze").replace("&", "§"));
					}else if(mascara.equalsIgnoreCase("GOLEM")){
						if(isMask(newArmorPiece, "GOLEM")){
							p.sendMessage(instance.getConfig().getString("Golem").replace("&", "§"));
							p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));
						}
					}else if(mascara.equalsIgnoreCase("ZOMBIE")){
						if(isMask(newArmorPiece, "ZOMBIE"))
							p.sendMessage(instance.getConfig().getString("Zombie").replace("&", "§"));
					}else if(mascara.equalsIgnoreCase("CAVESPIDER")){
						if(isMask(newArmorPiece, "CAVESPIDER")){
							p.sendMessage(instance.getConfig().getString("CaveSpider").replace("&", "§"));
							p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
						}
					}
				}
				if(oldArmorPiece != null && oldArmorPiece.getType() == Material.SKULL_ITEM){
					skull = (SkullMeta) oldArmorPiece.getItemMeta();
					if(skull.hasOwner() && skull.getOwner().startsWith("MHF_")){
						if(isMask(oldArmorPiece, "GOLEM")){
							p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
						}else if(isMask(oldArmorPiece, "CAVESPIDER")){
							p.removePotionEffect(PotionEffectType.NIGHT_VISION);
						}else if(isMask(oldArmorPiece, "OCELOT")){
							p.setWalkSpeed(Float.valueOf(instance.getConfig().getInt("Mascaras.OCELOT.Velocidade_Default"))/10);
						}
					}
				}
			}else if(oldArmorPiece != null && oldArmorPiece.getType() == Material.SKULL_ITEM){
				SkullMeta skull = (SkullMeta) oldArmorPiece.getItemMeta();
				if(skull.hasOwner() && skull.getOwner().startsWith("MHF_")){
					if(isMask(oldArmorPiece, "GOLEM")){
						p.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
					}else if(isMask(oldArmorPiece, "CAVESPIDER")){
						p.removePotionEffect(PotionEffectType.NIGHT_VISION);
					}else if(isMask(oldArmorPiece, "OCELOT")){
						p.setWalkSpeed(Float.valueOf(instance.getConfig().getInt("Mascaras.OCELOT.Velocidade_Default"))/10);
					}
				}
			}
		}
	}

	@EventHandler
	public void Fome(FoodLevelChangeEvent e){
		Player p = (Player) e.getEntity();
		ItemStack item = p.getInventory().getHelmet();
		if(item != null && item.getType() == Material.SKULL_ITEM && item.hasItemMeta()){
			SkullMeta sm = (SkullMeta) item.getItemMeta();
			if(isMask(item, sm.getOwner().split("_")[1]))
				e.setCancelled(true);
		}
	}

	@EventHandler
	public void Dano(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			if(e.getCause().equals(DamageCause.FIRE) || e.getCause().equals(DamageCause.FIRE_TICK) || e.getCause().equals(DamageCause.LAVA)){
				ItemStack item = p.getInventory().getHelmet();
				if(item != null && item.getType() == Material.SKULL_ITEM && item.hasItemMeta()){
					SkullMeta sm = (SkullMeta) item.getItemMeta();
					if(isMask(item, sm.getOwner().split("_")[1])){
						e.setCancelled(true);
						p.setFireTicks(0);
					}
				}
			}
		}
	}

	@EventHandler
	public void Dano(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
			Player p = (Player) e.getEntity();
			ItemStack item = ((Player) e.getDamager()).getInventory().getHelmet();
			if(item != null && item.getType() == Material.SKULL_ITEM && item.hasItemMeta()){
				SkullMeta sm = (SkullMeta) item.getItemMeta();
				if(isMask(item, sm.getOwner().split("_")[1]) && (Math.random()*100 <= 1))
					p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*3, 0));
			}
		}
	}

	public boolean hasItem(Inventory inv, ItemStack item, int qnt){
		SkullMeta sm = (SkullMeta) item.getItemMeta();
		int i = 0;
		for(ItemStack is : inv.getContents()){
			if(is != null && is.getType() == Material.SKULL_ITEM){
				SkullMeta im = (SkullMeta) is.getItemMeta();
				if(sm.hasOwner() && im.hasOwner() && sm.getOwner().equals(im.getOwner()))
					i+=is.getAmount();
			}
		}
		return (i >= qnt ? true : false);
	}

	public void removeHead(Player p, ItemStack item, int qnt){
		int i = 0;
		SkullMeta sm = (SkullMeta) item.getItemMeta();
		for(int k = 0; k<36; k++){
			ItemStack is = p.getInventory().getItem(k);
			if(is != null && is.getType() == Material.SKULL_ITEM){
				if(i == qnt) return;
				SkullMeta im = (SkullMeta) is.getItemMeta();
				if(im.hasLore() && sm.hasLore() && im.getLore().equals(sm.getLore())) continue;
				if(sm.hasOwner() && im.hasOwner() && sm.getOwner().equals(im.getOwner())){
					if(is.getAmount() > qnt){
						is.setAmount(is.getAmount()-qnt);
						return;
					}else{
						if(is.getAmount() == qnt){
							p.getInventory().setItem(k, null);
						}else{
							if(is.getAmount()-1 == 0){
								p.getInventory().setItem(k, null);
								i++;
							}else{
								for(int j = is.getAmount(); j>0; j--){
									if(i == qnt) break;
									if(is.getAmount()-1 > 0){
										is.setAmount(is.getAmount()-1);
									}else{
										p.getInventory().setItem(k, null);
									}
									i++;
								}
							}
						}
						p.updateInventory();
					}
				}
			}
		}
	}
	
	public boolean isMask(ItemStack item, String type){
		if(item.getType() != Material.SKULL_ITEM) return false;
		SkullMeta skull = (SkullMeta) item.getItemMeta();
		if(skull.hasDisplayName() && skull.hasLore() && skull.hasOwner() && skull.getOwner().contains("_")){
			if(skull.getDisplayName().equalsIgnoreCase(instance.getConfig().getString("Mascaras." + type.toUpperCase() + ".Nome").replace("&", "§"))){
				for(int i = 0; i<instance.getConfig().getStringList("Mascaras." + type.toUpperCase() + ".Lore").size(); i++){
					if(!instance.getConfig().getStringList("Mascaras." + type.toUpperCase() + ".Lore").get(i).replace("&", "§").equals(skull.getLore().get(i)))
						return false;
				}
				return true;
			}
		}
		return false;
	}

}

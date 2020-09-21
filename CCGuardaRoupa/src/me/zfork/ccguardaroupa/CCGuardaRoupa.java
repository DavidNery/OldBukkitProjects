package me.zfork.ccguardaroupa;

import me.zfork.ccguardaroupa.Utils.Color;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class CCGuardaRoupa extends JavaPlugin implements Listener{

	private String PLUGIN_NAME;
	private Utils utils;

	public void onEnable(){
		PLUGIN_NAME = getDescription().getName();
		ConsoleCommandSender sender = getServer().getConsoleSender();
		sender.sendMessage("§3==========[§b" + PLUGIN_NAME + "§3]==========");
		sender.sendMessage(" §3Status: §bAtivado");
		sender.sendMessage(" §3By: §bzFork");
		sender.sendMessage(" §3Versao: §b" + getDescription().getVersion());
		getServer().getPluginManager().registerEvents(this, this);
		utils = new Utils();
		sender.sendMessage("§3==========[§b" + PLUGIN_NAME + "§3]==========");
	}

	public void onDisable(){
		ConsoleCommandSender sender = getServer().getConsoleSender();
		HandlerList.unregisterAll();
		sender.sendMessage("§4==========[§c" + PLUGIN_NAME + "§4]==========");
		sender.sendMessage(" §4Status: §cDesativado");
		sender.sendMessage(" §4By: §czFork");
		sender.sendMessage(" §4Versao: §c" + getDescription().getVersion());
		sender.sendMessage("§4==========[§c" + PLUGIN_NAME + "§4]==========");
	}

	public static CCGuardaRoupa getCCGuardaRoupa(){
		return (CCGuardaRoupa) Bukkit.getServer().getPluginManager().getPlugin("CCGuardaRoupa");
	}

	@EventHandler
	public void Click(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)){
			if(!p.getItemInHand().equals(null) && p.getItemInHand().getType().equals(Material.PAPER)
					&& p.getItemInHand().getItemMeta().hasDisplayName()
					&& ((ItemMeta) p.getItemInHand().getItemMeta()).getDisplayName().equalsIgnoreCase("§aGuarda Roupa")){
				PlayerLeather playerleather = utils.getPlayerLeather(p);
				if(playerleather == null){
					Inventory inv = Bukkit.createInventory(null, 54, "§aGuarda Roupa");
					playerleather = new PlayerLeather(p, inv);
					utils.addPlayerLeather(playerleather);
				}else{
					playerleather.forceCancel();
					utils.removePlayerLeather(playerleather);
					Inventory inv = Bukkit.createInventory(null, 54, "§aGuarda Roupa");
					playerleather = new PlayerLeather(p, inv);
					utils.addPlayerLeather(playerleather);
				}
				playerleather.openInventory();
				playerleather.runTaskTimer(CCGuardaRoupa.getCCGuardaRoupa(), 0, 2);
			}
		}
	}

	@EventHandler
	public void Close(InventoryCloseEvent e){
		Player p = (Player) e.getPlayer();
		if(e.getInventory().getTitle().equalsIgnoreCase("§aGuarda Roupa")){
			utils.getPlayerLeather(p).cancel();
			p.getInventory().setArmorContents(null);
			if(e.getInventory().getItem(10).getType().name().endsWith("HELMET"))
				p.getInventory().setHelmet(e.getInventory().getItem(10));
			if(e.getInventory().getItem(19).getType().name().endsWith("CHESTPLATE"))
				p.getInventory().setChestplate(e.getInventory().getItem(19));
			if(e.getInventory().getItem(28).getType().name().endsWith("LEGGINGS"))
				p.getInventory().setLeggings(e.getInventory().getItem(28));
			if(e.getInventory().getItem(37).getType().name().endsWith("BOOTS"))
				p.getInventory().setBoots(e.getInventory().getItem(37));
		}
	}

	@EventHandler
	public void Join(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(p.getItemInHand().getType().equals(Material.AIR)){
			ItemStack paper = new ItemStack(Material.PAPER);
			ItemMeta im = paper.getItemMeta();
			im.setDisplayName("§aGuarda Roupa");
			paper.setItemMeta(im);
			p.setItemInHand(paper);
		}
	}

	@EventHandler
	public void Click(InventoryClickEvent e){
		Inventory inv = e.getInventory();
		if(inv.getTitle().equalsIgnoreCase("§aGuarda Roupa")){
			ItemStack item = e.getCurrentItem();
			if(item != null){
				Player p = (Player) e.getWhoClicked();
				e.setCancelled(true);
				if(e.getSlot() == 16){
					// Remover peça
					if(inv.getItem(13).getType().name().endsWith("HELMET"))
						inv.setItem(10, utils.getEditHelmet());
					else if(inv.getItem(13).getType().name().endsWith("CHESTPLATE"))
						inv.setItem(19, utils.getEditChestplate());
					else if(inv.getItem(13).getType().name().endsWith("LEGGINGS"))
						inv.setItem(28, utils.getEditLeggings());
					else if(inv.getItem(13).getType().name().endsWith("BOOTS"))
						inv.setItem(37, utils.getEditBoots());
					inv.setItem(16, null);
					inv.setItem(25, null);
					inv.setItem(39, null);
					inv.setItem(40, null);
					inv.setItem(41, null);
					return;
				}else if(e.getSlot() == 25){
					// Alterar brilho
					String verifyitem = inv.getItem(13).getType().name();
					if(verifyitem.endsWith("HELMET")){
						inv.setItem(10, utils.changeGlow(inv.getItem(10)));
						if(inv.getItem(10).getType().name().startsWith("LEATHER")) inv.setItem(23, inv.getItem(10));
					}else if(verifyitem.endsWith("CHESTPLATE")){
						inv.setItem(19, utils.changeGlow(inv.getItem(19)));
						if(inv.getItem(19).getType().name().startsWith("LEATHER")) inv.setItem(23, inv.getItem(19));
					}else if(verifyitem.endsWith("LEGGINGS")){
						inv.setItem(28, utils.changeGlow(inv.getItem(28)));
						if(inv.getItem(28).getType().name().startsWith("LEATHER")) inv.setItem(23, inv.getItem(28));
					}else if(verifyitem.endsWith("BOOTS")){
						inv.setItem(37, utils.changeGlow(inv.getItem(37)));
						if(inv.getItem(37).getType().name().startsWith("LEATHER")) inv.setItem(23, inv.getItem(37));
					}
					return;
				}else if(e.getSlot() == 23){
					// Clicou em uma peça de couro
					utils.setLeatherItems(inv);
				}else if(item != null && !item.getType().equals(Material.AIR) && e.getSlot() != 39 && e.getSlot() != 40 && e.getSlot() != 41){
					// Remove os itens "RGB" caso o item clicado não seja uma armadura de couro
					inv.setItem(39, null);
					inv.setItem(40, null);
					inv.setItem(41, null);
				}
				if(item != null && !item.getType().equals(Material.AIR)){
					inv.setItem(16, utils.getRemoverPeca());
					inv.setItem(25, utils.getAtivarBrilho());
				}
				if(e.getSlot() == 10)
					utils.setHelmetItems(p, inv);
				else if(e.getSlot() == 19)
					utils.setChestPlateItems(p, inv);
				else if(e.getSlot() == 28)
					utils.setLeggingsItems(p, inv);
				else if(e.getSlot() == 37)
					utils.setBootsItems(p, inv);
				// Alterar armadura
				else if(item.getType().name().endsWith("HELMET"))
					inv.setItem(10, item);
				else if(item.getType().name().endsWith("CHESTPLATE"))
					inv.setItem(19, item);
				else if(item.getType().name().endsWith("LEGGINGS"))
					inv.setItem(28, item);
				else if(item.getType().name().endsWith("BOOTS"))
					inv.setItem(37, item);
				else if(e.getSlot() == 39 || e.getSlot() == 40 || e.getSlot() == 41){
					ItemStack leather = inv.getItem(23);
					LeatherArmorMeta leathermeta = (LeatherArmorMeta) inv.getItem(23).getItemMeta();
					int r = leathermeta.getColor().getRed();
					int g = leathermeta.getColor().getGreen();
					int b = leathermeta.getColor().getBlue();
					if(e.getSlot() == 39){
						r = e.getClick().equals(ClickType.LEFT) ? r+5 : r-5;
						if(r > 255) 
							r = 255;
						else if(r < 0)
							r = 0;
						leathermeta.setColor(org.bukkit.Color.fromRGB(r, g, b));
						leather.setItemMeta(leathermeta);
						utils.setLevelColor(inv, Color.RED);
					}else if(e.getSlot() == 40){
						g = e.getClick().equals(ClickType.LEFT) ? g+5 : g-5;
						if(g > 255) 
							g = 255;
						else if(g < 0)
							g = 0;
						leathermeta.setColor(org.bukkit.Color.fromRGB(r, g, b));
						leather.setItemMeta(leathermeta);
						utils.setLevelColor(inv, Color.GREEN);
					}else if(e.getSlot() == 41){
						b = e.getClick().equals(ClickType.LEFT) ? b+5 : b-5;
						if(b > 255) 
							b = 255;
						else if(b < 0)
							b = 0;
						leathermeta.setColor(org.bukkit.Color.fromRGB(r, g, b));
						leather.setItemMeta(leathermeta);
						utils.setLevelColor(inv, Color.BLUE);
					}
					if(leather.getType().name().endsWith("HELMET"))
						inv.setItem(10, leather);
					else if(leather.getType().name().endsWith("CHESTPLATE"))
						inv.setItem(19, leather);
					else if(leather.getType().name().endsWith("LEGGINGS"))
						inv.setItem(28, leather);
					else if(leather.getType().name().endsWith("BOOTS"))
						inv.setItem(37, leather);
				}
			}
		}
	}

}

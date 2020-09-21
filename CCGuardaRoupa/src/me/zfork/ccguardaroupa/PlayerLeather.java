package me.zfork.ccguardaroupa;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerLeather extends BukkitRunnable{

	private Player p;
	private Utils utils;
	private Inventory inv;
	private boolean voltando = false;

	public PlayerLeather(Player p, Inventory inv) {
		this.p = p;
		this.utils = new Utils();
		this.inv = inv;
		utils.addItemsToInventory(p, inv);
	}

	@Override
	public void run() {
		ItemStack coloreditem = inv.getItem(12);
		LeatherArmorMeta coloreditemmeta = (LeatherArmorMeta) coloreditem.getItemMeta();
		int r = coloreditemmeta.getColor().getRed();
		int g = coloreditemmeta.getColor().getGreen();
		int b = coloreditemmeta.getColor().getBlue();
		if(voltando){
			if(r >= 5)
				r -= 5;
			else
				if(g >= 5)
					g -= 5;
				else
					if(b >= 5)
						b -= 5;
					else
						voltando = false;
		}else{
			if(r <= 250)
				r += 5;
			else
				if(g <= 250)
					g += 5;
				else
					if(b <= 250)
						b += 5;
					else
						voltando = true;
		}
		if(g % 2 == 0) coloreditemmeta.setDisplayName(utils.getColoredText());
		coloreditemmeta.setColor(Color.fromRGB(r, g, b));
		coloreditem.setItemMeta(coloreditemmeta);
		ItemStack coloredhelmet = inv.getItem(10);
		if(coloredhelmet.getItemMeta().getDisplayName().replaceAll("[&§][0-9a-f]", "").equalsIgnoreCase("COLORIDO")){
			coloredhelmet.setItemMeta(coloreditemmeta);
			inv.setItem(10, coloredhelmet);
			p.getInventory().setHelmet(coloredhelmet);
		}
		ItemStack coloredchestplate = inv.getItem(19);
		if(coloredchestplate.getItemMeta().getDisplayName().replaceAll("[&§][0-9a-f]", "").equalsIgnoreCase("COLORIDO")){
			coloredchestplate.setItemMeta(coloreditemmeta);
			inv.setItem(19, coloredchestplate);
			p.getInventory().setChestplate(coloredchestplate);
		}
		ItemStack coloredleggings = inv.getItem(28);
		if(coloredleggings.getItemMeta().getDisplayName().replaceAll("[&§][0-9a-f]", "").equalsIgnoreCase("COLORIDO")){
			coloredleggings.setItemMeta(coloreditemmeta);
			inv.setItem(28, coloredleggings);
			p.getInventory().setLeggings(coloredleggings);
		}
		ItemStack coloredboots = inv.getItem(37);
		if(coloredboots.getItemMeta().getDisplayName().replaceAll("[&§][0-9a-f]", "").equalsIgnoreCase("COLORIDO")){
			coloredboots.setItemMeta(coloreditemmeta);
			inv.setItem(37, coloredboots);
			p.getInventory().setBoots(coloredboots);
		}
	}
	
	@Override
	public synchronized void cancel() throws IllegalStateException {
		PlayerInventory pi = p.getInventory();
		if(pi.getHelmet() == null && pi.getChestplate() == null && pi.getLeggings() == null && pi.getBoots() == null){
			super.cancel();
		}else{
			if(!((pi.getHelmet() != null && pi.getHelmet().getItemMeta().getDisplayName().replaceAll("[&§][0-9a-f]", "").equalsIgnoreCase("COLORIDO"))
					|| (pi.getChestplate() != null && pi.getChestplate().getItemMeta().getDisplayName().replaceAll("[&§][0-9a-f]", "").equalsIgnoreCase("COLORIDO"))
					|| (pi.getLeggings() != null && pi.getLeggings().getItemMeta().getDisplayName().replaceAll("[&§][0-9a-f]", "").equalsIgnoreCase("COLORIDO"))
					|| (pi.getBoots() != null && pi.getBoots().getItemMeta().getDisplayName().replaceAll("[&§][0-9a-f]", "").equalsIgnoreCase("COLORIDO")))){
				super.cancel();
			}
		}
	}
	
	public synchronized void forceCancel() {
		super.cancel();
	}

	public Player getPlayer() {
		return p;
	}

	public void openInventory() {
		p.openInventory(inv);
	}

}

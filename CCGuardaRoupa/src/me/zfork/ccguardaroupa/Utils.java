package me.zfork.ccguardaroupa;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class Utils {

	private HashSet<PlayerLeather> leathers;
	private final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	private ItemStack editHelmet, editChestplate, editLeggings, editBoots,
	diamondhelmet, goldhelmet, ironhelmet, chainhelmet, leatherhelmet,
	diamondchestplate, goldchestplate, ironchestplate, chainchestplate, leatherchestplate, 
	diamondleggings, goldleggings, ironleggings, chainleggings, leatherleggings, 
	diamondboots, goldboots, ironboots, chainboots, leatherboots, 
	coloredhelmet, coloredchestplate, coloredleggings, coloredboots, 
	ativarbrilho, removerpeca, red, green, blue;
	private String[] letrascolorido = {"C", "O", "L", "O", "R", "I", "D", "O"};
	private String[] cores = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
	
	private Random r;

	public Utils() {
		this.leathers = new HashSet<>();
		r = new Random();
		createItems();
	}
	
	public enum Color {
		RED, GREEN, BLUE
	}

	protected void addPlayerLeather(PlayerLeather playerleather){
		leathers.add(playerleather);
	}

	protected void removePlayerLeather(PlayerLeather playerleather){
		leathers.remove(playerleather);
	}

	protected PlayerLeather getPlayerLeather(Player p){
		for(PlayerLeather playerleather : leathers)
			if(playerleather.getPlayer().getName().equalsIgnoreCase(p.getName()))
				return playerleather;
		return null;
	}

	protected ItemStack criarItem(Material material, int data, String nome, List<String> lore, boolean glow){
		ItemStack itemstack = new ItemStack(material, 1, (byte) data);
		if(nome != null || lore != null){
			ItemMeta im = itemstack.getItemMeta();
			if(nome != null) im.setDisplayName(nome.replace("&", "§"));
			if(lore != null) im.setLore(lore);
			itemstack.setItemMeta(im);
		}
		if(glow)
			return changeGlow(itemstack);
		else
			return itemstack;
	}

	protected ItemStack changeGlow(ItemStack item){
		try {
			Class<?> ItemStack = Class.forName("net.minecraft.server." + version + ".ItemStack");
			Class<?> NBTTagCompound = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
			Class<?> NBTTagList = Class.forName("net.minecraft.server." + version + ".NBTTagList");
			Class<?> CraftItemStack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
			Class<?> NBTBase = Class.forName("net.minecraft.server." + version + ".NBTBase");
			
			Method asNMSCopy = CraftItemStack.getDeclaredMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class);
			Method asCraftMirror = CraftItemStack.getDeclaredMethod("asCraftMirror", ItemStack);
			Method hasTag = ItemStack.getDeclaredMethod("hasTag");
			Method setTag = ItemStack.getDeclaredMethod("setTag", NBTTagCompound);
			Method getTag = ItemStack.getDeclaredMethod("getTag");
			Method hasKey = NBTTagCompound.getDeclaredMethod("hasKey", String.class);
			Method set = NBTTagCompound.getDeclaredMethod("set", String.class, NBTBase);
			Method remove = NBTTagCompound.getDeclaredMethod("remove", String.class);

			asNMSCopy.setAccessible(true);
			asCraftMirror.setAccessible(true);
			hasTag.setAccessible(true);
			setTag.setAccessible(true);
			getTag.setAccessible(true);
			set.setAccessible(true);
			Constructor<?> NBTTagCompoundConstructor = NBTTagCompound.getConstructor();
			Constructor<?> NBTTagListConstructor = NBTTagList.getConstructor();
			NBTTagCompoundConstructor.setAccessible(true);
			NBTTagListConstructor.setAccessible(true);
			Object nmsStack = asNMSCopy.invoke(null, item);
			Object tag = null;
			if((Boolean) hasTag.invoke(nmsStack))
				tag = getTag.invoke(nmsStack);
			else
				tag = NBTTagCompoundConstructor.newInstance();
			if((Boolean) hasKey.invoke(tag, "ench")){
				remove.invoke(tag, "ench");
			}else{
				Object ench = NBTTagListConstructor.newInstance();
				set.invoke(tag, "ench", ench);
			}
			setTag.invoke(nmsStack, tag);
			return (ItemStack) asCraftMirror.invoke(null, nmsStack);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void addItemsToInventory(Player p, Inventory playerinventory){
		PlayerInventory pi = p.getInventory();
		playerinventory.setItem(10, pi.getHelmet() == null ? editHelmet : pi.getHelmet());
		playerinventory.setItem(19, pi.getChestplate() == null ? editChestplate : pi.getChestplate());
		playerinventory.setItem(28, pi.getLeggings() == null ? editLeggings : pi.getLeggings());
		playerinventory.setItem(37, pi.getBoots() == null ? editBoots : pi.getBoots());
		setHelmetItems(p, playerinventory);
	}
	
	public String getColoredText(){
		String texto = "";
		for(int i = 0; i<letrascolorido.length; i++)
			texto += "§" + cores[r.nextInt(cores.length)] + letrascolorido[i];
		return texto;
	}

	private void createItems(){
		// Items de editar //
		editHelmet = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
		ItemMeta editHelmetMeta = editHelmet.getItemMeta();
		editHelmetMeta.setDisplayName("§eEditar Helmet");
		editHelmet.setItemMeta(editHelmetMeta);
		editChestplate = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
		ItemMeta editChestplateMeta = editChestplate.getItemMeta();
		editChestplateMeta.setDisplayName("§eEditar Chestplate");
		editChestplate.setItemMeta(editChestplateMeta);
		editLeggings = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
		ItemMeta editLeggingsMeta = editLeggings.getItemMeta();
		editLeggingsMeta.setDisplayName("§eEditar Leggings");
		editLeggings.setItemMeta(editLeggingsMeta);
		editBoots = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
		ItemMeta editBootsMeta = editBoots.getItemMeta();
		editBootsMeta.setDisplayName("§eEditar Boots");
		editBoots.setItemMeta(editBootsMeta);
		// Fim //

		// Armadura //
		// Capacete
		diamondhelmet = criarItem(Material.DIAMOND_HELMET, 0, "&rElmo de Diamante", null, false);
		goldhelmet = criarItem(Material.GOLD_HELMET, 0, "&rElmo de Ouro", null, false);
		ironhelmet = criarItem(Material.IRON_HELMET, 0, "&rElmo de Ferro", null, false);
		chainhelmet = criarItem(Material.CHAINMAIL_HELMET, 0, "&rElmo de Chain", null, false);
		leatherhelmet = criarItem(Material.LEATHER_HELMET, 0, "&rElmo de Couro", null, false);
		// Peitoral
		diamondchestplate = criarItem(Material.DIAMOND_CHESTPLATE, 0, "&rPeitoral de Diamante", null, false);
		goldchestplate = criarItem(Material.GOLD_CHESTPLATE, 0, "&rPeitoral de Ouro", null, false);
		ironchestplate = criarItem(Material.IRON_CHESTPLATE, 0, "&rPeitoral de Ferro", null, false);
		chainchestplate = criarItem(Material.CHAINMAIL_CHESTPLATE, 0, "&rPeitoral de Chain", null, false);
		leatherchestplate = criarItem(Material.LEATHER_CHESTPLATE, 0, "&rPeitoral de Couro", null, false);
		// Calca
		diamondleggings = criarItem(Material.DIAMOND_LEGGINGS, 0, "&rCalca de Diamante", null, false);
		goldleggings = criarItem(Material.GOLD_LEGGINGS, 0, "&rCalca de Ouro", null, false);
		ironleggings = criarItem(Material.IRON_LEGGINGS, 0, "&rCalca de Ferro", null, false);
		chainleggings = criarItem(Material.CHAINMAIL_LEGGINGS, 0, "&rCalca de Chain", null, false);
		leatherleggings = criarItem(Material.LEATHER_LEGGINGS, 0, "&rCalca de Couro", null, false);
		// Botas
		diamondboots = criarItem(Material.DIAMOND_BOOTS, 0, "&rBotas de Diamante", null, false);
		goldboots = criarItem(Material.GOLD_BOOTS, 0, "&rBotas de Ouro", null, false);
		ironboots = criarItem(Material.IRON_BOOTS, 0, "&rBotas de Ferro", null, false);
		chainboots = criarItem(Material.CHAINMAIL_BOOTS, 0, "&rBotas de Chain", null, false);
		leatherboots = criarItem(Material.LEATHER_BOOTS, 0, "&rBotas de Couro", null, false);
		// Fim //

		// Outros //
		// Colorido
		coloredhelmet = criarItem(Material.LEATHER_HELMET, 0, "§aC§bO§cL§dO§eR§fI§5D§9O", null, false);
		coloredchestplate = criarItem(Material.LEATHER_CHESTPLATE, 0, "§aC§bO§cL§dO§eR§fI§5D§9O", null, false);
		coloredleggings = criarItem(Material.LEATHER_LEGGINGS, 0, "§aC§bO§cL§dO§eR§fI§5D§9O", null, false);
		coloredboots = criarItem(Material.LEATHER_BOOTS, 0, "§aC§bO§cL§dO§eR§fI§5D§9O", null, false);
		// Colorido
		removerpeca = criarItem(Material.BARRIER, 0, "&cRemover peça", null, false);
		ativarbrilho = criarItem(Material.NETHER_STAR, 0, "&eAtivar brilho", null, true);
		// Cores leather
		red = criarItem(Material.STAINED_GLASS_PANE, 14, "&cVermelho", null, false);
		green = criarItem(Material.STAINED_GLASS_PANE, 5, "&aVerde", null, false);
		blue = criarItem(Material.STAINED_GLASS_PANE, 11, "&bAzul", null, false);
		// Cores leather
		// Fim //
	}
	
	public void setHelmetItems(Player p, Inventory inv){
		inv.setItem(12, coloredhelmet);
		inv.setItem(13, diamondhelmet);
		inv.setItem(14, goldhelmet);
		inv.setItem(21, ironhelmet);
		inv.setItem(22, chainhelmet);
		if(p.getInventory().getHelmet() == null){
			inv.setItem(23, leatherhelmet.clone());
		}else{
			if(!p.getInventory().getHelmet().getItemMeta().getDisplayName().equalsIgnoreCase("§rElmo de Couro"))
				inv.setItem(23, leatherhelmet.clone());
			else
				inv.setItem(23, p.getInventory().getHelmet());
		}
		ItemStack iteminslot = inv.getItem(10);
		if(iteminslot!= null && !iteminslot.getType().equals(Material.STAINED_GLASS_PANE)){
			inv.setItem(16, removerpeca);
			inv.setItem(25, ativarbrilho);
		}else{
			inv.setItem(16, null);
			inv.setItem(25, null);
		}
		if(!iteminslot.getType().equals(Material.LEATHER_HELMET)){
			inv.setItem(39, null);
			inv.setItem(40, null);
			inv.setItem(41, null);
		}else{
			if(iteminslot.getItemMeta().getDisplayName().endsWith("Elmo de Couro")){
				setLeatherItems(inv);
			}
		}
	}
	
	public void setChestPlateItems(Player p, Inventory inv){
		inv.setItem(12, coloredchestplate);
		inv.setItem(13, diamondchestplate);
		inv.setItem(14, goldchestplate);
		inv.setItem(21, ironchestplate);
		inv.setItem(22, chainchestplate);
		if(p.getInventory().getChestplate() == null){
			inv.setItem(23, leatherchestplate.clone());
		}else{
			if(!p.getInventory().getChestplate().getItemMeta().getDisplayName().equalsIgnoreCase("§rPeitoral de Couro"))
				inv.setItem(23, leatherchestplate.clone());
			else
				inv.setItem(23, p.getInventory().getChestplate());
		}
		ItemStack iteminslot = inv.getItem(19);
		if(iteminslot != null && !iteminslot.getType().equals(Material.STAINED_GLASS_PANE)){
			inv.setItem(16, removerpeca);
			inv.setItem(25, ativarbrilho);
		}else{
			inv.setItem(16, null);
			inv.setItem(25, null);
		}
		if(!iteminslot.getType().equals(Material.LEATHER_CHESTPLATE)){
			inv.setItem(39, null);
			inv.setItem(40, null);
			inv.setItem(41, null);
		}else{
			if(iteminslot.getItemMeta().getDisplayName().endsWith("Peitoral de Couro")){
				setLeatherItems(inv);
			}
		}
	}
	
	public void setLeggingsItems(Player p, Inventory inv){
		inv.setItem(12, coloredleggings);
		inv.setItem(13, diamondleggings);
		inv.setItem(14, goldleggings);
		inv.setItem(21, ironleggings);
		inv.setItem(22, chainleggings);
		if(p.getInventory().getLeggings() == null){
			inv.setItem(23, leatherleggings.clone());
		}else{
			if(!p.getInventory().getLeggings().getItemMeta().getDisplayName().equalsIgnoreCase("§rCalca de Couro"))
				inv.setItem(23, leatherleggings.clone());
			else
				inv.setItem(23, p.getInventory().getLeggings());
		}
		ItemStack iteminslot = inv.getItem(28);
		if(iteminslot != null && !iteminslot.getType().equals(Material.STAINED_GLASS_PANE)){
			inv.setItem(16, removerpeca);
			inv.setItem(25, ativarbrilho);
		}else{
			inv.setItem(16, null);
			inv.setItem(25, null);
		}
		if(!iteminslot.getType().equals(Material.LEATHER_LEGGINGS)){
			inv.setItem(39, null);
			inv.setItem(40, null);
			inv.setItem(41, null);
		}else{
			if(iteminslot.getItemMeta().getDisplayName().endsWith("Calca de Couro")) setLeatherItems(inv);
		}
	}
	
	public void setBootsItems(Player p, Inventory inv){
		inv.setItem(12, coloredboots);
		inv.setItem(13, diamondboots);
		inv.setItem(14, goldboots);
		inv.setItem(21, ironboots);
		inv.setItem(22, chainboots);
		if(p.getInventory().getBoots() == null){
			inv.setItem(23, leatherboots.clone());
		}else{
			if(!p.getInventory().getBoots().getItemMeta().getDisplayName().equalsIgnoreCase("§rBotas de Couro"))
				inv.setItem(23, leatherboots.clone());
			else
				inv.setItem(23, p.getInventory().getBoots());
		}
		ItemStack iteminslot = inv.getItem(37);
		if(iteminslot != null && !iteminslot.getType().equals(Material.STAINED_GLASS_PANE)){
			inv.setItem(16, removerpeca);
			inv.setItem(25, ativarbrilho);
		}else{
			inv.setItem(16, null);
			inv.setItem(25, null);
		}
		if(!iteminslot.getType().equals(Material.LEATHER_BOOTS)){
			inv.setItem(39, null);
			inv.setItem(40, null);
			inv.setItem(41, null);
		}else{
			if(iteminslot.getItemMeta().getDisplayName().endsWith("Botas de Couro")) setLeatherItems(inv);
		}
	}
	
	public void setLeatherItems(Inventory inv){
		if(inv.getItem(39) == null){
			inv.setItem(39, red.clone());
			inv.setItem(40, green.clone());
			inv.setItem(41, blue.clone());
			setLevelColor(inv, Color.RED);
			setLevelColor(inv, Color.GREEN);
			setLevelColor(inv, Color.BLUE);
		}
	}
	
	public void setLevelColor(Inventory inv, Color color){
		ItemStack itemcolorido = inv.getItem(23);
		LeatherArmorMeta itemcoloridom = (LeatherArmorMeta) itemcolorido.getItemMeta();
		switch (color) {
		case RED:
			int r = itemcoloridom.getColor().getRed();
			ItemStack red = inv.getItem(39);
			ItemMeta redmeta = red.getItemMeta();
			String corv = "§c" + r + " ";
			for(int i = 0; i<255; i+= 5)
				corv += (r >= i ? "§c|" : "§7|");
			redmeta.setLore(Arrays.asList(corv));
			red.setItemMeta(redmeta);
		case GREEN:
			int g = itemcoloridom.getColor().getGreen();
			ItemStack green = inv.getItem(40);
			ItemMeta greenmeta = green.getItemMeta();
			String corg = "§a" + g + " ";
			for(int i = 0; i<255; i+= 5)
				corg += (g >= i ? "§a|" : "§7|");
			greenmeta.setLore(Arrays.asList(corg));
			green.setItemMeta(greenmeta);
		case BLUE:
			int b = itemcoloridom.getColor().getBlue();
			ItemStack blue = inv.getItem(41);
			ItemMeta bluemeta = blue.getItemMeta();
			String corb = "§b" + b + " ";
			for(int i = 0; i<255; i+= 5)
				corb += (b >= i ? "§a|" : "§7|");
			bluemeta.setLore(Arrays.asList(corb));
			blue.setItemMeta(bluemeta);
		}
	}
	
	public ItemStack getAtivarBrilho() {
		return ativarbrilho;
	}
	
	public ItemStack getRemoverPeca() {
		return removerpeca;
	}
	
	public ItemStack getEditHelmet() {
		return editHelmet;
	}
	
	public ItemStack getEditChestplate() {
		return editChestplate;
	}
	
	public ItemStack getEditLeggings() {
		return editLeggings;
	}
	
	public ItemStack getEditBoots() {
		return editBoots;
	}

}

package me.zfork.landmasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class LandMasks extends JavaPlugin{
	
	private Inventory inv;
	
	@Override
	public void onEnable() {
		getServer().getConsoleSender().sendMessage("LandMasks sendo ativado...");
		if(!new File(getDataFolder(), "config.yml").exists()) {
			saveDefaultConfig();
			getServer().getConsoleSender().sendMessage("Config criada!");
		}
		inv = getServer().createInventory(null, getConfig().getInt("Tamanho_Inventario"), getConfig().getString("Nome_Inventario").replace("&", "§"));
		getServer().getConsoleSender().sendMessage("Inventario de mascaras criado!");
		for(String s : getConfig().getConfigurationSection("Mascaras").getKeys(false)){
			ItemStack mascara = getHead(s);
			SkullMeta sm = (SkullMeta) mascara.getItemMeta();
			sm.setDisplayName(getConfig().getString("Mascaras." + s + ".Nome").replace("&", "§"));
			List<String> lore = new ArrayList<String>();
			for(String l : getConfig().getStringList("Mascaras." + s + ".Lore"))
				lore.add(l.replace("&", "§"));
			sm.setLore(lore);
			mascara.setItemMeta(sm);
			if(inv.firstEmpty() == -1)
				break;
			else
				inv.setItem(inv.firstEmpty(), mascara);
		}
		getServer().getConsoleSender().sendMessage("Mascaras adicionadas ao inventario!");
		getCommand("mascaras").setExecutor(new CommandMasks());
		getServer().getConsoleSender().sendMessage("Comando registrado!");
		getServer().getPluginManager().registerEvents(new Events(), this);
		getServer().getConsoleSender().sendMessage("Eventos registrados!");
		getServer().getConsoleSender().sendMessage("LandMasks sendo ativado!");
	}
	
	@Override
	public void onDisable() {
		getServer().getConsoleSender().sendMessage("LandMasks sendo desativado...");
		getServer().getConsoleSender().sendMessage("LandMasks sendo desativado!");
	}
	
	public static LandMasks getLandMasks() {
		return (LandMasks) Bukkit.getServer().getPluginManager().getPlugin("LandMasks");
	}
	
	public Inventory getInv() {
		return this.inv;
	}
	
	public static ItemStack getHead(String mhf){
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner("MHF_" + mhf.toUpperCase());
        skull.setItemMeta(meta);
        return skull;
	}

}

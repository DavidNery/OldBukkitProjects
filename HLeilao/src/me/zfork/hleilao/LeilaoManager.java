package me.zfork.hleilao;

import java.text.NumberFormat;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LeilaoManager {
	
	private HLeilao instance;
	private ItemStack item;
	private double minimo, maiorlance;
	private String leiloando, liderando;
	private long proximo;
	private Inventory inv;
	
	public LeilaoManager(HLeilao instance){
		this.instance = instance;
		this.item = null;
		this.maiorlance = 0;
		this.minimo = 0;
		this.leiloando = null;
		this.liderando = null;
		this.proximo = 0l;
		this.inv = this.instance.getServer().createInventory(null, 9*3, "Leilao");
		for(int i = 0; i<27; i++){
			inv.setItem(i, new ItemStack(Material.getMaterial(instance.getConfig().getInt("Config.Bloco_Completar"))));
		}
		inv.setItem(13, new ItemStack(Material.DIAMOND));
		inv.setItem(15, new ItemStack(Material.PAPER));
	}
	
	public void setItem(ItemStack item){
		this.item = item;
		inv.setItem(13, item);
	}
	
	public ItemStack getItem(){
		return this.item;
	}
	
	public void setMinimo(double minimo){
		this.minimo = minimo;
	}
	
	public double getMinimo(){
		return this.minimo;
	}
	
	public void setMaiorLance(double maiorlance){
		this.maiorlance = maiorlance;
	}
	
	public double getMaiorLance(){
		return this.maiorlance;
	}
	
	public void setLeiloando(String leiloando){
		this.leiloando = leiloando;
	}
	
	public String getLeiloando(){
		return this.leiloando;
	}
	
	public void setLiderando(String liderando){
		this.liderando = liderando;
	}
	
	public String getLiderando(){
		return this.liderando;
	}
	
	public void setProximo(long proximo){
		this.proximo = proximo;
	}
	
	public long getProximo(){
		return this.proximo;
	}
	
	public Inventory getInv(){
		return this.inv;
	}
	
	public void changeInfo(){
		ItemStack item = new ItemStack(Material.PAPER);
		ItemMeta im = item.getItemMeta();
		im.setLore(Arrays.asList("§6Leiloador: §e" + leiloando, "§6Liderando: §e" + (liderando.equals("") ? "Ninguém" : liderando), 
				"§6Valor Atual: §e" + NumberFormat.getCurrencyInstance().format(maiorlance).replaceAll("[^\\d\\.,]+", "") + " Coins"));
		item.setItemMeta(im);
		inv.setItem(15, item);
	}
	
	public void resetar(long proximo){
		this.item = null;
		this.maiorlance = 0;
		this.minimo = 0;
		this.leiloando = null;
		this.liderando = null;
		this.proximo = proximo;
	}

}

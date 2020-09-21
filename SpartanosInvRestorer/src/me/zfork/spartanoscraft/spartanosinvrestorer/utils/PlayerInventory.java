package me.zfork.spartanoscraft.spartanosinvrestorer.utils;

import org.bukkit.inventory.Inventory;

public class PlayerInventory {
	
	private String ownerName;
	private Inventory previewInventory;
	private long expiryDate;
	private double preco;
	private static int id = 1;
	private int idForThis;
	private boolean podeComprar, temItemRaro;
	
	public PlayerInventory(String ownerName, long expiryDate, double preco, boolean podeComprar, boolean temItemRaro) {
		this.ownerName = ownerName;
		this.expiryDate = expiryDate;
		this.preco = preco;
		this.idForThis = id;
		this.podeComprar = podeComprar;
		this.temItemRaro = temItemRaro;
		id++;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public Inventory getPreviewInventory() {
		return previewInventory;
	}
	
	public void setPreviewInventory(Inventory previewInventory) {
		this.previewInventory = previewInventory;
	}

	public void setExpiryDate(long expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	public long getExpiryDate() {
		return expiryDate;
	}
	
	public double getPreco() {
		return preco;
	}
	
	public int getId() {
		return idForThis;
	}
	
	public boolean getPodeComprar() {
		return podeComprar;
	}
	
	public void setPodeComprar(boolean podeComprar) {
		this.podeComprar = podeComprar;
	}
	
	public boolean getItemRaro() {
		return temItemRaro;
	}
	
	public void setTemItemRaro(boolean temItemRaro) {
		this.temItemRaro = temItemRaro;
	}

}

package me.zfork.spartanoscraft.spartanosinvrestorer.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import me.zfork.spartanoscraft.spartanosinvrestorer.SpartanosInvRestorer;
import me.zfork.spartanoscraft.spartanosinvrestorer.sql.MySQL;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class PlayerInventoryUtils {

	private SpartanosInvRestorer instance;

	private ArrayList<PlayerInventory> savedInventories;
	private ArrayList<Inventory> inventarios;
	private HashMap<String, ArrayList<Inventory>> playersInventories;
	private HashMap<String, ArrayList<Inventory>> playersInventoriesGeral;
	
	private ItemStack voltar, proximo;

	private MySQL mysql;

	public PlayerInventoryUtils(SpartanosInvRestorer instance, MySQL mysql) throws ClassNotFoundException, SQLException, IOException {
		this.instance = instance;
		this.savedInventories = new ArrayList<>();
		this.inventarios = new ArrayList<>();
		this.playersInventories = new HashMap<>();
		this.playersInventoriesGeral = new HashMap<>();

		this.mysql = mysql;
		
		voltar = (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Inventories.Item_Voltar").replace("&", "§"));
		proximo = (ItemStack) instance.criarItem(instance.getConfig().getString("Config.Inventories.Item_Proximo").replace("&", "§"));
	}

	public ArrayList<PlayerInventory> getSavedInventories() {
		return savedInventories;
	}

	public ArrayList<Inventory> getInventarios() {
		return inventarios;
	}

	public HashMap<String, ArrayList<Inventory>> getPlayersInventories() {
		return playersInventories;
	}
	
	public HashMap<String, ArrayList<Inventory>> getPlayersInventoriesGeral() {
		return playersInventoriesGeral;
	}
	
	public ItemStack getVoltar() {
		return voltar;
	}
	
	public ItemStack getProximo() {
		return proximo;
	}

	public void loadInventories() throws ClassNotFoundException, SQLException, IOException {
		savedInventories = mysql.getInventories();
		Inventory inv = instance.getServer().createInventory(null, 54, "§7Comprar Inventários §f- §c§l1");
		inventarios.add(inv);
		for(PlayerInventory playerInventory : savedInventories)
			addPlayerInventory(playerInventory);
	}

	public ItemStack getHead(String owner) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
		SkullMeta sm = (SkullMeta) item.getItemMeta();
		sm.setOwner(owner);
		item.setItemMeta(sm);
		return item;
	}

	public Inventory cloneInventory(Inventory inv) {
		Inventory clonedInv = instance.getServer().createInventory(null, inv.getSize(), inv.getName());
		for(int i = 0; i<inv.getSize(); i++){
			ItemStack item = inv.getItem(i);
			if(item != null)
				clonedInv.setItem(i, item);
		}
		return clonedInv;
	}

	public boolean isVazio(Player p) {
		for(ItemStack item : p.getInventory().getContents())
			if(item != null && !item.getType().equals(Material.AIR)) return false;
		for(ItemStack item : p.getInventory().getArmorContents())
			if(item != null && !item.getType().equals(Material.AIR)) return false;
		return true;
	}
	
	public void addPlayerInInventory(String player){
		new BukkitRunnable() {
			@Override
			public void run() {
				Inventory inv = new ArrayList<>(inventarios).get(inventarios.size()-1);
				int firstEmpty = inv.firstEmpty();
				ItemStack head = getHead(player);
				SkullMeta sm = (SkullMeta) head.getItemMeta();
				sm.setDisplayName("§7Inventários do player §c§l" + player);
				head.setItemMeta(sm);
				if(firstEmpty == 53){
					inv.setItem(53, proximo);
					inv = instance.getServer().createInventory(null, 54, "§7Comprar Inventários §f- §c§l" + (inventarios.size()+1));
					inventarios.add(inv);
					inv.setItem(45, voltar);
					inv.setItem(49, voltar);
					inv.setItem(inv.firstEmpty(), head);
				}else{
					if(firstEmpty == 49 && inventarios.size() != 1)
						inv.setItem(firstEmpty+1, head);
					else
						inv.setItem(firstEmpty, head);
				}
			}
		}.runTaskAsynchronously(instance);
	}
	
	public void addPlayerInventory(PlayerInventory playerInventory) {
		new BukkitRunnable() {
			@Override
			public void run() {
				ArrayList<Inventory> inventariosPlayer = playersInventories.get(playerInventory.getOwnerName());
				Inventory playerInv;
				if(inventariosPlayer == null){
					if(playerInventory.getPodeComprar()) addPlayerInInventory(playerInventory.getOwnerName());
					inventariosPlayer = new ArrayList<>();
					playerInv = instance.getServer().createInventory(null, 54, "§7Inventários §f- §c§l" + playerInventory.getOwnerName() + " §f- §c§l1");
					inventariosPlayer.add(playerInv);
					playersInventories.put(playerInventory.getOwnerName(), inventariosPlayer);
				}else{
					playerInv = new ArrayList<>(inventariosPlayer).get(inventariosPlayer.size()-1);
				}
				int playerPagina = inventariosPlayer.size(), firstEmpty;
				ItemStack item = new ItemStack(Material.STONE);
				ItemMeta im = item.getItemMeta();
				im.setDisplayName("§7Inventário §c§l" + playerInventory.getId());
				item.setItemMeta(im);
				firstEmpty = playerInv.firstEmpty();
				if(firstEmpty == 53){
					playerInv.setItem(53, proximo);
					playerPagina++;
					playerInv = instance.getServer().createInventory(null, 54, "§7Inventários §f- §c§l" + playerInventory.getOwnerName() + " §f- §c§l" + playerPagina);
					inventariosPlayer.add(playerInv);
					playerInv.setItem(45, voltar);
					playerInv.setItem(playerInv.firstEmpty(), item);
				}else{
					if(firstEmpty == 45 && playerPagina != 1)
						playerInv.setItem(firstEmpty+1, item);
					else
						playerInv.setItem(firstEmpty, item);
				}
			}
		}.runTaskAsynchronously(instance);
	}

	public void removePlayerInventory(PlayerInventory playerInventory) {
		new BukkitRunnable() {
			@Override
			public void run() {
				savedInventories.remove(playerInventory);
				ArrayList<Inventory> playerInventories = playersInventories.get(playerInventory.getOwnerName());
				Inventory inv;
				ItemStack item;
				// Percorre os inventários do player
				for(int i = 0; i<playerInventories.size(); i++){
					inv = playerInventories.get(i);
					// Percorre os slots do inventário correspondente
					// a index "i" dos inventários do player
					for(int j = 0; j<inv.getSize()-1; ){
						item = inv.getItem(j);
						if(item == null){
							if(inv.getItem(j+1) != null){
								inv.setItem(j, inv.getItem(j+1));
								inv.setItem(j+1, null);
							}else{
								break;
							}
						}else{
							if(item.getType().equals(Material.STONE) &&
									item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
									item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Inventário §c§l" + playerInventory.getId())){
								inv.setItem(j, null);
								if(j+1 != 53 && inv.getItem(j+1) != null){
									inv.setItem(j, inv.getItem(j+1));
									inv.setItem(j+1, null);
								}else if(j == 0 && i != 0 && inv.getItem(j+1) == null) playerInventories.get(i-1).setItem(53, null);
							}
						}
						j++;
					}
					if(inv.firstEmpty() == 0){
						playerInventories.remove(i);
						for(HumanEntity players : inv.getViewers()){
							players.getOpenInventory().close();
							players.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Pagina_Nao_Mais_Existe").replace("&", "§"));
						}
					}
				}
				if(playerInventories.size() == 0) removePlayerForInventories(playerInventory.getOwnerName());
				if(playerInventory.getPodeComprar()){
					playerInventories = playersInventoriesGeral.get(playerInventory.getOwnerName());
					// Percorre os inventários do player
					for(int i = 0; i<playerInventories.size(); i++){
						inv = playerInventories.get(i);
						// Percorre os slots do inventário correspondente
						// a index "i" dos inventários do player
						for(int j = 0; j<inv.getSize()-1; ){
							item = inv.getItem(j);
							if(item == null){
								if(inv.getItem(j+1) != null){
									inv.setItem(j, inv.getItem(j+1));
									inv.setItem(j+1, null);
								}else{
									break;
								}
							}else{
								if(item.getType().equals(Material.STONE) &&
										item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
										item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Inventário §c§l" + playerInventory.getId())){
									inv.setItem(j, null);
									if(j+1 != 53 && inv.getItem(j+1) != null){
										inv.setItem(j, inv.getItem(j+1));
										inv.setItem(j+1, null);
									}else if(j == 0 && i != 0 && inv.getItem(j+1) == null) playerInventories.get(i-1).setItem(53, null);
								}
							}
							j++;
						}
						if(inv.firstEmpty() == 0){
							playerInventories.remove(i);
							for(HumanEntity players : inv.getViewers()){
								players.getOpenInventory().close();
								players.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Pagina_Nao_Mais_Existe").replace("&", "§"));
							}
						}
					}
					if(playerInventories.size() == 0){
						playersInventoriesGeral.remove(playerInventory.getOwnerName());
						removePlayerForInventoriesGeral(playerInventory.getOwnerName());
					}
				}
			}
		}.runTaskAsynchronously(instance);
	}
	
	public void removePlayerForInventoriesGeral(String ownerName) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Inventory inv;
				ItemStack item;
				for(int i = 0; i<inventarios.size(); i++){
					inv = inventarios.get(i);
					for(int j = 0; j<inv.getSize()-1; ){
						item = inv.getItem(j);
						if(item != null){
							if(item.getType().equals(Material.SKULL_ITEM) &&
									item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
									item.getItemMeta().getDisplayName().equalsIgnoreCase("§7Inventários do player §c§l" + ownerName)){
								inv.setItem(j, null);
								if(j+1 != 53 && inv.getItem(j+1) != null){
									inv.setItem(j, inv.getItem(j+1));
									inv.setItem(j+1, null);
								}else if(j == 0 && i != 0) inventarios.get(i-1).setItem(53, null);
							}
						}else{
							if(inv.getItem(j+1) != null)
								inv.setItem(j, inv.getItem(j+1));
							else
								break;
						}
						j++;
					}
					if(inv.firstEmpty() == 0 && i != 0){
						inventarios.remove(i);
						for(HumanEntity players : inv.getViewers()){
							players.getOpenInventory().close();
							players.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Pagina_Nao_Mais_Existe").replace("&", "§"));
						}
					}
				}
			}
		}.runTaskAsynchronously(instance);
	}

	public void removePlayerForInventories(String ownerName) {
		new BukkitRunnable() {
			@Override
			public void run() {
				playersInventories.remove(ownerName);
				for(int i = 0; i<savedInventories.size(); i++)
					if(savedInventories.get(i).getOwnerName().equalsIgnoreCase(ownerName)){
						savedInventories.remove(i);
						i--;
					}
				removePlayerForInventoriesGeral(ownerName);
			}
		}.runTaskAsynchronously(instance);
	}

	/**
	 * A method to serialize an inventory to Base64 string.
	 * 
	 * <p />
	 * 
	 * Special thanks to Comphenix in the Bukkit forums or also known
	 * as aadnk on GitHub.
	 * 
	 * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
	 * 
	 * @param inventory to serialize
	 * @return Base64 string of the provided inventory
	 * @throws IllegalStateException
	 */
	public String toBase64(Inventory inventory) throws IllegalStateException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			// Write the size of the inventory
			dataOutput.writeInt(inventory.getSize());

			// Save every element in the list
			for (int i = 0; i < inventory.getSize(); i++) {
				dataOutput.writeObject(inventory.getItem(i));
			}

			// Serialize that array
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save item stacks.", e);
		}
	}

	/**
	 * 
	 * A method to get an {@link Inventory} from an encoded, Base64, string.
	 * 
	 * <p />
	 * 
	 * Special thanks to Comphenix in the Bukkit forums or also known
	 * as aadnk on GitHub.
	 * 
	 * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
	 * 
	 * @param data Base64 string of data containing an inventory.
	 * @return Inventory created from the Base64 string.
	 * @throws IOException
	 */
	public Inventory fromBase64(String data, int id) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			Inventory inventory = instance.getServer().createInventory(null, dataInput.readInt(), "§7Inventário §c§l" + id);

			// Read the serialized inventory
			for (int i = 0; i < inventory.getSize(); i++) {
				inventory.setItem(i, (ItemStack) dataInput.readObject());
			}

			dataInput.close();
			return inventory;
		} catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type.", e);
		}
	}

}

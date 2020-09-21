package me.zfork.fkits;

import java.io.File;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import me.zfork.fkits.kits.KitsManager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class FKits extends JavaPlugin{
	
	private KitsManager kitsmanager;
	private Inventory inv;
	//private HashMap<String, Inventory> invs = new HashMap<String, Inventory>();
	private Random r = new Random();
	
	@SuppressWarnings("deprecation")
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bFKits§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bzFork");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
		}else{
			getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
		}
		getServer().getConsoleSender().sendMessage(" §3Criando inv dos kits...");
		if(getConfig().getInt("Config.GUI.Linhas") == 1){
			inv = Bukkit.createInventory(null, 9, "Kits");
		}else if(getConfig().getInt("Config.GUI.Linhas") == 2){
			inv = Bukkit.createInventory(null, 18, "Kits");
		}else if(getConfig().getInt("Config.GUI.Linhas") == 3){
			inv = Bukkit.createInventory(null, 27, "Kits");
		}else if(getConfig().getInt("Config.GUI.Linhas") == 4){
			inv = Bukkit.createInventory(null, 36, "Kits");
		}else if(getConfig().getInt("Config.GUI.Linhas") == 5){
			inv = Bukkit.createInventory(null, 45, "Kits");
		}else{
			inv = Bukkit.createInventory(null, 54, "Kits");
		}
		getServer().getConsoleSender().sendMessage(" §3Inv dos kits criados!");
		for(int i = 0; i<inv.getSize(); i++){
			byte data = (byte) r.nextInt(16);
			inv.setItem(i, new ItemStack(Material.getMaterial(160), 1, data == 8 ? 0 : data));
		}
		for(String kit : getConfig().getConfigurationSection("Config.Kits").getKeys(false)){
			try{
				ItemStack item = null;
				if(getConfig().getString("Config.Kits." + kit + ".Item_GUI").contains(":")){
					String[] partes = getConfig().getString("Config.Kits." + kit + ".Item_GUI").split(":");
					item = new ItemStack(Material.getMaterial(Integer.parseInt(partes[0])), 1, Byte.parseByte(partes[1]));
				}else{
					item = new ItemStack(Material.getMaterial(Integer.parseInt(getConfig().getString("Config.Kits." + kit + ".Item_GUI"))), 1);
				}
				ItemMeta im = item.getItemMeta();
				im.setDisplayName("§e§lKit " + kit);
				im.setLore(Arrays.asList("§aClique esquerdo para pegar o kit!", "§cClique direito para visualizar os itens!"));
				item.setItemMeta(im);
				inv.setItem(getConfig().getInt("Config.Kits." + kit + ".Local_GUI"), item);
				/*Inventory inv = Bukkit.createInventory(null, 9, "Kits - " + kit);
				for(int i = 0; i<inv.getSize(); i++){
					byte data = (byte) r.nextInt(16);
					inv.setItem(i, new ItemStack(Material.getMaterial(160), 1, data == 8 ? 0 : data));
				}
				inv.setItem(4, item);
				invs.put(kit.toLowerCase(), inv);*/
			}catch(Exception e){}
		}
		kitsmanager = new KitsManager(this);
		kitsmanager.loadKits();
		Bukkit.getServer().getPluginManager().registerEvents(new Eventos(), this);
		getServer().getConsoleSender().sendMessage("§3==========[§bFKits§3]==========");
	}

	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§4==========[§cFKits§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §czFork");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cFKits§4]==========");
	}

	public static FKits getFKits(){
		return (FKits) Bukkit.getServer().getPluginManager().getPlugin("FKits");
	}
	
	public KitsManager getKitsManager(){
		return this.kitsmanager;
	}
	
	public Inventory getKitsInventory(){
		return this.inv;
	}
	
	/*public Inventory getInv(String kit){
		return invs.get(kit.toLowerCase());
	}*/
	
	public String getTime(long time) {
		String format = "";
		long hours = TimeUnit.MILLISECONDS.toHours(time);
		long hoursInMillis = TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(time - hoursInMillis);
		long minutesInMillis = TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(time - (hoursInMillis + minutesInMillis));
		if (hours > 0)
			format = hours + (hours > 1 ? " horas" : " hora");
		if (minutes > 0) {
			if ((seconds > 0) && (hours > 0))
				format += ", ";
			else if (hours > 0)
				format += " e ";
			format += minutes + (minutes > 1 ? " minutos" : " minuto");
		}
		if (seconds > 0) {
			if ((hours > 0) || (minutes > 0))
				format += " e ";
			format += seconds + (seconds > 1 ? " segundos" : " segundo");
		}
		if (format.equals("")) {
			long rest = time / 100;
			if (rest == 0)
				rest = 1;
			format = "0." + rest + " segundos";
		}
		return format;
	}

}

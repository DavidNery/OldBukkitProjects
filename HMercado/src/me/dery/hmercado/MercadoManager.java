package me.dery.hmercado;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class MercadoManager {

	public static HMercado instance = HMercado.getHMercado();
	public static YamlConfiguration fc = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "items.yml"));
	public static HashMap<Integer, Inventory> paginas = new HashMap<Integer, Inventory>();
	public static HashMap<String, Integer> quantidade = new HashMap<String, Integer>();
	public static int pg = 1, local = 0;

	public static void loadItens(){
		Inventory inv = instance.getServer().createInventory(null, 54, "§6§lMercado - §7§lPagina " + pg);
		paginas.put(pg, inv);
		int i = 0, qnt = 0;
		for(String donos : fc.getConfigurationSection("Items").getKeys(false)){
			//List<String> item = new ArrayList<String>();
			for(String items : fc.getStringList("Items." + donos)){
				if((inv.firstEmpty() + 1) == 54){
					ItemStack botao = new ItemStack(Material.STONE_BUTTON);
					ItemMeta im = botao.getItemMeta();
					im.setDisplayName("§a» Proxima pagina »");
					botao.setItemMeta(im);
					paginas.get(pg).setItem(53, botao);
					ItemStack botaovoltar = new ItemStack(Material.STONE_BUTTON);
					ItemMeta imvoltar = botaovoltar.getItemMeta();
					imvoltar.setDisplayName("§c« Pagina anterior «");
					botaovoltar.setItemMeta(imvoltar);
					paginas.get(pg).setItem(45, botaovoltar);
					pg++;
					inv = instance.getServer().createInventory(null, 54, "§6§lMercado - §7§lPagina " + pg);
					i = 0;
					paginas.put(pg, inv);
					paginas.get(pg).setItem(i, buildItemStack(items, donos, items.split(";")[5]));
					qnt++;
					//item.add(ItemStackToString(buildItemStack(items), Integer.parseInt(items.split(";")[5])));
				}else{
					paginas.get(pg).setItem(i, buildItemStack(items, donos, items.split(";")[5]));
					i++;
					qnt++;
					//item.add(ItemStackToString(buildItemStack(items), Integer.parseInt(items.split(";")[5])));
				}
				if(!quantidade.containsKey(donos)){
		        	quantidade.put(donos, 1);
		        }else{
		        	quantidade.put(donos, quantidade.get(donos) + 1);
		        }
			}
		}
		instance.getServer().getConsoleSender().sendMessage(" §b" + qnt + "§3 itens carregados!");
	}

	@SuppressWarnings("deprecation")
	public static void venderItem(Player p, int preco, String item){
		List<String> items;
		if(fc.contains("Items." + p.getName())){
			items = fc.getStringList("Items." + p.getName());
			items.add(item);
			fc.set("Items." + p.getName(), items);
		}else{
			items = new ArrayList<String>();
			items.add(item);
			fc.set("Items." + p.getName(), items);
		}
		try {
			fc.save(new File(instance.getDataFolder(), "items.yml"));
			fc.load(new File(instance.getDataFolder(), "items.yml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		p.setItemInHand(null);
		p.updateInventory();
		if(paginas.size() == 0){
			Inventory inv = instance.getServer().createInventory(null, 54, "§6§lMercado - §7§lPagina " + pg);
			paginas.put(pg, inv);
			paginas.get(pg).setItem(local, buildItemStack(item, p.getName(), preco + ""));
			local++;
		}else{
			if((paginas.get(pg).firstEmpty() + 1) == 54){
				ItemStack botao = new ItemStack(Material.STONE_BUTTON);
				ItemMeta im = botao.getItemMeta();
				im.setDisplayName("§a» Proxima pagina »");
				botao.setItemMeta(im);
				paginas.get(pg).setItem(53, botao);
				ItemStack botaovoltar = new ItemStack(Material.STONE_BUTTON);
				ItemMeta imvoltar = botaovoltar.getItemMeta();
				imvoltar.setDisplayName("§c« Pagina anterior «");
				botaovoltar.setItemMeta(imvoltar);
				paginas.get(pg).setItem(45, botaovoltar);
				pg++;
				Inventory inv = instance.getServer().createInventory(null, 54, "§6§lMercado - §7§lPagina " + pg);
				local = 0;
				paginas.put(pg, inv);
				paginas.get(pg).setItem(local, buildItemStack(item, p.getName(), preco + ""));
			}else{
				paginas.get(pg).setItem(local, buildItemStack(item, p.getName(), preco + ""));
				local++;
			}
		}
	}

	public static HashMap<Integer, Inventory> getPaginas(){
		return paginas;
	}
	
	public static HashMap<String, Integer> getQuantidade(){
		return quantidade;
	}

	public static Inventory openInventory(int pagina){
		return paginas.get(pagina);
	}

	public static int getPage(){
		return pg;
	}

	public static FileConfiguration getItens(){
		return fc;
	}

	public static void setPg(int pagina){
		pg = pagina;
	}

	public static String ItemStackToString(ItemStack i){
		String item = "";
		item += i.getTypeId();
		MaterialData data = i.getData();
		if(data.getData() != 0){
			item += ":" + data.getData();
		}
		item += ";" + i.getAmount() + ";";
		if(i.getEnchantments().size() != 0){
			/*Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
			enchants.putAll(i.getEnchantments());*/
			for(Enchantment e : i.getEnchantments().keySet()){
				item += translateEnchantmentToPortuguese(e.getName()) + ":" + i.getEnchantmentLevel(e) + "-";
			}
			item = item.substring(0, item.length() - 1) + ";";
		}else{
			item += ";";
		}
		if(i.getItemMeta().getDisplayName() != null){
			item += i.getItemMeta().getDisplayName().replace("§", "&") + ";";
		}else{
			item += ";";
		}
		if(i.hasItemMeta() && i.getItemMeta().getLore() != null){
			for(String lore : i.getItemMeta().getLore()){
				item += lore.replace("§", "&") + "_";
			}
			item = item.substring(0, item.length() - 1) + ";";
		}else{
			item += ";";
		}
		return item;
	}

	public static String ItemStackToString(ItemStack i, int preco){
		String item = "";
		item += i.getTypeId();
		MaterialData data = i.getData();
		if(data.getData() != 0){
			item += ":" + data.getData();
		}
		item += ";" + i.getAmount() + ";";
		if(i.getEnchantments().size() != 0){
			/*Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
			enchants.putAll(i.getEnchantments());*/
			for(Enchantment e : i.getEnchantments().keySet()){
				item += translateEnchantmentToPortuguese(e.getName()) + ":" + i.getEnchantmentLevel(e) + "-";
			}
			item = item.substring(0, item.length() - 1) + ";";
		}else{
			item += ";";
		}
		if(i.getItemMeta().getDisplayName() != null){
			item += i.getItemMeta().getDisplayName().replace("§", "&") + ";";
		}else{
			item += ";";
		}
		if(i.hasItemMeta() && i.getItemMeta().getLore() != null){
			for(String lore : i.getItemMeta().getLore()){
				item += lore.replace("§", "&") + "_";
			}
			item = item.substring(0, item.length() - 1) + ";";
		}else{
			item += ";";
		}
		item += preco;
		return item;
	}

	public static ItemStack buildItemStack(String items, String vendedor, String preco){
		String[] partes = items.split(";");
		String item = partes[0];
		int quantidade = Integer.parseInt(partes[1]);
		ItemStack i = null;
		if(item.contains(":")){
			String id = item.split(":")[0];
			int data = Integer.parseInt(item.split(":")[1]);
			if(Material.matchMaterial(id) != null){
				i = new ItemStack(Material.getMaterial(Integer.parseInt(id)), quantidade, (byte) data);
			}
		}else{
			if(Material.matchMaterial(item) != null){
				i = new ItemStack(Material.getMaterial(Integer.parseInt(item)), quantidade);
			}
		}
		if(!partes[2].replaceAll("\\s+", "").equals("")){
			String[] penchants = partes[2].split("-");
			for(String en : penchants){
				String name = translateEnchantmentToEnglish(en.split(":")[0]);
				int level = Integer.parseInt(en.split(":")[1]);
				i.addUnsafeEnchantment(Enchantment.getByName(name), level);
			}
		}
		if(!partes[3].replaceAll("\\s+", "").equals("")){
			String nome = partes[3];
			ItemMeta im = i.getItemMeta();
			im.setDisplayName(nome.replace("&", "§"));
			i.setItemMeta(im);
		}
		if(!partes[4].replaceAll("\\s+", "").equals("")){
			String[] plore = partes[4].split("_");
			List<String> lore = new ArrayList<String>();
			for(String lores : plore){
				lore.add(lores.replace("&", "§"));
			}
			ItemMeta im = i.getItemMeta();
			im.setLore(lore);
			i.setItemMeta(im);
		}
		ItemMeta im = i.getItemMeta();
		List<String> lore = new ArrayList<String>();
		if(im.getLore() != null){
			lore.addAll(i.getItemMeta().getLore());
		}
		lore.add("§3§lVendedor: §f§l" + vendedor);
		lore.add("§2§lPreco: §f§l" + NumberFormat.getCurrencyInstance().format(Integer.parseInt(preco)).replace("$", ""));
		im.setLore(lore);
		i.setItemMeta(im);
		return i;
	}

	public static ItemStack buildItemStack(String items){
		String[] partes = items.split(";");
		String item = partes[0];
		int quantidade = Integer.parseInt(partes[1]);
		ItemStack i = null;
		if(item.contains(":")){
			String id = item.split(":")[0];
			int data = Integer.parseInt(item.split(":")[1]);
			if(Material.matchMaterial(id) != null){
				i = new ItemStack(Material.getMaterial(Integer.parseInt(id)), quantidade, (byte) data);
			}
		}else{
			if(Material.matchMaterial(item) != null){
				i = new ItemStack(Material.getMaterial(Integer.parseInt(item)), quantidade);
			}
		}
		if(!partes[2].replaceAll("\\s+", "").equals("")){
			String[] penchants = partes[2].split("-");
			for(String en : penchants){
				String name = translateEnchantmentToEnglish(en.split(":")[0]);
				int level = Integer.parseInt(en.split(":")[1]);
				i.addUnsafeEnchantment(Enchantment.getByName(name), level);
			}
		}
		if(!partes[3].replaceAll("\\s+", "").equals("")){
			String nome = partes[3];
			ItemMeta im = i.getItemMeta();
			im.setDisplayName(nome.replace("&", "§"));
			i.setItemMeta(im);
		}
		if(!partes[4].replaceAll("\\s+", "").equals("")){
			String[] plore = partes[4].split("_");
			List<String> lore = new ArrayList<String>();
			for(String lores : plore){
				lore.add(lores.replace("&", "§"));
			}
			ItemMeta im = i.getItemMeta();
			im.setLore(lore);
			i.setItemMeta(im);
		}
		return i;
	}

	public static String translateEnchantmentToPortuguese(String enchant){
		String en = "";
		switch(enchant.toUpperCase()){
		case "PROTECTION_ENVIRONMENTAL":
			en = "protecao";
			break;
		case "PROTECTION_FIRE":
			en = "protecao_fogo";
			break;
		case "PROTECTION_FALL":
			en = "protecao_queda";
			break;
		case "PROTECTION_EXPLOSIONS":
			en = "protecao_explosao";
			break;
		case "PROTECTION_PROJECTILE":
			en = "protecao_flecha";
			break;
		case "OXYGEN":
			en = "respiracao";
			break;
		case "WATER_WORKER":
			en = "afinidade_aquatica";
			break;
		case "THORNS":
			en = "espinhos";
			break;
		case "DAMAGE_ALL":
			en = "afiada";
			break;
		case "DAMAGE_UNDEAD":
			en = "julgamento";
			break;
		case "DAMAGE_ARTHROPODS":
			en = "ruina_artropodes";
			break;
		case "KNOCKBACK":
			en = "repulsao";
			break;
		case "FIRE_ASPECT":
			en = "aspecto_flamejante";
			break;
		case "LOOT_BONUS_MOBS":
			en = "pilhagem";
			break;
		case "DIG_SPEED":
			en = "eficiencia";
			break;
		case "SILK_TOUCH":
			en = "toque_suave";
			break;
		case "DURABILITY":
			en = "inquebravel";
			break;
		case "LOOT_BONUS_BLOCKS":
			en = "fortuna";
			break;
		case "ARROW_DAMAGE":
			en = "forca";
			break;
		case "ARROW_KNOCKBACK":
			en = "impacto";
			break;
		case "ARROW_FIRE":
			en = "chama";
			break;
		case "ARROW_INFINITE":
			en = "infinidade";
			break;
		}
		return en;
	}

	public static String translateEnchantmentToEnglish(String enchant){
		String en = "";
		switch(enchant.toLowerCase()){
		case "protecao":
			en = "PROTECTION_ENVIRONMENTAL";
			break;
		case "protecao_fogo":
			en = "PROTECTION_FIRE";
			break;
		case "protecao_queda":
			en = "PROTECTION_FALL";
			break;
		case "protecao_explosao":
			en = "PROTECTION_EXPLOSIONS";
			break;
		case "protecao_flecha":
			en = "PROTECTION_PROJECTILE";
			break;
		case "respiracao":
			en = "OXYGEN";
			break;
		case "afinidade_aquatica":
			en = "WATER_WORKER";
			break;
		case "espinhos":
			en = "THORNS";
			break;
		case "afiada":
			en = "DAMAGE_ALL";
			break;
		case "julgamento":
			en = "DAMAGE_UNDEAD";
			break;
		case "ruina_artropodes":
			en = "DAMAGE_ARTHROPODS";
			break;
		case "repulsao":
			en = "KNOCKBACK";
			break;
		case "aspecto_flamejante":
			en = "FIRE_ASPECT";
			break;
		case "pilhagem":
			en = "LOOT_BONUS_MOBS";
			break;
		case "eficiencia":
			en = "DIG_SPEED";
			break;
		case "toque_suave":
			en = "SILK_TOUCH";
			break;
		case "inquebravel":
			en = "DURABILITY";
			break;
		case "fortuna":
			en = "LOOT_BONUS_BLOCKS";
			break;
		case "forca":
			en = "ARROW_DAMAGE";
			break;
		case "impacto":
			en = "ARROW_KNOCKBACK";
			break;
		case "chama":
			en = "ARROW_FIRE";
			break;
		case "infinidade":
			en = "ARROW_INFINITE";
			break;
		}
		return en;
	}

}

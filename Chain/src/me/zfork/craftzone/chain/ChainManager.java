package me.zfork.craftzone.chain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class ChainManager {

	private Main instance;
	private ArrayList<String> players;
	private HashMap<String, Integer> kills;
	private HashMap<String, Integer> deaths;
	private ArrayList<ItemStack> items;
	private ItemStack[] armor;
	private Player topkiller;
	private int topkillerkills;
	private HashMap<String, PlayerKillingSpree> killingspree;

	public ChainManager(Main instance){
		this.instance = instance;
		this.topkiller = null;
		this.topkillerkills = 0;
		this.players = new ArrayList<>();
		this.kills = new HashMap<>();
		this.deaths = new HashMap<>();
		this.items = new ArrayList<>();
		this.armor = new ItemStack[4];
		instance.getServer().getConsoleSender().sendMessage(" §3Carregando itens...");
		for(String s : instance.getConfig().getStringList("Config.Itens.Inv")) items.add((ItemStack) criarItem(s));
		armor[0] = (ItemStack) criarItem(instance.getConfig().getStringList("Config.Itens.Armor").get(3));
		armor[1] = (ItemStack) criarItem(instance.getConfig().getStringList("Config.Itens.Armor").get(2));
		armor[2] = (ItemStack) criarItem(instance.getConfig().getStringList("Config.Itens.Armor").get(1));
		armor[3] = (ItemStack) criarItem(instance.getConfig().getStringList("Config.Itens.Armor").get(0));
		instance.getServer().getConsoleSender().sendMessage(" §b" + instance.getConfig().getStringList("Config.Itens.Inv").size() + " §3itens carregados!");
		killingspree = new HashMap<>();
	}

	public boolean hasPlayerInChain(String player){
		return players.stream().anyMatch(p -> p.equalsIgnoreCase(player));
	}

	@SuppressWarnings("deprecation")
	public void addPlayer(Player p){
		p.teleport(getLocation("Spawn"), TeleportCause.PLUGIN);
		players.add(p.getName().toLowerCase());
		if(!kills.containsKey(p.getName().toLowerCase())){
			kills.put(p.getName().toLowerCase(), 0);
			deaths.put(p.getName().toLowerCase(), 0);
		}
		for(ItemStack item : items) p.getInventory().setItem(p.getInventory().firstEmpty(), item);
		p.getInventory().setArmorContents(armor);
		p.updateInventory();
		if(!killingspree.containsKey(p.getName().toLowerCase())) killingspree.put(p.getName().toLowerCase(), new PlayerKillingSpree());
	}

	@SuppressWarnings("deprecation")
	public void delPlayer(Player p, boolean tpsaida){
		if(players.contains(p.getName().toLowerCase())){
			players.remove(p.getName().toLowerCase());
			if(tpsaida) p.teleport(getLocation("Saida"));
			for(int i = 0; i<36; i++){
				if(p.getInventory().getItem(i) != null)
					if(!instance.getConfig().getStringList("Config.Item_WhiteList").contains(""+p.getInventory().getItem(i).getTypeId()))
						p.getInventory().setItem(i, null);
			}
			p.getInventory().setArmorContents(null);
			p.updateInventory();
			if(killingspree.get(p.getName().toLowerCase()).getTask() != null) killingspree.get(p.getName().toLowerCase()).getTask().cancel();
			killingspree.remove(p.getName().toLowerCase());
		}
	}

	public ArrayList<String> getPlayers() {
		return players;
	}

	public HashMap<String, Integer> getKills() {
		return kills;
	}

	public HashMap<String, Integer> getDeaths() {
		return deaths;
	}

	public ArrayList<ItemStack> getItems() {
		return items;
	}

	public ItemStack[] getArmor() {
		return armor;
	}
	
	public Player getTopkiller() {
		return topkiller;
	}
	
	public void setTopkiller(Player topkiller) {
		this.topkiller = topkiller;
	}
	
	public int getTopkillerkills() {
		return topkillerkills;
	}
	
	public void setTopkillerkills(int topkillerkills) {
		this.topkillerkills = topkillerkills;
	}
	
	public HashMap<String, PlayerKillingSpree> getKillingspree() {
		return killingspree;
	}

	public Location getLocation(String type){
		String[] partes = instance.getConfig().getString("Locations." + type).split(" ");
		return new Location(instance.getServer().getWorld(partes[0]), 
				Double.parseDouble(partes[1]), Double.parseDouble(partes[2]), Double.parseDouble(partes[3]),
				Float.parseFloat(partes[4]), Float.parseFloat(partes[5]));
	}

	public void setLocation(Player p, String type){
		String loc = p.getWorld().getName() + " " + p.getLocation().getX() + " " + p.getLocation().getY() + " " + p.getLocation().getZ() + " "
				+ p.getLocation().getYaw() + " " + p.getLocation().getPitch();
		instance.getConfig().set("Locations." + type, loc);
		instance.saveConfig();
		instance.reloadConfig();
	}
	
	public void setItens(Player p){
		items.clear();
		for(ItemStack item : p.getInventory().getContents())
			if(item != null && item.getType() != Material.AIR) items.add(item);
		armor[0] = p.getInventory().getBoots();
		armor[1] = p.getInventory().getLeggings();
		armor[2] = p.getInventory().getChestplate();
		armor[3] = p.getInventory().getHelmet();
		List<String> sitems = new ArrayList<>();
		for(ItemStack item : p.getInventory().getContents())
			if(item != null && item.getType() != Material.AIR) sitems.add(criarItemString(item));
		instance.getConfig().set("Config.Itens.Inv", sitems);
		instance.getConfig().set("Config.Itens.Armor", Arrays.asList(criarItemString(p.getInventory().getHelmet()), 
				criarItemString(p.getInventory().getChestplate()), 
				criarItemString(p.getInventory().getLeggings()), 
				criarItemString(p.getInventory().getBoots())));
		instance.saveConfig();
		instance.reloadConfig();
	}

	public Object criarItem(String string){
		Object item = null;
		String[] partes = string.split(" ");
		int qnt = 1;
		String tipo = "";
		boolean splash = false;
		int tempo = 1;
		int amplificador = 1;
		try{
			for(String parte : partes){
				if(parte.toLowerCase().startsWith("item:")){
					String[] id = parte.split("item:");
					if(id[1].contains(":"))
						item = new ItemStack(Material.getMaterial(Integer.parseInt(id[1].split(":")[0])), qnt, Byte.parseByte(id[1].split(":")[1]));
					else
						item = new ItemStack(Material.getMaterial(Integer.parseInt(id[1])), qnt);
				}else if(parte.toLowerCase().startsWith("pocao:")){
					String[] id = parte.split("pocao:");
					tipo = traduzirPocao(id[1].toLowerCase());
					item = new Potion(PotionType.getByEffect(PotionEffectType.getByName(tipo)));
					for(String p : partes){
						if(p.toLowerCase().startsWith("splash:")){
							splash = Boolean.parseBoolean(p.toLowerCase().split("splash:")[1]);
							if(splash) ((Potion) item).splash();
						}else if(p.toLowerCase().startsWith("duracao:")){
							tempo = Integer.parseInt(p.toLowerCase().split("duracao:")[1]);
						}else if(p.toLowerCase().startsWith("amplificador:")){
							amplificador = Integer.parseInt(p.toLowerCase().split("amplificador:")[1]);
						}
					}
					item = ((Potion) item).toItemStack(qnt);
					PotionMeta pm = (PotionMeta) ((ItemStack) item).getItemMeta();
					pm.addCustomEffect(new PotionEffect(PotionEffectType.getByName(tipo), tempo*20, amplificador - 1), splash);
					((ItemStack) item).setItemMeta(pm);
				}else if(parte.toLowerCase().startsWith("cabeca:")){
					String[] id = parte.split("cabeca:");
					item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
					SkullMeta meta = (SkullMeta) ((ItemStack) item).getItemMeta();
					meta.setOwner(id[1]);
					((ItemStack) item).setItemMeta(meta);
				}else if(parte.toLowerCase().startsWith("enchants:")){
					String[] enchants = parte.split("enchants:");
					for(String enchant : enchants[1].split(",")){
						String[] partesenchant = enchant.split(":");
						((ItemStack) item).addUnsafeEnchantment(Enchantment.getByName(traduzirEnchant(partesenchant[0])), Integer.parseInt(partesenchant[1]));
					}
				}else if(parte.toLowerCase().startsWith("nome:")){
					ItemMeta im = ((ItemStack) item).getItemMeta();
					im.setDisplayName(parte.split("nome:")[1].replace("_", " ").replace("&", "§"));
					((ItemStack) item).setItemMeta(im);
				}else if(parte.toLowerCase().startsWith("lore:")){
					List<String> lore = new ArrayList<String>();
					for(String l : parte.split("(?i)lore:")[1].split("@")){
						lore.add(l.replace("_", " ").replace("&", "§"));
					}
					ItemMeta im = ((ItemStack) item).getItemMeta();
					im.setLore(lore);
					((ItemStack) item).setItemMeta(im);
				}else if(parte.toLowerCase().startsWith("qnt:")){
					qnt = Integer.parseInt(parte.toLowerCase().split("qnt:")[1]);
					if(item.getClass().equals(ItemStack.class)){
						((ItemStack) item).setAmount(qnt);
					}else{
						((Potion) item).toItemStack(qnt);
					}
				}else if(parte.toLowerCase().startsWith("chance:")){
					if((Math.random()*100) > Integer.parseInt(parte.toLowerCase().split("chance:")[1])){
						return null;
					}
				}
			}
			return item;
		}catch(Exception e){
			return null;
		}	
	}

	public String traduzirEnchant(String enchant){
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

	public String traduzirPocao(String pocao){
		String po = "";
		switch(pocao.toLowerCase()){
		case "velocidade":
			po = "SPEED";
			break;
		case "forca":
			po = "INCREASE_DAMAGE";
			break;
		case "lentidao":
			po = "SLOW";
			break;
		case "escavar-rapido":
			po = "FAST_DIGGING";
			break;
		case "escavar-lento":
			po = "SLOW_DIGGING";
			break;
		case "vida-instantanea":
			po = "HEAL";
			break;
		case "dano-instantaneo":
			po = "HARM";
			break;
		case "pulo":
			po = "JUMP";
			break;
		case "nausea":
			po = "CONFUSION";
			break;
		case "regeneracao":
			po = "REGENERATION";
			break;
		case "resistencia":
			po = "DAMAGE_RESISTANCE";
			break;
		case "resistencia-fogo":
			po = "FIRE_RESISTANCE";
			break;
		case "resistencia-agua":
			po = "WATER_BREATHING";
			break;
		case "invisibilidade":
			po = "INVISIBILITY";
			break;
		case "cegueira":
			po = "BLINDNESS";
			break;
		case "visao-noturna":
			po = "NIGHT_VISION";
			break;
		case "fome":
			po = "HUNGER";
			break;
		case "fraqueza":
			po = "WEAKNESS";
			break;
		case "veneno":
			po = "POISON";
			break;
		}
		return po;
	}

	public String criarItemString(ItemStack item){
		String string = "";
		if(item == null){
			string += "item:0";
		}else{
			if(item.getType().equals(Material.POTION)){
				Potion pocao = Potion.fromItemStack(item);
				string += "pocao:" + traduzirPocaoPT(pocao.getType().name().toUpperCase());
				string += " splah:" + pocao.isSplash();
				for(PotionEffect pe : pocao.getEffects()){
					string += " duracao:" + pe.getDuration()/20;
					string += " amplificador:" + pe.getAmplifier()+1;
				}
			}else if(item.getType().equals(Material.SKULL_ITEM) && ((SkullMeta) item.getItemMeta()).hasOwner()){
				SkullMeta sm = (SkullMeta) item.getItemMeta();
				string += "cabeca:"+sm.getOwner();
			}else{
				string += "item:"+item.getTypeId();
			}
			if(item.getData().getData() != 0) string += ":" + item.getData().getData();
			if(item.getAmount() > 1) string += " qnt:" + item.getAmount();
			for(Map.Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()){
				if(!string.contains("enchants:"))
					string += " enchants:" + traduzirEnchantPT(enchants.getKey().getName().toUpperCase()) + ":" + enchants.getValue();
				else
					string += "," + traduzirEnchantPT(enchants.getKey().getName().toUpperCase()) + ":" + enchants.getValue();
			}
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasDisplayName()) string += " nome:" + item.getItemMeta().getDisplayName().replace("§", "&").replace(" ", "_");
				if(item.getItemMeta().hasLore()){
					string += " lore:";
					for(String l : item.getItemMeta().getLore()) string += l.replace("§", "&").replace(" ", "_") + "@";
					string = string.substring(0, string.length()-1);
				}
			}
		}
		return string;
	}

	public String traduzirEnchantPT(String enchant){
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

	public String traduzirPocaoPT(String pocao){
		String po = "";
		switch(pocao.toUpperCase()){
		case "SPEED":
			po = "velocidade";
			break;
		case "INCREASE_DAMAGE":
			po = "forca";
			break;
		case "SLOW":
			po = "lentidao";
			break;
		case "FAST_DIGGING":
			po = "escavar-rapido";
			break;
		case "SLOW_DIGGING":
			po = "escavar-lento";
			break;
		case "HEAL":
			po = "vida-instantanea";
			break;
		case "HARM":
			po = "dano-instantaneo";
			break;
		case "JUMP":
			po = "pulo";
			break;
		case "CONFUSION":
			po = "nausea";
			break;
		case "REGENERATION":
			po = "regeneracao";
			break;
		case "DAMAGE_RESISTANCE":
			po = "resistencia";
			break;
		case "FIRE_RESISTANCE":
			po = "resistencia-fogo";
			break;
		case "WATER_BREATHING":
			po = "resistencia-agua";
			break;
		case "INVISIBILITY":
			po = "invisibilidade";
			break;
		case "BLINDNESS":
			po = "cegueira";
			break;
		case "NIGHT_VISION":
			po = "visao-noturna";
			break;
		case "HUNGER":
			po = "fome";
			break;
		case "WEAKNESS":
			po = "fraqueza";
			break;
		case "POISON":
			po = "veneno";
			break;
		}
		return po;
	}

}

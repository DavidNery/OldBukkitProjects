package me.zfork.spartanoscraft.spartanosbitcoins.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;

import me.zfork.spartanoscraft.spartanosbitcoins.SpartanosBitCoins;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class InventoryUtils {

	private final SpartanosBitCoins instance;
	private final ActionsManager actionsmanager;
	private final LinkedHashMap<String, BitInventory> pluginInventories;
	private final LinkedHashMap<String, Listener> inventoryListeners;

	private final ConfirmInventory confirmInventory;

	public InventoryUtils(SpartanosBitCoins instance) {
		this.instance = instance;
		this.pluginInventories = new LinkedHashMap<>();
		this.inventoryListeners = new LinkedHashMap<>();
		this.actionsmanager = instance.getActionsManager();

		// Criando inventário principal
		Inventory mainInv = createInventory("mainInventory", instance.getConfig().getString("Config.MainInv.Nome").replace("&", "§"), 
				instance.getConfig().getInt("Config.MainInv.Tamanho"), null);
		final LinkedHashMap<Integer, LinkedHashSet<String>> slotsAction = new LinkedHashMap<>();
		final LinkedHashMap<Integer, String> itemWithVariables = new LinkedHashMap<>();
		for(String slots : instance.getConfig().getConfigurationSection("Config.MainInv.Slots").getKeys(false)){
			if(instance.getConfig().contains("Config.MainInv.Slots." + slots + ".Acoes")){
				LinkedHashSet<String> actions = new LinkedHashSet<>();
				actions.addAll(instance.getConfig().getStringList("Config.MainInv.Slots." + slots + ".Acoes"));
				slotsAction.put(Integer.parseInt(slots)-1, actions);
			}
			String stringItem = instance.getConfig().getString("Config.MainInv.Slots." + slots + ".Item");
			ItemStack item = (ItemStack) criarItem(stringItem);
			if(stringItem.contains("{player}") || stringItem.contains("{pontos}"))
				itemWithVariables.put(Integer.parseInt(slots)-1, stringItem);
			if(slots.contains(",")){
				for(String slot : slots.split(","))
					mainInv.setItem(Integer.parseInt(slot)-1, item);
			}else if(slots.contains("-")){
				String[] partes = slots.split("-");
				for(int i = Integer.parseInt(partes[0])-1; i<=Integer.parseInt(partes[1])-1; i++)
					mainInv.setItem(i, item);
			}else{
				mainInv.setItem(Integer.parseInt(slots)-1, item);
			}
		}
		setInventorySlotsAction("mainInventory", instance.getConfig().getString("Config.MainInv.Nome").replace("&", "§"), slotsAction);
		pluginInventories.get("mainInventory").setItemWithVariables(itemWithVariables);
		// Inventário principal criado :)

		// Criando inventário de opcoes
		Inventory confirmInv = createInventory("confirmInventory", instance.getConfig().getString("Config.ConfirmInv.Nome").replace("&", "§"), 
				instance.getConfig().getInt("Config.ConfirmInv.Tamanho"), null);
		final LinkedHashMap<Integer, LinkedHashSet<String>> confirmSlotsAction = new LinkedHashMap<>();
		final LinkedHashMap<Integer, String> confirmItemWithVariables = new LinkedHashMap<>();
		for(String slots : instance.getConfig().getConfigurationSection("Config.ConfirmInv.Slots").getKeys(false)){
			if(instance.getConfig().contains("Config.ConfirmInv.Slots." + slots + ".Acoes")){
				LinkedHashSet<String> actions = new LinkedHashSet<>();
				actions.addAll(instance.getConfig().getStringList("Config.ConfirmInv.Slots." + slots + ".Acoes"));
				confirmSlotsAction.put(Integer.parseInt(slots)-1, actions);
			}
			String stringItem = instance.getConfig().getString("Config.ConfirmInv.Slots." + slots + ".Item");
			ItemStack item = (ItemStack) criarItem(stringItem);
			if(stringItem.contains("{player}") || stringItem.contains("{pontos}"))
				confirmItemWithVariables.put(Integer.parseInt(slots)-1, stringItem);
			if(slots.contains(",")){
				for(String slot : slots.split(","))
					confirmInv.setItem(Integer.parseInt(slot)-1, item);
			}else if(slots.contains("-")){
				String[] partes = slots.split("-");
				for(int i = Integer.parseInt(partes[0])-1; i<=Integer.parseInt(partes[1])-1; i++)
					confirmInv.setItem(i, item);
			}else{
				confirmInv.setItem(Integer.parseInt(slots)-1, item);
			}
		}
		this.confirmInventory = new ConfirmInventory(instance, confirmInv, confirmSlotsAction, confirmItemWithVariables);
		// Inventário de opcoes criado :)

		// Criando os outros inventários
		for(String inventarios : instance.getConfig().getConfigurationSection("Config.OtherInvs.").getKeys(false)){
			Inventory inv = createInventory(inventarios, instance.getConfig().getString("Config.OtherInvs." + inventarios + ".Nome").replace("&", "§"), 
					instance.getConfig().getInt("Config.OtherInvs." + inventarios + ".Tamanho"), null);
			final LinkedHashMap<Integer, LinkedHashSet<String>> otherSlotsAction = new LinkedHashMap<>();
			final LinkedHashMap<Integer, String> otherItemWithVariables = new LinkedHashMap<>();
			for(String slots : instance.getConfig().getConfigurationSection("Config.OtherInvs." + inventarios + ".Slots").getKeys(false)){
				if(instance.getConfig().contains("Config.OtherInvs." + inventarios + ".Slots." + slots + ".Acoes")){
					LinkedHashSet<String> actions = new LinkedHashSet<>();
					actions.addAll(instance.getConfig().getStringList("Config.OtherInvs." + inventarios + ".Slots." + slots + ".Acoes"));
					otherSlotsAction.put(Integer.parseInt(slots)-1, actions);
				}
				String stringItem = instance.getConfig().getString("Config.OtherInvs." + inventarios + ".Slots." + slots + ".Item");
				ItemStack item = (ItemStack) criarItem(stringItem);
				if(stringItem.contains("{player}") || stringItem.contains("{pontos}"))
					otherItemWithVariables.put(Integer.parseInt(slots)-1, stringItem);
				if(slots.contains(",")){
					for(String slot : slots.split(","))
						inv.setItem(Integer.parseInt(slot)-1, item);
				}else if(slots.contains("-")){
					String[] partes = slots.split("-");
					for(int i = Integer.parseInt(partes[0])-1; i<=Integer.parseInt(partes[1])-1; i++)
						inv.setItem(i, item);
				}else{
					inv.setItem(Integer.parseInt(slots)-1, item);
				}
			}
			setInventorySlotsAction(inventarios, instance.getConfig().getString("Config.OtherInvs." + inventarios + ".Nome").replace("&", "§"), otherSlotsAction);
			pluginInventories.get(inventarios).setItemWithVariables(otherItemWithVariables);
		}
		// Todos os outros foram criados :)
	}

	public LinkedHashMap<String, BitInventory> getInventarios() {
		return pluginInventories;
	}

	public LinkedHashMap<String, Listener> getInventoryListeners() {
		return inventoryListeners;
	}
	
	public ConfirmInventory getConfirmInventory() {
		return confirmInventory;
	}

	public Inventory createInventory(String inventoryName, String inventoryTitle, int inventorySize, LinkedHashMap<Integer, LinkedHashSet<String>> slotsAction) {
		if(pluginInventories.containsKey(inventoryName)) return null;
		Inventory inv = null;
		if(inventoryTitle == null)
			inv = instance.getServer().createInventory(null, inventorySize == -1 ? 54 : inventorySize);
		else
			inv = instance.getServer().createInventory(null, inventorySize == -1 ? 54 : inventorySize, inventoryTitle);
		if(slotsAction != null){
			Listener listener = new Listener() {

				@EventHandler
				public void InventoryClick(InventoryClickEvent e){
					Inventory inventario = e.getInventory();
					if(inventario.getTitle().equalsIgnoreCase(inventoryTitle)){
						e.setCancelled(true);
						for(Entry<Integer, LinkedHashSet<String>> slot : slotsAction.entrySet()){
							if(e.getSlot() == slot.getKey()){
								for(String action : slot.getValue()){
									boolean checkaction = actionsmanager.checkAction((Player) e.getWhoClicked(), inventoryTitle, e.getSlot(), action);
									if(checkaction) break;
								}
								break;
							}
						}
					}
				}

			};
			inventoryListeners.put(inventoryName, listener);
		}
		pluginInventories.put(inventoryName, new BitInventory(instance, inv));
		return inv;
	}

	public void createInventory(String inventoryName) {
		createInventory(inventoryName, null, -1, null);
	}

	public void createInventory(String inventoryName, String inventoryTitle) {
		createInventory(inventoryName, inventoryTitle, -1, null);
	}

	public void createInventory(String inventoryName, int inventorySize) {
		createInventory(inventoryName, null, inventorySize, null);
	}

	public void createInventory(String inventoryName, LinkedHashMap<Integer, LinkedHashSet<String>> slotsAction) {
		createInventory(inventoryName, null, -1, slotsAction);
	}

	public void setInventorySlotsAction(String inventoryName, String inventoryTitle, LinkedHashMap<Integer, LinkedHashSet<String>> slotsAction) {
		if(slotsAction.size() == 0) return;
		Listener listener = new Listener() {

			@EventHandler
			public void InventoryClick(InventoryClickEvent e){
				Inventory inventario = e.getInventory();
				if(inventario.getTitle().equalsIgnoreCase(inventoryTitle)){
					e.setCancelled(true);
					for(Entry<Integer, LinkedHashSet<String>> slot : slotsAction.entrySet()){
						if(e.getSlot() == slot.getKey()){
							for(String action : slot.getValue()){
								boolean checkaction = actionsmanager.checkAction((Player) e.getWhoClicked(), inventoryName, e.getSlot(), action);
								if(checkaction) break;
							}
							break;
						}
					}
				}
			}

		};
		inventoryListeners.put(inventoryName, listener);
	}

	public Inventory cloneInventory(Inventory inventoryToClone) {
		Inventory inv = instance.getServer().createInventory(null, inventoryToClone.getSize(), inventoryToClone.getTitle());
		inv.addItem(inventoryToClone.getContents());
		return inv;
	}

	@SuppressWarnings("deprecation")
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
						item = new ItemStack(Material.getMaterial(Integer.parseInt(id[1].split(":")[0])), qnt);
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
				}
			}
		}catch(Exception e){}
		return item;
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

}

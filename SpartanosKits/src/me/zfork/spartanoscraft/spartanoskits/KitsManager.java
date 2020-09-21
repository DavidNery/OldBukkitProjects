package me.zfork.spartanoscraft.spartanoskits;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.earth2me.essentials.Kit;
import com.earth2me.essentials.MetaItemStack;

public class KitsManager {

	private SpartanosKits instance;
	private Inventory maininv;
	private LinkedHashMap<Integer, String> maininvslots;
	private LinkedHashMap<String, Inventory> otherinvs;
	private LinkedHashMap<String, LinkedHashMap<Integer, String>> otherinvsslots;
	private LinkedHashMap<String, LinkedHashMap<Integer, Inventory>> kitspreview;

	public KitsManager(SpartanosKits instance) {
		this.instance = instance;
		this.otherinvs = new LinkedHashMap<String, Inventory>();
		this.maininvslots = new LinkedHashMap<Integer, String>();
		this.otherinvsslots = new LinkedHashMap<String, LinkedHashMap<Integer,String>>();
		this.kitspreview = new LinkedHashMap<String, LinkedHashMap<Integer, Inventory>>();
		createMainInv();
		createOtherInvs();
		createKitsPreview();
	}
	
	public void reload() {
		maininvslots.clear();
		otherinvs.clear();
		otherinvsslots.clear();
		kitspreview.clear();
		createMainInv();
		createOtherInvs();
		createKitsPreview();
	}

	public Inventory getMainInv() {
		return maininv;
	}

	public LinkedHashMap<Integer, String> getMainInvSlots() {
		return maininvslots;
	}

	public LinkedHashMap<String, LinkedHashMap<Integer, String>> getOtherInvsSlots() {
		return otherinvsslots;
	}

	public HashMap<String, Inventory> getOtherInvs() {
		return otherinvs;
	}
	
	public Inventory getPreviewInventory(String kit, int pagina) {
		return kitspreview.get(kit.toLowerCase()).get(pagina);
	}

	private void createMainInv() {
		this.maininv = instance.getServer().createInventory(null, 
				instance.getConfig().getInt("MainInv.Tamanho"), 
				instance.getConfig().getString("MainInv.Nome").replace("&", "§"));
		for(String slots : instance.getConfig().getConfigurationSection("MainInv.Slots").getKeys(false)){
			ItemStack item = (ItemStack) instance.criarItem(instance.getConfig().getString("MainInv.Slots." + slots + ".Item"));
			if(slots.contains(",")){
				for(String slot : slots.split(","))
					maininv.setItem(Integer.parseInt(slot)-1, item);
			}else if(slots.contains("-")){
				String[] partes = slots.split("-");
				for(int i = Integer.parseInt(partes[0])-1; i<=Integer.parseInt(partes[1])-1; i++)
					maininv.setItem(i, item);
			}else{
				maininv.setItem(Integer.parseInt(slots)-1, item);
			}
			if(instance.getConfig().contains("MainInv.Slots." + slots + ".Acao")){
				String acao = instance.getConfig().getString("MainInv.Slots." + slots + ".Acao");
				if(acao.startsWith("abrir inv ") || acao.startsWith("dar kit ")){
					if(slots.contains(",")){
						for(String slot : slots.split(","))
							maininvslots.put(Integer.parseInt(slot)-1, acao);
					}else if(slots.contains("-")){
						String[] partes = slots.split("-");
						for(int i = Integer.parseInt(partes[0])-1; i<=Integer.parseInt(partes[1])-1; i++)
							maininvslots.put(i, acao);
					}else{
						maininvslots.put(Integer.parseInt(slots)-1, acao);
					}
				}
			}
		}
	}

	private void createOtherInvs() {
		for(String inventarios : instance.getConfig().getConfigurationSection("OtherInvs").getKeys(false)){
			Inventory inv = instance.getServer().createInventory(null, 
					instance.getConfig().getInt("OtherInvs." + inventarios + ".Tamanho"), 
					instance.getConfig().getString("OtherInvs." + inventarios + ".Nome").replace("&", "§"));
			for(String slots : instance.getConfig().getConfigurationSection("OtherInvs." + inventarios + ".Slots").getKeys(false)){
				ItemStack item = (ItemStack) instance.criarItem(instance.getConfig().getString("OtherInvs." + inventarios + ".Slots." + slots + ".Item"));
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
				if(instance.getConfig().contains("OtherInvs." + inventarios + ".Slots." + slots + ".Acao")){
					String acao = instance.getConfig().getString("OtherInvs." + inventarios + ".Slots." + slots + ".Acao");
					if(acao.startsWith("abrir inv ") || acao.startsWith("dar kit ")){
						if(otherinvsslots.containsKey(inventarios)){
							if(slots.contains(",")){
								for(String slot : slots.split(","))
									otherinvsslots.get(inventarios).put(Integer.parseInt(slot)-1, acao);
							}else if(slots.contains("-")){
								String[] partes = slots.split("-");
								for(int i = Integer.parseInt(partes[0])-1; i<=Integer.parseInt(partes[1])-1; i++)
									otherinvsslots.get(inventarios).put(i, acao);
							}else{
								otherinvsslots.get(inventarios).put(Integer.parseInt(slots)-1, acao);
							}
						}else{
							LinkedHashMap<Integer, String> otherslots = new LinkedHashMap<Integer, String>();
							if(slots.contains(",")){
								for(String slot : slots.split(","))
									otherslots.put(Integer.parseInt(slot)-1, acao);
							}else if(slots.contains("-")){
								String[] partes = slots.split("-");
								for(int i = Integer.parseInt(partes[0])-1; i<Integer.parseInt(partes[1])-1; i++)
									otherslots.put(i, acao);
							}else{
								otherslots.put(Integer.parseInt(slots)-1, acao);
							}
							otherinvsslots.put(inventarios, otherslots);
						}
					}
				}
			}
			otherinvs.put(inventarios, inv);
		}
	}

	private void createKitsPreview() {
		ItemStack nextItem = (ItemStack) instance.criarItem(instance.getConfig().getString("Preview_Inv.NextItem"));
		ItemStack previousItem = (ItemStack) instance.criarItem(instance.getConfig().getString("Preview_Inv.PreviousItem"));
		for(String kits : instance.getEssentials().getSettings().getKits().getKeys(false)){
			try {
				Kit kit = new Kit(kits, instance.getEssentials());
				LinkedHashMap<Integer, Inventory> invkits = new LinkedHashMap<>();
				Inventory inv = instance.getServer().createInventory(null, 54, instance.getConfig().getString("Preview_Inv.Nome").replace("&", "§")
						.replace("{kit}", kits).replace("{pagina}", "1"));
				preencherPreviewInventory(inv);
				int pagina = 1;
				for(String kitItem : kit.getItems()){
					if(!(kitItem.startsWith(instance.getEssentials().getSettings().getCurrencySymbol()) || kitItem.startsWith("/"))){
						String[] partes = kitItem.split(" +");
						MetaItemStack metaitemstack = new MetaItemStack(instance.getEssentials().getItemDb().get(partes[0], partes.length > 1 ? Integer.parseInt(partes[1]) : 1));
						if(partes.length > 2)
							metaitemstack.parseStringMeta(null, true, partes, 2, instance.getEssentials());
						ItemStack item = metaitemstack.getItemStack();
						if(item.getType() != Material.AIR){
							int freeSlot = getFreeSlotInPreviewKitsInventory(inv);
							if(freeSlot != -1){
								inv.setItem(freeSlot, item);
							}else{
								inv.setItem(instance.getConfig().getInt("Preview_Inv.NextItem_Local")-1, nextItem);
								invkits.put(pagina, inv);
								inv = instance.getServer().createInventory(null, 54, instance.getConfig().getString("Preview_Inv.Nome").replace("&", "§")
										.replace("{kit}", kits).replace("{pagina}", ""+(pagina+1)));
								preencherPreviewInventory(inv);
								inv.setItem(instance.getConfig().getInt("Preview_Inv.PreviousItem_Local")-1, previousItem);
								inv.setItem(getFreeSlotInPreviewKitsInventory(inv), item);
								pagina++;
							}
						}
					}
				}
				if(pagina == 1 && invkits.get(kits.toLowerCase()) == null)
					invkits.put(pagina, inv);
				kitspreview.put(kits.toLowerCase(), invkits);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getFreeSlotInPreviewKitsInventory(Inventory inv){
		String slots = instance.getConfig().getString("Preview_Inv.ItemsSlots");
		if(slots.contains(",")){
			for(String slot : slots.split(","))
				if(inv.getItem(Integer.parseInt(slot)-1) == null) return Integer.parseInt(slot)-1;
		}else if(slots.contains("-")){
			String[] partes = slots.split("-");
			for(int i = Integer.parseInt(partes[0])-1; i<=Integer.parseInt(partes[1])-1; i++)
				if(inv.getItem(i) == null) return i;
		}
		return -1;
	}
	
	public void preencherPreviewInventory(Inventory inv){
		for(String slots : instance.getConfig().getConfigurationSection("Preview_Inv.Slots").getKeys(false)){
			ItemStack item = (ItemStack) instance.criarItem(instance.getConfig().getString("Preview_Inv.Slots." + slots));
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
	}

}

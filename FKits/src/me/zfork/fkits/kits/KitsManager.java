package me.zfork.fkits.kits;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import me.zfork.fkits.FKits;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class KitsManager {

	private ArrayList<Kit> kits;
	private FKits fkits;
	private int qntkits = 0;
	private int[] posicoesgrade = {0,8,45,53};
	private int[] posicoesvidro = {1,2,3,4,5,6,7,9,18,27,36,17,26,35,44,47,48,49,50,51};
	private Random r;
	private FileConfiguration fc;
	private File f;

	public KitsManager(FKits fkits){
		this.fkits = fkits;
		kits = new ArrayList<Kit>();
		r = new Random();
		f = new File(fkits.getDataFolder(), "kits.yml");
		if(!f.exists()){
			try {
				f.createNewFile();
			}catch(IOException e){}
		}
		fc = YamlConfiguration.loadConfiguration(f);
	}

	public void loadKits(){
		fkits.getServer().getConsoleSender().sendMessage(" §3Carregando kits...");
		for(String kit : fkits.getConfig().getConfigurationSection("Config.Kits").getKeys(false)){
			int pg = 1;
			Inventory inv = fkits.getServer().createInventory(null, 54, "Kit " + kit + " - " + pg);
			criarInv(inv);
			List<Object> items = new ArrayList<Object>();
			for(String item : fkits.getConfig().getStringList("Config.Kits." + kit + ".Items")){
				items.add(criarItem(item));
			}
			Kit newkit = new Kit(kit, items, fkits.getConfig().getInt("Config.Kits." + kit + ".Delay"));
			HashMap<Integer, Inventory> invs = new HashMap<Integer, Inventory>();
			invs.put(pg, inv);
			for(Object item : items){
				if(inv.firstEmpty() != -1){
					invs.get(pg).setItem(invs.get(pg).firstEmpty(), (ItemStack) item);
				}else{
					pg++;
					inv = fkits.getServer().createInventory(null, 54, "Kit " + kit + " - " + pg);
					criarInv(inv);
					inv.setItem(inv.firstEmpty(), (ItemStack) item);
					invs.put(pg, inv);
				}
			}
			newkit.getInventory().put(kit, invs);
			kits.add(newkit);
			qntkits++;
		}
		fkits.getServer().getConsoleSender().sendMessage(" §b" + qntkits + " §3kits carregados!");
	}

	public ArrayList<Kit> getKits(){
		return this.kits;
	}
	
	public Inventory getInventoryKit(String kitname, int page){
		for(Kit kit : kits){
			try{
				if(kit.getNome().equalsIgnoreCase(kitname)){
					return kit.getInventory().get(kit.getNome()).get(page);
				}
			}catch(Exception e){ return null; }
		}
		return null;
	}
	
	public int getQuantidadeKits(){
		return this.qntkits;
	}
	
	public Kit getKit(String kitname){
		for(Kit kit : kits){
			if(kit.getNome().equalsIgnoreCase(kitname)) return kit;
		}
		return null;
	}
	
	public void setPlayer(Kit kit, String player){
		List<String> players = fc.getStringList(kit.getNome());
		if(players.size() == 0){
			players.add(player.toLowerCase() + ":" + (System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(kit.getDelay())));
		}else{
			for(int i = 0; i<players.size(); i++){
				if(players.get(i).toLowerCase().startsWith(player.toLowerCase())){
					players.set(i, player.toLowerCase() + ":" + (System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(kit.getDelay())));
				}
			}
		}
		fc.set(kit.getNome(), players);
		try{
			fc.save(f);
			fc.load(f);
		}catch(IOException | InvalidConfigurationException e){}
	}
	
	public long getDelay(Kit kit, String player){
		for(String kits : fc.getStringList(kit.getNome())){
			if(kits.startsWith(player.toLowerCase())) return Long.parseLong(kits.split(":")[1]);
		}
		return -1;
	}
	
	@SuppressWarnings("deprecation")
	public void criarInv(Inventory inv){
		for(int posicao : posicoesgrade) inv.setItem(posicao, new ItemStack(Material.getMaterial(101)));
		for(int posicao : posicoesvidro){
			byte data = (byte) r.nextInt(16);
			inv.setItem(posicao, new ItemStack(Material.getMaterial(160), 1, data == 8 ? 0 : data));
		}
		ItemStack botao = new ItemStack(Material.STONE_BUTTON);
		ItemMeta botaometa = botao.getItemMeta();
		botaometa.setDisplayName("§c§lVoltar");
		botao.setItemMeta(botaometa);
		inv.setItem(46, botao);
		botaometa.setDisplayName("§a§lPróxima página");
		botao.setItemMeta(botaometa);
		inv.setItem(52, botao);
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
				}else if(parte.toLowerCase().startsWith("enchants:")){
					String[] enchants = partes[2].split("enchants:");
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

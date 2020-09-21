package me.zfork.hjungle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class HJungle extends JavaPlugin{
	
	private JungleManager jm;
	
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bHJungle§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bzFork");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
		}else{
			getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
		}
		jm = new JungleManager();
		Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
		getCommand("jungle").setExecutor(new Comandos());
		getServer().getConsoleSender().sendMessage("§3==========[§bHJungle§3]==========");
	}

	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§4==========[§cHJungle§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §czFork");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cHJungle§4]==========");
	}

	public static HJungle getHJungle(){
		return (HJungle) Bukkit.getServer().getPluginManager().getPlugin("HJungle");
	}
	
	public JungleManager getJungleManager(){
		return this.jm;
	}
	
	public ItemStack buildItemStack(String items){
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
	
	public String translateEnchantmentToEnglish(String enchant){
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

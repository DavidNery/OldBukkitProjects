package me.zfork.spartanoscraft.spartanoskits;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.ess3.api.IEssentials;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class SpartanosKits extends JavaPlugin{
	
	private IEssentials essentials;
	private KitsManager kitsmanager;
	
	public void onEnable(){
		ConsoleCommandSender sender = getServer().getConsoleSender();
		sender.sendMessage("§3==========[§bSpartanosKits§3]==========");
		sender.sendMessage(" §3Status: §bAtivado");
		sender.sendMessage(" §3By: §bzFork");
		sender.sendMessage(" §3Versao: §b" + getDescription().getVersion());
		this.essentials = (IEssentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
		if(essentials != null){
			sender.sendMessage(" §3Essentials OK!");
		}else{
			this.essentials = (IEssentials) Bukkit.getServer().getPluginManager().getPlugin("EssentialsX");
			if(essentials != null){
				sender.sendMessage(" §3Essentials OK!");
			}else{
				sender.sendMessage(" §4Essentials não encontrado :<");
				Bukkit.getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}
		saveDefaultConfig();
		sender.sendMessage(" §3Carregando kits...");
		this.kitsmanager = new KitsManager(this);
		sender.sendMessage(" §3Kits carregados!");
		getServer().getPluginManager().registerEvents(new Eventos(), this);
		sender.sendMessage("§3==========[§bSpartanosKits§3]==========");
	}
	
	public void onDisable(){
		ConsoleCommandSender sender = getServer().getConsoleSender();
		sender.sendMessage("§4==========[§cSpartanosKits§4]==========");
		sender.sendMessage(" §4Status: §cDesativado");
		sender.sendMessage(" §4By: §czFork");
		sender.sendMessage(" §4Versao: §c" + getDescription().getVersion());
		sender.sendMessage("§4==========[§cSpartanosKits§4]==========");
	}

	public static SpartanosKits getSpartanosKits(){
		return (SpartanosKits) Bukkit.getServer().getPluginManager().getPlugin("SpartanosKits");
	}
	
	public IEssentials getEssentials(){
		return essentials;
	}
	
	public KitsManager getKitsManager() {
		return kitsmanager;
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
	
	public String getTime(long millis){
		long days = TimeUnit.MILLISECONDS.toDays(millis);
		long hour = TimeUnit.MILLISECONDS.toHours(millis-TimeUnit.DAYS.toMillis(days));
		long min = TimeUnit.MILLISECONDS.toMinutes((millis-TimeUnit.DAYS.toMillis(days))-TimeUnit.HOURS.toMillis(hour));
		long second = TimeUnit.MILLISECONDS.toSeconds(((millis-TimeUnit.DAYS.toMillis(days))-TimeUnit.HOURS.toMillis(hour))-TimeUnit.MINUTES.toMillis(min));
		StringBuilder msg = new StringBuilder();
		if(days > 0)
			msg.append(days + " " + (days == 1 ? "dia" : "dias") + ", ");
		if(hour > 0)
			msg.append(hour + " " + (hour == 1 ? "hora" : "horas") + ", ");
		if(min > 0)
			msg.append(min + " " + (min == 1 ? "minuto" : "minutos") + ", ");
		if(second > 0)
			msg.append(second + " " + (second == 1 ? "segundo" : "segundos") + ", ");
		if(msg.toString().endsWith(", "))
			msg.delete(msg.length()-2, msg.length());
		if(msg.toString().contains(",") || msg.toString().equals("")){
			try{
				msg = msg.replace(msg.lastIndexOf(","), msg.lastIndexOf(",")+1, " e");
			}catch(StringIndexOutOfBoundsException ex){
				return "menos de um segundo";
			}
		}
		return msg.toString();
	}

}

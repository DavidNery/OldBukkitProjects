package me.zfork.spartanoscraft.spartanosinvrestorer;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import me.zfork.spartanoscraft.spartanosinvrestorer.comandos.Comandos;
import me.zfork.spartanoscraft.spartanosinvrestorer.listeners.Listeners;
import me.zfork.spartanoscraft.spartanosinvrestorer.sql.MySQL;
import me.zfork.spartanoscraft.spartanosinvrestorer.utils.PlayerInventory;
import me.zfork.spartanoscraft.spartanosinvrestorer.utils.PlayerInventoryUtils;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SpartanosInvRestorer extends JavaPlugin{
	
	private PlayerPointsAPI playerPointsAPI;
	
	public static String PLUGIN_NAME;
	private PlayerInventoryUtils playerInventoryUtils;
	private MySQL mysql;
	private BukkitTask task;
	
	public void onEnable(){
		PLUGIN_NAME = getDescription().getName();
		ConsoleCommandSender sender = getServer().getConsoleSender();
		PlayerPoints playerpoints = (PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints");
		if(playerpoints == null){
			sender.sendMessage("§4PlayerPoints nao encontrado!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		this.playerPointsAPI = new PlayerPointsAPI(playerpoints);
		sender.sendMessage("§3==========[§b" + PLUGIN_NAME + "§3]==========");
		sender.sendMessage(" §3Status: §bAtivado");
		sender.sendMessage(" §3By: §bzFork");
		sender.sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			sender.sendMessage(" §3Config: §bCriada");
		}else{
			sender.sendMessage(" §3Config: §bJa Existente");
		}
		try {
			mysql = new MySQL(getConfig().getString("Config.MySQL.User"), getConfig().getString("Config.MySQL.Senha"), 
					getConfig().getString("Config.MySQL.Database"), getConfig().getString("Config.MySQL.Host"));
			this.playerInventoryUtils = new PlayerInventoryUtils(this, mysql);
			mysql.setPlayerInventoryUtils(playerInventoryUtils);
			playerInventoryUtils.loadInventories();
		} catch (ClassNotFoundException | SQLException | IOException e) {
			e.printStackTrace();
			sender.sendMessage(" §4Nao foi possivel ativar o plugin!");
			sender.sendMessage(" §4Reveja sua configuracao MySQL!");
			sender.sendMessage("§3==========[§b" + PLUGIN_NAME + "§3]==========");
			return;
		}
		new Listeners(this);
		new Comandos(this);
		task = new BukkitRunnable() {
			@Override
			public void run() {
				try {
					mysql.saveToDataBase(playerInventoryUtils.getSavedInventories());
				} catch (ClassNotFoundException | SQLException e) {
					sender.sendMessage("");
					sender.sendMessage("");
					e.printStackTrace();
					sender.sendMessage(" §4§lALERTA!!!");
					sender.sendMessage(" §4Nao foi possivel salvar os inventarios no MySQL.");
					sender.sendMessage("");
					sender.sendMessage("");
				}
				for(PlayerInventory playerInventory : playerInventoryUtils.getSavedInventories().stream().filter(pi -> pi.getExpiryDate() <= System.currentTimeMillis()).collect(Collectors.toList())){
					if(!playerInventory.getPodeComprar()){
						playerInventory.setExpiryDate(System.currentTimeMillis()+TimeUnit.HOURS.toMillis(getConfig().getInt("Config.Tempo_Expirar_Player_Inventory_Geral")));
						playerInventory.setPodeComprar(true);
						ArrayList<Inventory> inventariosPlayer = playerInventoryUtils.getPlayersInventoriesGeral().get(playerInventory.getOwnerName());
						Inventory playerInv;
						if(inventariosPlayer == null){
							playerInventoryUtils.addPlayerInInventory(playerInventory.getOwnerName());
							inventariosPlayer = new ArrayList<>();
							playerInv = getServer().createInventory(null, 54, "§7Inventários §f- §c§l" + playerInventory.getOwnerName() + " §f- §c§l1");
							inventariosPlayer.add(playerInv);
							playerInventoryUtils.getPlayersInventoriesGeral().put(playerInventory.getOwnerName(), inventariosPlayer);
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
							playerInv.setItem(53, playerInventoryUtils.getProximo());
							playerPagina++;
							playerInv = getServer().createInventory(null, 54, "§7Inventários §f- §c§l" + playerInventory.getOwnerName() + " §f- §c§l" + playerPagina);
							inventariosPlayer.add(playerInv);
							playerInv.setItem(45, playerInventoryUtils.getVoltar());
							playerInv.setItem(playerInv.firstEmpty(), item);
						}else{
							if(firstEmpty == 45 && playerPagina != 1)
								playerInv.setItem(firstEmpty+1, item);
							else
								playerInv.setItem(firstEmpty, item);
						}
						if(playerInventory.getItemRaro())
							for(Player on : getServer().getOnlinePlayers()){
								on.sendMessage(getConfig().getString("Mensagem.Sucesso.Ficou_Disponivel").replace("&", "§")
										.replace("{player}", playerInventory.getOwnerName()).replace("{pontos}", playerInventory.getPreco()+""));
						}
					}else{
						playerInventoryUtils.removePlayerInventory(playerInventory);
						playerInventory.getPreviewInventory().getViewers().forEach(p -> p.getOpenInventory().close());
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 60*20, 60*20);
		sender.sendMessage("§3==========[§b" + PLUGIN_NAME + "§3]==========");
	}

	public void onDisable(){
		if(task != null) task.cancel();
		HandlerList.unregisterAll(this);
		ConsoleCommandSender sender = getServer().getConsoleSender();
		try {
			mysql.saveToDataBase(playerInventoryUtils.getSavedInventories());
		} catch (ClassNotFoundException | SQLException e) {
			sender.sendMessage("");
			sender.sendMessage("");
			sender.sendMessage(" §4§lALERTA!!!");
			sender.sendMessage(" §4Nao foi possivel salvar os inventarios no MySQL.");
			sender.sendMessage("");
			sender.sendMessage("");
		}
		sender.sendMessage("§4==========[§c" + PLUGIN_NAME + "§4]==========");
		sender.sendMessage(" §4Status: §cDesativado");
		sender.sendMessage(" §4By: §czFork");
		sender.sendMessage(" §4Versao: §c" + getDescription().getVersion());
		sender.sendMessage("§4==========[§c" + PLUGIN_NAME + "§4]==========");
	}

	public static SpartanosInvRestorer getSpartanosInvRestorer(){
		return (SpartanosInvRestorer) Bukkit.getServer().getPluginManager().getPlugin(PLUGIN_NAME);
	}
	
	public PlayerInventoryUtils getPlayerInventoryUtils() {
		return playerInventoryUtils;
	}
	
	public PlayerPointsAPI getPlayerPointsAPI() {
		return playerPointsAPI;
	}
	
	public boolean hasEffect(Potion potion, PotionEffectType effect) {
		for(PotionEffect pe : potion.getEffects()) if(pe.getType().equals(effect)) return true;
		return false;
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
					if(id[1].contains(",")){
						String sorteado = id[1];
						if(sorteado.contains(":"))
							item = new ItemStack(Material.getMaterial(Integer.parseInt(sorteado.split(":")[0])), qnt, Byte.parseByte(id[1].split(":")[1]));
						else
							item = new ItemStack(Material.getMaterial(Integer.parseInt(sorteado)), qnt);
					}else{
						if(id[1].contains(":"))
							item = new ItemStack(Material.getMaterial(Integer.parseInt(id[1].split(":")[0])), qnt, Byte.parseByte(id[1].split(":")[1]));
						else
							item = new ItemStack(Material.getMaterial(Integer.parseInt(id[1])), qnt);
					}
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

}

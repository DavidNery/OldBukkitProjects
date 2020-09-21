package me.dery.hbau;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class HBau extends JavaPlugin implements Listener{
	
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bHBau§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bDery");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
		}else{
			getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
		}
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		getServer().getConsoleSender().sendMessage("§3==========[§bHBau§3]==========");
	}

	public void onDisable(){
		getServer().getConsoleSender().sendMessage("§4==========[§cHBau§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §cDery");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cHBau§4]==========");
	}
	
	@EventHandler(ignoreCancelled=true)
	public void Open(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock().getType() == Material.AIR || e.getClickedBlock().getType() != Material.CHEST) return;
			if(e.getClickedBlock().hasMetadata("Dono")){
				if(!e.getClickedBlock().getMetadata("Dono").get(0).asString().equals(p.getName())){
					p.sendMessage(getConfig().getString("Nao_Pode_Abrir").replace("&", "§"));
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void Bau(BlockBreakEvent e){
		final Player p = e.getPlayer();
		if(p.getGameMode() == GameMode.CREATIVE) return;
		final Block b = e.getBlock();
		if((Math.random() * 100) <= getConfig().getDouble("Chance")){
			if(getConfig().getIntegerList("Blocos").contains(e.getBlock().getTypeId())){
				new BukkitRunnable(){
					@Override
					public void run(){
						b.setType(Material.CHEST);
						final Chest chest = (Chest) b.getLocation().getBlock().getState();
						for(String items : getConfig().getStringList("Items")){
							ItemStack item = buildItemStack(items);
							if(item != null){
								chest.getInventory().addItem(item);
							}
						}
						chest.setMetadata("Dono", new FixedMetadataValue(Bukkit.getServer().getPluginManager().getPlugin("HBau"), p.getName()));
						p.sendMessage(getConfig().getString("Achou_Bau").replace("&", "§"));
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 5.0F, 1.0F);
						shootFireWork(p);
						new BukkitRunnable(){
							@Override
							public void run(){
								chest.getInventory().clear();
								b.setType(Material.AIR);
							}
						}.runTaskLater(Bukkit.getServer().getPluginManager().getPlugin("HBau"), 10*20);
					}
				}.runTaskLater(this, 1L);
				return;
			}
		}
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
		double chance = 100;
		if(!partes[5].replaceAll("\\s+", "").equals("")){
			chance = Double.parseDouble(partes[5]);
		}
		if((Math.random() * 100) <= chance){
			return i;
		}
		return null;
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
	
	public static void shootFireWork(Player player) {
		Location loc = player.getLocation();
		org.bukkit.entity.Firework fw = loc.getWorld().spawn(loc, org.bukkit.entity.Firework.class);
		FireworkMeta fm = fw.getFireworkMeta();
		fm.addEffect(FireworkEffect.builder().with(getFireworkRandomType()).withColor(getFireworkRandomColor(), getFireworkRandomColor())
				.withFade(getFireworkRandomColor()).flicker(getFireworkRandomBoolean()).build());
		fm.setPower(getFireworkRandomPower());
		fw.setFireworkMeta(fm);
	}
	
	public static Color getFireworkRandomColor(){
		Color c = null;
		Random r = new Random();
		int next = r.nextInt(17);
		if(next == 0) {
			c = Color.AQUA;
		}else if(next == 1) {
			c = Color.BLACK;
		}else if(next == 2) {
			c = Color.BLUE;
		}else if(next == 3) {
			c = Color.FUCHSIA;
		}else if(next == 4) {
			c = Color.GRAY;
		}else if(next == 5) {
			c = Color.GREEN;
		}else if(next == 6) {
			c = Color.LIME;
		}else if(next == 7) {
			c = Color.MAROON;
		}else if(next == 8) {
			c = Color.NAVY;
		}else if(next == 9) {
			c = Color.OLIVE;
		}else if(next == 10) {
			c = Color.ORANGE;
		}else if(next == 11) {
			c = Color.PURPLE;
		}else if(next == 12) {
			c = Color.RED;
		}else if(next == 13) {
			c = Color.SILVER;
		}else if(next == 14) {
			c = Color.TEAL;
		}else if(next == 15) {
			c = Color.WHITE;
		}else if(next == 16) {
			c = Color.YELLOW;
		}
		return c;
	}
	
	public static Type getFireworkRandomType(){
		Type t = null;
		Random r = new Random();
		int next = r.nextInt(5);
		if(next == 0) {
			t = Type.BALL;
		}else if(next == 1) {
			t = Type.BALL_LARGE;
		}else if(next == 2) {
			t = Type.BURST;
		}else if(next == 3) {
			t = Type.CREEPER;
		}else if(next == 4) {
			t = Type.STAR;
		}
		return t;
	}
	public static int getFireworkRandomPower(){
		Random r = new Random();
		return r.nextInt(2);
	}
	
	public static Boolean getFireworkRandomBoolean(){
		Random r = new Random();
		return r.nextBoolean();
	}

}

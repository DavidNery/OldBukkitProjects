package me.zfork.ftesouros;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;

public class Eventos implements Listener{
	
	private FTesouros instance = FTesouros.getFTesouros();
	private Random r = new Random();
	private HashMap<String, ArrayList<Location>> blocks = new HashMap<String, ArrayList<Location>>();
	private HashMap<String, ArrayList<Entity>> entities = new HashMap<String, ArrayList<Entity>>();
	
	@EventHandler
	public void McMMOLevelUP(McMMOPlayerLevelUpEvent e){
		Player p = e.getPlayer();
		if(instance.getConfig().contains("Habilidades." + e.getSkill().name() + ".Ativar") && instance.getConfig().getBoolean("Habilidades." + e.getSkill().name() + ".Ativar")
				&& instance.getConfig().getInt("Habilidades." + e.getSkill().name() + ".Level_Min") <= e.getSkillLevel()
				&& (Math.random()*100) <= instance.getConfig().getInt("Habilidades." + e.getSkill().name() + ".Chance")){
			ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta bookmeta = (BookMeta) item.getItemMeta();
			bookmeta.setTitle(instance.getConfig().getString("Config.Livro_Title").replace("&", "§").replace("{player}", p.getName()));
			bookmeta.setAuthor(instance.getConfig().getString("Config.Livro_Author").replace("&", "§").replace("{player}", p.getName()));
			List<String> pages = new ArrayList<String>();
			int x = r.nextInt(instance.getConfig().getInt("Config.X_Max") - instance.getConfig().getInt("Config.X_Min") + 1) + instance.getConfig().getInt("Config.X_Min");
			int z = r.nextInt(instance.getConfig().getInt("Config.Z_Max") - instance.getConfig().getInt("Config.Z_Min") + 1) + instance.getConfig().getInt("Config.Z_Min");
			if(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].contains("1_8"))
				for(String page : instance.getConfig().getStringList("Config.Livro")) pages.add(page.replace("&", "§").replace("{x}", x + "").replace("{z}", z + ""));
			else
				for(String page : instance.getConfig().getStringList("Config.Livro"))
					pages.add("{text:\"" + page.replace("&", "§").replace("{x}", x + "").replace("{z}", z + "") + "\"}");
			bookmeta.setPages(pages);
			int level = 0;
			for(String levels : instance.getConfig().getConfigurationSection("Habilidades." + e.getSkill().name() + ".Levels").getKeys(false)){
				String[] partes = instance.getConfig().getString("Habilidades." + e.getSkill().name() + ".Levels." + levels + ".Skill_Level").split("-");
				if(Integer.parseInt(partes[0]) <= e.getSkillLevel()){
					if(partes.length == 2 && e.getSkillLevel() <= Integer.parseInt(partes[1])){
						level = Integer.parseInt(levels);
						break;
					}
					level = Integer.parseInt(levels);
					break;
				}
			}
			bookmeta.setLore(Arrays.asList("§6Livro do tesouro §e" + level + "§6!", "§3X: §f" + x, "§3Z: §f" + z));
			item.setItemMeta(bookmeta);
			for(int i = 0; i<100; i++) p.sendMessage("");
			p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Achou_Livro").replace("&", "§").replace("{level}", level + ""));
			if(p.getInventory().firstEmpty() != -1){
				p.getInventory().addItem(item);
			}else{
				if(p.getEnderChest().firstEmpty() == -1){
					p.getWorld().dropItemNaturally(p.getLocation(), item).setVelocity(p.getVelocity());
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Livro_Dropado").replace("&", "§"));
				}else{
					p.getEnderChest().addItem(item);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Livro_Adicionado_Ao_EnderChest").replace("&", "§"));
				}
			}
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 10F, 1F);
			if(instance.getConfig().getBoolean("Config.Anunciar_Achou_Livro"))
				for(Player on : instance.getServer().getOnlinePlayers())
					on.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Player_Achou_Livro").replace("&", "§").replace("{level}", level + "").replace("{player}", p.getName()));
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void Interact(PlayerInteractEvent e){
		final Player p = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.CHEST){
			Chest chest = (Chest) e.getClickedBlock().getState();
			if(chest.hasMetadata("FTesouros")){
				if(chest.getMetadata("FTesouros").get(0).asString().equals(p.getName())){
					for(Entity entity : p.getNearbyEntities(10, 15, 10)){
						if(entity instanceof Player && ((Player) entity).getName().equalsIgnoreCase(p.getName())) continue;
						if(!(entity instanceof Player) && !entity.isDead() && entity instanceof LivingEntity && ((LivingEntity) entity).isCustomNameVisible()){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Existe_Mobs_Perto").replace("&", "§"));
							e.setCancelled(true);
							return;
						}
					}
				}else{
					e.setCancelled(true);
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Bau_Nao_E_Seu").replace("&", "§"));
				}
			}
		}else if(p.getItemInHand() != null && p.getItemInHand().getType() == Material.WRITTEN_BOOK){
			if(!p.getWorld().getName().equalsIgnoreCase(instance.getConfig().getString("Config.Mundo_Tesouros"))) return;
			if(p.getItemInHand().hasItemMeta()){
				ItemStack item = p.getItemInHand();
				final BookMeta bookmeta = (BookMeta) item.getItemMeta();
				if(bookmeta.hasTitle() && bookmeta.getTitle().equalsIgnoreCase(instance.getConfig().getString("Config.Livro_Title").replace("&", "§").replace("{player}", p.getName()))){
					if(bookmeta.hasAuthor() && bookmeta.getAuthor().equalsIgnoreCase(instance.getConfig().getString("Config.Livro_Author").replace("&", "§").replace("{player}", p.getName()))){
						if(bookmeta.hasLore() && bookmeta.getLore().size() == 3 && bookmeta.getLore().get(0).matches("§6Livro do tesouro §e\\d+§6!")
								&& bookmeta.getLore().get(1).matches("§3X: §f\\d+") && bookmeta.getLore().get(2).matches("§3Z: §f\\d+")){
							if(Integer.parseInt(bookmeta.getLore().get(1).split("§f")[1]) == (int) p.getLocation().getX()
									&& Integer.parseInt(bookmeta.getLore().get(2).split("§f")[1]) == (int) p.getLocation().getZ()){
								e.setCancelled(true);
								if(p.getOpenInventory() != null) p.getOpenInventory().close();
								new BukkitRunnable(){
									@Override
									public void run(){
										for(int y = (int) p.getLocation().getY(); y<(int)(p.getLocation().getY())+15; y++){
											for(int x = (int)(p.getLocation().getX()-10); x < (int)(p.getLocation().getX()+10); x++){
												if(blocks.containsKey(p.getName())){
													blocks.get(p.getName()).add(new Location(p.getWorld(), x, y, (int)(p.getLocation().getZ()-10)));
												}else{
													ArrayList<Location> blocos = new ArrayList<Location>();
													blocos.add(new Location(p.getWorld(), x, y, (int)(p.getLocation().getZ()-10)));
													blocks.put(p.getName(), blocos);
												}
												Block block = p.getWorld().getBlockAt(x, y, (int)(p.getLocation().getZ()-10));
												block.setType(Material.getMaterial(instance.getConfig().getInt("Config.Bloco_Cercar")));
											}
											for(int x2 = (int)(p.getLocation().getX()-10); x2 <= (int)(p.getLocation().getX()+10); x2++){
												if(blocks.containsKey(p.getName())){
													blocks.get(p.getName()).add(new Location(p.getWorld(), x2, y, (int)(p.getLocation().getZ()+10)));
												}else{
													ArrayList<Location> blocos = new ArrayList<Location>();
													blocos.add(new Location(p.getWorld(), x2, y, (int)(p.getLocation().getZ()+10)));
													blocks.put(p.getName(), blocos);
												}
												Block block = p.getWorld().getBlockAt(x2, y, (int)(p.getLocation().getZ()+10));
												block.setType(Material.getMaterial(instance.getConfig().getInt("Config.Bloco_Cercar")));
											}
											for(int z = (int)(p.getLocation().getZ()-10); z < (int)(p.getLocation().getZ()+10); z++){
												if(blocks.containsKey(p.getName())){
													blocks.get(p.getName()).add(new Location(p.getWorld(), (int)(p.getLocation().getX()-10), y, z));
												}else{
													ArrayList<Location> blocos = new ArrayList<Location>();
													blocos.add(new Location(p.getWorld(), (int)(p.getLocation().getX()-10), y, z));
													blocks.put(p.getName(), blocos);
												}
												Block block = p.getWorld().getBlockAt((int)(p.getLocation().getX()-10), y, z);
												block.setType(Material.getMaterial(instance.getConfig().getInt("Config.Bloco_Cercar")));
											}
											for(int z2 = (int)(p.getLocation().getZ()-10); z2 <= (int)(p.getLocation().getZ()+10); z2++){
												if(blocks.containsKey(p.getName())){
													blocks.get(p.getName()).add(new Location(p.getWorld(), (int)(p.getLocation().getX()+10), y, z2));
												}else{
													ArrayList<Location> blocos = new ArrayList<Location>();
													blocos.add(new Location(p.getWorld(), (int)(p.getLocation().getX()+10), y, z2));
													blocks.put(p.getName(), blocos);
												}
												Block block = p.getWorld().getBlockAt((int)(p.getLocation().getX()+10), y, z2);
												block.setType(Material.getMaterial(instance.getConfig().getInt("Config.Bloco_Cercar")));
											}
										}
										int level = Integer.parseInt(bookmeta.getLore().get(0).split("§e")[1].replace("§6!", ""));
										for(String mobs : instance.getConfig().getStringList("Config.Mobs")){
											String[] partes = mobs.split(">");
											if(level == Integer.parseInt(partes[0])){
												for(String mob : partes[1].split(",")){
													if(EntityType.fromName(mob) != null){
														for(int i = 0; i<Integer.parseInt(partes[2]); i++){
															LivingEntity entity = (LivingEntity) p.getWorld().spawnEntity(p.getLocation(), EntityType.fromName(mob.toUpperCase()));
															entity.setCustomName(partes[3].replace("&", "§").replace("{mob}", entity.getType().name().replace("_", " ")));
															entity.setCustomNameVisible(true);
															entity.setMaxHealth(Integer.parseInt(partes[4]));
															entity.setHealth(Integer.parseInt(partes[4]));
															if(entities.containsKey(p.getName())){
																entities.get(p.getName()).add(entity);
															}else{
																ArrayList<Entity> entidades = new ArrayList<Entity>();
																entidades.add(entity);
																entities.put(p.getName(), entidades);
															}
														}
													}else{
														p.sendMessage(mob + " nao encontrado!");
													}
												}
											}
										}
										if(instance.getSQLType() == 1){
											new BukkitRunnable() {
												@Override
												public void run() {
													try {
														instance.getMySQL().openConnection();
														instance.getMySQL().addAchou(p.getName());
														instance.getMySQL().closeConnection();
													} catch (ClassNotFoundException | SQLException e) {}
												}
											}.runTask(instance);
										}else{
											new BukkitRunnable() {
												@Override
												public void run() {
													try {
														instance.getSQLite().openConnection();
														instance.getSQLite().addAchou(p.getName());
														instance.getSQLite().closeConnection();
													} catch (ClassNotFoundException | SQLException e) {}
												}
											}.runTask(instance);
										}
										p.setItemInHand(null);
										p.updateInventory();
										p.getLocation().getBlock().setType(Material.CHEST);
										blocks.get(p.getName()).add(p.getLocation());
										Chest chest = (Chest) p.getLocation().getBlock().getState();
										chest.setMetadata("FTesouros", new FixedMetadataValue(instance, p.getName()));
										for(String items : instance.getConfig().getStringList("Config.Level." + level)){
											if(chest.getInventory().firstEmpty() != -1){
												ItemStack itemcriado = (ItemStack) instance.criarItem(items, p.getName());
												if(itemcriado != null) chest.getInventory().setItem(chest.getInventory().firstEmpty(), itemcriado);
											}else{
												break;
											}
										}
										new BukkitRunnable() {
											@Override
											public void run() {
												for(Location loc : blocks.get(p.getName())){
													if(loc.getBlock().getType() == Material.CHEST){
														((Chest)loc.getBlock().getState()).getInventory().clear();
													}
													loc.getBlock().setType(Material.AIR);
												}
												for(Entity entity : entities.get(p.getName())) entity.remove();
												blocks.remove(p.getName());
												entities.remove(p.getName());
											}
										}.runTaskLater(instance, instance.getConfig().getInt("Config.Tempo_Remover_Bau")*20);
									}
								}.runTask(instance);
							}
						}
					}
				}
			}
		}
	}

}

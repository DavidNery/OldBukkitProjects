package me.zfork.fchestsell;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FChestSell extends JavaPlugin implements CommandExecutor{
	
	private static String PLUGIN_NAME;
	private static Economy econ = null;
	private LinkedHashMap<String, Long> map;
	
	public void onEnable(){
		PLUGIN_NAME = getDescription().getName();
		ConsoleCommandSender sender = getServer().getConsoleSender();
		sender.sendMessage("§3==========[§b" + PLUGIN_NAME + "§3]==========");
		sender.sendMessage(" §3Status: §bAtivado");
		sender.sendMessage(" §3By: §bzFork");
		sender.sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null){
			sender.sendMessage(" §3Vault: §bNao Encontrado");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}else{
			sender.sendMessage(" §3Vault: §bHooked (Economy)");
			setupEconomy();
			map = new LinkedHashMap<>();
		}
		sender.sendMessage("§3==========[§b" + PLUGIN_NAME + "§3]==========");
	}
	
	public void onDisable(){
		ConsoleCommandSender sender = getServer().getConsoleSender();
		sender.sendMessage("§4==========[§c" + PLUGIN_NAME + "§4]==========");
		sender.sendMessage(" §4Status: §cDesativado");
		sender.sendMessage(" §4By: §czFork");
		sender.sendMessage(" §4Versao: §c" + getDescription().getVersion());
		sender.sendMessage("§4==========[§c" + PLUGIN_NAME + "§4]==========");
	}

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}
	
	public FChestSell getFChestSell(){
		return this;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player){
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("venderlapisb")){
				if(!p.hasPermission("fchestsell.lapis")){
					p.sendMessage("§cVocê não pode utilizar este comando!");
					return true;
				}else if(map.containsKey(p.getName().toLowerCase())){
					p.sendMessage("§cAguarde mais §7" + ((map.get(p.getName().toLowerCase())-System.currentTimeMillis())/1000) + " §csegundos!");
					return true;
				}
				map.put(p.getName().toLowerCase(), System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(30));
				new BukkitRunnable() {
					@Override
					public void run() {
						int x = (int) p.getLocation().getX();
						int y = (int) p.getLocation().getY();
						int z = (int) p.getLocation().getZ();
						int qnt = 0;
						for(int i = x-10; i<=x+10; i++){
							for(int j = y-3; j<=y+3; j++){
								for(int k = z-10; k<=z+10; k++){
									Block b = p.getLocation().getWorld().getBlockAt(i, j, k);
									if(b.getState() instanceof Chest){
										Chest chest = (Chest) b.getState();
										for(BlockFace face : Utils.SHOP_FACES){
											if(b.getRelative(face).getType().equals(Material.WALL_SIGN) || b.getRelative(face).getType().equals(Material.SIGN_POST)){
												Sign sign = (Sign) b.getRelative(face).getState();
												if(Utils.isValidSign(sign.getLines())){
													if(sign.getLine(0).equalsIgnoreCase(p.getName())){
														for(int slot = 0; slot<chest.getInventory().getSize(); slot++){
															ItemStack item = chest.getInventory().getItem(slot);
															if(item == null) continue;
															if(item.getType().equals(Material.INK_SACK) && item.getDurability() == 4){
																qnt += item.getAmount();
																chest.getInventory().setItem(slot, null);
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
						if(qnt == 0){
							p.sendMessage("§cNão haviam §9Lápis Lazulis §cpara serem vendidos!");
						}else{
							double ganho = qnt*0.3038194444444444;
							p.sendMessage("§aVocê vendeu §9" + qnt + " Lápis Lazulis §apor " + ganho + " craftins!");
							econ.depositPlayer(p.getName(), ganho);
						}
						new BukkitRunnable() {
							@Override
							public void run() {
								map.remove(p.getName().toLowerCase());
							}
						}.runTaskLater(getFChestSell(), 30*20);
					}
				}.runTaskAsynchronously(this);
				return true;
			}
		}
		return false;
	}

}

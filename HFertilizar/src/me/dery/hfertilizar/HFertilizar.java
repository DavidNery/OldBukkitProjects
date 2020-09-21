package me.dery.hfertilizar;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HFertilizar extends JavaPlugin implements CommandExecutor{
	
	Economy econ = null;
	HashMap<String, Long> fertilizar = new HashMap<String, Long>();
	
	@Override
	public void onEnable() {
		getServer().getConsoleSender().sendMessage("§3Plugin Habilitado!");
		saveDefaultConfig();
		setupEconomy();
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		getServer().getConsoleSender().sendMessage("§4Plugin Desabilitado!");
	}
	
	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}
	
	public String getTime(long time) {
		String format = "";
		long hours = TimeUnit.MILLISECONDS.toHours(time);
		long hoursInMillis = TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(time - hoursInMillis);
		long minutesInMillis = TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(time - (hoursInMillis + minutesInMillis));
		if (hours > 0)
			format = hours + (hours > 1 ? " horas" : " hora");
		if (minutes > 0) {
			if ((seconds > 0) && (hours > 0))
				format += ", ";
			else if (hours > 0)
				format += " e ";
			format += minutes + (minutes > 1 ? " minutos" : " minuto");
		}
		if (seconds > 0) {
			if ((hours > 0) || (minutes > 0))
				format += " e ";
			format += seconds + (seconds > 1 ? " segundos" : " segundo");
		}
		if (format.equals("")) {
			long rest = time / 100;
			if (rest == 0)
				rest = 1;
			format = "0." + rest + " segundos";
		}
		return format;
	}
	
	public void Fertilizar(Player p, int raio){
		if(!econ.has(p.getName(), getConfig().getDouble("Preco"))){
			p.sendMessage(getConfig().getString("Sem_Dinheiro").replace("&", "§").replace("@necessario", NumberFormat.getNumberInstance().format(getConfig().getDouble("Preco"))).replace("@falta", NumberFormat.getNumberInstance().format(getConfig().getDouble("Preco") - econ.getBalance(p.getName()))));
			return;
		}
		Location loc = p.getLocation();
		boolean ok = false;
		for(int x = (int)loc.getX() - (raio / 2); x<(int)loc.getX() + (raio / 2); x++){
			for(int y = (int)loc.getY() - (raio / 2); y<(int)loc.getY() + (raio / 2); y++){
				for(int z = (int)loc.getZ() - (raio / 2); z<(int)loc.getZ() + (raio / 2); z++){
					Location location = new Location(loc.getWorld(), x, y, z);
					Block block = location.getBlock();
					if(block.isLiquid() || block.getType() == Material.AIR){
						continue;
					}else{
						if(block.getTypeId() == 81){
							for(int to = 1; to<=2; to++){
								if(block.getLocation().add(0, to, 0).getBlock().getType() == Material.AIR){
									if(block.getLocation().subtract(0, 2, 0).getBlock().getType() == Material.CACTUS
											/*&& block.getLocation().subtract(0, 2, 0).getBlock().getType() == Material.CACTUS
											&& block.getLocation().subtract(0, 3, 0).getBlock().getType() == Material.CACTUS
											&& block.getLocation().subtract(0, 4, 0).getBlock().getType() == Material.CACTUS*/){
										continue;
									}else{
										block.getLocation().add(0, to, 0).getBlock().setType(Material.CACTUS);
										ok = true;
									}
								}else{
									break;
								}
							}
						}
						if(block.getTypeId() == 83){
							for(int to = 1; to<=2; to++){
								if(block.getLocation().add(0, to, 0).getBlock().getType() == Material.AIR){
									if(block.getLocation().subtract(0, 2, 0).getBlock().getType() == Material.SUGAR_CANE_BLOCK
											/*&& block.getLocation().subtract(0, 2, 0).getBlock().getType() == Material.SUGAR_CANE_BLOCK
											&& block.getLocation().subtract(0, 3, 0).getBlock().getType() == Material.SUGAR_CANE_BLOCK
											&& block.getLocation().subtract(0, 4, 0).getBlock().getType() == Material.SUGAR_CANE_BLOCK*/){
										continue;
									}else{
										block.getLocation().add(0, to, 0).getBlock().setType(Material.SUGAR_CANE_BLOCK);
										ok = true;
									}
								}else{
									break;
								}
							}
						}
						if(block.getTypeId() == 127){
							if(block.getData() != 8 && block.getData() != 9 && block.getData() != 10 && block.getData() != 11){
								block.setData((byte) (block.getData() + 8));
								ok = true;
							}
						}
						if(block.getTypeId() == 115){
							if(block.getData() != 3){
								block.setData((byte) 3);
								ok = true;
							}
						}
						if(block.getTypeId() == 141){
							if(block.getData() != 15){
								block.setData((byte) 15);
								ok = true;
							}
						}
						if(block.getTypeId() == 142){
							if(block.getData() != 15){
								block.setData((byte) 15);
								ok = true;
							}
						}
					}
				}
			}
		}
		if(ok == true){
			p.sendMessage(getConfig().getString("Fertilizou").replace("&", "§").replace("@preco", NumberFormat.getNumberInstance().format(getConfig().getDouble("Preco"))));
			econ.withdrawPlayer(p.getName(), getConfig().getDouble("Preco"));
			fertilizar.put(p.getName().toLowerCase(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(getConfig().getLong("Tempo")));
		}else{
			p.sendMessage(getConfig().getString("Nao_E_Necessario").replace("&", "§"));
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("fertilizar")){
			if(!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			if(!p.hasPermission("vip.fertilizar")){
				p.sendMessage(getConfig().getString("Sem_Permissao").replace("&", "§"));
				return true;
			}
			if(fertilizar.containsKey(p.getName().toLowerCase()) && fertilizar.get(p.getName().toLowerCase()) > System.currentTimeMillis()){
				long restante = fertilizar.get(p.getName().toLowerCase()) - System.currentTimeMillis();
				p.sendMessage(getConfig().getString("Aguarde").replace("&", "§").replace("@tempo", getTime(restante)));
				return true;
			}
			fertilizar.remove(p.getName());
			Fertilizar(p, (getConfig().getInt("Raio") % 2 == 0 ? getConfig().getInt("Raio") : getConfig().getInt("Raio") + 1));
		}
		return false;
	}
	
}

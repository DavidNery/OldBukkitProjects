package me.zfork.ftesouros;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Comandos implements CommandExecutor{

	private FTesouros instance = FTesouros.getFTesouros();
	private Random r = new Random();
	private HashMap<String, Long> delay = new HashMap<String, Long>();

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("tesouro")){
			if(args.length == 0){
				for(String msg : instance.getConfig().getStringList("Mensagem.Comandos")){
					sender.sendMessage(msg.replace("&", "§"));
				}
				return true;
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Args.Tp"))){
				if(!(sender instanceof Player)) return true;
				Player p = (Player) sender;
				if(!p.hasPermission("ftesouros.tp")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}
				if(args.length <= 2){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Tp").replace("&", "§"));
					return true;
				}else if(args.length == 3){
					if(!isNum(args[1], args[2])){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Informe_Apenas_Numeros").replace("&", "§"));
						return true;
					}
					p.teleport(new Location(instance.getServer().getWorld(instance.getConfig().getString("Config.Mundo_Tesouros")), 
							Integer.parseInt(args[1]), 
							instance.getServer().getWorld(instance.getConfig().getString("Config.Mundo_Tesouros")).getHighestBlockYAt(Integer.parseInt(args[1]), Integer.parseInt(args[2])), 
							Integer.parseInt(args[2])));
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Teleportado").replace("&", "§"));
				}else{
					Player player = instance.getServer().getPlayer(args[4]);
					if(player == null){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§"));
						return true;
					}
					player.teleport(new Location(instance.getServer().getWorld(instance.getConfig().getString("Config.Mundo_Tesouros")), 
							Integer.parseInt(args[1]), 
							instance.getServer().getWorld(instance.getConfig().getString("Config.Mundo_Tesouros")).getHighestBlockYAt(Integer.parseInt(args[1]), Integer.parseInt(args[2])), 
							Integer.parseInt(args[2])));
					player.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Teleportado_Staff").replace("&", "§").replace("{staff}", p.getName()));
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Teleportou").replace("&", "§").replace("{player}", player.getName()));
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Args.Give"))){
				if(!sender.hasPermission("ftesouros.give")){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}else if(args.length <= 1){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto.Give").replace("&", "§"));
					return true;
				}else if(!isNum(args[1])){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Informe_Apenas_Numeros").replace("&", "§"));
					return true;
				}else if(!instance.getConfig().contains("Config.Level." + args[1])){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Level_Alto").replace("&", "§"));
					return true;
				}else if(args.length == 2){
					if(!(sender instanceof Player)) return true;
					Player p = (Player) sender;
					if(p.getInventory().firstEmpty() == -1){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inventario_Cheio").replace("&", "§"));
						return true;
					}
					ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
					BookMeta bookmeta = (BookMeta) item.getItemMeta();
					bookmeta.setTitle(instance.getConfig().getString("Config.Livro_Title").replace("&", "§").replace("{player}", p.getName()));
					bookmeta.setAuthor(instance.getConfig().getString("Config.Livro_Author").replace("&", "§").replace("{player}", p.getName()));
					List<String> pages = new ArrayList<String>();
					int x = r.nextInt(instance.getConfig().getInt("Config.X_Max") - instance.getConfig().getInt("Config.X_Min") + 1) + instance.getConfig().getInt("Config.X_Min");
					int z = r.nextInt(instance.getConfig().getInt("Config.Z_Max") - instance.getConfig().getInt("Config.Z_Min") + 1) + instance.getConfig().getInt("Config.Z_Min");
					for(String page : instance.getConfig().getStringList("Config.Livro")) pages.add(page.replace("&", "§").replace("{x}", x + "").replace("{z}", z + ""));
					bookmeta.setPages(pages);
					bookmeta.setLore(Arrays.asList("§6Livro do tesouro §e" + args[1] + "§6!", "§3X: §f" + x, "§3Z: §f" + z));
					item.setItemMeta(bookmeta);
					p.getInventory().addItem(item);
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Se_Deu_Livro").replace("&", "§").replace("{level}", args[1]));
				}else{
					Player p = instance.getServer().getPlayer(args[2]);
					if(p == null){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Offline").replace("&", "§"));
						return true;
					}
					if(p.getInventory().firstEmpty() == -1){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inventario_Player_Cheio").replace("&", "§").replace("{player}", p.getName()));
						return true;
					}
					ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
					BookMeta bookmeta = (BookMeta) item.getItemMeta();
					bookmeta.setTitle(instance.getConfig().getString("Config.Livro_Title").replace("&", "§").replace("{player}", p.getName()));
					bookmeta.setAuthor(instance.getConfig().getString("Config.Livro_Author").replace("&", "§").replace("{player}", p.getName()));
					List<String> pages = new ArrayList<String>();
					int x = r.nextInt(instance.getConfig().getInt("Config.X_Max") - instance.getConfig().getInt("Config.X_Min") + 1) + instance.getConfig().getInt("Config.X_Min");
					int z = r.nextInt(instance.getConfig().getInt("Config.Z_Max") - instance.getConfig().getInt("Config.Z_Min") + 1) + instance.getConfig().getInt("Config.Z_Min");
					for(String page : instance.getConfig().getStringList("Config.Livro")) pages.add(page.replace("&", "§").replace("{x}", x + "").replace("{z}", z + ""));
					bookmeta.setPages(pages);
					bookmeta.setLore(Arrays.asList("§6Livro do tesouro §e" + args[1] + "§6!", "§3X: §f" + x, "§3Z: §f" + z));
					item.setItemMeta(bookmeta);
					p.getInventory().addItem(item);
					sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Deu_Livro").replace("&", "§").replace("{player}", p.getName()).replace("{level}", args[1]));
					p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Ganhou_Livro").replace("&", "§").replace("{level}", args[1]));
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Args.Cacar"))){
				if(!(sender instanceof Player)) return true;
				Player p = (Player) sender;
				if(p.getItemInHand() != null && p.getItemInHand().getType() == Material.WRITTEN_BOOK){
					if(p.getItemInHand().hasItemMeta()){
						ItemStack item = p.getItemInHand();
						final BookMeta bookmeta = (BookMeta) item.getItemMeta();
						if(bookmeta.hasTitle() && bookmeta.getTitle().equalsIgnoreCase(instance.getConfig().getString("Config.Livro_Title").replace("&", "§").replace("{player}", p.getName()))){
							if(bookmeta.hasAuthor() && bookmeta.getAuthor().equalsIgnoreCase(instance.getConfig().getString("Config.Livro_Author").replace("&", "§").replace("{player}", p.getName()))){
								if(bookmeta.hasLore() && bookmeta.getLore().size() == 3 && bookmeta.getLore().get(0).matches("§6Livro do tesouro §e\\d+§6!")
										&& bookmeta.getLore().get(1).matches("§3X: §f\\d+") && bookmeta.getLore().get(2).matches("§3Z: §f\\d+")){
									int x = r.nextInt(instance.getConfig().getInt("Config.X_Max") - instance.getConfig().getInt("Config.X_Min") + 1) + instance.getConfig().getInt("Config.X_Min");
									int z = r.nextInt(instance.getConfig().getInt("Config.Z_Max") - instance.getConfig().getInt("Config.Z_Min") + 1) + instance.getConfig().getInt("Config.Z_Min");
									p.teleport(new Location(instance.getServer().getWorld(instance.getConfig().getString("Config.Mundo_Tesouros")), 
											x, instance.getServer().getWorld(instance.getConfig().getString("Config.Mundo_Tesouros")).getHighestBlockYAt(x, z), z));
									p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Teleportado_Mundo_Tesouros").replace("&", "§"));
								}else{
									p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Livro_Tesouro_Mao").replace("&", "§"));
									return true;
								}
							}else{
								p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Livro_Tesouro_Mao").replace("&", "§"));
								return true;
							}
						}else{
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Livro_Tesouro_Mao").replace("&", "§"));
							return true;
						}
					}else{
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Livro_Tesouro_Mao").replace("&", "§"));
						return true;
					}
				}else{
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Livro_Tesouro_Mao").replace("&", "§"));
					return true;
				}
			}else if(args[0].equalsIgnoreCase(instance.getConfig().getString("Config.Args.Rank"))){
				if(sender instanceof Player){
					if(delay.containsKey(((Player) sender).getName()) && delay.get(((Player) sender).getName()) > System.currentTimeMillis()){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde_Rank").replace("&", "§").replace("{tempo}", instance.getTime(delay.get(((Player) sender).getName()) - System.currentTimeMillis())));
						return true;
					}
				}
				if(!instance.getConfig().getBoolean("Config.Rank.Ativar")){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Rank_Desativado").replace("&", "§"));
					return true;
				}
				if(instance.getSQLType() == 1){
					new BukkitRunnable() {
						@Override
						public void run() {
							try {
								sender.sendMessage(instance.getConfig().getString("Config.Rank.TOP").replace("&", "§"));
								instance.getMySQL().openConnection();
								instance.getMySQL().getTOPAchou(sender);
								instance.getMySQL().closeConnection();
								sender.sendMessage(instance.getConfig().getString("Config.Rank.END").replace("&", "§"));
							} catch (ClassNotFoundException | SQLException e) {}
						}
					}.runTaskAsynchronously(instance);
				}else{
					new BukkitRunnable() {
						@Override
						public void run() {
							try {
								sender.sendMessage(instance.getConfig().getString("Config.Rank.TOP").replace("&", "§"));
								instance.getSQLite().openConnection();
								instance.getSQLite().getTOPAchou(sender);
								instance.getSQLite().closeConnection();
								sender.sendMessage(instance.getConfig().getString("Config.Rank.END").replace("&", "§"));
							} catch (ClassNotFoundException | SQLException e) {}
						}
					}.runTaskAsynchronously(instance);
				}
			}else{
				for(String msg : instance.getConfig().getStringList("Mensagem.Comandos")){
					sender.sendMessage(msg.replace("&", "§"));
				}
				return true;
			}
		}
		return false;
	}

	public boolean isNum(String ... num){
		try{
			for(String numero : num) Integer.parseInt(numero);
			return true;
		}catch(Exception e){return false;}
	}

}

package me.dery.hjokenpo;

import java.text.NumberFormat;

import me.dery.hjokenpo.desafio.Desafio;
import me.dery.hjokenpo.desafio.DesafioManager;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Comandos implements CommandExecutor{
	
	public static HJokenpo instance = HJokenpo.getHJokenpo();
	public static DesafioManager dm = instance.getDesafioManager();
	
	@SuppressWarnings("static-access")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(!(sender instanceof Player)) return true;
		if(cmd.getName().equalsIgnoreCase("jokenpo")){
			Player p = (Player) sender;
			if(args.length == 0){
				p.sendMessage(instance.getConfig().getString("Uso_Correto").replace("&", "§"));
				return true;
			}else if(args[0].equalsIgnoreCase("desafiar")){
				if(args.length == 1){
					p.sendMessage(instance.getConfig().getString("Uso_Correto_Desafiar").replace("&", "§"));
					return true;
				}
				Player player = instance.getServer().getPlayer(args[1]);
				if(player == null){
					p.sendMessage(instance.getConfig().getString("Player_Offline").replace("&", "§"));
					return true;
				}
				if(args[1].equalsIgnoreCase(p.getName())){
					p.sendMessage(instance.getConfig().getString("Si_Desafiar").replace("&", "§"));
					return true;
				}
				if(dm.isDesafiado(p.getName(), player.getName())){
					p.sendMessage(instance.getConfig().getString("Ja_Esta_Desafio").replace("&", "§").replace("@player", player.getName()));
					return true;
				}
				if(dm.Desafiou(p.getName(), player.getName())){
					p.sendMessage(instance.getConfig().getString("Ja_Desafiou").replace("&", "§").replace("@player", player.getName()));
					return true;
				}
				if(dm.hasPlayer(p.getName()) || dm.hasPlayer(player.getName())){
					p.sendMessage(instance.getConfig().getString("Ja_Esta_Desafio").replace("&", "§"));
					return true;
				}
				if(p.getLocation().getWorld() != player.getLocation().getWorld() || p.getLocation().distance(player.getLocation()) > instance.getConfig().getDouble("Distancia_Desafiar")){
					p.sendMessage(instance.getConfig().getString("Esta_Longe").replace("&", "§"));
					return true;
				}
				Desafio desafio;
				if(args.length >= 3){
					if(!isNum(args[2])){
						p.sendMessage(instance.getConfig().getString("Somente_Numeros").replace("&", "§"));
						return true;
					}
					if(Double.parseDouble(args[2]) <= 1){
						p.sendMessage(instance.getConfig().getString("Valor_Baixo").replace("&", "§"));
						return true;
					}
					desafio = new Desafio(p.getName(), player.getName(), Double.parseDouble(args[2]));
				}else{
					desafio = new Desafio(p.getName(), player.getName());
				}
				if((!instance.econ.has(p.getName(), desafio.getPremio() / 2)) || (!instance.econ.has(player.getName(), desafio.getPremio() / 2))){
					p.sendMessage(instance.getConfig().getString("Sem_Money").replace("&", "§").replace("@money", NumberFormat.getCurrencyInstance().format(desafio.getPremio() / 2).replace("$", "")));
					return true;
				}
				p.sendMessage(instance.getConfig().getString("Desafiou").replace("&", "§").replace("@money", NumberFormat.getCurrencyInstance().format(desafio.getPremio() / 2).replace("$", "")).replace("@player", player.getName()));
				player.sendMessage(instance.getConfig().getString("Desafiado").replace("&", "§").replace("@player", p.getName()).replace("@money", NumberFormat.getCurrencyInstance().format(desafio.getPremio() / 2).replace("$", "")));
				dm.addDesafio(desafio);
				return true;
			}else if(args[0].equalsIgnoreCase("aceitar")){
				if(args.length == 1){
					p.sendMessage(instance.getConfig().getString("Uso_Correto_Aceitar").replace("&", "§"));
					return true;
				}
				Player player = instance.getServer().getPlayer(args[1]);
				if(player == null){
					p.sendMessage(instance.getConfig().getString("Player_Offline").replace("&", "§"));
					return true;
				}
				if(args[1].equalsIgnoreCase(p.getName())){
					p.sendMessage(instance.getConfig().getString("Si_Aceitar").replace("&", "§"));
					return true;
				}
				if(dm.hasPlayer(p.getName()) || dm.hasPlayer(player.getName())){
					p.sendMessage(instance.getConfig().getString("Ja_Esta_Desafio").replace("&", "§"));
					return true;
				}
				if(p.isDead() || player.isDead()){
					p.sendMessage(instance.getConfig().getString("Player_Morto").replace("&", "§"));
					return true;
				}
				if(p.getLocation().getWorld() != player.getLocation().getWorld() || p.getLocation().distance(player.getLocation()) > instance.getConfig().getDouble("Distancia_Desafiar")){
					p.sendMessage(instance.getConfig().getString("Esta_Longe").replace("&", "§"));
					return true;
				}
				Desafio desafio = dm.getDesafio(player.getName(), p.getName());
				if(!dm.hasDesafio(desafio)){
					p.sendMessage(instance.getConfig().getString("Nao_Desafiou").replace("&", "§").replace("@player", player.getName()));
					return true;
				}
				if((!instance.econ.has(p.getName(), desafio.getPremio() / 2)) || (!instance.econ.has(player.getName(), desafio.getPremio() / 2))){
					p.sendMessage(instance.getConfig().getString("Sem_Money").replace("&", "§").replace("@money", NumberFormat.getCurrencyInstance().format(desafio.getPremio() / 2).replace("$", "")));
					return true;
				}
				instance.econ.withdrawPlayer(p.getName(), desafio.getPremio() / 2);
				instance.econ.withdrawPlayer(player.getName(), desafio.getPremio() / 2);
				p.sendMessage(instance.getConfig().getString("Aceitou_Desafio").replace("&", "§").replace("@player", player.getName()));
				player.sendMessage(instance.getConfig().getString("Desafio_Aceito").replace("&", "§").replace("@player", p.getName()));
				dm.addPlayer(p.getName());
				dm.addPlayer(player.getName());
				if(desafio.getTask() != null) desafio.getTask().cancel();
				desafio.setAcontecendo(true);
				openInventory(p);
				openInventory(player);
				return true;
			}else if(args[0].equalsIgnoreCase("info")){
				for(String msg : instance.getConfig().getStringList("Info")){
					p.sendMessage(msg.replace("&", "§"));
				}
				return true;
			}
		}
		return false;
	}
	
	public void openInventory(Player p){
		Inventory inv = instance.getServer().createInventory(null, 45, instance.getConfig().getString("Jokenpo_Inv_Nome").replace("&", "§"));
		if(instance.getConfig().getBoolean("AutoCompletar_Inv") == true){
			ItemStack[] item = new ItemStack[45];
			for(int i = 0; i<45; i++){
				item[i] = new ItemStack(Material.getMaterial(instance.getConfig().getInt("Item_Completar")));
			}
			inv.setContents(item);
		}
		ItemStack pedra = new ItemStack(Material.BEDROCK);
		ItemMeta pedrameta = pedra.getItemMeta();
		pedrameta.setDisplayName("§6Pedra");
		pedra.setItemMeta(pedrameta);
		ItemStack papel = new ItemStack(Material.PAPER);
		ItemMeta papelmeta = papel.getItemMeta();
		papelmeta.setDisplayName("§6Papel");
		papel.setItemMeta(papelmeta);
		ItemStack tesoura = new ItemStack(Material.SHEARS);
		ItemMeta tesourameta = tesoura.getItemMeta();
		tesourameta.setDisplayName("§6Tesoura");
		tesoura.setItemMeta(tesourameta);
		ItemStack amarela = new ItemStack(Material.WOOL, 1, (byte) 4);
		ItemMeta amarelameta = amarela.getItemMeta();
		amarelameta.setDisplayName("§eAguardando o oponente escolher...");
		amarela.setItemMeta(amarelameta);
		ItemStack verde = new ItemStack(Material.WOOL, 1, (byte) 5);
		ItemMeta verdemeta = verde.getItemMeta();
		verdemeta.setDisplayName("§aPronto.");
		verde.setItemMeta(verdemeta);
		inv.setItem(11, pedra);
		inv.setItem(13, papel);
		inv.setItem(15, tesoura);
		inv.setItem(29, verde);
		inv.setItem(31, pedra);
		inv.setItem(33, amarela);
		p.openInventory(inv);
	}
	
	public boolean isNum(String num){
		try{
			Double.parseDouble(num);
			return true;
		}catch(Exception e){}
		return false;
	}

}

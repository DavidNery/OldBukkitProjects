package me.dery.hmercado;

import java.io.File;
import java.text.NumberFormat;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Comandos implements CommandExecutor{
	
	private HMercado instance = HMercado.getHMercado();
	
	@SuppressWarnings("static-access")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(!(sender instanceof Player)) return true;
		Player p = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("mercado")){
			if(args.length == 0){
				if(instance.getAtivado() == false){
					p.sendMessage(instance.getConfig().getString("Mercado_Atualizando").replace("&", "§"));
					return true;
				}
				if((!new File(instance.getDataFolder(), "items.yml").exists())){
					p.sendMessage(instance.getConfig().getString("Sem_Itens").replace("&", "§"));
					return true;
				}
				p.sendMessage(instance.getConfig().getString("Abrindo").replace("&", "§"));
				p.openInventory(instance.getMercadoManager().openInventory(1));
				p.playSound(p.getLocation(), Sound.CHEST_OPEN, 5.0F, 1.0F);
				return true;
			}else if(args[0].equalsIgnoreCase("vender")){
				if(instance.getAtivado() == false){
					p.sendMessage(instance.getConfig().getString("Mercado_Atualizando").replace("&", "§"));
					return true;
				}
				if(!instance.econ.has(p.getName(), instance.getConfig().getDouble("Minimo_Vender"))){
					p.sendMessage(instance.getConfig().getString("Sem_Dinheiro_Vender").replace("&", "§").replace("@necessario", NumberFormat.getCurrencyInstance().format(instance.getConfig().getDouble("Minimo_Vender") - instance.econ.getBalance(p.getName())).replace("$", "")));
					return true;
				}
				if(args.length <= 1){
					p.sendMessage(instance.getConfig().getString("Uso_Correto").replace("&", "§"));
					return true;
				}
				if(p.getItemInHand().getType() == Material.AIR){
					p.sendMessage(instance.getConfig().getString("Sem_Item").replace("&", "§"));
					return true;
				}
				if(!isNum(args[1])){
					p.sendMessage(instance.getConfig().getString("Nao_Eh_Numero").replace("&", "§").replace("@erro", args[1]));
					return true;
				}
				if(instance.getConfig().getBoolean("Ativar_WhiteList")){
					if(!instance.getConfig().getStringList("WhiteList").contains(p.getItemInHand().getTypeId() + "")){
						p.sendMessage(instance.getConfig().getString("Nao_Pode").replace("&", "§").replace("@item", p.getItemInHand().getType().name()));
						return true;
					}
				}
				if(!p.hasPermission("hmercado.limit.bypass")){
					if(instance.getMercadoManager().getQuantidade().containsKey(p.getName()) && instance.getMercadoManager().getQuantidade().get(p.getName()) >= instance.getConfig().getInt("Maximo_Vendas")){
						p.sendMessage(instance.getConfig().getString("Nao_Pode_Vender_Mais").replace("&", "§"));
						return true;
			        }
				}
				int preco = Integer.parseInt(args[1]);
				if(preco <= 0){
					p.sendMessage(instance.getConfig().getString("Menor_Que_0").replace("&", "§"));
					return true;
				}
				p.sendMessage(instance.getConfig().getString("Vendeu").replace("&", "§").replace("@preco", NumberFormat.getCurrencyInstance().format(preco).replace("$", "")).replace("@quantidade", p.getItemInHand().getAmount() + "").replace("@item", p.getItemInHand().getType().name()));
				instance.getMercadoManager().venderItem(p, preco, instance.getMercadoManager().ItemStackToString(p.getItemInHand(), preco));
				if(!instance.getMercadoManager().getQuantidade().containsKey(p.getName())){
					instance.getMercadoManager().getQuantidade().put(p.getName(), 1);
		        }else{
		        	instance.getMercadoManager().getQuantidade().put(p.getName(), instance.getMercadoManager().getQuantidade().get(p.getName()) + 1);
		        }
				return true;
			}
		}
		return false;
	}
	
	public boolean isNum(String num){
		try{
			Integer.parseInt(num);
			return true;
		}catch(NumberFormatException e){}
		return false;
	}

}

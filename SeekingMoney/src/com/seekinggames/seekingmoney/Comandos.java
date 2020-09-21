package com.seekinggames.seekingmoney;

import java.text.NumberFormat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Comandos implements CommandExecutor{
	
	@SuppressWarnings("static-access")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(!(sender instanceof Player)) return true;
		if(cmd.getName().equalsIgnoreCase("money")){
			Player p = (Player) sender;
			if(!SeekingMoney.getSeekingMoney().getAPI().hasConta(p.getName())){
				SeekingMoney.getSeekingMoney().getAPI().criarConta(p.getName());
			}
			if(args.length == 0){
				p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Money").replace("&", "§").replace("@money", NumberFormat.getCurrencyInstance().format(SeekingMoney.getSeekingMoney().getAPI().getMoneyConta(p.getName())).replace("$", "")));
				return true;
			}else if(args[0].equalsIgnoreCase("pay") || args[0].equalsIgnoreCase("send")){
				if(args.length <= 2){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Uso_Correto_Pay").replace("&", "§"));
					return true;
				}
				if(args[1].equalsIgnoreCase(p.getName())){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Si_Mesmo").replace("&", "§"));
					return true;
				}
				if(!isNum(args[2])){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Somente_Numeros").replace("&", "§"));
					return true;
				}
				if(!SeekingMoney.getSeekingMoney().getAPI().hasConta(args[1])){
					SeekingMoney.getSeekingMoney().getAPI().criarConta(args[1]);
				}
				double valor = Double.valueOf(args[2]);
				if(valor <= 0){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Maior_Que_Zero").replace("&", "§"));
					return true;
				}
				if(!SeekingMoney.getSeekingMoney().getAPI().hasMoney(p.getName(), valor)){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Sem_Money").replace("&", "§"));
					return true;
				}
				SeekingMoney.getSeekingMoney().getAPI().trocarMoney(p.getName(), args[1], valor);
				p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Enviou_Money").replace("&", "§").replace("@player", args[1]).replace("@quantidade", NumberFormat.getCurrencyInstance().format(valor).replace("$", "")));
				if(SeekingMoney.getSeekingMoney().getServer().getPlayer(args[1]) != null){
					SeekingMoney.getSeekingMoney().getServer().getPlayer(args[1]).sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Recebeu_Money").replace("&", "§").replace("@player", p.getName()).replace("@quantidade", NumberFormat.getCurrencyInstance().format(valor).replace("$", "")));
				}
			}else if(args[0].equalsIgnoreCase("top")){
				int i = 1;
				p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Money_TOP_Titulo").replace("&", "§"));
				for(String tops : SeekingMoney.getSeekingMoney().getTopsArray()){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Money_TOP_Formato").replace("&", "§").replace("@player", tops).replace("@colocacao", i + "").replace("@money", NumberFormat.getCurrencyInstance().format(SeekingMoney.getSeekingMoney().getTops().get(tops)).replace("$", "")));
					i++;
				}
			}else if(args[0].equalsIgnoreCase("set")){
				if(!p.hasPermission("seekingmoney.admin")){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Sem_Permissao").replace("&", "§"));
					return true;
				}
				if(args.length <= 2){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Uso_Correto_Set").replace("&", "§"));
					return true;
				}
				if(!isNum(args[2])){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Somente_Numeros").replace("&", "§"));
					return true;
				}
				if(!SeekingMoney.getSeekingMoney().getAPI().hasConta(args[1])){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Conta_Nao_Existe").replace("&", "§"));
					return true;
				}
				double valor = Double.valueOf(args[2]);
				if(valor <= 0){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Maior_Que_Zero").replace("&", "§"));
					return true;
				}
				SeekingMoney.getSeekingMoney().getAPI().setMoney(args[1], valor);
				p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Setou_Money").replace("&", "§").replace("@player", args[1]).replace("@money", NumberFormat.getCurrencyInstance().format(valor).replace("$", "")));
			}else if(args[0].equalsIgnoreCase("give")){
				if(!p.hasPermission("seekingmoney.admin")){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Sem_Permissao").replace("&", "§"));
					return true;
				}
				if(args.length <= 2){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Uso_Correto_Give").replace("&", "§"));
					return true;
				}
				if(!isNum(args[2])){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Somente_Numeros").replace("&", "§"));
					return true;
				}
				if(!SeekingMoney.getSeekingMoney().getAPI().hasConta(args[1])){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Conta_Nao_Existe").replace("&", "§"));
					return true;
				}
				double valor = Double.valueOf(args[2]);
				if(valor <= 0){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Maior_Que_Zero").replace("&", "§"));
					return true;
				}
				SeekingMoney.getSeekingMoney().getAPI().darMoney(args[1], valor);
				p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Deu_Money").replace("&", "§").replace("@player", args[1]).replace("@money", NumberFormat.getCurrencyInstance().format(valor).replace("$", "")));
			}else if(args[0].equalsIgnoreCase("take")){
				if(!p.hasPermission("seekingmoney.admin")){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Sem_Permissao").replace("&", "§"));
					return true;
				}
				if(args.length <= 2){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Uso_Correto_Take").replace("&", "§"));
					return true;
				}
				if(!isNum(args[2])){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Somente_Numeros").replace("&", "§"));
					return true;
				}
				if(!SeekingMoney.getSeekingMoney().getAPI().hasConta(args[1])){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Conta_Nao_Existe").replace("&", "§"));
					return true;
				}
				double valor = Double.valueOf(args[2]);
				if(valor <= 0){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Maior_Que_Zero").replace("&", "§"));
					return true;
				}
				SeekingMoney.getSeekingMoney().getAPI().tirarMoney(args[1], valor);
				p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Tirou_Money").replace("&", "§").replace("@player", args[1]).replace("@money", NumberFormat.getCurrencyInstance().format(valor).replace("$", "")));
			}else{
				if(!SeekingMoney.getSeekingMoney().getAPI().hasConta(args[0])){
					p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Conta_Nao_Existe").replace("&", "§"));
					return true;
				}
				p.sendMessage(SeekingMoney.getSeekingMoney().getConfig().getString("Money_Outros").replace("&", "§").replace("@player", (SeekingMoney.getSeekingMoney().getServer().getPlayer(args[0]) != null ? SeekingMoney.getSeekingMoney().getServer().getPlayer(args[0]).getName() : args[0])).replace("@money", NumberFormat.getCurrencyInstance().format(SeekingMoney.getSeekingMoney().getAPI().getMoneyConta(args[0])).replace("$", "")));
			}
		}
		return false;
	}
	
	public boolean isNum(String num){
		try{
			Double.parseDouble(num);
			return true;
		}catch(Exception e){}
		return false;
	}

}

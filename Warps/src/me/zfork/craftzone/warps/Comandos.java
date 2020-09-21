package me.zfork.craftzone.warps;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Comandos implements CommandExecutor{

	private static final Main instance = Main.getWarps();
	private static final FileConfiguration warpsfile = instance.getWarpsFile();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("warp")){
			if(!(sender instanceof Player)){
				sender.sendMessage("§9Comando apenas para players!");
				return true;
			}
			if(args.length == 0){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto").replace("&", "§").replace("{cmd}", "/" + label.split(" ")[0]));
			}else{
				if(!warpsfile.contains("Warps") || warpsfile.getConfigurationSection("Warps").getKeys(false).size() == 0){
					sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Warps").replace("&", "§"));
					return true;
				}
				final String warpname = args[0];
				final Player p = (Player) sender;
				for(String warps : warpsfile.getConfigurationSection("Warps").getKeys(false)){
					if(warps.equalsIgnoreCase(warpname)){
						instance.tpPlayer(p, warps);
						return true;
					}
				}
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Warp_Invalida").replace("&", "§").replace("{warp}", warpname));
			}
		}else if(cmd.getName().equalsIgnoreCase("setwarp")){
			if(!(sender instanceof Player)){
				sender.sendMessage("§9Comando apenas para players!");
				return true;
			}
			final Player p =(Player)sender;
			if(!p.hasPermission(instance.getConfig().getString("Config.Permissao.SetWarp"))){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§").replace("{cmd}", "/" + label.split(" ")[0]));
				return true;
			}else if(args.length == 0){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto").replace("&", "§").replace("{cmd}", "/" + label.split(" ")[0]));
				return true;
			}
			final String warpname = args[0];
			if(warpsfile.contains("Warps")){
				for(String warps : warpsfile.getConfigurationSection("Warps").getKeys(false)){
					if(warps.equalsIgnoreCase(warpname)){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Warp_Ja_Setado").replace("&", "§").replace("{warp}", warps));
						return true;
					}
				}
			}
			warpsfile.set("Warps." + warpname + ".World", p.getWorld().getName());
			warpsfile.set("Warps." + warpname + ".X", p.getLocation().getX());
			warpsfile.set("Warps." + warpname + ".Y", p.getLocation().getY());
			warpsfile.set("Warps." + warpname + ".Z", p.getLocation().getZ());
			warpsfile.set("Warps." + warpname + ".Yaw", p.getLocation().getYaw());
			warpsfile.set("Warps." + warpname + ".Pitch", p.getLocation().getPitch());
			warpsfile.set("Warps." + warpname + ".Ativar_Perm", true);
			warpsfile.set("Warps." + warpname + ".Ativar_Delay", true);
			warpsfile.set("Warps." + warpname + ".Simplificar", true);
			warpsfile.set("Warps." + warpname + ".ItemInv", "item:2 nome:&6{warp} lore:&cClique_para_se_teletransportar_ate_a_warp_&7{warp}&c!");
			warpsfile.set("Warps." + warpname + ".Local", -1);
			if(args.length >= 2){
				for(int i = 1; i < args.length; i++){
					final String property = args[i];
					if(property.contains(":")) setWarpProperty(warpname, property);
				}
			}
			try{
				instance.saveWarpsfile();
			}catch(IOException | InvalidConfigurationException e){
				e.printStackTrace();
			}
			p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Warp_Setado").replace("&", "§").replace("{warp}", warpname));
		}else if(cmd.getName().equalsIgnoreCase("delwarp")){
			if(!sender.hasPermission(instance.getConfig().getString("Config.Permissao.DelWarp"))){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§").replace("{cmd}", "/" + label.split(" ")[0]));
				return true;
			}else if(args.length == 0){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto").replace("&", "§").replace("{cmd}", "/" + label.split(" ")[0]));
				return true;
			}else if(!warpsfile.contains("Warps")){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Warps").replace("&", "§"));
				return true;
			}
			final String warpname = args[0];
			for(final String warps : warpsfile.getConfigurationSection("Warps").getKeys(false)){
				if(warps.equalsIgnoreCase(warpname)){
					warpsfile.set("Warps." + warps, null);
					try{
						instance.saveWarpsfile();
					}catch(IOException | InvalidConfigurationException e){
						e.printStackTrace();
					}
					sender.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Warp_Deletado").replace("&", "§").replace("{warp}", warpname));
					return true;
				}
			}
			sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Warp_Nao_Setado").replace("&", "§").replace("{warp}", warpname));
		}else if(cmd.getName().equalsIgnoreCase("warps")){
			if(!warpsfile.contains("Warps")){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Warps").replace("&", "§"));
				return true;
			}
			int warpsqnt = 0;
			for(String warp : warpsfile.getConfigurationSection("Warps").getKeys(false))
				if(sender.hasPermission(instance.getConfig().getString("Config.Permissao_Warps").replace("{warp}", warp))) warpsqnt += 1;
			if(warpsqnt == 0){
				sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Warps_Disponiveis").replace("&", "§"));
				return true;
			}
			if(instance.getConfig().getBoolean("GUI.Ativar") && sender instanceof Player){
				Inventory inv = instance.getServer().createInventory(null, 
						instance.getConfig().getInt("GUI.Tamanho"), 
						instance.getConfig().getString("GUI.Nome").replace("&", "§"));
				ItemStack item = (ItemStack)instance.criarItem(instance.getConfig().getString("GUI.Item_Ocupar"));
				if(instance.getConfig().getBoolean("GUI.Auto_Completar"))
					for(int i = 0; i < instance.getConfig().getInt("GUI.Tamanho"); i++) inv.setItem(i, item);
				for(String warp : warpsfile.getConfigurationSection("Warps").getKeys(false)){
					if(!warpsfile.getBoolean("Warps." + warp + ".Ativar_Perm")){
						ItemStack warpitem =(ItemStack)instance.criarItem(warpsfile.getString("Warps." + warp + ".ItemInv").replace("{warp}", warp));
						int local = warpsfile.getInt("Warps." + warp + ".Local");
						if(getInvFirstEmpty(inv, item) == -1)
							break;
						inv.setItem((local == -1) ? getInvFirstEmpty(inv, item) : local, warpitem);
					}else{
						if(sender.hasPermission(instance.getConfig().getString("Config.Permissao_Warps").replace("{warp}", warp))){
							ItemStack warpitem =(ItemStack)instance.criarItem(warpsfile.getString("Warps." + warp + ".ItemInv").replace("{warp}", warp));
							int local = warpsfile.getInt("Warps." + warp + ".Local");
							if(getInvFirstEmpty(inv, item) == -1)
								break;
							inv.setItem((local == -1) ? getInvFirstEmpty(inv, item) : local, warpitem);
						}
					}
				}
				((Player) sender).openInventory(inv);
			}else{
				final String warps = instance.getConfig().getString("Mensagem.Sucesso.Warps").replace("&", "§").replace("{warpsqnt}", String.valueOf(warpsqnt));
				if(Double.parseDouble(instance.version.substring(1, 4).replace("_", ".")) >= 1.7 && instance.getConfig().getBoolean("Config.Ativar_JSON") && warps.contains("{warps}") && sender instanceof Player){
					JSONMessage json = new JSONMessage().addText(warps.split("([&|§][a-fA-F0-9k-oK-orR])*?\\{warps\\}")[0]);
					final String lastwarp = new ArrayList<>(warpsfile.getConfigurationSection("Warps").getKeys(false)).get(warpsfile.getConfigurationSection("Warps").getKeys(false).size() - 1);
					String hoverwarp = "";
					for(String s : instance.getConfig().getStringList("Config.Hover_Warp"))
						hoverwarp += (s + "\n");
					hoverwarp = hoverwarp.replace("&", "§").substring(0, hoverwarp.toString().length() - 2);
					for(String warp : warpsfile.getConfigurationSection("Warps").getKeys(false)){
						if(!warpsfile.getBoolean("Warps." + warp + ".Ativar_Perm")){
							json.addText(instance.getConfig().getString("Config.Modo_Warp").replace("{warp}", warp).replace("&", "§"))
								.withHoverAction(JSONMessage.HoverAction.SHOW_TEXT, hoverwarp.replace("{warp}", warp))
								.withClickAction(JSONMessage.ClickAction.RUN_COMMAND, "/warp " + warp);
							if(warp.equals(lastwarp))
								json.addText(instance.getConfig().getString("Config.Separador_Warps").replace("&", "§"));
						}else{
							if(sender.hasPermission(instance.getConfig().getString("Config.Permissao_Warps").replace("{warp}", warp))){
								json.addText(instance.getConfig().getString("Config.Modo_Warp").replace("{warp}", warp).replace("&", "§"))
									.withHoverAction(JSONMessage.HoverAction.SHOW_TEXT, hoverwarp.replace("{warp}", warp))
									.withClickAction(JSONMessage.ClickAction.RUN_COMMAND, "/warp " + warp);
								if(!warp.equals(lastwarp)) json.addText(instance.getConfig().getString("Config.Separador_Warps").replace("&", "§"));
							}
						}
					}
					String[] split = warps.split("([&|§][a-fA-F0-9k-oK-orR])*?\\{warps\\}");
					String strverify = null;
					if(split.length >= 2){
						strverify = "[\"\",{\"text\":\"" + warps.split("\\{warps\\}")[0] + "\"},{\"text\":\"" + warps.split("\\{warps\\}")[1] +"\"}]";
						json.addText(split[1]);
					}else{
						strverify = "[\"\",{\"text\":\"" + warps.split("\\{warps\\}")[0] + "\"}]";
					}
					if(json.toString().equalsIgnoreCase(strverify)){
						sender.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Warps_Disponiveis").replace("&", "§"));
					}else{
						json.sendJson((Player)sender);
					}
				}else{
					StringBuilder sb = new StringBuilder();
					for(String warp : warpsfile.getConfigurationSection("Warps").getKeys(false))
						if(sender.hasPermission(instance.getConfig().getString("Config.Permissao_Warps").replace("{warp}", warp)))
							sb.append(instance.getConfig().getString("Config.Modo_Warp").replace("{warp}", warp).replace("&", "§"))
								.append(instance.getConfig().getString("Config.Separador_Warps").replace("&", "§"));
					sender.sendMessage(warps.replace("{warps}", sb.toString().substring(0, sb.toString().length()-instance.getConfig().getString("Config.Separador_Warps").length())));
				}
			}
		}
		return false;
	}

	public static void setWarpProperty(String warpName, String property){
		String[] propertypartes = property.split("(ativar_perm:|ativar_delay:|iteminv:|local:|simplificar:)");
		try{
			if(property.toLowerCase().startsWith("ativar_perm")) warpsfile.set("Warps." + warpName + ".Ativar_Perm", Boolean.parseBoolean(propertypartes[1]));
			else if(property.toLowerCase().startsWith("ativar_delay")) warpsfile.set("Warps." + warpName + ".Ativar_Delay", Boolean.parseBoolean(propertypartes[1]));
			else if(property.toLowerCase().startsWith("iteminv")) warpsfile.set("Warps." + warpName + ".ItemInv", propertypartes[1].replace("<>", " "));
			else if(property.toLowerCase().startsWith("local")) warpsfile.set("Warps." + warpName + ".Local", Integer.parseInt(propertypartes[1]));
			else if(property.toLowerCase().startsWith("simplificar")) warpsfile.set("Warps." + warpName + ".Simplificar", Boolean.parseBoolean(propertypartes[1]));
		}catch(Exception ex){}
	}
	
	public static int getInvFirstEmpty(Inventory inv, ItemStack item){
		if(!instance.getConfig().getBoolean("GUI.Auto_Completar"))
			return inv.firstEmpty();
		int vazio = 0;
		for(ItemStack i : inv.getContents()){
			if(i.isSimilar(item)) return vazio;
			vazio++;
		}
		return -1;
	}
}

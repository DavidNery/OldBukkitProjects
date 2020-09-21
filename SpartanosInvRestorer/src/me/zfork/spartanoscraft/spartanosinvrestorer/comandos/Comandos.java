package me.zfork.spartanoscraft.spartanosinvrestorer.comandos;

import java.util.ArrayList;

import me.zfork.spartanoscraft.spartanosinvrestorer.SpartanosInvRestorer;
import me.zfork.spartanoscraft.spartanosinvrestorer.utils.PlayerInventory;
import me.zfork.spartanoscraft.spartanosinvrestorer.utils.PlayerInventoryUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Comandos implements CommandExecutor{
	
	private SpartanosInvRestorer instance;
	private PlayerInventoryUtils playerInventoryUtils;
	
	public Comandos(SpartanosInvRestorer instance) {
		this.instance = instance;
		this.playerInventoryUtils = instance.getPlayerInventoryUtils();
		instance.getCommand("invsrestorer").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("invsrestorer") && sender instanceof Player){
			Player p = (Player) sender;
			if(args.length == 0){
				Inventory inv = playerInventoryUtils.cloneInventory(new ArrayList<>(playerInventoryUtils.getInventarios()).get(0));
				if(inv.getItem(0) == null){
					try{
						inv = playerInventoryUtils.getPlayersInventories().get(p.getName()).get(0);
						if(inv.getItem(0) == null){
							p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Inventarios_Total").replace("&", "§"));
							return true;
						}
					}catch(Exception e){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Inventarios_Total").replace("&", "§"));
						return true;
					}
				}else{
					ItemStack item = playerInventoryUtils.getHead(p.getName());
					ItemMeta im = item.getItemMeta();
					im.setDisplayName("§7Seus inventários");
					item.setItemMeta(im);
					inv.setItem(49, item);
				}
				p.openInventory(inv);
			}else if(args[0].equalsIgnoreCase("ver")){
				if(args.length == 1){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Cmd.Ver").replace("&", "§"));
					return true;
				}else if(!p.hasPermission(instance.getConfig().getString("Config.Permissao.Ver"))){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}
				if(isNum(args[1])){
					try{
						p.openInventory(new ArrayList<>(playerInventoryUtils.getSavedInventories()).get(Integer.parseInt(args[1])-1).getPreviewInventory());
					}catch(Exception e){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inventario_Nao_Encontrado").replace("&", "§"));
					}
				}else{
					try{
						p.openInventory(new ArrayList<>(playerInventoryUtils.getPlayersInventories().get(args[1])).get(0));
					}catch(Exception e){
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Nao_Encontrado").replace("&", "§"));
					}
				}
			}else if(args[0].equalsIgnoreCase("deletar")){
				if(args.length == 1){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Cmd.Deletar").replace("&", "§"));
					return true;
				}else if(!p.hasPermission(instance.getConfig().getString("Config.Permissao.Deletar"))){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
					return true;
				}
				if(isNum(args[1])){
					int id = Integer.parseInt(args[1])-1;
					for(PlayerInventory playerInventory : playerInventoryUtils.getSavedInventories()){
						if(playerInventory.getId() == id){
							playerInventoryUtils.removePlayerInventory(playerInventory);
							p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Inventario_Apagado").replace("&", "§").replace("{id}", id+""));
							return true;
						}
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inventario_Nao_Encontrado").replace("&", "§"));
				}else{
					for(String playerInventory : playerInventoryUtils.getPlayersInventories().keySet()){
						if(playerInventory.equalsIgnoreCase(args[1])){
							playerInventoryUtils.removePlayerForInventories(playerInventory);
							p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Inventarios_Apagados").replace("&", "§").replace("{player}", playerInventory));
							return true;
						}
					}
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Nao_Encontrado").replace("&", "§"));
				}
			}/*else if(args[0].equalsIgnoreCase("cib")){
				Chest chest = (Chest) p.getTargetBlock((HashSet<Material>) null, 5).getState();
				for(ItemStack item : chest.getInventory().getContents()){
					if(item == null) break;
					if(p.getInventory().firstEmpty() != -1){
						if(item.getType().name().endsWith("HELMET") && p.getInventory().getHelmet() == null) p.getInventory().setHelmet(item);
						else if(item.getType().name().endsWith("CHESTPLATE") && p.getInventory().getChestplate() == null) p.getInventory().setChestplate(item);
						else if(item.getType().name().endsWith("LEGGINGS") && p.getInventory().getLeggings() == null) p.getInventory().setLeggings(item);
						else if(item.getType().name().endsWith("BOOTS") && p.getInventory().getBoots() == null) p.getInventory().setBoots(item);
						else p.getInventory().addItem(item);
					}else{
						break;
					}
				}
			}*/
		}
		return false;
	}
	
	public boolean isNum(String num) {
		try{
			Integer.parseInt(num);
			return true;
		}catch(Exception e){return false;}
	}

}

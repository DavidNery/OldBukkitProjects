package me.dery.hmercado;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class Listeners implements Listener{
	
	public static HMercado instance = HMercado.getHMercado();
	
	@EventHandler
	public void Pegar(InventoryPickupItemEvent e){
		if(e.getInventory().getTitle().startsWith("§6§lMercado")){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void RS(ServerCommandEvent e){
		String cmd = e.getCommand();
		if(cmd.startsWith("/stop") || cmd.equalsIgnoreCase("/stop") || cmd.startsWith("/end") || cmd.equalsIgnoreCase("/end")){
			for(Player on : instance.getServer().getOnlinePlayers()){
				if(on.getOpenInventory() != null && on.getOpenInventory().getTitle().startsWith("§6§lMercado")){
					on.getOpenInventory().close();
				}
			}
			instance.getServer().shutdown();
		}else if(cmd.startsWith("/reload") || cmd.startsWith("/rl") || cmd.equalsIgnoreCase("/reload") || cmd.equalsIgnoreCase("/rl")){
			for(Player on : instance.getServer().getOnlinePlayers()){
				if(on.getOpenInventory() != null && on.getOpenInventory().getTitle().startsWith("§6§lMercado")){
					on.getOpenInventory().close();
				}
			}
			instance.getServer().reload();
		}else if(cmd.startsWith("/plugman reload hmercado")){
			for(Player on : instance.getServer().getOnlinePlayers()){
				if(on.getOpenInventory() != null && on.getOpenInventory().getTitle().startsWith("§6§lMercado")){
					on.getOpenInventory().close();
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void RS(PlayerCommandPreprocessEvent e){
		String cmd = e.getMessage().toLowerCase();
		if(cmd.startsWith("/enderchest") || cmd.startsWith("/echest")){
			e.setCancelled(true);
			if(!e.getPlayer().hasPermission("essentials.enderchest")) return;
			e.getPlayer().openInventory(e.getPlayer().getEnderChest());
			return;
		}
		if(!e.getPlayer().isOp()) return;
		if(cmd.startsWith("/stop") || cmd.equalsIgnoreCase("/stop") || cmd.startsWith("/end") || cmd.equalsIgnoreCase("/end")){
			e.setCancelled(true);
			for(Player on : instance.getServer().getOnlinePlayers()){
				if(on.getOpenInventory() != null && on.getOpenInventory().getTitle().startsWith("§6§lMercado")){
					on.getOpenInventory().close();
				}
			}
			instance.getServer().shutdown();
		}else if(cmd.startsWith("/reload") || cmd.startsWith("/rl") || cmd.equalsIgnoreCase("/reload") || cmd.equalsIgnoreCase("/rl")){
			e.setCancelled(true);
			for(Player on : instance.getServer().getOnlinePlayers()){
				if(on.getOpenInventory() != null && on.getOpenInventory().getTitle().startsWith("§6§lMercado")){
					on.getOpenInventory().close();
				}
			}
			instance.getServer().reload();
		}else if(cmd.startsWith("/plugman reload hmercado")){
			for(Player on : instance.getServer().getOnlinePlayers()){
				if(on.getOpenInventory() != null && on.getOpenInventory().getTitle().startsWith("§6§lMercado")){
					on.getOpenInventory().close();
				}
			}
		}
	}
	
	@SuppressWarnings({ "deprecation", "static-access" })
	@EventHandler
	public void Pegar(InventoryMoveItemEvent e){
		if(e.getInitiator().getTitle().startsWith("§6§lMercado") || e.getDestination().getTitle().startsWith("§6§lMercado")){
			e.setCancelled(true);
			Player p = (Player) e.getSource().getHolder();
			p.updateInventory();
			p.getOpenInventory().close();
			int pg = 1;
			if(e.getInitiator().getTitle().startsWith("§6§lMercado")){
				pg = Integer.parseInt(e.getInitiator().getTitle().split("-")[1].replace(" ", "").replace("Pagina", "").replace("§7§l", ""));
			}else if(e.getDestination().getTitle().startsWith("§6§lMercado")){
				pg = Integer.parseInt(e.getDestination().getTitle().split("-")[1].replace(" ", "").replace("Pagina", "").replace("§7§l", ""));
			}
			p.openInventory(instance.getMercadoManager().getPaginas().get(pg));
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void Pegar(InventoryClickEvent e){
		ItemStack item = e.getCurrentItem();
		if(item == null || item.getType() == Material.AIR) return;
		if(e.getInventory().getTitle().startsWith("§6§lMercado")){
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			if(item.getType() == Material.STONE_BUTTON && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§a» Proxima pagina »")){
				int pg = Integer.parseInt(e.getInventory().getTitle().split("-")[1].replace(" ", "").replace("Pagina", "").replace("§7§l", ""));
				if(pg + 1 > instance.getMercadoManager().getPaginas().size()){
					p.sendMessage(instance.getConfig().getString("Sem_Paginas").replace("&", "§"));
					return;
				}
				p.openInventory(instance.getMercadoManager().getPaginas().get(pg + 1));
				p.playSound(p.getLocation(), Sound.CLICK, 5.0F, 1.0F);
				return;
			}else if(item.getType() == Material.STONE_BUTTON && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§c« Pagina anterior «")){
				int pg = Integer.parseInt(e.getInventory().getTitle().split("-")[1].replace(" ", "").replace("Pagina", "").replace("§7§l", ""));
				if(pg - 1 < 1){
					p.sendMessage(instance.getConfig().getString("Sem_Paginas").replace("&", "§"));
					p.playSound(p.getLocation(), Sound.NOTE_STICKS, 50.0F, 1.0F);
					return;
				}
				p.openInventory(instance.getMercadoManager().getPaginas().get(pg - 1));
				p.playSound(p.getLocation(), Sound.CLICK, 5.0F, 1.0F);
				return;
			}
			MaterialData data = item.getData();
			if(item.getType() == Material.WOOL && data.getData() == (byte) 13 && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§aRetirar Venda")){
				if(p.getOpenInventory() != null && (!p.getOpenInventory().getTitle().startsWith("§6§lMercado"))) return;
				String vendedor = "";
				ItemMeta im = p.getOpenInventory().getItem(4).getItemMeta();
				vendedor = im.getLore().get(im.getLore().size() - 2).replace("§3§lVendedor: §f§l", "");
				if(!vendedor.equals(p.getName())){
					p.sendMessage(instance.getConfig().getString("Nao_E_Seu").replace("&", "§"));
					p.playSound(p.getLocation(), Sound.NOTE_STICKS, 50.0F, 1.0F);
					return;
				}
				if(p.getInventory().firstEmpty() == -1){
					p.sendMessage(instance.getConfig().getString("Inv_Cheio").replace("&", "§"));
					p.playSound(p.getLocation(), Sound.NOTE_STICKS, 50.0F, 1.0F);
					return;
				}
				List<String> lore = im.getLore();
				lore.remove(lore.size() - 1);
				lore.remove(lore.size() - 1);
				im.setLore(lore);
				im.setDisplayName(p.getOpenInventory().getItem(4).getItemMeta().getDisplayName());
				p.getOpenInventory().getItem(4).setItemMeta(im);
				List<String> itemsdenovo = instance.getMercadoManager().getItens().getStringList("Items." + vendedor);
				String itemstring = instance.getMercadoManager().ItemStackToString(p.getOpenInventory().getItem(4));
				for(int i = 0; i<instance.getMercadoManager().getItens().getStringList("Items." + vendedor).size(); i++){
					if(instance.getMercadoManager().getItens().getStringList("Items." + vendedor).get(i).startsWith(itemstring)){
						itemsdenovo.remove(i);
						instance.getMercadoManager().getItens().set("Items." + vendedor, itemsdenovo);
						try{
							instance.getMercadoManager().getItens().save(new File(instance.getDataFolder(), "items.yml"));
							instance.getMercadoManager().getItens().load(new File(instance.getDataFolder(), "items.yml"));
						}catch(Exception e1){}
						p.getInventory().addItem(p.getOpenInventory().getItem(4));
						p.sendMessage(instance.getConfig().getString("Removeu_Item").replace("&", "§"));
						p.playSound(p.getLocation(), Sound.NOTE_PIANO, 50.0F, 1.0F);
						p.getOpenInventory().close();
				        instance.getMercadoManager().getQuantidade().put(p.getName(), instance.getMercadoManager().getQuantidade().get(p.getName()) - 1);
				        if(instance.getMercadoManager().getQuantidade().get(p.getName()) == 0){
							instance.getMercadoManager().getQuantidade().remove(p.getName());
				        }
				        if(instance.getMercadoManager().getItens().getStringList("Items." + vendedor).size() <= 0){
				        	instance.getMercadoManager().getItens().set("Items." + vendedor, null);
				        }
				        try{
							instance.getMercadoManager().getItens().save(new File(instance.getDataFolder(), "items.yml"));
							instance.getMercadoManager().getItens().load(new File(instance.getDataFolder(), "items.yml"));
						}catch(Exception e1){}
				        if(instance.getMercadoManager().getItens().getConfigurationSection("Items").getKeys(false).size() <= 0){
				        	new File(instance.getDataFolder(), "items.yml").delete();
				        }
						return;
					}
				}
				p.sendMessage(instance.getConfig().getString("Nao_Esta_A_Venda").replace("&", "§"));
				p.getOpenInventory().close();
				return;
			}else if(item.getType() == Material.WOOL && data.getData() == (byte) 13 && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§aComprar Item")){
				if(p.getOpenInventory() != null && (!p.getOpenInventory().getTitle().startsWith("§6§lMercado"))) return;
				String vendedor = "";
				ItemMeta im = p.getOpenInventory().getItem(4).getItemMeta();
				vendedor = im.getLore().get(im.getLore().size() - 2).replace("§3§lVendedor: §f§l", "");
				if(vendedor.equalsIgnoreCase(p.getName())){
					p.sendMessage(instance.getConfig().getString("Nao_Pode_Comprar_Si_Mesmo").replace("&", "§"));
					p.playSound(p.getLocation(), Sound.NOTE_STICKS, 50.0F, 1.0F);
					return;
				}
				ItemMeta copia = p.getOpenInventory().getItem(4).getItemMeta();
				List<String> lore = im.getLore();
				lore.remove(lore.size() - 1);
				lore.remove(lore.size() - 1);
				im.setLore(lore);
				im.setDisplayName(p.getOpenInventory().getItem(4).getItemMeta().getDisplayName());
				p.getOpenInventory().getItem(4).setItemMeta(im);
				List<String> itemsdenovo = instance.getMercadoManager().getItens().getStringList("Items." + vendedor);
				String itemstring = instance.getMercadoManager().ItemStackToString(p.getOpenInventory().getItem(4));
				p.getOpenInventory().getItem(4).setItemMeta(copia);
				double preco = 0;
				for(int i = 0; i<instance.getMercadoManager().getItens().getStringList("Items." + vendedor).size(); i++){
					/*if(instance.getMercadoManager().getItens().getStringList("Items." + vendedor).get(i).startsWith(itemstring)){
						if(NumberFormat.getCurrencyInstance().format(Double.parseDouble(instance.getMercadoManager().getItens().getStringList("Items." + vendedor).get(i).split(";")[5])).replace("$", "").equals(copia.getLore().get(copia.getLore().size() - 1).substring(0, copia.getLore().get(copia.getLore().size() - 1).length()).replace("§2§lPreco: §f§l", ""))){
							click.put(p.getName(), i);
							break;
						}
					}*/
					if(instance.getMercadoManager().getItens().getStringList("Items." + vendedor).get(i).startsWith(itemstring)){
						if(p.getInventory().firstEmpty() == -1){
							p.sendMessage(instance.getConfig().getString("Inv_Cheio").replace("&", "§"));
							p.playSound(p.getLocation(), Sound.NOTE_STICKS, 50.0F, 1.0F);
							return;
						}
						preco = Double.parseDouble(instance.getMercadoManager().getItens().getStringList("Items." + vendedor).get(i).split(";")[5]);
						if(!instance.econ.has(p.getName(), preco)){
							p.sendMessage(instance.getConfig().getString("Sem_Money").replace("&", "§"));
							p.playSound(p.getLocation(), Sound.NOTE_STICKS, 50.0F, 1.0F);
							return;
						}
						p.getOpenInventory().getItem(4).setItemMeta(im);
						p.getInventory().addItem(p.getOpenInventory().getItem(4));
						p.getOpenInventory().close();
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 5.0F, 1.0F);
						itemsdenovo.remove(i);
						instance.econ.withdrawPlayer(p.getName(), preco);
						instance.econ.depositPlayer(vendedor, preco*0.9);
						instance.econ.depositPlayer("Banco", preco*0.1);
						instance.getMercadoManager().getItens().set("Items." + vendedor, itemsdenovo);
						try{
							instance.getMercadoManager().getItens().save(new File(instance.getDataFolder(), "items.yml"));
							instance.getMercadoManager().getItens().load(new File(instance.getDataFolder(), "items.yml"));
						}catch(Exception e1){}
						p.sendMessage(instance.getConfig().getString("Comprou").replace("&", "§").replace("@vendedor", vendedor).replace("@preco", NumberFormat.getCurrencyInstance().format(preco).replace("$", "")));
						for(int j = 0; j<3; j++){
							Firework fw = p.getWorld().spawn(p.getLocation(), Firework.class);
							FireworkMeta fm = fw.getFireworkMeta();
							fm.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.AQUA, Color.RED)
									.withFade(Color.BLACK).flicker(true).build());
							fm.setPower(0);
							fw.setFireworkMeta(fm);
						}
						p.getOpenInventory().close();
						instance.getMercadoManager().getQuantidade().put(vendedor, instance.getMercadoManager().getQuantidade().get(vendedor) - 1);
				        if(instance.getMercadoManager().getQuantidade().get(vendedor) == 0){
							instance.getMercadoManager().getQuantidade().remove(vendedor);
				        }
				        if(instance.getMercadoManager().getItens().getStringList("Items." + vendedor).size() <= 0){
				        	instance.getMercadoManager().getItens().set("Items." + vendedor, null);
				        }
				        try{
							instance.getMercadoManager().getItens().save(new File(instance.getDataFolder(), "items.yml"));
							instance.getMercadoManager().getItens().load(new File(instance.getDataFolder(), "items.yml"));
						}catch(Exception e1){}
				        if(instance.getMercadoManager().getItens().getConfigurationSection("Items").getKeys(false).size() <= 0){
				        	new File(instance.getDataFolder(), "items.yml").delete();
				        }
						return;
					}
				}
				p.sendMessage(instance.getConfig().getString("Ja_Vendido").replace("&", "§"));
				p.playSound(p.getLocation(), Sound.NOTE_STICKS, 50.0F, 1.0F);
				return;
			}else if(item.getType() == Material.WOOL && data.getData() == (byte) 14 && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().startsWith("§cCancelar")){
				if(p.getOpenInventory() != null && (!p.getOpenInventory().getTitle().startsWith("§6§lMercado"))) return;
				p.getOpenInventory().close();
				p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 5.0F, 1.0F);
				return;
			}
			if(item.hasItemMeta() && item.getItemMeta().hasLore()){
				ItemMeta im = item.getItemMeta();
				String vendedor = im.getLore().get(im.getLore().size() - 2).replace("§3§lVendedor: §f§l", "");
				ItemMeta copia = item.getItemMeta();
				List<String> lore = im.getLore();
				lore.remove(lore.size() - 1);
				lore.remove(lore.size() - 1);
				im.setLore(lore);
				item.setItemMeta(im);
				item.setItemMeta(copia);
				Inventory inv = instance.getServer().createInventory(null, 9, "§6§lMercado");
				ItemStack comprar = new ItemStack(Material.WOOL, 1, (byte) 13);
				ItemMeta comprarmeta = comprar.getItemMeta();
				comprarmeta.setDisplayName((vendedor.equalsIgnoreCase(p.getName()) ? "§aRetirar Venda" : "§aComprar Item"));
				comprar.setItemMeta(comprarmeta);
				ItemStack cancelar = new ItemStack(Material.WOOL, 1, (byte) 14);
				ItemMeta cancelarmeta = cancelar.getItemMeta();
				cancelarmeta.setDisplayName((vendedor.equalsIgnoreCase(p.getName()) ? "§cCancelar" : "§cCancelar Compra"));
				cancelar.setItemMeta(cancelarmeta);
				inv.setItem(2, comprar);
				inv.setItem(4, item);
				inv.setItem(6, cancelar);
				p.getOpenInventory().close();
				p.openInventory(inv);
			}
		}
	}

}

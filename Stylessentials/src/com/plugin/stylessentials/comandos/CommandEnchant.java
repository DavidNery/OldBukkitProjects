package com.plugin.stylessentials.comandos;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.plugin.stylessentials.Stylessentials;
import com.plugin.stylessentials.Util;

public class CommandEnchant implements CommandExecutor {
	
	private Stylessentials instance = Stylessentials.getStylessentials();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("Enchant") && sender instanceof Player) {
			if(!sender.hasPermission("stylessentials.enchant")) {
				sender.sendMessage(instance.getConfig().getString("Sem_Permissao").replace("&", "§").replace("{cmd}", label));
				return true;
			}
			if(args.length == 0) {
				sender.sendMessage(instance.getConfig().getString("Enchant.Uso_Correto").replace("&", "§"));
				return true;
			}else if(args.length == 1) {
				Player player = (Player)sender;
				ItemStack is = player.getItemInHand();
				if(is.getType() == Material.AIR) {
					sender.sendMessage(instance.getConfig().getString("Enchant.Sem_Item").replace("&", "§"));
					return true;
				}
				if(args[0].equalsIgnoreCase("clear")) {
					if(contaisEnchantments(is)) {
						removeEnchantments(is);
						sender.sendMessage(instance.getConfig().getString("Enchant.Enchants_Removidos").replace("&", "§"));
					}else {
						sender.sendMessage(instance.getConfig().getString("Enchant.Nao_Tem_Enchants").replace("&", "§"));
					}
					return true;
				}
				String enchantment = args[0];
				int level = 1;
				addEnchantment(is, enchantment, level);
				sender.sendMessage(instance.getConfig().getString("Enchant.Enchant_Adicionado").replace("&", "§")
						.replace("{enchant}", enchantment).replace("{level}", level + ""));
				return true;
			}else if(args.length >= 2) {
				Player player = (Player)sender;
				ItemStack is = player.getItemInHand();
				if(is.getType() == Material.AIR) {
					sender.sendMessage(instance.getConfig().getString("Enchant.Sem_Item").replace("&", "§"));
					return true;
				}
				if(!Util.containsNumber(args[1])) {
					sender.sendMessage(instance.getConfig().getString("Nao_E_Numero").replace("&", "§"));
					return true;
				}
				String enchantment = args[0];
				int level = Integer.valueOf(args[1]);
				addEnchantment(is, enchantment, level);
				sender.sendMessage(instance.getConfig().getString("Enchant.Enchant_Adicionado").replace("&", "§")
						.replace("{enchant}", enchantment).replace("{level}", level + ""));
				return true;
			}
			return true;
		}
		return false;
	}
	
	/*
	 * Verificar se um item contem encantamentos.
	 */
	public static boolean contaisEnchantments(ItemStack item) {
        if(item != null) {
            for(Enchantment en : item.getEnchantments().keySet()) {
                if(item.containsEnchantment(en)) {
                    return true;
                }
            }
        }
        return false;
    }
	
	/*
	 * Remover todos os encantamentos de um item.
	 */
	public static void removeEnchantments(ItemStack item) {
        if(item != null) {
            for(Enchantment en : item.getEnchantments().keySet()) {
                item.removeEnchantment(en);
            }
        }
    }
	
	/*
	 * Adicionar um encantamento a um item.
	 */
    public static Enchantment addEnchantment(ItemStack item, String enchantment, int level) {
        if(item != null) {
            item.addUnsafeEnchantment(getEnchantByName(enchantment), level);
        }
        return null;
    }
    
    /*
	 * Enchantments byName.
	 */
	public static Enchantment getEnchantByName(String enchant) {
        if(enchant.equalsIgnoreCase("Efficiency")) {
            return Enchantment.DIG_SPEED;
        }
        if(enchant.equalsIgnoreCase("Silktouch")) {
            return Enchantment.SILK_TOUCH;
        }
        if(enchant.equalsIgnoreCase("Unbreaking")) {
            return Enchantment.DURABILITY;
        }
        if(enchant.equalsIgnoreCase("Fortune")) {
            return Enchantment.LOOT_BONUS_BLOCKS;
        }
        if(enchant.equalsIgnoreCase("LuckOfTheSea")) {
            return Enchantment.LUCK;
        }
        if(enchant.equalsIgnoreCase("Lure")) {
            return Enchantment.LURE;
        }
        if(enchant.equalsIgnoreCase("Protection")) {
            return Enchantment.PROTECTION_ENVIRONMENTAL;
        }
        if(enchant.equalsIgnoreCase("Fireprotection")) {
            return Enchantment.PROTECTION_FIRE;
        }
        if(enchant.equalsIgnoreCase("Featherfalling")) {
            return Enchantment.PROTECTION_FALL;
        }
        if(enchant.equalsIgnoreCase("Blastprotection")) {
            return Enchantment.PROTECTION_EXPLOSIONS;
        }
        if(enchant.equalsIgnoreCase("Projectileprotection")) {
            return Enchantment.PROTECTION_PROJECTILE;
        }
        if(enchant.equalsIgnoreCase("Respiration")) {
            return Enchantment.OXYGEN;
        }
        if(enchant.equalsIgnoreCase("Aquaaffinity")) {
            return Enchantment.WATER_WORKER;
        }
        if(enchant.equalsIgnoreCase("Thorns")) {
            return Enchantment.THORNS;
        }
        if(enchant.equalsIgnoreCase("Depthstrider")) {
            return Enchantment.DEPTH_STRIDER;
        }
        if(enchant.equalsIgnoreCase("Sharpness")) {
            return Enchantment.DAMAGE_ALL;
        }
        if(enchant.equalsIgnoreCase("Smite")) {
            return Enchantment.DAMAGE_UNDEAD;
        }
        if(enchant.equalsIgnoreCase("Baneofarthropods")) {
            return Enchantment.DAMAGE_ARTHROPODS;
        }
        if(enchant.equalsIgnoreCase("Knockback")) {
            return Enchantment.KNOCKBACK;
        }
        if(enchant.equalsIgnoreCase("Fireaspect")) {
            return Enchantment.FIRE_ASPECT;
        }
        if(enchant.equalsIgnoreCase("Looting")) {
            return Enchantment.LOOT_BONUS_MOBS;
        }
        if(enchant.equalsIgnoreCase("Power")) {
            return Enchantment.ARROW_DAMAGE;
        }
        if(enchant.equalsIgnoreCase("Punch")) {
            return Enchantment.ARROW_KNOCKBACK;
        }
        if(enchant.equalsIgnoreCase("Flame")) {
            return Enchantment.ARROW_FIRE;
        }
        if(enchant.equalsIgnoreCase("Infinity")) {
            return Enchantment.ARROW_INFINITE;
        }
        return null;
    }

}

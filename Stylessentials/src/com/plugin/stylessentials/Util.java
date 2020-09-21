package com.plugin.stylessentials;

import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

public class Util {
	
	/*
	 * Método para verificar se uma String contem números.
	 */
	
	public static boolean containsNumber(String string) {
		if(string == null || string.length() == 0) {
			return false;
		}
		for(int i = 0; i < string.length(); i++) {
			if(!Character.isDigit(string.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Método para criar um item.
	 * Ex;
	 * List<String> lore = new ArrayList<String>();
	 * lore.add("teste");
	 * lore.add("algo");
	 * ItemStack is = createItem(Material.APPLE, 10, 0, "§cMaça", lore);
	 */
	
	public static ItemStack createItem(Material item, int quantia, int data, String nome, List<String> lore) {
		if(item != null) {
			ItemStack is = new ItemStack(item, quantia);
			if(data != 0) {
				is.setDurability((short)data);
			}
			if(nome != null) {
				ItemMeta meta = is.getItemMeta();
				meta.setDisplayName(nome);
				is.setItemMeta(meta);
			}
			if(lore != null) {
				ItemMeta meta = is.getItemMeta();
				lore.add(lore.toString());
				meta.setLore(lore);
				is.setItemMeta(meta);
			}
			return is;
		}
		return null;
	}
	
	/*
	 * Método para enviar um title a um player ou criar um for para enviar a todos.
	 */
	
	public static void sendTitle(Player player, String title) {
		PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TIMES, 
				ChatSerializer.a("{\"text\":\"" + title + "\"}"), 20, 40, 30);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(titlePacket);
	}
	
	/*
	 * Método para enviar um sub title a um player ou criar um for para enviar a todos.
	 */
	
	public static void sendSubTitle(Player player, String subTitle) {
		PacketPlayOutTitle subTitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, 
				ChatSerializer.a("{\"text\":\"" + subTitle + "\"}"), 20, 40, 30);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(subTitlePacket);
	}
	
	/*
	 * Pegar encantamentos por nome.
	 * Encantamentos da 1.8
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
	
	/*
	 * Pegar efeitos por nome.
	 * Efeitos da 1.8
	 */
	
	public static PotionEffectType getEffectByName(String potion) {
        if(potion.equalsIgnoreCase("Nightvision")) {
            return PotionEffectType.NIGHT_VISION;
        }
        if(potion.equalsIgnoreCase("Invisibility")) {
            return PotionEffectType.INVISIBILITY;
        }
        if(potion.equalsIgnoreCase("Jump")) {
            return PotionEffectType.JUMP;
        }
        if(potion.equalsIgnoreCase("Fireresistance")) {
            return PotionEffectType.FIRE_RESISTANCE;
        }
        if(potion.equalsIgnoreCase("Speed")) {
            return PotionEffectType.SPEED;
        }
        if(potion.equalsIgnoreCase("Slowness")) {
            return PotionEffectType.SLOW;
        }
        if(potion.equalsIgnoreCase("Healing")) {
            return PotionEffectType.HEAL;
        }
        if(potion.equalsIgnoreCase("Damage")) {
            return PotionEffectType.HARM;
        }
        if(potion.equalsIgnoreCase("Poison")) {
            return PotionEffectType.POISON;
        }
        if(potion.equalsIgnoreCase("Regeneration")) {
            return PotionEffectType.REGENERATION;
        }
        if(potion.equalsIgnoreCase("Strenght")) {
            return PotionEffectType.INCREASE_DAMAGE;
        }
        if(potion.equalsIgnoreCase("Weakness")) {
            return PotionEffectType.WEAKNESS;
        }
        if(potion.equalsIgnoreCase("Hunger")) {
            return PotionEffectType.HUNGER;
        }
        return null;
    }
	
	/*
	 * Método para lançar um foguete aleatório em um player.
	 */
	
	public static void shootFireWork(Player player) {
		Location loc = player.getLocation();
		org.bukkit.entity.Firework fw = loc.getWorld().spawn(loc, org.bukkit.entity.Firework.class);
		FireworkMeta fm = fw.getFireworkMeta();
		fm.addEffect(FireworkEffect.builder().with(getFireworkRandomType()).withColor(getFireworkRandomColor(), getFireworkRandomColor())
				.withFade(getFireworkRandomColor()).flicker(getFireworkRandomBoolean()).build());
		fm.setPower(getFireworkRandomPower());
		fw.setFireworkMeta(fm);
	}
	
	/*
	 * Método fireworks
	 */
	
	public static Color getFireworkRandomColor() {
		Color c = null;
		Random r = new Random();
		int next = r.nextInt(17);
		if(next == 0) {
			c = Color.AQUA;
		}else if(next == 1) {
			c = Color.BLACK;
		}else if(next == 2) {
			c = Color.BLUE;
		}else if(next == 3) {
			c = Color.FUCHSIA;
		}else if(next == 4) {
			c = Color.GRAY;
		}else if(next == 5) {
			c = Color.GREEN;
		}else if(next == 6) {
			c = Color.LIME;
		}else if(next == 7) {
			c = Color.MAROON;
		}else if(next == 8) {
			c = Color.NAVY;
		}else if(next == 9) {
			c = Color.OLIVE;
		}else if(next == 10) {
			c = Color.ORANGE;
		}else if(next == 11) {
			c = Color.PURPLE;
		}else if(next == 12) {
			c = Color.RED;
		}else if(next == 13) {
			c = Color.SILVER;
		}else if(next == 14) {
			c = Color.TEAL;
		}else if(next == 15) {
			c = Color.WHITE;
		}else if(next == 16) {
			c = Color.YELLOW;
		}
		return c;
	}
	
	/*
	 * Método fireworks
	 */
	
	public static Type getFireworkRandomType() {
		Type t = null;
		Random r = new Random();
		int next = r.nextInt(5);
		if(next == 0) {
			t = Type.BALL;
		}else if(next == 1) {
			t = Type.BALL_LARGE;
		}else if(next == 2) {
			t = Type.BURST;
		}else if(next == 3) {
			t = Type.CREEPER;
		}else if(next == 4) {
			t = Type.STAR;
		}
		return t;
	}
	
	/*
	 * Método fireworks
	 */
	
	public static int getFireworkRandomPower() {
		Random r = new Random();
		return r.nextInt(2);
	}
	
	/*
	 * Método fireworks
	 */
	
	public static Boolean getFireworkRandomBoolean() {
		Random r = new Random();
		return r.nextBoolean();
	}
	
	/*
	 * Método para teleportar player.
	 */
	
	public static void teleportPlayer(Player player1, Player player2) {
		player1.teleport(player2.getLocation());
	}
	
	/*
	 * Método para teleportar player.
	 */
	
	public static void teleportPlayer(Player player, Location location) {
		player.teleport(location);
	}
	
	/*
	 * Método para verificar se um player esta em um gamemode.
	 */
	
	public static boolean isInGamemode(Player player, String gamemode) {
		if(player != null) {
			if(gamemode != null) {
				if(player.getGameMode() == getGamemodeByName(gamemode)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * Pegar gamemode por name.
	 * Gamemode da 1.8
	 */
	
	public static GameMode getGamemodeByName(String gamemode) {
		if(gamemode.equalsIgnoreCase("survival")||gamemode.equalsIgnoreCase("0")) {
			return GameMode.SURVIVAL;
		}
		if(gamemode.equalsIgnoreCase("creative")||gamemode.equalsIgnoreCase("1")) {
			return GameMode.CREATIVE;
		}
		if(gamemode.equalsIgnoreCase("adventure")||gamemode.equalsIgnoreCase("2")) {
			return GameMode.ADVENTURE;
		}
		if(gamemode.equalsIgnoreCase("spectator")||gamemode.equalsIgnoreCase("3")) {
			return GameMode.SPECTATOR;
		}
		return null;
	}
	
}
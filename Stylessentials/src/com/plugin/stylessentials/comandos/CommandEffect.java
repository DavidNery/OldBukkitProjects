package com.plugin.stylessentials.comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.plugin.stylessentials.Stylessentials;
import com.plugin.stylessentials.Util;

public class CommandEffect implements CommandExecutor {
	
	private Stylessentials instance = Stylessentials.getStylessentials();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("effect")) {
			if(!sender.hasPermission("stylessentials.effect")) {
				sender.sendMessage(instance.getConfig().getString("Sem_Permissao").replace("&", "§").replace("{cmd}", label));
				return true;
			}
			if(args.length == 2) {
				if(args[1].equalsIgnoreCase("clear")) {
					Player target = sender.getServer().getPlayer(args[0]);
					if(target == null) {
						sender.sendMessage(instance.getConfig().getString("Player_Nao_Encontrado").replace("&", "§").replace("{player}", args[0]));
						return true;
					}
					removePotionEffects(target);
					sender.sendMessage(instance.getConfig().getString("Effect.Removeu_Outro").replace("&", "§").replace("{player}", args[0]));
					return true;
				}
				return true;
			}
			if(args.length <= 2) {
				sender.sendMessage(instance.getConfig().getString("Effect.Uso_Correto").replace("&", "§"));
				return true;
			}else if(args.length == 3) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage(instance.getConfig().getString("Player_Nao_Encontrado").replace("&", "§").replace("{player}", args[0]));
					return true;
				}
				if(!Util.containsNumber(args[2])) {
					sender.sendMessage(instance.getConfig().getString("Nao_E_Numero").replace("&", "§"));
					return true;
				}
				String efeito = args[1];
				int time = Integer.valueOf(args[2]);
				int level = 1;
				if(time < 1) {
					sender.sendMessage(instance.getConfig().getString("Effect.Tempo").replace("&", "§"));
					return true;
				}else if(level < 1) {
					sender.sendMessage(instance.getConfig().getString("Effect.Nivel").replace("&", "§"));
					return true;
				}
				addPotionEffect(target, efeito, time, level);
				sender.sendMessage(instance.getConfig().getString("Effect.Aplicou_Efeito").replace("&", "§")
						.replace("{player}", target.getName()).replace("{efeito}", efeito).replace("{tempo}", time + "").replace("{level}", level + ""));
				return true;
			}else if(args.length >= 4) {
				Player target = sender.getServer().getPlayer(args[0]);
				if(target == null) {
					sender.sendMessage(instance.getConfig().getString("Player_Nao_Encontrado").replace("&", "§").replace("{player}", args[0]));
					return true;
				}
				if(!Util.containsNumber(args[2])) {
					sender.sendMessage(instance.getConfig().getString("Nao_E_Numero").replace("&", "§"));
					return true;
				}
				if(!Util.containsNumber(args[3])) {
					sender.sendMessage(instance.getConfig().getString("Nao_E_Numero").replace("&", "§"));
					return true;
				}
				String efeito = args[1];
				int time = Integer.valueOf(args[2]);
				int level = Integer.valueOf(args[3]);
				if(time < 1) {
					sender.sendMessage(instance.getConfig().getString("Effect.Tempo").replace("&", "§"));
					return true;
				}else if(level < 1) {
					sender.sendMessage(instance.getConfig().getString("Effect.Nivel").replace("&", "§"));
					return true;
				}
				addPotionEffect(target, efeito, time, level);
				sender.sendMessage(instance.getConfig().getString("Effect.Aplicou_Efeito").replace("&", "§")
						.replace("{player}", target.getName()).replace("{efeito}", efeito).replace("{tempo}", time + "").replace("{level}", level + ""));
				return true;
			}
			return true;
		}
		return false;
	}
	
	/*
	 * Remover os efeitos de poÃ§Ã£o de um player.
	 */
	
	public static void removePotionEffects(Player player) {
        if(player != null) {
            for(PotionEffect pe : player.getActivePotionEffects()) {
                player.removePotionEffect(pe.getType());
            }
        }
    }
	
	/*
	 * Remover um efeito de poÃ§Ã£o de um player.
	 */
   
    public static void removePotionEffect(Player player, String potion) {
        if(player != null) {
            player.removePotionEffect(getByName(potion));
        }
    }
	
	/*
	 * Adicionar efeito de poÃ§Ã£o a um player.
	 */
	
	public static void addPotionEffect(Player player, String potion, int time, int level) {
        if(player != null) {
        	player.removePotionEffect(getByName(potion));
            player.addPotionEffect(new PotionEffect(getByName(potion), time * 20, level - 1));
        }
    }
	
	/*
	 * Pegar um efeito por nome.
	 */
	
	public static PotionEffectType getByName(String potion) {
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

}

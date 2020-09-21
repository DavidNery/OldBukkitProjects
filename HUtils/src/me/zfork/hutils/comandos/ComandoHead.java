package me.zfork.hutils.comandos;

import me.zfork.hutils.HUtils;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class ComandoHead implements CommandExecutor{
	
	private HUtils instance = HUtils.getHUtils();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("head")){
			if(!(sender instanceof Player)){
				sender.sendMessage("Unkown command. Type \"help\" for help.");
				return true;
			}
			Player p = (Player) sender;
			if(!p.hasPermission("utils.head")){
				p.sendMessage(instance.getConfig().getString("Mensagem.Sem_Permissao").replace("&", "§").replace("@cmd", label));
				return true;
			}
			if(p.getInventory().firstEmpty() == -1){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Inventario_Cheio").replace("&", "§"));
				return true;
			}
			if(args.length == 0){
				ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                meta.setOwner(p.getName());
                meta.setDisplayName(instance.getConfig().getString("Config.Nome_Cabeca").replace("&", "§").replace("@player", p.getName()));
                skull.setItemMeta(meta);
                p.getInventory().addItem(skull);
                p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Pegou_Cabeca").replace("&", "§"));
			}else{
				ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                meta.setOwner(instance.getServer().getPlayer(args[0]) == null ? args[0] : instance.getServer().getPlayer(args[0]).getName());
                meta.setDisplayName(instance.getConfig().getString("Config.Nome_Cabeca").replace("&", "§").replace("@player", instance.getServer().getPlayer(args[0]) == null ? args[0] : instance.getServer().getPlayer(args[0]).getName()));
                skull.setItemMeta(meta);
                p.getInventory().addItem(skull);
                p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Pegou_Cabeca_Outro").replace("&", "§").replace("@player", instance.getServer().getPlayer(args[0]) == null ? args[0] : instance.getServer().getPlayer(args[0]).getName()));
			}
		}
		return false;
	}

}

package me.zfork.hutils.listeners;

import java.text.NumberFormat;

import me.zfork.hutils.HUtils;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;

public class Listeners implements Listener{
	
	private HUtils instance = HUtils.getHUtils();
	
	@EventHandler
	public void Shoot(final ProjectileHitEvent e){
		new BukkitRunnable() {
			@Override
			public void run() {
				e.getEntity().remove();
			}
		}.runTaskLater(instance, 2*20);
	}
	
	@EventHandler(ignoreCancelled=true)
	public void Chat(ChatMessageEvent e){
		Player p = e.getSender();
		if(p.hasPermission("hutils.admin")) return;
		if(e.getChannel().getName().equalsIgnoreCase("global")){
			if(instance.getEcon().has(p.getName(), instance.getConfig().getDouble("Config.Preco_Falar"))){
				instance.getEcon().depositPlayer(p.getName(), e.getChannel().getCostPerMessage());
			}else{
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Dinheiro").replace("&", "§").replace("@valor", NumberFormat.getNumberInstance().format(instance.getConfig().getDouble("Config.Preco_Falar"))));
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void Join(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(instance.getConfig().contains("Config.Grupos." + instance.getPermission().getPrimaryGroup(p))){
			for(Player on : instance.getServer().getOnlinePlayers()){
				if(on == null) continue;
				on.sendMessage(instance.getConfig().getString("Config.Grupos." + instance.getPermission().getPrimaryGroup(p)).replace("&", "§").replace("@player", p.getName()));
			}
		}
	}
	
	@EventHandler
	public void Close(InventoryCloseEvent e){
		if(e.getInventory().getTitle().equalsIgnoreCase("§6§lLIXEIRA")){
			e.getInventory().clear();
		}
	}
	
	@EventHandler
	public void Break(BlockBreakEvent e){
		Player p = e.getPlayer();
		if(p.getItemInHand() != null && instance.getConfig().getStringList("Config.Items_Banidos") != null
				&& instance.getConfig().getStringList("Config.Items_Banidos").size() != 0){
			if(instance.getConfig().getStringList("Config.Items_Banidos").contains(p.getItemInHand().getType().name().toLowerCase())
					|| instance.getConfig().getStringList("Config.Items_Banidos").contains(p.getItemInHand().getTypeId() + "")){
				e.setCancelled(true);
				p.setItemInHand(null);
			}
		}
	}
	
	@EventHandler
	public void Place(BlockPlaceEvent e){
		Player p = e.getPlayer();
		if(p.getItemInHand() != null && instance.getConfig().getStringList("Config.Items_Banidos") != null
				&& instance.getConfig().getStringList("Config.Items_Banidos").size() != 0){
			if(instance.getConfig().getStringList("Config.Items_Banidos").contains(p.getItemInHand().getType().name().toLowerCase())
					|| instance.getConfig().getStringList("Config.Items_Banidos").contains(p.getItemInHand().getTypeId() + "")){
				e.setCancelled(true);
				p.setItemInHand(null);
			}
		}
	}
	
	@EventHandler
	public void Interact(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if(p.getItemInHand() != null && instance.getConfig().getStringList("Config.Items_Banidos") != null
				&& instance.getConfig().getStringList("Config.Items_Banidos").size() != 0){
			if(instance.getConfig().getStringList("Config.Items_Banidos").contains(p.getItemInHand().getType().name().toLowerCase())
					|| instance.getConfig().getStringList("Config.Items_Banidos").contains(p.getItemInHand().getTypeId() + "")){
				e.setCancelled(true);
				p.setItemInHand(null);
			}
		}
	}
	
	/*@EventHandler
	public void Console(PlayerCommandPreprocessEvent e){
		String cmd = e.getMessage().toLowerCase();
		if(cmd.startsWith("/") && cmd.contains("console")){
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void Desconhecido(PlayerCommandPreprocessEvent e){
		Player p = e.getPlayer();
    	String cmd = e.getMessage().split(" ")[0];
    	HelpTopic topic = Bukkit.getServer().getHelpMap().getHelpTopic(cmd);
    	String mensagem = instance.getConfig().getString("Mensagem.Erro.Comando_Desconhecido").replace("&", "§").replace("@cmd", e.getMessage());
    	if(topic == null){
    		p.playSound(p.getLocation(), Sound.NOTE_BASS_GUITAR, 7.0F, 1.0F);
    		p.sendMessage(mensagem);
    		e.setCancelled(true);
    	}
	}*/
	
	@EventHandler
	public void Damage(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player) e.getEntity();
			if(e.getCause() == DamageCause.VOID){
				p.chat("/spawn");
				e.setCancelled(true);
				e.setDamage(0);
			}
		}
	}
	
	@EventHandler
	public void Death(PlayerDeathEvent e){
		Player p = e.getEntity();
		p.getWorld().strikeLightningEffect(p.getLocation());
		if(p.getKiller() instanceof Player){
			String msg = instance.getConfig().getString("Mensagem.Player_Morreu").replace("&", "§").replace("@player", p.getName()).replace("@killer", p.getKiller().getName());
			if(p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName()){
				msg = msg.replace("@item", p.getItemInHand().getItemMeta().getDisplayName());
			}else{
				msg = msg.replace("@item", p.getItemInHand().getType().name());
			}
			for(Player on : instance.getServer().getOnlinePlayers()){
				on.sendMessage(msg);
			}
			p.sendMessage(instance.getConfig().getString("Mensagem.Foi_Morto").replace("&", "§").replace("@killer", p.getKiller().getName()));
			if(Math.random() * 100 <= instance.getConfig().getDouble("Config.Chance_Dropar_Cabeca")){
				ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                meta.setOwner(p.getName());
                meta.setDisplayName(instance.getConfig().getString("Config.Nome_Cabeca").replace("&", "§").replace("@player", p.getName()));
                skull.setItemMeta(meta);
                e.getDrops().add(skull);
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void Command(PlayerCommandPreprocessEvent e){
		Player p = e.getPlayer();
		p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 5.0F, 1.0F);
	}

}

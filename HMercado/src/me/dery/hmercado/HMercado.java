package me.dery.hmercado;

import java.io.File;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class HMercado extends JavaPlugin{

	public static Economy econ = null;
	private MercadoManager mm;
	public static boolean ativado = true;
	private BukkitTask task = null;

	@SuppressWarnings("static-access")
	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bHMercado§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bDery");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null){
			getServer().getConsoleSender().sendMessage(" §3Vault: §bNao Encontrado");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}else{
			getServer().getConsoleSender().sendMessage(" §3Vault: §bHooked (Economy)");
			if(!new File(getDataFolder(), "config.yml").exists()){
				saveDefaultConfig();
				getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
			}else{
				getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
			}
			setupEconomy();
			getCommand("mercado").setExecutor(new Comandos());
			Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);
			getServer().getConsoleSender().sendMessage(" §3Carregando itens...");
			if(new File(getDataFolder(), "items.yml").exists()){
				getMercadoManager().loadItens();
			}else{
				getServer().getConsoleSender().sendMessage(" §3Nao haviam itens para serem carregados");
			}
			task = new BukkitRunnable(){
				@Override
				public void run() {
					recarregarMercado();
				}
			}.runTaskTimer(this, 120*20, getConfig().getInt("Tempo_Atualizar_Mercado")*20);
		}
		getServer().getConsoleSender().sendMessage("§3==========[§bHMercado§3]==========");
	}

	public void onDisable(){
		HandlerList.unregisterAll(this);
		if(task != null) task.cancel();
		getServer().getConsoleSender().sendMessage("§4==========[§cHMercado§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §cDery");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cHMercado§4]==========");
	}

	public boolean setupEconomy(){
		RegisteredServiceProvider<Economy> ec = getServer().getServicesManager().getRegistration(Economy.class);
		if(ec!=null){
			econ = (Economy)ec.getProvider();
		}
		return econ != null;
	}

	@SuppressWarnings("static-access")
	public void recarregarMercado(){
		if(!new File(getDataFolder(), "items.yml").exists()) return;
		for(Player on : getServer().getOnlinePlayers()){
			if(on.getOpenInventory() != null && on.getOpenInventory().getTitle().startsWith("§6§lMercado")){
				on.getOpenInventory().close();
			}
		}
		setAtivado(false);
		getMercadoManager().setPg(1);
		Inventory inv = getServer().createInventory(null, 54, "§6§lMercado - §7§lPagina " + getMercadoManager().getPage());
		getMercadoManager().getPaginas().clear();
		getMercadoManager().getPaginas().put(getMercadoManager().getPage(), inv);
		int i = 0;
		for(String donos : getMercadoManager().getItens().getConfigurationSection("Items").getKeys(false)){
			for(String items : getMercadoManager().getItens().getStringList("Items." + donos)){
				if((inv.firstEmpty() + 1) == 54){
					ItemStack botao = new ItemStack(Material.STONE_BUTTON);
					ItemMeta im = botao.getItemMeta();
					im.setDisplayName("§a» Proxima pagina »");
					botao.setItemMeta(im);
					getMercadoManager().getPaginas().get(getMercadoManager().getPage()).setItem(53, botao);
					ItemStack botaovoltar = new ItemStack(Material.STONE_BUTTON);
					ItemMeta imvoltar = botaovoltar.getItemMeta();
					imvoltar.setDisplayName("§c« Pagina anterior «");
					botaovoltar.setItemMeta(imvoltar);
					getMercadoManager().getPaginas().get(getMercadoManager().getPage()).setItem(45, botaovoltar);
					getMercadoManager().setPg(getMercadoManager().getPage() + 1);
					inv = getServer().createInventory(null, 54, "§6§lMercado - §7§lPagina " + getMercadoManager().getPage());
					i = 0;
					getMercadoManager().getPaginas().put(getMercadoManager().getPage(), inv);
					getMercadoManager().getPaginas().get(getMercadoManager().getPage()).setItem(i, getMercadoManager().buildItemStack(items, donos, items.split(";")[5]));
				}else{
					getMercadoManager().getPaginas().get(getMercadoManager().getPage()).setItem(i, getMercadoManager().buildItemStack(items, donos, items.split(";")[5]));
					i++;
				}
				if(!getMercadoManager().getQuantidade().containsKey(donos)){
					getMercadoManager().getQuantidade().put(donos, 1);
				}else{
					getMercadoManager().getQuantidade().put(donos, getMercadoManager().getQuantidade().get(donos) + 1);
				}
			}
		}
		for(Player on : getServer().getOnlinePlayers()){
			if(getConfig().getBoolean("Anunciar_Atualizacao_Mercado") == true){
				on.sendMessage(getConfig().getString("Mercado_Atualizado").replace("&", "§"));
			}
		}
		setAtivado(true);
	}

	public static HMercado getHMercado(){
		return (HMercado) Bukkit.getServer().getPluginManager().getPlugin("HMercado");
	}

	public MercadoManager getMercadoManager(){
		return mm;
	}

	public boolean getAtivado(){
		return ativado;
	}

	@SuppressWarnings("static-access")
	public void setAtivado(boolean ativado){
		this.ativado = ativado;
	}

}

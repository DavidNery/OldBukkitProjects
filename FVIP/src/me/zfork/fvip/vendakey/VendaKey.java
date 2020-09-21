package me.zfork.fvip.vendakey;

import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

import me.zfork.fvip.FVip;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class VendaKey extends BukkitRunnable{
	
	private FVip instance;
	private String key;
	private double preco;
	private String vendedor;
	private String grupo;
	private String tempo;
	private int i;
	
	public VendaKey(FVip instance, String key, double preco, String vendedor, String grupo, String tempo){
		this.instance = instance;
		this.key = key;
		this.preco = preco;
		this.vendedor = vendedor;
		this.grupo = grupo;
		this.tempo = tempo;
		this.i = instance.getConfig().getInt("Config.VendaKey.Anuncios");
	}
	
	public String getKey(){
		return this.key;
	}
	
	public double getPreco(){
		return this.preco;
	}
	
	public String getVendedor(){
		return this.vendedor;
	}
	
	public String getGrupo(){
		return this.grupo;
	}
	
	public String getTempo(){
		return this.tempo;
	}

	@Override
	public void run() {
		if(i > 0){
			for(Player on : instance.getServer().getOnlinePlayers()){
				for(String msg : instance.getConfig().getStringList("Mensagem.VendaKey.Iniciou")){
					on.sendMessage(msg.replace("{vendedor}", vendedor).replace("{preco}", NumberFormat.getCurrencyInstance().format(preco).replace("$", ""))
							.replace("{acaba}", i + "").replace("{tempo}", (i*instance.getConfig().getInt("Config.VendaKey.Tempo_Anuncios")) + "")
							.replace("{grupo}", grupo).replace("{tempokey}", tempo).replace("&", "§"));
				}
			}
			i--;
		}else{
			instance.getTasks().get(vendedor).cancel();
			instance.getTasks().remove(vendedor);
			for(Player on : instance.getServer().getOnlinePlayers()){
				for(String msg : instance.getConfig().getStringList("Mensagem.VendaKey.Acabou")){
					on.sendMessage(msg.replace("{vendedor}", vendedor).replace("{preco}", NumberFormat.getCurrencyInstance().format(preco).replace("$", ""))
							.replace("{grupo}", grupo).replace("{tempokey}", tempo).replace("&", "§"));
				}
			}
			instance.getVendakey().put(vendedor, System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.VendaKey.Delay")));
		}
	}

}

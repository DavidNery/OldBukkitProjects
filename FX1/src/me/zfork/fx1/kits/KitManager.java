package me.zfork.fx1.kits;

import java.util.ArrayList;

import me.zfork.fx1.FX1;

public class KitManager {
	
	private FX1 instance;
	private ArrayList<Kit> kits;
	
	public KitManager(FX1 instance){
		this.instance = instance;
		this.kits = new ArrayList<Kit>();
	}
	
	public void loadAllKits(){
		int i = 0;
		for(String kitname : instance.getConfig().getConfigurationSection("Config.Kits").getKeys(false)){
			Kit kit = new Kit(kitname);
			if(instance.getConfig().contains("Config.Kits." + kitname + ".Custo")) kit.setCusto(instance.getConfig().getInt("Config.Kits." + kitname + ".Custo"));
			if(instance.getConfig().contains("Config.Kits." + kitname + ".Armadura.Capacete")) 
				kit.setItem(3, instance.getConfig().getString("Config.Kits." + kitname + ".Armadura.Capacete"));
			if(instance.getConfig().contains("Config.Kits." + kitname + ".Armadura.Peitoral")) 
				kit.setItem(2, instance.getConfig().getString("Config.Kits." + kitname + ".Armadura.Peitoral"));
			if(instance.getConfig().contains("Config.Kits." + kitname + ".Armadura.Calca")) 
				kit.setItem(1, instance.getConfig().getString("Config.Kits." + kitname + ".Armadura.Calca"));
			if(instance.getConfig().contains("Config.Kits." + kitname + ".Armadura.Bota")) 
				kit.setItem(0, instance.getConfig().getString("Config.Kits." + kitname + ".Armadura.Bota"));
			if(instance.getConfig().contains("Config.Kits." + kitname + ".Itens")){
				for(String item : instance.getConfig().getStringList("Config.Kits." + kitname + ".Itens")){
					kit.getItems().add(item);
				}
			}
			if(instance.getConfig().contains("Config.Kits." + kitname + ".Padrao")) kit.setPadrao(instance.getConfig().getBoolean("Config.Kits." + kitname + ".Padrao"));
			if(instance.getConfig().contains("Config.Kits." + kitname + ".Proprio")) kit.setProprio(instance.getConfig().getBoolean("Config.Kits." + kitname + ".Proprio"));
			if(instance.getConfig().contains("Config.Kits." + kitname + ".Perde")) kit.setPerde(instance.getConfig().getBoolean("Config.Kits." + kitname + ".Perde"));
			kits.add(kit);
			i++;
		}
		instance.getServer().getConsoleSender().sendMessage(" §b" + i + " §3kits carregados!");
	}
	
	public ArrayList<Kit> getKits(){
		return this.kits;
	}
	
	public Kit getKit(String nome){
		for(Kit kit : kits) if(kit.getNome().equalsIgnoreCase(nome)) return kit;
		return null;
	}
	
	public Kit getDefaultKit(){
		for(Kit kit : kits) if(kit.getPadrao()) return kit;
		return null;
	}

}

package me.zfork.fkitspro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayersKits {
	
	public static ArrayList<PlayersKits> playerKitsCache = new ArrayList<>();
	
	private String name;
	private HashMap<String, Long> playerKitsDelay;
	
	public PlayersKits(String name) {
		this.name = name;
		this.playerKitsDelay = new HashMap<>();
		playerKitsCache.add(this);
	}
	
	public String getName() {
		return name;
	}
	
	public HashMap<String, Long> getPlayerKitsDelay() {
		return playerKitsDelay;
	}

	/** 
	 * 
	 * @param kitName Nome do kit que deseja saber o pr�ximo delay para pegar (millis)
	 * @return <b>-1</b> se o kit n�o tiver sido pego, 
	 * <b>-2</b> se o kit s� puder ser pego 1 vez e 
	 * <b>-3</b> se o usu�rio ainda n�o puder pegar novamente.<br/>
	 * Tamb�m retorna um <b>milli</b> se puder pegar novamente.
	 */
	public long getUsedKit(String kitName) {
		for(Map.Entry<String, Long> kits : playerKitsDelay.entrySet())
			if(kits.getKey().equalsIgnoreCase(kitName))
				if(kits.getValue() >= System.currentTimeMillis())
					return kits.getValue();
				else
					return (kits.getValue() == -2L ? -2L : -3L);
		return -1;
	}
	
	public void addDelay(String kitName, long newDelay) {
		for(Map.Entry<String, Long> kits : playerKitsDelay.entrySet())
			if(kits.getKey().equalsIgnoreCase(kitName)){
				kits.setValue(newDelay);
				return;
			}
		// N�o tinha pego o kit, ent�o adiciona
		playerKitsDelay.put(kitName, newDelay);
	}
	
	public void removeDelay(String kitName) {
		for(String kits : playerKitsDelay.keySet())
			if(kits.equalsIgnoreCase(kitName)){
				playerKitsDelay.remove(kits);
				return;
			}
	}

}

package me.zfork.fvip.builders;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class JSONMessage {

	public enum ClickAction{
		RUN_COMMAND, SUGGEST_COMMAND, OPEN_URL
	}

	public enum HoverAction{
		SHOW_TEXT
	}

	private static final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	private static final Pattern pattern = Pattern.compile("([&|§][a-fA-F0-9k-oK-orR]){1}");
	private ArrayList<String> partes;
	private String json = "";

	public JSONMessage(){
		json += "[\"\",";
		partes = new ArrayList<>();
	}

	public JSONMessage parseToJSON(String text){
		Matcher matcher = pattern.matcher(text);
		if(matcher.find()){
			String cor = "";
			String palavra = "";
			String geral = "";
			char[] array = text.toCharArray();
			for(int j = 0; j<array.length; j++){
				if(j+1 != array.length && pattern.matcher(String.valueOf(array[j]) + String.valueOf(array[j+1])).matches()){
					cor += String.valueOf(array[j]) + String.valueOf(array[j+1]);
					j+=1;
				}else{
					palavra += array[j];
					if(j+1 != array.length){
						if(String.valueOf(array[j+1]).matches("&|§")){
							/*json += "{\"text\":\"" + palavra + "\"";
							json += getFormat(cor);
							json += "},";*/
							geral += "{\"text\":\"" + palavra + "\"" + getFormat(cor) + "},";
							partes.add(geral);
							cor = "";
							palavra = "";
							geral = "";
						}
					}else{
						/*json += "{\"text\":\"" + palavra + "\"";
						json += getFormat(cor);
						json += "},";*/
						geral += "{\"text\":\"" + palavra + "\"" + getFormat(cor) + "},";
						partes.add(geral);
						geral = "";
					}
				}
			}
		}
		return this;
	}

	private String getFormat(String cor){
		String retornar = "";
		if(cor.matches("(.+)?([&§]0)(.+)?")) retornar += ",\"color\":\"black\"";
		if(cor.matches("(.+)?([&§]1)(.+)?")) retornar += ",\"color\":\"dark_blue\"";
		if(cor.matches("(.+)?([&§]2)(.+)?")) retornar += ",\"color\":\"dark_green\"";
		if(cor.matches("(.+)?([&§]3)(.+)?")) retornar += ",\"color\":\"dark_aqua\"";
		if(cor.matches("(.+)?([&§]4)(.+)?")) retornar += ",\"color\":\"dark_red\"";
		if(cor.matches("(.+)?([&§]5)(.+)?")) retornar += ",\"color\":\"dark_purple\"";
		if(cor.matches("(.+)?([&§]6)(.+)?")) retornar += ",\"color\":\"gold\"";
		if(cor.matches("(.+)?([&§]7)(.+)?")) retornar += ",\"color\":\"gray\"";
		if(cor.matches("(.+)?([&§]8)(.+)?")) retornar += ",\"color\":\"dark_gray\"";
		if(cor.matches("(.+)?([&§]9)(.+)?")) retornar += ",\"color\":\"blue\"";
		if(cor.matches("(.+)?([&§]a)(.+)?")) retornar += ",\"color\":\"green\"";
		if(cor.matches("(.+)?([&§]b)(.+)?")) retornar += ",\"color\":\"aqua\"";
		if(cor.matches("(.+)?([&§]c)(.+)?")) retornar += ",\"color\":\"red\"";
		if(cor.matches("(.+)?([&§]d)(.+)?")) retornar += ",\"color\":\"light_purple\"";
		if(cor.matches("(.+)?([&§]e)(.+)?")) retornar += ",\"color\":\"yellow\"";
		if(cor.matches("(.+)?([&§]f)(.+)?")) retornar += ",\"color\":\"white\"";
		if(cor.matches("(.+)?([&§]k)(.+)?")) retornar += ",\"obfuscated\":true";
		if(cor.matches("(.+)?([&§]l)(.+)?")) retornar += ",\"bold\":true";
		if(cor.matches("(.+)?([&§]m)(.+)?")) retornar += ",\"strikethrough\":true";
		if(cor.matches("(.+)?([&§]n)(.+)?")) retornar += ",\"underlined\":true";
		if(cor.matches("(.+)?([&§]o)(.+)?")) retornar += ",\"italic\":true";
		return retornar;
	}

	public JSONMessage addText(String text){
		if(partes.size()>0){
			for(String parte : partes){
				json += parte;
			}
			partes.clear();
		}
		Matcher matcher = pattern.matcher(text);
		if(matcher.find()){
			parseToJSON(text);
		}else{
			json += "{\"text\":\"" + text + "\"},";
		}
		return this;
	}

	public JSONMessage withHoverAction(HoverAction hoveraction, String value){
		for(int i = 0; i<partes.size(); i++){
			String parte = partes.get(i);
			if(parte.endsWith("},")) parte = parte.substring(0, parte.length()-2);
			parte += ",\"hoverEvent\":{\"action\":\"" + hoveraction.name().toLowerCase() + "\",\"value\":\"" + value + "\"}},";
			partes.set(i, parte);
		}
		return this;
	}

	public JSONMessage withClickAction(ClickAction clickaction, String value){
		for(int i = 0; i<partes.size(); i++){
			String parte = partes.get(i);
			if(parte.endsWith("},")) parte = parte.substring(0, parte.length()-2);
			parte += ",\"clickEvent\":{\"action\":\"" + clickaction.name().toLowerCase() + "\",\"value\":\"" + value + "\"}},";
			partes.set(i, parte);
		}
		return this;
	}

	public JSONMessage removeLastElement(){
		if(partes.size()>0){
			for(String parte : partes){
				json += parte;
			}
			partes.clear();
		}
		this.json = json.substring(0, json.lastIndexOf("{\"text\":\""));
		return this;
	}

	@Override
	public String toString(){
		return this.json;
	}

	public void sendJson(Player p){
		try {
			if(partes.size()>0){
				for(String parte : partes){
					json += parte;
				}
				partes.clear();
			}
			if(json.endsWith(",")) json = json.substring(0, json.length()-1);
			if(!json.endsWith("]")) json += "]";
			Class<?> chatSerializer = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer");
			Class<?> chatComponent = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
			Class<?> packet = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat");
			Constructor<?> constructor = packet.getConstructor(chatComponent);

			Object text = chatSerializer.getMethod("a", String.class).invoke(chatSerializer, json);
			Object packetFinal = constructor.newInstance(text);

			Object handle = p.getClass().getMethod("getHandle").invoke(p);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + version + ".Packet")).invoke(playerConnection, packetFinal);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

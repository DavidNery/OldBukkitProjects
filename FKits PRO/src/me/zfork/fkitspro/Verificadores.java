package me.zfork.fkitspro;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Verificadores {
	
	private FKitsPRO instance;
	
	public Verificadores(FKitsPRO instance) {
		this.instance = instance;
	}
	
	public synchronized boolean CheckKey(CommandSender sender){
		String ip = "";
		String fakeip = "";
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while(interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				if (iface.isLoopback() || !iface.isUp()) continue;
				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				ip = addresses.nextElement().getHostAddress();
				while(addresses.hasMoreElements()){
					InetAddress ia = addresses.nextElement();
					if(ia.getHostAddress().matches("^(\\d{1,3}(\\.\\d{1,3}){3})$"))
						ip = ia.getHostAddress();
					else
						fakeip = ia.getHostAddress();
				}
			}
			URL url = new URL("http://pluginsdodery.esy.es/checar_plugin.php?plugin=FKitsPRO&key=" + instance.getConfig().getString("Config.Key") + "&ip="
					+ (ip.equals("") ? fakeip : ip)/* + ":" + Bukkit.getServer().getPort()*/);
			URLConnection connection = null;
			connection = url.openConnection();
			connection.connect();
			connection.setReadTimeout(5000);
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String key = buffReader.readLine();
			if(!(sender instanceof Player))
				sender.sendMessage(" §3Seu IP: §b" + (ip.equals("") ? fakeip : ip) + ":" + Bukkit.getServer().getPort());
			buffReader.close();
			if (key == null){
				if(sender instanceof Player){
					sender.sendMessage("§cFEventos §7Nao foi possivel verificar sua key!");
					sender.sendMessage("§cFEventos §7Desativando plugin...");
				}else{
					sender.sendMessage(" §4Nao foi possivel verificar sua key!");
				}
				Bukkit.getServer().getPluginManager().disablePlugin(instance);
				return false;
			}
			if(!key.contains("true")){
				if(sender instanceof Player)
					sender.sendMessage("§cFEventos §7" + key);
				else
					sender.sendMessage(" §4" + key);
				Bukkit.getServer().getPluginManager().disablePlugin(instance);
				return false;
			}
			if(Double.parseDouble(key.split(" ")[1]) > Double.parseDouble(instance.getDescription().getVersion())){
				if(sender instanceof Player){
					sender.sendMessage("§6FEventos §7Novo update encontrado!");
					sender.sendMessage("§6FEventos §7Me chame no skype!");
				}else{
					sender.sendMessage(" §6Novo update encontrado!");
					sender.sendMessage(" §6Me chame no skype!");
				}
			}
		} catch (Exception e2) {
			if(!(sender instanceof Player)) sender.sendMessage(" §3Seu IP: §b" + 
					(ip.equals("") ? fakeip : ip) + ":" + Bukkit.getServer().getPort());
			if(sender instanceof Player)
				sender.sendMessage("§cFEventos §7Ocorreu um erro ao verificar sua key!");
			else
				sender.sendMessage(" §4Ocorreu um erro ao verificar sua key!");
			Bukkit.getServer().getPluginManager().disablePlugin(instance);
			return false;
		}
		return true;
	}

}

package me.zfork.fterrenos;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class AutoUpdate {

	private FTerrenos instance = FTerrenos.getFTerrenos();
	private String urlVersionCheck;
	private String urlDownload;
	private String currentVersion;

	public AutoUpdate(String UrlVersion, String UrlDownload){
		this.urlVersionCheck = UrlVersion;
		this.urlDownload = UrlDownload;
		this.currentVersion = instance.getDescription().getVersion();
		start();
	}

	private void start(){
		String v = null;
		try {
			v = getText(urlVersionCheck);
		} catch (Exception e){
			instance.getServer().getConsoleSender().sendMessage(" §4Ocorreu uma falha ao tentar verificar se existem atualizacoes!");
		}
		if(Double.parseDouble(v) > Double.parseDouble(currentVersion)){
			instance.getServer().getConsoleSender().sendMessage(" §bBaixando nova atualizacao (V" + v + ")!");
			startDownload();
		}else{
			instance.getServer().getConsoleSender().sendMessage(" §3Nao existem novas atualizacoes!");
		}
	}

	private boolean startDownload(){
		try {
			URL download = new URL(this.urlDownload);
			BufferedInputStream in = null;
			FileOutputStream fout = null;
			try {
				File f = new File("plugins" + System.getProperty("file.separator") + "FTerrenos" + System.getProperty("file.separator") + "update");
				File f2 = new File("plugins" + System.getProperty("file.separator") + "FTerrenos" + System.getProperty("file.separator") + "update" + System.getProperty("file.separator") + "FTerrenos.rar");
				if(!f.exists()) f.mkdir();
				if(!f2.exists()) f2.createNewFile();
				in = new BufferedInputStream(download.openStream());
				fout = new FileOutputStream("plugins" + System.getProperty("file.separator") + "FTerrenos" + System.getProperty("file.separator") + "update" + System.getProperty("file.separator") + "FTerrenos.rar");

				final byte data[] = new byte[1024];
				int count;
				while ((count = in.read(data, 0, 1024)) != -1){
					fout.write(data, 0, count);
				}
			}catch (Exception e){
				instance.getServer().getConsoleSender().sendMessage(" §4Ocorreu uma falha ao tentar baixar a atualizacao!");
				e.printStackTrace();
				return false;
			} finally {
				if(in != null){
					in.close();
				}
				if(fout != null){
					fout.close();
				}
			}
			instance.getServer().getConsoleSender().sendMessage(" §3Download concluido!");
			return true;
		} catch (IOException e){
			instance.getServer().getConsoleSender().sendMessage(" §4Ocorreu uma falha ao tentar verificar se existem atualizacoes!");
			return false;
		}
	}

	private String getText(String url) throws Exception {
		URL website = new URL(url);
		URLConnection connection = website.openConnection();
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder response = new StringBuilder();
		String inputLine;
		while ((inputLine = in.readLine()) != null){
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}

}

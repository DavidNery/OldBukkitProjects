package me.zfork.spartanoscraft.spartanosbitcoins.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import me.zfork.spartanoscraft.spartanosbitcoins.SpartanosBitCoins;

public class FileLogger {

	private File f;

	private final SimpleDateFormat sdf;

	public FileLogger(SpartanosBitCoins instance) {
		this.sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		sdf.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
		f = new File(instance.getDataFolder(), "logs.yml");
		try {
			if(!f.exists()) f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void logToFile(String log) {
		Date d = new Date();
		FileWriter fw;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(f, true);
			pw = new PrintWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw.println("[" + sdf.format(d) + "] " + log);
		pw.flush();
		pw.close();
	}

}

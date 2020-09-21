package me.zfork.fpin;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PinEmail {

	private String para;
	private Player p;
	private Plugin plugin;

	public PinEmail(Plugin plugin, String para, Player p){
		this.para = para;
		this.p = p;
		this.plugin = plugin;
	}

	public void send(){
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
				HtmlEmail email = new HtmlEmail();
				email.setCharset("utf-8");
				email.setHostName(plugin.getConfig().getString("Config.SMTP"));
				email.setSmtpPort(plugin.getConfig().getInt("Config.SMTP_Port"));
				email.setAuthenticator(new DefaultAuthenticator(plugin.getConfig().getString("Config.Email"), plugin.getConfig().getString("Config.Senha")));
				if(email.getHostName().equalsIgnoreCase("smtp.live.com"))
					email.setStartTLSEnabled(true);
				else
					email.setSSLOnConnect(true);
				try {
					email.setFrom(plugin.getConfig().getString("Config.Email"), plugin.getConfig().getString("Config.Usuario"));
					email.setSubject(plugin.getConfig().getString("Config.Assunto"));
					FPin.getFPin().getSQlite().openConnection();
					StringBuilder mensagem = new StringBuilder();
					mensagem.append("<html><body>");
					for(String linhas : plugin.getConfig().getStringList("Config.Conteudo_Email_Enviado")){
						mensagem.append(linhas.replace("<data/>", FPin.getFPin().getTimeFormatted()).replace("<ip/>", p.getAddress().getAddress().getHostAddress()).replace("<pin/>", FPin.getFPin().getSQlite().getPin(p.getName().toLowerCase())).replace("<player/>", p.getName()) + "\n");
					}
					mensagem.append("</body></html>");
					FPin.getFPin().getSQlite().closeConnection();
					email.setHtmlMsg(mensagem.toString());
					email.addTo(para, p.getName());
					email.send();
					p.sendMessage(plugin.getConfig().getString("Mensagem.Sucesso.Email_Enviado").replace("&", "§").replace("@email", para));
					p.playSound(p.getLocation(), Sound.LEVEL_UP, 10.0F, 1.0F);
				} catch (ClassNotFoundException | SQLException | EmailException e) {
					FPin.getFPin().printError("==================== | " + p.getName() + " | Email | " + FPin.getFPin().getTimeFormatted() + " | ====================");
					StringWriter ste = new StringWriter();
					e.printStackTrace(new PrintWriter(ste));
					FPin.getFPin().printError(ste.toString());
					FPin.getFPin().printError("==================== | " + p.getName() + " | Email | " + FPin.getFPin().getTimeFormatted() + " | ====================\n");
					p.sendMessage("§6§lFPIN §cNao foi possivel enviar o email!");
					p.playSound(p.getLocation(), Sound.NOTE_BASS_GUITAR, 10.0F, 1.0F);
				}
			}
		});
	}

}
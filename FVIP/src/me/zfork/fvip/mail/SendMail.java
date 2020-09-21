package me.zfork.fvip.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import me.zfork.fvip.FVip;

import org.bukkit.entity.Player;

public class SendMail extends Thread{

	private FVip instance;
	private Player p;
	private String mailSMTPServer;
	private String mailSMTPServerPort;
	private String from;
	private String fromName;
	private String senha;
	private String to;
	private String toName;
	private String assunto;
	private String mensagem;

	public SendMail(FVip instance, Player p, String mailSMTPServer, String mailSMTPServerPort, 
			String from, String fromName, String senha, 
			String to, String toName, 
			String assunto, String mensagem) {
		this.instance = instance;
		this.p = p;
		this.mailSMTPServer = mailSMTPServer;
		this.mailSMTPServerPort = mailSMTPServerPort;
		this.from = from;
		this.fromName = fromName;
		this.senha = senha;
		this.to = to;
		this.toName = toName;
		this.assunto = assunto;
		this.mensagem = mensagem;
	}

	@Override
	public void run(){
		Thread.currentThread().setContextClassLoader(getContextClassLoader());
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", mailSMTPServer);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.user", from);
		props.put("mail.smtp.port", mailSMTPServerPort);
		if(!mailSMTPServerPort.startsWith("587")){
			props.put("mail.smtp.socketFactory.port", mailSMTPServerPort);
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.put("mail.smtp.starttls.enable","true");
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, senha);
			}
		});
		Message msg = new MimeMessage(session);
		try {
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to, toName));
			msg.setFrom(new InternetAddress(from, fromName));
			msg.setSubject(assunto);
			msg.setContent(mensagem, "text/html; charset=utf-8");
		} catch (Exception e) {
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Email_Nao_Enviado").replace("&", "§"));
			instance.getServer().getConsoleSender().sendMessage("§4");
			instance.getServer().getConsoleSender().sendMessage("§4Não foi possível setar a mensagem.");
			e.printStackTrace();
			instance.getServer().getConsoleSender().sendMessage("§4Não foi possível setar a mensagem.");
			instance.getServer().getConsoleSender().sendMessage("§4");
		}
		Transport tr;
		try {
			tr = session.getTransport("smtp");
			tr.connect(mailSMTPServer, from, senha);
			msg.saveChanges();
			tr.sendMessage(msg, msg.getAllRecipients());
			tr.close();
			p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Email_Enviado").replace("&", "§"));
		} catch (Exception e) {
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Email_Nao_Enviado").replace("&", "§"));
			instance.getServer().getConsoleSender().sendMessage("§4");
			instance.getServer().getConsoleSender().sendMessage("§4Não foi possível enviar o email!");
			e.printStackTrace();
			instance.getServer().getConsoleSender().sendMessage("§4Não foi possível enviar o email!");
			instance.getServer().getConsoleSender().sendMessage("§4");
		}
	}
}
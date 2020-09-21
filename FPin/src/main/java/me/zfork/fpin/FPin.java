package me.zfork.fpin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.xephi.authme.api.NewAPI;

public class FPin extends JavaPlugin implements CommandExecutor{

	private SQLite sqlite;
	private Random r = new Random();
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private Calendar c = new GregorianCalendar(TimeZone.getTimeZone("America/Sao_Paulo"));

	public void onEnable(){
		getServer().getConsoleSender().sendMessage("§3==========[§bFPin§3]==========");
		getServer().getConsoleSender().sendMessage(" §3Status: §bAtivado");
		getServer().getConsoleSender().sendMessage(" §3By: §bzFork");
		getServer().getConsoleSender().sendMessage(" §3Versao: §b" + getDescription().getVersion());
		if(!new File(getDataFolder(), "config.yml").exists()){
			saveDefaultConfig();
			getServer().getConsoleSender().sendMessage(" §3Config: §bCriada");
		}else{
			getServer().getConsoleSender().sendMessage(" §3Config: §bJa Existente");
		}
		if(Bukkit.getServer().getPluginManager().getPlugin("AuthMe") == null){
			getServer().getConsoleSender().sendMessage(" §3AuthMe: §bNao Encontrado");
			getServer().getConsoleSender().sendMessage("§3==========[§bFPin§3]==========");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}else{
			getServer().getConsoleSender().sendMessage(" §3AuthMe: §bTudo OK!");
		}
		File f = new File(getDataFolder(), "pins.db");
		if(!f.exists()){
			try{
				f.createNewFile();
			}catch(IOException e){
				getServer().getConsoleSender().sendMessage(" §4Nao foi possivel criar o arquivo \"pins.db\"!");
				getServer().getConsoleSender().sendMessage("§3==========[§bFPin§3]==========");
				Bukkit.getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}
		try {
			sqlite = new SQLite();
		} catch (ClassNotFoundException | SQLException e) {
			getServer().getConsoleSender().sendMessage(" §4Nao foi possivel acessar o arquivo \"pins.db\"!");
			getServer().getConsoleSender().sendMessage("§3==========[§bFPin§3]==========");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		getServer().getConsoleSender().sendMessage("§3==========[§bFPin§3]==========");
	}

	public void onDisable(){
		try {
			sqlite.closeConnection();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		getServer().getConsoleSender().sendMessage("§4==========[§cFPin§4]==========");
		getServer().getConsoleSender().sendMessage(" §4Status: §cDesativado");
		getServer().getConsoleSender().sendMessage(" §4By: §czFork");
		getServer().getConsoleSender().sendMessage(" §4Versao: §c" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§4==========[§cFPin§4]==========");
	}

	public static FPin getFPin(){
		return (FPin) Bukkit.getServer().getPluginManager().getPlugin("FPin");
	}

	public SQLite getSQlite(){
		return this.sqlite;
	}
	
	public String getTimeFormatted(){
		return sdf.format(c.getTime());
	}

	public void printError(String mensagem){
		try{
			File saveTo = new File(getDataFolder(), "erros.txt");
			if (!saveTo.exists()){
				saveTo.createNewFile();
			}
			FileWriter fw = new FileWriter(saveTo, true);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(mensagem);
			pw.flush();
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public boolean isEmail(String email){
		Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		Matcher matcher = pattern.matcher(email);
		if(matcher.matches() == true) return true;
		return false;
	}

	public boolean isDate(String data){
		if(data.replaceAll("\\d{2}/\\d{2}/\\d{4}", "").equals("")) return true;
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("pin")){
			if(!(sender instanceof Player)) return true;
			try{
				Player p = (Player) sender;
				if(args.length == 0){
					p.sendMessage(getConfig().getString("Mensagem.Erro.Poucos_Argumentos").replace("&", "§"));
					return true;
				}else if(args[0].equalsIgnoreCase("recuperar")){
					if(args.length <= 2){
						p.sendMessage(getConfig().getString("Mensagem.Erro.Poucos_Argumentos_Recuperar").replace("&", "§"));
						return true;
					}
					sqlite.openConnection();
					if(!sqlite.hasPlayer(p.getName().toLowerCase())){
						p.sendMessage(getConfig().getString("Mensagem.Erro.Nao_Tem_Pin").replace("&", "§"));
						sqlite.closeConnection();
						return true;
					}
					if(!sqlite.getEmail(p.getName().toLowerCase()).equalsIgnoreCase(args[1])){
						p.sendMessage(getConfig().getString("Mensagem.Erro.Email_Incorreto").replace("&", "§"));
						sqlite.closeConnection();
						return true;
					}
					if(!sqlite.getData(p.getName().toLowerCase()).equalsIgnoreCase(args[2])){
						p.sendMessage(getConfig().getString("Mensagem.Erro.Data_Incorreta").replace("&", "§"));
						sqlite.closeConnection();
						return true;
					}
					if(args.length == 3){
						if(!isEmail(args[1])){
							p.sendMessage(getConfig().getString("Mensagem.Erro.Nao_E_Email").replace("&", "§").replace("@fakeemail", args[1]));
							sqlite.closeConnection();
							return true;
						}
						sqlite.closeConnection();
						p.sendMessage(getConfig().getString("Mensagem.Sucesso.Enviando_Email").replace("&", "§").replace("@email", args[1]));
						PinEmail email = new PinEmail(this, args[1], p);
						email.send();
						return true;
					}
					if(!sqlite.getPin(p.getName().toLowerCase()).equals(args[3])){
						p.sendMessage(getConfig().getString("Mensagem.Erro.Pin_Incorreto").replace("&", "§"));
						sqlite.closeConnection();
						return true;
					}
					String csenha = getConfig().getString("Config.Caracteres_Senha");
					String senha = "";
					for(int i = 0; i<getConfig().getInt("Config.Tamanho_Senha"); i++){
						senha += csenha.charAt(r.nextInt(csenha.length()));
					}
					sqlite.delPlayer(p.getName().toLowerCase());
					sqlite.closeConnection();
					Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), getConfig().getString("Config.Comando_Executado").replace("@senha", senha).replace("@player", p.getName()));
					StringBuilder mensagem = new StringBuilder();
					for(String msg : getConfig().getStringList("Mensagem.Sucesso.Senha_Aterada")){
						mensagem.append(msg.replace("&", "§").replace("@senha", senha).replace("@player", p.getName()) + "\n");
					}
					p.kickPlayer(mensagem.toString());
					return true;
				}else if(args[0].equalsIgnoreCase("ativar")){
					if(!NewAPI.getInstance().isAuthenticated(p)){
						p.sendMessage(getConfig().getString("Mensagem.Erro.Nao_Esta_Logado").replace("&", "§"));
						return true;
					}else if(args.length <= 2){
						p.sendMessage(getConfig().getString("Mensagem.Erro.Poucos_Argumentos_Ativar").replace("&", "§"));
						return true;
					}
					sqlite.openConnection();
					if(sqlite.hasPlayer(p.getName().toLowerCase())){
						p.sendMessage(getConfig().getString("Mensagem.Erro.Tem_Pin").replace("&", "§"));
						sqlite.closeConnection();
						return true;
					}else if(!isEmail(args[1])){
						p.sendMessage(getConfig().getString("Mensagem.Erro.Nao_E_Email").replace("&", "§").replace("@fakeemail", args[1]));
						sqlite.closeConnection();
						return true;
					}else if(!isDate(args[2])){
						p.sendMessage(getConfig().getString("Mensagem.Erro.Nao_E_Data").replace("&", "§").replace("@fakedata", args[2]));
						sqlite.closeConnection();
						return true;
					}
					String cpin = getConfig().getString("Config.Caracteres_PIN");
					String pin = "";
					for(int i = 0; i<getConfig().getInt("Config.Tamanho_PIN"); i++){
						pin += cpin.charAt(r.nextInt(cpin.length()));
					}
					sqlite.addNew(p.getName().toLowerCase(), pin, args[1], args[2]);
					sqlite.closeConnection();
					StringBuilder mensagem = new StringBuilder();
					for(String msg : getConfig().getStringList("Mensagem.Sucesso.PIN_Ativado")){
						mensagem.append(msg.replace("&", "§").replace("@data", args[2]).replace("@email", args[1]).replace("@pin", pin).replace("@player", p.getName()) + "\n");
					}
					p.kickPlayer(mensagem.toString());
				}else if(args[0].equalsIgnoreCase("remover")){
					if(!p.hasPermission("fpin.remover")){
						p.sendMessage(getConfig().getString("Mensagem.Erro.Sem_Permissao").replace("&", "§"));
						return true;
					}
					sqlite.openConnection();
					if(args.length <= 1){
						p.sendMessage(getConfig().getString("Mensagem.Erro.Poucos_Argumentos_Remover").replace("&", "§"));
						sqlite.closeConnection();
						return true;
					}else if(!sqlite.hasPlayer(args[1].toLowerCase())){
						p.sendMessage(getConfig().getString("Mensagem.Erro.Nao_Tem_Pin").replace("&", "§"));
						sqlite.closeConnection();
						return true;
					}
					sqlite.delPlayer(args[1].toLowerCase());
					p.sendMessage(getConfig().getString("Mensagem.Sucesso.Removeu_PIN").replace("&", "§").replace("@player", Bukkit.getServer().getPlayer(args[1]) != null ? Bukkit.getServer().getPlayer(args[1]).getName() : args[1]));
				}
			}catch(Exception e){
				StackTraceElement[] erro = e.getStackTrace();
				sender.sendMessage("§6§lFPin §cOcorreu o erro §f" + erro[0].getClassName().substring(erro[0].getClassName()
						.lastIndexOf(".") + 1).toString() + " §cna classe §7" + erro[3].getClassName() + " §cno método §7" + 
						erro[3].getMethodName() + " §ce na linha §f" + erro[3].getLineNumber() + "§c!");
				printError("==================== " + sender.getName() + " | " + cmd.getName() + " | " + FPin.getFPin().getTimeFormatted() + " ====================");
				StringWriter ste = new StringWriter();
				e.printStackTrace(new PrintWriter(ste));
				printError(ste.toString());
				printError("==================== " + sender.getName() + " | " + cmd.getName() + " | " + FPin.getFPin().getTimeFormatted() + " ====================");
			}
		}
		return false;
	}

}

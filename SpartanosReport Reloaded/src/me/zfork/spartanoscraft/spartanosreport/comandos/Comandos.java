package me.zfork.spartanoscraft.spartanosreport.comandos;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.zfork.spartanoscraft.spartanosreport.SpartanosReport;
import me.zfork.spartanoscraft.spartanosreport.utils.JSONMessage;
import me.zfork.spartanoscraft.spartanosreport.utils.JSONMessage.ClickAction;
import me.zfork.spartanoscraft.spartanosreport.utils.JSONMessage.HoverAction;
import me.zfork.spartanoscraft.spartanosreport.utils.PlayerReports;
import me.zfork.spartanoscraft.spartanosreport.utils.PlayerReportsManager;

import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Comandos implements CommandExecutor, TabCompleter {
	
	private final SpartanosReport instance;
	private final PlayerReportsManager playerReportsManager;
	private final HashMap<String, Long> delay;
	
	public Comandos(SpartanosReport instance) {
		this.instance = SpartanosReport.getSpartanosReport();
		this.playerReportsManager = instance.getPlayerReportsManager();
		this.delay = new HashMap<>();
		
		instance.getCommand("reportar").setExecutor(this);
		instance.getCommand("reports").setExecutor(this);
		instance.getCommand("creports").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player){
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("reportar")){
				if(args.length <= 1){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Uso_Correto_Reportar").replace("&", "§"));
					return true;
				}else if(args[0].equalsIgnoreCase(p.getName())){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Si_Mesmo").replace("&", "§"));
					return true;
				}else if(delay.containsKey(p.getName().toLowerCase())){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Aguarde_Reportar").replace("&", "§")
							.replace("{tempo}", ""+((delay.get(p.getName().toLowerCase())-System.currentTimeMillis())/1000)));
					return true;
				}
				OfflinePlayer of = null;
				for(OfflinePlayer off : instance.getServer().getOfflinePlayers()){
					if(off.getName().equalsIgnoreCase(args[0])){
						of = off;
						break;
					}
				}
				if(of == null){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Nao_Existe").replace("&", "§"));
					return true;
				}
				String report = null;
				for(String r : instance.getConfig().getConfigurationSection("Config.Reports_Validos").getKeys(false)){
					if(r.equalsIgnoreCase(args[1])){
						report = r;
						break;
					}
				}
				if(report == null){
					JSONMessage jsonmessage = new JSONMessage();
					List<String> msg = instance.getConfig().getStringList("Mensagem.Erro.Motivo_Valido");
					String estilomotivo = instance.getConfig().getString("Config.Estilo_Motivo");
					String separadormotivo = instance.getConfig().getString("Config.Separador_Motivo");
					for(String s : msg){
						if(s.contains("{motivos}")){
							jsonmessage.addText(s.split("([&|§][a-fA-F0-9k-oK-orR])*?\\{motivos\\}")[0]);
							for(String motivos : instance.getConfig().getConfigurationSection("Config.Reports_Validos").getKeys(false)){
								String hover = "";
								for(String hovermotivo : instance.getConfig().getStringList("Config.Reports_Validos." + motivos + ".Descricao"))
									hover += hovermotivo.replace("&", "§") + "\n";
								hover = hover.substring(0, hover.length()-2);
								jsonmessage.addText(estilomotivo.replace("&", "§").replace("{motivo}", motivos.replace("_", " ")))
									.withHoverAction(HoverAction.SHOW_TEXT, hover)
									.withClickAction(ClickAction.SUGGEST_COMMAND, 
											"/reportar " + (args[0]==null||args[0].equals("") ? "<player>" : args[0]) + " " + motivos);
								jsonmessage.addText(separadormotivo.replace("&", "§"));
							}
							jsonmessage.removeLastElement();
							jsonmessage.addText(s.split("([&|§][a-fA-F0-9k-oK-orR])*?\\{motivos\\}")[1]);
							jsonmessage.sendJson(p);
						}else{
							p.sendMessage(s.replace("&", "§"));
						}
					}
					return true;
				}
				PlayerReports playerReports = playerReportsManager.getPlayerReports(of.getName());
				if(playerReports == null){
					playerReports = new PlayerReports(of.getName(), report.replace("_", " "), p.getName(), instance.getSdf().format(new Date()));
					playerReportsManager.addNewReport(playerReports);
				}else{
					ItemStack item = (ItemStack) instance.criarItem(
							instance.getConfig().getString("Config.Reports_Validos." + report.replace(" ", "_") + ".Report_Item")
							.replace("{player}", playerReports.getPlayer()).replace("{motivo}", report).replace("{motivoqnt}", playerReports.getReports().get(report.replace("_", " "))+"")
							.replace("{lastreporter}", playerReports.getLastReporter()));
					playerReports.setLastReporter(p.getName());
					playerReports.setDate(instance.getSdf().format(new Date()));
					playerReports.setRemove(System.currentTimeMillis()+TimeUnit.DAYS.toMillis(instance.getConfig().getInt("Config.Tempo_Remover")));
					playerReports.setReportsTotal(playerReports.getReportsTotal()+1);
					playerReportsManager.addReport(playerReports, item, report);
				}
				report = report.replace("_", " ");
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Reportou").replace("&", "§")
						.replace("{player}", of.getName()).replace("{motivo}", report));
				JSONMessage staffmsg = new JSONMessage();
				String reporthover = "";
				for(String s : instance.getConfig().getStringList("Config.Staffs.Report_Hover"))
					reporthover += s.replace("&", "§").replace("{player}", p.getName()).replace("{reportado}", of.getName()).replace("{motivo}", report)
							.replace("{reportsqnt}", playerReports.getReportsTotal()+"").replace("{reportsqntmotivo}", playerReports.getReports().get(report)+"")+"\n";
				reporthover = reporthover.substring(0, reporthover.length()-2);
				for(String s : instance.getConfig().getStringList("Mensagem.Sucesso.Reportou_Staff")){
					staffmsg.addText(s.replace("&", "§").replace("{player}", p.getName()).replace("{reportado}", of.getName()).replace("{motivo}", report)
							.replace("{reportsqnt}", playerReports.getReportsTotal()+"").replace("{reportsqntmotivo}", playerReports.getReports().get(report)+""));
					if(!s.replaceAll("\\s+", "").equals("")){
						staffmsg.withHoverAction(HoverAction.SHOW_TEXT, reporthover)
						.withClickAction(ClickAction.RUN_COMMAND, "/reports " + of.getName());
					}
					staffmsg.addText("\n");
				}
				staffmsg = staffmsg.removeLastElement();
				for(Player staffs : instance.getServer().getOnlinePlayers()){
					if(staffs.hasPermission("report.staff")){
						staffmsg.sendJson(staffs);
						staffs.playSound(staffs.getLocation(), Sound.ANVIL_USE, 5.0F, 1.0F);
					}
				}
				delay.put(p.getName().toLowerCase(), System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(instance.getConfig().getInt("Config.Tempo_Reportar")));
				new BukkitRunnable() {
					@Override
					public void run() {
						if(delay.containsKey(p.getName().toLowerCase())) delay.remove(p.getName().toLowerCase());
					}
				}.runTaskLater(instance, instance.getConfig().getInt("Config.Tempo_Reportar")*20);
			}else if(cmd.getName().equalsIgnoreCase("reports")){
				if(!p.hasPermission("report.staff")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao.Reports").replace("&", "§"));
					return true;
				}else if(args.length == 0){
					for(String msg : instance.getConfig().getStringList("Mensagem.Sucesso.Menu_Aberto"))
						p.sendMessage(msg.replace("&", "§").replace("{reportstotal}", playerReportsManager.getReports().size() +""));
					p.openInventory(playerReportsManager.getReportsInventories().get(0));
					return true;
				}
				PlayerReports pr = playerReportsManager.getPlayerReports(args[0]);
				if(pr != null){
					p.openInventory(playerReportsManager.getPlayerReportsInventories().get(pr.getPlayer()).get(0));
					playerReportsManager.getStaffLook().put(p.getName(), pr.getPlayer());
					return true;
				}
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Nao_Reportado").replace("&", "§"));
			}else if(cmd.getName().equalsIgnoreCase("creports")){
				if(!p.hasPermission("report.staff")){
					p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Sem_Permissao.Reports").replace("&", "§"));
					return true;
				}else if(args.length == 0){
					for(String msg : instance.getConfig().getStringList("Mensagem.Sucesso.Menu_Aberto"))
						p.sendMessage(msg.replace("&", "§").replace("{reportstotal}", playerReportsManager.getClosedReports().size() +""));
					p.openInventory(playerReportsManager.getClosedReportsInventories().get(0));
					return true;
				}
				PlayerReports pr = playerReportsManager.getClosedPlayerReports(args[0]);
				if(pr != null){
					p.openInventory(playerReportsManager.getPlayerClosedReportsInventories().get(pr.getPlayer()).get(0));
					return true;
				}
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Player_Nao_Reportado").replace("&", "§"));
			}
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("reportar") && args.length >= 2 && sender instanceof Player){
			Player p = (Player) sender;
			List<String> msg = instance.getConfig().getStringList("Mensagem.Erro.Motivo_Valido");
			String estilomotivo = instance.getConfig().getString("Config.Estilo_Motivo");
			String separadormotivo = instance.getConfig().getString("Config.Separador_Motivo");
			JSONMessage jsonmessage = new JSONMessage();
			for(String s : msg){
				if(s.contains("{motivos}")){
					jsonmessage.addText(s.split("([&|§][a-fA-F0-9k-oK-orR])*?\\{motivos\\}")[0]);
					for(String motivos : instance.getConfig().getConfigurationSection("Config.Reports_Validos").getKeys(false)){
						String hover = "";
						for(String hovermotivo : instance.getConfig().getStringList("Config.Reports_Validos." + motivos + ".Descricao"))
							hover += hovermotivo.replace("&", "§") + "\n";
						hover = hover.substring(0, hover.length()-2);
						jsonmessage.addText(estilomotivo.replace("&", "§").replace("{motivo}", motivos.replace("_", " ")))
							.withHoverAction(HoverAction.SHOW_TEXT, hover)
							.withClickAction(ClickAction.SUGGEST_COMMAND, 
									"/reportar " + (args[0]==null||args[0].equals("") ? "<player>" : args[0]) + " " + motivos);
						jsonmessage.addText(separadormotivo.replace("&", "§"));
					}
					jsonmessage.removeLastElement();
					jsonmessage.addText(s.split("([&|§][a-fA-F0-9k-oK-orR])*?\\{motivos\\}")[1]);
					jsonmessage.sendJson(p);
				}else{
					p.sendMessage(s.replace("&", "§"));
				}
			}
		}
		return Arrays.asList("");
	}

}

package me.zfork.fvip;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.zfork.fvip.api.PlayerActiveVipEvent;
import me.zfork.fvip.api.PlayerActiveVipEvent.ActivationType;
import me.zfork.fvip.builders.TitleBuilder;
import me.zfork.fvip.mail.SendMail;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mercadopago.MP;

public class MercadoPago extends Thread{
	
	private FVip instance;
	private String codigo = "";
	private Player p;
	private SQLite sqlite;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	public MercadoPago(FVip instance, String codigo, Player p){
		this.instance = instance;
		this.sqlite = instance.getSQLite();
		this.codigo = codigo.toUpperCase();
		this.p = p;
	}
	
	@Override
	public void run(){
		MP mp = null;
		JSONObject payment_info = null;
		try{
			mp = new MP(instance.getConfig().getString("Config.MercadoPago.Client_ID"), instance.getConfig().getString("Config.MercadoPago.Client_SECRET"));
			payment_info = mp.getPayment(codigo);
		}catch(Exception e){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "§"));
			return;
		}
		if(payment_info == null){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Codigo_Invalido").replace("&", "§").replace("{codigo}", codigo));
			return;
		}
		try{
			if(payment_info.getJSONObject("response").getJSONObject("collection").get("date_approved").equals(null)){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Codigo_Nao_Pago").replace("&", "§").replace("{codigo}", codigo));
				return;
			}
		}catch(JSONException e){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "§"));
			return;
		}
		String grupo = "";
		int dias = 1;
		try {
			String info = String.valueOf(payment_info.getJSONObject("response").getJSONObject("collection").get("order_id"));
			if(info.equals(null) || (!info.matches("^([a-zA-Z0-9\\+]+,\\d+)$"))){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Eh_Pagamento_VIP").replace("&", "§").replace("{codigo}", codigo));
				return;
			}
			grupo = info.split(",")[0];
			dias = Integer.parseInt(info.split(",")[1]);
			if(!instance.getConfig().getStringList("Config.Grupos_VIPs").contains(grupo)){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Produto_Invalido").replace("&", "§").replace("{codigo}", codigo));
				return;
			}else if(dias < 0){
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Produto_Invalido").replace("&", "§").replace("{codigo}", codigo));
				return;
			}
		} catch (JSONException e){
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "§"));
			return;
		}
		try{
			sqlite.openConnection();
			boolean hasPlayerGroup = sqlite.hasPlayerGrupo(p.getName(), grupo);
			String tempo = "";
			if(hasPlayerGroup)
				tempo = (TimeUnit.DAYS.toMillis(dias) + sqlite.getDias(p.getName(), grupo)) + "";
			else
				tempo = (System.currentTimeMillis()+TimeUnit.DAYS.toMillis(dias)) + "";
			PlayerActiveVipEvent event = new PlayerActiveVipEvent(p, ActivationType.MERCADOPAGO, codigo, grupo, new Date(Long.parseLong(tempo)));
			instance.getServer().getPluginManager().callEvent(event);
			if(!event.isCancelled()){
				//sqlite.addNewCodigo(codigo, System.currentTimeMillis() + "", p.getName());
				//sqlite.addNew(p.getName(), (System.currentTimeMillis()+TimeUnit.DAYS.toMillis(dias)) + "", grupo);
				if(hasPlayerGroup){
					sqlite.renew(p.getName(), tempo, grupo);
				}else{
					sqlite.addNew(p.getName(), tempo, grupo);
					if(sqlite.getPlayerGroupUsando(p.getName()) == null)
						sqlite.setUsando(p.getName(), grupo, true);
				}
				sqlite.closeConnection();
				for(String s : instance.getConfig().getStringList("Config.Comandos_Executados"))
					instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), s.replace("{player}", p.getName()).replace("{grupo}", grupo));
				for(String items : instance.getConfig().getStringList("Config.Items")){
					String[] grupos = items.split(" ")[0].split("grupo:")[1].split(" ")[0].split(",");
					for(String gr : grupos){
						if(gr.equalsIgnoreCase(grupo)){
							if(items.contains("xp:")){
								p.giveExpLevels(Integer.parseInt(items.split("xp:")[1]));
							}else if(items.contains("cmd:")){
								instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), items.split("cmd:")[1].replace("{player}", p.getName()));
							}else{
								ItemStack item = (ItemStack) instance.criarItem(items);
								if(p.getInventory().firstEmpty() != -1){
									p.getInventory().addItem(item == null ? new ItemStack(Material.AIR) : item);
								}else{
									p.getWorld().dropItemNaturally(p.getLocation(), item == null ? new ItemStack(Material.AIR) : item);
								}
							}
							break;
						}
					}
				}
				for(String msg : instance.getConfig().getStringList("Mensagem.Sucesso.Ativou_VIP")){
					for(Player on : instance.getServer().getOnlinePlayers()){
						on.sendMessage(msg.replace("&", "§").replace("{player}", p.getName()).replace("{grupo}", grupo)
								.replace("{tempo}", sdf.format(new Date(System.currentTimeMillis()+TimeUnit.DAYS.toMillis(dias)))));
					}
				}
				if(instance.getServer().getClass().getPackage().getName().split("\\.")[3].contains("v1_8_R3")){
					for(String grps : instance.getConfig().getConfigurationSection("Config.Grupos_VIPs").getKeys(false)){
						if(grps.equalsIgnoreCase(grupo)){
							if(instance.getConfig().getBoolean("Config.Grupos_VIPs." + grps + ".Ativar_VIP.Ativar_Title")){
								TitleBuilder.sendTitle(instance.getConfig().getInt("Config.Grupos_VIPs." + grps + ".Ativar_VIP.FadeIn")*20, 
										instance.getConfig().getInt("Config.Grupos_VIPs." + grps + ".Ativar_VIP.Stay")*20, instance.getConfig().getInt("Config.Grupos_VIPs." + grps + ".Ativar_VIP.FadeOut")*20,
										instance.getConfig().getString("Config.Grupos_VIPs." + grps + ".Ativar_VIP.Title").replace("{player}", p.getName()).replace("{vip}", grupo)
										.replace("{tempo}", sdf.format(new Date(Long.parseLong(tempo)))), null, instance.getServer().getOnlinePlayers());
							}
							if(instance.getConfig().getBoolean("Config.Grupos_VIPs." + grps + ".Ativar_VIP.Ativar_Subtitle")){
								TitleBuilder.sendTitle(instance.getConfig().getInt("Config.Grupos_VIPs." + grps + ".Ativar_VIP.FadeIn")*20, 
										instance.getConfig().getInt("Config.Grupos_VIPs." + grps + ".Ativar_VIP.Stay")*20, instance.getConfig().getInt("Config.Grupos_VIPs." + grps + ".Ativar_VIP.FadeOut")*20,
										null, instance.getConfig().getString("Config.Grupos_VIPs." + grps + ".Ativar_VIP.SubTitle").replace("{player}", p.getName()).replace("{vip}", grupo)
										.replace("{tempo}", sdf.format(new Date(Long.parseLong(tempo)))), instance.getServer().getOnlinePlayers());
							}
							break;
						}
					}
				}
				if(instance.getCabecas().contains("Cabecas") && instance.getConfig().getBoolean("Config.Mural_Cabecas.MercadoPago")){
					List<String> cabecas = instance.getCabecas().getStringList("Cabecas");
					final String time = tempo;
					final String group = grupo;
					new BukkitRunnable() {
						@Override
						public void run() {
							for(int i = cabecas.size()-1; i>=0; i--){
								Location loc = deserializeLocation(cabecas.get(i));
								if(loc != null){
									if(i == 0){
										String[] partes = cabecas.get(0).split(" ");
										loc.getBlock().setType(Material.SKULL);
										BlockFace face = BlockFace.valueOf(partes[4]);
										if((!loc.getBlock().getRelative(BlockFace.valueOf(cabecas.get(0).split(" ")[4]).getOppositeFace()).getLocation().getBlock().isLiquid())
												&& (!loc.getBlock().getRelative(BlockFace.valueOf(cabecas.get(0).split(" ")[4]).getOppositeFace()).getLocation().getBlock().isEmpty())){
											switch(face){
											case SOUTH:
												loc.getBlock().setData((byte) 0x3);
												break;
											case WEST:
												loc.getBlock().setData((byte) 0x4);
												break;
											case NORTH:
												loc.getBlock().setData((byte) 0x2);
												break;
											case EAST:
												loc.getBlock().setData((byte) 0x5);
												break;
											default:
												loc.getBlock().setData((byte) 0x0);
												break;
											}
										}else{
											loc.getBlock().setData((byte) 0x1);
										}
										Skull skull = (Skull) loc.getBlock().getState();
										skull.setSkullType(SkullType.PLAYER);
										skull.setOwner(p.getName());
										skull.setRotation(face);
										skull.update();
										//Block relative = skull.getBlock().getRelative(skull.getRotation()).getLocation().subtract(0, 1, 0).getBlock();
										Block relative = new Location(instance.getServer().getWorld(partes[0]), Double.parseDouble(partes[5]), 
												Double.parseDouble(partes[6]), Double.parseDouble(partes[7])).getBlock();
										relative.setType(Material.WALL_SIGN);
										Sign sign = (Sign) relative.getState();
										org.bukkit.material.Sign s = new org.bukkit.material.Sign(Material.WALL_SIGN);
										s.setFacingDirection(skull.getRotation());
										sign.setData(s);
										List<String> placa = instance.getConfig().getStringList("Config.Placa_Cabeca");
										for(int j = 0; j<placa.size(); j++){
											if(j > 3) break;
											sign.setLine(j, placa.get(j).replace("&", "§").replace("{player}", p.getName())
													.replace("{tempo}", new SimpleDateFormat(instance.getConfig().getString("Config.Placa_Cabeca_Tempo_Format")).format(new Date(Long.parseLong(time)))).replace("{vip}", group)
													.replace("{ativado}", new SimpleDateFormat(instance.getConfig().getString("Config.Placa_Cabeca_Ativado_Format")).format(Calendar.getInstance().getTime())));
										}
										sign.update();
									}else{
										if(i-1 >= 0) loc = deserializeLocation(cabecas.get(i-1));
										if(i+1 > cabecas.size() || loc.getBlock().getType() != Material.SKULL) continue;
										Location loc2 = deserializeLocation(cabecas.get(i));
										loc2.getBlock().setType(Material.SKULL);
										BlockFace face = BlockFace.valueOf(cabecas.get(i).split(" ")[4]);
										if((!loc2.getBlock().getRelative(BlockFace.valueOf(cabecas.get(i).split(" ")[4]).getOppositeFace()).getLocation().getBlock().isLiquid())
												&& (!loc2.getBlock().getRelative(BlockFace.valueOf(cabecas.get(i).split(" ")[4]).getOppositeFace()).getLocation().getBlock().isEmpty())){
											switch(face){
											case SOUTH:
												loc2.getBlock().setData((byte) 0x3);
												break;
											case WEST:
												loc2.getBlock().setData((byte) 0x4);
												break;
											case NORTH:
												loc2.getBlock().setData((byte) 0x2);
												break;
											case EAST:
												loc2.getBlock().setData((byte) 0x5);
												break;
											default:
												loc2.getBlock().setData((byte) 0x0);
												break;
											}
										}else{
											loc2.getBlock().setData((byte) 0x1);
										}
										Skull skull = (Skull) loc2.getBlock().getState();
										Skull skull2 = ((Skull) loc.getBlock().getState());
										if(skull2.hasOwner()){
											String[] partes = cabecas.get(i/*+1 >= cabecas.size() ? i : i+1*/).split(" ");
											String[] partes2 = cabecas.get(i-1).split(" ");
											skull.setSkullType(SkullType.PLAYER);
											skull.setOwner(skull2.getOwner());
											skull.setRotation(BlockFace.valueOf(partes[4]));
											skull.update();
											//Block relative = skull.getBlock().getRelative(skull.getRotation()).getLocation().subtract(0, 1, 0).getBlock();
											Block relative = new Location(instance.getServer().getWorld(partes[0]), Double.parseDouble(partes[5]), 
													Double.parseDouble(partes[6]), Double.parseDouble(partes[7])).getBlock();
											relative.setType(Material.WALL_SIGN);
											Sign sign = (Sign) relative.getState();
											Sign sign2 = (Sign) new Location(instance.getServer().getWorld(partes2[0]), Double.parseDouble(partes2[5]), 
													Double.parseDouble(partes2[6]), Double.parseDouble(partes2[7])).getBlock().getState();
											org.bukkit.material.Sign s = new org.bukkit.material.Sign(Material.WALL_SIGN);
											s.setFacingDirection(skull.getRotation());
											sign.setData(s);
											for(int j = 0; j<4; j++){
												sign.setLine(j, sign2.getLine(j));
											}
											sign.update();
										}
									}
								}
							}
						}
					}.runTask(instance);
				}
				p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.VIP_Ativado").replace("&", "§").replace("{vip}", grupo)
						.replace("{tempo}", new SimpleDateFormat("dd/MM/yyyy").format(new Date(Long.parseLong(tempo)))));
				if(instance.getConfig().getBoolean("Config.Mail.Ativar")){
					try {
						p.sendMessage(instance.getConfig().getString("Mensagem.Sucesso.Enviando_Email").replace("&", "§"));
						JSONObject payer = payment_info.getJSONObject("response").getJSONObject("collection").getJSONObject("payer");
						String nome = payer.getString("first_name") + " " + payer.getString("last_name");
						new SendMail(instance, p, 
								instance.getMailSMTPServer(), instance.getMailSMTPServerPort(), 
								instance.getFrom(), instance.getFromName(), instance.getSenha(), 
								payer.getString("email"), nome, 
								instance.getAssunto().replace("{player}", p.getName()).replace("{vip}", grupo).replace("{comprador}", nome), 
								instance.getMensagem().replace("{player}", p.getName()).replace("{vip}", grupo)
								.replace("{tempo}", new SimpleDateFormat("dd/MM/yyyy").format(new Date(Long.parseLong(tempo))))
									.replace("{ativado}", sdf.format(Calendar.getInstance().getTime()))
									.replace("{comprador}", nome)
									.replace("{codigo}", codigo)).start();
					} catch (JSONException e) {
						e.printStackTrace();
						p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Email_Nao_Enviado").replace("&", "§"));
					}
				}
			}else{
				p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Evento_Cancelado_Plugin_Externo").replace("&", "§"));
			}
		} catch (ClassNotFoundException | SQLException e) {
			p.sendMessage(instance.getConfig().getString("Mensagem.Erro.Nao_Foi_Possivel_Fazer_Acao").replace("&", "§"));
		}
	}
	
	public Location deserializeLocation(String loc){
		String[] partes = loc.split(" ");
		if((!loc.contains(" ")) || partes.length != 8 || instance.getServer().getWorld(partes[0]) == null) return null;
		try{
			return new Location(instance.getServer().getWorld(partes[0]), Double.parseDouble(partes[1]), Double.parseDouble(partes[2]), Double.parseDouble(partes[3]));
		}catch(Exception e){return null;}
	}

}

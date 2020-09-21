package me.zfork.fvip;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import me.zfork.fvip.builders.JSONMessage;
import me.zfork.fvip.builders.JSONMessage.ClickAction;
import me.zfork.fvip.builders.JSONMessage.HoverAction;

import org.bukkit.entity.Player;

public class SQLite {

	private FVip instance;
	private Connection connection;
	private PreparedStatement stmt;
	private Map<String, String> players;

	public SQLite(FVip instance) throws SQLException, ClassNotFoundException {
		this.instance = instance;
		this.players = new HashMap<String, String>();
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + instance.getDataFolder().getAbsolutePath() + File.separator + "vips.db");
		stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS vips (player VARCHAR(255), tempo VARCHAR(255), vip VARCHAR(255), usando BOOLEAN, desbanir INTEGER)");
		stmt.executeUpdate();
		stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS keys (key VARCHAR(255), grupo VARCHAR(255), dia VARCHAR(255))");
		stmt.executeUpdate();
		stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS usados (codigo VARCHAR(255), dia VARCHAR(255), player VARCHAR(255))");
		stmt.executeUpdate();
		closeConnection();
	}

	public void openConnection() throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + instance.getDataFolder().getAbsolutePath() + File.separator + "vips.db");
	}

	public void closeConnection() throws ClassNotFoundException, SQLException{
		if(connection != null){
			Class.forName("org.sqlite.JDBC");
			connection.close();
			if(stmt != null) stmt.close();
			connection = null;
			stmt = null;
		}
	}

	public void addNew(final String player, final String tempo, final String vip) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("INSERT INTO vips (player, tempo, vip, usando, desbanir) VALUES (?, ?, ?, 1, 1);");
		stmt.setString(1, player.toLowerCase());
		stmt.setString(2, tempo);
		stmt.setString(3, vip);
		stmt.executeUpdate();
	}
	
	public void addNewKey(final String key, final String grupo, final String dia) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("INSERT INTO keys (key, grupo, dia) VALUES (?, ?, ?);");
		stmt.setString(1, key);
		stmt.setString(2, grupo);
		stmt.setString(3, dia);
		stmt.executeUpdate();
	}
	
	public void addNewCodigo(final String codigo, final String dia, final String player) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("INSERT INTO usados (codigo, dia, player) VALUES (?, ?, ?);");
		stmt.setString(1, codigo.toUpperCase());
		stmt.setString(2, dia);
		stmt.setString(3, player);
		stmt.executeUpdate();
	}
	
	public void delPlayer(final String player) throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("DELETE FROM vips WHERE player=?;");
		stmt.setString(1, player.toLowerCase());
		stmt.executeUpdate();
	}
	
	public void delPlayerGroup(final String player, final String grupo) throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("DELETE FROM vips WHERE player=? AND vip=?;");
		stmt.setString(1, player.toLowerCase());
		stmt.setString(2, grupo);
		stmt.executeUpdate();
	}
	
	public void delKey(final String key, final boolean all) throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		if(key.equals("*") && all){
			stmt = connection.prepareStatement("DELETE FROM keys");
			stmt.executeUpdate();
		}else{
			stmt = connection.prepareStatement("DELETE FROM keys WHERE key=?;");
			stmt.setString(1, key);
			stmt.executeUpdate();
		}
	}
	
	public boolean hasPlayer(final String player) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT * FROM vips");
		final ResultSet rs = stmt.executeQuery();
		while (rs.next()){
			if(rs.getString("player").equalsIgnoreCase(player.toLowerCase())) return true;
		}
		return false;
	}
	
	public boolean hasPlayerGrupo(final String player, final String vip) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT * FROM vips WHERE vip=?;");
		stmt.setString(1, vip);
		final ResultSet rs = stmt.executeQuery();
		while (rs.next()){
			if(rs.getString("player").equalsIgnoreCase(player.toLowerCase())) return true;
		}
		return false;
	}
	
	public boolean hasKey(final String key) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT key FROM keys");
		final ResultSet rs = stmt.executeQuery();
		while (rs.next()){
			if(rs.getString("key").equals(key)) return true;
		}
		return false;
	}
	
	public boolean hasCodigo(final String codigo) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT codigo FROM usados");
		final ResultSet rs = stmt.executeQuery();
		while (rs.next()){
			if(rs.getString("codigo").equals(codigo.toUpperCase())) return true;
		}
		return false;
	}
	
	public long getDias(final String player, final String grupo) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT * FROM vips WHERE player=?");
		stmt.setString(1, player.toLowerCase());
		final ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			if(rs.getString("vip").equalsIgnoreCase(grupo)) return Long.parseLong(rs.getString("tempo"));
		}
		return 0;
	}
	
	public int getDiaKey(final String key) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT * FROM keys WHERE key=?");
		stmt.setString(1, key);
		final ResultSet rs = stmt.executeQuery();
		return Integer.parseInt(rs.getString("dia"));
	}
	
	public long getDiaCodigo(final String codigo) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT * FROM usados WHERE codigo=?");
		stmt.setString(1, codigo);
		final ResultSet rs = stmt.executeQuery();
		return Long.parseLong(rs.getString("dia"));
	}
	
	public String getKeyGrupo(final String key) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT * FROM keys WHERE key=?");
		stmt.setString(1, key);
		final ResultSet rs = stmt.executeQuery();
		return rs.getString("grupo");
	}
	
	public String getKeyTempo(final String key) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT * FROM keys WHERE key=?");
		stmt.setString(1, key);
		final ResultSet rs = stmt.executeQuery();
		return rs.getString("dia");
	}
	
	public String getPlayerCodigo(final String codigo) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT * FROM usados WHERE codigo=?");
		stmt.setString(1, codigo);
		final ResultSet rs = stmt.executeQuery();
		return rs.getString("player");
	}
	
	public String getPlayerGroupUsando(final String player) throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT * FROM vips WHERE player=?");
		stmt.setString(1, player.toLowerCase());
		final ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			if(rs.getBoolean("usando")) return rs.getString("vip");
		}
		return null;
	}
	
	public void setUsando(final String player, final String vip, final boolean usando) throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("UPDATE vips SET usando='" + (usando ? 1 : 0) + "' WHERE player=? AND vip=?;");
		stmt.setString(1, player.toLowerCase());
		stmt.setString(2, vip);
		stmt.executeUpdate();
	}
	
	public void renew(final String player, final String tempo, final String vip) throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		int unbans = getUnbans(player.toLowerCase());
		stmt = connection.prepareStatement("UPDATE vips SET desbanir=?,tempo=? WHERE player=? AND vip=?;");
		stmt.setInt(1, unbans + 1);
		stmt.setString(2, tempo);
		stmt.setString(2, player.toLowerCase());
		stmt.setString(3, vip);
		stmt.executeUpdate();
	}
	
	public void desbanir(final String player, final String vip) throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		int unbans = getUnbans(player.toLowerCase());
		stmt = connection.prepareStatement("UPDATE vips SET desbanir=? WHERE player=? AND vip=?;");
		stmt.setInt(1, unbans - 1);
		stmt.setString(2, player.toLowerCase());
		stmt.setString(3, vip);
		stmt.executeUpdate();
	}
	
	public int getUnbans(final String player) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		String groupusando = getPlayerGroupUsando(player.toLowerCase());
		stmt = connection.prepareStatement("SELECT * FROM vips WHERE player=? AND vip=?");
		stmt.setString(1, player.toLowerCase());
		stmt.setString(2, groupusando);
		final ResultSet rs = stmt.executeQuery();
		while (rs.next()){
			return rs.getInt("desbanir");
		}
		return 0;
	}
	
	public String getKeys() throws ClassNotFoundException, SQLException{
		String keys = "";
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT * FROM keys");
		final ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			keys += rs.getString("key") + ", ";
		}
		return keys.length() > 0 ? keys.substring(0, keys.length()-2) : keys;
	}
	
	public void getKeys1_7(Player p) throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT * FROM keys");
		final ResultSet rs = stmt.executeQuery();
		String hover = "";
		for(String texto : instance.getConfig().getStringList("Config.v1_7")) hover += (texto + "\n");
		hover = hover.substring(0, hover.length()-1);
		String msg = instance.getConfig().getString("Mensagem.Sucesso.Keys").replace("&", "§");
		String separador = instance.getConfig().getString("Config.Separador_Keys").replace("&", "§");
		String formatkey = instance.getConfig().getString("Config.Formato_Keys").replace("&", "§");
		if(msg.contains("{keys}")){
			JSONMessage json = new JSONMessage();
			json.addText(msg.split("([&|§][a-fA-F0-9k-oK-orR])*?\\{keys\\}")[0]);
			int i = 0;
			while(rs.next()){
				String dia = Integer.parseInt(rs.getString("dia")) > 1 ? rs.getString("dia") + " dias" : rs.getString("dia") + " dia";
				String key = rs.getString("key");
				if(i == 0){
					json.addText(formatkey.replace("{key}", key))
						.withHoverAction(HoverAction.SHOW_TEXT, hover.replace("&", "§").replace("{tempo}", dia).replace("{grupo}", rs.getString("grupo")))
						.withClickAction(ClickAction.SUGGEST_COMMAND, "/usarkey " + key);
				}else{
					json.addText(separador).addText(formatkey.replace("{key}", key))
						.withHoverAction(HoverAction.SHOW_TEXT, hover.replace("&", "§").replace("{tempo}", dia).replace("{grupo}", rs.getString("grupo")))
						.withClickAction(ClickAction.SUGGEST_COMMAND, "/usarkey " + key);
				}
				i++;
			}
			json.addText(msg.split("([&|§][a-fA-F0-9k-oK-orR])*?\\{keys\\}")[1]);
			String sjson = json.toString();
			if(sjson.equalsIgnoreCase(new JSONMessage().addText(msg.replaceAll("([&|§][a-fA-F0-9k-oK-orR])*?\\{keys\\}", "")).toString())){
				String keys = getKeys();
				p.sendMessage(msg.replace("{keys}", keys.length() == 0 ? "§cnenhuma§r" : keys));
			}else{
				json.sendJson(p);
			}
		}else{
			p.sendMessage(msg);
		}
	}
	
	public String getInfoKey(final String key) throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT * FROM keys WHERE key=?");
		stmt.setString(1, key);
		final ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			String dia = rs.getString("dia");
			return "Tempo: " + (Integer.parseInt(dia) > 1 ? dia + " dias" : dia + " dia")  + ", Grupo: " + rs.getString("grupo") + "";
		}
		return null;
	}
	
	public void removeVIPs() throws SQLException, ClassNotFoundException{
		players.clear();
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT * FROM vips");
		final ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			String player = rs.getString("player");
			if(Long.parseLong(rs.getString("tempo")) < System.currentTimeMillis()){
				String vip = rs.getString("vip");
				if(!players.containsKey(player)) players.put(player, vip);
				delPlayerGroup(player, vip);
			}
		}
	}
	
	public void changePlayerGroup(final String player, final String grupo) throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		setUsando(player, getPlayerGroupUsando(player), false);
		stmt = connection.prepareStatement("UPDATE vips SET usando=1 WHERE player=? AND vip=?;");
		stmt.setString(1, player.toLowerCase());
		stmt.setString(2, grupo);
		stmt.executeUpdate();
	}
	
	public Map<String, String> getPlayers(){
		return this.players;
	}

}

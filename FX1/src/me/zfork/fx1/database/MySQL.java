package me.zfork.fx1.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.zfork.fx1.FX1;

import org.bukkit.entity.Player;

public class MySQL {
	
	private FX1 instance;
	private String user, password, database, host;
	private Connection connection;
	private PreparedStatement stmt;

	public MySQL(final FX1 instance, final String usuario, final String pass, final String db, final String hoost) throws SQLException, ClassNotFoundException {
		this.instance = instance;
		this.user = usuario;
		this.password = pass;
		this.database = db;
		this.host = hoost;
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://" + hoost + "/" + db + "", usuario, pass);
		Statement stmt = connection.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS fx1 (player VARCHAR(255), vitorias INTEGER, derrotas INTEGER)");
		closeConnection();
	}
	
	public void openConnection() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "", user, password);
	}
	
	public void closeConnection() throws ClassNotFoundException, SQLException{
		if(connection != null){
			Class.forName("com.mysql.jdbc.Driver");
			connection.close();
		}
	}

	public void addNew(final String player, final int vitorias, final int derrotas) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		String sql = "INSERT INTO fx1 (player, vitorias, derrotas) VALUES (?, ?, ?)";
		stmt = connection.prepareStatement(sql);
		stmt.setString(1, player);
		stmt.setInt(2, vitorias);
		stmt.setInt(3, derrotas);
		stmt.execute();
	}

	public void updateVitorias(final String player, final int vitorias) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		String sql = "SELECT * FROM fx1";
		stmt = connection.prepareStatement(sql);
		final ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			if(rs.getString("player").equalsIgnoreCase(player)){
				sql = "UPDATE fx1 SET vitorias= ? WHERE player= ?";
				PreparedStatement stmt = connection.prepareStatement(sql);
				stmt.setInt(1, getVitorias(rs.getString("player")) + vitorias);
				stmt.setString(2, rs.getString("player"));
				stmt.executeUpdate();
				return;
			}
		}
	}
	
	public void updateDerrotas(final String player, final int derrotas) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		String sql = "SELECT * FROM fx1";
		stmt = connection.prepareStatement(sql);
		final ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			if(rs.getString("player").equalsIgnoreCase(player)){
				sql = "UPDATE fx1 SET derrotas= ? WHERE player= ?";
				PreparedStatement stmt = connection.prepareStatement(sql);
				stmt.setInt(1, getDerrotas(rs.getString("player")) + derrotas);
				stmt.setString(2, rs.getString("player"));
				stmt.executeUpdate();
				return;
			}
		}
	}

	public int getVitorias(final String player) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		String sql = "SELECT * FROM fx1";
		stmt = connection.prepareStatement(sql);
		final ResultSet rs = stmt.executeQuery();
		while(rs.next())
			if(rs.getString("player").equalsIgnoreCase(player)) return rs.getInt("vitorias");
		return 0;
	}
	
	public int getDerrotas(final String player) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		String sql = "SELECT * FROM fx1";
		stmt = connection.prepareStatement(sql);
		final ResultSet rs = stmt.executeQuery();
		while(rs.next())
			if(rs.getString("player").equalsIgnoreCase(player)) return rs.getInt("derrotas");
		return 0;
	}

	public boolean hasPlayer(final String player) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		String sql = "SELECT * FROM fx1 WHERE player= ?";
		stmt = connection.prepareStatement(sql);
		stmt.setString(1, player);
		final ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			if(rs.getString("player").equalsIgnoreCase(player)) return true;
		}
		return false;
	}

	public void getTOPVitorias(Player p) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		String sql = "SELECT * FROM fx1 ORDER BY vitorias DESC LIMIT " + instance.getConfig().getInt("Config.Rank.TOP_Vitorias_Limite");
		stmt = connection.prepareStatement(sql);
		final ResultSet rs = stmt.executeQuery();
		int i = 1;
		while (rs.next()) {
			if(rs.getInt("vitorias") > 0){
				p.sendMessage(instance.getConfig().getString("Config.Formato_Mais_Venceu").replace("&", "§").replace("{player}", rs.getString("player"))
						.replace("{vitorias}", "" + rs.getInt("vitorias")).replace("{colocacao}", i + ""));
				i++;
			}
		}
	}
	
	public void getTOPDerrotas(Player p) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		String sql = "SELECT * FROM fx1 ORDER BY vitorias DESC LIMIT " + instance.getConfig().getInt("Config.Rank.TOP_Vitorias_Limite");
		stmt = connection.prepareStatement(sql);
		final ResultSet rs = stmt.executeQuery();
		int i = 1;
		while (rs.next()) {
			if(rs.getInt("derrotas") > 0){
				p.sendMessage(instance.getConfig().getString("Config.Formato_Mais_Perdeu").replace("&", "§").replace("{player}", rs.getString("player"))
						.replace("{derrotas}", "" + rs.getInt("derrotas")).replace("{colocacao}", i + ""));
				i++;
			}
		}
	}

	public void addPontoVitoria(final String player) throws ClassNotFoundException, SQLException {
		if (hasPlayer(player)) {
			updateVitorias(player, 1);
		} else {
			addNew(player, 1, 0);
		}
	}
	
	public void addPontoDerrota(final String player) throws ClassNotFoundException, SQLException {
		if (hasPlayer(player)) {
			updateDerrotas(player, 1);
		} else {
			addNew(player, 0, 1);
		}
	}
	
	public void resetPlayer(final String player, final String tipo) throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		String sql = "SELECT * FROM fx1";
		stmt = connection.prepareStatement(sql);
		final ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			if(rs.getString("player").equalsIgnoreCase(player)){
				sql = "UPDATE fx1 SET " + tipo.toLowerCase() + "='" + 0 + "' WHERE player= ?";
				stmt = connection.prepareStatement(sql);
				stmt.setString(1, rs.getString("player"));
				stmt.executeUpdate();
				return;
			}
		}
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getDatabase() {
		return database;
	}

	public String getHost() {
		return host;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public Statement getStmt() {
		return stmt;
	}

}

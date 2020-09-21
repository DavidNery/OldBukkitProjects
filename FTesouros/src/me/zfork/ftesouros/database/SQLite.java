package me.zfork.ftesouros.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.zfork.ftesouros.FTesouros;

import org.bukkit.command.CommandSender;

public class SQLite {
	
	private FTesouros instance;
	private Connection connection = null;
	private PreparedStatement stmt = null;

	public SQLite(final FTesouros instance) throws SQLException, ClassNotFoundException {
		this.instance = instance;
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + instance.getDataFolder().getAbsolutePath() + File.separator + "ftesouros.db");
		Statement stmt = connection.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS ftesouros (player VARCHAR(255), achou INTEGER)");
		closeConnection();
	}
	
	public void openConnection() throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + instance.getDataFolder().getAbsolutePath() + File.separator + "ftesouros.db");
	}
	
	public void closeConnection() throws ClassNotFoundException, SQLException{
		if(connection != null){
			Class.forName("org.sqlite.JDBC");
			connection.close();
		}
	}

	public void addNew(final String player, final int achou) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		String sql = "INSERT INTO ftesouros (player, achou) VALUES (?, ?)";
		stmt = connection.prepareStatement(sql);
		stmt.setString(1, player);
		stmt.setInt(2, achou);
		stmt.execute();
	}

	public void updateAchou(final String player, final int achou) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		String sql = "SELECT * FROM ftesouros";
		stmt = connection.prepareStatement(sql);
		final ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			if(rs.getString("player").equalsIgnoreCase(player)){
				sql = "UPDATE ftesouros SET achou= ? WHERE player= ?";
				PreparedStatement stmt = connection.prepareStatement(sql);
				stmt.setInt(1, getPlayerAchou(rs.getString("player")) + achou);
				stmt.setString(2, rs.getString("player"));
				stmt.executeUpdate();
				return;
			}
		}
	}

	public int getPlayerAchou(final String player) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		String sql = "SELECT * FROM ftesouros";
		stmt = connection.prepareStatement(sql);
		final ResultSet rs = stmt.executeQuery();
		while(rs.next())
			if(rs.getString("player").equalsIgnoreCase(player)) return rs.getInt("achou");
		return 0;
	}

	public boolean hasPlayer(final String player) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		String sql = "SELECT * FROM ftesouros WHERE player= ?";
		stmt = connection.prepareStatement(sql);
		stmt.setString(1, player);
		final ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			if(rs.getString("player").equalsIgnoreCase(player)) return true;
		}
		return false;
	}

	public void getTOPAchou(CommandSender p) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		String sql = "SELECT * FROM ftesouros ORDER BY achou DESC LIMIT " + instance.getConfig().getInt("Config.Rank.TOP_Achou_Limite");
		stmt = connection.prepareStatement(sql);
		final ResultSet rs = stmt.executeQuery();
		int i = 1;
		while (rs.next()) {
			if(rs.getInt("achou") > 0){
				p.sendMessage(instance.getConfig().getString("Config.Rank.Formato_Rank_Achou").replace("&", "§").replace("{player}", rs.getString("player"))
						.replace("{achou}", "" + rs.getInt("achou")).replace("{colocacao}", i + ""));
				i++;
			}
		}
	}

	public void addAchou(final String player) throws ClassNotFoundException, SQLException {
		if (hasPlayer(player)) {
			updateAchou(player, 1);
		} else {
			addNew(player, 1);
		}
	}
	
	public void resetPlayer(final String player, final String tipo) throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		String sql = "SELECT * FROM ftesouros";
		stmt = connection.prepareStatement(sql);
		final ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			if(rs.getString("player").equalsIgnoreCase(player)){
				sql = "UPDATE ftesouros SET " + tipo.toLowerCase() + "='" + 0 + "' WHERE player= ?";
				stmt = connection.prepareStatement(sql);
				stmt.setString(1, rs.getString("player"));
				stmt.executeUpdate();
				return;
			}
		}
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public PreparedStatement getStmt() {
		return stmt;
	}

}

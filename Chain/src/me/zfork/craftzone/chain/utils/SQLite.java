package me.zfork.craftzone.chain.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.zfork.craftzone.chain.Main;

public class SQLite {
	
	private Main instance;
	private Connection connection;
	private PreparedStatement stmt;

	public SQLite(Main instance) throws SQLException, ClassNotFoundException, IOException {
		this.instance = instance;
		Class.forName("org.sqlite.JDBC");
		if(!new File(instance.getDataFolder(), "bans.db").exists()) new File(instance.getDataFolder().getAbsolutePath() + File.separator + "bans.db").createNewFile();
		connection = DriverManager.getConnection("jdbc:sqlite:" + instance.getDataFolder().getAbsolutePath() + File.separator + "bans.db");
		stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS bans (player VARCHAR(255), motivo VARCHAR(255), staff VARCHAR(255))");
		stmt.executeUpdate();
		closeConnection();
	}

	public void openConnection() throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		if(connection == null) connection = DriverManager.getConnection("jdbc:sqlite:" + instance.getDataFolder().getAbsolutePath() + File.separator + "bans.db");
	}

	public void closeConnection() throws ClassNotFoundException, SQLException{
		if(connection != null){
			Class.forName("org.sqlite.JDBC");
			connection.close();
			stmt.close();
			connection = null;
			stmt = null;
		}
	}

	public void addNew(final String player, final String motivo, final String staff) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("INSERT INTO bans (player, motivo, staff) VALUES (?, ?, ?);");
		stmt.setString(1, player.toLowerCase());
		stmt.setString(2, motivo);
		stmt.setString(3, staff);
		stmt.executeUpdate();
	}
	
	public void delPlayer(final String player) throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("DELETE FROM bans WHERE player=?");
		stmt.setString(1, player.toLowerCase());
		stmt.executeUpdate();
	}
	
	public boolean isBanned(final String player) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT * FROM bans WHERE player=?");
		stmt.setString(1, player.toLowerCase());
		final ResultSet rs = stmt.executeQuery();
		return rs.next();
	}
	
	public String getMotivo(final String player) throws SQLException, ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT motivo FROM bans WHERE player=?");
		stmt.setString(1, player.toLowerCase());
		ResultSet rs = stmt.executeQuery();
		return rs.getString("motivo");
	}
	
	public String getStaff(final String player) throws SQLException, ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT staff FROM bans WHERE player=?");
		stmt.setString(1, player.toLowerCase());
		ResultSet rs = stmt.executeQuery();
		return rs.getString("staff");
	}

}

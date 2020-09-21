package me.zfork.fkitspro.databases;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.zfork.fkitspro.FKitsPRO;
import me.zfork.fkitspro.PlayersKits;

public class SQLite {

	private FKitsPRO instance;
	private Connection connection;
	private PreparedStatement stmt;

	public SQLite(FKitsPRO instance) throws SQLException, ClassNotFoundException, IOException {
		this.instance = instance;
		Class.forName("org.sqlite.JDBC");
		if(!new File(instance.getDataFolder(), "delay.db").exists()) new File(instance.getDataFolder().getAbsolutePath() + File.separator + "delay.db").createNewFile();
		connection = DriverManager.getConnection("jdbc:sqlite:" + instance.getDataFolder().getAbsolutePath() + File.separator + "delay.db");
		stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS delay (player VARCHAR(255), kit VARCHAR(255), tempo VARCHAR(255))");
		stmt.executeUpdate();
		closeConnection();
	}

	public void openConnection() throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		if(connection == null) connection = DriverManager.getConnection("jdbc:sqlite:" + instance.getDataFolder().getAbsolutePath() + File.separator + "delay.db");
	}

	public void closeConnection() throws ClassNotFoundException, SQLException{
		if(connection != null){
			Class.forName("org.sqlite.JDBC");
			if(stmt != null) stmt.close();
			connection.close();
			stmt = null;
			connection = null;
		}
	}

	public void addNew(final String player, final String kit, final String tempo) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("INSERT INTO delay (player, kit, tempo) VALUES (?, ?, ?);");
		stmt.setString(1, player.toLowerCase());
		stmt.setString(2, kit);
		stmt.setString(3, tempo);
		stmt.executeUpdate();
	}
	
	public void restart() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("DROP TABLE IF EXISTS delay");
		stmt.executeUpdate();
		stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS delay (player VARCHAR(255), kit VARCHAR(255), tempo VARCHAR(255))");
		stmt.executeUpdate();
	}
	
	public synchronized void downloadPlayersKits() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		stmt = connection.prepareStatement("SELECT * FROM delay");
		final ResultSet rs = stmt.executeQuery();
		String player = "";
		PlayersKits playersKits = null;
		while(rs.next()){
			if(!player.equals(rs.getString("player"))){
				playersKits = new PlayersKits(rs.getString("player"));
				player = rs.getString("player");
			}
			playersKits.addDelay(rs.getString("kit"), Long.parseLong(rs.getString("tempo")));
		}
	}

}
